package mcp.mobius.opis.network;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
@ChannelHandler.Sharable
public class PacketManager {

    private static final EnumMap<Side, FMLEmbeddedChannel> channels = Maps.newEnumMap(Side.class);

    public static void init()
    {
        if (!channels.isEmpty()) {
            return;
        }
        Codec codec = new Codec(null);

        codec.addDiscriminator(0, PacketReqChunks.class);
        codec.addDiscriminator(1, PacketReqData.class);
        codec.addDiscriminator(2, NetDataCommand.class);
        codec.addDiscriminator(3, NetDataList.class);
        codec.addDiscriminator(4, NetDataValue.class);
        codec.addDiscriminator(5, PacketChunks.class);

        channels.putAll(NetworkRegistry.INSTANCE.newChannel("Opis", new ChannelHandler[] { codec }));
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            FMLEmbeddedChannel channel = (FMLEmbeddedChannel)channels.get(Side.CLIENT);
            String codecName = channel.findChannelHandlerNameForType(Codec.class);
            channel.pipeline().addAfter(codecName, "ClientHandler", new HandlerClient(null));
        }
        FMLEmbeddedChannel channel = (FMLEmbeddedChannel)channels.get(Side.SERVER);
        String codecName = channel.findChannelHandlerNameForType(Codec.class);
        channel.pipeline().addAfter(codecName, "ServerHandler", new HandlerServer(null));
    }

    private static final class Codec
            extends FMLIndexedMessageToMessageCodec<PacketBase>
    {
        public void encodeInto(ChannelHandlerContext ctx, PacketBase packet, ByteBuf target)
                throws Exception
        {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            packet.encode(output);
            target.writeBytes(output.toByteArray());
        }

        public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, PacketBase packet)
        {
            ByteArrayDataInput input = ByteStreams.newDataInput(source.array());
            input.skipBytes(1);
            packet.decode(input);
            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                actionClient(packet);
            } else {
                actionServer(ctx, packet);
            }
        }

        @SideOnly(Side.CLIENT)
        private void actionClient(PacketBase packet)
        {
            Minecraft mc = Minecraft.func_71410_x();
            packet.actionClient(mc.field_71441_e, mc.field_71439_g);
        }

        private void actionServer(ChannelHandlerContext ctx, PacketBase packet)
        {
            EntityPlayerMP player = ((NetHandlerPlayServer)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).field_147369_b;
            packet.actionServer(player.field_70170_p, player);
        }

        public FMLIndexedMessageToMessageCodec<PacketBase> addDiscriminator(int discriminator, Class<? extends PacketBase> type)
        {
            if (!hasEmptyContructor(type)) {
                LogManager.getLogger().log(Level.FATAL, type.getName() + "does not have an empty constructor!");
            }
            return super.addDiscriminator(discriminator, type);
        }

        private static boolean hasEmptyContructor(Class type)
        {
            try
            {
                for (Constructor c : type.getConstructors()) {
                    if (c.getParameterTypes().length == 0) {
                        return true;
                    }
                }
            }
            catch (SecurityException e) {}
            return false;
        }
    }

    @ChannelHandler.Sharable
    @SideOnly(Side.CLIENT)
    private static final class HandlerClient
            extends SimpleChannelInboundHandler<PacketBase>
    {
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet)
                throws Exception
        {
            Minecraft mc = Minecraft.func_71410_x();
            packet.actionClient(mc.field_71441_e, mc.field_71439_g);
        }
    }

    @ChannelHandler.Sharable
    private static final class HandlerServer
            extends SimpleChannelInboundHandler<PacketBase>
    {
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet)
                throws Exception
        {
            EntityPlayerMP player = ((NetHandlerPlayServer)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).field_147369_b;
            packet.actionServer(player.field_70170_p, player);
        }
    }

    public static void sendToServer(PacketBase packet)
    {
        ((FMLEmbeddedChannel)channels.get(Side.CLIENT)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        ((FMLEmbeddedChannel)channels.get(Side.CLIENT)).writeAndFlush(packet);
    }

    public static void sendToPlayer(PacketBase packet, EntityPlayer player)
    {
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).writeAndFlush(packet);
    }

    public static void sendToPlayer(Packet packet, EntityPlayerMP player)
    {
        player.field_71135_a.func_147359_a(packet);
    }

    public static void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point)
    {
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).writeAndFlush(packet);
    }

    public static void sendToDimension(PacketBase packet, int dimension)
    {
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimension));
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).writeAndFlush(packet);
    }

    public static void sendToAll(PacketBase packet)
    {
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        ((FMLEmbeddedChannel)channels.get(Side.SERVER)).writeAndFlush(packet);
    }

    public static Packet toMcPacket(PacketBase packet)
    {
        return ((FMLEmbeddedChannel)channels.get(FMLCommonHandler.instance().getEffectiveSide())).generatePacketFrom(packet);
    }

    public static void validateAndSend(PacketBase capsule, EntityPlayerMP player)
    {
        if (!capsule.msg.isDisplayActive(PlayerTracker.INSTANCE.getPlayerSelectedTab(player))) {
            return;
        }
        if (capsule.msg.canPlayerUseCommand(player)) {
            sendToPlayer(capsule, player);
        }
    }

    public static void sendPacketToAllSwing(PacketBase capsule)
    {
        for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing) {
            validateAndSend(capsule, player);
        }
    }

    public static void sendChatMsg(String msg, EntityPlayerMP player)
    {
        sendToPlayer(new S02PacketChat(new ChatComponentText(msg)), player);
    }

    public static void splitAndSend(Message msg, ArrayList<? extends ISerializable> data, EntityPlayerMP player)
    {
        int i = 0;
        while (i < data.size())
        {
            validateAndSend(new NetDataList(msg, data.subList(i, Math.min(i + 500, data.size()))), player);
            i += 500;
        }
    }

    public static void sendFullUpdate(EntityPlayerMP player)
    {
        ArrayList<DataEntity> timingEntities = EntityManager.INSTANCE.getWorses(100);
        ArrayList<DataBlockTileEntity> timingTileEnts = TileEntityManager.INSTANCE.getWorses(100);

        ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);
        ArrayList<DataEntityPerClass> timingEntsClass = EntityManager.INSTANCE.getTotalPerClass();
        ArrayList<DataBlockTileEntityPerClass> timingTEsClass = TileEntityManager.INSTANCE.getCumulativeTimingTileEntities();

        DataTiming totalTimeTE = TileEntityManager.INSTANCE.getTotalUpdateTime();
        DataTiming totalTimeEnt = EntityManager.INSTANCE.getTotalUpdateTime();

        DataNetworkTick totalNetwork = new DataNetworkTick().fill();
        DataBlockTick totalWorldTick = new DataBlockTick().fill();

        ArrayList<DataEvent> timingEvents = new ArrayList();
        HashBasedTable<Class, String, DescriptiveStatistics> eventData = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).data;
        HashBasedTable<Class, String, String> eventMod = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).dataMod;
        for (Table.Cell<Class, String, DescriptiveStatistics> cell : eventData.cellSet()) {
            timingEvents.add(new DataEvent().fill(cell, (String)eventMod.get(cell.getRowKey(), cell.getColumnKey())));
        }
        ArrayList<DataEvent> timingTicks = new ArrayList();
        HashBasedTable<Class, String, DescriptiveStatistics> eventTickData = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).dataTick;
        HashBasedTable<Class, String, String> eventTickMod = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).dataModTick;
        for (Table.Cell<Class, String, DescriptiveStatistics> cell : eventTickData.cellSet()) {
            timingTicks.add(new DataEvent().fill(cell, (String)eventTickMod.get(cell.getRowKey(), cell.getColumnKey())));
        }
        validateAndSend(new NetDataList(Message.LIST_TIMING_HANDLERS, timingTicks), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES, timingEntities), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS, timingTileEnts), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS_PER_CLASS, timingTEsClass), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_EVENTS, timingEvents), player);
        validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES_PER_CLASS, timingEntsClass), player);
        validateAndSend(new NetDataValue(Message.VALUE_TIMING_TILEENTS, totalTimeTE), player);
        validateAndSend(new NetDataValue(Message.VALUE_TIMING_ENTITIES, totalTimeEnt), player);

        validateAndSend(new NetDataValue(Message.VALUE_TIMING_WORLDTICK, totalWorldTick), player);
        validateAndSend(new NetDataValue(Message.VALUE_TIMING_NETWORK, totalNetwork), player);



        validateAndSend(new NetDataValue(Message.STATUS_TIME_LAST_RUN, new SerialLong(ProfilerSection.timeStampLastRun)), player);

        validateAndSend(new NetDataValue(Message.STATUS_ACCESS_LEVEL, new SerialInt(PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal())), player);


        String name = player.getDisplayName();
        boolean filtered = false;
        if (PlayerTracker.INSTANCE.filteredAmount.containsKey(name)) {
            filtered = ((Boolean)PlayerTracker.INSTANCE.filteredAmount.get(name)).booleanValue();
        }
        ArrayList<AmountHolder> amountEntities = EntityManager.INSTANCE.getCumulativeEntities(filtered);



        validateAndSend(new NetDataList(Message.LIST_AMOUNT_ENTITIES, amountEntities), player);
    }

}
