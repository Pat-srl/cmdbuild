/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.graph;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface ClasseHierarchyService {

    ClasseHierarchy getClasseHierarchy(String classe);

    default ClasseHierarchy getClasseHierarchy(Classe classe) {
        return getClasseHierarchy(classe.getName());
    }

    @Deprecated//TODO remove
    default List<Classe> getClassesForDomain(Domain domain) {
        return set(getClasseHierarchy(domain.getSourceClass()).getDescendantsAndSelf()).with(getClasseHierarchy(domain.getTargetClass()).getDescendantsAndSelf()).stream().filter(domain::isDomainForClasse).collect(toList());
    }
}
