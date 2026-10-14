/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.beans;

import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.logic.mapping.json.Constants;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailTemplateImplTest {

    /**
     * Test of getUploadAttachmentsFilter method, upload all attachments for the
     * card, of class EmailTemplateImpl.
     */
    @Test
    public void testGetUploadAttachmentsFilter_All() {
        System.out.println("getUploadAttachmentsFilter_All");

        // arrange:
        EmailTemplateImpl instance = EmailTemplateImpl.builder()
                .withCode("Template")
                .withContentType("text/html")
                .withContent("TestAttr")
                .withMeta(map(EmailTemplate.UPLOAD_ATTACHMENTS_FILTERS, "{}"))
                .build();

        // act:
        CmdbFilter result = instance.getUploadAttachmentsFilter();

        // assert:
        assertTrue(result.isNoop());
    }

    /**
     * Test of getUploadAttachmentsFilter method, upload filtered (attribute
     * filter) attachments for the card, of class EmailTemplateImpl.
     */
    @Test
    public void testGetUploadAttachmentsFilter_AttributeFilter() {
        System.out.println("getUploadAttachmentsFilter_AttributeFilter");

        // arrange:
        EmailTemplateImpl instance = EmailTemplateImpl.builder()
                .withCode("Template")
                .withContentType("text/html")
                .withContent("TestAttr")
                .withMeta(map(EmailTemplate.UPLOAD_ATTACHMENTS_FILTERS, buildAttributeFilter()))
                .build();

        // act:
        CmdbFilter result = instance.getUploadAttachmentsFilter();

        // assert:
        assertTrue(result.hasFilter());
        assertTrue(result.hasAttributeFilter());
    }

    /**
     * Test of getUploadAttachmentsFilter method, don't upload attachments at
     * all for the card, of class EmailTemplateImpl.
     */
    @Test
    public void testGetUploadAttachmentsFilter_None() {
        System.out.println("getUploadAttachmentsFilter_None");

        // arrange:
        EmailTemplateImpl instance = EmailTemplateImpl.builder()
                .withCode("Template")
                .withContentType("text/html")
                .withContent("TestAttr")
                .build();

        // act:
        CmdbFilter result = instance.getUploadAttachmentsFilter();

        // assert:
        assertTrue(result.isFalse());
    }

    /**
     * Test of getUploadAttachmentsFilter method, don't upload attachments at
     * all for the card, of class EmailTemplateImpl.
     */
    @Test
    public void testGetUploadAttachmentsFilter_NoneWithNull() {
        System.out.println("getUploadAttachmentsFilter_NoneWithNull");

        // arrange:
        EmailTemplateImpl instance = EmailTemplateImpl.builder()
                .withCode("Template")
                .withContentType("text/html")
                .withContent("TestAttr")
                .withMeta(map(EmailTemplate.UPLOAD_ATTACHMENTS_FILTERS, null))
                .build();

        // act:
        CmdbFilter result = instance.getUploadAttachmentsFilter();

        // assert:
        assertTrue(result.isFalse());
    }

    private String buildAttributeFilter() {
        return toJson(map(
                Constants.Filters.ATTRIBUTE_KEY,
                map(
                        "simple",
                        map(Constants.Filters.ATTRIBUTE_KEY, "Code", "operator", "equal", "value", "something")
                )
        ));
    }
}
