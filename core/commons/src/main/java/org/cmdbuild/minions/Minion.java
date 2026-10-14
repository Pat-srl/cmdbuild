/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static org.cmdbuild.minions.MinionUtils.normalizeMinionId;

public interface Minion extends MinionStatusInfo {

    boolean isEnabled();

    void startService();

    void stopService();

    boolean canStart();

    boolean canStop();

    boolean isHidden();

    default String getId() {
        return normalizeMinionId(getName());
    }
}
