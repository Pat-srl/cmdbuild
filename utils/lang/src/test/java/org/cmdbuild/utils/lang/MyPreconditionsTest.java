package org.cmdbuild.utils.lang;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class MyPreconditionsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testTrim1() {
        assertEquals("asd", trimAndCheckNotBlank(" asd "));
    }

    @Test
    public void testTrim2() {
        assertEquals("asd", trimAndCheckNotBlank("asd"));
    }

    @Test
    public void testTrim3() {
        assertEquals("asd", trimAndCheckNotBlank("   asd"));
    }

    @Test(expected = NullPointerException.class)
    public void testTrimEx1() {
        trimAndCheckNotBlank("");
    }

    @Test(expected = NullPointerException.class)
    public void testTrimEx2() {
        trimAndCheckNotBlank(" ");
    }

    @Test(expected = NullPointerException.class)
    public void testTrimEx3() {
        trimAndCheckNotBlank("  ");
    }

    @Test(expected = NullPointerException.class)
    public void testTrimEx4() {
        trimAndCheckNotBlank(null);
    }

    @Test
    public void testCheck1() {
        assertEquals(" asd ", checkNotBlank(" asd "));
    }

    @Test
    public void testCheck2() {
        assertEquals("asd", checkNotBlank("asd"));
    }

    @Test
    public void testCheck3() {
        assertEquals("   asd", checkNotBlank("   asd"));
    }

    @Test(expected = NullPointerException.class)
    public void testCheckEx1() {
        checkNotBlank("");
    }

    @Test(expected = NullPointerException.class)
    public void testCheckEx2() {
        checkNotBlank(" ");
    }

    @Test(expected = NullPointerException.class)
    public void testCheckEx3() {
        checkNotBlank("  ");
    }

    @Test(expected = NullPointerException.class)
    public void testCheckEx4() {
        checkNotBlank((String) null);
    }

    @Test(expected = RuntimeException.class)
    public void testCheckEx5() {
        checkNotBlank(new byte[]{});
    }

    @Test(expected = NullPointerException.class)
    public void testCheckEx6() {
        checkNotBlank((byte[]) null);
    }

}
