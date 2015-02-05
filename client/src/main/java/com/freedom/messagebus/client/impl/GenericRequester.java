package com.freedom.messagebus.client.impl;

import com.freedom.messagebus.client.*;
import com.freedom.messagebus.client.message.model.Message;
import com.freedom.messagebus.business.model.Node;
import com.freedom.messagebus.client.core.config.ConfigManager;
import com.freedom.messagebus.client.model.MessageCarryType;
import org.jetbrains.annotations.NotNull;

public class GenericRequester extends AbstractMessageCarryer implements IRequester {

    public GenericRequester() {
        super(MessageCarryType.REQUEST);
    }

    /**
     * send a request and got a response
     *
     * @param msg     request message
     * @param to      send to destination
     * @param timeout response wait timeout
     * @return Message the response
     * @throws com.freedom.messagebus.client.MessageResponseTimeoutException
     */
    @Override
    public Message request(@NotNull Message msg,
                           @NotNull String to,
                           long timeout) throws MessageResponseTimeoutException {
        final MessageContext cxt = new MessageContext();
        cxt.setCarryType(MessageCarryType.REQUEST);
        cxt.setAppId(super.context.getAppId());

        cxt.setSourceNode(ConfigManager.getInstance().getAppIdQueueMap().get(this.context.getAppId()));
        Node node = ConfigManager.getInstance().getQueueNodeMap().get(to);
        cxt.setTargetNode(node);
        cxt.setTimeout(timeout);
        cxt.setMessages(new Message[]{msg});

        cxt.setPool(this.context.getPool());
        cxt.setConnection(this.context.getConnection());

        carry(cxt);

        if (cxt.isTimeout() || cxt.getConsumedMsg() == null)
            throw new MessageResponseTimeoutException("message request time out.");

        return cxt.getConsumedMsg();
    }

}