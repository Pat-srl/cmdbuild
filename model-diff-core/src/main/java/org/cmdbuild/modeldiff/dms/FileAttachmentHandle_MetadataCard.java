/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import org.cmdbuild.dao.entrytype.Classe;

public class FileAttachmentHandle_MetadataCard extends FileAttachmentHandle {

    /**
     * Valued only for <i>card metadata for <code>FILE</code>
     * {@link Attribute}</i>, if this is an update document operation on an
     * existing <i>card metadata</i>.
     */
    private boolean bUpdate;

    /**
     * For newly created <i>card metadata for <code>FILE</code>
     * {@link Attribute}</i>.
     *
     * @param classe
     * @param cardId
     */
    public FileAttachmentHandle_MetadataCard(Classe classe, String cardId) {
        this(classe, cardId, false);
    }

    /**
     * For <i>card attribute</i>.
     *
     * @param classe
     * @param cardId
     * @param bUpdate if this is an update document operation on an existing
     * <i>card metadata for <code>FILE</code> {@link Attribute}</i>.
     *
     */
    public FileAttachmentHandle_MetadataCard(Classe classe, String cardId, boolean bUpdate) {
        super(classe, cardId);

        this.bUpdate = bUpdate;
    }

    @Override
    public FileAttachmentHandle_MetadataCard copyWith(String newCardId) {
        return new FileAttachmentHandle_MetadataCard(classe, newCardId, bUpdate);
    }

    /**
     *
     * @return if this is an update document operation on an existing <i>card
     * metadata for <code>FILE</code> {@link Attribute}</i>.
     */
    public boolean isUpdate() {
        return bUpdate;
    }

    @Override
    public String toString() {
        return "FileAttachmentsHandle_MetadataCard{classId=< %s >, cardId=< %s >, udate=< %s >}".formatted(getClasseName(), cardId, bUpdate);
    }
} // end FileAttachmentHandle_MetadataCard class
