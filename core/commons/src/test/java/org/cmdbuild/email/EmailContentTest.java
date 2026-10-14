/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import java.nio.charset.StandardCharsets;
import static org.cmdbuild.email.EmailContentUtils.getContentTypeOrAutoDetect;
import static org.cmdbuild.email.EmailContentUtils.htmlToPlainText;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.normalizeNewlines;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailContentTest {

    @Test
    public void testEmailContentParsing() {
        String rawContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_raw_payload.txt")),
                htmlContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_html_content.txt")),
                plaintextContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_plaintext_content.txt")),
                contentType = "multipart/MIXED; boundary=\"=_cbd9373c11e1eb9e792e2db543e9f2f8\"";

        Email email = mock(Email.class);
        when(email.getContent()).thenReturn("");
        when(email.getContentType()).thenReturn("x");
        when(email.getMultipartContent()).thenReturn(rawContent.getBytes(StandardCharsets.UTF_8));
        when(email.getMultipartContentType()).thenReturn(contentType);
        when(email.hasMultipartContent()).thenReturn(true);
        when(email.hasAnyContent()).thenReturn(true);

        assertEquals(plaintextContent, EmailContentUtils.getContentPlaintext(email));
        assertEquals(htmlContent, EmailContentUtils.getContentHtml(email));
    }

    @Test
    public void testEmailContentParsing2() {
        String rawContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_2_raw_payload.txt")),
                plaintextContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_2_plaintext_content.txt")),
                htmlContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_2_html_content.txt")),
                contentType = "multipart/mixed; boundary=_008_1e3d4432fb7d443cbed94cc8a06c15f3Mbxld01ldge_";

        Email email = mock(Email.class);
        when(email.getContent()).thenReturn("");
        when(email.getContentType()).thenReturn("x");
        when(email.getMultipartContent()).thenReturn(rawContent.getBytes(StandardCharsets.UTF_8));
        when(email.getMultipartContentType()).thenReturn(contentType);
        when(email.hasMultipartContent()).thenReturn(true);
        when(email.hasAnyContent()).thenReturn(true);

        assertEquals(plaintextContent, EmailContentUtils.getContentPlaintext(email));
        assertEquals(htmlContent, EmailContentUtils.getContentHtml(email));
    }

    @Test
    public void testEmailContentParsing3() {
        String rawContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_3_raw_payload.txt")),
                plaintextContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_3_plaintext_content.txt")),
                htmlContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_3_html_content.txt")),
                contentType = "multipart/alternative; boundary=_000_147a02f40ee7428f92f508a6db63752aMbxld01ldge_";

        Email email = mock(Email.class);
        when(email.getContent()).thenReturn("");
        when(email.getContentType()).thenReturn("x");
        when(email.getMultipartContent()).thenReturn(rawContent.getBytes(StandardCharsets.UTF_8));
        when(email.getMultipartContentType()).thenReturn(contentType);
        when(email.hasMultipartContent()).thenReturn(true);
        when(email.hasAnyContent()).thenReturn(true);

        assertEquals(plaintextContent, normalizeNewlines(EmailContentUtils.getContentPlaintext(email)));
        assertEquals(htmlContent, EmailContentUtils.getContentHtml(email));
    }

    @Test
    public void testEmailContentParsing4() {
        String plaintextContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_3_plaintext_content.txt"));

        Email email = mock(Email.class);
        when(email.getContent()).thenReturn(plaintextContent);
        when(email.getContentType()).thenReturn("text/plain");
        when(email.hasContent()).thenReturn(true);
        when(email.hasAnyContent()).thenReturn(true);

        assertEquals(plaintextContent, normalizeNewlines(EmailContentUtils.getContentPlaintext(email)));
    }

    @Test
    public void testEmailContentParsing5() {
        String htmlContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_3_html_content.txt"));

        Email email = mock(Email.class);
        when(email.getContent()).thenReturn(htmlContent);
        when(email.getContentType()).thenReturn("text/html");
        when(email.hasContent()).thenReturn(true);
        when(email.hasAnyContent()).thenReturn(true);

        assertEquals(htmlContent, EmailContentUtils.getContentHtmlOrRawPlaintext(email));
    }

    @Test
    public void testEmailContentParsing6() {
        String content = "p><span style=\"color: #222222; font-family: arial; font-size: small;\">La presente per segnalare l'avvenuta ricezione della richiesta numero 3160</span><span style=\"color: #222222; font-family: arial; font-size: small;\">.</span></p>\n"
                + "<div><span style=\"color: #222222; font-family: arial; font-size: small;\">Azienda: <br /></span></div>";

        assertEquals("text/html", getContentTypeOrAutoDetect(null, content));
        assertEquals("text/html", getContentTypeOrAutoDetect("application/octet-stream", content));
    }

    @Test
    public void testEmailContentParsing7() {
        String content = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/email_7_content.txt"));

        assertEquals("text/html", getContentTypeOrAutoDetect(null, content));
        assertEquals("text/html", getContentTypeOrAutoDetect("application/octet-stream", content));
    }

    @Test
    public void testHtmlToPlaintext1() {
        assertEquals("", htmlToPlainText(""));
        assertEquals("asd", htmlToPlainText("asd"));
        assertEquals("asd dsa", htmlToPlainText("<b>asd</b> dsa"));
        assertEquals("asd\ndsa", htmlToPlainText("<b>asd</b><br />dsa"));
        assertEquals("as\nd\ndsa", htmlToPlainText("<b>as\nd</b><br>dsa"));
        assertEquals("asd\ndsa", htmlToPlainText("<b>asd</b><!-- something --><br>ds<!-- \n\nelse \n-->a"));
        assertEquals("asd", htmlToPlainText("\t\t   asd"));
        assertEquals("asd", htmlToPlainText("\n\t\t   asd"));
        assertEquals("a\nasd", htmlToPlainText("\na\n\t\t   asd"));
        assertEquals("a\n\nasd", htmlToPlainText("\na\n\t\t  \n\n \t\n\t asd"));
    }

    @Test
    public void testHtmlToPlaintext2() {
        assertEquals("a__asd", "a\n\n\n\n\nasd".replaceAll("\n\n+", "__"));
        assertEquals("a\n\nasd", "a\n\n\n\n\nasd".replaceAll("\n\n+", "\n\n"));
        assertEquals("a\n\nasd", htmlToPlainText("\na\n\r\n\r\r\n\n\nasd"));
    }

    @Test
    public void testHtmlToPlaintext3() {
        assertEquals(checkNotBlank(readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_4_content_plaintext.txt"))),
                htmlToPlainText(readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_4_content_html.txt"))));
    }
}
