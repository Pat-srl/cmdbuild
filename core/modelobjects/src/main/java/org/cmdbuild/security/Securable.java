/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author ataboga
 */
public interface Securable {

    @JsonIgnore
    public boolean isReadable();

    @JsonIgnore
    public boolean isUpdatable();
}
