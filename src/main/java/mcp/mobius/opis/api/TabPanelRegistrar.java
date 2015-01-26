package mcp.mobius.opis.api;

import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.SwingUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum TabPanelRegistrar implements ChangeListener {

    INSTANCE;

    private HashMap<String, JTabbedPane> sections = new HashMap();
    private HashMap<SelectedTab, ITabPanel> lookup = new HashMap();

    private TabPanelRegistrar() {}

    public JTabbedPane registerSection(String name)
    {
        JTabbedPane pane = new JTabbedPane();
        pane.addChangeListener(this);
        this.sections.put(name, pane);
        SwingUI.instance().getTabbedPane().addTab(name, pane);
        return pane;
    }

    public ITabPanel registerTab(ITabPanel panel, String name)
    {
        this.lookup.put(panel.getSelectedTab(), panel);
        SwingUI.instance().getTabbedPane().addTab(name, (JPanel)panel);
        return panel;
    }

    public ITabPanel registerTab(ITabPanel panel, String name, String section)
    {
        this.lookup.put(panel.getSelectedTab(), panel);
        ((JTabbedPane)this.sections.get(section)).addTab(name, (JPanel)panel);
        return panel;
    }

    public ITabPanel getTab(SelectedTab refname)
    {
        return (ITabPanel)this.lookup.get(refname);
    }

    public JPanel getTabAsPanel(SelectedTab refname)
    {
        return (JPanel)this.lookup.get(refname);
    }

    public void refreshAll()
    {
        for (ITabPanel panel : this.lookup.values()) {
            if (panel.refreshOnString()) {
                panel.refresh();
            }
        }
    }

    public void stateChanged(ChangeEvent e)
    {
        Component source = ((JTabbedPane)e.getSource()).getSelectedComponent();
        if ((source instanceof ITabPanel))
        {
            ITabPanel panel = (ITabPanel)source;
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }
    }

}
