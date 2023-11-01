package com.application_task.app.controllers.factory;

import com.application_task.app.controllers.WebController;
import com.application_task.app.controllers.impl.AuthorController;
import com.application_task.app.controllers.impl.MovieController;
import org.jetbrains.annotations.NotNull;

public class WebControllerFactory {
    public static WebController create(final @NotNull Type type) {
        return switch (type) {
            case MOVIE -> new MovieController();
            case AUTHOR -> new AuthorController();
        };
    }

    public enum Type {
        MOVIE,
        AUTHOR
    }
}
