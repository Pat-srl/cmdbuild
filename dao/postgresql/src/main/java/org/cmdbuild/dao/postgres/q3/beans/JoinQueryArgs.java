/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import java.util.List;
import org.cmdbuild.dao.core.q3.JoinQueryBuilder;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;
import org.cmdbuild.data.filter.CmdbFilter;

public interface JoinQueryArgs extends JoinQueryBuilder {

    List<WhereArg> getOnExprs();

    List<WhereArg> getWhere();

    List<CmdbFilter> getFilters();

    String getJoinId();

    EntryType getFrom();

    List<SelectArg> getSelect();

}
