/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EmailTemplateInlineDataTest {

    @Test
    public void testReadWrite1() {
        EmailTemplateInlineData one = EmailTemplateInlineDataImpl.builder()
                .withTemplate("template")
                .withId("123")
                .withContent("content")
                .withDelay(1234l)
                .withReportList(list("rep1", "rep2").map(ReportConfigImpl::fromCode))
                .build();

        assertEquals("template", one.getTemplate());
        assertEquals("123", one.getId());
        assertEquals("content", one.getContent());
        assertEquals((Long) 1234l, one.getDelay());
        assertEquals(list("rep1", "rep2"), list(one.getReportList()).map(ReportConfig::getCode));

        EmailTemplateInlineData two = fromJson(toJson(one), EmailTemplateInlineData.class);

        assertEquals("template", two.getTemplate());
        assertEquals("123", two.getId());
        assertEquals("content", two.getContent());
        assertEquals((Long) 1234l, two.getDelay());
        assertEquals(list("rep1", "rep2"), list(two.getReportList()).map(ReportConfig::getCode));
    }

    @Test
    public void testReadWrite2() {
        EmailTemplateInlineData one = EmailTemplateInlineDataImpl.builder()
                .withTemplate("template")
                .withId("123")
                .withContent("content")
                .withDelay(1234l)
                .withReportList(list(ReportConfigImpl.builder().withCode("test").withFormat(ReportFormat.ODT).withParams(map("one", "asd", "two", "dsa")).build()))
                .build();

        assertEquals("template", one.getTemplate());
        assertEquals("123", one.getId());
        assertEquals("content", one.getContent());
        assertEquals((Long) 1234l, one.getDelay());
        assertEquals(list("test"), list(one.getReportList()).map(ReportConfig::getCode));

        EmailTemplateInlineData two = fromJson(toJson(one), EmailTemplateInlineData.class);

        assertEquals("template", two.getTemplate());
        assertEquals("123", two.getId());
        assertEquals("content", two.getContent());
        assertEquals((Long) 1234l, two.getDelay());
        assertEquals(list("test"), list(two.getReportList()).map(ReportConfig::getCode));
        assertEquals(ReportFormat.ODT, getOnlyElement(two.getReportList()).getFormat());
        assertEquals(map("one", "asd", "two", "dsa"), getOnlyElement(two.getReportList()).getParams());
    }

}
