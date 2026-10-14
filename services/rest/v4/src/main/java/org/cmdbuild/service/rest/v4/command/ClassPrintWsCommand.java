/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

/**
 * @author ldare
 */
@Component
public class ClassPrintWsCommand {

    private final UserClassService userClassService;
    private final SysReportService sysReportService;

    public ClassPrintWsCommand(UserClassService userClassService, SysReportService sysReportService) {
        this.userClassService = checkNotNull(userClassService);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public DataHandler doPrintClassReport(String classId, WsQueryOptions wsQueryOptions, String extension, String attributes) {
        Classe classe = userClassService.getUserClass(classId);
        return sysReportService.executeUserClassReport(classe, reportExtFromString(extension), buildQueryOptions(classe, wsQueryOptions, attributes));
    }

    public DataHandler doPrintClassSchemaReport(String classId, String fileName, String extension) {
        return sysReportService.executeClassSchemaReport(userClassService.getUserClass(classId), reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    public DataHandler doPrintSchemaReport(String classId, String fileName, String extension) {
        return sysReportService.executeSchemaReport(reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    public static DaoQueryOptions buildQueryOptions(Classe classe, WsQueryOptions wsQueryOptions, @Nullable String attributes) {
        List<String> attrs = isBlank(attributes) ? null : fromJson(attributes, LIST_OF_STRINGS);
        return DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withAttrs(attrs)//TODO fix this
                .build().mapAttrNames(classe.getAliasToAttributeMap());
    }

}
