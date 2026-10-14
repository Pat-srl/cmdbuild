/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.role.RoleInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class RoleInfoImpl implements RoleInfo {

    public static final RoleInfo ANONYMOUS_LOGIN_ROLE = new RoleInfoImpl("anonymous");

    private final Long id;
    private final String name, description;

    public RoleInfoImpl(@Nullable Long id, String name, @Nullable String description) {
        this.id = id;
        this.name = checkNotBlank(name);
        this.description = firstNotBlank(name, description);
    }

    public RoleInfoImpl(String name) {
        this(null, name, name);
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RoleInfoImpl{" + "id=" + id + ", name=" + name + '}';
    }

}
