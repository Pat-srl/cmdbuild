/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import java.util.Objects;
import org.cmdbuild.dao.entrytype.Classe;

/**
 * A {@link Card} <i>attachment</i> has no related <i>metadata card</i> but is
 * identified by it's <i>DMS document id</i>.
 * 
 * @author afelice
 */
public class FileAttachmentHandle_CardAttachment extends FileAttachmentHandle {

    private String documentId;
//    private String metadataCardId;
    
    /**
     * Valued only for <i>card attachment</i>, if this is an update document
     * operation on an existing <i>card attachment</i>.
     */
    private boolean bUpdate;

    /**
     * For newly created <i>card attachment</i>.
     *
     * @param classe
     * @param cardId the related card id 
     * @param documentId the document id
     */
    public FileAttachmentHandle_CardAttachment(Classe classe, String cardId, String documentId) {
        this(classe, cardId, documentId, false);
    }

    /**
     * For <i>card attachment</i>.
     *
     * @param classe
     * @param cardId
     * @param documentId 
     * @param bUpdate if this is an update document operation on an existing
     * <i>card attachment</i>.
     *
     */
    public FileAttachmentHandle_CardAttachment(Classe classe, String cardId, String documentId, boolean bUpdate) {
        super(classe, cardId);

        this.documentId = documentId;                
//        this.metadataCardId = metadataCardId;

        this.bUpdate = bUpdate;
    }

    @Override
    public FileAttachmentHandle_CardAttachment copyWith(String newCardId) {
        return new FileAttachmentHandle_CardAttachment(classe, newCardId, documentId, bUpdate);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileAttachmentHandle_CardAttachment other = (FileAttachmentHandle_CardAttachment) o;        
        return super.equals(other) && Objects.equals(documentId, other.documentId);
    }    

    @Override
    public int hashCode() {
        int hash = 5;
        
        hash = 67 * hash + super.hashCode();
        hash = 67 * hash + Objects.hashCode(this.documentId);
        
        return hash;
    }

//    public String getMetadataCardId() {
//        return metadataCardId;
//    }    
    
    /**
     *
     * @return if this is an update document operation on an existing <i>card
     * attachment</i>.
     */
    public boolean isUpdate() {
        return bUpdate;
    }

    @Override
    public String toString() {
        return "FileAttachmentHandle_CardAttachment{classId=< %s >, cardId=< %s >, documentId=< %s >, udate=< %s >}".formatted(getClasseName(), cardId, documentId, bUpdate);
    }
} // end FileAttachmentHandle_CardAttachment class
