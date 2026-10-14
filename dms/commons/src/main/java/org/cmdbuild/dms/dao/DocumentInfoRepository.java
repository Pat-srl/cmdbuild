/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import java.util.List;

public interface DocumentInfoRepository {

    List<DmsModelDocument> getAllByCardId(long cardId);

    DmsModelDocument getById(String documentId);

    DmsModelDocument getByIdIncludeDeleted(String documentId);

    DmsModelDocument getByIdOrNull(String documentId);

    DmsModelDocument getOne(long cardId, String documentId);

    void delete(DmsModelDocument document);

    List<DmsModelDocument> getAllVersions(String documentId);

    boolean hasContent();

    DmsModelDocument getByIdAndVersion(String documentId, String version);

    List<DmsModelDocument> getAll();

}
