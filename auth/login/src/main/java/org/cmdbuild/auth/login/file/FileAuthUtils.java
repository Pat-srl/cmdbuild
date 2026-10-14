/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.file;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAuthUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static AuthFile buildAuthFile(File authDir) {
        LOGGER.debug("build auth file for tomcat authDir =< {} >", authDir.getAbsolutePath());
        checkArgument(authDir.exists() && authDir.isDirectory(), "invalid auth dir = %s", authDir);
        String fileContent = format("v1:%s:%s", randomId(8), encodeString(Long.toString(now().toEpochSecond()))), fileName = hash(fileContent);
        File file = new File(authDir, fileName);
        try {
            file.createNewFile();
            writeToFile(file, fileContent);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        checkArgument(file.exists());
        String password = format("file:%s", fileName);
        LOGGER.debug("built auth token =< {} >", password);
        return new AuthFile() {
            @Override
            public File getFile() {
                return file;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
    }

    public static boolean isAuthFilePassword(@Nullable String password) {
        return isNotBlank(password) && password.matches("^file:[a-zA-Z0-9]+$");
    }

    public static boolean isValidAuthFilePassword(File authDir, String password) {
        checkArgument(isAuthFilePassword(password));
        try {
            Matcher matcher = Pattern.compile("^file:([a-zA-Z0-9]+)$").matcher(password);
            checkArgument(matcher.matches(), "invalid auth file password value");
            String fileName = checkNotBlank(matcher.group(1));
            String fileContent = readToString(new File(authDir, fileName));
            matcher = Pattern.compile("^v1:[0-9a-zA-Z]+:([0-9a-zA-Z]+)$").matcher(fileContent);
            checkArgument(matcher.matches(), "invalid auth file content");
            long timestampSeconds = toLong(decodeString(checkNotBlank(matcher.group(1))));
            checkArgument(abs(timestampSeconds - now().toEpochSecond()) < 600, "auth file content is expired");
            return true;
        } catch (Exception ex) {
            LOGGER.error(marker(), "error validating auth file password", ex);
            return false;
        }
    }

    public interface AuthFile {

        File getFile();

        String getPassword();
    }

}
