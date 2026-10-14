package org.cmdbuild.auth.role;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.cmdbuild.auth.grant.Grant;
import org.cmdbuild.auth.role.RolePrivilegeUtils.ProcessedRolePrivileges;
import static org.cmdbuild.auth.role.RoleType.DEFAULT;
import static org.cmdbuild.auth.role.RoleTypeUtils.parseRoleType;
import static org.cmdbuild.auth.role.RoleTypeUtils.serializeRoleType;
import static org.cmdbuild.common.Constants.ROLE_CLASS_NAME;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_EMAIL;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@CardMapping(ROLE_CLASS_NAME)
public class RoleImpl implements Role {

    private final Long id;
    private final String name;
    private final String description;
    private final String email;
    private final List<Grant> privileges;
    private final boolean active;
    private final RoleType type;
    private final GroupConfig config;
    private final Map<String, Boolean> customPrivileges;
    private final Set<RolePrivilege> allPrivileges;

    private RoleImpl(RoleImplBuilder builder) {
        this.id = builder.id;
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.email = builder.email;
        this.privileges = list(builder.privileges).immutable();
        this.active = firstNotNull(builder.active, true);
        this.config = firstNotNull(builder.config, GroupConfigImpl.builder().build());
        this.type = firstNotNull(builder.type, DEFAULT);

        Map<RolePrivilege, Boolean> parsedCustomPrivileges = builder.customPrivileges == null ? emptyMap() : builder.customPrivileges.entrySet().stream().collect(toMap((e) -> convert(format("rp_%s", checkNotBlank(e.getKey()).toLowerCase().replaceFirst("^rp_", "")).toUpperCase(), RolePrivilege.class), (e) -> checkNotNull(e.getValue())));
        ProcessedRolePrivileges processRolePrivileges = RolePrivilegeUtils.processRolePermissions(type, parsedCustomPrivileges);

        this.allPrivileges = ImmutableSet.copyOf(processRolePrivileges.getRolePrivileges());
        this.customPrivileges = processRolePrivileges.getCustomPrivileges().entrySet().stream().collect(toMap((e) -> e.getKey().name().toLowerCase().replaceFirst("^rp_", ""), Entry::getValue)).immutable();
    }

    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    public List<Grant> getAllPrivileges() {
        return privileges;
    }

    @Override
    @CardAttr(ATTR_EMAIL)
    public String getEmail() {
        return email;
    }

    @Override
    public RoleType getType() {
        return type;
    }

    @CardAttr("Type")
    public String getTypeStr() {
        return serializeRoleType(type);
    }

    @Override
    @CardAttr("Config")
    public GroupConfig getConfig() {
        return config;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return active;
    }

    @Override
    public Set<RolePrivilege> getRolePrivileges() {
        return allPrivileges;
    }

    @Override
    @CardAttr("Permissions")//TODO rename column
    @JsonBean  //TODO replace this with attribute awareness - check card attribute type, and use that to identify json data
    public Map<String, Boolean> getCustomPrivileges() {
        return customPrivileges;
    }

    @Override
    public String toString() {
        return "RoleImpl{" + "name=" + name + '}';
    }

    public static RoleImplBuilder builder() {
        return new RoleImplBuilder();
    }

    public static RoleImplBuilder copyOf(Role role) {
        return builder()
                .withActive(role.isActive())
                .withConfig(role.getConfig())
                .withDescription(role.getDescription())
                .withEmail(role.getEmail())
                .withId(role.getId())
                .withName(role.getName())
                .withType(role.getType())
                .withPrivileges(role.getAllPrivileges())
                .withCustomPrivileges(role.getCustomPrivileges());
    }

    public static class RoleImplBuilder implements Builder<RoleImpl, RoleImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private String email;
        private final Collection<Grant> privileges = list();
        private Boolean active;
        private GroupConfig config;
        private RoleType type;
        private Map<String, Boolean> customPrivileges;

        public RoleImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public RoleImplBuilder withConfig(GroupConfig config) {
            this.config = config;
            return this;
        }

        public RoleImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public RoleImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public RoleImplBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RoleImplBuilder withType(RoleType roleType) {
            this.type = roleType;
            return this;
        }

        public RoleImplBuilder withTypeStr(String roleType) {
            return this.withType(parseRoleType(roleType));
        }

        public RoleImplBuilder withCustomPrivileges(Map<String, Boolean> customPrivileges) {
            this.customPrivileges = customPrivileges;
            return this;
        }

        public RoleImplBuilder withPrivileges(Collection<Grant> privileges) {
            this.privileges.addAll(privileges);
            return this;
        }

        public RoleImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        @Override
        public RoleImpl build() {
            return new RoleImpl(this);
        }
    }
}
