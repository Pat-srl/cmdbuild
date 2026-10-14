/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join.test;

import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.GREATER;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyJsonSafe;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.view.join.JoinAttributeGroupImpl;
import org.cmdbuild.view.join.JoinAttributeImpl;
import org.cmdbuild.view.join.JoinElementImpl;
import static org.cmdbuild.view.join.JoinType.JT_LEFT_JOIN;
import org.cmdbuild.view.join.JoinViewConfig;
import org.cmdbuild.view.join.JoinViewConfigImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinViewConfigSerializerTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testJoinViewConfig() {
        JoinViewConfig config = JoinViewConfigImpl.builder()
                .withMasterClass("MyClass")
                .withMasterClassAlias("MyClass_0")
                .withSorter(CmdbSorterImpl.sorter("MyAttr", DESC))
                .withFilter(AttributeFilterConditionImpl.builder().withOperator(GREATER).withKey("MyClass.MyAttr").withValues(10).build().toAttributeFilter().toCmdbFilters())
                .withAttributeGroup(JoinAttributeGroupImpl.builder().withName("MyGroup").withDescription("My Group").build())
                .withAttribute(JoinAttributeImpl.builder().withExpr("MyClass_0.MyAttr").withGroup("MyGroup").withName("Attr").withDescription("Attr Descr").build())
                .withJoinElement(JoinElementImpl.builder().withDomain("MyDomain").withSource("MyClass_0").withDomainAlias("MyDomain_0").withTargetType("MyOtherClass").withTargetAlias("MyOtherClass_0").withJoinType(JT_LEFT_JOIN).withDirection(RD_DIRECT).build())
                .build();

        String string = toJson(config);

        logger.info("config 1 = \n\n{}\n", prettifyJsonSafe(string));

        JoinViewConfig config2 = fromJson(string, JoinViewConfigImpl.class);
        String string2 = toJson(config2);

        logger.info("config 2 = \n\n{}\n", prettifyJsonSafe(string2));

        assertEquals(string2, string);
    }
}
