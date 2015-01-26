package mcp.mobius.opis.api;

import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum MessageHandlerRegistrar {

    INSTANCE;

    private HashMap<Message, HashSet<IMessageHandler>> msgHandlers = new HashMap();

    private MessageHandlerRegistrar() {}

    public void registerHandler(Message msg, IMessageHandler handler)
    {
        if (!this.msgHandlers.containsKey(msg)) {
            this.msgHandlers.put(msg, new HashSet());
        }
        ((HashSet)this.msgHandlers.get(msg)).add(handler);
    }

    public void routeMessage(Message msg, PacketBase rawdata)
    {
        if (this.msgHandlers.containsKey(msg)) {
            for (IMessageHandler handler : (HashSet)this.msgHandlers.get(msg)) {
                if (!handler.handleMessage(msg, rawdata)) {
                    modOpis.log.warning(String.format("Unhandled msg %s in handler %s", new Object[] { msg, handler }));
                }
            }
        } else {
            modOpis.log.warning(String.format("Unhandled msg : %s", new Object[] { msg }));
        }
    }

}
