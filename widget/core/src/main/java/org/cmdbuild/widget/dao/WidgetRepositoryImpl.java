/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.dao;

import org.cmdbuild.widget.model.WidgetDbData;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import java.util.List;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetConst.ATTR_OWNER;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_TABLE;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class WidgetRepositoryImpl implements WidgetRepository {

    private final DaoService dao;

    public WidgetRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<WidgetData> getAllWidgetsForClass(String className) {
        return dao.selectAll().from(WidgetDataFromDbImpl.class).where(ATTR_OWNER, EQ, className).asList(WidgetData.class).stream().sorted(Ordering.natural().onResultOf(WidgetData::getIndex)).collect(toImmutableList());
    }

    @Override
    public void deleteForClass(String className) {
        dao.getJdbcTemplate().update("UPDATE \"" + WIDGET_TABLE + "\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND _cm3_utils_regclass_to_name(\"" + ATTR_OWNER + "\") = ?", checkNotBlank(className));
    }

    @Override
    public void updateForClass(String className, List<WidgetData> widgets) {//TODO not very efficent, rewrite this without delete-then-create
        deleteForClass(className);
        widgets.stream().map((widget) -> WidgetDataFromDbImpl.copyOf(widget).withOwner(className).build()).forEach(dao::create);
    }

    @Override
    public List<WidgetDbData> getAllWidgets() {
        return dao.selectAll().from(WidgetDataFromDbImpl.class).asList();
    }
}
