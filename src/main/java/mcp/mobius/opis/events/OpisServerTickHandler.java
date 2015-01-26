package mcp.mobius.opis.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum OpisServerTickHandler {

    INSTANCE;

    public long profilerUpdateTickCounter = 0L;
    public int profilerRunningTicks;
    public EventTimer timer500 = new EventTimer(500L);
    public EventTimer timer1000 = new EventTimer(1000L);
    public EventTimer timer2000 = new EventTimer(2000L);
    public EventTimer timer5000 = new EventTimer(5000L);
    public EventTimer timer10000 = new EventTimer(10000L);
    public HashMap<EntityPlayerMP, AccessLevel> cachedAccess = new HashMap();

    private OpisServerTickHandler() {}

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event)
    {
        StringCache.INSTANCE.syncNewCache();
        if (this.timer1000.isDone())
        {
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_UPLOAD, new SerialLong(((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount)));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_DOWNLOAD, new SerialLong(((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount)));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_CHUNK_FORCED, new SerialInt(ChunkManager.INSTANCE.getForcedChunkAmount())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_CHUNK_LOADED, new SerialInt(ChunkManager.INSTANCE.getLoadedChunkAmount())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_TIMING_TICK, new DataTiming(((ProfilerTick)ProfilerSection.TICK.getProfiler()).data.getGeometricMean())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_TILEENTS, new SerialInt(TileEntityManager.INSTANCE.getAmountTileEntities())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_ENTITIES, new SerialInt(EntityManager.INSTANCE.getAmountEntities())));
            for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing) {
                if ((!this.cachedAccess.containsKey(player)) || (this.cachedAccess.get(player) != PlayerTracker.INSTANCE.getPlayerAccessLevel(player)))
                {
                    PacketManager.validateAndSend(new NetDataValue(Message.STATUS_ACCESS_LEVEL, new SerialInt(PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal())), player);
                    this.cachedAccess.put(player, PlayerTracker.INSTANCE.getPlayerAccessLevel(player));
                }
            }
            ArrayList<DataThread> threads = new ArrayList();
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                threads.add(new DataThread().fill(t));
            }
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_THREADS, threads));


            ArrayList<DataDimension> dimData = new ArrayList();
            Integer[] arr$ = DimensionManager.getIDs();int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++)
            {
                int dim = arr$[i$].intValue();
                dimData.add(new DataDimension().fill(dim));
            }
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_DIMENSION_DATA, dimData));
            if (modOpis.profilerRun)
            {
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUNNING, new SerialInt(modOpis.profilerMaxTicks)));
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUN_UPDATE, new SerialInt(this.profilerRunningTicks)));
            }
            ((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount = 0L;
            ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount = 0L;
        }
        if (this.timer2000.isDone()) {
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PLAYERS, EntityManager.INSTANCE.getAllPlayers()));
        }
        if (this.timer5000.isDone())
        {
            updatePlayers();

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND, new ArrayList(((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).data.values())));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND, new ArrayList(((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).data.values())));

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND_250, new ArrayList(((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).data250.values())));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND_250, new ArrayList(((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).data250.values())));

            ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).startInterval();
            ((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).startInterval();
        }
        this.profilerUpdateTickCounter += 1L;
        if ((this.profilerRunningTicks < modOpis.profilerMaxTicks) && (modOpis.profilerRun))
        {
            this.profilerRunningTicks += 1;
        }
        else if ((this.profilerRunningTicks >= modOpis.profilerMaxTicks) && (modOpis.profilerRun))
        {
            this.profilerRunningTicks = 0;
            modOpis.profilerRun = false;
            ProfilerSection.desactivateAll(Side.SERVER);

            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_STOP, new SerialInt(modOpis.profilerMaxTicks)));
            for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing) {
                PacketManager.sendFullUpdate(player);
            }
        }
    }

    private void updatePlayers()
    {
        for (EntityPlayerMP player : PlayerTracker.INSTANCE.playerOverlayStatus.keySet())
        {
            if (PlayerTracker.INSTANCE.playerOverlayStatus.get(player) == OverlayStatus.CHUNKSTATUS)
            {
                PacketManager.validateAndSend(new NetDataCommand(Message.LIST_CHUNK_LOADED_CLEAR), player);
                PacketManager.splitAndSend(Message.LIST_CHUNK_LOADED, ChunkManager.INSTANCE.getLoadedChunks(((Integer)PlayerTracker.INSTANCE.playerDimension.get(player)).intValue()), player);
            }
            if (PlayerTracker.INSTANCE.playerOverlayStatus.get(player) == OverlayStatus.MEANTIME)
            {
                ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);

                PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
            }
        }
    }

}
