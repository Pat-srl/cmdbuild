/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import jakarta.inject.Provider;
import org.cmdbuild.auth.session.dao.SessionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SessionDataServiceImpl extends GenericSessionDataService implements SessionDataService {

    public SessionDataServiceImpl(Provider<SessionRepository> sessionRepository, CurrentSessionHolder currentSessionIdHolder) {// TODO: provider injection is not great, it should be possibile to refactor session repo and remove the dependency loop
        super(s -> sessionRepository.get().getSessionByIdOrNull(s), currentSessionIdHolder);
    }

}
