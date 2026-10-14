/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

/**
 *
 * @author schursin
 */
@Component
public class ClassAttributeWsCommand {

    private final UserClassService userClassService;

    public ClassAttributeWsCommand(UserClassService userClassService) {
        this.userClassService = checkNotNull(userClassService);
    }

    public Attribute doRead(String classId, String attrId) {
        return userClassService.getUserAttribute(classId, attrId);
    }

    public List<Attribute> doReadAll(String classId, Function<String, List<Attribute>> function) {
        return function.apply(classId);
    }

    public Attribute doCreate(String classId, WsAttributeData data) {
        Classe classe = userClassService.getUserClass(classId);
        return userClassService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
    }

    public Attribute doUpdate(String classId, WsAttributeData data) {
        Classe classe = userClassService.getUserClass(classId);
        return userClassService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
    }

    public void doDelete(String classId, String attrId) {
        userClassService.deleteAttribute(classId, attrId);
    }

    public Classe doReorder(String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Classe classe = userClassService.getUserClass(classId);
        userClassService.updateAttributes(prepareAttributesToUpdateForOrder(classe::getAttribute, attrOrder));
        return userClassService.getUserClass(classId);
    }

    static List<Attribute> prepareAttributesToUpdateForOrder(Function<String, Attribute> attributeFun, List<String> attributes) {
        checkArgument(set(attributes).size() == attributes.size());
        List<Attribute> listAttribute = list();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = checkNotNull(attributeFun.apply(attributes.get(i)));
            int newIndex = i + 1;
            if (attribute.getIndex() != newIndex) {
                listAttribute.add(AttributeImpl.copyOf(attribute).withIndex(newIndex).build());
            }
        }
        return listAttribute;
    }
}
