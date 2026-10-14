/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.inner.FlowConversionMode;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_FULL;
import org.cmdbuild.workflow.model.Process;

public interface RiverFlowConversionService {

    RiverFlow cardToRiverFlow(Flow card, FlowConversionMode mode);

    Flow copyRiverFlowDataToNewCard(Process process, RiverFlow riverFlow);

    Flow copyRiverFlowDataToCard(Flow card, RiverFlow riverFlow);

    default RiverFlow cardToRiverFlow(Flow card) {
        return cardToRiverFlow(card, CM_FULL);
    }
}
