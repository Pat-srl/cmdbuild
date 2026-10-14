/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
public class GeneratedData_Classe_FilteredCards extends GeneratedData_Classe {
    private final List<FilteredCard> filteredCards = list();
    
    
    public void addValues(FilteredCard data, Map<String, Object> cardSerialization) {
        filteredCards.add(data);
        
        if (values == null) {
            values = list();
        }
        values.add(cardSerialization);
    }

    // @todo AFE TBC usato?
//    /**
//     * Used in {@link CardDataSerializerImpl_Pojo}
//     *
//     * @return
//     */
//    public Map<Long, Card> getCards() {
//        return filteredCards.stream()
//                .collect(Collectors.toMap(FilteredCard::getCardId,
//                        e -> e.getCard()));
//    }
    
}
