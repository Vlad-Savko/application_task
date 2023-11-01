package com.application_task.app.util;

import java.util.Map;

public class ParamValidator {
    public static boolean checkMovieParams(Map<String, String> params) {
        String yearOfRelease = Constants.Params.YEAR_OF_RELEASE;
        if (params.containsKey(yearOfRelease)) {
            return params
                    .get(yearOfRelease)
                    .matches(Constants.OfServer.INT_VALUE_REG_EXP);
        }
        return true;
    }

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
