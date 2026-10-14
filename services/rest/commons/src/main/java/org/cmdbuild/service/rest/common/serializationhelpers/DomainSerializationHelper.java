/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.CascadeAction;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_SOURCE_FILTER_SIDE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_TARGET_FILTER_SIDE;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.ecql.EcqlBindingInfo;

import static org.cmdbuild.dao.utils.DomainUtils.*;
import static org.cmdbuild.ecql.utils.EcqlUtils.buildDomainEcqlId;
import static org.cmdbuild.ecql.utils.EcqlUtils.buildDomainMasterDetailEcqlId;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlBindingInfoForExpr;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpression;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromDomainClassFilter;
import static org.cmdbuild.service.rest.common.serializationhelpers.EcqlFilterSerializationHelper.addEcqlFilter;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class DomainSerializationHelper {

    private final DaoService dao;
    private final ObjectTranslationService translationService;

    public DomainSerializationHelper(
            DaoService dao,
            ObjectTranslationService translationService) {
        this.dao = checkNotNull(dao);
        this.translationService = checkNotNull(translationService);
    }

    public FluentMap<String, Object> serializeBasicDomain(Domain domain) {
        return map("_id", domain.getName(),
                "name", domain.getName(),
                "description", domain.getDescription());
    }

    public FluentMap<String, Object> serializeDetailedDomain(Domain domain) {
        return serializeBasicDomain(domain).with("source", domain.getSourceClass().getName(),
                "sources", domain.getSourceClasses().stream().map(Classe::getName).sorted().collect(toList()),
                "sourceProcess", domain.getSourceClass().isProcess(),
                "destination", domain.getTargetClass().getName(),
                "destinations", domain.getTargetClasses().stream().map(Classe::getName).sorted().collect(toList()),
                "destinationProcess", domain.getTargetClass().isProcess(),
                "cardinality", serializeDomainCardinality(domain.getCardinality()),
                "descriptionDirect", domain.getDirectDescription(),
                "_descriptionDirect_translation", translationService.translateDomainDirectDescription(domain.getName(), domain.getDirectDescription()),
                "descriptionInverse", domain.getInverseDescription(),
                "_descriptionInverse_translation", translationService.translateDomainInverseDescription(domain.getName(), domain.getInverseDescription()),
                "indexDirect", domain.getIndexForSource(),
                "indexInverse", domain.getIndexForTarget(),
                "descriptionMasterDetail", domain.getMasterDetailDescription(),
                "_descriptionMasterDetail_translation", translationService.translateDomainMasterDetailDescription(domain.getName(), domain.getMasterDetailDescription()),
                "filterMasterDetail", domain.getMasterDetailFilter(),
                "isMasterDetail", domain.isMasterDetail(),
                "sourceInline", domain.getMetadata().isSourceInline(),
                "sourceDefaultClosed", domain.getMetadata().isSourceDefaultClosed(),
                "destinationInline", domain.getMetadata().isTargetInline(),
                "destinationDefaultClosed", domain.getMetadata().isTargetDefaultClosed(),
                "active", domain.isActive(),
                "disabledSourceDescendants", CmCollectionUtils.toList(domain.getDisabledSourceDescendants()),
                "disabledDestinationDescendants", CmCollectionUtils.toList(domain.getDisabledTargetDescendants()),
                "masterDetailAggregateAttrs", CmCollectionUtils.toList(domain.getMasterDetailAggregateAttrs()),
                "masterDetailDisabledCreateAttrs", CmCollectionUtils.toList(domain.getMasterDetailDisabledCreateAttrs()),
                "cascadeActionDirect", serializeEnum(domain.getMetadata().getCascadeActionDirect()),
                "cascadeActionInverse", serializeEnum(domain.getMetadata().getCascadeActionInverse()),
                "_cascadeActionDirect_actual", serializeEnum(getActualCascadeAction(domain, domain.getMetadata().getCascadeActionDirect(), RD_DIRECT)),
                "_cascadeActionInverse_actual", serializeEnum(getActualCascadeAction(domain, domain.getMetadata().getCascadeActionInverse(), RD_INVERSE)),
                "cascadeActionDirect_askConfirm", domain.getMetadata().getCascadeActionDirectAskConfirm(),
                "cascadeActionInverse_askConfirm", domain.getMetadata().getCascadeActionInverseAskConfirm(),
                "_can_create", domain.hasServicePermission(CP_UPDATE),
                "sourceEditable", domain.getMetadata().isSourceEditable(),
                "targetEditable", domain.getMetadata().isTargetEditable(),
                "sourceFilter", domain.getSourceFilter(),
                "targetFilter", domain.getTargetFilter())
                .accept(m -> attachEcqlFilterStuffIfApplicable(m, domain, DOMAIN_SOURCE_FILTER_SIDE))
                .accept(m -> attachEcqlFilterStuffIfApplicable(m, domain, DOMAIN_TARGET_FILTER_SIDE))
                .accept(m -> ecqlClassMasterDetailFilterStuff(m, domain));
    }

    public DomainDefinitionImpl.DomainDefinitionImplBuilder toDomainDefinition(WsDomainData domain) {
        return DomainDefinitionImpl.builder()
                .withName(domain.name)
                .withSourceClass(dao.getClasse(domain.source))
                .withTargetClass(dao.getClasse(domain.destination))
                .withMetadata(DomainMetadataImpl.builder()
                        .accept(domain.metadataFiller())
                        .build());
    }

    public List<FluentMap<String, Object>> serializeDomainList(List<Domain> domains, String classId, Boolean includeFullDetails, UserClassService userClassService) {
        FluentMap<String, Integer> domainIndex = getClassDomainsIndexes(domains, userClassService.getUserClass(classId));
        return domains.stream().sorted(Ordering.natural().onResultOf(d -> domainIndex.get(d.getName()))).map(d -> {
            return (includeFullDetails ? serializeDetailedDomain(d) : serializeBasicDomain(d)).with("_index", domainIndex.get(d.getName()));
        }).collect(toList());
    }

    public static class WsDomainData {

        private final String name, description, source, destination, cardinality, descriptionDirect, descriptionInverse, descriptionMasterDetail, filterMasterDetail, sourceFilter, targetFilter;
        private final Integer indexDirect, indexInverse;
        private final Boolean isActive, isMasterDetail, inline1, defaultClosed1, inline2, defaultClosed2, cascadeActionDirectAskConfirm, cascadeActionInverseAskConfirm, sourceEditable, tagetEditable;
        private final List<String> disabledSourceDescendants, disabledDestinationDescendants, masterDetailAggregateAttrs, masterDetailDisabledCreateAttrs;
        private final CascadeAction cascadeActionDirect, cascadeActionInverse;

        public WsDomainData(@JsonProperty("source") String source,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("destination") String destination,
                @JsonProperty("cardinality") String cardinality,
                @JsonProperty("descriptionDirect") String descriptionDirect,
                @JsonProperty("descriptionInverse") String descriptionInverse,
                @JsonProperty("indexDirect") Integer indexDirect,
                @JsonProperty("indexInverse") Integer indexInverse,
                @JsonProperty("descriptionMasterDetail") String descriptionMasterDetail,
                @JsonProperty("filterMasterDetail") String filterMasterDetail,
                @JsonProperty("disabledSourceDescendants") List<String> disabledSourceDescendants,
                @JsonProperty("disabledDestinationDescendants") List<String> disabledDestinationDescendants,
                @JsonProperty("masterDetailAggregateAttrs") List<String> masterDetailAggregateAttrs,
                @JsonProperty("masterDetailDisabledCreateAttrs") List<String> masterDetailDisabledCreateAttrs,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("isMasterDetail") Boolean isMasterDetail,
                @JsonProperty("sourceInline") Boolean inline1,
                @JsonProperty("sourceDefaultClosed") Boolean defaultClosed1,
                @JsonProperty("destinationInline") Boolean inline2,
                @JsonProperty("destinationDefaultClosed") Boolean defaultClosed2,
                @JsonProperty("cascadeActionDirect_askConfirm") Boolean cascadeActionDirectAskConfirm,
                @JsonProperty("cascadeActionInverse_askConfirm") Boolean cascadeActionInverseAskConfirm,
                @JsonProperty("cascadeActionDirect") String cascadeActionDirect,
                @JsonProperty("cascadeActionInverse") String cascadeActionInverse,
                @JsonProperty("sourceEditable") Boolean isSourceEditable,
                @JsonProperty("targetEditable") Boolean isTargetEditable,
                @JsonProperty("sourceFilter") String sourceFilter,
                @JsonProperty("targetFilter") String targetFilter) {
            this.source = checkNotBlank(source);
            this.destination = checkNotBlank(destination);
            this.cardinality = checkNotBlank(cardinality);
            this.descriptionDirect = descriptionDirect;
            this.descriptionInverse = descriptionInverse;
            this.indexDirect = indexDirect;
            this.indexInverse = indexInverse;
            this.descriptionMasterDetail = descriptionMasterDetail;
            this.filterMasterDetail = filterMasterDetail;
            this.isActive = isActive;
            this.isMasterDetail = isMasterDetail;
            this.inline1 = inline1;
            this.defaultClosed1 = defaultClosed1;
            this.inline2 = inline2;
            this.defaultClosed2 = defaultClosed2;
            this.name = name;
            this.description = description;
            this.disabledSourceDescendants = disabledSourceDescendants;
            this.disabledDestinationDescendants = disabledDestinationDescendants;
            this.masterDetailAggregateAttrs = masterDetailAggregateAttrs;
            this.masterDetailDisabledCreateAttrs = masterDetailDisabledCreateAttrs;
            this.cascadeActionDirectAskConfirm = cascadeActionDirectAskConfirm;
            this.cascadeActionInverseAskConfirm = cascadeActionInverseAskConfirm;
            this.cascadeActionDirect = parseEnumOrNull(cascadeActionDirect, CascadeAction.class);
            this.cascadeActionInverse = parseEnumOrNull(cascadeActionInverse, CascadeAction.class);
            this.sourceEditable = isSourceEditable;
            this.tagetEditable = isTargetEditable;
            this.sourceFilter = sourceFilter;
            this.targetFilter = targetFilter;
        }
        
        /**
         * Was done directly in {@link DomainSerializationHelper#toDomainDefinition(org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper.WsDomainData) }
         * 
         * @return 
         */
        public Consumer<DomainMetadataImpl.DomainMetadataImplBuilder> metadataFiller() {
            return (b) -> b.withCardinality(cardinality)
                            .withDescription(description)
                            .withDirectDescription(descriptionDirect)
                            .withInverseDescription(descriptionInverse)
                            .withIsActive(isActive)
                            .withIsMasterDetail(isMasterDetail)
                            .withDisabledSourceDescendants(disabledSourceDescendants)
                            .withDisabledTargetDescendants(disabledDestinationDescendants)
                            .withMasterDetailDescription(descriptionMasterDetail)
                            .withMasterDetailFilter(filterMasterDetail)
                            .withSourceIndex(indexDirect)
                            .withTargetIndex(indexInverse)
                            .withSourceInline(inline1)
                            .withSourceDefaultClosed(defaultClosed1)
                            .withTargetInline(inline2)
                            .withTargetDefaultClosed(defaultClosed2)
                            .withMasterDetailAggregateAttrs(masterDetailAggregateAttrs)
                            .withMasterDetailDisabledCreateAttrs(masterDetailDisabledCreateAttrs)
                            .withCascadeActionDirect(cascadeActionDirect)
                            .withCascadeActionInverse(cascadeActionInverse)
                            .withCascadeActionDirectAskConfirm(cascadeActionDirectAskConfirm)
                            .withCascadeActionInverseAskConfirm(cascadeActionInverseAskConfirm)
                            .withSourceEditable(sourceEditable)
                            .withTargetEditable(tagetEditable)
                            .withSourceFilter(sourceFilter)
                            .withTargetFilter(targetFilter);
        }  
    } // end WsDomainData class

    private void attachEcqlFilterStuffIfApplicable(FluentMap<String, Object> domainData, Domain domain, String filterSide) {
        String filter = domain.getFilterFromFilterSide(filterSide);
        if (isNotEmpty(filter)) {
            // Add output EcqlId
            EcqlBindingInfo ecqlBindingInfo = getEcqlBindingInfoForExpr(getEcqlExpressionFromDomainClassFilter(domain, filterSide));
            final String ecqlId = buildDomainEcqlId(domain, filterSide);

            EcqlFilterSerializationHelper.addEcqlFilter(domainData, format("ecql%s", StringUtils.capitalize(filterSide)), ecqlId, ecqlBindingInfo);
        }
    }

    private void ecqlClassMasterDetailFilterStuff(FluentMap<String, Object> domainData, Domain domain) {
        String filter = domain.getMasterDetailFilter();
        if (!domain.isMasterDetail() || isEmpty(filter)) {
            return;
        }

        EcqlBindingInfo ecqlBindingInfo = getEcqlBindingInfoForExpr(getEcqlExpression(domain.getMetadata(), filter));
        final String ecqlId = buildDomainMasterDetailEcqlId(domain);
        addEcqlFilter(domainData, "ecqlFilterMasterDetail", ecqlId, ecqlBindingInfo);
    }

}
