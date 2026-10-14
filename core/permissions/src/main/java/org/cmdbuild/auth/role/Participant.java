/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;

public interface Participant {

    @Nullable
    Long getId();

    ParticipantType getParticipantType();

    default boolean isOfType(ParticipantType participantType) {
        return equal(getParticipantType(), participantType);
    }

    default boolean hasId() {
        return getId() != null;
    }
}
