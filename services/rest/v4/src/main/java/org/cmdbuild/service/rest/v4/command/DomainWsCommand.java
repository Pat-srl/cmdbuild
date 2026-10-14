/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class DomainWsCommand {

    private final DaoService daoService;
    private final DomainRepository domainRepository;
    private final DomainSerializationHelper domainSerializationHelper;

    public DomainWsCommand(DaoService daoService, DomainRepository domainRepository, DomainSerializationHelper domainSerializationHelper) {
		this.daoService = checkNotNull(daoService);
		this.domainRepository = checkNotNull(domainRepository);
		this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
	}

    public List<Domain> doReadAll(Supplier<List<Domain>> function, String filterStr) {
        List<Domain> domainList = function.get();
        return filterDomains(domainList, filterStr);
    }

    public Domain doCreate(DomainSerializationHelper.WsDomainData data) {
        return domainRepository.createDomain(domainSerializationHelper.toDomainDefinition(data).build());
    }

    public Domain doUpdate(String domainId, DomainSerializationHelper.WsDomainData data) {
        Domain domain = domainRepository.getDomain(domainId);
        return domainRepository.updateDomain(domainSerializationHelper.toDomainDefinition(data).withOid(domain.getId()).build());
    }

    public void doDelete(String domainId) {
        domainRepository.deleteDomain(domainRepository.getDomain(domainId));
    }

	public List<Domain> filterDomains(List<Domain> listDomain, String filterStr) {
		CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
		filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
		if (filter.hasAttributeFilter()) {
			listDomain = AttributeFilterProcessor.<Domain>builder()
					.withKeyToValueFunction((key, domain) -> {
						return switch (key) {
							case SOURCE -> domain.getSourceClass();
							case DESTINATION -> domain.getTargetClass();
							case ACTIVE -> Boolean.toString(domain.isActive());
							case CARDINALITY -> serializeDomainCardinality(domain.getCardinality());
							default -> throw new IllegalArgumentException("unsupported filter key = " + key);
						};
					})
					.withConditionEvaluatorFunction(new AttributeFilterProcessor.ConditionEvaluatorFunction() {

						@Override
						public boolean evaluate(AttributeFilterCondition condition, Object value) {
							return switch (condition.getOperator()) {
								case EQUAL -> equal(valueToString(value), condition.getSingleValue());
								case IN -> condition.getValues().contains(valueToString(value));
								case CONTAIN ->
                                        ((Classe) value).equalToOrAncestorOf(daoService.getClasse(condition.getSingleValue())); //TODO filter also
								default ->
										throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
							};
						}

						private String valueToString(Object value) {
							if (value instanceof Classe classe) {
								return classe.getName();
							} else {
								return CmStringUtils.toStringOrNull(value);
							}
						}
					})
					.withFilter(filter.getAttributeFilter())
					.filter(listDomain);
		}
		return listDomain;
	}
}
