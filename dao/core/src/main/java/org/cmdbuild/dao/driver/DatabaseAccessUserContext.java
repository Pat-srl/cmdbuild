/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import java.util.Set;
import jakarta.annotation.Nullable;

public interface DatabaseAccessUserContext {

    boolean ignoreTenantPolicies();

    Set<Long> getTenantIds();

    @Nullable
    String getUsername();

    @Nullable
    String getRolename();

    @Nullable
    String getSessionId();

    @Nullable
    String getLanguage();

    DatabaseAccessUserScope getScope();

    DatabaseAccessUserContext withScope(DatabaseAccessUserScope scope);
}
