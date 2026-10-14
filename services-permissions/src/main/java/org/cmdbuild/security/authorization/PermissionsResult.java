/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

/**
 *
 * @author ataboga
 */
public record PermissionsResult(
        boolean readable,
        boolean updatable
        ) {

}
