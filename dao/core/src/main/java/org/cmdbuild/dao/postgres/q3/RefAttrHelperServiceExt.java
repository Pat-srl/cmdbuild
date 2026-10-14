/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.postgres.q3;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;

public interface RefAttrHelperServiceExt extends RefAttrHelperService{

    @Nullable
    Card getReferencedCard(Attribute a, @Nullable Object value);

}
