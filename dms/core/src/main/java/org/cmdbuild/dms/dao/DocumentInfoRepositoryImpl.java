/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_VERSION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DocumentInfoRepositoryImpl implements DocumentInfoRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public DocumentInfoRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public boolean hasContent() {
        return dao.getJdbcTemplate().queryForObject("SELECT EXISTS (SELECT 1 FROM \"_Document\" LIMIT 1)", Boolean.class);
    }

    @Override
    public List<DmsModelDocument> getAllByCardId(long cardId) {
        return dao.selectAll().from(DmsModelDocument.class).where(DOCUMENT_ATTR_CARD, EQ, cardId).asList();
    }

    @Override
    public DmsModelDocument getOne(long cardId, String documentId) {
        checkNotBlank(documentId);
        DmsModelDocument document = getById(documentId);
        checkArgument(equal(cardId, document.getCardId()));
        return document;
    }

    @Override
    public DmsModelDocument getById(String documentId) {
        return checkNotNull(dao.selectAll().from(DmsModelDocument.class).where(DOCUMENT_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).getOneOrNull(), "document not found for id =< %s >", documentId);
    }

    @Override
    public DmsModelDocument getByIdIncludeDeleted(String documentId) {
        return checkNotNull(dao.selectAll().from(DmsModelDocument.class).includeHistory().where(DOCUMENT_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).where(ATTR_STATUS, IN, list("A", "N")).getOneOrNull(), "document not found for id =< %s >", documentId);
    }

    @Override
    @Nullable
    public DmsModelDocument getByIdOrNull(String documentId) {
        return dao.selectAll().from(DmsModelDocument.class).where(DOCUMENT_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).getOneOrNull();
    }

    @Override
    public List<DmsModelDocument> getAllVersions(String documentId) {
        return dao.selectAll().from(DmsModelDocument.class).includeHistory().where(ATTR_CURRENTID, EQ, getById(documentId).getId()).asList();
    }

    @Override
    public void delete(DmsModelDocument document) {
        dao.delete(document);
    }

    @Override
    public DmsModelDocument getByIdAndVersion(String documentId, String version) {
        return dao.selectAll().from(DmsModelDocument.class).includeHistory().where(ATTR_CURRENTID, EQ, getById(documentId).getId()).where(DOCUMENT_ATTR_VERSION, EQ, checkNotBlank(version)).orderBy(ATTR_BEGINDATE, DESC).limit(1).getOne();
    }

    @Override
    public List<DmsModelDocument> getAll() {
        return dao.selectAll().from(DmsModelDocument.class).asList();
    }

}
