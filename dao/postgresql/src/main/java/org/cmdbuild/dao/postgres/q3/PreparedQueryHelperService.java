/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.postgres.q3.beans.WhereElement;
import org.cmdbuild.dao.postgres.q3.beans.SelectElement;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import java.util.List;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.orm.CardMapper;

public interface PreparedQueryHelperService {

    PreparedQueryExt prepareQuery(String query, List<SelectElement> preparedQuerySelect, List<WhereElement> where, EntryType from, CardMapper cardMapper);

}
