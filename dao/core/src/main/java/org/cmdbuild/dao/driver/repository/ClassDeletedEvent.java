/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static java.util.Collections.singleton;

public interface ClassDeletedEvent extends ClassStructureChangedEvent {

    public static ClassDeletedEvent affecting(long oid) {
        return new ClassDeletedEventImpl(singleton(oid));
    }
}
