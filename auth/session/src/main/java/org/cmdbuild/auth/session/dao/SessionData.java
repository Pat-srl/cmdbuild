/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.SessionExpirationStrategy;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean;

public interface SessionData {

    @Nullable
    Long getId();

    String getSessionId();

    SessionDataJsonBean getData();

    ZonedDateTime getLoginDate();

    ZonedDateTime getLastActiveDate();

    @Nullable
    ZonedDateTime getExpirationDate();

    SessionExpirationStrategy getExpirationStrategy();

}
