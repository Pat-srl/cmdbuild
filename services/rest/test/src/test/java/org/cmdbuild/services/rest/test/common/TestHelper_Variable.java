/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.common;

/**
 *
 * @author schursin
 */
public class TestHelper_Variable {

    public static final String A_CLASS_VIEW_MODE_ADMIN = "admin";
    public static final String A_CLASS_VIEW_MODE_USER = "user";

    public static final Integer A_TEST_LIMIT = 5;
    public static final Long A_TEST_LONG_LIMIT = 10L;
    public static final Integer A_TEST_OFFSET = 0;
    public static final Long A_TEST_LONG_OFFSET = 0L;
    public static final String A_TEST_SORT = "";
    public static final String A_TEST_EMPTY_FILTER = "";
    public static final String A_TEST_NULL_FILTER = null;
    public static final String A_TEST_FILTER = """
            {
              "attribute": {
                "simple": {
                  "attribute": "active",
                  "operator": "equal",
                  "value": ["true"]
                }
              }
            }""";
    public static final String A_TEST_MODEL_STRING = "{\n" +
            "  \"output\": [\n" +
            "    {\n" +
            "      \"name\": \"cardId\",\n" +
            "      \"type\": \"FOREIGNKEY\",\n" +
            "      \"fkTarget\": \"Class.MyClass\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String A_TEST_DATAHANDLER_TYPE = "text/plain";

    public static final Boolean NOT_SHARED = false;
    public static final Boolean SHARED = true;

    public static final Boolean NOT_DETAILED = false;
    public static final Boolean DETAILED = true;

    public static final Boolean NOT_VISIBLE = false;
    public static final Boolean VISIBLE = true;

    public static final Boolean DONT_INCLUDE = false;
    public static final Boolean INCLUDE = true;

    public static final Boolean NOT_MERGE = false;
    public static final Boolean MERGE = true;

    public static final Boolean NOT_ACTIVE = false;
    public static final Boolean ACTIVE = true;

    public static final String A_KNOWN_DATASOURCE_NAME = "dataSourceName";

    public static final String A_JSON_MIME_TYPE = "application/json";

    public static final String A_KNOWN_GENERIC_ID1 = "id1";
    public static final String A_KNOWN_GENERIC_ID2 = "id2";
    public static final String A_KNOWN_GENERIC_ID3 = "id3";

    public static final String A_KNOWN_NUMBER = "123";

    public static final String A_KNOWN_NAME1 = "name1";
    public static final String A_KNOWN_NAME2 = "name2";
    public static final String A_KNOWN_NAME3 = "name3";

    public static final String A_KNOWN_CODE1 = "code1";
    public static final String A_KNOWN_CODE2 = "code2";
    public static final String A_KNOWN_CODE3 = "code3";

    public static final String A_KNOWN_PATCH1 = "patch1";
    public static final String A_KNOWN_PATCH2 = "patch2";
    public static final String A_KNOWN_PATCH3 = "patch3";

    public static final String A_KNOWN_FILE_NAME = "fileName";

    public static final String A_CSV_REPORT_EXTENSION = "csv";

    public static final String A_KNOWN_ANY_ID = "_ANY";
    public static final String A_KNOWN_ALL_ID = "_ALL";
    public static final String A_KNOWN_NOTALL_ID = "_NotALL";

    public static final String A_KNOWN_CLASS_NAME1 = "class1";
    public static final String A_KNOWN_CLASS_NAME2 = "class2";
    public static final String A_KNOWN_CLASS_NAME3 = "class3";
    public static final String A_KNOWN_CLASS_ID = "classId";
    public static final String A_SOURCE_CLASS_NAME1 = "sourceClass1";
    public static final String A_SOURCE_CLASS_NAME2 = "sourceClass2";
    public static final String A_TARGET_CLASS_NAME1 = "targetClass1";
    public static final String A_TARGET_CLASS_NAME2 = "targetClass2";
    public static final String A_DIFFERENT_CLASS_NAME = "anotherClass";

    public static final String A_KNOWN_ATTR_ID = "attrName1";
    public static final String A_KNOWN_ATTR_NAME1 = "attrName1";
    public static final String A_KNOWN_ATTR_NAME2 = "attrName2";
    public static final String A_KNOWN_ATTR_NAME3 = "attrName3";

    public static final String A_KNOWN_GISATTRIBUTE_NAME1 = "gisAttributeName1";
    public static final String A_KNOWN_GISATTRIBUTE_NAME2 = "gisAttributeName2";
    public static final String A_KNOWN_GISATTRIBUTE_NAME3 = "gisAttributeName3";
    public static final String A_KNOWN_GISATTRIBUTE_NAME4 = "gisAttributeName4";


    public static final String A_KNOWN_FILTER_NAME = "filterName";

    public static final String A_KNOWN_COMPONENT_CODE1 = "componentCode1";
    public static final String A_KNOWN_COMPONENT_CODE2 = "componentCode2";
    public static final String A_KNOWN_COMPONENT_CODE3 = "componentCode3";

    public static final String A_KNOWN_UI_COMPONENT_NAME1 = "componentName1";
    public static final String A_KNOWN_UI_COMPONENT_NAME2 = "componentName2";
    public static final String A_KNOWN_UI_COMPONENT_NAME3 = "componentName3";

    public static final String A_KNOWN_DASHBOARD_NAME1 = "dashboardName1";
    public static final String A_KNOWN_DASHBOARD_NAME2 = "dashboardName2";
    public static final String A_KNOWN_DASHBOARD_NAME3 = "dashboardName3";

    public static final String A_KNOWN_LOOKUP_NAME1 = "lookupName1";
    public static final String A_KNOWN_LOOKUP_NAME2 = "lookupName2";
    public static final String A_KNOWN_LOOKUP_NAME3 = "lookupName3";

    public static final String A_KNOWN_EMAIL_NAME1 = "emailName1";
    public static final String A_KNOWN_EMAIL_NAME2 = "emailName2";
    public static final String A_KNOWN_EMAIL_NAME3 = "emailName3";

    public static final String A_KNOWN_PLAN_ID = "planId";

    public static final String A_EMAIL_TEMPLATE_NAME1 = "emailTemplateName1";
    public static final String A_EMAIL_TEMPLATE_NAME2 = "emailTemplateName2";
    public static final String A_EMAIL_TEMPLATE_NAME3 = "emailTemplateName3";

    public static final String A_KNOWN_UI_COMPONENT_DATA_NAME1 = "uiComponentDataName1";
    public static final String A_KNOWN_UI_COMPONENT_DATA_NAME2 = "uiComponentDataName2";
    public static final String A_KNOWN_UI_COMPONENT_DATA_NAME3 = "uiComponentDataName3";

    public static final String A_KNOWN_REPORT_CONFIG_NAME = "reportConfigName";

    public static final String A_KNOWN_NAVTREE_NAME1 = "navTreeName1";
    public static final String A_KNOWN_NAVTREE_NAME2 = "navTreeName2";
    public static final String A_KNOWN_NAVTREE_NAME3 = "navTreeName3";

    public static final String A_KNOWN_REPORT_INFO_NAME1 = "reportInfoName1";
    public static final String A_KNOWN_REPORT_INFO_NAME2 = "reportInfoName2";
    public static final String A_KNOWN_REPORT_INFO_NAME3 = "reportInfoName3";

    public static final String A_KNOWN_ETL_GATE_NAME1 = "etlGateName1";
    public static final String A_KNOWN_ETL_GATE_NAME2 = "etlGateName2";
    public static final String A_KNOWN_ETL_GATE_NAME3 = "etlGateName3";

    public static final String A_KNOWN_ETL_TEMPLATE_NAME1 = "etlTemplateName1";
    public static final String A_KNOWN_ETL_TEMPLATE_NAME2 = "etlTemplateName2";
    public static final String A_KNOWN_ETL_TEMPLATE_NAME3 = "etlTemplateName3";

    public static final String A_KNOWN_VIEW_ID = "viewId";
    public static final String A_KNOWN_VIEW_NAME1 = "viewName1";
    public static final String A_KNOWN_VIEW_NAME2 = "viewName2";
    public static final String A_KNOWN_VIEW_NAME3 = "viewName3";

    public static final String A_KNOWN_WSVIEWDATA_NAME1 = "wsViewDataName1";
    public static final String A_KNOWN_WSVIEWDATA_NAME2 = "wsViewDataName2";

    public static final String A_KNOWN_DOMAIN_NAME1 = "domainName1";
    public static final String A_KNOWN_DOMAIN_NAME2 = "domainName2";

    public static final String A_KNOWN_LAYER_NAME = "layerName";

    public static final String A_KNOWN_XPDL_ID = "xpdlId";

    public static final String A_KNOWN_ROLE_ID = "roleId";
    public static final String A_KNOWN_ROLE_NAME1 = "roleName1";
    public static final String A_KNOWN_ROLE_NAME2 = "roleName2";
    public static final String A_KNOWN_ROLE_NAME3 = "roleName3";

    public static final String A_KNOWN_PROCESS_ID = "processId";

    public static final String A_KNOWN_WEBHOOK_CODE1 = "webhookCode1";
    public static final String A_KNOWN_WEBHOOK_CODE2 = "webhookCode2";
    public static final String A_KNOWN_WEBHOOK_CODE3 = "webhookCode3";

    public static final String A_KNOWN_TREE_MODE = "TREE";

    public static final String A_KNOWN_TREE_ID = "5";

    public static final String A_KNOWN_ATTACHMENT_ID = "attachmentId";

    public static final Long A_KNOWN_LONG_ID1 = 1L;
    public static final Long A_KNOWN_LONG_ID2 = 2L;
    public static final Long A_KNOWN_LONG_ID3 = 3L;

    public static final Long A_KNOWN_FILTER_ID1 = 1L;
    public static final Long A_KNOWN_FILTER_ID2 = 2L;

    public static final Long A_KNOWN_REPORT_ID1 = 1L;
    public static final Long A_KNOWN_REPORT_ID2 = 2L;

    public static final Long A_KNOWN_CLASS_ID1 = 1L;
    public static final Long A_KNOWN_CLASS_ID2 = 2L;
    public static final Long A_KNOWN_CLASS_ID3 = 3L;

    public static final Long A_KNOWN_USERDATA_ID1 = 10L;
    public static final Long A_KNOWN_USERDATA_ID2 = 20L;

    public static final Long A_EMAIL_SIGNATURE_ID1 = 10L;
    public static final Long A_EMAIL_SIGNATURE_ID2 = 20L;
    public static final Long A_EMAIL_SIGNATURE_ID3 = 30L;

    public static final Long A_KNOWN_LOOKUP_ID1 = 11L;
    public static final Long A_KNOWN_LOOKUP_ID2 = 22L;
    public static final Long A_KNOWN_LOOKUP_ID3 = 33L;

    public static final Long A_KNOWN_ROLE_ID1 = 101L;
    public static final Long A_KNOWN_ROLE_ID2 = 202L;
    public static final Long A_KNOWN_ROLE_ID3 = 303L;

    public static final Long A_KNOWN_JOBDATA_ID1 = 111L;
    public static final Long A_KNOWN_JOBDATA_ID2 = 222L;
    public static final Long A_KNOWN_JOBDATA_ID3 = 333L;
    public static final Long A_KNOWN_JOB_ID = 0L;

    public static final Long A_KNOWN_CARD_ID1 = 1000L;
    public static final Long A_KNOWN_CARD_ID2 = 2000L;
    public static final Long A_KNOWN_CARD_ID3 = 3000L;
    public static final Long A_KNOWN_CARD_ID4 = 4000L;

    public static final Long A_KNOWN_STORED_FILTER_ID1 = 1001L;
    public static final Long A_KNOWN_STORED_FILTER_ID2 = 2002L;

    public static final Long A_KNOWN_WATERWAY_DESCRIPTOR_ID1 = 1010L;
    public static final Long A_KNOWN_WATERWAY_DESCRIPTOR_ID2 = 2020L;
    public static final Long A_KNOWN_WATERWAY_DESCRIPTOR_ID3 = 3030L;

    public static final Long A_KNOWN_GRANTDATA_ID1 = 12L;
    public static final Long A_KNOWN_GRANTDATA_ID2 = 23L;
    public static final Long A_KNOWN_GRANTDATA_ID3 = 34L;

    public static final String A_KNOWN_HOMEPAGE_NAME1 = "homepageName1";
    public static final String A_KNOWN_HOMEPAGE_NAME2 = "homepageName2";
    public static final String A_KNOWN_HOMEPAGE_NAME3 = "homepageName3";

    public static final Long A_RANDOM_ID = 666L;

}
