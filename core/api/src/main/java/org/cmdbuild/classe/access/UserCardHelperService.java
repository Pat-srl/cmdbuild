/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.EntryType;

public interface UserCardHelperService {

    Card sanitizeValues(Card card);

    Map<String, Object> sanitizeValues(EntryType type, Map<String, Object> values);
}
