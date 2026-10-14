/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import java.util.Map;
import java.util.Objects;

public interface SkedJob extends Runnable {

    String getCode();

    String getTrigger();

    Map<String, String> getConfig();

    SkedJobClusterMode getClusterMode();

    SkedJobTriggerType getTriggerType();

    Runnable getRunnable();

    @Override
    default void run() {
        getRunnable().run();
    }

    default boolean hasClusterMode(SkedJobClusterMode mode) {
        return Objects.equals(mode, getClusterMode());
    }

    enum SkedJobTriggerType {
        ST_CRON, ST_TIMESTAMP
    }
}
