package com.application_task.app.util;

import java.util.Map;

/**
 * Represents a request parameters validator
 */
public class ParamValidator {

    /**
     * Checks if the given parameters of movie search are valid
     *
     * @param params {@link Map} which contains parameters to check
     * @return {@code true} if the given parameters are valid
     */
    public static boolean checkMovieParams(Map<String, String> params) {
        String yearOfRelease = Constants.Params.YEAR_OF_RELEASE;
        if (params.containsKey(yearOfRelease)) {
            return params
                    .get(yearOfRelease)
                    .matches(Constants.OfServer.INT_VALUE_REG_EXP);
        }
        return true;
    }

    /**
     * Checks if the given parameters of author search are valid
     *
     * @param params {@link Map} which contains parameters to check
     * @return {@code true} if the given parameters are valid
     */
    public static boolean checkAuthorParams(Map<String, String> params) {
        String age = Constants.Params.AGE;
        if (params.containsKey(age)) {
            return params
                    .get(age)
                    .matches(Constants.OfServer.AGE_REG_EXP);
        }
        return true;
    }
}
