/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.FkDomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.FkDomain;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class FkDomainWsCommand {

    private final FkDomainRepository fkDomainRepository;
    private final DaoService daoService;

    public FkDomainWsCommand(FkDomainRepository fkDomainRepository, DaoService daoService) {
        this.fkDomainRepository = checkNotNull(fkDomainRepository);
        this.daoService = checkNotNull(daoService);
    }

    public List<FkDomain> fetchFkDomains(String filterStr) {
        List<FkDomain> listFkDomain = fkDomainRepository.getAllFkDomains();
//                isAdminViewMode(viewMode) ? domainService.getUserDomains() : domainService.getActiveUserDomains(); TODO user access (?)
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasAttributeFilter()) {
            listFkDomain = AttributeFilterProcessor.<FkDomain>builder()
                    .withKeyToValueFunction((key, domain) -> {
                        return switch (key) {
                            case SOURCE ->
                                domain.getSourceClass();
                            case DESTINATION ->
                                domain.getTargetClass();
                            case "cardinality" ->
                                serializeDomainCardinality(domain.getCardinality());
                            case "isMasterDetail" ->
                                domain.isMasterDetail();
                            default ->
                                throw illegalArgument("unsupported filter key = %s", key);
                        };
                    })
                    .withConditionEvaluatorFunction(new AttributeFilterProcessor.ConditionEvaluatorFunction() {

                        @Override
                        public boolean evaluate(AttributeFilterCondition condition, Object value) {
                            return switch (condition.getOperator()) {
                                case EQUAL ->
                                    equal(valueToString(value), condition.getSingleValue());
                                case IN ->
                                    condition.getValues().contains(valueToString(value));
                                case CONTAIN ->
                                        ((Classe) value).equalToOrAncestorOf(daoService.getClasse(condition.getSingleValue())); //TODO filter also
                                default ->
                                    throw illegalArgument("unsupported operator = %s", condition.getOperator());
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
                    .filter(listFkDomain);
        }
        return listFkDomain;
    }
}
