package org.cmdbuild.view.join;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import static org.cmdbuild.dao.utils.CmSorterUtils.serializeSorter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNoDuplicates;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.view.join.JoinViewPrivilegeMode.JVPM_DEFAULT;

@JsonInclude()
@JsonIgnoreProperties()
public class JoinViewConfigImpl implements JoinViewConfig {

    private final String masterClass, masterClassAlias, iconFont;
    private final CmdbFilter filter;
    private final CmdbSorter sorter;
    private final List<JoinElement> joinElements;
    private final List<JoinAttribute> attributes;
    private final List<JoinAttributeGroup> attributeGroups;
    private final JoinViewPrivilegeMode privilegeMode;

    private JoinViewConfigImpl(JoinViewConfigImplBuilder builder) {
        this(builder.toMap());
    }

    @JsonCreator
    public JoinViewConfigImpl(Map<String, ?> config) {
        masterClass = toStringNotBlank(config.get("masterClass"));
        masterClassAlias = toStringNotBlank(config.get("masterClassAlias"));
        filter = parseFilter(toStringOrNull(config.get("filter")));
        sorter = parseSorter(toStringOrNull(config.get("sorter")));
        joinElements = unflattenListOfMaps(config, "join").stream().map(JoinElementImpl::new).collect(toImmutableList());
        attributeGroups = unflattenListOfMaps(config, "attributeGroups").stream().map(JoinAttributeGroupImpl::new).collect(toImmutableList());
        attributes = unflattenListOfMaps(config, "attributes").stream().map(JoinAttributeImpl::new).collect(toImmutableList());
        privilegeMode = parseEnumOrDefault(toStringOrNull(config.get("privilegeMode")), JVPM_DEFAULT);
        iconFont = toStringOrNull(config.get("iconFont"));

        checkNoDuplicates(list(joinElements, JoinElement::getTargetAlias).with(masterClassAlias).with(joinElements, JoinElement::getDomainAlias));
        checkNoDuplicates(list(attributes, JoinAttribute::getName));
        checkNoDuplicates(list(attributeGroups, JoinAttributeGroup::getName));

        attributes.stream().filter(JoinAttribute::hasGroup).map(JoinAttribute::getGroup).forEach(g -> checkArgument(list(attributeGroups, JoinAttributeGroup::getName).contains(g), "missing attribute group =< %s >", g));
    }

    @Override
    @JsonIgnore
    public String getMasterClass() {
        return masterClass;
    }

    @Override
    @JsonIgnore
    public String getMasterClassAlias() {
        return masterClassAlias;
    }

    @Override
    @JsonIgnore
    public CmdbFilter getFilter() {
        return filter;
    }

    @Override
    @JsonIgnore
    public CmdbSorter getSorter() {
        return sorter;
    }

    @Override
    @JsonIgnore
    public List<JoinElement> getJoinElements() {
        return joinElements;
    }

    @Override
    @JsonIgnore
    public List<JoinAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @JsonIgnore
    public List<JoinAttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    @Override
    @JsonIgnore
    public JoinViewPrivilegeMode getPrivilegeMode() {
        return privilegeMode;
    }

    @Override
    @JsonIgnore
    public String getIconFont() {
        return iconFont;
    }

    @JsonAnyGetter
    public Map<String, String> toMap() {
        return copyOf(this).toMap();
    }

    public static JoinViewConfigImplBuilder builder() {
        return new JoinViewConfigImplBuilder();
    }

    public static JoinViewConfigImplBuilder copyOf(JoinViewConfig source) {
        return new JoinViewConfigImplBuilder()
                .withMasterClass(source.getMasterClass())
                .withMasterClassAlias(source.getMasterClassAlias())
                .withFilter(source.getFilter())
                .withSorter(source.getSorter())
                .withJoinElements(source.getJoinElements())
                .withAttributes(source.getAttributes())
                .withAttributeGroups(source.getAttributeGroups())
                .withPrivilegeMode(source.getPrivilegeMode());
    }

    public static class JoinViewConfigImplBuilder implements Builder<JoinViewConfigImpl, JoinViewConfigImplBuilder> {

        private String masterClass;
        private String masterClassAlias;
        private CmdbFilter filter;
        private CmdbSorter sorter;
        private final List<JoinElement> joinElements = list();
        private final List<JoinAttribute> attributes = list();
        private final List<JoinAttributeGroup> attributeGroups = list();
        private JoinViewPrivilegeMode privilegeMode;
        private String iconFont;

        public Map<String, String> toMap() {
            return flattenMaps((Map) map().skipNullValues().with(
                    "masterClass", masterClass,
                    "masterClassAlias", masterClassAlias,
                    "filter", serializeFilter(filter),
                    "sorter", serializeSorter(sorter),
                    "join", list(joinElements).map(e -> ((JoinElementImpl) e).toMap()),
                    "attributes", list(attributes).map(e -> ((JoinAttributeImpl) e).toMap()),
                    "attributeGroups", list(attributeGroups).map(e -> ((JoinAttributeGroupImpl) e).toMap()),
                    "privilegeMode", serializeEnum(privilegeMode),
                    "icon_font", iconFont));
        }

        public JoinViewConfigImplBuilder withMasterClass(String masterClass) {
            this.masterClass = masterClass;
            return this;
        }

        public JoinViewConfigImplBuilder withMasterClassAlias(String masterClassAlias) {
            this.masterClassAlias = masterClassAlias;
            return this;
        }

        public JoinViewConfigImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public JoinViewConfigImplBuilder withSorter(CmdbSorter sorter) {
            this.sorter = sorter;
            return this;
        }

        public JoinViewConfigImplBuilder withJoinElements(List<JoinElement> joinElements) {
            this.joinElements.clear();
            this.joinElements.addAll(firstNotNull(joinElements, emptyList()));
            return this;
        }

        public JoinViewConfigImplBuilder withAttributes(List<JoinAttribute> attributes) {
            this.attributes.clear();
            this.attributes.addAll(firstNotNull(attributes, emptyList()));
            return this;
        }

        public JoinViewConfigImplBuilder withAttributeGroups(List<JoinAttributeGroup> attributeGroups) {
            this.attributeGroups.clear();
            this.attributeGroups.addAll(firstNotNull(attributeGroups, emptyList()));
            return this;
        }

        public JoinViewConfigImplBuilder withJoinElement(JoinElement joinElement) {
            this.joinElements.add(joinElement);
            return this;
        }

        public JoinViewConfigImplBuilder withAttribute(JoinAttribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public JoinViewConfigImplBuilder withAttributeGroup(JoinAttributeGroup attributeGroup) {
            this.attributeGroups.add(attributeGroup);
            return this;
        }

        public JoinViewConfigImplBuilder withPrivilegeMode(JoinViewPrivilegeMode privilegeMode) {
            this.privilegeMode = privilegeMode;
            return this;
        }

        public JoinViewConfigImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        @Override
        public JoinViewConfigImpl build() {
            return new JoinViewConfigImpl(this);
        }

    }
}
