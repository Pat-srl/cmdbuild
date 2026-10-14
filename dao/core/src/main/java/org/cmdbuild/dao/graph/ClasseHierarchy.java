/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.graph;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface ClasseHierarchy {

    Classe getClasse();

    @Nullable
    Classe getParentOrNull();

    List<Classe> getAncestors();

    Collection<Classe> getChildren();

    Collection<Classe> getLeaves();

    Collection<Classe> getDescendants();

    boolean isAncestorOf(Classe classe);

    default Classe getParent() {
        return checkNotNull(getParentOrNull(), "this class does not have a parent");
    }

    default Collection<Classe> getDescendantsAndSelf() {
        return list(getClasse()).with(getDescendants());
    }

    default Collection<Classe> getAncestorsAndSelf() {
        return getClasse().hasParent() ? list(getAncestors()).with(getClasse()) : singletonList(getClasse());
    }

    default boolean equalToOrAncestorOf(Classe otherClass) {
        return getDescendantsAndSelf().stream().anyMatch(d -> equal(d.getName(), otherClass.getName()));
    }

    static boolean isLeaf(Classe classe) {
        return !classe.isSuperclass();
    }

}
