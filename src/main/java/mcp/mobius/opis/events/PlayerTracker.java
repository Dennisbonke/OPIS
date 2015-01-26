package mcp.mobius.opis.events;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum  PlayerTracker {

    INSTANCE;

    public HashSet<EntityPlayerMP> playersSwing = new HashSet();
    public HashMap<String, Boolean> filteredAmount = new HashMap();
    public HashMap<EntityPlayerMP, OverlayStatus> playerOverlayStatus = new HashMap();
    public HashMap<EntityPlayerMP, Integer> playerDimension = new HashMap();
    public HashMap<EntityPlayerMP, SelectedTab> playerTab = new HashMap();
    private HashSet<String> playerPrivileged = new HashSet();

    private PlayerTracker() {}

    public SelectedTab getPlayerSelectedTab(EntityPlayerMP player)
    {
        return (SelectedTab)this.playerTab.get(player);
    }

    public AccessLevel getPlayerAccessLevel(EntityPlayerMP player)
    {
        return getPlayerAccessLevel(player.getDisplayName());
    }

    public AccessLevel getPlayerAccessLevel(String name)
    {
        GameProfile profile = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(name).func_146103_bH();
        if ((MinecraftServer.func_71276_C().func_71203_ab().func_152596_g(profile)) || (MinecraftServer.func_71276_C().func_71264_H())) {
            return AccessLevel.ADMIN;
        }
        if (this.playerPrivileged.contains(name)) {
            return AccessLevel.PRIVILEGED;
        }
        return AccessLevel.NONE;
    }

    public void addPrivilegedPlayer(String name, boolean save)
    {
        this.playerPrivileged.add(name);
        if (save)
        {
            modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[0], modOpis.commentPrivileged).set((String[])this.playerPrivileged.toArray(new String[0]));
            modOpis.instance.config.save();
        }
    }

    public void addPrivilegedPlayer(String name)
    {
        addPrivilegedPlayer(name, true);
    }

    public void rmPrivilegedPlayer(String name)
    {
        this.playerPrivileged.remove(name);
        modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[0], modOpis.commentPrivileged).set((String[])this.playerPrivileged.toArray(new String[0]));
        modOpis.instance.config.save();
    }

    public void reloeadPriviligedPlayers()
    {
        String[] users = modOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[0], modOpis.commentPrivileged).getStringList();
        for (String s : users) {
            INSTANCE.addPrivilegedPlayer(s, false);
        }
    }

    public boolean isAdmin(EntityPlayerMP player)
    {
        return getPlayerAccessLevel(player).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isAdmin(String name)
    {
        return getPlayerAccessLevel(name).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isPrivileged(EntityPlayerMP player)
    {
        return getPlayerAccessLevel(player).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    public boolean isPrivileged(String name)
    {
        return getPlayerAccessLevel(name).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        this.playerOverlayStatus.remove(event.player);
        this.playerDimension.remove(event.player);

        this.playersSwing.remove(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        PacketManager.validateAndSend(new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())), (EntityPlayerMP) event.player);








        StringCache.INSTANCE.syncCache((EntityPlayerMP)event.player);
    }

}
