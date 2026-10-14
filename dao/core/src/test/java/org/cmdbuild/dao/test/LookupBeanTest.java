/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.test;

import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.LookupValueImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LookupBeanTest {

    @Test
    public void testLookupBean() {
        LookupValue value = LookupValueImpl.builder().withCode("my_code").withDescription("My Description").withId(123l).withLookupType("my_type").build();

        assertEquals(123l, (long) convert(value, Long.class));
        assertEquals("my_code", convert(value, String.class, false));//TODO check this
    }

}
