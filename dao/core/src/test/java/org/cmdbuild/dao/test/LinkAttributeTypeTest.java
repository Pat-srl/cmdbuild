package org.cmdbuild.dao.test;

import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.LINK_MATCHING_REGEX;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.validateLinkAttrValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LinkAttributeTypeTest {

    @Test
    public void testLinkAttrValidation() {
        assertEquals("<a href=\"http://test\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\">test</a>"));
        assertEquals("<a href=\"http://test\"> </a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\"> </a>"));
        assertEquals("<a href=\"http://test\"></a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\"></a>"));
        assertEquals("<a href=\"ftp://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"ftp://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
        assertEquals("<a href=\"ftps://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"ftps://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
        assertEquals("<a href=\"ws://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"ws://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
        assertEquals("<a href=\"wss://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"wss://ciao.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
    }

    @Test
    public void testLinkAttributeValuesMatching() {
        assertEquals("<a href=\"http://test\">test</a>", validateLinkAttrValue("<a href=\"http://test\">test</a>"));
        assertEquals("<a href=\"http://test\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", validateLinkAttrValue("<a href=\"http://test\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
    }

    @Test
    public void testLinkAttributeRegexpMatching() {
        assertTrue("<a href=\"http://test\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"http://www.google.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"http://www.google.com?\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"http://www.google.com?query=asdasd\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"http://www.google.com?query=asdasd&query2=qweqwe\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"http://www.google.com?query=asdasd&query2=qweqwe#header\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"https://docs.google.com/document/d/1RLsnXErasdfxgPqMBMLp5XPq0qwuIopo/edit#heading=h.sai9h\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"https://docs.google.com/document/d/1RLsnXErqwehS7WZ49fs0qwuIopo/edit#heading=h.sai9h\" target=\"_blank\" rel=\"noopener noreferrer\">testlink</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"ftp://docs.google.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"ftps://docs.google.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"ws://docs.google.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
        assertTrue("<a href=\"wss://docs.google.com\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>".matches(LINK_MATCHING_REGEX));
    }

}
