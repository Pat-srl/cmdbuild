/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

import static com.google.common.base.Strings.nullToEmpty;

public class PlatformUtils {

    public static void checkOsUser() {
        if (isLinux() && nullToEmpty(System.getProperty("user.name")).equalsIgnoreCase("root")) {
            throw new IllegalArgumentException("invalid OS user detected: this webapp has been started with ROOT user; this is NOT allowed, use a regular/limited user instead");
        }
    }

    public static boolean isLinux() {
        return nullToEmpty(System.getProperty("os.name")).equalsIgnoreCase("linux");
    }
}
