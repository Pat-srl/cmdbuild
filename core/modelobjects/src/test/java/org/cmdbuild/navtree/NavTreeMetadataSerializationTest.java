/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree;

import org.cmdbuild.navtree.NavTreeMetadataImpl;
import static org.cmdbuild.navtree.NavTreeMetadataImpl.INITIAL_EXPANSION_DEPTH;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NavTreeMetadataSerializationTest {

    @Test
    public void testNavTreeNodeSerialization_DependencyTree_FormWsData() {
        System.out.println("navTreeNodeSerialization_DependencyTree_FromWsData");

        //arrange:
        int aKnownDepth = 12;
        NavTreeMetadataImpl metadata = NavTreeMetadataImpl.builder()
                .withInitialExpansionDepth(aKnownDepth)
                .build();

        //act:
//        String serialization = toJson(metadata);
//        System.out.println("TEST: serialization = %s".formatted(toPrettyJson(serialization)));
//        NavTreeMetadataImpl resultDeserialized = fromJson(serialization, NavTreeMetadataImpl.class);

        assertEquals((Integer) aKnownDepth, metadata.getInitialExpansionDepth());
    }

    @Test
    public void testNavTreeNodeSerialization_DependencyTree_FromPersistence() {
        System.out.println("navTreeNodeSerialization_DependencyTree_FromPersistence");

        //arrange:
        int aKnownDepth = 12;
        NavTreeMetadataImpl metadata = new NavTreeMetadataImpl(map(INITIAL_EXPANSION_DEPTH, toStringOrNull(aKnownDepth)));

        assertEquals((Integer) aKnownDepth, metadata.getInitialExpansionDepth());
    }

}
