/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.navtree.NavTree;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationVisitor visitor;

    public AuthorizationServiceImpl(AuthorizationVisitor visitor) {
        this.visitor = checkNotNull(visitor);
    }

    @Override
    public <T> boolean canRead(T obj) {
        return switch (obj) {
            case NavTree navTree ->
                visitor.visit(navTree).readable();
            default ->
                false;
        };
    }

    @Override
    public <T> boolean canUpdate(T obj) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
