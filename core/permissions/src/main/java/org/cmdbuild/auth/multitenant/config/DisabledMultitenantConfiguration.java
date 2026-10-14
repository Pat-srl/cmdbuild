/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.config;

import jakarta.annotation.Nullable;

public enum DisabledMultitenantConfiguration implements MultitenantConfiguration {

    INSTANCE;

    @Override
    public MultitenantMode getMultitenantMode() {
        return MultitenantMode.MTM_DISABLED;
    }

    @Override
    public String getTenantClass() throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDbFunction() throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public String getTenantDomain() {
        return null;
    }

    @Override
    public boolean tenantAdminIgnoresTenantByDefault() {
        return true;
    }

    @Override
    public String getTenantName() {
        return null;
    }
}
