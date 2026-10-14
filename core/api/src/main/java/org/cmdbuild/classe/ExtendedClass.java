/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;

public interface ExtendedClass extends ExtendedClassData {

    Classe getClasse();

    Map<String, List<LookupValue>> getLookupValuesByAttr();

}
