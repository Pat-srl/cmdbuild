/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlBindingInfoImpl;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author afelice
 */
public class EcqlFilterSerializationHelperTest {

    private final static String FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)"; 
    private final static String FLOOR_FILTER_ECQL_ID = "b7ajnokc26tji9iiljhe5yret7ej";
    
    /**
     * Test of buildEcqlFilterStuff method, of class EcqlFilterSerializationHelper.
     */
    @Test
    public void testBuildEcqlFilterStuff_ClassAttribute() {
        System.out.println("buildEcqlFilterStuff_ClassAttribute");
        
        //arrange:
        String key = "filter";
        String filter = FLOOR_FILTER;
        String ecqlKey = "ecqlFilter";
        String ecqlId = FLOOR_FILTER_ECQL_ID;
        EcqlBindingInfo ecqlBindingInfo = EcqlBindingInfoImpl.builder()
                                           .withClientBindings(asList("Building.id"))
                                           .build();
        CmMapUtils.FluentMap<String, Object> expResult = map("filter", FLOOR_FILTER,
                                                             "ecqlFilter", map(
                                                                "id", FLOOR_FILTER_ECQL_ID,
                                                                "bindings", map(
                                                                        "server", emptyList(),
                                                                        "client", asList("Building.id")
                                                                )        
                                                             )
                                                        );
        //act:
        CmMapUtils.FluentMap<String, Object> result = EcqlFilterSerializationHelper.buildEcqlFilterStuff(
                        key, filter, 
                        ecqlKey, ecqlId, 
                        ecqlBindingInfo);
        
        //assert:
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildEcqlFilterStuff method, of class EcqlFilterSerializationHelper.
     */
    @Test
    public void testBuildEcqlFilterStuff_DomainSource() {
        System.out.println("buildEcqlFilterStuff_DomainSource");
        
        //arrange:
        String key = "sourceFilter";
        String filter = FLOOR_FILTER;
        String ecqlKey = "sourceFilter_ecqlFilter";
        String ecqlId = FLOOR_FILTER_ECQL_ID;
        EcqlBindingInfo ecqlBindingInfo = EcqlBindingInfoImpl.builder()
                                           .withClientBindings(asList("Building.id"))
                                           .build();
        CmMapUtils.FluentMap<String, Object> expResult = map("sourceFilter", FLOOR_FILTER,
                                                             "sourceFilter_ecqlFilter", map(
                                                                "id", FLOOR_FILTER_ECQL_ID,
                                                                "bindings", map(
                                                                        "server", emptyList(),
                                                                        "client", asList("Building.id")
                                                                )        
                                                             )
                                                        );
        //act:
        CmMapUtils.FluentMap<String, Object> result = EcqlFilterSerializationHelper.buildEcqlFilterStuff(
                        key, filter, 
                        ecqlKey, ecqlId, 
                        ecqlBindingInfo);
        
        //assert:
        assertEquals(expResult, result);
    }    
    
}
