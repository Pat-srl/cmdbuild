/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Handle for documents to merge in <i>dms</i>:
 * <ul>
 * <li>documents related to <code>FILE</code> {@link Attribute} in {@link Card}s and related metadata;
 * <li>{@link Card} attachments. 
 * </ul>
 * 
 * @author afelice
 */
public class DmsDocumentDataHandle {
    
    private Map<FileAttachmentHandle, DocumentHandle> fileAttribDocToInsertColl = map();
    private final Map<FileAttachmentHandle, String> fileAttribDocToRemoveColl = map();
    
    // Insert or update mode
    private Map<FileAttachmentHandle, DocumentHandle> cardAttachmentDocToInsertColl = map(); // a DocumentHandle for each documentId to add as attachment
    private final Map<FileAttachmentHandle, String> cardAttachmentDocToRemoveColl = map(); // the filename for each documentId to remove as attachment
    
    public void addFileAttribDocToInsert(FileAttachmentHandle fileAttributeHandle, DocumentHandle document) {
        fileAttribDocToInsertColl.put(fileAttributeHandle, document);
    }    

    public void addFileAttribDocToRemove(FileAttachmentHandle fileAttributeHandle, String toRemoveFilename) {
        fileAttribDocToRemoveColl.put(fileAttributeHandle, toRemoveFilename);
    }    
    
    public Map<FileAttachmentHandle, DocumentHandle> getFileAttribDocToInsertColl() {
        return map(fileAttribDocToInsertColl);
    }
    
    public Map<FileAttachmentHandle, String> getFileAttribDocToRemoveColl() {
        return map(fileAttribDocToRemoveColl);
    }
    
    public boolean isFileAttribDocToInsertEmpty() {
        return fileAttribDocToInsertColl.isEmpty();
    }

    public boolean isFileAttribDocToRemoveEmpty() {
        return fileAttribDocToRemoveColl.isEmpty();
    }    

    public void addCardAttachmentDocToInsert(FileAttachmentHandle fileAttributeHandle, DocumentHandle document) {
        cardAttachmentDocToInsertColl.put(fileAttributeHandle, document);
    }    

    public void addCardAttachmentDocToRemove(FileAttachmentHandle fileAttributeHandle, String toRemoveFilename) {
        cardAttachmentDocToRemoveColl.put(fileAttributeHandle, toRemoveFilename);
    }    
    
    public Map<FileAttachmentHandle, DocumentHandle> getCardAttachmentsToInsertColl() {
        return map(cardAttachmentDocToInsertColl);
    }
    
    public Map<FileAttachmentHandle, String> getCardAttachmentsToRemoveColl() {
        return map(cardAttachmentDocToRemoveColl);
    }
    
    public boolean isCardAttachmentToInsertEmpty() {
        return cardAttachmentDocToInsertColl.isEmpty();
    }

    public boolean isCardAttachmentToRemoveEmpty() {
        return cardAttachmentDocToRemoveColl.isEmpty();
    }    
    
    /**
     * Handle newly added {@link Card}: in <code>diff</code> there was still an <code>UUID</code>,
     * after card insert the persisted <code>id</code> has to be used.
     * 
     * @param uuidsMap 
     */
    public void apply(Map<String, Long> uuidsMap) {
        if (!uuidsMap.isEmpty()) {
            fileAttribDocToInsertColl = applyIdChange(fileAttribDocToInsertColl, uuidsMap);
            cardAttachmentDocToInsertColl = applyIdChange(cardAttachmentDocToInsertColl, uuidsMap);
        }
    }

    private <T> Map<FileAttachmentHandle, T> applyIdChange(Map<FileAttachmentHandle, T> origMap, Map<String, Long> uuidsMap) {
        Map<FileAttachmentHandle, T> result = map();
        
        origMap.entrySet().forEach(entry -> {
            final String foundCardId = entry.getKey().getCardId();
            FileAttachmentHandle toUseKey = entry.getKey();
            if (uuidsMap.containsKey(foundCardId)) {
                // Substitute card UUID with persisted id
                toUseKey = entry.getKey().copyWith(String.valueOf(uuidsMap.get(foundCardId)));
            }
            
            result.put(toUseKey, entry.getValue());
        });
        
        return result;
    }

}
