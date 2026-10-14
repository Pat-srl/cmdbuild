/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.Classe;

public interface ClasseRepository extends ClasseReadonlyRepository  {

	Classe createClass(ClassDefinition definition);

	Classe updateClass(ClassDefinition definition);

        ClassDefinition retrieveClassOid(ClassDefinition definition);
        
	void deleteClass(Classe dbClass);
}
