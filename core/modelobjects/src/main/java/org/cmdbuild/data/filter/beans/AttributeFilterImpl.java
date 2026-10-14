/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import com.google.common.collect.Iterables;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import java.util.List;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.AttributeFilter.AttributeFilterMode;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.TRUE;
import org.cmdbuild.data.filter.CmdbFilter;

public class AttributeFilterImpl implements AttributeFilter {

    private final AttributeFilterMode mode;
    private final List<AttributeFilter> elements;
    private final AttributeFilterCondition condition;

    private AttributeFilterImpl(AttributeFilterMode mode, List<AttributeFilter> elements, AttributeFilterCondition condition) {
        this.mode = checkNotNull(mode);
        switch (mode) {
            case AND, OR -> {
                this.elements = copyOf(checkNotNull(elements));
                checkArgument(elements.size() > 1, "element list must be of size >=2 for filter mode = %s", mode);
                this.condition = null;
            }
            case SIMPLE -> {
                this.elements = null;
                this.condition = checkNotNull(condition);
            }
            case NOT -> {
                this.elements = singletonList(Iterables.getOnlyElement(checkNotNull(elements)));
                this.condition = null;
            }
            default ->
                throw new UnsupportedOperationException(format("unsupported filter mode = %s", mode));
        }
    }

    @Override
    public CmdbFilter toCmdbFilters() {
        return CmdbFilterImpl.build(this);
    }

    @Override
    public AttributeFilterMode getMode() {
        return mode;
    }

    @Override
    public List<AttributeFilter> getElements() {
        checkArgument(!isSimple());
        return elements;
    }

    @Override
    public AttributeFilterCondition getCondition() {
        checkArgument(isSimple());
        return condition;
    }

    @Override
    public String toString() {
        return "AttributeFilterImpl{" + "mode=" + mode + ", elements=" + elements + ", condition=" + condition + '}';
    }

    public static AttributeFilterImpl build(AttributeFilterMode mode, List<AttributeFilter> elements) {
        return new AttributeFilterImpl(mode, elements, null);
    }

    public static AttributeFilterImpl simple(AttributeFilterCondition condition) {
        return new AttributeFilterImpl(AttributeFilterMode.SIMPLE, null, condition);
    }

    public static AttributeFilter and(AttributeFilter... elements) {
        return and(asList(elements));
    }

    public static AttributeFilter and(List<AttributeFilter> elements) {
        return elements.size() == 1 ? Iterables.getOnlyElement(elements) : new AttributeFilterImpl(AttributeFilterMode.AND, elements, null);
    }

    public static AttributeFilter or(List<AttributeFilter> elements) {
        return elements.size() == 1 ? Iterables.getOnlyElement(elements) : new AttributeFilterImpl(AttributeFilterMode.OR, elements, null);
    }

    public static AttributeFilter andOr(AttributeFilterMode mode, List<AttributeFilter> elements) {
        return elements.size() == 1 ? Iterables.getOnlyElement(elements) : new AttributeFilterImpl(mode, elements, null);
    }

    public static AttributeFilterImpl not(AttributeFilter elements) {
        return new AttributeFilterImpl(AttributeFilterMode.NOT, singletonList(elements), null);
    }

    public static AttributeFilter empty() {
        return simple(AttributeFilterConditionImpl.builder().withKey("_DUMMY_KEY_").withOperator(TRUE).build());
    }
}
