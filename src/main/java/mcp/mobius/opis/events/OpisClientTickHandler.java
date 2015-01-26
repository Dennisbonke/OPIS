package mcp.mobius.opis.events;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum OpisClientTickHandler {

    INSTANCE;

    public long profilerUpdateTickCounter = 0L;
    public long profilerRunningTicks = 0L;
    public EventTimer timer500 = new EventTimer(500L);
    public EventTimer timer1000 = new EventTimer(1000L);
    public EventTimer timer2000 = new EventTimer(2000L);
    public EventTimer timer5000 = new EventTimer(5000L);
    public EventTimer timer10000 = new EventTimer(10000L);

    private OpisClientTickHandler() {}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickEnd(TickEvent.ClientTickEvent event)
    {
        if ((this.timer1000.isDone()) &&
                (modOpis.swingOpen)) {
            PacketManager.sendToServer(new PacketReqData(Message.STATUS_PING, new SerialLong(System.nanoTime())));
        }
        if (modOpis.profilerRunClient)
        {
            ((PanelRenderTileEnts)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS)).getBtnRunRender().setText("Running...");
            ((PanelRenderEntities)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES)).getBtnRunRender().setText("Running...");
            ((PanelRenderHandlers)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERHANDLERS)).getBtnRunRender().setText("Running...");
            ((PanelEventClient)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS)).getBtnRunRender().setText("Running...");
        }
        else
        {
            ((PanelRenderTileEnts)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS)).getBtnRunRender().setText("Run Render");
            ((PanelRenderEntities)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES)).getBtnRunRender().setText("Run Render");
            ((PanelRenderHandlers)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERHANDLERS)).getBtnRunRender().setText("Run Render");
            ((PanelEventClient)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS)).getBtnRunRender().setText("Run Render");
        }
        this.profilerUpdateTickCounter += 1L;
        if ((this.profilerRunningTicks < modOpis.profilerMaxTicks) && (modOpis.profilerRunClient))
        {
            this.profilerRunningTicks += 1L;
        }
        else if ((this.profilerRunningTicks >= modOpis.profilerMaxTicks) && (modOpis.profilerRunClient))
        {
            this.profilerRunningTicks = 0L;
            modOpis.profilerRunClient = false;
            ProfilerSection.desactivateAll(Side.CLIENT);

            System.out.printf("Profiling done\n", new Object[0]);

            updateTabs();
        }
    }

    private void updateTabs()
    {
        ArrayList<DataTileEntityRender> tileEntData = new ArrayList();
        double tileEntTotal = 0.0D;
        for (TileEntity te : ((ProfilerRenderTileEntity)ProfilerSection.RENDER_TILEENTITY.getProfiler()).data.keySet()) {
            try
            {
                DataTileEntityRender dataTe = new DataTileEntityRender().fill(te);
                tileEntData.add(dataTe);
                tileEntTotal += dataTe.update.timing.doubleValue();
            }
            catch (Exception e)
            {
                modOpis.log.warning(String.format("Error while adding entity %s to the list", new Object[] { te }));
            }
        }
        System.out.printf("Rendered %d TileEntities\n", new Object[] { Integer.valueOf(tileEntData.size()) });

        Collections.sort(tileEntData);
        ((PanelRenderTileEnts)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS)).setTable(tileEntData);
        ((PanelRenderTileEnts)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERTILEENTS)).getLblTotal().setText(String.format("Total : %.3f µs", new Object[] { Double.valueOf(tileEntTotal / 1000.0D) }));



        ArrayList<DataEntityRender> entData = new ArrayList();
        double entTotal = 0.0D;
        for (Entity ent : ((ProfilerRenderEntity)ProfilerSection.RENDER_ENTITY.getProfiler()).data.keySet()) {
            try
            {
                DataEntityRender dataEnt = new DataEntityRender().fill(ent);
                entData.add(dataEnt);
                entTotal += dataEnt.update.timing.doubleValue();
            }
            catch (Exception e)
            {
                modOpis.log.warning(String.format("Error while adding entity %s to the list", new Object[] { ent }));
            }
        }
        System.out.printf("Rendered %d Entities\n", new Object[] { Integer.valueOf(entData.size()) });

        Collections.sort(entData);
        ((PanelRenderEntities)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES)).setTable(entData);
        ((PanelRenderEntities)TabPanelRegistrar.INSTANCE.getTab(SelectedTab.RENDERENTITIES)).getLblTotal().setText(String.format("Total : %.3f µs", new Object[] { Double.valueOf(entTotal / 1000.0D) }));







        ArrayList<DataEvent> timingEvents = new ArrayList();
        HashBasedTable<Class, String, DescriptiveStatistics> eventData = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).data;
        HashBasedTable<Class, String, String> eventMod = ((ProfilerEvent)ProfilerSection.EVENT_INVOKE.getProfiler()).dataMod;
        for (Table.Cell<Class, String, DescriptiveStatistics> cell : eventData.cellSet()) {
            timingEvents.add(new DataEvent().fill(cell, (String)eventMod.get(cell.getRowKey(), cell.getColumnKey())));
        }
        ((PanelEventClient) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.CLIENTEVENTS)).setTable(timingEvents);



        ArrayList<DataBlockRender> blockData = new ArrayList();
        for (CoordinatesBlock coord : ((ProfilerRenderBlock) ProfilerSection.RENDER_BLOCK.getProfiler()).data.keySet()) {
            try
            {
                DataBlockRender dataBlock = new DataBlockRender().fill(coord);
                blockData.add(dataBlock);
            }
            catch (Exception e)
            {
                modOpis.log.warning(String.format("Error while adding block %s to the list", new Object[] { coord }));
            }
        }
    }

}
