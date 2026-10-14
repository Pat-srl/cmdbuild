/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.model.FlowImpl;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import org.cmdbuild.workflow.dao.CardToFlowCardWrapperService;
import org.cmdbuild.workflow.model.ProcessImpl;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.workflow.utils.WfRiverUtils.RIVER_FAKE_PACKAGE;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.toFlowStatus;

@Component
public class RiverCardToFlowCardWrapperServiceImpl implements CardToFlowCardWrapperService {

    private final ProcessRepository planClasseRepository;

    public RiverCardToFlowCardWrapperServiceImpl(ProcessRepository planClasseRepository) {
        this.planClasseRepository = checkNotNull(planClasseRepository);
    }

    @Override
    public Flow cardToFlowCard(DatabaseRecord card) {
        String planId = getPlanId((Card) card);
        Process process = planClasseRepository.getPlanClasseByClassAndPlanId(((Card) card).getClassName(), planId);
        process = ProcessImpl.copyOf(process).withInner(((Card) card).getType()).build();
        return FlowImpl.builder().withCard((Card) card).withFlowStatus(toFlowStatus(card.get(ATTR_FLOW_STATUS, LookupValue.class))).withPlan(process).build();
    }

    public static String getPlanId(Card card) {
        String value = checkNotBlank(card.get(ATTR_PLAN_INFO, String.class));
        PlanInfo planInfo = PlanInfoImpl.deserialize(value);
        checkArgument(planInfo.getPackageId().equals(RIVER_FAKE_PACKAGE));
        return planInfo.getDefinitionId();
    }

}
