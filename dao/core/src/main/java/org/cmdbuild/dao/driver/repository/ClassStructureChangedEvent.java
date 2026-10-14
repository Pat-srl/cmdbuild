/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static java.util.Collections.singleton;
import java.util.Set;
import org.cmdbuild.dao.event.DaoEvent;

/**
 * class structure changes; this should trigger invalidation of any cached class
 * data
 */
public interface ClassStructureChangedEvent extends DaoEvent {

    boolean impactAllClasses();

    Set<Long> getAffectedClassOids();

    public static ClassStructureChangedEvent affectingAll() {
        return AllClassStructureChangedEventImpl.INSTANCE;
    }

    public static ClassStructureChangedEvent affecting(long oid) {
        return new ClassStructureChangedEventImpl(singleton(oid));
    }

    public static ClassStructureChangedEvent affecting(Iterable<Long> oids) {
        return new ClassStructureChangedEventImpl(oids);
    }

}
