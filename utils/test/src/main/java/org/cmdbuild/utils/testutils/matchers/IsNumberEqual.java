/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.testutils.matchers;

import java.util.Objects;
import static org.cmdbuild.utils.lang.CmConvertUtils.toNumberOrNull;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsNumberEqual<T> extends BaseMatcher<T> {

    private final Number expectedValue;

    public IsNumberEqual(T equalArg) {
        expectedValue = toNumberOrNull(equalArg);
    }

    @Override
    public boolean matches(Object actualValue) {
        Number other = toNumberOrNull(actualValue);
        if (Objects.equals(expectedValue, other)) {
            return true;
        } else {
            return expectedValue.doubleValue() == other.doubleValue();//TODO improve this
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedValue);
    }

    @Factory
    public static <T> Matcher<T> equalToNumber(T operand) {
        return new IsNumberEqual<>(operand);
    }
}
