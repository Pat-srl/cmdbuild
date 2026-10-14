/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import static org.cmdbuild.email.template.EmailUtils.handleEmailSignatureForTemplate;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EmailSignatureTest {

    @Test
    public void testEmailSignature() {
        assertEquals("my template", handleEmailSignatureForTemplate("my template", null));
        assertEquals("my template\n\n<div data-type=\"signature\">sign</div>", handleEmailSignatureForTemplate("my template", "sign"));
        assertEquals("my template <div data-type=\"signature\">sign</div> yep", handleEmailSignatureForTemplate("my template <div data-type=\"signature\"></div> yep", "sign"));
        assertEquals("my template <div data-type=\"signature\">sign</div> yep", handleEmailSignatureForTemplate("my template <div   data-type=\"signature\">\n\t  </div> yep", "sign"));
        assertEquals("my template <div data-type=\"signature\">yuppy</div> yep", handleEmailSignatureForTemplate("my template <div data-type=\"signature\">yuppy</div> yep", "sign"));
        assertEquals("my template <DIV  data-type = \"signature\">yuppy</DIV> yep", handleEmailSignatureForTemplate("my template <DIV  data-type = \"signature\">yuppy</DIV> yep", "sign"));
    }

}
