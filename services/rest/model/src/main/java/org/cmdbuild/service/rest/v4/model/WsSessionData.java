/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import org.cmdbuild.ui.TargetDevice;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsSessionData {

    public final String username, password, role, scope;
    public final Long defaultTenant;
    public final boolean ignoreTenants;
    public final List<Long> activeTenants;
    public final TargetDevice device;

    public WsSessionData(@JsonProperty("username") String username,
                         @JsonProperty("password") String password,
                         @JsonProperty("role") String role,
                         @JsonProperty("scope") String scope,
                         @JsonProperty("device") String device,
                         @JsonProperty("tenant") Long defaultTenant,
                         @JsonProperty("ignoreTenants") Boolean ignoreTenants,
                         @JsonProperty("activeTenants") List<Long> activeTenants) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.scope = scope;
        this.device = parseEnumOrNull(device, TargetDevice.class);
        this.defaultTenant = defaultTenant;
        this.activeTenants = activeTenants == null ? emptyList() : ImmutableList.copyOf(activeTenants);
        this.ignoreTenants = firstNonNull(ignoreTenants, false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Long getDefaultTenant() {
        return defaultTenant;
    }

    public List<Long> getActiveTenants() {
        return activeTenants;
    }

    @Nullable
    public TargetDevice getDevice() {
        return device;
    }

}
