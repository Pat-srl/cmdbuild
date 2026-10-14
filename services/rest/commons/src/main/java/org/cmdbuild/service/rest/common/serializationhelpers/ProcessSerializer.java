/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowActivity;
import org.cmdbuild.workflow.model.Process;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;

/**
 *
 * @author afelice
 */
@Component
public class ProcessSerializer {

    protected final CardWsSerializationHelperv3 cardSerializationHelper;
    protected final ObjectTranslationService translationService;
    protected final UserClassService classeService;
    protected final ClassSerializationHelper classSerializationHelper;
    protected final WorkflowConfiguration workflowConfiguration;
    private final RoleRepository roleRepository;

    public ProcessSerializer(
            CardWsSerializationHelperv3 cardSerializationHelper,
            UserClassService classeService, ClassSerializationHelper classSerializationHelper,
            ObjectTranslationService translationService,
            WorkflowConfiguration workflowConfiguration,
            RoleRepository roleRepository) {
        this.cardSerializationHelper = checkNotNull(cardSerializationHelper);
        this.classeService = checkNotNull(classeService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.translationService = checkNotNull(translationService);
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.roleRepository = checkNotNull(roleRepository);
    }

    /**
     * Was in <code>ProcessWs.read()</code> and in subsequent:
     * <ol>
     * <li><code>ProcessWs.detailedResponse()</code>;
     * <li><code>ProcessWs.processSpecificDataMapConsumer()</code>;
     *
     *
     * @param process
     * @return
     */
    public FluentMap<String, Object> serializeProcess(Process process) {
        return mapOf(String.class, Object.class).with("flowStatusAttr", process.getFlowStatusLookup(),
                "messageAttr", process.getMetadata().getMessageAttr(),
                "enableSaveButton", firstNotNull(process.isFlowSaveButtonEnabled(), workflowConfiguration.enableSaveButton()),
                "stoppableByUser", process.getMetadata().isWfUserStoppable(),
                "engine", process.getProviderOrDefault(RIVER),
                "planId", process.getPlanIdOrNull());
//         if (detailed) {
//                m.put("activities", (p.isSuperclass() || !p.isActive()) ? emptyList() : workflowService.getTaskDefinitions(p.getName()).stream().map(t -> converterService.serializeEssentialTaskDefinition(p, t)).collect(toImmutableList()));
//            }
    }

    /**
     * Was in <code>ProcessSerializer</code>.
     *
     * @param flow
     * @return
     */
    public FluentMap<String, Object> serializeBasicHistory(Flow flow) {
        return cardSerializationHelper.serializeBasicHistory(flow)
                .with("_historyType", "card")
                .with("_status", flow.getCardStatus().name())
                .accept(m -> {
                    m.put("status", flow.getFlowStatusId(),
                            "_status_description", flow.getFlowStatusDescription());
                }).accept((Consumer) serializeActivitiesInfos(flow));
    }

    /**
     * Was in <code>ProcessSerializer</code>.
     *
     * @param value
     * @return
     */
    @Nullable
    public String serializeTaskDescriptionValue(@Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            if (value instanceof LookupValue lookupValue) {
                LookupValue lookup = lookupValue;
                return translationService.translateLookupDescriptionSafe(lookup.getLookupType(), lookup.getCode(), lookup.getDescription());
            } else if (value instanceof IdAndDescription idAndDescription) {
                return idAndDescription.getDescription();
            } else {
                return toStringOrEmpty(value);
            }
        }
    }

    /**
     * Was in <code>ProcessSerializer.serializeFlow()</code>.
     *
     * @param card
     * @return
     */
    public FluentMap<String, Object> serializeFlow(Flow card) {
        return serializeFlow(card, false, DaoQueryOptionsImpl.emptyOptions());
    }

    /**
     * Flow serialization, supporting only part of attributes selected.
     *
     * @param card
     * @param selectedAttrs
     * @return
     */
    public FluentMap<String, Object> serializeFlow(Flow card, Set<String> selectedAttrs) {
        return serializeFlow(card, false,
                DaoQueryOptionsImpl.builder()
                        .withAttrs(selectedAttrs)
                        .build());
    }

    /**
     * Was in <code>ProcessSerializer.serializeFlow()</code>, with serialization
     * of widget and task list.
     *
     * @param card
     * @param includeModel
     * @param queryOptions
     * @return
     */
    public FluentMap<String, Object> serializeFlow(Flow card, boolean includeModel, DaoQueryOptions queryOptions) {
//        ProcessStatus processStatus = lookupHelper.getFlowStatusLookup(card).transform(ProcessStatusUtils::toProcessStatus).orNull();
//card.getStatus();
        Set<ExtendedCardOptions> extendedCardOptions = EnumSet.noneOf(ExtendedCardOptions.class);
        if (includeModel) {
            extendedCardOptions.add(INCLUDE_MODEL);
        }
        return (FluentMap) cardSerializationHelper.serializeCard(card, queryOptions, extendedCardOptions).with(
                "name", card.getFlowId(),
                "status", card.getFlowStatusLookup().getId(), ///processStatus != null ? processStatus.getId() : null,
                "_status_description", card.getFlowStatusLookup().getDescription()//processStatus == null ? null : processStatus.getDescription()
        );
    }

    /**
     * Was in <code>ProcessSerializer.serializeTaskInfos()</code>.
     *
     * @param flow
     * @return
     */
    private Consumer<FluentMap> serializeActivitiesInfos(Flow flow) {
        return c -> {
            List<FlowActivity> activities = flow.getFlowActivities();
            if (activities.size() == 1) {
                FlowActivity activity = getOnlyElement(activities);
                c.put(
                        "_activity_code", activity.getDefinitionId(),
                        "_activity_description", activity.getDescription(),
                        "_activity_performer", activity.getPerformerGroup(),
                        "__activity_performer_description", getPerformerDescription(activity.getPerformerGroup()));
            }
            c.put("activities", activities.stream().map(activity -> map(
                    "code", activity.getDefinitionId(),
                    "description", activity.getDescription(),
                    "performer", activity.getPerformerGroup(),
                    "_performer_description", getPerformerDescription(activity.getPerformerGroup()))).collect(toList()));
        };
    }

    private String getPerformerDescription(String performerGroup) {
        return applyOrDefault(roleRepository.getGroupWithNameOrNull(performerGroup), r -> r.getDescription(), performerGroup);
    }
}
