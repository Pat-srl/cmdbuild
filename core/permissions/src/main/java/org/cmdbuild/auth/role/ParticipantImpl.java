/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import org.cmdbuild.auth.role.Participant;
import org.cmdbuild.auth.role.ParticipantType;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class ParticipantImpl implements Participant {

    private final long id;
    private final ParticipantType type;

    public ParticipantImpl(ParticipantType type, long id) {
        this.id = checkNotNullAndGtZero(id);
        this.type = checkNotNull(type);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public ParticipantType getParticipantType() {
        return type;
    }

    @Override
    public boolean hasId() {
        return true;
    }

}
