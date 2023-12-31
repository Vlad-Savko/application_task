package com.application_task.app;

import com.application_task.app.controllers.factory.WebControllerFactory;
import com.application_task.app.util.Constants;
import com.application_task.app.web.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server.Builder()
                .port(8080)
                .controller(Constants.OfServer.MOVIE_CONTROLLER_CONTEXT_PATH, WebControllerFactory.create(WebControllerFactory.Type.MOVIE))
                .controller(Constants.OfServer.AUTHOR_CONTROLLER_CONTEXT_PATH, WebControllerFactory.create(WebControllerFactory.Type.AUTHOR))
                .build();

        server.start();
    }
}
