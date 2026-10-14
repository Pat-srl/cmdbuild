/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import org.cmdbuild.auth.role.Participant;
import org.cmdbuild.auth.role.ParticipantType;
import static org.cmdbuild.auth.role.ParticipantType.PT_USER;

public interface LoginUserInfo extends Participant {

    String getUsername();

    @Override
    public default ParticipantType getParticipantType() {
        return PT_USER;
    }

}
