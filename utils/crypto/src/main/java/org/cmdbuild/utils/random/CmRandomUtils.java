/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.random;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.encode.CmEncodeUtils.ENCODE_MAX_RADIX;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeUuid;

public class CmRandomUtils {

    public static final int DEFAULT_RANDOM_ID_SIZE = 24;

    public static boolean isRandomId(String value) {
        return isNotBlank(value) && equal(checksum(value.substring(1)), value.charAt(0));
    }

    public static String randomId() {
        return randomId(DEFAULT_RANDOM_ID_SIZE);
    }

    public static String randomId(int size) {
        checkArgument(size > 0);
        byte[] data = new byte[size];
        ThreadLocalRandom.current().nextBytes(data);
        String str = encodeBytes(data).substring(0, size - 1);
        str = checksum(str) + str;
        return str;
    }

    public static String randomIdOrEmpty(int size) {
        return size <= 0 ? "" : randomId(size);
    }

    public static String randomIdFromUuid() {
        return encodeUuid(UUID.randomUUID());
    }

    public static byte[] randomBytes() {
        return randomBytes(1024 * (ThreadLocalRandom.current().nextInt(1, 10)));
    }

    public static byte[] randomBytes(int count) {
        byte[] data = new byte[count];
        ThreadLocalRandom.current().nextBytes(data);
        return data;
    }

    public static int randomInt(int max) {
        return new Random().nextInt(max);
    }

    private static char checksum(String str) {
        char sum = 0;
        for (char c : str.toCharArray()) {
            sum += c;
        }
        return BigInteger.valueOf(sum % ENCODE_MAX_RADIX).toString(ENCODE_MAX_RADIX).charAt(0);
    }
}
