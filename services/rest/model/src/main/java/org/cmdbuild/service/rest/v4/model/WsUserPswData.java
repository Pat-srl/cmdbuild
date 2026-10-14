/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsUserPswData {

    private final String password, oldpassword;

    public WsUserPswData(@JsonProperty("password") String password, @JsonProperty("oldpassword") String oldpassword) {
        this.password = checkNotBlank(password, "missing 'password' param");
        this.oldpassword = checkNotBlank(oldpassword, "missing 'oldpassword' param");
    }

    public String getPassword() {
        return password;
    }

    public String getOldpassword() {
        return oldpassword;
    }

}