/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.Integer.MAX_VALUE;
import java.util.List;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.CascadeAction;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_AUTO;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_RESTRICT;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_SETNULL;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_ONE_TO_MANY;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_ONE_TO_ONE;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_ONE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class DomainUtils {

    public static String serializeDomainCardinality(DomainCardinality cardinality) {
        return switch (cardinality) {
            case ONE_TO_ONE ->
                DOMAIN_ONE_TO_ONE;
            case ONE_TO_MANY ->
                DOMAIN_ONE_TO_MANY;
            case MANY_TO_ONE ->
                DOMAIN_MANY_TO_ONE;
            case MANY_TO_MANY ->
                DOMAIN_MANY_TO_MANY;
            default ->
                throw new UnsupportedOperationException();
        };
    }

    public static DomainCardinality parseDomainCardinality(String cardinality) {
        return switch (nullToEmpty(cardinality).toUpperCase().trim()) {
            case DOMAIN_ONE_TO_ONE ->
                ONE_TO_ONE;
            case DOMAIN_ONE_TO_MANY ->
                ONE_TO_MANY;
            case DOMAIN_MANY_TO_ONE ->
                MANY_TO_ONE;
            case DOMAIN_MANY_TO_MANY ->
                MANY_TO_MANY;
            default ->
                throw new DaoException("invalid cardinality value =< %s >", cardinality);
        };
    }

    public static CmMapUtils.FluentMap<String, Integer> getClassDomainsIndexes(List<Domain> domains, Classe classe) {
        return map(domains, Domain::getName, d -> classe.getMetadata().getDomainOrder().contains(d.getName()) ? classe.getMetadata().getDomainOrder().indexOf(d.getName()) : MAX_VALUE);
    }

    public static CascadeAction getActualCascadeAction(Domain domain, CascadeAction cascadeAction, RelationDirection direction) { //see sql _cm3_domain_cascade_get
        if (equal(cascadeAction, CA_AUTO)) {
            cascadeAction = CA_SETNULL;
            if (domain.getThisDomainWithDirection(direction).getTargetClasses().stream().flatMap(c -> c.getAllAttributes().stream()).anyMatch(a -> a.isOfType(REFERENCE) && equal(a.getType().as(ReferenceAttributeType.class).getDomainName(), domain.getName()) && equal(a.getType().as(ReferenceAttributeType.class).getDirection(), direction.inverse()))) {
                cascadeAction = CA_RESTRICT;
            }
        }
        return cascadeAction;
    }

}
