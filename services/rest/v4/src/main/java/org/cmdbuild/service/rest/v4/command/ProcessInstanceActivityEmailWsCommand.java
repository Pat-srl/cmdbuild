/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ProcessInstanceActivityEmailWsCommand {

    private final EmailService emailService;

    public ProcessInstanceActivityEmailWsCommand(EmailService emailService) {
        this.emailService = checkNotNull(emailService);
    }

    public Collection<Email> doUpdateEmailWithCardData(Long flowId, WsQueryOptions wsQueryOptions) {
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        return emailService.getAllForCard(flowId, queryOptions);
    }
}
