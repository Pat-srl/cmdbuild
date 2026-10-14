/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.job.MapperConfigImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.embedEmailInlineAttachmentsAsBase64;
import static org.cmdbuild.email.template.EmailUtils.formatEmailHeaderToken;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.template.EmailUtils.parseEmailReferencesHeader;
import static org.cmdbuild.email.template.EmailUtils.processMapperExpr;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.normalizeNewlines;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.email.utils.EmailMtaUtils.parseEmail;

public class EmailUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testEmailTokenParsing1() {
        String token = "<299378267.0.1552566949093@phil>",
                parsed = parseEmailHeaderToken(token);
        assertEquals("299378267.0.1552566949093@phil", parsed);
        assertEquals(token, formatEmailHeaderToken(parsed));
    }

    @Test
    public void testEmailTokenParsing2() {
        String token = "  <299378267.0.1552566949093@phil> ",
                parsed = parseEmailHeaderToken(token);
        assertEquals("299378267.0.1552566949093@phil", parsed);
    }

    @Test
    public void testParseEmailReferencesHeader1() {
        List<String> list = parseEmailReferencesHeader("<299378267.0.1552566949093@phil> <299378267.0.155256694asd3@phil> <299378267.0.fdsd@phil>");
        assertEquals(list("299378267.0.1552566949093@phil", "299378267.0.155256694asd3@phil", "299378267.0.fdsd@phil"), list);
    }

    @Test
    public void testParseEmailReferencesHeader2() {
        List<String> list = parseEmailReferencesHeader("<299378267.0.1552566949093@phil>\n\r<299378267.0.155256694asd3@phil>\n\r<299378267.0.fdsd@phil>");
        assertEquals(list("299378267.0.1552566949093@phil", "299378267.0.155256694asd3@phil", "299378267.0.fdsd@phil"), list);
    }

    @Test
    public void testParseEmailReferencesHeader3() {
        List<String> list = parseEmailReferencesHeader("<299378267.0.1552566949093@phil>,<299378267.0.155256694asd3@phil>,<299378267.0.fdsd@phil>");
        assertEquals(list("299378267.0.1552566949093@phil", "299378267.0.155256694asd3@phil", "299378267.0.fdsd@phil"), list);
    }

    @Test
    public void testProcessMapperExpr1() {
        assertEquals("else", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn <key>something</key> <value>else</value> dcawd", "something"));
    }

    @Test
    public void testPrsocessMapperExpr2() {
        assertEquals("", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn <key>something</key> <value>else</value> dcawd", "asd"));
    }

    @Test
    public void testPrsocessMapperExpr3() {
        assertEquals("qwe", processMapperExpr(new MapperConfigImpl(), "dg v45itng4ng5i3gn\n <key>something</key> <value>else</value> <key>asd</key> \n<value>qwe</value> dcawd", "asd"));
    }

    @Test
    public void testMultipartProcessing() throws MessagingException {
        Email email = EmailImpl.builder()
                .withContentType("multipart/alternative; boundary=\"--==_mimepart_5da47564b93d2_1cc63faa3b5ae2fc11725b\"; charset=UTF-8")
                .withContent(readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_1_content_raw.txt"))).build();

        String expectedPlaintext = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_1_content_plaintext.txt")),
                expectedHtml = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_1_content_html.txt"));

        assertEquals(expectedPlaintext, normalizeNewlines(email.getContentPlaintext()));
        assertEquals(expectedHtml, normalizeNewlines(email.getContentHtml()));

    }

    @Test
    public void testInlineImagesProcessing1() throws MessagingException {
        MimeMessage message = buildMimeMessage("multipart/mixed;boundary=\"_004_AM6PR04MB55104AAC81CD7466F29FFAADF22E0AM6PR04MB5510eurp_\"", "/org/cmdbuild/test/core/email/email_with_embedded_images.raw");
        Email email = parseEmail(message);
        String contentHtml = email.getContentHtml();

        logger.info("email content 1 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:292d27a4-05fd-42b6-af68-a0ee7e4c1798\"")));
        assertThat(contentHtml, not(containsString(" src=\"data:image/")));
        assertThat(contentHtml, containsString(" src=\"cid:cm_qe7n2i7rshxsytaxhwipurzj\""));

        contentHtml = embedEmailInlineAttachmentsAsBase64(contentHtml, email.getAttachments());

        logger.info("email content 2 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:292d27a4-05fd-42b6-af68-a0ee7e4c1798\"")));
        assertThat(contentHtml, not(containsString(" src=\"cid:cm_qe7n2i7rshxsytaxhwipurzj\"")));
        assertThat(contentHtml, containsString(" src=\"data:image/png;%20name=%22Screenshot%20from%202019-07-08%2010-41-12.png%22;base64,iVBORw0KGgoAAAANSUhEUgAA"));
    }

    @Test
    public void testInlineImagesProcessing2() throws MessagingException {
        MimeMessage message = buildMimeMessage("multipart/mixed;boundary=\"_004_AM6PR04MB55104AAC81CD7466F29FFAADF22E0AM6PR04MB5510eurp_\"", "/org/cmdbuild/test/core/email/email_with_embedded_images.raw");
        Email email = parseEmail(message);
        String contentHtml = email.getContentHtml();

        logger.info("email content 1 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:292d27a4-05fd-42b6-af68-a0ee7e4c1798\"")));
        assertThat(contentHtml, not(containsString(" src=\"data:image/")));
        assertThat(contentHtml, containsString(" src=\"cid:cm_"));

        contentHtml = embedEmailInlineAttachmentsAsBase64(contentHtml, email.getAttachments());

        logger.info("email content 2 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:292d27a4-05fd-42b6-af68-a0ee7e4c1798\"")));
        assertThat(contentHtml, not(containsString(" src=\"cid:cm_")));
        assertThat(contentHtml, containsString(" src=\"data:image/png;%20name=%22Screenshot%20from%202019-07-08%2010-41-12.png%22;base64,iVBORw0KGgoAAAANSUhEUgAA"));
    }

    @Test
    public void testInlineImagesProcessing3() throws MessagingException {
        Email email = parseEmail(readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/test_inline_image.txt")));
        String contentHtml = email.getContentHtml();

        logger.info("email content 1 = \n\n{}\n", contentHtml);

//        assertThat(contentHtml, not(containsString(" src=\"cid:")));//TODO test content        
        assertThat(contentHtml, not(containsString(" src=\"data:image/")));
        assertThat(contentHtml, containsString(" src=\"cid:cm_"));

        contentHtml = embedEmailInlineAttachmentsAsBase64(contentHtml, email.getAttachments());

        logger.info("email content 2 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:")));//TODO test content        
        assertThat(contentHtml, not(containsString(" src=\"cid:cm_")));
        assertThat(contentHtml, containsString(" src=\"data:image")); //TODO test content
    }

    @Test
    public void testInlineImagesProcessing4() throws MessagingException {
        Email email = parseEmail(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/test_inline_image.txt")));
        String contentHtml = email.getContentHtml();

        logger.info("email content 1 = \n\n{}\n", contentHtml);

//        assertThat(contentHtml, not(containsString(" src=\"cid:")));//TODO test content        
        assertThat(contentHtml, not(containsString(" src=\"data:image/")));
        assertThat(contentHtml, containsString(" src=\"cid:cm_"));

        contentHtml = embedEmailInlineAttachmentsAsBase64(contentHtml, email.getAttachments());

        logger.info("email content 2 = \n\n{}\n", contentHtml);

        assertThat(contentHtml, not(containsString(" src=\"cid:")));//TODO test content        
        assertThat(contentHtml, not(containsString(" src=\"cid:cm_")));
        assertThat(contentHtml, containsString(" src=\"data:image")); //TODO test content        
    }

    @Test
    public void testAttachmentProcessing1() throws MessagingException {
        Email email = parseEmail(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_6_raw.txt")));

        logger.info("html content = \n\n{}\n", email.getContentHtml());
        logger.info("plaintext content = \n\n{}\n", email.getContentPlaintext());

        assertThat(email.getContentHtml(), containsString("con la seguente mail segnaliamo"));
        assertThat(email.getContentHtml(), containsString("che si chiama &#34;xxxxxxxxxxx - Master&#34; e che"));
        assertThat(email.getContentPlaintext(), containsString("con la seguente mail segnaliamo"));
        assertThat(email.getContentPlaintext(), containsString("che si chiama \"xxxxxxxxxxx - Master\" e che"));
        assertEquals(0, email.getAttachments().size());
    }

    private MimeMessage buildMimeMessage(String contentType, String resource) throws MessagingException {
        DataHandler dataHandler = newDataHandler(toByteArray(getClass().getResourceAsStream(resource)), contentType);
        assertThat(dataHandler.getContentType(), startsWith("multipart/mixed"));

        MimeMessage message = new MimeMessage((Session) null);
        message.setDataHandler(dataHandler);
        message.setHeader("Content-Type", contentType);
        assertThat(message.getContentType(), startsWith("multipart/mixed"));
        return message;
    }

}
