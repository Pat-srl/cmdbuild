/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.river.engine.RiverFlow;

public interface FlowPersistenceService {

    Flow updateFlowCard(RiverFlow flow);

    Flow updateFlowCard(Flow card, RiverFlow flow);

    Flow createFlowCard(RiverFlow flow);
}
