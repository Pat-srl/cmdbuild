/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import java.util.Collection;
import static org.cmdbuild.minions.SystemStatus.SYST_NOT_RUNNING;
import static org.cmdbuild.minions.SystemStatus.SYST_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_READY_RESTART_REQUIRED;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_DATABASE_CONFIGURATION;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_PATCH_MANAGER;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface MinionService extends MinionBeanRepository {

    ZonedDateTime getStartupDateTime();

    SystemStatus getSystemStatus();

    void startSystem();

    void stopSystem();

    Collection<Minion> getMinions();

    Minion getMinion(String id);

    default void startMinion(String id) {
        getMinion(id).startService();
    }

    default void stopMinion(String id) {
        getMinion(id).stopService();
    }

    default boolean isSystemReady() {
        return equal(getSystemStatus(), SYST_READY);
    }

    default boolean isSystemRequiresRestart() {
        return equal(getSystemStatus(), SYST_READY_RESTART_REQUIRED);
    }

    default boolean isSystemStandby() {
        return equal(getSystemStatus(), SYST_NOT_RUNNING);
    }

    default boolean isWaitingForUser() {
        return hasStatus(SYST_WAITING_FOR_DATABASE_CONFIGURATION, SYST_WAITING_FOR_PATCH_MANAGER);
    }

    default boolean hasStatus(SystemStatus... any) {
        return set(any).contains(getSystemStatus());
    }

}
