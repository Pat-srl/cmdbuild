package org.cmdbuild.workflow.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PREV_EXECUTORS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class FlowImpl implements Flow {

    private final Process plan;
    private final Card card;
    private final FlowStatus flowStatus;
    private final List<FlowActivity> flowActivities;
    private final Map<String, Object> widgetData;

    private FlowImpl(FlowCardBuilder builder) {
        this.plan = checkNotNull(builder.plan);
        if (builder.flowActivities == null && builder.previousExecutors == null) {
            this.card = checkNotNull(builder.card);
        } else {
            this.card = CardImpl.copyOf(builder.card).accept((c) -> {
                if (builder.flowActivities != null) {
                    c
                            .withAttribute(ATTR_TASK_INSTANCE_ID, builder.flowActivities.stream().map(FlowActivity::getInstanceId).collect(toList()))
                            .withAttribute(ATTR_TASK_DEFINITION_ID, builder.flowActivities.stream().map(FlowActivity::getDefinitionId).collect(toList()))
                            .withAttribute(ATTR_NEXT_EXECUTOR, builder.flowActivities.stream().map(FlowActivity::getPerformerGroup).collect(toList()));
                }
                if (builder.previousExecutors != null) {
                    c.withAttribute(ATTR_PREV_EXECUTORS, ImmutableSet.copyOf(builder.previousExecutors));
                }
            }).build();
        }

        this.flowStatus = checkNotNull(builder.flowStatus);
        checkArgument(equal(card.getType().getName(), plan.getName()), "planClasse name does not match flowCard.type name");

        List<String> activityIds = card.getNotNull(ATTR_TASK_INSTANCE_ID, List.class);
        List<String> activityDefintionIds = card.getNotNull(ATTR_TASK_DEFINITION_ID, List.class);
        List<String> activityPerformers = card.getNotNull(ATTR_NEXT_EXECUTOR, List.class);
        checkArgument(activityIds.size() == activityDefintionIds.size() && activityIds.size() == activityPerformers.size(), "activity info size mismatch: activities = %s, act definitions = %s, performers = %s", activityIds, activityDefintionIds, activityPerformers);
        flowActivities = IntStream.range(0, activityIds.size()).mapToObj((i)
                -> new FlowActivityImpl(
                        activityIds.get(i),
                        activityDefintionIds.get(i),
                        activityPerformers.get(i),
                        plan.getTaskById(activityDefintionIds.get(i)).getDescription()))
                .collect(toImmutableList());
        this.widgetData = map(builder.widgetData).immutable();
    }

    @Override
    public Long getTenantId() {
        return card.getTenantId();
    }

    @Override
    public List<FlowActivity> getFlowActivities() {
        return flowActivities;
    }

    @Override
    public List<String> getPreviousExecutors() {
        return convert(card.get(ATTR_PREV_EXECUTORS), List.class);
    }

    @Override
    public Process getType() {
        return plan;
    }

    @Override
    public FlowStatus getStatus() {
        return flowStatus;
    }

    @Nullable
    @Override
    public PlanInfo getPlanInfoOrNull() {
        String value = card.get(ATTR_PLAN_INFO, String.class);
        return PlanInfoImpl.deserializeNullable(value);
    }

    @Override
    public Long getId() {
        return card.getId();
    }

    @Override
    public boolean hasId() {
        return card.hasId();
    }

    @Override
    public String getCode() {
        return card.getCode();
    }

    @Override
    public String getDescription() {
        return card.getDescription();
    }

    @Override
    public Object get(String key) {
        return card.get(key);
    }

    @Override
    public <T> T get(String key, Class<? extends T> requiredType) {
        return card.get(key, requiredType);
    }

    @Override
    public <T> T get(String key, Class<? extends T> requiredType, T defaultValue) {
        return card.get(key, requiredType, defaultValue);
    }

    @Override
    public Iterable<Entry<String, Object>> getAttributeValues() {
        return card.getAttributeValues();
    }

    @Override
    public Iterable<Entry<String, Object>> getRawValues() {
        return card.getRawValues();
    }

    @Override
    public String getUser() {
        return card.getUser();
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return card.getBeginDate();
    }

    @Override
    public ZonedDateTime getEndDate() {
        return card.getEndDate();
    }

    @Override
    public Long getCurrentId() {
        return card.getCurrentId();
    }

    @Override
    public Long getCardId() {
        return card.getId();
    }

    @Override
    public String getFlowId() {
        return card.get(ATTR_FLOW_ID, String.class);
    }

    @Override
    public Map<String, Object> getWidgetData() {
        return widgetData;
    }

    @Override
    public String toString() {
        return "Flow{id=" + getIdOrNull() + ", process=" + getType().getName() + ", tasklist=" + getFlowActivities().stream().map(t -> format("%s[%s]@%s", t.getDefinitionId(), t.getInstanceId(), t.getPerformerGroup())).collect(joining(",")) + ", status=" + getStatus() + '}';
    }

    public static FlowCardBuilder builder() {
        return new FlowCardBuilder();
    }

    public static FlowCardBuilder copyOf(Flow flow) {
        return builder()
                .withCard(flow)
                .withFlowActivities(flow.getFlowActivities())
                .withFlowStatus(flow.getStatus())
                .withPlan(flow.getType())
                .withWidgetData(flow.getWidgetData());
    }

    public static class FlowCardBuilder implements Builder<FlowImpl> {

        private Card card;
        private Process plan;
        private FlowStatus flowStatus;
        private List<FlowActivity> flowActivities;
        private Collection<String> previousExecutors;
        private final Map<String, Object> widgetData = map();

        @Override
        public FlowImpl build() {
            return new FlowImpl(this);
        }

        public FlowCardBuilder withoutFlowActivity(Predicate<FlowActivity> filter) {
            flowActivities = list(flowActivities).without(filter);
            return this;
        }

        public FlowCardBuilder withPlan(Process plan) {
            this.plan = plan;
            return this;
        }

        public FlowCardBuilder withFlowStatus(FlowStatus flowStatus) {
            this.flowStatus = flowStatus;
            return this;
        }

        public FlowCardBuilder withCard(Card value) {
            card = value;
            return this;
        }

        public FlowCardBuilder withCard(Process plan, Map<String, Object> values) {
            this.plan = checkNotNull(plan);
            this.card = CardImpl.buildCard(plan, values);
            return this;
        }

        public FlowCardBuilder withFlowActivities(List<FlowActivity> flowActivities) {
            this.flowActivities = flowActivities;
            return this;
        }

        public FlowCardBuilder withPreviousExecutors(Collection<String> previousExecutors) {
            this.previousExecutors = previousExecutors;
            return this;
        }

        public FlowCardBuilder withWidgetData(Map<String, Object> widgetData) {
            this.widgetData.putAll(widgetData);
            return this;
        }

    }
}
