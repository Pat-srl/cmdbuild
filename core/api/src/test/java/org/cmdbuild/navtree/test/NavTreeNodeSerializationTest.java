/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree.test;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import static org.cmdbuild.navtree.NavTreeNodeSubclassViewMode.SVM_SUBCLASSES;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavTreeNodeSerializationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testNavTreeNodeSerialization1() {
        NavTreeNode node1 = NavTreeNodeImpl.builder()
                .withId("_test")
                .withTargetClassName("MyClass")
                .build();
        String value = toJson(node1);
        logger.info("value = {}", value);
        NavTreeNode node2 = fromJson(value, NavTreeNode.class);
        assertEquals(node1.getId(), node2.getId());
        assertEquals(node1.getTargetClassName(), node2.getTargetClassName());
        assertEquals(node1.getParentId(), node2.getParentId());
        assertEquals(node1.getTargetClassDescription(), node2.getTargetClassDescription());
        assertEquals(node1.getDomainName(), node2.getDomainName());
        assertEquals(node1.getDirection(), node2.getDirection());
        assertEquals(node1.getShowOnlyOne(), node2.getShowOnlyOne());
        assertEquals(node1.getChildNodes(), node2.getChildNodes());
        assertEquals(node1.getTargetFilter(), node2.getTargetFilter());
        assertEquals(node1.getEnableRecursion(), node2.getEnableRecursion());
        assertEquals(node1.getSubclassViewMode(), node2.getSubclassViewMode());
        assertEquals(node1.getSubclassViewShowIntermediateNodes(), node2.getSubclassViewShowIntermediateNodes());
        assertEquals(node1.getSubclassFilter(), node2.getSubclassFilter());
        assertEquals(node1.getSubclassDescriptions(), node2.getSubclassDescriptions());
        assertEquals(node1.hasChildNodes(), node2.hasChildNodes());
        assertTrue(EqualsBuilder.reflectionEquals(node1.getThisNodeAndAllDescendants(), node2.getThisNodeAndAllDescendants()));
        assertEquals(node1.hasParent(), node2.hasParent());
        assertEquals(node1.hasFilter(), node2.hasFilter());
        assertEquals(node1.getDirect(), node2.getDirect());
    }

    @Test
    public void testNavTreeNodeSerialization2() {
        NavTreeNode node1 = NavTreeNodeImpl.builder()
                .withId("_test")
                .withTargetClassName("MyClass")
                .withSubclassViewMode(SVM_SUBCLASSES)
                .withSubclassDescriptions(map("One", "uno", "Two", "due"))
                .build();
        String value = toJson(node1);
        logger.info("value = {}", value);
        NavTreeNode node2 = fromJson(value, NavTreeNode.class);
        assertEquals(node1.getId(), node2.getId());
        assertEquals(node1.getTargetClassName(), node2.getTargetClassName());
        assertEquals(node1.getParentId(), node2.getParentId());
        assertEquals(node1.getTargetClassDescription(), node2.getTargetClassDescription());
        assertEquals(node1.getDomainName(), node2.getDomainName());
        assertEquals(node1.getDirection(), node2.getDirection());
        assertEquals(node1.getShowOnlyOne(), node2.getShowOnlyOne());
        assertEquals(node1.getChildNodes(), node2.getChildNodes());
        assertEquals(node1.getTargetFilter(), node2.getTargetFilter());
        assertEquals(node1.getEnableRecursion(), node2.getEnableRecursion());
        assertEquals(node1.getSubclassViewMode(), node2.getSubclassViewMode());
        assertEquals(node1.getSubclassViewShowIntermediateNodes(), node2.getSubclassViewShowIntermediateNodes());
        assertEquals(node1.getSubclassFilter(), node2.getSubclassFilter());
        assertEquals(node1.getSubclassDescriptions(), node2.getSubclassDescriptions());
        assertEquals(node1.hasChildNodes(), node2.hasChildNodes());
        assertTrue(EqualsBuilder.reflectionEquals(node1.getThisNodeAndAllDescendants(), node2.getThisNodeAndAllDescendants()));
        assertEquals(node1.hasParent(), node2.hasParent());
        assertEquals(node1.hasFilter(), node2.hasFilter());
        assertEquals(node1.getDirect(), node2.getDirect());
    }

}
