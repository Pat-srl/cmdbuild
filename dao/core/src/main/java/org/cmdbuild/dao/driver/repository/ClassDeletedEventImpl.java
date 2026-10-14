/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ClassDeletedEventImpl implements ClassDeletedEvent {

    private final Set<Long> affectedClassOids;

    public ClassDeletedEventImpl(Iterable<Long> affectedClassOids) {
        this.affectedClassOids = ImmutableSet.copyOf(affectedClassOids);
    }

    @Override
    public boolean impactAllClasses() {
        return false;
    }

    @Override
    public Set<Long> getAffectedClassOids() {
        return affectedClassOids;
    }

}
