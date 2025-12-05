package com.modulythe.framework.application;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/*
 * validate JWT token formats
 */
public class Utils {

    private Utils() {
    }

    private static final String REG_EXP = "^[\\w-]+\\.[\\w-]+\\.[\\w-]+$";
    private static final Pattern JWT_CHECKER_STATE_MACHINE = Pattern.compile(REG_EXP);

    public static boolean isValidJwtToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return JWT_CHECKER_STATE_MACHINE.matcher(token).matches();
    }

    public static boolean isNotValidJwtToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return !isValidJwtToken(token);
    }
}
