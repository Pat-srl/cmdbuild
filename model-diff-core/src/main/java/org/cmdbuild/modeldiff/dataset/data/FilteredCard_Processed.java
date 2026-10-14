/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNotNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Already processed card serialization.
 * 
 * <p>Used for <i>card attachments</i>, where the serialization logic is in {@link CardAttachmentBasicSerializer}.
 * @author afelice
 */
public class FilteredCard_Processed extends FilteredCard {
    
    private final Map<String, Object> cardValuesSerialization;
    
    public FilteredCard_Processed(Card card, Set<String> selectedAttrs, Map<String, Object> cardValuesSerialization) {
        super(card, selectedAttrs);
        
        if (isNotNullOrEmpty(selectedAttrs)) {
            this.cardValuesSerialization = map(cardValuesSerialization).withKeys(selectedAttrs);
        } else {
            this.cardValuesSerialization = cardValuesSerialization;
        }
    }

    public Map<String, Object> getCardValuesSerialization() {
        return cardValuesSerialization;
    }   
    
}
