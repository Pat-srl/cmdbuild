/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import java.time.ZonedDateTime;

public interface SkedEnv {

    boolean isMasterNode();

    default boolean isMasterNodeForJob(SkedJob job, ZonedDateTime fireTimestamp) {
        return isMasterNode();
    }

}
