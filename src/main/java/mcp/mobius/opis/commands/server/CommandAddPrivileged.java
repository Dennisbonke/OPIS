package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class CommandAddPrivileged extends CommandBase implements IOpisCommand {

    public String func_71517_b()
    {
        return "opis_addpriv";
    }

    public String func_71518_a(ICommandSender icommandsender)
    {
        return "";
    }

    public void func_71515_b(ICommandSender icommandsender, String[] astring)
    {
        PlayerTracker.INSTANCE.addPrivilegedPlayer(astring[0]);
        icommandsender.func_145747_a(new ChatComponentText(String.format("Player %s added to Opis user list", new Object[] { astring[0] })));
    }

    public int func_82362_a()
    {
        return 3;
    }

    public boolean func_71519_b(ICommandSender sender)
    {
        if ((sender instanceof DedicatedServer)) {
            return true;
        }
        if ((!(sender instanceof DedicatedServer)) && (!(sender instanceof EntityPlayerMP))) {
            return true;
        }
        return PlayerTracker.INSTANCE.isAdmin(((EntityPlayerMP)sender).getDisplayName());
    }

    public String getDescription()
    {
        return "Add a user to the privileged list of users.";
    }

    public String getCommandNameOpis()
    {
        return "opis_addpriv";
    }

}
