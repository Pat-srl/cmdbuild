/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.soap.SoapHelper.SOAP_12_NAMESPACE;
import org.cmdbuild.utils.soap.SoapHelper.SoapResponseImpl;
import org.cmdbuild.utils.soap.SoapResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class SoapUtilsTest {

    private final String payload = """
                                   <?xml version="1.0"?>
                                   <soap:Envelope
                                   xmlns:soap="http://www.w3.org/2003/05/soap-envelope/"
                                   soap:encodingStyle="http://www.w3.org/2003/05/soap-encoding">
                                   <soap:Body>
                                     <VerifyResponse xmlns="http://test.com">
                                            <VerifyResult>
                                               <Result>OK</Result>
                                               <Qualification>allow</Qualification>
                                               <Profile>MONO</Profile>
                                               <Message>PROD e CP</Message>
                                            </VerifyResult>
                                         </VerifyResponse>
                                   </soap:Body>
                                   </soap:Envelope>""",
            faultPayload = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"><soap:Body><soap:Fault><soap:Code><soap:Value>soap:Receiver</soap:Value><soap:Subcode><soap:Value xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\">ns1:VersionMismatch</soap:Value></soap:Subcode></soap:Code><soap:Reason><soap:Text xml:lang=\"en\">\"http://www.w3.org/2003/05/soap-envelope/\", the namespace on the \"Envelope\" element, is not a valid SOAP version.</soap:Text></soap:Reason></soap:Fault></soap:Body></soap:Envelope>";

    @Test
    public void testSoapResponseParsing1() {

        SoapResponse soapResponse = new SoapResponseImpl(payload, SOAP_12_NAMESPACE);

        assertEquals(payload, soapResponse.asString());
        assertNotNull(soapResponse.asDocument());

        assertEquals("OK", soapResponse.evalXpath("//*[local-name()='VerifyResponse']/*[local-name()='VerifyResult']/*[local-name()='Result']"));
        assertEquals("allow", soapResponse.evalXpath("//*[local-name()='Qualification']"));
        assertEquals("MONO", soapResponse.evalXpath("/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='VerifyResponse']/*[local-name()='VerifyResult']/*[local-name()='Profile']"));
        assertEquals("MONO", soapResponse.evalXpath("/soap:Envelope/soap:Body/*[local-name()='VerifyResponse']/*[local-name()='VerifyResult']/*[local-name()='Profile']"));

        soapResponse = new SoapResponseImpl(payload, map(SOAP_12_NAMESPACE).with("test", "http://test.com"));

        assertEquals("OK", soapResponse.evalXpath("//test:VerifyResponse/test:VerifyResult/test:Result"));
        assertEquals("allow", soapResponse.evalXpath("//test:Qualification"));
        assertEquals("MONO", soapResponse.evalXpath("/*[local-name()='Envelope']/*[local-name()='Body']/test:VerifyResponse/test:VerifyResult/test:Profile"));
        assertEquals("MONO", soapResponse.evalXpath("/soap:Envelope/soap:Body/test:VerifyResponse/test:VerifyResult/test:Profile"));

        assertEquals("""
                     <soap:Body xmlns:soap="http://www.w3.org/2003/05/soap-envelope/">
                       <VerifyResponse xmlns="http://test.com">
                              <VerifyResult>
                                 <Result>OK</Result>
                                 <Qualification>allow</Qualification>
                                 <Profile>MONO</Profile>
                                 <Message>PROD e CP</Message>
                              </VerifyResult>
                           </VerifyResponse>
                     </soap:Body>""", soapResponse.evalXpathNode("//soap:Body"));

        assertEquals("""
                     <soap:Body xmlns:soap="http://www.w3.org/2003/05/soap-envelope/">
                       <VerifyResponse xmlns="http://test.com">
                              <VerifyResult>
                                 <Result>OK</Result>
                                 <Qualification>allow</Qualification>
                                 <Profile>MONO</Profile>
                                 <Message>PROD e CP</Message>
                              </VerifyResult>
                           </VerifyResponse>
                     </soap:Body>""", soapResponse.getBodyAsString());
    }

    @Test
    public void testSoapResponseParsing2() {
        SoapResponse soapResponse = new SoapResponseImpl(faultPayload, SOAP_12_NAMESPACE);
        assertEquals("<soap:Fault xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"><soap:Code><soap:Value>soap:Receiver</soap:Value><soap:Subcode><soap:Value xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\">ns1:VersionMismatch</soap:Value></soap:Subcode></soap:Code><soap:Reason><soap:Text xml:lang=\"en\">\"http://www.w3.org/2003/05/soap-envelope/\", the namespace on the \"Envelope\" element, is not a valid SOAP version.</soap:Text></soap:Reason></soap:Fault>",
                soapResponse.evalXpathNode("//soap:Fault"));
    }

}
