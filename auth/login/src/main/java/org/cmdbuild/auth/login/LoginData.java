/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import org.cmdbuild.auth.user.SessionTypeInfo;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.multitenant.api.TenantLoginData;
import org.cmdbuild.ui.TargetDevice;

public interface LoginData extends TenantLoginData, SessionTypeInfo {

    String getLoginString();

    String getPassword();

    @Nullable
    String getLoginGroupName();

    boolean noPersist();

    boolean isPasswordRequired();

    boolean isServiceUsersAllowed();

    boolean forceUserGroup();

    @Nullable
    TargetDevice getTargetDevice();

}
