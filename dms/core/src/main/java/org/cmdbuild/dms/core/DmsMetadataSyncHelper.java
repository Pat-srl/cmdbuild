/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("dmsMetadataSyncHelper")
public class DmsMetadataSyncHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService service;
    private final DaoService dao;
    private final UserClassService userClassService;
    private final LockService lockService;

    public DmsMetadataSyncHelper(DmsService service, DaoService dao, UserClassService userClassService, LockService lockService) {
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
        this.userClassService = checkNotNull(userClassService);
        this.lockService = checkNotNull(lockService);
    }

    public void run() {
        checkArgument(service.isEnabled(), "dms service not enabled");
        logger.info("start migration/sync procedure; count records");
        long count = dao.selectCount().from(BASE_CLASS_NAME).getCount(),//TODO check simple class (??)
                documentCount = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).getCount();
        logger.info("processing {} cards, current doc metadata count = {}", count, documentCount);
        AtomicLong i = new AtomicLong(0);
        Consumer<Long> listener = buildProgressListener(count, e -> logger.info("processing: {}", e.getProgressDescriptionDetailed()));
        dao.getAllClasses().stream().filter(c -> c.isStandardClass() && !c.isSuperclass() && (userClassService.getUserClassOrNull(c.getName()) != null || c.getName().equals("Email") || c.getName().equals("_CalendarEvent"))).forEach(c -> {//TODO check simple class (??)
            logger.debug("processing class = {}", c);
            dao.select(ATTR_ID).from(c).run().forEach(r -> {
                logger.trace("processing record = {}", r);
                service.getCardAttachments(c.getName(), r.get(ATTR_ID, Long.class), DaoQueryOptionsImpl.emptyOptions(), true);
                listener.accept(i.incrementAndGet());
            });
        });
        logger.info("migration/sync completed, analyzing");
        long documentCount2 = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).getCount();
        logger.info("processed {} cards, doc metadata count = {} -> {}", i.get(), documentCount, documentCount2);
    }

    public void syncDocumentId(boolean hashCheck) {
        logger.info("start sync document id");
        long documentCount = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).getCount();
        logger.info("processing {} documents", documentCount);
        AtomicLong i = new AtomicLong(0);
        Consumer<Long> listener = buildProgressListener(documentCount, e -> logger.info("processing: {}", e.getProgressDescriptionDetailed()));
        List<Map<String, Object>> dmsModelRows = dao.getJdbcTemplate().queryForList("SELECT _dmsmodel.\"Id\" _id, _dmsmodel.\"Card\" _card, _dmsmodel.\"DocumentId\" _documentid, _dmsmodel.\"FileName\" _filename, _dmsmodel.\"Hash\" _hash, _class.\"Id\" _cardid, _cm3_utils_regclass_to_name(_class.\"IdClass\"::regclass) _cardclassname FROM \"DmsModel\" _dmsmodel LEFT JOIN \"Class\" _class ON _dmsmodel.\"Card\" = _class.\"Id\" AND _class.\"Status\" = 'A' WHERE _dmsmodel.\"Status\" = 'A'");
        dmsModelRows.forEach(r -> {
            String documentId = (String) r.get("_documentid");
            String fileName = (String) r.get("_filename");
            String cardClassName = applyOrNull(r.get("_cardclassname"), String.class::cast);
            Long cardId = applyOrNull(r.get("_cardid"), Long.class::cast);
            if (cardId != null) {
                DocumentInfoAndDetail cardAttachment = service.getCardAttachmentFromDms(cardClassName, cardId, fileName);
                if (cardAttachment != null) {
                    Boolean fileExists = hashCheck ? dao.getJdbcTemplate().queryForObject(format("SELECT EXISTS (SELECT * FROM \"DmsModel\" WHERE \"Status\" = 'A' AND \"Card\" = %s AND \"FileName\" = %s AND \"Hash\" = %s)", cardId, systemToSqlExpr(cardAttachment.getFileName()), systemToSqlExpr(cardAttachment.getHash())), Boolean.class) : true;
                    if (fileExists) {
                        if (!documentId.equals(cardAttachment.getDocumentId())) {
                            try (AutoCloseableItemLock lock = acquireLock(cardClassName, cardId)) {
                                dao.getJdbcTemplate().update(format("UPDATE \"DmsModel\" SET \"DocumentId\" = '%s' WHERE \"Status\" = 'A' AND \"Card\" = %s AND \"FileName\" = %s AND \"Hash\" = %s", cardAttachment.getDocumentId(), cardId, systemToSqlExpr(fileName), systemToSqlExpr(cardAttachment.getHash())));
                                logger.debug("sync attachment {} for card = {}, updated attachment id from {} to {}", fileName, cardId, documentId, cardAttachment.getDocumentId());
                            }
                        } else {
                            logger.debug("sync attachment {} for card = {} not required, attachment id {} is ok", fileName, cardId, documentId);
                        }
                    } else {
                        logger.warn("row with card =< {} > filename =< {} > hash =< {} > not found in dms model", cardId, fileName, cardAttachment.getHash());
                    }
                } else {
                    logger.warn("attachment =< {} > not found in dms", format("%s/%s/%s", cardClassName, cardId, fileName));
                }
            } else {
                logger.warn("card id =< {} > not found (attachment id =< {} >)", r.get("_card"), documentId);
            }
            listener.accept(i.incrementAndGet());
        });
    }

    private AutoCloseableItemLock acquireLock(String className, long cardId) {
        return lockService.aquireLockOrWaitOrFail(key(dao.getTypeName(className, cardId), cardId), LS_REQUEST);
    }
}
