/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.user.UserData.USER_CLASS_NAME;
import static org.cmdbuild.common.Constants.ROLE_CLASS_NAME;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class UserRoleRepositoryImpl implements UserRoleRepository {

	private final DaoService dao;

	public UserRoleRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public void addRoleToUser(long userId, long roleId) {
		dao.create(RelationImpl.builder()
				.withType(dao.getDomain("UserRole"))
				.withSourceCard(card(USER_CLASS_NAME, userId))
				.withTargetCard(card(ROLE_CLASS_NAME, roleId)).build());
	}

	@Override
	public void removeRoleFromUser(long userId, long roleId) {
		dao.getJdbcTemplate().update("UPDATE \"Map_UserRole\" SET \"Status\" = 'N' WHERE \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"Status\" = 'A'", userId, roleId); //TODO relation service ??
	}

}
