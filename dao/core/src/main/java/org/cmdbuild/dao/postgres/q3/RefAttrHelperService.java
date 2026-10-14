/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;

public interface RefAttrHelperService {

    Classe getTargetClassForAttribute(Attribute a);

    @Nullable
    Attribute getAttrForMasterCardFilterOrNull(Classe source, Classe target);
}
