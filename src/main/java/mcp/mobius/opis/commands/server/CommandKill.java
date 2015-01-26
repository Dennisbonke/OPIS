package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class CommandKill extends CommandBase implements IOpisCommand {

    public String func_71517_b()
    {
        return "opis_kill";
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
        if (astring.length != 2) {
            return;
        }
        int dim = Integer.valueOf(astring[0]).intValue();
        int eid = Integer.valueOf(astring[1]).intValue();

        World world = DimensionManager.getWorld(dim);
        if (world == null)
        {
            icommandsender.func_145747_a(new ChatComponentText(String.format("§oCannot find dim %d in world %d", new Object[] { Integer.valueOf(dim) })));
            return;
        }
        Entity entity = world.func_73045_a(eid);
        if (entity == null)
        {
            icommandsender.func_145747_a(new ChatComponentText(String.format("§oCannot find entity %d in dim %d", new Object[] { Integer.valueOf(eid), Integer.valueOf(dim) })));
            return;
        }
        entity.func_70106_y();
        icommandsender.func_145747_a(new ChatComponentText(String.format("§oKilled entity %d in dim %d", new Object[] { Integer.valueOf(eid), Integer.valueOf(dim) })));
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
        return "Kills the given entity id in the given dimension.";
    }

}
