/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import static org.cmdbuild.auth.role.ParticipantType.PT_ROLE;

public interface RoleInfo extends Participant {

    String getName();

    String getDescription();

    @Override
    public default ParticipantType getParticipantType() {
        return PT_ROLE;
    }

}
