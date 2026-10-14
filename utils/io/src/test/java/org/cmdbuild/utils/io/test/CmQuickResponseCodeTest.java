/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.io.test;

import java.io.IOException;
import static org.cmdbuild.utils.io.CmQuickResponseCodeUtils.generateQuickResponseCode;
import static org.cmdbuild.utils.io.CmQuickResponseCodeUtils.readQuickResponseCode;
import static org.cmdbuild.utils.io.CmQuickResponseCodeUtils.readQuickResponseCodeCropped;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class CmQuickResponseCodeTest {

    @Test
    public void testQuickResponseCodeEquals() throws IOException {
        byte[] qrCodeFromText = generateQuickResponseCode("thisisaqrcode", 128, 128);
        assertEquals("thisisaqrcode", readQuickResponseCode(qrCodeFromText));
    }

    @Test
    public void testQuickResponseCodeCropped() throws IOException {
        byte[] byteImage = getClass().getResourceAsStream("/org/cmdbuild/utils/qrcode/test/crop_qrcode.jpeg").readAllBytes();
        assertEquals("thisisaqrcode", readQuickResponseCodeCropped(byteImage, 2200, 780, 128, 128));
    }
}
