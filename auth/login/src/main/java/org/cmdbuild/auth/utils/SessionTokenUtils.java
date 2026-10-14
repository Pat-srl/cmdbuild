/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.LoginData;
import org.cmdbuild.auth.login.LoginDataImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SessionTokenUtils {

    private final static Pattern BASIC_PATTERN = Pattern.compile("basic([^g]+)g([^g]+)", CASE_INSENSITIVE);

    public static String buildBasicAuthToken(String username, String password) {
        return format("basic%sg%s", Hex.encodeHexString(checkNotBlank(username).getBytes(StandardCharsets.UTF_8)), Hex.encodeHexString(checkNotBlank(password).getBytes(StandardCharsets.UTF_8))).toLowerCase();
    }

    public static boolean isBasicAuthToken(@Nullable String token) {
        return isNotBlank(token) && BASIC_PATTERN.matcher(token).matches();
    }

    public static LoginData basicAuthTokenToLoginData(String token) {
        try {
            Matcher matcher = BASIC_PATTERN.matcher(checkNotBlank(token));
            checkArgument(matcher.matches(), "invalid basic auth token =< %s >", token);
            String username = new String(Hex.decodeHex(checkNotBlank(matcher.group(1))), StandardCharsets.UTF_8),
                    password = new String(Hex.decodeHex(checkNotBlank(matcher.group(2))), StandardCharsets.UTF_8);
            return LoginDataImpl.builder().withLoginString(username).withPassword(password).build();
        } catch (DecoderException ex) {
            throw runtime(ex);
        }
    }

}
