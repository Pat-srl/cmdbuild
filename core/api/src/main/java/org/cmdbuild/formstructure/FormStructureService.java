/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formstructure;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import jakarta.annotation.Nullable;
import javax.swing.text.View;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface FormStructureService {

    @Nullable
    FormStructure getFormByCodeOrNull(String code);

    FormStructure createForm(String code, FormStructure form);

    FormStructure updateForm(String code, FormStructure form);

    void deleteForm(String code);

    default FormStructure getFormByCode(String code) {
        return checkNotNull(getFormByCodeOrNull(code), "form structure not found for code =< %s >", code);
    }

    @Nullable
    default FormStructure setForm(String code, @Nullable FormStructure form) {
        if (getFormByCodeOrNull(code) == null) {
            return form == null ? null : createForm(code, form);
        } else {
            if (form == null) {
                deleteForm(code);
                return null;
            } else {
                return updateForm(code, form);
            }
        }
    }

    @Nullable
    default FormStructure getFormForClassOrNull(Classe classe) {
        return getFormByCodeOrNull(format("class.%s.default", classe.getName()));
    }

    @Nullable
    default FormStructure getFormForTaskOrNull(Classe process, String taskId) {
        return getFormByCodeOrNull(format("process.%s.%s.default", process.getName(), checkNotBlank(taskId)));
    }

    @Nullable
    default FormStructure setFormForClass(Classe classe, @Nullable FormStructure form) {
        return setForm(format("class.%s.default", classe.getName()), form);
    }

    @Nullable
    default FormStructure setFormForTask(Classe process, String taskId, @Nullable FormStructure form) {
        return setForm(format("process.%s.%s.default", process.getName(), checkNotBlank(taskId)), form);
    }

    @Nullable
    default FormStructure getFormForViewOrNull(PrivilegeSubjectWithInfo view) {//TODO use actual view interface
        return getFormByCodeOrNull(format("view.%s.default", view.getName()));
    }

    @Nullable
    default FormStructure setFormForView(PrivilegeSubjectWithInfo view, @Nullable FormStructure form) {//TODO use actual view interface
        return setForm(format("view.%s.default", view.getName()), form);
    }

}
