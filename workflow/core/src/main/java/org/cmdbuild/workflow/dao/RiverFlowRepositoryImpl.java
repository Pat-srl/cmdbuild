/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.data.RiverFlowRepository;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.model.Flow;
import org.springframework.stereotype.Component;

@Component
public class RiverFlowRepositoryImpl implements RiverFlowRepository {

	private final FlowCardRepository cardRepository;
	private final RiverFlowConversionService conversionService;
	private final DaoService dao;

	public RiverFlowRepositoryImpl(FlowCardRepository cardRepository, RiverFlowConversionService conversionService, DaoService dao) {
		this.cardRepository = checkNotNull(cardRepository);
		this.conversionService = checkNotNull(conversionService);
		this.dao = checkNotNull(dao);
	}

	@Override
	public RiverFlow getFlowById(String flowId) {
		return getOnlyElement(dao.getJdbcTemplate().query("SELECT \"Id\" _id,\"IdClass\" _class FROM \"Activity\" WHERE \"ProcessCode\" = ? AND \"Status\" = 'A'", (r, i) -> {//TODO rewrite this, avoid duplicate query, avoid query on Activity
			long cardId = r.getLong("_id"), classId = r.getLong("_class");
			String className = dao.getClasse(classId).getName();
			Flow flow = cardRepository.getFlowCardByClasseIdAndCardId(className, cardId);
			return conversionService.cardToRiverFlow(flow);
		}, checkNotBlank(flowId)));
	}

}
