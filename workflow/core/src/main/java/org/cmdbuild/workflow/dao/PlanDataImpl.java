/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import jakarta.annotation.Nullable;

@CardMapping("_Plan")
public class PlanDataImpl implements PlanData {

    public final static String ATTR_DATA = "Data", ATTR_CLASSE_ID = "ClassId";

    private final Long id;
    private final String planId, data, classId;

    private PlanDataImpl(SimplePlanDataBuilder builder) {
        this.id = builder.id;
        this.planId = checkNotBlank(builder.planId);
        this.data = checkNotBlank(builder.data);
        this.classId = checkNotBlank(builder.classId);
    }

    @Nullable
    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getPlanId() {
        return planId;
    }

    @CardAttr(ATTR_DATA)
    @Override
    public String getData() {
        return data;
    }

    @CardAttr(ATTR_CLASSE_ID)
    @Override
    public String getClasseId() {
        return classId;
    }

    public static SimplePlanDataBuilder builder() {
        return new SimplePlanDataBuilder();
    }

    public static SimplePlanDataBuilder copyOf(PlanData source) {
        return new SimplePlanDataBuilder()
                .withId(source.getId())
                .withPlanId(source.getPlanId())
                .withData(source.getData())
                .withClasseId(source.getClasseId());
    }

    public static class SimplePlanDataBuilder implements Builder<PlanDataImpl, SimplePlanDataBuilder> {

        private Long id;
        private String planId;
        private String data;
        private String classId;

        public SimplePlanDataBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimplePlanDataBuilder withPlanId(String planId) {
            this.planId = planId;
            return this;
        }

        public SimplePlanDataBuilder withData(String data) {
            this.data = data;
            return this;
        }

        public SimplePlanDataBuilder withClasseId(String classId) {
            this.classId = classId;
            return this;
        }

        @Override
        public PlanDataImpl build() {
            return new PlanDataImpl(this);
        }

    }
}
