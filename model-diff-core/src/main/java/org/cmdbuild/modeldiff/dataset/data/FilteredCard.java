/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.List;
import java.util.Set;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.modeldiff.data.CmCardAttributesData;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * {@link Card} with (in case):
 * <ul>
 * <li>filtering on attributes;
 * <li>attachments.
 * </ul>
 * 
 * <p>Used while serializing a {@link Card} to create <i>data</i> JSON, because a 
 * full {@link Card} is needed. A {@link CmCardAttributesData} wouldn't be enough
 * to pass it to {@link CardWsSerializationHelperv3}.
 * 
 * @author afelice
 */
public class FilteredCard {
    private final Card card;
    private final Set<String> selectedAttrs;
    private List<DocumentInfoAndDetail> attachmentsInfos = list();

    public FilteredCard(Card card, Set<String> selectedAttrs) {
        this.card = card;
        
        if (card instanceof Card_WithAttachments) {
            this.attachmentsInfos = ((Card_WithAttachments)card).attachmentsInfos;
        }
        
        this.selectedAttrs = selectedAttrs;
    }

    public Card getCard() {
        return card;
    }

    public Long getCardId() {
        return getCard().getId();
    }
    
    public Set<String> getSelectedAttrs() {
        return selectedAttrs;
    }
    
    public List<DocumentInfoAndDetail> getAttachmentsInfos() {
        return attachmentsInfos;
    }
    
}
