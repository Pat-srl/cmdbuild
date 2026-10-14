package org.cmdbuild.utils.hash.test;

import static com.google.common.base.Objects.equal;
import java.math.BigInteger;
import static java.util.Arrays.asList;
import java.util.UUID;
import org.apache.commons.lang3.ArrayUtils;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeUuid;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeUuid;
import static org.cmdbuild.utils.hash.CmHashUtils.compact;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.hash.CmHashUtils.toIntHash;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertTrue;

public class HashUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testHash1() throws Exception {
        for (int i : asList(1, 2, 4, 5, 8, 9, 13, 15, 16, 17, 24, 30, 32, 33, 34, 50, 100, 200, 512, 513, 1000, 2000)) {
            assertEquals(i, hash("asd", i).trim().length());
        }
    }

    @Test
    public void testUuidToHash() {
        UUID in = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        byte[] b1 = BigInteger.valueOf(in.getMostSignificantBits()).toByteArray();//TODO pad array
        assertEquals(8, b1.length);
        byte[] b2 = BigInteger.valueOf(in.getLeastSignificantBits()).toByteArray();//TODO pad array
        assertEquals(8, b2.length);
        byte[] data = new byte[16];
        for (int i = 0; i < 8; i++) {
            data[i] = b1[i];
            data[i + 8] = b2[i];
        }
        BigInteger numOrig = new BigInteger(data);
        BigInteger num = numOrig.multiply(BigInteger.valueOf(2));
        if (num.compareTo(BigInteger.ZERO) < 0) {
            num = num.abs().add(BigInteger.ONE);
        }
        String hash = num.toString(Character.MAX_RADIX);

        logger.debug("hash = {}", hash);

        BigInteger num2 = new BigInteger(hash, Character.MAX_RADIX);
        if (num2.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE)) {
            num2 = num2.subtract(BigInteger.ONE).negate();
        }
        num2 = num2.divide(BigInteger.valueOf(2));

        assertEquals(numOrig, num2);

        byte[] data2 = num2.toByteArray();//TODO pad array

        assertEquals(16, data2.length);
        Assert.assertArrayEquals(data, data2);

        long l1 = new BigInteger(ArrayUtils.subarray(data2, 0, 8)).longValueExact();
        long l2 = new BigInteger(ArrayUtils.subarray(data2, 8, 16)).longValueExact();

        UUID out = new UUID(l1, l2);
        assertEquals(in, out);
    }

    @Test
    public void testUuidToHash2() {
        UUID in = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

        String hash = encodeUuid(in);

        logger.debug("hash = {}", hash);

        UUID out = decodeUuid(hash);

        assertEquals(in, out);
    }

    @Test
    public void testEncode1() {
        String source = "something something $$#@%^&&*";
        String encoded = encodeString(source);
        assertTrue(!equal(source, encoded));
        String decoded = decodeString(encoded);
        assertEquals(source, decoded);
    }

    @Test
    public void testEncode2() {
        String source = "";
        String encoded = encodeString(source);
        assertTrue(!equal(source, encoded));
        String decoded = decodeString(encoded);
        assertEquals(source, decoded);
    }

    @Test
    public void testEncode3() {
        String source = " ";
        String encoded = encodeString(source);
        assertTrue(!equal(source, encoded));
        String decoded = decodeString(encoded);
        assertEquals(source, decoded);
    }

    @Test
    public void testToIntHash() {
        assertEquals(813440715, toIntHash(""));
        assertEquals(447813662, toIntHash("asd"));
        assertEquals(1016662447, toIntHash("123"));
        assertEquals(962733807, toIntHash("f2b3h58ifn72i84w3"));
        assertEquals(1905174043, toIntHash("&^%*^%*BV&^B%*&"));
        assertEquals(201408430, toIntHash("qw3vt5w3v"));
        assertEquals(623925595, toIntHash("wve5yrytrbtgbfthbfthbdrthbdrbtyhetrbyrty"));
        assertEquals(190915613, toIntHash("."));
    }

    @Test
    public void testCompact() {
        assertEquals(null, compact(null, 10));
        assertEquals("", compact("", 10));
        assertEquals("test", compact("test", 10));
        assertEquals(10, compact("onetwothreefourfive", 10).length());
        assertEquals("on_wus6_ve", compact("onetwothreefourfive", 10));
        assertEquals("onetwothreefourfive", compact("onetwothreefourfive", 50));
    }
}
