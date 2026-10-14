/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.api;

import java.util.Set;
import jakarta.annotation.Nullable;

/**
 *
 */
public interface TenantLoginData {

	@Nullable
	Long getDefaultTenant();

	@Nullable
	Set<Long> getActiveTenants();

	@Nullable
	Boolean ignoreTenantPolicies();
}
