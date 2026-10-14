/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.xml;

import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CmXmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String S_KEY_INDENT_AMOUNT = "{http://xml.apache.org/xalan}indent-amount";

    @Nullable
    public static String prettifyIfXml(@Nullable String mayBeXml) {
        return isXml(mayBeXml) ? prettifyXml(mayBeXml) : mayBeXml;
    }

    public static Object prettifyXmlLazy(String xml) {
        return lazyString(() -> prettifyIfXml(xml));
    }

    public static String prettifyXml(String xml) {
        try {
            StringWriter stringWriter = new StringWriter();
            getPrettyTransformer().transform(new StreamSource(new StringReader(xml)), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception ex) {
            LOGGER.warn("unable to prettify xml = {}", abbreviate(xml.replaceAll("\\n|\\r", ""), 100));
            LOGGER.debug("unable to prettify xml", ex);
            return xml;
        }
    }

    public static boolean isXml(@Nullable String mayBeXml) {
        return nullToEmpty(mayBeXml).matches("(?s)^\\s*[<].*[>]\\s*$");//TODO better euristic; maybe use tika?
    }

    public static String nodeToStringPretty(Node node) {
        try {
            StringWriter stringWriter = new StringWriter();
            getPrettyTransformer().transform(new DOMSource(node), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String nodeToString(Node node) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerException ex) {
            throw runtime(ex);
        }
    }

    public static XPath getXpath(String... namespaces) {
        return getXpath(map((Object[]) namespaces));
    }

    public static XPath getXpath(Map<String, String> namespaces) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextImpl(namespaces));
        return xpath;
    }

    public static Document toDocument(String xml) {
        return toDocument(xml, true);
    }

    public static Document toDocumentNoNamespace(String xml) {
        return toDocument(xml, false);
    }

    public static List<Node> asList(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).collect(toList());
    }

    private static Document toDocument(String xml, boolean namespaceAware) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(checkNotBlank(xml))));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw runtime(ex, "error parsing xml document =< %s >", CmStringUtils.abbreviate(xml));
        }
    }

    @Nullable
    public static String applyXpath(String xml, String nskey, String nsval, String xpath) {
        return applyXpath(xml, map(nskey, nsval), xpath);
    }

    @Nullable
    public static String applyXpath(String xml, Map<String, String> namespaces, String xpath) {
        try {
            XPath xpathHelper = getXpath(namespaces);
            Document document = toDocument(xml);
            return (String) xpathHelper.compile(xpath).evaluate(document, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            throw runtime(ex, "error applying xpath expression =< %s >", xpath);
        }
    }

    @Nullable
    public static String applyXpath(Node xml, Map<String, String> namespaces, String xpath) {
        try {
            XPath xpathHelper = getXpath(namespaces);
            return (String) xpathHelper.compile(xpath).evaluate(xml, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            throw runtime(ex, "error applying xpath expression =< %s >", xpath);
        }
    }

    @Nullable
    public static String applyXpath(String xml, String xpath) {
        return applyXpath(xml, emptyMap(), xpath);
    }

    private static Transformer getPrettyTransformer() throws TransformerConfigurationException {

        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:template match=\"*\"><xsl:copy><xsl:copy-of select=\"@*\" /><xsl:apply-templates /></xsl:copy></xsl:template><xsl:template match=\"*/text()[not(normalize-space())]\" /></xsl:stylesheet>")));
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(S_KEY_INDENT_AMOUNT, "4");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        return transformer;
    }

    private static class NamespaceContextImpl implements NamespaceContext {

        private final BiMap<String, String> namespaces;

        public NamespaceContextImpl(Map<String, String> namespaces) {
            this.namespaces = ImmutableBiMap.copyOf(namespaces);
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return namespaces.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return namespaces.inverse().get(namespaceURI);
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return namespaces.inverse().containsKey(namespaceURI) ? singleton(getPrefix(namespaceURI)).iterator() : emptyList().iterator();
        }
    }
}
