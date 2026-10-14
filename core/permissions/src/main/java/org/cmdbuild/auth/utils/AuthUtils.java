/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.UnauthorizedAccessException;

public class AuthUtils {

    public static void checkAuthorized(boolean authorized) {
        checkAuthorized(authorized, "access denied");
    }

    public static void checkAuthorized(boolean authorized, String message, Object... args) {
        if (!authorized) {
            throw new UnauthorizedAccessException(message, args);
        }
    }

    @Nullable
    public static String getUsernameFromHistoryUser(@Nullable String user) {
        if (isBlank(user)) {
            return user;
        } else {
            Matcher matcher = Pattern.compile(".*? / (.*)").matcher(user);
            if (matcher.matches()) {
                return matcher.group(1);
            } else {
                return user;
            }
        }
    }

}
