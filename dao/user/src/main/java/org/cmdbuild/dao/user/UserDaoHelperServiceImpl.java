/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.user;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.springframework.stereotype.Component;

@Component
public class UserDaoHelperServiceImpl implements UserDaoHelperService {

	private final OperationUserSupplier userSupplier;

	public UserDaoHelperServiceImpl(OperationUserSupplier userSupplier) {
		this.userSupplier = checkNotNull(userSupplier);
	}

	@Override
	public OperationUser getUser() {
		return userSupplier.getUser();
	}

}
