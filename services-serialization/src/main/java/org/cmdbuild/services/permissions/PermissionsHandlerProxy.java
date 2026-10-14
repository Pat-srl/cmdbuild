/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.services.permissions;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.widget.model.WidgetData;

/**
 * Proxy for permissions handling. Will be refactored with
 * <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/7682">#7682
 * -- Permissions handling: preliminar step for refactoring</a>
 *
 * @author afelice
 */
public interface PermissionsHandlerProxy {

    Card cardWsSerializationHelperv3_fetchCardPermissions(Card card, boolean canWrite);

    boolean cardWsSerializationHelperv3_isWidgetEnabled(Card card, WidgetData w);

    boolean cardWsSerializationHelperv3_hasDmsCategoryWritePermission(String classId, final Classe userClass,
            org.cmdbuild.lookup.LookupValue category, String defaultDmsCategory);

    boolean cardWsSerializationHelperv3_checkDmsPermission(Classe userClass, String categoryValue, ClassPermission permissionToCheck, String defaultDmsCategory);
}
