/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join.test;

import java.math.BigInteger;
import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.view.join.utils.JoinViewUtils.assembleJoinId;
import static org.cmdbuild.view.join.utils.JoinViewUtils.parseJoinId;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JoinViewUtilsTest {

    @Test
    public void testJoinViewId() {
        assertEquals(0x7fffffffffffffffL, Long.MAX_VALUE);
        String value = assembleJoinId(list(13l, 0x7fffffffffffffffL, 123123123123l)).toString();
        assertEquals("130922337203685477580700000000123123123123", value);
        List<Long> ids = parseJoinId(new BigInteger(value));
        assertEquals(list(13l, 0x7fffffffffffffffL, 123123123123l), ids);
    }

}
