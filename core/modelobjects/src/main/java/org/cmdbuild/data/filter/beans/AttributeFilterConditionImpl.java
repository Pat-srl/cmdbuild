/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import java.lang.invoke.MethodHandles;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterConditionOperator;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.FALSE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeFilterConditionImpl implements AttributeFilterCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AttributeFilterConditionOperator operator;
    private final String key, className;
    private final List<String> values;

    private AttributeFilterConditionImpl(AttributeFilterConditionBuilder builder) {
        AttributeFilterConditionOperator candidateOperator = checkNotNull(builder.operator, "filter operator cannot be null");
        this.key = checkNotBlank(builder.key, "filter key cannot be blank");
        this.className = builder.className;
        this.values = switch (candidateOperator) {
            case ISNULL, ISNOTNULL, FALSE, TRUE ->
                emptyList();
            case EQUAL, LIKE, GREATER, BEGIN, END, LESS, NOTBEGIN, NOTEND, NOTEQUAL -> {
                String value = toStringOrNull(getOnlyElement(builder.values, null));
                if (isBlank(value)) {
                    LOGGER.warn(marker(), "filter value cannot be blank for key =< {} > operator = {} (will replace operator with FALSE filter)", key, candidateOperator);
                    candidateOperator = FALSE;
                    yield emptyList();
                } else {
                    yield singletonList(value);
                }
            }
            default ->
                ImmutableList.copyOf(transform(checkNotNull(builder.values), CmStringUtils::toStringOrNull)); //TODO validate values for other operators
        };
        this.operator = checkNotNull(candidateOperator);
    }

    public AttributeFilterImpl toAttributeFilter() {
        return AttributeFilterImpl.simple(this);
    }

    @Override
    public boolean hasClassName() {
        return !isBlank(className);
    }

    @Override
    public String getClassName() {
        return checkNotBlank(className);
    }

    @Override
    public AttributeFilterConditionOperator getOperator() {
        return operator;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "AttributeFilterConditionImpl{" + "operator=" + operator + ", key=" + key + ", className=" + className + ", values=" + values + '}';
    }

    public static AttributeFilterConditionBuilder builder() {
        return new AttributeFilterConditionBuilder();
    }

    public static AttributeFilterConditionBuilder copyOf(AttributeFilterCondition source) {
        return new AttributeFilterConditionBuilder()
                .withOperator(source.getOperator())
                .withKey(source.getKey())
                .withClassName(source.hasClassName() ? source.getClassName() : null)
                .withValues(source.getValues());
    }

    public static AttributeFilterConditionImpl eq(String key, Object value) {
        return builder().eq().withKey(key).withValues(value).build();
    }

    public static AttributeFilterConditionImpl in(String key, Object... values) {
        return in(key, set(values));
    }

    public static AttributeFilterConditionImpl in(String key, Iterable values) {
        return builder().withOperator(AttributeFilterConditionOperator.IN).withKey(key).withValues(values).build();
    }

    public static class AttributeFilterConditionBuilder implements Builder<AttributeFilterConditionImpl, AttributeFilterConditionBuilder> {

        private AttributeFilterConditionOperator operator;
        private String key, className;
        private Collection<Object> values;

        public AttributeFilterConditionBuilder withOperator(AttributeFilterConditionOperator operator) {
            this.operator = operator;
            return this;
        }

        public AttributeFilterConditionBuilder withOperator(String operator) {
            return this.withOperator(parseEnum(operator, AttributeFilterConditionOperator.class));
        }

        public AttributeFilterConditionBuilder eq() {
            return this.withOperator(AttributeFilterConditionOperator.EQUAL);
        }

        public AttributeFilterConditionBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public AttributeFilterConditionBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public AttributeFilterConditionBuilder withValues(Object... values) {
            return this.withValues(asList(values));
        }

        public AttributeFilterConditionBuilder withValues(Iterable values) {
            this.values = toList(values);
            return this;
        }

        @Override
        public AttributeFilterConditionImpl build() {
            return new AttributeFilterConditionImpl(this);
        }

    }
}
