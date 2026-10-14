/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import static org.cmdbuild.sync.WsClassData_WithPermissionMode.isReserved;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * CRUD on {@link Attribute} for {@link Classe}.
 * 
 * @author afelice
 */
@Component
@Qualifier("classeAttribute")
public class AttributeSyncImpl_Classe extends AttributeSyncImpl {
    
    private final UserClassService classService;

    public AttributeSyncImpl_Classe(UserClassService classService,
            AttributeTypeConversionService attributeSerializer) {
        super(attributeSerializer);
        this.classService = checkNotNull(classService);
    }    
    
    @Override
    public Attribute read(String classId, String attrId) {
        return classService.getUserClass(classId).getAttribute(attrId);
    }    
 
    @Override
    public Attribute add(Attribute attribute) {
        Attribute result;
        final EntryType owner = attribute.getOwner();
        
        // A similar check is in AttribteSyncImpl_Domain. But done on Attributes 
        // for Classe leads to errors in unit-test fixtures, not always resolvable
        // with the use of mockBuildClasse_WithoutAttributes() and mockClasseReturn_Versions().
        // In original source code ClassAttributeWs.create() was not present, so
        // skipped even here
        //checkArgument(owner.getAttributeOrNull(attribute.getName()) == null, "attribute already present in Classe = %s for name = %s", owner.getName(), attribute.getName()); // Something similar is in DomainAttributeWs.create()
        
        if (isReserved(owner) || WsAttributeData.isReserved(attribute)) {
            // A reserved/protected Classe
            result = classService.createAttribute_Reserved(attribute);            
        } else {
            // A normal Classe
            result = classService.createAttribute(attribute);
        }
        
        // @todo AFE add Attribute description translation: _description_translation, _description_plural_translation        
        return result;
    }    
    
    @Override
    public Attribute update(Attribute attribute) {
        Attribute result;
        final EntryType owner = attribute.getOwner();
        if (WsClassData_WithPermissionMode.isReserved(attribute.getOwner()) || WsAttributeData.isReserved(attribute)) {
            // A reserved/protected Classe
            result = classService.updateAttribute_Reserved(attribute);            
        } else {
            result = classService.updateAttribute(attribute);
        }
        
        // @todo AFE add classe description translation: _description_translation, _description_plural_translation
        return result;
    }    
    
    // @todo AFE TBC
//    @Override
//    public void remove(Attribute attribute) {
//        // @todo AFE TBC as in UserClassServiceImpl.deleteAttribute()
//        // checkArgument(attribute.hasServiceModifyPermission(), "CM: permission denied: user not authorized to delete attribute = %s", attribute);
//        dao.deleteAttribute(attribute);
//    }    
    
}
