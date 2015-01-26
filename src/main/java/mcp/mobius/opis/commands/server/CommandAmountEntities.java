package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class CommandAmountEntities extends CommandBase implements IOpisCommand {

    public String func_71517_b()
    {
        return "opis_nent";
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
        if ((icommandsender instanceof EntityPlayerMP))
        {
            icommandsender.func_145747_a(new ChatComponentText("DEPRECATED ! Please run /opis instead.")); return;
        }
        ArrayList<AmountHolder> ents;
        if ((astring.length == 1) && (astring[0].equals("all"))) {
            ents = EntityManager.INSTANCE.getCumulativeEntities(false);
        } else {
            ents = EntityManager.INSTANCE.getCumulativeEntities(true);
        }
        for (AmountHolder s : ents) {
            icommandsender.func_145747_a(new ChatComponentText(String.format("%s : %s", new Object[] { s.key, s.value })));
        }
    }

    public int func_82362_a()
    {
        return 0;
    }

    public boolean func_71519_b(ICommandSender sender)
    {
        return true;
    }

    public String getDescription()
    {
        return "Opens a summary of the number of entities on the server, by type.";
    }

}
