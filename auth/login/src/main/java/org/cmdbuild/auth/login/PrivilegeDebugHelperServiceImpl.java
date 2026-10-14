package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.fullAccess;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUserImpl;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import org.springframework.stereotype.Component;

@Component("privilegeDebugHelperService")
public class PrivilegeDebugHelperServiceImpl implements PrivilegeDebugHelperService {

    private final AuthenticationService authenticationService;
    private final RoleRepository groupRepository;

    public PrivilegeDebugHelperServiceImpl(AuthenticationService authenticationService, RoleRepository groupRepository) {
        this.authenticationService = checkNotNull(authenticationService);
        this.groupRepository = checkNotNull(groupRepository);
    }

    @Override
    public String dumpDebugInfoForGroup(String groupName, @Nullable String filter) {
        OperationUser operationUser = authenticationService.buildOperationUser(LoginDataImpl.buildNoPasswordRequired("dummy"),
                LoginUserImpl.builder().withUsername("dummy").addGroup(groupRepository.getGroupWithName(groupName)).withAvailableTenantContext(fullAccess()).build());
        return "privileges for group =< %s > :\n%s".formatted(groupName, list(operationUser.getPrivilegeContext().getAllPrivileges().entrySet())
                .sorted(e -> e.getKey())
                .filter(e -> isBlank(filter) || Pattern.compile(filter).matcher(e.getKey()).find())
                .map(e -> {
                    StringBuilder val = new StringBuilder("%-32s\n\tservice: %s\n\tui     : %s\n\tcustom :".formatted(
                            e.getKey(),
                            list(e.getValue().getMinPrivilegesForAllRecords().getServicePrivileges()).map(CmConvertUtils::serializeEnum).sorted(),
                            list(e.getValue().getMinPrivilegesForAllRecords().getUiPrivileges()).map(CmConvertUtils::serializeEnum).sorted(),
                            mapToLoggableStringInline(e.getValue().getMinPrivilegesForAllRecords().getCustomPrivileges())));
                    if (e.getValue().hasPrivilegesWithFilter()) {
                        e.getValue().getPrivilegeGroupsWithFilter().forEach(g -> {
                            val.append("\tfilter =< %s >\n".formatted(abbreviate(serializeFilter(g.getFilter()))))
                                    .append("\t\tservice: %s\n\t\tui     : %s\n\t\tcustom :".formatted(
                                            e.getKey(),
                                            list(g.getServicePrivileges()).map(CmConvertUtils::serializeEnum).sorted(),
                                            list(g.getUiPrivileges()).map(CmConvertUtils::serializeEnum).sorted(),
                                            mapToLoggableStringInline(g.getCustomPrivileges())));
                        });
                    }
                    return val.toString();
                })
                .collect(joining("\n")));
    }

}
