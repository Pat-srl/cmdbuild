/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toUnmodifiableList;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import static org.cmdbuild.service.rest.v4.command.ClassAttributeWsCommand.prepareAttributesToUpdateForOrder;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.stereotype.Component;

/**
 *
 * @author ldare
 */
@Component
public class DmsModelWsCommand {

    private final DaoService daoService;
    private final UserClassService userClassService;
    private final ClassSerializationHelper classSerializationHelper;
    private final SysReportService sysReportService;

    public DmsModelWsCommand(DaoService daoService, UserClassService userClassService, ClassSerializationHelper classSerializationHelper, SysReportService sysReportService) {
        this.daoService = checkNotNull(daoService);
        this.userClassService = checkNotNull(userClassService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public List<Classe> doReadAll(Boolean active) {
        Stream<Classe> listClasse = daoService.getAllClasses().stream().filter(Classe::isDmsModel);
        if (active) {
            listClasse = listClasse.filter(Classe::isActive);
        }
        return listClasse.collect(toUnmodifiableList());
    }

    public ExtendedClass doRead(String classId, UserClassService.ClassQueryFeatures feature1, UserClassService.ClassQueryFeatures feature2) {
        Classe classe = loadDmsModel(classId);
        return userClassService.getExtendedClass(classe, feature1, feature2);
    }

    public Attribute doReadAttribute(String classId, String attrId) {
        return loadDmsModel(classId).getAttribute(attrId);
    }

    public Collection<Attribute> doReadAllAttributes(String classId, Function<Classe, Collection<Attribute>> function) {
        Classe classe = loadDmsModel(classId);
        return function.apply(classe);
    }

    public ExtendedClass doCreate(ClassSerializationHelper.WsClassData data) {
        loadDmsModel(data.parentId);
        return userClassService.createClass(classSerializationHelper.extendedClassDefinitionForNewClass(data));
    }

    public ExtendedClass doUpdate(String classId, ClassSerializationHelper.WsClassData data) {
        loadDmsModel(classId);
        return userClassService.updateClass(classSerializationHelper.extendedClassDefinitionForExistingClass(classId, data));
    }

    public void doDelete(String classId) {
        loadDmsModel(classId);
        userClassService.deleteClass(classId);
    }

    public Attribute doCreateAttribute(String classId, WsAttributeData data) {
        Classe classe = loadDmsModel(classId);
        return userClassService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
    }

    public Attribute doUpdateAttribute(String classId, WsAttributeData data) {
        Classe classe = loadDmsModel(classId);
        return userClassService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
    }

    public void doDeleteAttribute(String classId, String attrId) {
        loadDmsModel(classId);
        userClassService.deleteAttribute(classId, attrId);
    }

    public Classe doReorderAttributes(String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Classe classe = loadDmsModel(classId);
        userClassService.updateAttributes(prepareAttributesToUpdateForOrder(classe::getAttribute, attrOrder));

        return classe;
    }

    public DataHandler doPrintModelSchemaReport(String classId, String fileName, String extension) {
        return sysReportService.executeClassSchemaReport(loadDmsModel(classId), reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    private Classe loadDmsModel(String classId) {
        Classe classe = daoService.getClasse(classId);
        checkArgument(classe.isDmsModel(), "invalid class =< %s >: not a dms model", classId);
        return classe;
    }
}
