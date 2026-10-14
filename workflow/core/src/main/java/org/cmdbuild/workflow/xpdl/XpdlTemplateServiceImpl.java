/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.xpdl;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import static java.lang.String.format;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.workflow.model.Process;
import org.springframework.stereotype.Component;

@Component
public class XpdlTemplateServiceImpl implements XpdlTemplateService {

    private final AuthenticationService authenticationService;

    public XpdlTemplateServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = checkNotNull(authenticationService);
    }

    @Override
    public DataSource getTemplate(Process process) {
        XpdlDocumentHelper xpdlDocumentHelper = new XpdlDocumentHelper();
        xpdlDocumentHelper.createXpdlTemplateForProcess(process, list(authenticationService.getAllGroups().stream().filter(Role::isActive)).map(Role::getName));
        return newDataSource(xpdlDocumentHelper.xpdlByteArray(), "application/x-xpdl", format("%s.xpdl", process.getName()));
    }
}
