/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class ClassWsCommand {

    private final ClassSerializationHelper classSerializationHelper;
    private final UserClassService userClassService;

    public ClassWsCommand(UserClassService userClassService, ClassSerializationHelper classSerializationHelper) {
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.userClassService = checkNotNull(userClassService);
    }

    public List<Classe> doReaAll(Supplier<List<Classe>> function) {
        return function.get();
    }

    public ExtendedClass doRead(String classId, UserClassService.ClassQueryFeatures... features) {
        return userClassService.getExtendedClass(classId, features);
    }

    public ExtendedClass doCreate(ClassSerializationHelper.WsClassData data) {
        return userClassService.createClass(classSerializationHelper.extendedClassDefinitionForNewClass(data));
    }

    public ExtendedClass doUpdate(String classId, ClassSerializationHelper.WsClassData data) {
        return userClassService.updateClass(classSerializationHelper.extendedClassDefinitionForExistingClass(classId, data));
    }

    public void doDelete(String classId) {
        userClassService.deleteClass(classId);
    }

}
