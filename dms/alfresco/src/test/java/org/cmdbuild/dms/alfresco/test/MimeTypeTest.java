/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.alfresco.test;

import jakarta.activation.DataHandler;
import org.apache.http.entity.ContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class MimeTypeTest {

    @Test
    public void mimeTypeTest1() {
        DataHandler data = newDataHandler("-----BEGIN CERTIFICATE-----".getBytes());

        assertEquals("application/x-x509-cert; format=pem", data.getContentType());

        assertEquals("application/x-x509-cert", ContentType.parse(data.getContentType()).getMimeType());
    }
}
