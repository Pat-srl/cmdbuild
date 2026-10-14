/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static java.lang.String.format;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_DEFAULT;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.entrytype.ClassPermission;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import org.cmdbuild.widget.model.WidgetData;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class PermissionsHandlerProxyImpl implements PermissionsHandlerProxy {

    @Override
    public Card cardWsSerializationHelperv3_fetchCardPermissions(Card card, boolean canWrite) {
        final ClassPermissionsImpl permissions = ClassPermissionsImpl.copyOf(card.getType()).removePermissions(PermissionScope.PS_UI, getClassPermissionToRemove(canWrite)).build();
        final ClasseImpl classeWithPermissions = ClasseImpl.copyOf(card.getType()).withPermissions(permissions).build();
        final Card cardWithPermissions = CardImpl.copyOf(card).withType(classeWithPermissions).build();
        return cardWithPermissions;
    }

    @Override
    public boolean cardWsSerializationHelperv3_isWidgetEnabled(Card card, WidgetData w) {
        return toBooleanOrDefault(card.getType().getOtherPermissions().get(format("widget_%s", w.getId())), true);
    }

    @Override
    public boolean cardWsSerializationHelperv3_hasDmsCategoryWritePermission(String classId, final Classe userClass,
            org.cmdbuild.lookup.LookupValue category, String defaultDmsCategory) {
        boolean canWrite;
        canWrite = classId.equals("Email") || classId.equals("_CalendarEvent") ? true : userClass.hasDmsCategoryWritePermission(formatCategoryValue(userClass, category.getCode(), defaultDmsCategory));
        return canWrite;
    }

    @Override
    public boolean cardWsSerializationHelperv3_checkDmsPermission(Classe userClass, String categoryValue, ClassPermission permissionToCheck, String defaultDmsCategory) {
        Set<GrantAttributePrivilege> categoryPermission = userClass.getDmsPermissions().getOrDefault(formatCategoryValue(userClass, categoryValue, defaultDmsCategory), set(GAP_DEFAULT));
        if (categoryPermission.contains(GAP_WRITE)) {
            return true;
        } else if (categoryPermission.contains(GAP_DEFAULT)) {
            return userClass.isProcess() ? userClass.hasUiPermission(CP_WF_BASIC) : userClass.hasUiPermission(permissionToCheck);
        } else {
            return false;
        }
    }

    private Set getClassPermissionToRemove(boolean canWrite) {
        if (canWrite) {
            return set();
        } else {
            return set(CP_WRITE);
        }
    }

    private static String formatCategoryValue(Classe userClass, String categoryValue, String defaultDmsCategory) {
        return format("%s_%s", userClass.hasDmsCategory() ? userClass.getDmsCategory() : defaultDmsCategory, categoryValue);
    }

} // end PermissionsHandlerProxyImpl
