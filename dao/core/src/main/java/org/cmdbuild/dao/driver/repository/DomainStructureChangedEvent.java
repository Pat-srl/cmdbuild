/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import com.google.common.collect.ImmutableSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.Set;
import org.cmdbuild.dao.event.DaoEvent;

/**
 * domain structure changes; this should trigger invalidation of any cached domain
 * data
 */
public interface DomainStructureChangedEvent extends DaoEvent {

    boolean impactAllDomains();

    Set<Long> getAffectedDomainOids();

    public static DomainStructureChangedEvent affectingAll() {
        return AllDomainStructureChangedEventImpl.INSTANCE;
    }

    public static DomainStructureChangedEvent affecting(long oid) {
        return new DomainStructureChangedEventImpl(singleton(oid));
    }

    public static DomainStructureChangedEvent affecting(Iterable<Long> oids) {
        return new DomainStructureChangedEventImpl(oids);
    }

    public enum AllDomainStructureChangedEventImpl implements DomainStructureChangedEvent {
        INSTANCE;

        @Override
        public boolean impactAllDomains() {
            return true;
        }

        @Override
        public Set<Long> getAffectedDomainOids() {
            return emptySet();
        }
    }

    public class DomainStructureChangedEventImpl implements DomainStructureChangedEvent {

        private final Set<Long> affectedDomainOids;

        public DomainStructureChangedEventImpl(Iterable<Long> affectedDomainOids) {
            this.affectedDomainOids = ImmutableSet.copyOf(affectedDomainOids);
        }

        @Override
        public boolean impactAllDomains() {
            return false;
        }

        @Override
        public Set<Long> getAffectedDomainOids() {
            return affectedDomainOids;
        }

    }

}
