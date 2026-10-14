/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.dao.PlanDataImpl.ATTR_CLASSE_ID;
import org.springframework.stereotype.Component;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;

@Component
public class PlanDataRepositoryImpl implements PlanDataRepository {

    private final DaoService dao;

    public PlanDataRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    @Nullable
    public PlanData getPlanDataForProcessClasseOrNull(String classeId) {
        return dao.selectAll().from(PlanData.class)
                .where(ATTR_CLASSE_ID, EQ, checkNotBlank(classeId))
                .orderBy(ATTR_BEGINDATE, DESC)
                .limit(1l)
                .getOneOrNull();
    }

    @Override
    public List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId) {
        return dao.selectAll().from(PlanData.class)
                .where(ATTR_CLASSE_ID, EQ, checkNotBlank(classId))
                .orderBy(ATTR_BEGINDATE, DESC)
                .run().stream().map(ResultRow::toCard).map((p) -> new RiverPlanVersionInfo() {//TODO create bean
            @Override
            public ZonedDateTime getLastUpdate() {
                return toDateTime(p.getBeginDate());
            }

            @Override
            public String getPlanId() {
                return checkNotBlank(p.getCode());
            }
        }).collect(toList());
    }

    @Override
    @Nullable
    public PlanData getPlanDataByIdOrNull(String planId) {
        return dao.selectAll().from(PlanData.class)
                .where(ATTR_CODE, EQ, checkNotBlank(planId))
                .limit(1l)//in case of multiple results, any row is good (if the code is equal, the data must be equal too
                .getOneOrNull();
    }

    @Override
    public PlanData create(PlanData plantData) {
        return dao.create(plantData);
    }

    @Override
    public PlanData update(PlanData data) {
        return dao.update(data);
    }

}
