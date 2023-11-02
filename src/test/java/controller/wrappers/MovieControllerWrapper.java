package controller.wrappers;

import com.application_task.app.controllers.impl.MovieController;
import com.sun.net.httpserver.HttpExchange;

public class MovieControllerWrapper extends MovieController {
    public MovieControllerWrapper(String user, String password, String databaseUrl) {
        super(user, password, databaseUrl);
    }

    @Override
    public void execute(HttpExchange httpExchange) {
        super.execute(httpExchange);
    }
}
