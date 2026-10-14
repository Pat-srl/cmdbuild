/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class DmsAttachmentDownloaderImpl implements DmsAttachmentDownloader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService dmsService;

    public DmsAttachmentDownloaderImpl(DmsService dmsService) {
        this.dmsService = checkNotNull(dmsService);
    }

    @Override
    public List<DataHandler> downloadAttachments(Card clientCard, CmdbFilter uploadAttachmentsFilter) {
        logger.debug("uploading attachments with filter =< {} >", uploadAttachmentsFilter);

        DaoQueryOptionsImpl daoQueryOptions = DaoQueryOptionsImpl.build(uploadAttachmentsFilter);
        List<DocumentInfoAndDetail> docInfos = dmsService.getCardAttachments(clientCard.getClassName(), clientCard.getId(), daoQueryOptions);

        logger.debug("uploading attachments matched =< {} >", docInfos);
        return list(docInfos).map(d -> dmsService.getDocumentData(clientCard.getClassName(), clientCard.getId(), d.getFileName(), d.getVersion()));
    }

}
