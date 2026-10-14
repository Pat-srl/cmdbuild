package org.cmdbuild.utils.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testKeyId() throws Exception {
        logger.info("testKeyId BEGIN");
        assertEquals("e47f9eda18a855f9", Cm3EasyCryptoUtils.defaultUtils().getKeyId());
        logger.info("testKeyId END");
    }

    @Test
    public void testCryptoUtils() throws Exception {
        logger.info("testCryptoUtils BEGIN");
        String value = "this is a param value to be encrypted";
        org.cmdbuild.utils.cli.Main.setCliHome(new File("/tmp/test_dummy.war"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream systemOut = System.out, printStream = new PrintStream(out);
        System.setOut(printStream);
        try {
            org.cmdbuild.utils.cli.Main.main(new String[]{"crypto", "encryptLegacy", value});
        } finally {
            System.setOut(systemOut);
        }
        assertEquals("SkGIhtI7SJDYayN18xQ0gbYIsb4WDJuA5P2Y68Nt4t9POPJElrNEXw==\n", out.toString());
        logger.info("testCryptoUtils END");
    }

}
