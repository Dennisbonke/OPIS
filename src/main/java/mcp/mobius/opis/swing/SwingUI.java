package mcp.mobius.opis.swing;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class SwingUI extends JFrame implements WindowListener, ChangeListener, IMessageHandler {

    public static HashSet<JButtonAccess> registeredButtons = new HashSet();
    private static SwingUI _instance = new SwingUI();
    private JPanel contentPane;
    private JTabbedPane tabbedPane;

    public static SwingUI instance()
    {
        return _instance;
    }

    public void showUI()
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    SwingUI.instance().setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private SwingUI()
    {
        setTitle("Opis Control Panel");
        setDefaultCloseOperation(2);
        setBounds(100, 100, 893, 455);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(this.contentPane);

        this.tabbedPane = new JTabbedPane(1);
        this.tabbedPane.addChangeListener(this);
        this.contentPane.add(this.tabbedPane, "Center");

        addWindowListener(this);
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(60000);
    }

    public void windowActivated(WindowEvent arg0) {}

    public void windowClosed(WindowEvent arg0)
    {
        mcp.mobius.opis.modOpis.swingOpen = false;
        PacketManager.sendToServer(new PacketReqData(Message.COMMAND_UNREGISTER_SWING));
    }

    public void windowClosing(WindowEvent arg0) {}

    public void windowDeactivated(WindowEvent arg0) {}

    public void windowDeiconified(WindowEvent arg0) {}

    public void windowIconified(WindowEvent arg0) {}

    public void windowOpened(WindowEvent arg0) {}

    public JTabbedPane getTabbedPane()
    {
        return this.tabbedPane;
    }

    public boolean handleMessage(Message msg, PacketBase rawdata)
    {
        AccessLevel level;
        switch (msg.ordinal())
        {
            case 1:
                level = AccessLevel.values()[((SerialInt)rawdata.value).value];
                for (JButtonAccess button : registeredButtons) {
                    if (level.ordinal() < button.getAccessLevel().ordinal()) {
                        button.setEnabled(false);
                    } else {
                        button.setEnabled(true);
                    }
                }
                break;
            case 2:
                mcp.mobius.opis.modOpis.swingOpen = true;
                showUI();
                Minecraft.func_71410_x().func_147108_a(new GuiChat());
                PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(SelectedTab.SUMMARY.ordinal())));
                break;
            default:
                return false;
        }
        return true;
    }

    public void stateChanged(ChangeEvent e)
    {
        Component source = ((JTabbedPane)e.getSource()).getSelectedComponent();
        if ((source instanceof ITabPanel))
        {
            ITabPanel panel = (ITabPanel)source;
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }
        if ((source instanceof JTabbedPane))
        {
            JTabbedPane pane = (JTabbedPane)source;
            ITabPanel panel = (ITabPanel)pane.getSelectedComponent();
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }
    }

}
