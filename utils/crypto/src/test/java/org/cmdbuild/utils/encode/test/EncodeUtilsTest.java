/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode.test;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.io.ByteStreams.toByteArray;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.ldecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.ldecodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.lencodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.lencodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testEncodeUtils1() throws DecoderException {
        assertEquals("", decodeString(encodeString("")));
        assertEquals("a", decodeString(encodeString("a")));
        assertEquals("ab", decodeString(encodeString("ab")));
        assertEquals("abc", decodeString(encodeString("abc")));
        assertEquals("abcd", decodeString(encodeString("abcd")));
        assertEquals("abcde", decodeString(encodeString("abcde")));
        assertEquals("abcdef", decodeString(encodeString("abcdef")));
        assertEquals("abcdefg", decodeString(encodeString("abcdefg")));
        assertEquals("abcdefgh", decodeString(encodeString("abcdefgh")));
        assertEquals("abcdefghi", decodeString(encodeString("abcdefghi")));
        assertEquals("abcdefghij", decodeString(encodeString("abcdefghij")));
        assertEquals("abcdefghijk", decodeString(encodeString("abcdefghijk")));
        assertEquals("abcdefghijkl", decodeString(encodeString("abcdefghijkl")));

        assertArrayEquals(Hex.decodeHex("00"), decodeBytes(encodeBytes(Hex.decodeHex("00"))));
        assertArrayEquals(Hex.decodeHex("0000"), decodeBytes(encodeBytes(Hex.decodeHex("0000"))));
        assertArrayEquals(Hex.decodeHex("000000"), decodeBytes(encodeBytes(Hex.decodeHex("000000"))));
        assertArrayEquals(Hex.decodeHex("FF"), decodeBytes(encodeBytes(Hex.decodeHex("FF"))));
    }

    @Test
    public void testXEncodeUtils1() throws DecoderException {
        assertEquals("", xencodeString(""));
        assertEquals("", xdecodeString(xencodeString("")));
        assertEquals("61", xencodeString("a"));
        assertEquals("a", xdecodeString(xencodeString("a")));
        assertEquals("6162", xencodeString("ab"));
        assertEquals("ab", xdecodeString(xencodeString("ab")));
        assertEquals("616263", xencodeString("abc"));
        assertEquals("abc", xdecodeString(xencodeString("abc")));
        assertEquals("61626364x", xencodeString("abcd"));
        assertEquals("abcd", xdecodeString(xencodeString("abcd")));
        assertEquals("6hm26j6k", xencodeString("abcde"));
        assertEquals("abcde", xdecodeString(xencodeString("abcde")));
        assertEquals("6hm26j6k66", xencodeString("abcdef"));
        assertEquals("abcdef", xdecodeString(xencodeString("abcdef")));
        assertEquals("6hm26j6k6667", xencodeString("abcdefg"));
        assertEquals("abcdefg", xdecodeString(xencodeString("abcdefg")));
        assertEquals("6hm26j6k666768", xencodeString("abcdefgh"));
        assertEquals("abcdefgh", xdecodeString(xencodeString("abcdefgh")));
        assertEquals("6hm26j6k66676869x", xencodeString("abcdefghi"));
        assertEquals("abcdefghi", xdecodeString(xencodeString("abcdefghi")));
        assertEquals("6hm26j6k6mm7m8m9", xencodeString("abcdefghij"));
        assertEquals("abcdefghij", xdecodeString(xencodeString("abcdefghij")));
        assertEquals("6hm26j6k6mm7m8m96b", xencodeString("abcdefghijk"));
        assertEquals("abcdefghijk", xdecodeString(xencodeString("abcdefghijk")));
        assertEquals("6hm26j6k6mm7m8m96b6c", xencodeString("abcdefghijkl"));
        assertEquals("abcdefghijkl", xdecodeString(xencodeString("abcdefghijkl")));

        assertArrayEquals(Hex.decodeHex("00"), xdecodeBytes(xencodeBytes(Hex.decodeHex("00"))));
        assertArrayEquals(Hex.decodeHex("0000"), xdecodeBytes(xencodeBytes(Hex.decodeHex("0000"))));
        assertArrayEquals(Hex.decodeHex("000000"), xdecodeBytes(xencodeBytes(Hex.decodeHex("000000"))));
        assertArrayEquals(Hex.decodeHex("FF"), xdecodeBytes(xencodeBytes(Hex.decodeHex("FF"))));
    }

    @Test
    public void testLencodeUtils1() {
        String value = lencodeString("");
        assertEquals("0", value);
        assertEquals("", ldecodeString(value));
    }

    @Test
    public void testDecode() throws DecoderException {
        assertArrayEquals(Hex.decodeHex("000000"), decodeBytes("001"));
        assertArrayEquals(Hex.decodeHex("00"), decodeBytes("1"));
        assertArrayEquals(Hex.decodeHex("000001"), decodeBytes("003"));
        assertArrayEquals(Hex.decodeHex("00FF01"), decodeBytes("0e8"));
        assertArrayEquals(Hex.decodeHex("000000"), decodeBytes("001"));
        assertArrayEquals(Hex.decodeHex("000000"), decodeBytes("001"));
    }

    @Test
    public void testLdecode() throws DecoderException {
        assertArrayEquals(Hex.decodeHex("00"), ldecodeBytes(lencodeBytes(Hex.decodeHex("00"))));
        assertArrayEquals(Hex.decodeHex("0000"), ldecodeBytes(lencodeBytes(Hex.decodeHex("0000"))));
        assertArrayEquals(Hex.decodeHex("000000"), ldecodeBytes(lencodeBytes(Hex.decodeHex("000000"))));
        assertArrayEquals(Hex.decodeHex("FF"), ldecodeBytes(lencodeBytes(Hex.decodeHex("FF"))));
    }

    @Test
    public void testXencodeEmptyBytes1() throws DecoderException {
        byte[] data = Hex.decodeHex("000000");
        String encoded = xencodeBytes(data);
        assertEquals("000000", encoded);
        byte[] decoded = xdecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testXencodeEmptyBytes2() throws DecoderException {
        byte[] data = Hex.decodeHex("00");
        String encoded = xencodeBytes(data);
        assertEquals("00", encoded);
        byte[] decoded = xdecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testXencodeEmptyBytes3() throws DecoderException {
        byte[] data = Hex.decodeHex("000001");
        String encoded = xencodeBytes(data);
        assertEquals("000001", encoded);
        byte[] decoded = xdecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testXencodeEmptyBytes4() throws DecoderException {
        byte[] data = Hex.decodeHex("00FF01");
        String encoded = xencodeBytes(data);
        assertEquals("00ff01", encoded);
        byte[] decoded = xdecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testLencodeEmptyBytes1() throws DecoderException {
        byte[] data = Hex.decodeHex("000000");
        String encoded = lencodeBytes(data);
        assertEquals("001", encoded);
        byte[] decoded = ldecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testLencodeEmptyBytes2() throws DecoderException {
        byte[] data = Hex.decodeHex("00");
        String encoded = lencodeBytes(data);
        assertEquals("1", encoded);
        byte[] decoded = ldecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testLencodeEmptyBytes3() throws DecoderException {
        byte[] data = Hex.decodeHex("000001");
        String encoded = lencodeBytes(data);
        assertEquals("003", encoded);
        byte[] decoded = ldecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testLencodeEmptyBytes4() throws DecoderException {
        byte[] data = Hex.decodeHex("00FF01");
        String encoded = lencodeBytes(data);
        assertEquals("0e8", encoded);
        byte[] decoded = ldecodeBytes(encoded);
        assertEquals(data.length, decoded.length);
        assertArrayEquals(data, decoded);
    }

    @Test
    public void testLencodeUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!",
                packed = lencodeString(value);
        assertEquals("16d5lknizb87tqaipx4yywhutdlmjtdtmm1zcv81lx6mryf42270plwi6oivq15hzrhuv3iywvea43zmlqj877", packed);
        assertEquals(value, ldecodeString(packed));
    }

    @Test
    public void testXEncodeUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!",
                packed = xencodeString(value);
        assertEquals("6mmennnm77jl7m2035nm6n203lm8mnjp65i025lu40i4255u2m2girip2bjqntja5t5rjbiv3r0aga0d45kc4c4v", packed);
        assertEquals(value, xdecodeString(packed));
    }

    @Test
    public void testEncodeUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!", packed = encodeString(value);
        assertEquals(value, decodeString(packed));
    }

    @Test
    public void testEncodeUtils3() throws IOException {
        byte[] data = new byte[0];
        String packed = encodeBytes(data);
        assertEquals("0", packed);
        assertTrue(Arrays.equals(data, decodeBytes(packed)));
    }

    @Test
    public void testEncodeUtils4() throws IOException {
        byte[] data = new byte[]{0};
        String packed = encodeBytes(data);
        assertEquals("1", packed);
        assertTrue(Arrays.equals(data, decodeBytes(packed)));
    }

    @Test
    public void testEncodeUtils5() throws IOException {
        Set<String> values = set();
        for (int b = Byte.MIN_VALUE; b <= Byte.MAX_VALUE; b++) {
            byte[] data = new byte[]{(byte) b};
            String packed = encodeBytes(data);
            logger.trace("byte array = {} encodes as string = {}", data, packed);
            checkArgument(values.add(packed));
            assertTrue(Arrays.equals(data, decodeBytes(packed)));
        }
    }

    @Test
    public void testEncodeUtils7() throws IOException {
        byte[] data = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/encode/test/data.raw"));
        String packed = encodeBytes(data);
        assertEquals("2ktr2h8jzexu9k1oogjhbtrqxsmq2a6lzpbaqsvo6uvyzm8s0yvjk6wj6ejeqxzh4x0020q42id0o5g1zredefmr82ybyi76rocrf7xu8psawvfww5juitbyu9i3llx6yetkws2ir473bvpz2fsht6lxkmjn3v36ev8lxqzy20gdu9tvcpin6l85or39k1x7nrnav7evvygjycbs6jmo3uwr13lz9bcs1enpmhriberojup73wk1i6penie824p1zxtisp4qy1ktsyf7i6ab3179uc3qyopt7mm1yrngt5v6ekgorpss9e36rmmnan7559zaf5g2t81kntjc06f05puep6cpdkp1wgwovda8chmt9abmxjcvctmxrxkvcu3ksi5k53w7njiy9wcebwelk86eussvmxf6l30ouzjlw8kv8yeptqbubtzzbhphyapuegkrdjezem564m1ry4reqvfygbu3lwe54urmpuiddk75fkxlfrdtt7369edsxanxy4941axignmpo0jp40f9jrhwpi0c4euy975wj46qsl23und9lteevwdpvjwazb6j611u400cv5y9u9flg2i8e8ezjnnph2bccy34fykpc2e2h0jrkhr6z1g4phtocj1yzzw0crm9zcuber6zut1413lnvgee07i9qx8pn4va9usvivhyvhbw4bl1wmcrj8usqnqkz1bzj4oms0xajw0269087cqkacym3ofywuuwnxuji7bzv8jxs7ybbkywg9bswc87xpdy8ze2x8okukefxk8r92otoyhfivdg9mopcgfkoxzx02sbyjrggnx007owvw89bwl5yd7tgvgfttu6el30zvkbmzd1ygw40z4irafjkbk5rqoy2pegt62lzkk0pqlowifusql29gvloxdrfqi17xtd4xhg2edlljde7sekwh6m45sjlv8tzvtthabgd4s89z39fs57d52vg4zxkzk9uvganr4laafwslecft042tbqukkv9ajr2c9haf8rkh7n2etcv21e1si52z5gubmadri8qpieb7t4vtifrwjm4o07zeqqb7mrggz1f93bbrg5in0ji4nfcc250vpmmt9iobjg6ykq50j1wj5foer4uafhpe0n96lid2vf5dpt00jkekdljup1ek0o1l7ahbqtudovfzk0qlres93zrthz6u7okpopb0h80pjpt1prbb5kxzo3vawdjs4ecf30rvcwppg13mtky7mk526d9z44y1ywv2o411inor3ytbdbvwnlphohygfnqhrrqh8fvut2zdp6potuvfg9r7mlpn5bsfamh2q5hcpgjttjos5nxvkf3qcgmon1t0tmerh6npevscx2p3whko2ik0su2g5eq0z6ik6ymjoy4m42ddsrs1bf94tib8u8u61eu12sbnzfse3h87g9ruxapaui8ideabq6vlqlxbzedj1xxvxipyghz01wu0datixel1slmh8zu2hyhhgdky716g9bowz287suhrom4vwybftphviq9gr3nrzp1258i0y9ix6mxdjosf9o6t3bdx1nrvjs4a9rp1bev", packed);
        assertTrue(Arrays.equals(data, decodeBytes(packed)));
    }

}
