/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import static java.lang.Integer.min;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Arrays;
import java.util.UUID;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmEncodeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static BigInteger RESERVED_VALUES_OFFSET = BigInteger.valueOf(1);//note: this is equal to the number of special values; currently only one: SPECIAL_VALUE_BLANK
    private final static BigInteger SPECIAL_VALUE_BLANK = BigInteger.valueOf(0); // this must be less than RESERVED_VALUES_OFFSET
    private final static String SPECIAL_VALUE_BLANK_ENCODED = encodeBytes(new byte[]{});
    public final static int ENCODE_MAX_RADIX = 36;//same as Character.MAX_RADIX
    private final static char[] X_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v'};
    private final static byte[] X_TABLE_REV = new byte[256];
    private final static char X_CHAR = 'x';

    static {
        for (byte i = 0; i < X_TABLE.length; i++) {
            X_TABLE_REV[X_TABLE[i]] = i;
        }
    }

    public static String encodeString(String string) {
        return encodeBytes(string.getBytes(Charsets.UTF_8));
    }

    public static String encodeBytes(byte[] data, int offset, int len) {
        if (offset == 0 && len == data.length) {
            return encodeBytes(data);
        } else {
            return encodeBytes(Arrays.copyOfRange(data, offset, offset + len));
        }
    }

    public static String encodeBytes(byte[] data) {
//        return xencodeBytes(data);
        return lencodeBytes(data);
    }

    public static String decodeString(String data) {
//        return tryDecode(data, list((Function<String, String>) CmEncodeUtils::xdecodeString, (Function<String, String>) CmEncodeUtils::ldecodeString));
        return ldecodeString(data);
    }

    public static byte[] decodeBytes(String data) {
//        return tryDecode(data, list((Function<String, byte[]>) CmEncodeUtils::xdecodeBytes, (Function<String, byte[]>) CmEncodeUtils::ldecodeBytes));
        return ldecodeBytes(data);
    }

    //    private static <A, B> B tryDecode(A input, List<Function<A, B>> algos) {
//        for (Function<A, B> algo : algos) {
//            try {
//                return algo.apply(input);
//            } catch (Exception ex) {
//                LOGGER.debug("error decoding data", ex);
//            }
//        }
//        throw runtime("failed to decode data =< %s >", abbreviate(input));
//    }
    public static String encodeUuid(UUID uuid) {
        return encodeBytes(uuidToBytes(uuid));
    }

    public static UUID decodeUuid(String hash) {
        return bytesToUuid(decodeBytes(hash));
    }

    public static String encodeHex(String value) {
        return "HEX" + Hex.encodeHexString(value.getBytes(UTF_8));
    }

    public static String decodeIfHex(String value) {
        if (nullToEmpty(value).matches("(0x|HEX)[0-9a-fA-F]*")) {
            try {
                return new String(Hex.decodeHex(value.replaceFirst("^(0x|HEX)", "")), UTF_8);
            } catch (DecoderException ex) {
                throw runtime(ex, "error decoding hex value = %s", value);
            }
        } else {
            return value;
        }
    }

    public static String xencodeString(String string) {
        return xencodeBytes(string.getBytes(Charsets.UTF_8));
    }

    public static String xencodeBytes(byte[] data) {
        return xencodeBytes(data, 0, data.length);
    }

    public static String xencodeBytes(byte[] data, int offset, int len) {
        StringWriter writer = new StringWriter();
        for (int i = offset; i < offset + len; i += 5) {
            int count = min(5, (offset + len) - i);
            if (count == 5) {
                writer.append(X_TABLE[((data[i] >> 4 & 0B00001111) | (data[i + 4] >> 3 & 0B00010000))]);
                writer.append(X_TABLE[((data[i] & 0B00001111) | (data[i + 4] >> 2 & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 1] >> 4 & 0B00001111) | (data[i + 4] >> 1 & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 1] & 0B00001111) | (data[i + 4] & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 2] >> 4 & 0B00001111) | (data[i + 4] << 1 & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 2] & 0B00001111) | (data[i + 4] << 2 & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 3] >> 4 & 0B00001111) | (data[i + 4] << 3 & 0B00010000))]);
                writer.append(X_TABLE[((data[i + 3] & 0B00001111) | (data[i + 4] << 4 & 0B00010000))]);
            } else {
                for (int j = 0; j < count; j++) {
                    writer.append(X_TABLE[data[i + j] >> 4 & 0B00001111]);
                    writer.append(X_TABLE[data[i + j] & 0B00001111]);
                }
                if (count == 4) {
                    writer.append(X_CHAR);
                }
            }
        }
        return writer.toString();
    }

    public static String xdecodeString(String data) {
        return new String(xdecodeBytes(data), Charsets.UTF_8);
    }

    public static byte[] xdecodeBytes(String data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < data.length(); i += 8) {
            int count = min(8, data.length() - i);
            if (count == 8 && (data.length() != i + 9 || data.charAt(i + 8) != X_CHAR)) {
                out.write((X_TABLE_REV[data.charAt(i)] << 4 & 0B11110000) | ((X_TABLE_REV[data.charAt(i + 1)] & 0B00001111)));
                out.write((X_TABLE_REV[data.charAt(i + 2)] << 4 & 0B11110000) | ((X_TABLE_REV[data.charAt(i + 3)] & 0B00001111)));
                out.write((X_TABLE_REV[data.charAt(i + 4)] << 4 & 0B11110000) | ((X_TABLE_REV[data.charAt(i + 5)] & 0B00001111)));
                out.write((X_TABLE_REV[data.charAt(i + 6)] << 4 & 0B11110000) | ((X_TABLE_REV[data.charAt(i + 7)] & 0B00001111)));
                out.write((X_TABLE_REV[data.charAt(i)] << 3 & 0B10000000)
                        | (X_TABLE_REV[data.charAt(i + 1)] << 2 & 0B01000000)
                        | (X_TABLE_REV[data.charAt(i + 2)] << 1 & 0B00100000)
                        | (X_TABLE_REV[data.charAt(i + 3)] & 0B00010000)
                        | (X_TABLE_REV[data.charAt(i + 4)] >> 1 & 0B00001000)
                        | (X_TABLE_REV[data.charAt(i + 5)] >> 2 & 0B00000100)
                        | (X_TABLE_REV[data.charAt(i + 6)] >> 3 & 0B00000010)
                        | (X_TABLE_REV[data.charAt(i + 7)] >> 4 & 0B00000001));
            } else {
                if (data.charAt(i) != X_CHAR) {
                    for (int j = 0; j < count; j += 2) {
                        out.write((X_TABLE_REV[data.charAt(i + j)] << 4 & 0B11110000) | (X_TABLE_REV[data.charAt(i + j + 1)] & 0B00001111));
                    }
                }
            }
        }
        byte[] encoded = out.toByteArray();
        LOGGER.trace("decoded bytes = {}", lazyString(() -> Hex.encodeHexString(encoded)));
        return encoded;
    }

    public static String xencodeUuid(UUID uuid) {
        return xencodeBytes(uuidToBytes(uuid));
    }

    public static UUID xdecodeUuid(String hash) {
        return bytesToUuid(xdecodeBytes(hash));
    }

    @Deprecated
    public static String lencodeString(String string) {
        return lencodeBytes(string.getBytes(Charsets.UTF_8));
    }

    @Deprecated
    public static String lencodeBytes(byte[] data) {
        BigInteger num;
        int zerosAtBeginning = 0;
        while (zerosAtBeginning < data.length - 1 && data[zerosAtBeginning] == 0) {
            zerosAtBeginning++;
        }
        if (data.length == 0) {
            num = SPECIAL_VALUE_BLANK;
        } else {
            BigInteger numOrig = new BigInteger(data, zerosAtBeginning, data.length - zerosAtBeginning);
            num = numOrig.multiply(BigInteger.valueOf(2));
            if (num.compareTo(BigInteger.ZERO) < 0) {
                num = num.abs().add(BigInteger.ONE);
            }
            num = num.add(RESERVED_VALUES_OFFSET);
        }
        String encodedData = num.toString(ENCODE_MAX_RADIX);
        if (zerosAtBeginning > 0) {
            encodedData = "0".repeat(zerosAtBeginning) + encodedData;
        }
        return encodedData;
    }

    @Deprecated
    public static String ldecodeString(String data) {
        return new String(ldecodeBytes(data), Charsets.UTF_8);
    }

    @Deprecated
    public static byte[] ldecodeBytes(String data) {
        if (equal(data, SPECIAL_VALUE_BLANK_ENCODED)) {
            return new byte[]{};
        } else {
            int zerosAtBeginning = data.replaceAll("^([0]*).*$", "$1").length();
            BigInteger num = new BigInteger(data.substring(zerosAtBeginning), ENCODE_MAX_RADIX);
            num = num.subtract(RESERVED_VALUES_OFFSET);
            if (num.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE)) {
                num = num.subtract(BigInteger.ONE).negate();
            }
            num = num.divide(BigInteger.valueOf(2));
            byte[] decodedData = num.toByteArray();
            if (zerosAtBeginning > 0) {
                ByteBuffer buffer = ByteBuffer.allocate(decodedData.length + zerosAtBeginning);
                for (int i = 0; i < zerosAtBeginning; i++) {
                    buffer.put((byte) 0);
                }
                buffer.put(decodedData);
                decodedData = buffer.array();
            }
            return decodedData;
        }
    }

    @Deprecated
    public static String lencodeUuid(UUID uuid) {
        return lencodeBytes(uuidToBytes(uuid));
    }

    @Deprecated
    public static UUID ldecodeUuid(String hash) {
        return bytesToUuid(ldecodeBytes(hash));
    }

    public static UUID bytesToUuid(byte[] data) {
        checkArgument(data.length == 16);
        long l1 = new BigInteger(ArrayUtils.subarray(data, 0, 8)).longValueExact();
        long l2 = new BigInteger(ArrayUtils.subarray(data, 8, 16)).longValueExact();
        UUID uuid = new UUID(l1, l2);
        return uuid;
    }

    public static byte[] uuidToBytes(UUID uuid) {
        byte[] b1 = BigInteger.valueOf(uuid.getMostSignificantBits()).toByteArray();//TODO pad array
        checkArgument(b1.length == 8);
//		assertEquals(8, b1.length);
        byte[] b2 = BigInteger.valueOf(uuid.getLeastSignificantBits()).toByteArray();//TODO pad array
        checkArgument(b1.length == 8);
//		assertEquals(8, b2.length);
        byte[] data = new byte[16];
        for (int i = 0; i < 8; i++) {
            data[i] = b1[i];
            data[i + 8] = b2[i];
        }
        return data;
    }

    @Nullable
    public static String sanitizeStringForId(@Nullable String id) {
        return nullToEmpty(id).replaceAll("[^0-9a-zA-Z]", "");
    }
}
