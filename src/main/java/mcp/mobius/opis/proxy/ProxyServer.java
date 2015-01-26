package mcp.mobius.opis.proxy;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class ProxyServer implements IMessageHandler {

    public void init() {}

    public boolean handleMessage(Message msg, PacketBase rawdata)
    {
        return false;
    }

}
