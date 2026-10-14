package org.cmdbuild.api.fluent;

import java.util.ArrayList;
import java.util.List;

public class ActiveQueryRelations extends RelationsQuery {

    private final FluentApiExecutor executor;

    ActiveQueryRelations(FluentApiExecutor executor, String className, Long id) {
        super(className, id);
        this.executor = executor;
    }

    protected FluentApiExecutor executor() {
        return executor;
    }

    public ActiveQueryRelations withDomain(final String domainName) {
        setDomainName(domainName);
        return this;
    }

    public List<CardDescriptor> fetch() {
        List<CardDescriptor> descriptors = new ArrayList<>();
        List<Relation> relations = executor().fetch(this);
        relations.forEach((relation) -> {
            descriptors.add(descriptorFrom(relation));
        });
        return descriptors;
    }

    private CardDescriptor descriptorFrom(Relation relation) {
        String className;
        long id;
        if (getCardId() == relation.getCardId1()) {
            className = relation.getClassName2();
            id = relation.getCardId2();
        } else {
            className = relation.getClassName1();
            id = relation.getCardId1();
        }
        return new CardDescriptorImpl(className, id);
    }

}
