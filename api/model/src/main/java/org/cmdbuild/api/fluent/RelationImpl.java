package org.cmdbuild.api.fluent;

import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class RelationImpl implements Relation {

    private final String domainName;
    private CardDescriptor card1;
    private CardDescriptor card2;
    private final Map<String, Object> attributes;

    public RelationImpl(String domainName) {
        this(domainName, null, null, emptyMap());
    }

    public RelationImpl(String domainName, CardDescriptor card1, CardDescriptor card2) {
        this(domainName, card1, card2, emptyMap());
    }

    public RelationImpl(String domainName, @Nullable CardDescriptor card1, @Nullable CardDescriptor card2, Map<String, Object> attributes) {
        this.domainName = checkNotBlank(domainName);
        this.card1 = card1;
        this.card2 = card2;
        this.attributes = map(attributes);
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getClassName1() {
        return card1.getClassName();
    }

    @Override
    public long getCardId1() {
        return card1.getId();
    }

    @Override
    public RelationImpl setCard1(String className, long id) {
        this.card1 = new CardDescriptorImpl(className, id);
        return this;
    }

    @Override
    public String getClassName2() {
        return card2.getClassName();
    }

    @Override
    public long getCardId2() {
        return card2.getId();
    }

    @Override
    public RelationImpl setCard2(String className, long id) {
        this.card2 = new CardDescriptorImpl(className, id);
        return this;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Relation setAttribute(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
        return this;
    }

}
