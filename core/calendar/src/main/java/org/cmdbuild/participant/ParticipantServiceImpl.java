/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.participant;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.auth.role.Participant;
import static org.cmdbuild.auth.role.ParticipantType.PT_ROLE;
import static org.cmdbuild.auth.role.ParticipantType.PT_USER;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUserInfo;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.participant.ParticipantUtils.parseParticipants;
import org.springframework.stereotype.Component;

@Component
public class ParticipantServiceImpl implements ParticipantService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ParticipantServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @Override
    public List<LoginUserInfo> getUsers(Collection<String> participants) {
        return parseParticipants(participants).stream().filter(p -> p.isOfType(PT_USER)).map(this::getParticipant).map(LoginUserInfo.class::cast).collect(toImmutableList());
    }

    @Override
    public List<RoleInfo> getRoles(Collection<String> participants) {
        return parseParticipants(participants).stream().filter(p -> p.isOfType(PT_ROLE)).map(this::getParticipant).map(RoleInfo.class::cast).collect(toImmutableList());
    }

    @Override
    public List<Participant> getParticipants(Collection<String> participants) {
        return parseParticipants(participants).stream().map(this::getParticipant).collect(toImmutableList());
    }

    @Override
    public ParticipantHelper buildParticipants() {
        return ParticipantUtils.buildParticipants();
    }

    @Override
    public List<String> getParticipantsEmailAddresses(Collection<String> participants) {
        return parseParticipants(participants).stream().map(p -> {
            switch (p.getParticipantType()) {
                case PT_USER:
                    return userRepository.getUserById(p.getId()).getEmail();
                case PT_ROLE:
                    return roleRepository.getById(p.getId()).getEmail();
                default:
                    return null;
            }
        }).filter(StringUtils::isNotBlank).collect(toImmutableList());
    }

    private Participant getParticipant(Participant participant) {
        if (participant instanceof LoginUserInfo || participant instanceof RoleInfo) {
            return participant;
        } else {
            switch (participant.getParticipantType()) {
                case PT_ROLE:
                    return roleRepository.getById(participant.getId());
                case PT_USER:
                    return userRepository.getUserById(participant.getId());
                default:
                    throw new UnsupportedOperationException("unsupported participant type = " + participant.getParticipantType());
            }
        }
    }
}
