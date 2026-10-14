/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.lang;

import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.alwaysFalse;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.alwaysTrue;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.and;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.isNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.or;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ataboga
 */
public class CmPredicatesTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final List LIST_TO_CHECK = list("a", "b", null);

    @Test
    public void testAlwaysTrue() {
        System.out.println("filter list with alwaysTrue()");

        assertTrue(LIST_TO_CHECK.stream().filter(alwaysTrue()).count() == 3L);
        assertTrue(list(LIST_TO_CHECK).filter(alwaysTrue()).size() == 3);
    }

    @Test
    public void testAlwaysFalse() {
        System.out.println("filter list with alwaysFalse()");

        assertTrue(LIST_TO_CHECK.stream().filter(alwaysFalse()).count() == 0L);
        assertTrue(list(LIST_TO_CHECK).filter(alwaysFalse()).isEmpty());
    }

    @Test
    public void testIsNull() {
        System.out.println("filter list with isNull()");

        assertTrue(LIST_TO_CHECK.stream().filter(isNull()).count() == 1L);
        assertTrue(list(LIST_TO_CHECK).filter(isNull()).size() == 1);
    }

    @Test
    public void testNotNull() {
        System.out.println("filter list with notNull()");

        assertTrue(LIST_TO_CHECK.stream().filter(notNull()).count() == 2L);
        assertTrue(list(LIST_TO_CHECK).filter(notNull()).size() == 2);
    }

    @Test
    public void testAnd() {
        System.out.println("filter list with alwaysTrue() && notNull()");

        assertTrue(LIST_TO_CHECK.stream().filter(and(alwaysTrue(), notNull())).count() == 2L);
        assertTrue(list(LIST_TO_CHECK).filter(and(alwaysTrue(), notNull())).size() == 2);
    }

    @Test
    public void testOr() {
        System.out.println("filter list with alwaysFalse() || notNull()");

        assertTrue(LIST_TO_CHECK.stream().filter(or(alwaysFalse(), notNull())).count() == 2L);
        assertTrue(list(LIST_TO_CHECK).filter(or(alwaysFalse(), notNull())).size() == 2);
    }

    @Test
    public void testEqual() {
        System.out.println("filter list with equal(Function<T, R> func, R value)");

        TestPredicateClass testPredicateClassA = new TestPredicateClass("a");
        TestPredicateClass testPredicateClassC = new TestPredicateClass("c");

        List objList = List.of(testPredicateClassA, testPredicateClassC);

        assertTrue(objList.stream().filter(equal(TestPredicateClass::getStringa, "a")).count() == 1L);
        assertTrue(list(objList).filter(equal(TestPredicateClass::getStringa, "a")).size() == 1L);
        assertTrue(objList.stream().filter(equal(TestPredicateClass::getStringa, "b")).count() == 0L);
        assertTrue(list(objList).filter(equal(TestPredicateClass::getStringa, "b")).size() == 0L);
    }

    private class TestPredicateClass {

        private final String stringa;

        public TestPredicateClass(String stringa) {
            this.stringa = stringa;
        }

        public String getStringa() {
            return stringa;
        }
    }
}
