/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilterForClass;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * @author ldare
 */
@Component
public class RoleClassFilterWsCommand {

    private final RoleRepository roleRepository;
    private final CardFilterService cardFilterService;

    public RoleClassFilterWsCommand(RoleRepository roleRepository, CardFilterService cardFilterService) {
        this.roleRepository = checkNotNull(roleRepository);
        this.cardFilterService = checkNotNull(cardFilterService);
    }

    public List<CardFilterAsDefaultForClass> doRead(String roleId) {
        Role role = roleRepository.getByNameOrId(roleId);
        return cardFilterService.getDefaultFiltersForRole(role.getId());
    }

    public void doUpdateWithPost(String roleId, List<WsDefaultStoredFilterForClass> filters) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<CardFilterAsDefaultForClass> filtersUpdate = filters.stream().map(f -> new CardFilterAsDefaultForClassImpl(cardFilterService.getById(f.getId()), f.getForClass(), role.getId())).collect(toList());
        cardFilterService.setDefaultFiltersForRole(role.getId(), filtersUpdate);
    }
}
