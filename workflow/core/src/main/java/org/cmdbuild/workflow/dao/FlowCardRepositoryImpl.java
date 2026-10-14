package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.size;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import org.cmdbuild.classe.access.UserCardFileService;
import org.cmdbuild.classe.access.UserCardQueryOptions;
import org.cmdbuild.classe.access.UserCardQueryOptionsImpl;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.workflow.model.Flow;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.FlowStatus.SUSPENDED;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.FLOW_STATUS_LOOKUP;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.toFlowStatusLookupCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FlowCardRepositoryImpl implements FlowCardRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CardToFlowCardWrapperService wrapperService;
    private final ProcessRepository planClasseRepository;
    private final UserCardService cardService;
    private final LookupService lookupService;
    private final UserCardFileService userCardFileService;

    public FlowCardRepositoryImpl(
            DaoService dao,
            CardToFlowCardWrapperService wrapperService,
            ProcessRepository planClasseRepository,
            UserCardService cardService,
            LookupService lookupService,
            UserCardFileService userCardFileService) {
        this.dao = checkNotNull(dao);
        this.wrapperService = checkNotNull(wrapperService);
        this.planClasseRepository = checkNotNull(planClasseRepository);
        this.cardService = checkNotNull(cardService);
        this.lookupService = checkNotNull(lookupService);
        this.userCardFileService = checkNotNull(userCardFileService);
    }

    @Override
    public Flow create(Flow flow) {
        Card card = flowToCard(flow);
        card = dao.create(card);
        return cardToFlowCard(card);
    }

    @Override
    public Flow update(Flow flow) {
        Card card = flowToCard(flow);
        Card oldCard = dao.getCard(card);
        card = dao.update(card);
        userCardFileService.clearDeletedAttachments(oldCard, card);
        return cardToFlowCard(card);
    }

    @Override
    public Flow getFlowCard(Flow processInstance) {
        logger.debug("getting process instance for class '{}' and card id '{}'", processInstance.getType(), processInstance.getCardId());
        return cardToFlowCard(getProcessCard(processInstance));
    }

    @Override
    public Flow getFlowCardByPlanAndCardId(Process classe, Long cardId) {
        logger.debug("getting process instance for class '{}' and card id '{}'", classe, cardId);
        return cardToFlowCard(getProcessCard(classe, cardId));
    }

    @Override
    public Flow getFlowCardByPlanIdAndFlowId(String planId, String flowId) {
        Process plan = planClasseRepository.getPlanClasseByPlanId(planId);
        return cardToFlowCard(getProcessCard(plan, flowId));
    }

    @Override
    public Flow getFlowCardByClasseIdAndCardId(String classeName, Long cardId) {
        Process plan = classToPlan(dao.getClasse(classeName));
        return cardToFlowCard(getProcessCard(plan, cardId));
    }

    @Override
    public Iterable<? extends Flow> queryOpenAndSuspended(Process processClass) {
        logger.debug("getting all opened and suspended process instances for class = {}", processClass);
        return dao.selectAll().from(processClass).where(ATTR_FLOW_STATUS, IN, set(
                lookupService.getLookupByTypeAndCode(FLOW_STATUS_LOOKUP, toFlowStatusLookupCode(OPEN)).getId(),
                lookupService.getLookupByTypeAndCode(FLOW_STATUS_LOOKUP, toFlowStatusLookupCode(SUSPENDED)).getId()
        )).getCards().stream().map(this::cardToFlowCard).collect(toList());
    }

    @Override
    public PagedElements<Flow> getUserCardsByClassIdAndQueryOptions(String classId, DaoQueryOptions query) {
        return getUserFlows(classId, UserCardQueryOptionsImpl.builder().withQueryOptions(query).build());
    }

    @Override
    public PagedElements<Flow> getUserFlows(String classId, UserCardQueryOptions cardQueryOptions) {
        checkArgument(dao.getClasse(classId).isProcess(), "invalid process class = {}", classId);
        return cardService.getUserCards(classId, cardQueryOptions).map(this::cardToFlowCard);
    }


    @Override
    public Flow getUserFlowCard(String classId, long cardId) {
        return cardToFlowCard(cardService.getUserCard(classId, cardId));//TODO access control
    }

    @Override
    public boolean userCanReadCard(String classId, long cardId) {
        return cardService.userCanReadCard(classId, cardId);
    }

    private Process classToPlan(Classe classe) {
        return planClasseRepository.classToPlanClasse(classe);
    }

    private Flow cardToFlowCard(Card card) {
        return wrapperService.cardToFlowCard(card);
    }

    private Card flowToCard(Flow flow) {
        return CardImpl.copyOf(flow).withAttribute(ATTR_FLOW_STATUS, lookupService.getLookupByTypeAndCode(FLOW_STATUS_LOOKUP, toFlowStatusLookupCode(flow.getStatus()))).build();
    }

    private Card getProcessCard(Flow processInstance) {
        return getProcessCard(processInstance.getType(), processInstance.getCardId());
    }

    private Card getProcessCard(Process classe, long cardId) {
        logger.debug("getting process card for class = {} and card id = {}", classe, cardId);
        return dao.selectAll().from(classe).where(ATTR_ID, EQ, cardId).getCard();
    }

    private Card getProcessCard(Process processClass, String flowId) {
        logger.debug("getting process card for class '{}' and process instance id '{}'", processClass, flowId);
        return dao.selectAll().from(processClass).where(ATTR_FLOW_ID, EQ, flowId).getCard();
    }

    public static boolean isSystemAttrAndShouldBeSkipped(Attribute attribute) {
        return attribute.hasNotServiceListPermission() || set(ATTR_FLOW_DATA, ATTR_FLOW_STATUS).contains(attribute.getName());
    }

    @Nullable
    public static String buildFlowCardCode(List<String> activityDefinitionIds, Process process) {
        List<String> activities = activityDefinitionIds;
        if (activities.isEmpty()) {
            return null;
        } else {
            String taskId = activities.get(0);
            TaskDefinition taskDefinition = process.getTaskById(taskId);
            String label = defaultString(taskDefinition.getDescription());
            if (size(activities) > 1) {
                return format("%s, ...", label);
            } else {
                return label;
            }
        }
    }

}
