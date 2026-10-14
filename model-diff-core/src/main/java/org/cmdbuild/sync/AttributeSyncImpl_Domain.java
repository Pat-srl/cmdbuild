/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.DOMAINKEY;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * CRUD on {@link Attribute} for {@link Domain}.
 * 
 * @author afelice
 */
@Component
@Qualifier("domainAttribute")
public class AttributeSyncImpl_Domain extends AttributeSyncImpl {
    
    private final DaoService dao;

    public AttributeSyncImpl_Domain(DaoService dao,
            AttributeTypeConversionService attributeSerializer) {
        super(attributeSerializer);
        this.dao = checkNotNull(dao);
    }    
    
    @Override
    public Attribute read(String domainId, String attrId) {
        // as in DomainAttributeWs.read()        
        Domain domain = dao.getDomain(domainId);
        return domain.getAttribute(attrId);
    }    
     
    @Override
    public Attribute add(Attribute attribute) {
        // as in DomainAttributeWs.create()        
        // @todo when #7756 closed, this code should be in a cmdbuild-services-rest-commons::DomainAttributeWsCommand.create()
        final Domain domain = (Domain)attribute.getOwner();
        checkArgument(domain.getAttributeOrNull(attribute.getName()) == null, "attribute already present in Domain =< %s > for name =< %s >", domain.getName(), attribute.getName()); // Something similar is in DomainAttributeWs.create()
        if (domain.hasCardinality(MANY_TO_MANY) && attribute.isUnique() && attribute.isMandatory()) {
            attribute = AttributeImpl.copyOf(attribute).withMeta(DOMAINKEY, "true").build();
        } else if (domain.hasCardinality(MANY_TO_MANY) && (!attribute.isUnique() || !attribute.isMandatory())) {
            attribute = AttributeImpl.copyOf(attribute).withMeta(DOMAINKEY, "false").build();
        }
        Attribute createdAttribute = dao.createAttribute(attribute);
        
        // @todo AFE add Attribute description translation: _description_translation, _description_plural_translation
        return createdAttribute;
    }    
    
    @Override
    public Attribute update(Attribute attribute) {
        // as in DomainAttributeWs.update()        
        // @todo when #7756 closed, this code should be in a cmdbuild-services-rest-commons::DomainAttributeWsCommand.update()
        final Domain domain = (Domain)attribute.getOwner();
        Attribute updatedAttribute = dao.updateAttribute(attribute);
        
        // @todo AFE add Attribute description translation: _description_translation, _description_plural_translation        
        return updatedAttribute;
    }    
    
    // @todo AFE TBC
//    @Override
//    public void remove(Attribute attribute) {
//        // @todo AFE TBC as in UserClassServiceImpl.deleteAttribute()
//        // checkArgument(attribute.hasServiceModifyPermission(), "CM: permission denied: user not authorized to delete attribute = %s", attribute);
//        dao.deleteAttribute(attribute);
//    }    
    
}
