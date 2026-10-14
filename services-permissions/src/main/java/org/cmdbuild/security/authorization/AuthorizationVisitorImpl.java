/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Objects;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_DEPENDENCYTREE;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.navtree.NavTree;
import static org.cmdbuild.navtree.NavTreeType.NT_DEPENDENCYTREE;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class AuthorizationVisitorImpl implements AuthorizationVisitor {

    private final OperationUserSupplier userSupplier;

    public AuthorizationVisitorImpl(OperationUserSupplier userSupplier) {
        this.userSupplier = checkNotNull(userSupplier);
    }

    @Override
    public PermissionsResult visit(NavTree navTree) {
        boolean read = true, update = true;
        if (navTree.getActive()) {
            if (Objects.equals(NT_DEPENDENCYTREE, navTree.getType())) {
                PrivilegeSubject privilege = new PrivilegeSubject() {
                    @Override
                    public String getPrivilegeId() {
                        return privilegeId(PS_DEPENDENCYTREE, navTree.getName());
                    }
                };
                read = userSupplier.hasPrivileges(p -> p.hasReadAccess(privilege));
                update = userSupplier.hasPrivileges(p -> p.hasWriteAccess(privilege));
            }
            return new PermissionsResult(read, update);
        }
        return new PermissionsResult(false, false);
    }
}
