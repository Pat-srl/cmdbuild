/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import jakarta.annotation.Nullable;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.isBase64;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.encode.CmEncodeUtils.ldecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.lencodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeBytes;
import static org.cmdbuild.utils.hash.CmHashUtils.hashToBytes;
import static org.cmdbuild.utils.io.CmCompressionUtils.deflate;
import static org.cmdbuild.utils.io.CmCompressionUtils.inflate;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmPackUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String PACK_MAGIC_PREFIX = "pack", PACK_MAGIC_SUFFIX = "kcap", PAX_MAGIC_PREFIX = "pax", PAX_MAGIC_SUFFIX = "xap";
    private final static int PACK_HASH_SIZE = 4, PACK_VERSION_HASHED = 1, PACK_VERSION_HASHED_DEFLATED = 2;

    public static String pack(String value) {
        return pack(nullToEmpty(value).getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    public static String packIfNotBlankOrNull(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else if (isPacked(value)) {
            return value;
        } else {
            return pack(value);
        }
    }

    @Nullable
    public static String packOrNull(@Nullable byte[] data) {
        return data == null ? null : pack(data);
    }

    public static String pack(byte[] data) {
        return pax(data);
    }

    @Deprecated
    public static String lpack(String value) {
        return lpack(nullToEmpty(value).getBytes(StandardCharsets.UTF_8));
    }

    @Deprecated
    public static String lpack(byte[] data) {
        LOGGER.trace("pack {} bytes of data", data.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try ( DataOutputStream out = new DataOutputStream(byteArrayOutputStream)) {
            int version;
            byte[] hash = hashToBytes(data, PACK_HASH_SIZE),
                    deflated = deflate(data);
            LOGGER.trace("hash = {}", hash);
            if (deflated.length < data.length) {
                LOGGER.trace("using deflate (size reduced to {})", deflated.length);
                version = PACK_VERSION_HASHED_DEFLATED;
                data = deflated;
            } else {
                LOGGER.trace("skip deflate");
                version = PACK_VERSION_HASHED;
            }
            LOGGER.trace("version = {}", version);
            out.writeByte(version);
            out.write(hash);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        String pack = PACK_MAGIC_PREFIX + lencodeBytes(byteArrayOutputStream.toByteArray()) + PACK_MAGIC_SUFFIX;
        LOGGER.trace("pack string size = {}", pack.length());
        return pack;
    }

    public static String pax(String value) {
        return pax(nullToEmpty(value).getBytes(StandardCharsets.UTF_8));
    }

    public static String pax(byte[] data) {
        LOGGER.trace("pack {} bytes of data", data.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try ( DataOutputStream out = new DataOutputStream(byteArrayOutputStream)) {
            int version;
            byte[] hash = hashToBytes(data, PACK_HASH_SIZE),
                    deflated = deflate(data);
            LOGGER.trace("hash = {}", hash);
            if (deflated.length < data.length) {
                LOGGER.trace("using deflate (size reduced to {})", deflated.length);
                version = PACK_VERSION_HASHED_DEFLATED;
                data = deflated;
            } else {
                LOGGER.trace("skip deflate");
                version = PACK_VERSION_HASHED;
            }
            LOGGER.trace("version = {}", version);
            out.writeByte(version);
            out.write(hash);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        String pack = PAX_MAGIC_PREFIX + xencodeBytes(byteArrayOutputStream.toByteArray()) + PAX_MAGIC_SUFFIX;
        LOGGER.trace("pack string size = {}", pack.length());
        return pack;
    }

    public static String unpack(String packed) {
        return new String(unpackBytes(packed), StandardCharsets.UTF_8);
    }

    @Nullable
    public static byte[] unpackBytesOrNull(@Nullable String packed) {
        if (isBlank(packed)) {
            return null;
        } else {
            return unpackBytes(packed);
        }
    }

    public static byte[] unpackBytes(String packed) {
        try {
            LOGGER.trace("unpack value = {}", abbreviate(packed));
            packed = packed.toLowerCase().replaceAll("[^a-z0-9]", "");
            DataInputStream in;
            if (packed.matches("pax[a-z0-9]*xap")) {
                in = new DataInputStream(new ByteArrayInputStream(xdecodeBytes(packed.replaceAll("(^pax|xap$)", ""))));
            } else if (packed.matches("pack[a-z0-9]+(kcap)?")) {
                in = new DataInputStream(new ByteArrayInputStream(ldecodeBytes(packed.replaceAll("(^pack|kcap$)", ""))));
            } else {
                throw runtime("invalid pack format");
            }
            int version = in.readByte();
            LOGGER.trace("version = {}", version);
            boolean inflate = switch (version) {
                case PACK_VERSION_HASHED ->
                    false;
                case PACK_VERSION_HASHED_DEFLATED ->
                    true;
                default ->
                    throw new UnsupportedOperationException("unsupported pack version = " + version);
            };
            byte[] storedHash = new byte[PACK_HASH_SIZE], data;
            checkArgument(in.read(storedHash) == PACK_HASH_SIZE, "unable to read hash bytes from packed data");
            LOGGER.trace("stored hash = {}", storedHash);
            int size = in.readInt();
            checkArgument(size >= 0, "invalid size value = %s", size);
            LOGGER.trace("stored size = {}", size);
            data = toByteArray(in);
            checkArgument(data.length == size, "invalid data size (expected %s but got %s )", size, data.length);
            if (inflate) {
                data = inflate(data);
                LOGGER.trace("using inflate, size grow to = {}", data.length);
            }
            byte[] newHash = hashToBytes(data, PACK_HASH_SIZE);
            checkArgument(Arrays.equals(newHash, storedHash), "invalid data hash (probable corruption of data)");
            LOGGER.trace("hash match ok");
            return data;
        } catch (Exception ex) {
            throw runtime(ex, "invalid pack format for value = " + abbreviate(packed));
        }
    }

    public static String unpackIfPacked(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpack(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed;
        }
    }

    public static byte[] unpackBytesIfPacked(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed).getBytes(StandardCharsets.UTF_8);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackBytes(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static String unpackIfPackedOrBase64(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpack(packed);
        } else if (isBase64(packed)) {
            LOGGER.debug("detected base64 value, decoding");
            return new String(decodeBase64(packed), StandardCharsets.UTF_8);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed;
        }
    }

    public static byte[] unpackBytesIfPackedOrBase64(@Nullable String packed) {
        if (isBlank(packed)) {
            LOGGER.debug("blank data found, returning plain data");
            return nullToEmpty(packed).getBytes(StandardCharsets.UTF_8);
        } else if (isPacked(packed)) {
            LOGGER.debug("detected packed value, unpacking");
            return unpackBytes(packed);
        } else if (isBase64(packed)) {
            LOGGER.debug("detected base64 value, decoding");
            return decodeBase64(packed);
        } else {
            LOGGER.debug("no encoding detected, returning plain data");
            return packed.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static boolean isPacked(String value) {
        return isNotBlank(value) && value.toLowerCase().replaceAll("[^a-z0-9]", "").matches("pa(ck|x)[a-z0-9]+(xap|kcap)?");
    }

}
