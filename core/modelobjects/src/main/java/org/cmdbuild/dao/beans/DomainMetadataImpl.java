/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import jakarta.annotation.Nullable;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.dao.entrytype.CascadeAction;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_AUTO;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeClassPermissionMode;
import static org.cmdbuild.dao.entrytype.Domain.DEFAULT_INDEX_VALUE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_SOURCE_FILTER_SIDE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_TARGET_FILTER_SIDE;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainMetadata;
import static org.cmdbuild.dao.entrytype.DomainMetadata.CARDINALITY;
import static org.cmdbuild.dao.entrytype.DomainMetadata.CASCADE_ACTION_INVERSE_ASK_CONFIRM;
import static org.cmdbuild.dao.entrytype.DomainMetadata.MASTERDETAIL_DISABLEDATTRS;
import org.cmdbuild.dao.utils.DomainUtils;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.blankToNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class DomainMetadataImpl extends EntryTypeMetadataImpl implements DomainMetadata {

    private final static Set<String> DOMAIN_METADATA_ATTRS = set(
            DESCRIPTION_1, DESCRIPTION_2, CARDINALITY,
            MASTERDETAIL, MASTERDETAIL_DESCRIPTION, MASTERDETAIL_FILTER, MASTERDETAIL_AGGREGATE, MASTERDETAIL_DISABLEDATTRS,
            DISABLED_1, DISABLED_2, INDEX_1, INDEX_2, INLINE_1, INLINE_2,
            DEFAULT_CLOSED_1, DEFAULT_CLOSED_2, SOURCE_FILTER, TARGET_FILTER,
            CASCADE_ACTION_DIRECT, CASCADE_ACTION_INVERSE, CASCADE_ACTION_DIRECT_ASK_CONFIRM, CASCADE_ACTION_INVERSE_ASK_CONFIRM,
            SOURCE_EDITABLE, TARGET_EDITABLE,
            IN_CLASS_REFERENCE_FILTERS_LEGACY, IN_CLASS_REFERENCE_FILTERS // TODO remove this, before 3.4.5
    ).immutable();

    private final String description1, description2, masterDetailDescription, masterDetailFilter;
    private final String sourceFilter, targetFilter;
    private final boolean isMasterDetail, isInline1, isDefaultClosed1, isInline2, isDefaultClosed2, cascadeActionDirectAskConfirm, cascadeActionInverseAskConfirm, sourceEditable, targetEditable;
    private final Set<String> disabled1, disabled2, masterDetailAggregateAttrs, masterDetailDisabledCreateAttrs;
    private final int index1, index2;
    private final DomainCardinality cardinality;
    private final CascadeAction cascadeActionDirect, cascadeActionInverse;

    public DomainMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(DOMAIN_METADATA_ATTRS::contains)));
        description1 = map.get(DESCRIPTION_1);
        description2 = map.get(DESCRIPTION_2);
        cardinality = Optional.ofNullable(blankToNull(map.get(CARDINALITY))).map(DomainUtils::parseDomainCardinality).orElse(MANY_TO_MANY);
        isMasterDetail = toBooleanOrDefault(map.get(MASTERDETAIL), false);
        masterDetailDescription = map.get(MASTERDETAIL_DESCRIPTION);
        masterDetailFilter = map.get(MASTERDETAIL_FILTER);
        masterDetailAggregateAttrs = parseSet(map.get(MASTERDETAIL_AGGREGATE));
        masterDetailDisabledCreateAttrs = parseSet(map.get(MASTERDETAIL_DISABLEDATTRS));

        String classReferenceFilter = firstNotNull(map.get(IN_CLASS_REFERENCE_FILTERS), "{}"); // TODO remove this, after 3.4.6
        String classReferenceFilterLegacy = firstNotNull(map.get(IN_CLASS_REFERENCE_FILTERS_LEGACY), "{}"); // TODO remove this, after 3.4.6

        sourceFilter = firstNotNullOrNull(
                map.get(SOURCE_FILTER),
                fromJson(classReferenceFilter, MAP_OF_STRINGS).get(DOMAIN_SOURCE_FILTER_SIDE),
                fromJson(classReferenceFilterLegacy, MAP_OF_STRINGS).get(DOMAIN_SOURCE_FILTER_SIDE)
        );
        targetFilter = firstNotNullOrNull(
                map.get(TARGET_FILTER),
                mapOf(String.class, String.class).with(fromJson(classReferenceFilter, MAP_OF_STRINGS)).get(DOMAIN_TARGET_FILTER_SIDE),
                mapOf(String.class, String.class).with(fromJson(classReferenceFilterLegacy, MAP_OF_STRINGS)).get(DOMAIN_TARGET_FILTER_SIDE)
        );

        disabled1 = parseSet(map.get(DISABLED_1));
        disabled2 = parseSet(map.get(DISABLED_2));
        index1 = toIntegerOrDefault(map.get(INDEX_1), DEFAULT_INDEX_VALUE);
        index2 = toIntegerOrDefault(map.get(INDEX_2), DEFAULT_INDEX_VALUE);
        isInline1 = toBooleanOrDefault(map.get(INLINE_1), false);
        isDefaultClosed1 = isInline1 && toBooleanOrDefault(map.get(DEFAULT_CLOSED_1), true);
        isInline2 = toBooleanOrDefault(map.get(INLINE_2), false);
        isDefaultClosed2 = isInline2 && toBooleanOrDefault(map.get(DEFAULT_CLOSED_2), true);
        cascadeActionDirect = parseEnumOrDefault(map.get(CASCADE_ACTION_DIRECT), CA_AUTO);
        cascadeActionInverse = parseEnumOrDefault(map.get(CASCADE_ACTION_INVERSE), CA_AUTO);
        cascadeActionDirectAskConfirm = toBooleanOrDefault(map.get(CASCADE_ACTION_DIRECT_ASK_CONFIRM), false);
        cascadeActionInverseAskConfirm = toBooleanOrDefault(map.get(CASCADE_ACTION_INVERSE_ASK_CONFIRM), false);
        sourceEditable = toBooleanOrDefault(map.get(SOURCE_EDITABLE), true);
        targetEditable = toBooleanOrDefault(map.get(TARGET_EDITABLE), true);
    }

    public DomainMetadataImpl() {
        this(emptyMap());
    }

    private static Set<String> parseSet(@Nullable String value) {
        return ImmutableSet.copyOf(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(value)));
    }

    @Override
    public boolean isSourceInline() {
        return isInline1;
    }

    @Override
    public boolean isSourceDefaultClosed() {
        return isDefaultClosed1;
    }

    @Override
    public boolean isTargetInline() {
        return isInline2;
    }

    @Override
    public boolean isTargetDefaultClosed() {
        return isDefaultClosed2;
    }

    @Override
    public String getDirectDescription() {
        return description1;
    }

    @Override
    public String getInverseDescription() {
        return description2;
    }

    @Override
    public DomainCardinality getCardinality() {
        return cardinality;
    }

    @Override
    public String getMasterDetailDescription() {
        return masterDetailDescription;
    }

    @Override
    public String getMasterDetailFilter() {
        return masterDetailFilter;
    }

    @Override
    public Collection<String> getMasterDetailAggregateAttrs() {
        return masterDetailAggregateAttrs;
    }

    @Override
    public Collection<String> getMasterDetailDisabledCreateAttrs() {
        return masterDetailDisabledCreateAttrs;
    }

    @Override
    public String getSourceFilter() {
        return sourceFilter;
    }

    @Override
    public String getTargetFilter() {
        return targetFilter;
    }

    @Override
    public boolean isMasterDetail() {
        return isMasterDetail;
    }

    @Override
    public Collection<String> getDisabledSourceDescendants() {
        return disabled1;
    }

    @Override
    public Collection<String> getDisabledTargetDescendants() {
        return disabled2;
    }

    @Override
    public int getIndexForSource() {
        return index1;
    }

    @Override
    public int getIndexForTarget() {
        return index2;
    }

    @Override
    public boolean getCascadeActionDirectAskConfirm() {
        return cascadeActionDirectAskConfirm;
    }

    @Override
    public boolean getCascadeActionInverseAskConfirm() {
        return cascadeActionInverseAskConfirm;
    }

    @Override
    public CascadeAction getCascadeActionDirect() {
        return cascadeActionDirect;
    }

    @Override
    public CascadeAction getCascadeActionInverse() {
        return cascadeActionInverse;
    }

    @Override
    public boolean isSourceEditable() {
        return sourceEditable;
    }

    @Override
    public boolean isTargetEditable() {
        return targetEditable;
    }

    public static DomainMetadataImplBuilder builder() {
        return new DomainMetadataImplBuilder();
    }

    public static DomainMetadataImplBuilder copyOf(DomainMetadata source) {
        return builder().with(source);
    }

    public static class DomainMetadataImplBuilder implements Builder<DomainMetadataImpl, DomainMetadataImplBuilder> {

        private final Map<String, String> metadata = map();

        public DomainMetadataImplBuilder with(String key, @Nullable Object value) {
            metadata.put(key, toStringOrNull(value));
            return this;
        }

        public DomainMetadataImplBuilder with(DomainMetadata source) {
            metadata.putAll(source.getAll());
            return this;
        }

        public DomainMetadataImplBuilder withCardinality(String value) {
            return this.with(CARDINALITY, value);
        }

        public DomainMetadataImplBuilder withCardinality(DomainCardinality domainCardinality) {
            return withCardinality(serializeDomainCardinality(domainCardinality));
        }

        public DomainMetadataImplBuilder withDescription(String value) {
            return this.with(DESCRIPTION, value);
        }

        public DomainMetadataImplBuilder withDirectDescription(String value) {
            return this.with(DESCRIPTION_1, value);
        }

        public DomainMetadataImplBuilder withInverseDescription(String value) {
            return this.with(DESCRIPTION_2, value);
        }

        public DomainMetadataImplBuilder withIsActive(Boolean value) {
            return this.with(ACTIVE, value);
        }

        public DomainMetadataImplBuilder withIsMasterDetail(Boolean value) {
            return this.with(MASTERDETAIL, value);
        }

        public DomainMetadataImplBuilder withSourceInline(Boolean value) {
            return this.with(INLINE_1, value);
        }

        public DomainMetadataImplBuilder withSourceDefaultClosed(Boolean value) {
            return this.with(DEFAULT_CLOSED_1, value);
        }

        public DomainMetadataImplBuilder withTargetInline(Boolean value) {
            return this.with(INLINE_2, value);
        }

        public DomainMetadataImplBuilder withTargetDefaultClosed(Boolean value) {
            return this.with(DEFAULT_CLOSED_2, value);
        }

        public DomainMetadataImplBuilder withDisabledSourceDescendants(Collection<String> value) {
            return this.with(DISABLED_1, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withDisabledTargetDescendants(Collection<String> value) {
            return this.with(DISABLED_2, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withMasterDetailDescription(String value) {
            return this.with(MASTERDETAIL_DESCRIPTION, value);
        }

        public DomainMetadataImplBuilder withMasterDetailFilter(String value) {
            return this.with(MASTERDETAIL_FILTER, value);
        }

        public DomainMetadataImplBuilder withMasterDetailAggregateAttrs(Collection<String> value) {
            return this.with(MASTERDETAIL_AGGREGATE, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withMasterDetailDisabledCreateAttrs(Collection<String> value) {
            return this.with(MASTERDETAIL_DISABLEDATTRS, Joiner.on(",").join(firstNonNull(value, emptyList())));
        }

        public DomainMetadataImplBuilder withSourceFilter(String value) {
            return this.with(SOURCE_FILTER, value);
        }

        public DomainMetadataImplBuilder withTargetFilter(String value) {
            return this.with(TARGET_FILTER, value);
        }

        public DomainMetadataImplBuilder withSourceIndex(Number value) {
            return this.with(INDEX_1, value);
        }

        public DomainMetadataImplBuilder withTargetIndex(Number value) {
            return this.with(INDEX_2, value);
        }

        public DomainMetadataImplBuilder withMode(ClassPermissionMode mode) {
            return this.with(ENTRY_TYPE_MODE, serializeClassPermissionMode(mode));
        }

        public DomainMetadataImplBuilder withCascadeActionDirect(CascadeAction value) {
            return this.with(CASCADE_ACTION_DIRECT, serializeEnum(value));
        }

        public DomainMetadataImplBuilder withCascadeActionInverse(CascadeAction value) {
            return this.with(CASCADE_ACTION_INVERSE, serializeEnum(value));
        }

        public DomainMetadataImplBuilder withCascadeActionDirectAskConfirm(Boolean value) {
            return this.with(CASCADE_ACTION_DIRECT_ASK_CONFIRM, value);
        }

        public DomainMetadataImplBuilder withCascadeActionInverseAskConfirm(Boolean value) {
            return this.with(CASCADE_ACTION_INVERSE_ASK_CONFIRM, value);
        }

        public DomainMetadataImplBuilder withCascadeAction(CascadeAction direct, CascadeAction inverse) {
            return this.withCascadeActionDirect(direct).withCascadeActionInverse(inverse);
        }

        public DomainMetadataImplBuilder withSourceEditable(Boolean value) {
            return this.with(SOURCE_EDITABLE, value);
        }

        public DomainMetadataImplBuilder withTargetEditable(Boolean value) {
            return this.with(TARGET_EDITABLE, value);
        }

        @Override
        public DomainMetadataImpl build() {
            return new DomainMetadataImpl(metadata);
        }

    }
}
