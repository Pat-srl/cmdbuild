/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dms.dao.DocumentData.DOCUMENTDATA_ATTR_DOCUMENTID;
import static org.cmdbuild.dms.dao.DocumentData.DOCUMENTDATA_ATTR_VERSION;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class DocumentDataRepositoryImpl implements DocumentDataRepository {

    private final DaoService dao;

    public DocumentDataRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    @Nullable
    public byte[] getDocumentDataOrNull(String documentId, String version) {
        DocumentData data = dao.selectAll().from(DocumentData.class).where(DOCUMENTDATA_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).where(DOCUMENTDATA_ATTR_VERSION, EQ, checkNotBlank(version)).getOneOrNull();
        return data == null ? null : data.getData();
    }

    @Override
    public boolean hasDocumentData(String documentId, String version) {
        return dao.selectCount().from(DocumentData.class).where(DOCUMENTDATA_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).where(DOCUMENTDATA_ATTR_VERSION, EQ, checkNotBlank(version)).getCount() > 0;
    }

    @Override
    public void createDocumentData(String documentId, String version, byte[] data) {
        dao.create(DocumentData.builder().withDocumentId(documentId).withVersion(version).withData(data).build());
    }

}
