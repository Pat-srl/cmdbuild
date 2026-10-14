/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.participant;

import java.util.Collection;
import java.util.List;
import org.cmdbuild.auth.role.Participant;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUserInfo;

public interface ParticipantService {

    List<LoginUserInfo> getUsers(Collection<String> participants);

    List<RoleInfo> getRoles(Collection<String> participants);

    List<Participant> getParticipants(Collection<String> participants);

    ParticipantHelper buildParticipants();

    List<String> getParticipantsEmailAddresses(Collection<String> participants);

}
