/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

import org.cmdbuild.navtree.NavTree;

/**
 *
 * @author ataboga
 */
public interface AuthorizationVisitor {

    PermissionsResult visit(NavTree navTree);
}
