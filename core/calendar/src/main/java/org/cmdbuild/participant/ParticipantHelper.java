/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.participant;

import java.util.List;
import org.cmdbuild.auth.user.LoginUserInfo;

public interface ParticipantHelper {

    ParticipantHelper addUsers(LoginUserInfo... users);

    ParticipantHelper addUsers(long... userIds);

    ParticipantHelper addRoles(long... groupIds);
    
    ParticipantHelper addRoles(Iterable<Long> groupIds);

    List<String> toParticipants();

}
