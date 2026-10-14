/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.soap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmMultipartUtils.getSinglePlaintextPart;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipart;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.xml.CmXmlUtils;
import static org.cmdbuild.utils.xml.CmXmlUtils.getXpath;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXmlLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SoapHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String url, soapAction, requestBody, soapVersion = SOAP_VERSION_12;
    private final Map<String, String> namespaces = map();

    private final static String SOAP_VERSION_11 = "1.1", SOAP_VERSION_12 = "1.2";

    public static final Map<String, String> SOAP_11_NAMESPACE = ImmutableMap.of("soap", "http://schemas.xmlsoap.org/soap/envelope/"),
            SOAP_12_NAMESPACE = ImmutableMap.of("soap", "http://www.w3.org/2003/05/soap-envelope/");

    public static SoapHelper newSoap() {
        return new SoapHelper();
    }

    public SoapHelper withUrl(String url) {
        this.url = url;
        return this;
    }

    public SoapHelper withSoapAction(String soapAction) {
        this.soapAction = soapAction;
        return this;
    }

    public SoapHelper withSoapVersion(String soapVersion) {
        this.soapVersion = checkNotBlank(soapVersion);
        return this;
    }

    public SoapHelper withSoapVersion11() {
        return withSoapVersion(SOAP_VERSION_11);
    }

    public SoapHelper withSoapVersion12() {
        return withSoapVersion(SOAP_VERSION_12);
    }

    public SoapHelper withBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public SoapHelper withBody(String requestBody, Object... args) {
        return this.withBody(format(requestBody, list(args).stream().map(CmStringUtils::toStringOrEmpty).map(StringEscapeUtils::escapeXml10).collect(toList()).toArray(Object[]::new)));
    }

    public SoapHelper withNamespace(String prefix, String namespace) {
        this.namespaces.put(prefix, namespace);
        return this;
    }

    public SoapResponse call() {
        String otherSoapHeaderAttrs = "";
        switch (soapVersion) {
            case SOAP_VERSION_11 ->
                namespaces.putAll(SOAP_11_NAMESPACE);
            case SOAP_VERSION_12 -> {
                namespaces.putAll(SOAP_12_NAMESPACE);
                otherSoapHeaderAttrs += " soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\"";
            }
            default ->
                throw illegalArgument("unsupported soap version = %s", soapVersion);
        }
        String soapNs = namespaces.entrySet().stream().map(e -> format(" xmlns:%s=\"%s\"", e.getKey(), e.getValue())).collect(joining()) + otherSoapHeaderAttrs;
        String requestPayload = format("""
                                       <?xml version="1.0"?>
                                       <soap:Envelope %s>
                                       <soap:Body>%s</soap:Body>
                                       </soap:Envelope>""", soapNs, checkNotNull(requestBody, "request body is null"));

        logger.debug("preparing soap call to url =< {} > method =< {} >", url, soapAction);
        logger.trace("soap request payload = \n\n{}\n", prettifyXmlLazy(requestPayload));

        SoapResponse soapResponse = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(checkNotBlank(url, "soap url endpoint is null"));
            request.setEntity(new StringEntity(requestPayload, ContentType.create("application/soap+xml")));
            if (isNotBlank(soapAction)) {
                request.setHeader("SOAPAction", soapAction);
            }
            CloseableHttpResponse response = httpClient.execute(request);
            logger.debug("response status = {}", response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String responsePayload = EntityUtils.toString(entity),
                    contentType = ContentType.getLenientOrDefault(entity).toString();
            if (isMultipart(contentType)) {
                responsePayload = getSinglePlaintextPart(newDataSource(responsePayload, contentType));//TODO improve this (handle xop+xml, filter xml, ??)
            }
            logger.trace("soap response payload = \n\n{}\n", prettifyXmlLazy(responsePayload));
            soapResponse = new SoapResponseImpl(responsePayload, namespaces);
            checkArgument(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, "error: response status is %s", response.getStatusLine());
            //TODO validate soap response
            EntityUtils.consumeQuietly(entity);//TODO check if this is really required
            return soapResponse;
        } catch (Exception ex) {
            String soapInfo = "";
            if (soapResponse != null) {
                try {
                    String soapFault = soapResponse.evalXpathNode("soap:Fault");
                    soapInfo = format("fault =< %s >", checkNotBlank(soapFault, "soap fault not available"));
                } catch (Exception ex2) {
                    logger.debug("unable to extract soap fault from response", ex2);
                    try {
                        soapInfo = format("response =< %s >", abbreviate(soapResponse.asString()));
                    } catch (Exception ex3) {
                        logger.debug("unable to extract soap payload from response", ex3);
                    }
                }
            }
            throw runtime(ex, "error invoking soap ws url =< %s > method =< %s > %s", url, soapAction, soapInfo);
        }
    }

    public static class SoapResponseImpl implements SoapResponse {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, String> namespaces;
        private final String responsePayload;
        private final Supplier<Document> document = Suppliers.memoize(this::doToDocument);

        public SoapResponseImpl(String responsePayload, Map<String, String> namespaces) {
            this.responsePayload = checkNotBlank(responsePayload);
            this.namespaces = ImmutableMap.copyOf(namespaces);
            logger.debug("prepared soap response =< {} >", abbreviate(responsePayload));
        }

        @Override
        public String asString() {
            return responsePayload;
        }

        @Override
        public Document asDocument() {
            return document.get();
        }

        @Override
        @Nullable
        public String evalXpath(String expr) {
            return evalXpath(expr, XPathConstants.STRING);
        }

        @Override
        @Nullable
        public String evalXpathNode(String expr) {
            Node node = evalXpath(expr, XPathConstants.NODE);
            return applyOrNull(node, CmXmlUtils::nodeToString);
        }

        private <T> T evalXpath(String expr, QName type) {
            try {
                return (T) getXpath(namespaces).compile(checkNotBlank(expr, "xpath expr is null")).evaluate(document.get(), type);
            } catch (Exception ex) {
                throw runtime(ex, "error processing xpath expr =< %s >", expr);
            }
        }

        private Document doToDocument() {
            return CmXmlUtils.toDocument(responsePayload);
        }
    }
}
