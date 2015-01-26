package mcp.mobius.opis.data.managers;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.events.OpisServerTickHandler;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class MetaManager {

    public static void reset()
    {
        mcp.mobius.opis.modOpis.profilerRun = false;
        mcp.mobius.opis.modOpis.selectedBlock = null;
        OpisServerTickHandler.INSTANCE.profilerRunningTicks = 0;

        ProfilerSection.resetAll(Side.SERVER);
        ProfilerSection.desactivateAll(Side.SERVER);
    }

}
