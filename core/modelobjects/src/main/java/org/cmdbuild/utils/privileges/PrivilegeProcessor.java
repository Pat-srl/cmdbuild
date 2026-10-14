/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.privileges;

import java.util.Set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface PrivilegeProcessor<P> {

    default Set<P> expandPrivileges(P... privileges) {
        return expandPrivileges(list(privileges));
    }

    Set<P> expandPrivileges(Iterable<P> privileges);

    Set<P> mergePrivileges(Iterable<P> first, Iterable<P> second);

    Set<P> expandPrivilegesBackwards(Iterable<P> privileges);

}
