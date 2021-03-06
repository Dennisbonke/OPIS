package mcp.mobius.opis.proxy;

import cpw.mods.fml.relauncher.Side;
import mapwriter.api.MwAPI;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.managers.MetaManager;
import mcp.mobius.opis.gui.font.TrueTypeFont;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SwingUI;
import net.minecraft.util.ResourceLocation;

import java.util.logging.Level;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class ProxyClient extends ProxyServer implements IMessageHandler {

    public static TrueTypeFont fontMC8;
    public static TrueTypeFont fontMC12;
    public static TrueTypeFont fontMC16;
    public static TrueTypeFont fontMC18;
    public static TrueTypeFont fontMC24;

    public void init()
    {
        MwAPI.registerDataProvider("Loaded chunks", OverlayLoadedChunks.INSTANCE);
        MwAPI.registerDataProvider("Mean time", OverlayMeanTime.INSTANCE);
        MwAPI.registerDataProvider("Ent per chunk", OverlayEntityPerChunk.INSTANCE);









        fontMC8 = Fonts.createFont(new ResourceLocation("opis", "fonts/LiberationMono-Bold.ttf"), 14.0F, true);

        IMessageHandler panelSummary = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelSummary(), "Summary");

        TabPanelRegistrar.INSTANCE.registerSection("Tracking");
        IMessageHandler panelPlayers = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelPlayers(), "Players", "Tracking");
        IMessageHandler panelAmountEntities = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelAmountEntities(), "Entities", "Tracking");
        IMessageHandler panelAmountTileEnts = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelAmountTileEnts(), "Tile Entities", "Tracking");
        IMessageHandler panelDimensions = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelDimensions(), "Dimensions", "Tracking");

        TabPanelRegistrar.INSTANCE.registerSection("Server timing");
        IMessageHandler panelTimingTileEnts = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingTileEnts(), "Tile Entities", "Server timing");
        IMessageHandler panelTimingTileEntsPerClass = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingTileEntsPerClass(), "Tile Entities [Type]", "Server timing");
        IMessageHandler panelTimingEntities = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingEntities(), "Entities", "Server timing");
        IMessageHandler panelTimingEntPerClass = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingEntitiesPerClass(), "Entities [Type]", "Server timing");
        IMessageHandler panelTimingHandlers = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingHandlers(), "Handlers", "Server timing");
        IMessageHandler panelTimingChunks = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingChunks(), "Chunks", "Server timing");
        IMessageHandler panelTimingEvents = (IMessageHandler) TabPanelRegistrar.INSTANCE.registerTab(new PanelTimingEvents(), "Events", "Server timing");

        TabPanelRegistrar.INSTANCE.registerSection("Client timing");
        TabPanelRegistrar.INSTANCE.registerTab(new PanelRenderTileEnts(), "[Render] TileEnts", "Client timing");
        TabPanelRegistrar.INSTANCE.registerTab(new PanelRenderEntities(), "[Render] Entities", "Client timing");
        TabPanelRegistrar.INSTANCE.registerTab(new PanelRenderHandlers(), "[Render] Handlers", "Client timing");
        TabPanelRegistrar.INSTANCE.registerTab(new PanelEventClient(), "Events", "Client timing");

        TabPanelRegistrar.INSTANCE.registerSection("Network");
        IMessageHandler panelPacketsOut = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelOutbound(), "Outbound", "Network");
        IMessageHandler panelPacketsIn = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelInbound(), "Inbound", "Network");
        IMessageHandler panelPacketsOut250 = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelOutbound250(), "Outbound 250", "Network");
        IMessageHandler panelPacketsIn250 = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelInbound250(), "Inbound 250", "Network");

        TabPanelRegistrar.INSTANCE.registerSection("Debug");
        IMessageHandler panelOrphanTileEnts = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelOrphanTileEntities(), "Orphan TileEnts", "Debug");
        IMessageHandler panelThreads = (IMessageHandler)TabPanelRegistrar.INSTANCE.registerTab(new PanelThreads(), "Threads", "Debug");

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_PLAYERS, panelPlayers);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_AMOUNT_ENTITIES, panelAmountEntities);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_AMOUNT_TILEENTS, panelAmountTileEnts);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_TILEENTS, panelTimingTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_TILEENTS, panelTimingTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.CLIENT_HIGHLIGHT_BLOCK, panelTimingTileEnts);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_ENTITIES, panelTimingEntities);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_ENTITIES, panelTimingEntities);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingEntities);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingEntities);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingEntities);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_ENTITIES_PER_CLASS, panelTimingEntPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingEntPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingEntPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingEntPerClass);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_TILEENTS_PER_CLASS, panelTimingTileEntsPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingTileEntsPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingTileEntsPerClass);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingTileEntsPerClass);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_HANDLERS, panelTimingHandlers);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_HANDLERS, panelTimingHandlers);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingHandlers);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingHandlers);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingHandlers);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_CHUNK, panelTimingChunks);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingChunks);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingChunks);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingChunks);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_EVENTS, panelTimingEvents);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelTimingEvents);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelTimingEvents);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelTimingEvents);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_ACCESS_LEVEL, DataCache.instance());
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_CURRENT_TIME, DataCache.instance());
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_ACCESS_LEVEL, SwingUI.instance());
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.CLIENT_SHOW_SWING, SwingUI.instance());

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_AMOUNT_TILEENTS, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_AMOUNT_ENTITIES, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_AMOUNT_HANDLERS, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_AMOUNT_UPLOAD, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_AMOUNT_DOWNLOAD, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_TICK, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_CHUNK_FORCED, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_CHUNK_LOADED, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_TILEENTS, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_ENTITIES, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_HANDLERS, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_WORLDTICK, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_START, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STOP, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUN_UPDATE, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_RUNNING, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_TIME_LAST_RUN, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_ENTUPDATE, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.VALUE_TIMING_NETWORK, panelSummary);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_PING, panelSummary);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_DIMENSION_DATA, panelDimensions);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_PACKETS_OUTBOUND, panelPacketsOut);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_PACKETS_INBOUND, panelPacketsIn);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_PACKETS_OUTBOUND_250, panelPacketsOut250);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_PACKETS_INBOUND_250, panelPacketsIn250);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_ORPHAN_TILEENTS_CLEAR, panelOrphanTileEnts);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_ORPHAN_TILEENTS, panelOrphanTileEnts);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_THREADS, panelThreads);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.CLIENT_CLEAR_SELECTION, modOpis.proxy);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.CLIENT_START_PROFILING, modOpis.proxy);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.CLIENT_SHOW_RENDER_TICK, modOpis.proxy);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.OVERLAY_CHUNK_ENTITIES, OverlayEntityPerChunk.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_ENTITIES, OverlayEntityPerChunk.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_TICKETS, OverlayLoadedChunks.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_TILEENTS, OverlayMeanTime.INSTANCE);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_TIMING_CHUNK, ChunkManager.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED, ChunkManager.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.LIST_CHUNK_LOADED_CLEAR, ChunkManager.INSTANCE);

        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STRINGUPD, StringCache.INSTANCE);
        MessageHandlerRegistrar.INSTANCE.registerHandler(Message.STATUS_STRINGUPD_FULL, StringCache.INSTANCE);
    }

    public boolean handleMessage(Message msg, PacketBase rawdata)
    {
        switch (msg.ordinal())
        {
            case 1:
                modOpis.selectedBlock = null;
                break;
            case 2:
                modOpis.log.log(Level.INFO, "Started profiling");
                MetaManager.reset();
                modOpis.profilerRun = true;
                ProfilerSection.activateAll(Side.CLIENT);
                break;
            case 3:
                modOpis.log.log(Level.INFO, "=== RENDER TICK ===");




                break;
            default:
                return false;
        }
        return true;
    }

}
