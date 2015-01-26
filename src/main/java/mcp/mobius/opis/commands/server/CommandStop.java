package mcp.mobius.opis.commands.server;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
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
public class CommandStop extends CommandBase implements IOpisCommand {

    public String func_71517_b()
    {
        return "opis_stop";
    }

    public String getCommandNameOpis()
    {
        return func_71517_b();
    }

    public String func_71518_a(ICommandSender icommandsender)
    {
        return "";
    }

    public void func_71515_b(ICommandSender icommandsender, String[] astring)
    {
        mcp.mobius.opis.modOpis.profilerRun = false;
        ProfilerSection.desactivateAll(Side.SERVER);
        icommandsender.func_145747_a(new ChatComponentText(String.format("§oOpis stopped.", new Object[0])));
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
        return PlayerTracker.INSTANCE.isPrivileged(((EntityPlayerMP)sender).getDisplayName());
    }

    public String getDescription()
    {
        return "Ends a run before completion.";
    }

}
