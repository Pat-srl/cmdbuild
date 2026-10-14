/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import java.time.ZonedDateTime;

public interface RiverPlanVersionInfo {

    ZonedDateTime getLastUpdate();

    String getPlanId();

}
