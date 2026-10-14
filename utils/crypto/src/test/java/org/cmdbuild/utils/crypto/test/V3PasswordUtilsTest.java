package org.cmdbuild.utils.crypto.test;

import static com.google.common.base.Strings.emptyToNull;
import org.cmdbuild.utils.crypto.Cm3PasswordUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V3PasswordUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test1() {
        String hash1 = Cm3PasswordUtils.hash("something"),
                hash2 = Cm3PasswordUtils.hash("something"),
                hash3 = Cm3PasswordUtils.hash("else");

        logger.debug("hash 1 = {} {}", hash1, hash1.length());
        logger.debug("hash 2 = {} {}", hash2, hash2.length());
        logger.debug("hash 3 = {} {}", hash3, hash3.length());

        assertNotNull(emptyToNull(hash1));
        assertNotNull(emptyToNull(hash2));
        assertNotNull(emptyToNull(hash3));

        assertNotEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);

        assertTrue(hash1.length() > 30 && hash1.length() < 120);
        assertTrue(hash2.length() > 30 && hash2.length() < 120);
        assertTrue(hash3.length() > 30 && hash3.length() < 120);

        assertTrue(Cm3PasswordUtils.isEncrypted(hash1));
        assertTrue(Cm3PasswordUtils.isEncrypted(hash2));
        assertTrue(Cm3PasswordUtils.isEncrypted(hash3));

        assertTrue(Cm3PasswordUtils.isValid("something", hash1));
        assertTrue(Cm3PasswordUtils.isValid("something", hash2));
        assertTrue(Cm3PasswordUtils.isValid("else", hash3));

        assertFalse(Cm3PasswordUtils.isValid("asd", hash1));
        assertFalse(Cm3PasswordUtils.isValid("asd", hash2));
        assertFalse(Cm3PasswordUtils.isValid("asd", hash3));
    }

    @Test
    public void test2() {
        String hash1 = "2smkw0ikke823nz9p7jm0ht33jrl98gs6axoh6y1lwx01gn1oae1i2wll8qlc6jebb9glw4ookjitbggi9c6zvhs75",
                hash2 = "2uktro0usv649pefm142si4htuin1sl38yot6s4mvgc7whvs4x1cut2wgj1koh90brvhnyewi9t2beopr47ac8f94x",
                hash3 = "2rh41783dhdm2w7uz865b2gbb4fgskemtwtz88xrrewg58lcbqckwcfm1hnrnpcubsu4sdbbpzvbceblc4b3kxoidd";

        logger.debug("hash 1 = {} {}", hash1, hash1.length());
        logger.debug("hash 2 = {} {}", hash2, hash2.length());
        logger.debug("hash 3 = {} {}", hash3, hash3.length());

        assertNotNull(emptyToNull(hash1));
        assertNotNull(emptyToNull(hash2));
        assertNotNull(emptyToNull(hash3));

        assertNotEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);

        assertTrue(hash1.length() > 30 && hash1.length() < 120);
        assertTrue(hash2.length() > 30 && hash2.length() < 120);
        assertTrue(hash3.length() > 30 && hash3.length() < 120);

        assertTrue(Cm3PasswordUtils.isEncrypted(hash1));
        assertTrue(Cm3PasswordUtils.isEncrypted(hash2));
        assertTrue(Cm3PasswordUtils.isEncrypted(hash3));

        assertTrue(Cm3PasswordUtils.isValid("something", hash1));
        assertTrue(Cm3PasswordUtils.isValid("something", hash2));
        assertTrue(Cm3PasswordUtils.isValid("else", hash3));

        assertFalse(Cm3PasswordUtils.isValid("asd", hash1));
        assertFalse(Cm3PasswordUtils.isValid("asd", hash2));
        assertFalse(Cm3PasswordUtils.isValid("asd", hash3));
    }
}
