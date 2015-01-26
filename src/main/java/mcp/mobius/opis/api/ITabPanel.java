package mcp.mobius.opis.api;

import mcp.mobius.opis.swing.SelectedTab;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public abstract interface ITabPanel {

    public abstract SelectedTab getSelectedTab();

    public abstract boolean refreshOnString();

    public abstract boolean refresh();

}
