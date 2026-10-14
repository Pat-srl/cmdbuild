/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth.utils;

import java.util.stream.IntStream;

/**
 *
 * @author ataboga
 */
public class LdapUtils {

    public static String encodeFilter(String value) {
        if (value == null) {
            return null;
        }

        String[] filterEscapeTable = new String['\\' + 1];
        for (char c = 0; c < filterEscapeTable.length; c++) {
            filterEscapeTable[c] = String.valueOf(c);
        }
        filterEscapeTable['*'] = "\\2a";
        filterEscapeTable['('] = "\\28";
        filterEscapeTable[')'] = "\\29";
        filterEscapeTable['\\'] = "\\5c";
        filterEscapeTable[0] = "\\00";

        StringBuilder encodedValue = new StringBuilder(value.length() * 2);
        IntStream.range(0, value.length()).forEach(i -> {
            char c = value.charAt(i);
            if (c < filterEscapeTable.length) {
                encodedValue.append(filterEscapeTable[c]);
            } else {
                encodedValue.append(c);
            }
        });
        return encodedValue.toString();
    }
}
