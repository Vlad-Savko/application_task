package controller.wrappers;

import com.application_task.app.controllers.impl.AuthorController;
import com.sun.net.httpserver.HttpExchange;

public class AuthorControllerWrapper extends AuthorController {

    public AuthorControllerWrapper(String user, String password, String databaseUrl) {
        super(user, password, databaseUrl);
    }

    @Override
    public void execute(HttpExchange httpExchange) {
        super.execute(httpExchange);
    }
}
