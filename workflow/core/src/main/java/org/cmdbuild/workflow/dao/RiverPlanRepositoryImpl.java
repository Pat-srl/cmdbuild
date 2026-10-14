/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.cmdbuild.workflow.model.PlanUpdatedEventImpl;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.core.RiverPlanImpl;
import static org.cmdbuild.workflow.utils.WfRiverXpdlUtils.parseXpdlForCmdb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiverPlanRepositoryImpl implements ExtendedRiverPlanRepository {

    public final static String ATTR_BIND_TO_CLASS = "cmdbuildBindToClass";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmCache<Optional<RiverPlan>> riverPlanByPlanId;
    private final CmCache<Optional<RiverPlan>> riverPlanByClassId;

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    private final PlanDataRepository dataRepository;

    public RiverPlanRepositoryImpl(PlanDataRepository dataRepository, CacheService cacheService) {
        this.dataRepository = checkNotNull(dataRepository);
        riverPlanByPlanId = cacheService.newCache("river_plan_by_plan_id", CacheConfig.SYSTEM_OBJECTS);
        riverPlanByClassId = cacheService.newCache("river_plan_by_class_id", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateAll() {
        riverPlanByClassId.invalidateAll();
        riverPlanByPlanId.invalidateAll();
    }

    @Override
    public List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId) {
        return dataRepository.getPlanVersionsByClassIdOrderByCreationDesc(classId);
    }

    @Override
    @Nullable
    public RiverPlan getPlanByIdOrNull(String planId) {
        return riverPlanByPlanId.get(planId, () -> Optional.ofNullable(doGetPlanByIdOrNull(planId))).orElse(null);
    }

    @Nullable
    private RiverPlan doGetPlanByIdOrNull(String planId) {
        return toRiverPlan(dataRepository.getPlanDataByIdOrNull(planId));
    }

    @Override
    @Nullable
    public RiverPlan getPlanByClassIdOrNull(String classeId) {
        return riverPlanByClassId.get(classeId, () -> Optional.ofNullable(doGetPlanByClassIdOrNull(classeId))).orElse(null);
    }

    @Nullable
    private RiverPlan doGetPlanByClassIdOrNull(String classeId) {
        return toRiverPlan(dataRepository.getPlanDataForProcessClasseOrNull(classeId));
    }

    @Nullable
    private RiverPlan toRiverPlan(@Nullable PlanData planData) {
        if (planData == null) {
            return null;
        } else {
            RiverPlan plan = parseXpdlForCmdb(planData.getData());
            plan = RiverPlanImpl.copyOf(plan).withPlanId(planData.getPlanId()).build();
            return plan;
        }
    }

    @Override
    public RiverPlan createPlan(RiverPlan riverPlan) {
        String classeId = riverPlan.getAttr(ATTR_BIND_TO_CLASS),
                planId = riverPlan.getId();
        dataRepository.create(PlanDataImpl.builder()
                .withClasseId(classeId)
                .withPlanId(planId)
                .withData(riverPlan.toXpdl())
                .build());
        invalidateAll();
        eventBus.post(new PlanUpdatedEventImpl(classeId, planId));
        return riverPlan;
    }

    @Override
    public RiverPlan updatePlan(RiverPlan riverPlan) {
        PlanData data = dataRepository.getPlanDataById(riverPlan.getId());
        data = dataRepository.update(PlanDataImpl.copyOf(data).withData(riverPlan.toXpdl()).build());
        invalidateAll();
        eventBus.post(new PlanUpdatedEventImpl(data.getClasseId(), data.getPlanId()));
        return riverPlan;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
