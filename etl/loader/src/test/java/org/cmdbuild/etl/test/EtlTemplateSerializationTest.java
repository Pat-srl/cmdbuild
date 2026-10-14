/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.data.EtlTemplateConfigImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertTrue;
import org.cmdbuild.etl.loader.EtlTemplateConfig;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_UPDATE_ATTR_ON_MISSING;
import static org.cmdbuild.etl.loader.EtlTemplateType.ETT_EXPORT;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_XLSX;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_CODE;
import org.junit.Ignore;

public class EtlTemplateSerializationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    @Ignore //TODO remove this, and/or move to wy serialization test
    public void testTemplateToJson() {
        String filter = serializeFilter(AttributeFilterConditionImpl.eq("MyOtherAttr", "TEST").toAttributeFilter().toCmdbFilters());
        EtlTemplateConfig config = EtlTemplateConfigImpl.builder()
                .withAttributeNameForUpdateAttrOnMissing("myAttr")
                .withAttributeValueForUpdateAttrOnMissing("myValue")
                .withColumns(list(EtlTemplateColumnConfigImpl.builder().withAttributeName("MyOtherAttr").withColumnName("MyColAttr").withMode(ETCM_CODE).build()))
                .withExportFilterAsString(filter)
                .withMergeMode(EM_UPDATE_ATTR_ON_MISSING)
                .withTargetName("MyClass")
                .withTargetType(ET_CLASS)
                .withType(ETT_EXPORT)
                .withFileFormat(EFF_XLSX)
                .withSource("mySource")
                .build();

        assertEquals(filter, serializeFilter(config.getExportFilter()));

        String asString = toJson(config);

        logger.debug("as string = {}", asString);
//        assertEquals(fromJson("{\"columns\":[{\"attributeName\":\"MyOtherAttr\",\"columnName\":\"MyColAttr\",\"default\":null,\"mode\":\"code\"}],\"targetName\":\"MyClass\",\"source\":\"mySource\",\"attributeNameForUpdateAttrOnMissing\":null,\"attributeValueForUpdateAttrOnMissing\":null,\"exportFilter\":\"{\\\"filter\\\":\\\"meh\\\"}\",\"csvSeparator\":null,\"importKeyAttribute\":null,\"useHeader\":true,\"ignoreColumnOrder\":false,\"headerRow\":null,\"dataRow\":null,\"firstCol\":null,\"errorTemplate\":null,\"errorAccount\":null,\"targetType\":\"class\",\"format\":\"xlsx\",\"mergeMode\":\"no_merge\",\"type\":\"export\"}", JsonNode.class),
//                fromJson(asString, JsonNode.class));

        EtlTemplateConfig config2 = fromJson(asString, EtlTemplateConfigImpl.class);

        assertEquals(config.getTargetName(), config2.getTargetName());
        assertEquals(config.getTargetType(), config2.getTargetType());
        assertEquals(ETCM_CODE, config.getColumns().get(0).getMode());
        assertEquals(config.getMergeMode(), config2.getMergeMode());
        assertEquals(config.getFileFormat(), config2.getFileFormat());
        assertEquals(config.getSource(), config2.getSource());
        assertEquals(filter, serializeFilter(config2.getExportFilter()));
        assertTrue(config.getUseHeader());
    }

}
