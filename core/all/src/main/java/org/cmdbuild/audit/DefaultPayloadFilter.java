package org.cmdbuild.audit;

import java.util.Map;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.xml.CmXmlUtils.getXpath;
import static org.cmdbuild.utils.xml.CmXmlUtils.nodeToStringPretty;
import static org.cmdbuild.utils.xml.CmXmlUtils.isXml;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Component
public class DefaultPayloadFilter implements PayloadFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public RequestData filterPayload(RequestData data) {
        if (data.getMethod().matches("(?i)POST|PUT") && data.getPath().matches("(?i)/?services/rest/v./(sessions|users(/.+)?)/?") && data.hasPayloadText() && isJson(data.getPayloadText())) {
            logger.debug("strip auth password from request tracking data (restws)");
            data = RequestDataImpl.copyOf(data).withPayloadText(stripJsonAuthPassword(data.getPayloadText())).build();
        } else if (data.hasMethod("POST") && data.getPath().matches("(?i)/?services/soap/Private/?") && data.hasPayloadText() && isXml(data.getPayloadText())) {
            logger.debug("strip auth password from request tracking data (soapws)");
            data = RequestDataImpl.copyOf(data).withPayloadText(stripSoapAuthPassword(data.getPayloadText())).build();
        }
        return data;
    }

    private String stripJsonAuthPassword(String payloadText) {
        Map<String, Object> payload = fromJson(payloadText, MAP_OF_OBJECTS);
        for (String key : list(payload.keySet()).filter(k -> k.matches("(?i)(password|oldpassword|confirmpassword)"))) {
            payload = map(payload).with(key, "___password_removed___");
        }
        return toJson(payload);
    }

    private String stripSoapAuthPassword(String payloadText) {
        try {
            Document document = toDocument(payloadText);
            Node node = (Node) getXpath("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
                    .compile("//wsse:Password").evaluate(document, XPathConstants.NODE);
            if (node != null && isNotBlank(node.getTextContent())) {
                node.setTextContent("___password_removed___");
                payloadText = nodeToStringPretty(document);
            }
            return payloadText;
        } catch (XPathExpressionException ex) {
            throw runtime(ex);
        }
    }

}
