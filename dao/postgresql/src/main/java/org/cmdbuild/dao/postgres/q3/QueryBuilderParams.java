/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.postgres.q3.beans.JoinQueryArgs;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.QueryBuilderOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;

public interface QueryBuilderParams extends QueryBuilderOptionsCommons {

    EntryType getFrom();

    DaoQueryOptions getQueryOptions();

    @Override
    default Set<QueryBuilderOptions> getBuilderOptions() {
        return emptySet();
    }

    default boolean isUpdate() {
        return false;
    }

    default Map<String, Object> getValues() {
        return emptyMap();
    }

    default boolean isDelete() {
        return false;
    }

    default boolean getActiveCardsOnly() {
        return true;
    }

    @Nullable
    default CardMapper getCardMapper() {
        return null;
    }

    default boolean getJoinForRefCodeDescription() {
        return false;
    }

    default List<SelectArg> getSelectArgs() {
        return emptyList();
    }

    default boolean getSelectCount() {
        return false;
    }

    default boolean getSelectRowNumber() {
        return false;
    }

    default List<JoinQueryArgs> getJoinArgs() {
        return emptyList();
    }

    default List<WhereArg> getWhere() {
        return emptyList();
    }

}
