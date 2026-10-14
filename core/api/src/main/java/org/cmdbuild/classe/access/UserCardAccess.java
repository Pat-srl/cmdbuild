/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.SelectMatchFilterBuilder;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;

public interface UserCardAccess {

    Classe getUserClass();

    CmdbFilter getWholeClassFilter();

    Map<String, UserCardAccessWithFilter> getSubsetFiltersByName();

    Classe getUserClass(Set<String> activeFilters);

    Consumer<SelectMatchFilterBuilder> addSubsetFilterMarkersToQueryVisitor();

    Card addCardAccessPermissionsFromSubfilterMark(Card card);

    Set<String> getActiveFilters(Function<String, Object> record);

    Set<String> getFilterMarkNames();

    default boolean hasWholeClassFilter() {
        return !getWholeClassFilter().isNoop();
    }

}
