package mcp.mobius.opis.network.enums;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.EnumSet;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum Message {

    LIST_CHUNK_TILEENTS,  LIST_CHUNK_ENTITIES,  LIST_CHUNK_LOADED,  LIST_CHUNK_LOADED_CLEAR,  LIST_CHUNK_TICKETS,  LIST_TIMING_TILEENTS,  LIST_TIMING_TILEENTS_PER_CLASS,  LIST_TIMING_ENTITIES,  LIST_TIMING_ENTITIES_PER_CLASS,  LIST_TIMING_HANDLERS,  LIST_TIMING_CHUNK,  LIST_TIMING_EVENTS,  LIST_AMOUNT_ENTITIES,  LIST_AMOUNT_TILEENTS,  LIST_PLAYERS(EnumSet.of(SelectedTab.PLAYERS)),  LIST_DIMENSION_DATA(EnumSet.of(SelectedTab.DIMENSIONS)),  LIST_PACKETS_OUTBOUND(EnumSet.of(SelectedTab.PACKETOUTBOUND)),  LIST_PACKETS_INBOUND(EnumSet.of(SelectedTab.PACKETINBOUND)),  LIST_PACKETS_OUTBOUND_250(EnumSet.of(SelectedTab.PACKETOUTBOUND250)),  LIST_PACKETS_INBOUND_250(EnumSet.of(SelectedTab.PACKETINBOUND250)),  LIST_ORPHAN_TILEENTS,  LIST_ORPHAN_TILEENTS_CLEAR,  LIST_THREADS(EnumSet.of(SelectedTab.THREADS)),  VALUE_TIMING_TILEENTS,  VALUE_TIMING_ENTITIES,  VALUE_TIMING_HANDLERS,  VALUE_TIMING_WORLDTICK,  VALUE_TIMING_ENTUPDATE,  VALUE_TIMING_TICK,  VALUE_TIMING_NETWORK,  VALUE_AMOUNT_TILEENTS(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_AMOUNT_ENTITIES(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_AMOUNT_HANDLERS(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_AMOUNT_UPLOAD(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_AMOUNT_DOWNLOAD(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_CHUNK_FORCED(EnumSet.of(SelectedTab.SUMMARY)),  VALUE_CHUNK_LOADED(EnumSet.of(SelectedTab.SUMMARY)),  STATUS_START,  STATUS_STOP,  STATUS_RUN_UPDATE,  STATUS_RUNNING,  STATUS_CURRENT_TIME,  STATUS_TIME_LAST_RUN,  STATUS_ACCESS_LEVEL,  STATUS_PING(EnumSet.of(SelectedTab.SUMMARY)),  STATUS_STRINGUPD,  STATUS_STRINGUPD_FULL,  COMMAND_TELEPORT_BLOCK(AccessLevel.PRIVILEGED),  COMMAND_TELEPORT_CHUNK(AccessLevel.PRIVILEGED),  COMMAND_TELEPORT_TO_ENTITY(AccessLevel.PRIVILEGED),  COMMAND_TELEPORT_PULL_ENTITY(AccessLevel.PRIVILEGED),  COMMAND_START(AccessLevel.PRIVILEGED),  COMMAND_KILLALL(AccessLevel.PRIVILEGED),  COMMAND_FILTERING_TRUE,  COMMAND_FILTERING_FALSE,  COMMAND_UNREGISTER,  COMMAND_UNREGISTER_SWING,  COMMAND_OPEN_SWING,  COMMAND_KILL_HOSTILES_DIM(AccessLevel.PRIVILEGED),  COMMAND_KILL_HOSTILES_ALL(AccessLevel.PRIVILEGED),  COMMAND_PURGE_CHUNKS_DIM(AccessLevel.PRIVILEGED),  COMMAND_PURGE_CHUNKS_ALL(AccessLevel.PRIVILEGED),  COMMAND_KILL_STACKS_DIM(AccessLevel.PRIVILEGED),  COMMAND_KILL_STACKS_ALL(AccessLevel.PRIVILEGED),  OVERLAY_CHUNK_ENTITIES,  OVERLAY_CHUNK_TIMING,  CLIENT_START_PROFILING,  CLIENT_STOP_PROFILING,  CLIENT_RESET_PROFILING,  CLIENT_SHOW_RENDER_TICK,  CLIENT_SHOW_SWING,  CLIENT_CLEAR_SELECTION,  CLIENT_HIGHLIGHT_BLOCK,  SWING_TAB_CHANGED;

    private AccessLevel accessLevel = AccessLevel.NONE;
    private EnumSet<SelectedTab> tabEnum;

    private Message()
    {
        this.accessLevel = AccessLevel.NONE;
        this.tabEnum = EnumSet.of(SelectedTab.ANY);
    }

    private Message(AccessLevel level)
    {
        this.accessLevel = level;
        this.tabEnum = EnumSet.of(SelectedTab.ANY);
    }

    private Message(EnumSet<SelectedTab> _tabEnum)
    {
        this.accessLevel = AccessLevel.NONE;
        this.tabEnum = _tabEnum;
    }

    private Message(AccessLevel level, EnumSet<SelectedTab> _tabEnum)
    {
        this.accessLevel = level;
        this.tabEnum = _tabEnum;
    }

    public AccessLevel getAccessLevel()
    {
        return this.accessLevel;
    }

    public void setAccessLevel(AccessLevel level)
    {
        this.accessLevel = level;
    }

    public boolean canPlayerUseCommand(EntityPlayerMP player)
    {
        return PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal() >= this.accessLevel.ordinal();
    }

    public boolean canPlayerUseCommand(String name)
    {
        return PlayerTracker.INSTANCE.getPlayerAccessLevel(name).ordinal() >= this.accessLevel.ordinal();
    }

    public boolean isDisplayActive(SelectedTab tab)
    {
        if (this.tabEnum.contains(SelectedTab.ANY)) {
            return true;
        }
        if (this.tabEnum.contains(SelectedTab.NONE)) {
            return false;
        }
        if (this.tabEnum.contains(tab)) {
            return true;
        }
        return false;
    }

    public static void setTablesMinimumLevel(AccessLevel level)
    {
        LIST_CHUNK_TILEENTS.setAccessLevel(level);
        LIST_CHUNK_ENTITIES.setAccessLevel(level);
        LIST_TIMING_TILEENTS.setAccessLevel(level);
        LIST_TIMING_ENTITIES.setAccessLevel(level);
        LIST_TIMING_HANDLERS.setAccessLevel(level);
        LIST_TIMING_CHUNK.setAccessLevel(level);
        LIST_TIMING_TILEENTS_PER_CLASS.setAccessLevel(level);
        LIST_TIMING_ENTITIES_PER_CLASS.setAccessLevel(level);
        LIST_TIMING_HANDLERS.setAccessLevel(level);
        LIST_TIMING_EVENTS.setAccessLevel(level);
        LIST_AMOUNT_ENTITIES.setAccessLevel(level);
        LIST_AMOUNT_TILEENTS.setAccessLevel(level);
        LIST_PLAYERS.setAccessLevel(level);
        LIST_DIMENSION_DATA.setAccessLevel(level);
        LIST_PACKETS_OUTBOUND.setAccessLevel(level);
        LIST_PACKETS_INBOUND.setAccessLevel(level);
        LIST_PACKETS_OUTBOUND_250.setAccessLevel(level);
        LIST_PACKETS_INBOUND_250.setAccessLevel(level);
        LIST_ORPHAN_TILEENTS.setAccessLevel(level);
        LIST_THREADS.setAccessLevel(level);
    }

    public static void setOverlaysMinimumLevel(AccessLevel level)
    {
        OVERLAY_CHUNK_ENTITIES.setAccessLevel(level);
        OVERLAY_CHUNK_TIMING.setAccessLevel(level);
        LIST_CHUNK_LOADED.setAccessLevel(level);
        LIST_CHUNK_TICKETS.setAccessLevel(level);
    }

    public static void setOpisMinimumLevel(AccessLevel level)
    {
        COMMAND_OPEN_SWING.setAccessLevel(level);
    }

}
