/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.stream.IntStream;
import org.cmdbuild.utils.crypto.MagicUtils;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void magicTestStr() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper("asde", 3, 7, 11, 14);
        String one = "thisissctringone",
                enc = helper.embedMagic(one),
                two = helper.stripMagic(enc);

        assertTrue(helper.hasMagic(enc));
        assertFalse(helper.hasMagic(one));

        assertNotEquals(one, enc);
        assertEquals("thiasisssctdriengone", enc);
        assertEquals(one, two);
    }

    @Test
    public void magicTestBytes1() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        byte[] one = "thisissctringone".getBytes(StandardCharsets.US_ASCII),
                enc = helper.embedMagic(one),
                two = helper.stripMagic(enc);

        assertTrue(helper.hasMagic(enc));
        assertFalse(helper.hasMagic(one));

        assertThat(enc, not(equalTo(one)));
        assertThat(enc, equalTo(new byte[]{116, 104, 105, 12, 115, 105, 115, 34, 115, 99, 116, -12, 114, 105, -1, 110, 103, 111, 110, 101}));
        assertThat(two, equalTo(one));
    }

    @Test
    public void magicTestBytes2() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        String string = "aasasdasdasdqweqweqwewq1435234t25tf45t3f45tv435tv45tv45";
        IntStream.rangeClosed(0, string.length()).mapToObj(i -> string.substring(0, i).getBytes(UTF_8)).forEach(data -> {
            byte[] withMagic = helper.embedMagic(data), two = helper.stripMagic(withMagic);

            assertEquals(data.length + 4, withMagic.length);
            assertEquals(data.length, two.length);

            assertFalse(helper.hasMagic(data));
            assertTrue(helper.hasMagic(withMagic));

            assertThat(withMagic, not(equalTo(data)));
            assertThat(two, equalTo(data));
        });
    }

    @Test
    public void magicTestBytes3() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        byte[] one = new byte[]{}, enc = helper.embedMagic(one), two = helper.stripMagic(enc);

        assertFalse(helper.hasMagic(one));
        assertTrue(helper.hasMagic(enc));

        assertThat(two, equalTo(one));
        assertThat(enc, equalTo(new byte[]{12, 34, -12, -1}));
    }

    @Test
    public void magicTestBytes4() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        byte[] one = new byte[]{0}, enc = helper.embedMagic(one), two = helper.stripMagic(enc);

        assertFalse(helper.hasMagic(one));
        assertTrue(helper.hasMagic(enc));

        assertThat(two, equalTo(one));
        assertThat(enc, equalTo(new byte[]{0, 12, 34, -12, -1}));
    }

    @Test
    public void magicTestBytes5() {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        byte[] one = new byte[]{0, 0, 0}, enc = helper.embedMagic(one), two = helper.stripMagic(enc);

        assertFalse(helper.hasMagic(one));
        assertTrue(helper.hasMagic(enc));

        assertThat(two, equalTo(one));
        assertThat(enc, equalTo(new byte[]{0, 0, 0, 12, 34, -12, -1}));
    }

    @Test
    public void magicTestStream() throws IOException {
        MagicUtils.MagicUtilsHelper helper = MagicUtils.helper(new byte[]{12, 34, -12, -1}, 3, 7, 11, 14);
        String string = "aasasdasdasdqweqweqwewq1435234t25tf45t3f45tv435tv45tv45";
        IntStream.rangeClosed(0, string.length()).mapToObj(i -> string.substring(0, i).getBytes(UTF_8)).forEach(rethrowConsumer(data -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (OutputStream wrapped = helper.addMagicOnClose(out)) {
                wrapped.write(data);
            }
            byte[] withMagic = out.toByteArray(), two = helper.stripMagic(withMagic);

            assertEquals(data.length + 4, withMagic.length);
            assertEquals(data.length, two.length);

            assertFalse(helper.hasMagic(data));
            assertTrue(helper.hasMagic(withMagic));

            assertThat(withMagic, not(equalTo(data)));
            assertThat(two, equalTo(data));
        }));
    }
}
