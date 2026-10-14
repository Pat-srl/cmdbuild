/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import static org.cmdbuild.workflow.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;

@Component
public class FlowPersistenceServiceImpl implements FlowPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FlowCardRepository cardRepository;
    private final ProcessRepository classeRepository;
    private final RiverFlowConversionService conversionService;

    public FlowPersistenceServiceImpl(FlowCardRepository cardRepository, ProcessRepository classeRepository, RiverFlowConversionService conversionService) {
        this.cardRepository = checkNotNull(cardRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.conversionService = checkNotNull(conversionService);
    }

    @Override
    public Flow createFlowCard(RiverFlow flow) {
        logger.debug("create flow card for flow = {}", flow);
        Process classe = classeRepository.getProcessClassByName((flow.getPlan().getAttr(ATTR_BIND_TO_CLASS)));
        return cardRepository.create(conversionService.copyRiverFlowDataToNewCard(classe, flow));
    }

    @Override
    public Flow updateFlowCard(RiverFlow flow) {
        Flow card = cardRepository.getFlowCardByPlanIdAndFlowId(flow.getPlanId(), flow.getId());
        return updateFlowCard(card, flow);
    }

    @Override
    public Flow updateFlowCard(Flow card, RiverFlow flow) {
        logger.debug("update flow card for flow = {} card = {}", flow, card);
        return cardRepository.update(conversionService.copyRiverFlowDataToCard(card, flow));
    }

}
