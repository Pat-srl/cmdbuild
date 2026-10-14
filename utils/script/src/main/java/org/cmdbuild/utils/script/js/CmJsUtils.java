/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.script.js;

import static java.util.Arrays.asList;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class CmJsUtils {

    public static RhinoJsEngine getRhinoJsEngine(String... init) {
        RhinoJsEngine scriptEngine = checkNotNull(new RhinoJsEngine(), "js script engine not found");
        asList(init).forEach(rethrowConsumer(scriptEngine::eval));
        return scriptEngine;
    }

    public static class RhinoJsEngine {

        private final ScriptableObject globalScope;

        private RhinoJsEngine() {
            Context cx = Context.enter();
            try {
                cx.setLanguageVersion(Context.VERSION_ES6);
                globalScope = cx.initStandardObjects();
            } finally {
                Context.exit();
            }
        }

        public Object eval(String code) {
            Context cx = Context.enter();
            try {
                Object result = cx.evaluateString(globalScope, code, "script", 1, null);
                return Context.jsToJava(result, Object.class);
            } finally {
                Context.exit();
            }
        }

        public Object get(String name) {
            Object val = globalScope.get(name, globalScope);
            if (val == Scriptable.NOT_FOUND) {
                return null;
            }
            return Context.jsToJava(val, Object.class);
        }
    }
}
