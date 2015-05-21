package com.github.mwarc.realtimeauctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.EventBusBridgeHook;
import org.vertx.java.core.sockjs.SockJSSocket;

public class EventBusHook implements EventBusBridgeHook {

    private static final Logger logger = LoggerFactory.getLogger(EventBusHook.class);

    @Override
    public boolean handleSocketCreated(SockJSSocket socket) {
        logger.info(
            "Client {} has been connected to sever {}",
            socket.writeHandlerID(), socket.localAddress()
        );
        return true;
    }

    @Override
    public void handleSocketClosed(SockJSSocket socket) {
        logger.info(
            "Client {} has been disconnected from server {}",
            socket.writeHandlerID(), socket.localAddress()
        );
    }

    @Override
    public boolean handleSendOrPub(SockJSSocket socket, boolean send, JsonObject message, String address) {
        logger.info(
            "Event sent from client {} to server {}. Event {} will not be published to topic {}",
            socket.writeHandlerID(), socket.localAddress(), message, address
        );
        return false;
    }

    @Override
    public boolean handlePreRegister(SockJSSocket socket, String address) {
        logger.info(
            "Client {} has subscribed to topic {} on server {}",
            socket.writeHandlerID(), address, socket.localAddress()
        );
        return true;
    }

    @Override
    public void handlePostRegister(SockJSSocket socket, String address) {
    }

    @Override
    public boolean handleUnregister(SockJSSocket socket, String address) {
        logger.info(
            "Client {} has unsubscribed from topic {} on server {}",
            socket.writeHandlerID(), address, socket.localAddress()
        );
        return true;
    }

    @Override
    public boolean handleAuthorise(JsonObject message, String sessionID, Handler<AsyncResult<Boolean>> handler) {
        logger.info("Will not authorize {}", message);
        return false;
    }
}
