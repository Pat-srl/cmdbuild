/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import org.cmdbuild.etl.waterway.storage.EtlMessage;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_ATTACHMENTS;

/**
 * @author ldare
 */
@Component
public class EtlMessageWsCommand {

    private final WaterwayService waterwayService;
    private final CardMapperService cardMapperService;

    public EtlMessageWsCommand(WaterwayService waterwayService, CardMapperService cardMapperService) {
        this.waterwayService = checkNotNull(waterwayService);
        this.cardMapperService = checkNotNull(cardMapperService);
    }

    public PagedElements<WaterwayMessage> doReadMessages(WsQueryOptions wsQueryOptions) {
        DaoQueryOptions daoQueryOptions = DaoQueryOptionsImpl
                .copyOf(wsQueryOptions.getQuery())
                .withAttrs(cardMapperService.getClasseForModelOrBuilder(EtlMessage.class).getCoreAttributes().stream().map(Attribute::getName).filter(not(ETL_MESSAGE_ATTR_ATTACHMENTS::equals)).collect(toSet()))
                .build(); // skip attachments here
        return waterwayService.getMessages(wsQueryOptions.isDetailed() ? wsQueryOptions.getQuery() : daoQueryOptions);
    }

    public WaterwayMessage doRead(String messageReference) {
        return waterwayService.getMessage(messageReference);
    }

    public WaterwayMessageAttachment doReadAttachment(String messageReference, String attachmentId) {
        return waterwayService.getMessageAttachmentLoadData(messageReference, attachmentId);
    }

    public Map<WaterwayMessageStatus, Long> doReadMessagesStats() {
        return waterwayService.getMessagesStats().getMessageCountByStatus();
    }
}
