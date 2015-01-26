package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class CommandHelp extends CommandBase implements IOpisCommand {

    public String func_71517_b()
    {
        return "opis_help";
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
        IOpisCommand[] commands = { new CommandStart(), new CommandStop(), new CommandReset(), new CommandFrequency(), new CommandTicks(), new CommandChunkList(), new CommandTimingTileEntities(), new CommandTimingEntities(), new CommandAmountEntities(), new CommandKill(), new CommandKillAll(), new CommandAddPrivileged(), new CommandRmPrivileged() };
        for (IOpisCommand cmd : commands) {
            icommandsender.func_145747_a(new ChatComponentText(String.format("/%s : %s", new Object[] { cmd.getCommandNameOpis(), cmd.getDescription() })));
        }
    }

    public int func_82362_a()
    {
        return 3;
    }

    public boolean func_71519_b(ICommandSender sender)
    {
        return true;
    }

    public String getDescription()
    {
        return "This message.";
    }

}
