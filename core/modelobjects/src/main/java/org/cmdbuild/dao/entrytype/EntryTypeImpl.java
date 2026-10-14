package org.cmdbuild.dao.entrytype;

import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Streams.stream;
import java.util.Collection;
import static java.util.Collections.emptyList;

import java.util.Map;
import static java.util.function.Function.identity;
import jakarta.annotation.Nullable;

import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public abstract class EntryTypeImpl implements EntryType {

    private final String name;
    private final Long id;
    private final Map<String, Attribute> attributes;
    private final Map<String, AttributeGroupData> attributeGroups;

    protected EntryTypeImpl(String name, Long oid, Iterable<? extends AttributeWithoutOwner> attributes, Iterable<? extends AttributeGroupData> attributeGroups) {
        this.name = checkNotBlank(name);
        this.id = oid;
        this.attributes = stream(attributes).map(a -> AttributeImpl.copyOf(a).withOwner(EntryTypeImpl.this).build()).collect(toImmutableMap(AttributeWithoutOwner::getName, identity()));
        this.attributeGroups = uniqueIndex((Collection<AttributeGroupData>) firstNotNull(attributeGroups, emptyList()), AttributeGroupData::getName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public Collection<Attribute> getAllAttributes() {
        return attributes.values();
    }

    @Override
    public Map<String, Attribute> getAllAttributesAsMap() {
        return attributes;
    }

    @Override
    public Map<String, AttributeGroupData> getAttributeGroupsAsMap() {
        return attributeGroups;
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntryType)) {
            return false;
        }
        EntryType other = (EntryType) obj;
        if ((long) getId() != (long) other.getId()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntryType{" + "name=" + name + ", id=" + id + '}';
    }

}
