package mcp.mobius.opis;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.commands.server.*;
import mcp.mobius.opis.events.*;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.proxy.ProxyServer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.util.logging.Logger;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
@Mod(modid="Opis", name="Opis", version="1.2.3", dependencies="required-after:MobiusCore@[1.2.3]", acceptableRemoteVersions="*")
public class modOpis {

    @Mod.Instance("Opis")
    public static modOpis instance;
    public static Logger log = Logger.getLogger("Opis");
    @SidedProxy(clientSide="mcp.mobius.opis.proxy.ProxyClient", serverSide="mcp.mobius.opis.proxy.ProxyServer")
    public static ProxyServer proxy;
    public static int profilerDelay = 1;
    public static boolean profilerRun = false;
    public static boolean profilerRunClient = false;
    public static int profilerMaxTicks = 250;
    public static boolean microseconds = true;
    private static int lagGenID = -1;
    public static CoordinatesBlock selectedBlock = null;
    public static boolean swingOpen = false;
    public Configuration config = null;
    public static String commentTables = "Minimum access level to be able to view tables in /opis command. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentOverlays = "Minimum access level to be able to show overlays in MapWriter. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentOpis = "Minimum access level to be open Opis interface. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentPrivileged = "List of players with PRIVILEGED access level.";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.config = new Configuration(event.getSuggestedConfigurationFile());

        profilerDelay = this.config.get("general", "profiler.delay", 1).getInt();
        lagGenID = this.config.get("general", "laggenerator_id", -1).getInt();
        profilerMaxTicks = this.config.get("general", "profiler.maxpts", 250).getInt();
        microseconds = this.config.get("general", "display.microseconds", true).getBoolean(true);


        String[] users = this.config.get("ACCESS_RIGHTS", "privileged", new String[0], commentPrivileged).getStringList();
        AccessLevel minTables = AccessLevel.PRIVILEGED;
        AccessLevel minOverlays = AccessLevel.PRIVILEGED;
        AccessLevel openOpis = AccessLevel.PRIVILEGED;
        try
        {
            openOpis = AccessLevel.valueOf(this.config.get("ACCESS_RIGHTS", "opis", "NONE", commentTables).getString());
        }
        catch (IllegalArgumentException e) {}
        try
        {
            minTables = AccessLevel.valueOf(this.config.get("ACCESS_RIGHTS", "tables", "NONE", commentTables).getString());
        }
        catch (IllegalArgumentException e) {}
        try
        {
            minOverlays = AccessLevel.valueOf(this.config.get("ACCESS_RIGHTS", "overlays", "NONE", commentOverlays).getString());
        }
        catch (IllegalArgumentException e) {}
        Message.setTablesMinimumLevel(minTables);
        Message.setOverlaysMinimumLevel(minOverlays);
        Message.setOpisMinimumLevel(openOpis);
        for (String s : users) {
            PlayerTracker.INSTANCE.addPrivilegedPlayer(s, false);
        }
        this.config.save();

        MinecraftForge.EVENT_BUS.register(new OpisClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new OpisServerEventHandler());
        FMLCommonHandler.instance().bus().register(OpisClientTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(OpisServerTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(PlayerTracker.INSTANCE);


        PacketManager.init();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        if (lagGenID != -1)
        {
            Block blockDemo = new BlockLag(Material.field_151575_d);
            GameRegistry.registerBlock(blockDemo, "opis.laggen");
            GameRegistry.registerTileEntity(TileLag.class, "opis.laggen");

            Block blockDebug = new BlockDebug(Material.field_151575_d);
            GameRegistry.registerBlock(blockDebug, "opis.debug");
            GameRegistry.registerTileEntity(TileDebug.class, "opis.debug");
        }
        ProfilerSection.RENDER_TILEENTITY.setProfiler(new ProfilerRenderTileEntity());
        ProfilerSection.RENDER_ENTITY.setProfiler(new ProfilerRenderEntity());
        ProfilerSection.RENDER_BLOCK.setProfiler(new ProfilerRenderBlock());


        ProfilerSection.EVENT_INVOKE.setProfiler(new ProfilerEvent());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ModIdentification.init();
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        ProfilerSection.DIMENSION_TICK.setProfiler(new ProfilerDimTick());
        ProfilerSection.DIMENSION_BLOCKTICK.setProfiler(new ProfilerDimBlockTick());
        ProfilerSection.ENTITY_UPDATETIME.setProfiler(new ProfilerEntityUpdate());
        ProfilerSection.TICK.setProfiler(new ProfilerTick());
        ProfilerSection.TILEENT_UPDATETIME.setProfiler(new ProfilerTileEntityUpdate());
        ProfilerSection.PACKET_INBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.PACKET_OUTBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.NETWORK_TICK.setProfiler(new ProfilerNetworkTick());

        event.registerServerCommand(new CommandChunkList());
        event.registerServerCommand(new CommandFrequency());
        event.registerServerCommand(new CommandStart());
        event.registerServerCommand(new CommandStop());
        event.registerServerCommand(new CommandTimingTileEntities());
        event.registerServerCommand(new CommandTicks());
        event.registerServerCommand(new CommandTimingEntities());
        event.registerServerCommand(new CommandAmountEntities());
        event.registerServerCommand(new CommandKill());
        event.registerServerCommand(new CommandKillAll());
        event.registerServerCommand(new CommandReset());
        event.registerServerCommand(new CommandEntityCreate());
        event.registerServerCommand(new CommandOpis());
        event.registerServerCommand(new CommandAddPrivileged());
        event.registerServerCommand(new CommandRmPrivileged());





        event.registerServerCommand(new CommandHelp());
    }

}
