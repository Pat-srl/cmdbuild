/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.modeldiff.data.DataMerger;

/**
 * Placeholder class to aggregate additional infos not found in {@link Card}:
 * <ol>
 * <li>created in {@lin DataCollector};
 * <li>will be serialized in {@link CardDataSerializer};
 * <li>used back in {@link DataMerger}.
 * </ol>
 * 
 * @author afelice
 */
public class Card_WithAttachments implements Card {
    
    public Card innerCard;
    public List<DocumentInfoAndDetail> attachmentsInfos;

    public Card_WithAttachments(Card innerCard, List<DocumentInfoAndDetail> attachments) {
        this.innerCard = innerCard;
        this.attachmentsInfos = attachments;
    }

    @Override
    public Classe getType() {
        return innerCard.getType();
    }

    @Override
    public String getCode() {
        return innerCard.getCode();
    }

    @Override
    public String getDescription() {
        return innerCard.getDescription();
    }

    @Override
    public Long getId() {
        return innerCard.getId();
    }

    @Override
    public String getUser() {
        return innerCard.getUser();
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return innerCard.getBeginDate();
    }

    @Override
    public ZonedDateTime getEndDate() {
        return innerCard.getEndDate();
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getRawValues() {
        return innerCard.getRawValues();
    }

    @Override
    public Long getCurrentId() {
        return innerCard.getCurrentId();
    }

    @Override
    public Long getTenantId() {
        return innerCard.getTenantId();
    }

    @Override
    public Map<String, Object> getAllValuesAsMap() {
        return innerCard.getAllValuesAsMap();
    }

    @Override
    public boolean hasAttribute(String key) {
        return innerCard.hasAttribute(key);
    }

    @Override
    public Object get(String key) {
        return innerCard.get(key);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getAttributeValues() {
        return innerCard.getAttributeValues();
    }
        
}
