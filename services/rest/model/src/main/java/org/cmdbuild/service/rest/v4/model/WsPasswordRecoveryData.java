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
public class WsPasswordRecoveryData {

    private final String email;

    public WsPasswordRecoveryData(@JsonProperty("email") String email) {
        this.email = checkNotBlank(email, "missing 'email' param");
    }

    public String getEmail() {
        return email;
    }

}
