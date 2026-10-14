/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import java.util.function.Supplier;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PasswordBulletsUtils {

    public static final String BULLETS = "••••••";

    public static boolean isBullets(String string) {
        return isNotBlank(string) && string.trim().matches("•+");
    }

    @Nullable
    public static String handleBullets(String string, Supplier<String> defaultSupplier) {
        if (isBullets(string)) {
            return defaultSupplier.get();
        } else {
            return string;
        }
    }

    @Nullable
    public static String stringToBullets(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else {
            return BULLETS;
        }
    }
}
