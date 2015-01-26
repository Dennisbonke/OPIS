package mcp.mobius.opis.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.opis.modOpis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class OpisClientEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if (modOpis.selectedBlock == null) {
            return;
        }
        if (Minecraft.func_71410_x().field_71441_e.field_73011_w.field_76574_g != modOpis.selectedBlock.dim) {
            return;
        }
        if (Minecraft.func_71410_x().field_71441_e.func_147437_c(modOpis.selectedBlock.x, modOpis.selectedBlock.y, modOpis.selectedBlock.z)) {
            return;
        }
        double partialTicks = event.partialTicks;

        EntityLivingBase player = Minecraft.func_71410_x().field_71451_h;
        double px = player.field_70142_S + (player.field_70165_t - player.field_70142_S) * partialTicks;
        double py = player.field_70137_T + (player.field_70163_u - player.field_70137_T) * partialTicks;
        double pz = player.field_70136_U + (player.field_70161_v - player.field_70136_U) * partialTicks;

        int bx = modOpis.selectedBlock.x;
        int by = modOpis.selectedBlock.y;
        int bz = modOpis.selectedBlock.z;

        double offset = 0.02D;
        double delta = 1.0D + 2.0D * offset;

        double x = bx - px - offset;
        double y = by - py - offset;
        double z = bz - pz - offset;

        GL11.glPushAttrib(16640);

        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(false);

        Tessellator tessellator = Tessellator.field_78398_a;
        tessellator.func_78382_b();

        tessellator.func_78370_a(255, 0, 0, 150);

        tessellator.func_78377_a(x, y, z);
        tessellator.func_78377_a(x + delta, y, z);
        tessellator.func_78377_a(x + delta, y, z + delta);
        tessellator.func_78377_a(x, y, z + delta);

        tessellator.func_78377_a(x, y + delta, z);
        tessellator.func_78377_a(x, y + delta, z + delta);
        tessellator.func_78377_a(x + delta, y + delta, z + delta);
        tessellator.func_78377_a(x + delta, y + delta, z);

        tessellator.func_78377_a(x, y, z);
        tessellator.func_78377_a(x, y + delta, z);
        tessellator.func_78377_a(x + delta, y + delta, z);
        tessellator.func_78377_a(x + delta, y, z);

        tessellator.func_78377_a(x, y, z + delta);
        tessellator.func_78377_a(x + delta, y, z + delta);
        tessellator.func_78377_a(x + delta, y + delta, z + delta);
        tessellator.func_78377_a(x, y + delta, z + delta);

        tessellator.func_78377_a(x, y, z);
        tessellator.func_78377_a(x, y, z + delta);
        tessellator.func_78377_a(x, y + delta, z + delta);
        tessellator.func_78377_a(x, y + delta, z);

        tessellator.func_78377_a(x + delta, y, z);
        tessellator.func_78377_a(x + delta, y + delta, z);
        tessellator.func_78377_a(x + delta, y + delta, z + delta);
        tessellator.func_78377_a(x + delta, y, z + delta);

        tessellator.func_78381_a();

        GL11.glEnable(3553);

        GL11.glPopAttrib();
    }

}
