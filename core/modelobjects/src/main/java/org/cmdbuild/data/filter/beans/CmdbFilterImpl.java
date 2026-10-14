/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.CqlFilter;
import org.cmdbuild.data.filter.FulltextFilter;
import org.cmdbuild.data.filter.RelationFilter;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import org.cmdbuild.data.filter.AttachmentFilter;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CompositeFilter;
import org.cmdbuild.data.filter.ContextFilter;
import org.cmdbuild.data.filter.EcqlFilter;
import org.cmdbuild.data.filter.FunctionFilter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.CmFilterUtils.expandFulltextFilterForAttrs;
import static org.cmdbuild.dao.utils.CmFilterUtils.mapNamesInFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.mapValuesInFilter;

public class CmdbFilterImpl implements CmdbFilter {

    private static final CmdbFilter FALSE_FILTER = new CmdbFilterImpl(true);

    private final AttributeFilter attributeFilter;
    private final RelationFilter relationFilter;
    private final FulltextFilter fulltextFilter;
    private final CqlFilter cqlFilter;
    private final AttachmentFilter attachmentFilter;
    private final EcqlFilter ecqlFilter;
    private final FunctionFilter functionFilter;
    private final CompositeFilter compositeFilter;
    private final ContextFilter contextFilter;
    private final boolean isFalse;

    private CmdbFilterImpl(boolean isFalse) {
        checkArgument(isFalse);
        this.isFalse = true;
        this.attributeFilter = null;
        this.relationFilter = null;
        this.fulltextFilter = null;
        this.cqlFilter = null;
        this.ecqlFilter = null;
        this.functionFilter = null;
        this.compositeFilter = null;
        this.attachmentFilter = null;
        this.contextFilter = null;
    }

    private CmdbFilterImpl(CmdbFilterBuilder builder) {
        this.attributeFilter = builder.attributeFilter;
        this.relationFilter = builder.relationFilter;
        this.fulltextFilter = builder.fulltextFilter;
        this.cqlFilter = builder.cqlFilter;
        this.ecqlFilter = builder.ecqlFilter;
        this.functionFilter = builder.functionFilter;
        this.compositeFilter = builder.compositeFilter;
        this.attachmentFilter = builder.attachmentFilter;
        this.contextFilter = builder.contextFilter;
        checkArgument(ecqlFilter == null || cqlFilter == null, "cannot set both cqlFilter and ecqlFilter");
        checkArgument(compositeFilter == null || (attributeFilter == null && contextFilter == null && relationFilter == null && cqlFilter == null && ecqlFilter == null && functionFilter == null && attachmentFilter == null), "cannot set both composite filter and any other filter type");
        this.isFalse = false;
    }

    public static CmdbFilter falseFilter() {
        return FALSE_FILTER;
    }

    @Override
    public boolean isFalse() {
        return isFalse;
    }

    @Override
    public FunctionFilter getFunctionFilter() {
        return checkNotNull(functionFilter);
    }

    @Override
    public AttributeFilter getAttributeFilter() {
        return checkNotNull(attributeFilter);
    }

    @Override
    public RelationFilter getRelationFilter() {
        return checkNotNull(relationFilter);
    }

    @Override
    public FulltextFilter getFulltextFilter() {
        return checkNotNull(fulltextFilter);
    }

    @Override
    public CqlFilter getCqlFilter() {
        return checkNotNull(cqlFilter);
    }

    @Override
    public EcqlFilter getEcqlFilter() {
        return checkNotNull(ecqlFilter);
    }

    @Override
    public CompositeFilter getCompositeFilter() {
        return checkNotNull(compositeFilter);
    }

    @Override
    public AttachmentFilter getAttachmentFilter() {
        return checkNotNull(attachmentFilter);
    }

    @Override
    public ContextFilter getContextFilter() {
        return checkNotNull(contextFilter);
    }

    @Override
    public boolean hasContextFilter() {
        return contextFilter != null;
    }

    @Override
    public boolean hasAttributeFilter() {
        return attributeFilter != null;
    }

    @Override
    public boolean hasRelationFilter() {
        return relationFilter != null;
    }

    @Override
    public boolean hasFulltextFilter() {
        return fulltextFilter != null;
    }

    @Override
    public boolean hasCqlFilter() {
        return cqlFilter != null;
    }

    @Override
    public boolean hasEcqlFilter() {
        return ecqlFilter != null;
    }

    @Override
    public boolean hasFunctionFilter() {
        return functionFilter != null;
    }

    @Override
    public boolean hasCompositeFilter() {
        return compositeFilter != null;
    }

    @Override
    public boolean hasAttachmentFilter() {
        return attachmentFilter != null;
    }

    @Override
    public CmdbFilter mapNames(Function<String, String> map) {
        return mapNamesInFilter(this, map);
    }

    @Override
    public CmdbFilter mapValues(Map<String, String> map) {
        return mapValuesInFilter(this, map);
    }

    @Override
    public CmdbFilter expandFulltextFilter(Collection<String> attributes) {
        return expandFulltextFilterForAttrs(this, attributes);
    }

    @Override
    public String toString() {
        return format("CmdbFilterImpl{noop:%s, false: %s, asJson=%s}", isNoop(), isFalse(), CmFilterUtils.serializeFilter(this));//TODO composite filter is serializable with this method? fix
    }

    public static CmdbFilterBuilder builder() {
        return new CmdbFilterBuilder();
    }

    public static CmdbFilter build(AttributeFilter attributeFilter) {
        return builder().withAttributeFilter(attributeFilter).build();
    }

    private final static CmdbFilter NOOP = builder().build();

    public static CmdbFilter noopFilter() {
        return NOOP;
    }
    
    public static boolean isNoop(CmdbFilter filter) {
        return NOOP == filter;
    }

    public static CmdbFilterBuilder copyOf(CmdbFilter filter) {
        return new CmdbFilterBuilder()
                .withAttributeFilter(((CmdbFilterImpl) filter).attributeFilter)
                .withFunctionFilter(((CmdbFilterImpl) filter).functionFilter)
                .withRelationFilter(((CmdbFilterImpl) filter).relationFilter)
                .withFulltextFilter(((CmdbFilterImpl) filter).fulltextFilter)
                .withAttachmentFilter(((CmdbFilterImpl) filter).attachmentFilter)
                .withCqlFilter(((CmdbFilterImpl) filter).cqlFilter)
                .withEcqlFilter(((CmdbFilterImpl) filter).ecqlFilter)
                .withCompositeFilter(((CmdbFilterImpl) filter).compositeFilter)
                .withContextFilter(((CmdbFilterImpl) filter).contextFilter);
    }

    public static class CmdbFilterBuilder implements Builder<CmdbFilterImpl, CmdbFilterBuilder> {

        private AttributeFilter attributeFilter;
        private RelationFilter relationFilter;
        private FulltextFilter fulltextFilter;
        private CqlFilter cqlFilter;
        private EcqlFilter ecqlFilter;
        private FunctionFilter functionFilter;
        private CompositeFilter compositeFilter;
        private AttachmentFilter attachmentFilter;
        private ContextFilter contextFilter;

        public CmdbFilterBuilder withFunctionFilter(FunctionFilter functionFilter) {
            this.functionFilter = functionFilter;
            return this;
        }

        public CmdbFilterBuilder withContextFilter(ContextFilter contextFilter) {
            this.contextFilter = contextFilter;
            return this;
        }

        public CmdbFilterBuilder withAttachmentFilter(AttachmentFilter attachmentFilter) {
            this.attachmentFilter = attachmentFilter;
            return this;
        }

        public CmdbFilterBuilder withAttributeFilter(AttributeFilter attributeFilter) {
            this.attributeFilter = attributeFilter;
            return this;
        }

        public CmdbFilterBuilder withRelationFilter(RelationFilter relationFilter) {
            this.relationFilter = relationFilter;
            return this;
        }

        public CmdbFilterBuilder withFulltextFilter(String fulltextFilter) {
            return withFulltextFilter(new FulltextFilterImpl(fulltextFilter));
        }

        public CmdbFilterBuilder withFulltextFilter(FulltextFilter fulltextFilter) {
            this.fulltextFilter = fulltextFilter;
            return this;
        }

        public CmdbFilterBuilder withCqlFilter(String cqlFilter) {
            return withCqlFilter(CqlFilterImpl.build(cqlFilter));
        }

        public CmdbFilterBuilder withCqlFilter(CqlFilter cqlFilter) {
            this.cqlFilter = cqlFilter;
            return this;
        }

        public CmdbFilterBuilder withEcqlFilter(EcqlFilter ecqlFilter) {
            this.ecqlFilter = ecqlFilter;
            return this;
        }

        public CmdbFilterBuilder withCompositeFilter(CompositeFilter compositeFilter) {
            this.compositeFilter = compositeFilter;
            return this;
        }

        @Override
        public CmdbFilterImpl build() {
            return new CmdbFilterImpl(this);
        }

    }
}
