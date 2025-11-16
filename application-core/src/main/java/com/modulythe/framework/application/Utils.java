package com.modulythe.framework.application;

import java.util.regex.Pattern;

public class Utils {

    private Utils() {
    }

    private static final String REG_EXP = "^[\\w-]+\\.[\\w-]+\\.[\\w-]+$";
    private static final Pattern JWT_CHECKER_STATE_MACHINE = Pattern.compile(REG_EXP);

    public static boolean isValidJwtToken(String token) {
        return JWT_CHECKER_STATE_MACHINE.matcher(token).matches();
    }

    public static boolean isNotValidJwtToken(String token) {
        return !isValidJwtToken(token);
    }
}
