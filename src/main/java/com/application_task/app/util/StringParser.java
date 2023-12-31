package com.application_task.app.util;

import com.application_task.app.exception.WrongSearchFiltersException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a parser, which parses and validates strings
 */
public class StringParser {
    /**
     * Represents a set with possible movie search filters
     */
    private static final Set<String> paramsForMovies = Set.of(
            Constants.Params.NAME,
            Constants.Params.YEAR_OF_RELEASE,
            Constants.Params.AUTHOR_ID
    );

    /**
     * Represents a set with possible author search filters
     */
    private static final Set<String> paramsForAuthors = Set.of(
            Constants.Params.FIRST_NAME,
            Constants.Params.LAST_NAME,
            Constants.Params.AGE,
            Constants.Params.MOVIE_ID
    );

    /**
     * Validates and parses filters by given type
     *
     * @param str  {@link String} to validate and parse
     * @param type type of filters to apply on given string
     * @return {@link Map} of parsed and validated filters
     * @throws WrongSearchFiltersException if validation or parsing fails
     * @see WrongSearchFiltersException
     */
    public static Map<String, String> parseMovieFilters(String str, Type type) throws WrongSearchFiltersException {

        Set<String> params = switch (type) {
            case MOVIE -> paramsForMovies;
            case AUTHOR -> paramsForAuthors;
        };
        Map<String, String> filters = new HashMap<>();
        for (String part : str.split(",")) {
            String[] half = part.split("=");
            String leftHalf, rightHalf;
            try {
                leftHalf = half[0];
                if (leftHalf.equals(Constants.Params.NAME)) {
                    rightHalf = half[1]
                            .replaceAll("%27", "")
                            .replaceAll("%20", " ");
                } else {
                    rightHalf = half[1];
                }
            } catch (IndexOutOfBoundsException e) {
                throw new WrongSearchFiltersException(Constants.Message.ERROR_NO_SUCH_FILTER_PARAM.formatted(part));
            }
            if (!params.contains(leftHalf)) {
                throw new WrongSearchFiltersException(Constants.Message.ERROR_NO_SUCH_FILTER_PARAM.formatted(leftHalf));
            }
            filters.put(leftHalf, rightHalf);
        }

        return filters;
    }

    /**
     * Represents possible filters types
     */
    public enum Type {
        MOVIE,
        AUTHOR
    }

}
