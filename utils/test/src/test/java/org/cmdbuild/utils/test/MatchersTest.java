/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import static org.cmdbuild.utils.testutils.matchers.IsNumberEqual.equalToNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class MatchersTest {

    @Test
    public void testIsNumerEqual() {
        assertThat(123, equalToNumber(123l));
        assertThat(123l, equalToNumber(123));
        assertThat(123, equalToNumber(123.0));
        assertThat(123.0, equalToNumber(123));
        assertThat(123, equalToNumber(BigInteger.valueOf(123)));
        assertThat(BigInteger.valueOf(123), equalToNumber(123));
        assertThat(123, equalToNumber(BigDecimal.valueOf(123)));
        assertThat(BigDecimal.valueOf(123), equalToNumber(123));
    }

}
