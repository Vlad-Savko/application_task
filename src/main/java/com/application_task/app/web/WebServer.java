package com.application_task.app.web;

/**
 * Represents a web-server, which gets HTTP requests and sends HTTP responses
 */
public interface WebServer {
    /**
     * Starts the web-server
     *
     * @see com.sun.net.httpserver.HttpServer
     */
    void start();

    /**
     * Stops the web-server
     *
     * @see com.sun.net.httpserver.HttpServer
     */
    void stop();
}
