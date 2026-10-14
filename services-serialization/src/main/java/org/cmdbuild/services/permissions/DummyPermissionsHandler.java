/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.permissions;

import com.google.common.annotations.VisibleForTesting;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.widget.model.WidgetData;

/**
 * Skips all permissions checking
 *
 * @author afelice
 */
@VisibleForTesting
public class DummyPermissionsHandler implements PermissionsHandlerProxy {

    @Override
    public Card cardWsSerializationHelperv3_fetchCardPermissions(Card card, boolean canWrite) {
        return card;
    }

    @Override
    public boolean cardWsSerializationHelperv3_isWidgetEnabled(Card card, WidgetData w) {
        return true;
    }

    @Override
    public boolean cardWsSerializationHelperv3_hasDmsCategoryWritePermission(String classId, Classe userClass, LookupValue category, String defaultDmsCategory) {
        return true;
    }

    @Override
    public boolean cardWsSerializationHelperv3_checkDmsPermission(Classe userClass, String categoryValue, ClassPermission permissionToCheck, String defaultDmsCategory) {
        return true;
    }

} // end DummyPermissionsHandler class
