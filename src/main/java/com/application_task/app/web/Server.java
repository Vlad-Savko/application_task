package com.application_task.app.web;

import com.application_task.app.controllers.WebController;
import com.application_task.app.util.Constants;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 *
 * @implSpec creates few contexts for given controllers, implements Builder creational pattern
 */
public class Server implements WebServer {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final Map<String, Handler> controllers;
    private final int port;

    private HttpServer httpServer;

    /**
     * Constructs a {@link WebServer} with the controllers and their related contexts provided by {@code builder}
     *
     * @param builder {@link Builder} of this object
     */
    public Server(Builder builder) {
        this.controllers = new HashMap<>();
        if (builder.controllers != null) {
            this.controllers.putAll(builder.controllers);
        }

        this.port = builder.port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        try {
            this.httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(this.port), Constants.ZERO);
            if (!this.controllers.isEmpty()) {
                this.controllers.forEach((url, handler) -> this.httpServer.createContext(url, handler::handle));
            }

            httpServer.start();
            LOGGER.log(Level.INFO, Constants.OfServer.SERVER_STARTED.formatted(port));
        } catch (IOException e) {
            throw new RuntimeException(Constants.Message.ERROR_STARTING_SERVER, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.httpServer.stop(4);
        LOGGER.log(Level.INFO, Constants.OfServer.SERVER_STOPPED);
    }

    /**
     * Builder of {@link Server} which is the implementation of Builder creational pattern
     */
    public final static class Builder {
        private int port;
        private Map<String, Handler> controllers;

        public Builder controller(String contextPath, WebController webController) {
            if (this.controllers == null) {
                this.controllers = new HashMap<>();
            }
            this.controllers.putIfAbsent(contextPath, new Handler(webController));
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}
