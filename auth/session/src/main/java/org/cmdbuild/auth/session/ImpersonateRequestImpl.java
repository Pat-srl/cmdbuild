/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ImpersonateRequestImpl implements ImpersonateRequest {

    private final String username;
    private final String group;
    private final String sponsor;
    private final Boolean atomic;

    public ImpersonateRequestImpl(@Nullable String username, @Nullable String group) {
        this(username, group, null, false);
    }

    public ImpersonateRequestImpl(@Nullable String username, @Nullable String group, @Nullable String sponsor) {
        this(username, group, sponsor, false);
    }

    public ImpersonateRequestImpl(@Nullable String username, @Nullable String group, @Nullable String sponsor, @Nullable Boolean atomic) {
        this.username = username;
        this.group = group;
        this.sponsor = sponsor;
        this.atomic = atomic;
        checkArgument(isNotBlank(username) || isNotBlank(group) || isNotBlank(sponsor));
    }

    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @Nullable
    public String getGroup() {
        return group;
    }

    @Nullable
    @Override
    public String getSponsor() {
        return sponsor;
    }

    @Override
    public boolean isTransient() {
        return atomic;
    }

    @Override
    public String toString() {
        return "ImpersonateRequest{" + "username=" + username + ", group=" + group + ", sponsor=" + sponsor + '}';
    }

}
