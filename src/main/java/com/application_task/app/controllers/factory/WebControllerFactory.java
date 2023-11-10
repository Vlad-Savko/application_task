package com.application_task.app.controllers.factory;

import com.application_task.app.controllers.WebController;
import com.application_task.app.controllers.impl.AuthorController;
import com.application_task.app.controllers.impl.MovieController;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a controller factory (factory method creational pattern implementation), which produces different controllers depending on demand
 */
public class WebControllerFactory {

    /**
     * Produces web-controller depending on demanding type
     *
     * @param type the type of controller to produce
     * @return {@link WebController} of given type
     */
    public static WebController create(final @NotNull Type type) {
        return switch (type) {
            case MOVIE -> new MovieController();
            case AUTHOR -> new AuthorController();
        };
    }

    /**
     * Represents possible types of web-controllers
     */
    public enum Type {
        MOVIE,
        AUTHOR
    }
}
