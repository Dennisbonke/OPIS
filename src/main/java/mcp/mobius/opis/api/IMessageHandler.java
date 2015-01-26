package mcp.mobius.opis.api;

import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public abstract interface IMessageHandler {

    public abstract boolean handleMessage(Message paramMessage, PacketBase paramPacketBase);

}
