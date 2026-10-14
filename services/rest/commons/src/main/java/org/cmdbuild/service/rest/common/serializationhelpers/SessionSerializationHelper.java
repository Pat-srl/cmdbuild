/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Comparator;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.*;
import org.springframework.stereotype.Component;

/**
 * @author ldare
 */
@Component
public class SessionSerializationHelper {

    private final MultitenantService multitenantService;
    private final ObjectTranslationService translationService;

    public SessionSerializationHelper(MultitenantService multitenantService, ObjectTranslationService translationService) {
        this.multitenantService = multitenantService;
        this.translationService = translationService;
    }

    public Object serializeSession(Session session) {
        return serializeSession(session, false, false);
    }

    public CmMapUtils.FluentMap<String, ?> serializeSession(Session session, @Nullable Boolean includeExtendedData, boolean includeId) {
        OperationUser user = session.getOperationUser();
        return (CmMapUtils.FluentMap) map(
                "_id", "current",
                "username", user.getLoginUser().getUsername(),
                "userId", user.getLoginUser().getId(),
                "userDescription", user.getLoginUser().getDescription(),
                "role", user.getDefaultGroupNameOrNull(),
                "availableRoles", user.getLoginUser().getGroupNames(),
                "multigroup", user.getLoginUser().hasMultigroupEnabled(),
                "rolePrivileges", user.getRolePrivilegesAsMap().entrySet().stream().filter(Map.Entry::getValue).collect(toMap((e) -> serializeEnum(e.getKey()), Map.Entry::getValue)),
                "beginDate", toIsoDateTime(session.getBeginDate()),
                "lastActive", toIsoDateTime(session.getLastActiveDate()),
                "device", serializeEnum(session.getTargetDevice()),
                "sessionType", serializeEnum(user.getSessionType())
        ).accept((m) -> {
            if (includeId) {
                m.put("_id", session.getSessionId());
            }
            if (multitenantService.isEnabled()) {
                m.put("availableTenants", user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds(),
                        "tenant", user.getUserTenantContext().getDefaultTenantId(),
                        "activeTenants", user.getUserTenantContext().getActiveTenantIds(),
                        "canIgnoreTenants", user.getLoginUser().getAvailableTenantContext().ignoreTenantPolicies(),
                        "ignoreTenants", user.getUserTenantContext().ignoreTenantPolicies(),
                        "multiTenantActivationPrivileges", serializeEnum(user.getLoginUser().getAvailableTenantContext().getTenantActivationPrivileges())
                );
            }
            if (firstNonNull(includeExtendedData, false)) {
                m.put(
                        "availableRolesExtendedData", user.getLoginUser().getRoleInfos().stream().map((g) -> map(
                        "_id", g.getId(),
                        "code", g.getName(),
                        "description", g.getDescription(),
                        "_description_translation", translationService.translateRoleDescription(g.getName(), g.getDescription())
                )).collect(toList()),
                        "availableTenantsExtendedData", multitenantService.getTenantDescriptions(user.getLoginUser().getAvailableTenantContext().getAvailableTenantIds())
                                .entrySet().stream()
                                .sorted(Comparator.comparing(Map.Entry::getValue))
                                .map((e) -> map("code", e.getKey(), "description", firstNonNull(trimToNull(e.getValue()), format("tenant #%s", e.getKey())))).collect(toList()));
            }
        });
    }

    public static CmMapUtils.FluentMap<String, Object> serializeGroupOfPrivileges(GroupOfPrivileges privileges) {
        return (CmMapUtils.FluentMap) map(
                "source", privileges.getSource(),
                "privileges", privileges.getServicePrivileges().stream().sorted(Comparator.naturalOrder()).map(CmConvertUtils::enumToString).collect(toList())
        ).skipNullValues().with(
                "attributePrivileges", emptyToNull(map(privileges.getAttributePrivileges()).mapValues((v) -> v.stream().sorted(Comparator.naturalOrder()).map(CmConvertUtils::enumToString))));
    }
}
