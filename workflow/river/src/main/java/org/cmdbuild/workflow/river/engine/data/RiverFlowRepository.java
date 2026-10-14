/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.data;

import org.cmdbuild.workflow.river.engine.RiverFlow;

public interface RiverFlowRepository {

    RiverFlow getFlowById(String flowId);

}
