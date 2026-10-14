/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_LANGUAGE_BEANSHELL;

public class ScriptTaskExtraAttr {

    private final String content, language;

    public ScriptTaskExtraAttr(String language, @Nullable String content) {
        this.content = nullToEmpty(content);
        this.language = firstNotBlank(language, SCRIPT_LANGUAGE_BEANSHELL);
    }

    public String getScript() {
        return content;
    }

    public String getLanguage() {
        return language;
    }

}
