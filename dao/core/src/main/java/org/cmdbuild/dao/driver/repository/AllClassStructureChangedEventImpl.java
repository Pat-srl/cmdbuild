/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static java.util.Collections.emptySet;
import java.util.Set;

public enum AllClassStructureChangedEventImpl implements ClassStructureChangedEvent {
	INSTANCE;

	@Override
	public boolean impactAllClasses() {
		return true;
	}

	@Override
	public Set<Long> getAffectedClassOids() {
		return emptySet();
	}
}
