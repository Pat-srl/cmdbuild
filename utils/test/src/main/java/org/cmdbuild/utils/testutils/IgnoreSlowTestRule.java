/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.testutils;

import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.junit.Assume.assumeTrue;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class IgnoreSlowTestRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new IgnorableStatement(base, description);
    }

    private class IgnorableStatement extends Statement {

        private final Statement base;
        private final Description description;

        public IgnorableStatement(Statement base, Description description) {
            this.base = base;
            this.description = description;
        }

        @Override
        public void evaluate() throws Throwable {
            boolean shouldIgnore = false;
            Slow annotation = description.getAnnotation(Slow.class);
            if (annotation != null) {
                if (isNotBlank(System.getProperty("org.cmdbuild.test.skipSlow"))) {
                    shouldIgnore = toBoolean(System.getProperty("org.cmdbuild.test.skipSlow"));
                } else if (isNotBlank(System.getenv("CMDBUILD_TEST_SKIP_SLOW"))) {
                    shouldIgnore = toBoolean(System.getenv("CMDBUILD_TEST_SKIP_SLOW"));
                }
            }
            assumeTrue("slow test, ignored", !shouldIgnore);
            base.evaluate();
        }
    }
}
