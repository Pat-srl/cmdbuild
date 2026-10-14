/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.service.rest.v4.command.ClassAttributeWsCommand.prepareAttributesToUpdateForOrder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author schursin
 */
@Component
public class DomainAttributeWsCommand {

    private final DaoService daoService;

    public DomainAttributeWsCommand(DaoService daoService) {
        this.daoService = checkNotNull(daoService);
	}

    public List<Attribute> doReadAll(String domainId, Boolean onlyActive) {
        Domain domain = daoService.getDomain(domainId);
        List<Attribute> attributeList = domain.getServiceAttributes();
        if (onlyActive) {
            attributeList = list(attributeList).without(a -> !a.isActive());
        }
        return attributeList;
    }

    public Attribute doRead(String domainId, String attrId) {
        Domain domain = daoService.getDomain(domainId);
		return domain.getAttribute(attrId);
	}

    public Attribute doCreate(String domainId, WsAttributeData data) {
        checkNotNull(data);
        Domain domain = daoService.getDomain(domainId);
        checkArgument(domain.getAttributeOrNull(data.getName()) == null, "attribute already present in domain = %s for name = %s", domainId, data.getName());
        return daoService.createAttribute(data.toAttrDefinition(domain));
    }

    public Attribute doUpdate(String domainId, String attrId, WsAttributeData data) {
        checkNotNull(data);
        Domain domain = daoService.getDomain(domainId);
        domain.getAttribute(attrId);
        checkArgument(equal(attrId, data.getName()), "data attr name = %s does not match with path attr id = %s", data.getName(), attrId);
        Attribute attribute = data.toAttrDefinition(domain);
        return daoService.updateAttribute(attribute);
    }

    public void doDelete(String domainId, String attrId) {
        Domain domain = daoService.getDomain(domainId);
        Attribute attribute = domain.getAttribute(attrId);
        daoService.deleteAttribute(attribute);
    }

    public Domain doReorder(String domainId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Domain domain = daoService.getDomain(domainId);
        daoService.updateAttributes(prepareAttributesToUpdateForOrder(domain::getAttribute, attrOrder));
        return daoService.getDomain(domainId);
    }
}
