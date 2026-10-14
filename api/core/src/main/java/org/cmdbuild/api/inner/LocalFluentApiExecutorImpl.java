package org.cmdbuild.api.inner;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Suppliers.memoize;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Streams.stream;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.api.LookupWrapper;
import org.cmdbuild.api.fluent.Attachment;
import org.cmdbuild.api.fluent.AttachmentDescriptor;
import org.cmdbuild.api.fluent.AttachmentDescriptorImpl;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.CreateReport;
import org.cmdbuild.api.fluent.DownloadedReport;
import org.cmdbuild.api.fluent.ExistingCard;
import org.cmdbuild.api.fluent.ExistingProcessInstance;
import org.cmdbuild.api.fluent.ExistingRelation;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.FunctionCall;
import org.cmdbuild.api.fluent.LazyAttachmentImpl;
import org.cmdbuild.api.fluent.Lookup;
import org.cmdbuild.api.fluent.NewCard;
import org.cmdbuild.api.fluent.NewProcessInstance;
import org.cmdbuild.api.fluent.NewRelation;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptor;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptorImpl;
import org.cmdbuild.api.fluent.QueryAllLookup;
import org.cmdbuild.api.fluent.QueryClass;
import org.cmdbuild.api.fluent.QuerySingleLookup;
import org.cmdbuild.api.fluent.Relation;
import org.cmdbuild.api.fluent.RelationsQuery;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.urlToByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.url.CmUrlUtils.toFileUrl;
import org.cmdbuild.workflow.WorkflowService;
import static org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy.SET_ALL_CLASS_VARIABLES;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.utils.WfWidgetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalFluentApiExecutorImpl implements FluentApiExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static Function<org.cmdbuild.lookup.LookupValue, Lookup> STORE_TO_API_LOOKUP = (org.cmdbuild.lookup.LookupValue input) -> new LookupWrapper(input);

    private final LookupService lookupService;
    private final StoredFunctionService functionService;
    private final WorkflowTypeConverter typeConverter;
    private final ApiConverterService converterService;
    private final DaoService dao;
    private final ReportService reportService;
    private final WorkflowService workflowService;
    private final DmsService dmsService;

    public LocalFluentApiExecutorImpl(LookupService lookupService, StoredFunctionService functionService, WorkflowTypeConverter typeConverter, ApiConverterService converterService, DaoService dao, ReportService reportService, WorkflowService workflowService, DmsService dmsService) {
        this.lookupService = checkNotNull(lookupService);
        this.functionService = checkNotNull(functionService);
        this.typeConverter = checkNotNull(typeConverter);
        this.converterService = checkNotNull(converterService);
        this.dao = checkNotNull(dao);
        this.reportService = checkNotNull(reportService);
        this.workflowService = checkNotNull(workflowService);
        this.dmsService = checkNotNull(dmsService);
    }

    @Override
    public void update(ExistingCard card) {
        if (dao.getClasse(card.getClassName()).isStandardClass()) {
            boolean hasDifferences = false;
            org.cmdbuild.dao.beans.Card dbCard = dao.getCard(card.getId());
            for (String attributeName : card.getAttributeNames()) {
                if (!equal(dbCard.get(attributeName), card.getAttributes().get(attributeName))) {
                    hasDifferences = true;
                }
            }
            if (hasDifferences) {
                dao.update(converterService.apiCardToDaoCard(card));
            }
        } else {
            dao.update(converterService.apiCardToDaoCard(card));
        }
        updateAttachments(card);
    }

    @Override
    public Iterable<Lookup> fetch(QueryAllLookup queryLookup) {
        Iterable<org.cmdbuild.lookup.LookupValue> allLookup = lookupService.getAllLookup(queryLookup.getType());
        Iterable<Lookup> result = Iterables.transform(allLookup, STORE_TO_API_LOOKUP);
        return result;
    }

    @Override
    public Lookup fetch(QuerySingleLookup querySingleLookup) {
        Integer id = querySingleLookup.getId();
        org.cmdbuild.lookup.LookupValue input = lookupService.getLookup(Long.valueOf(id));
        Lookup result = STORE_TO_API_LOOKUP.apply(input);
        return result;
    }

    @Override
    public CardDescriptor create(NewCard card) {
        return converterService.daoCardToApiCard(dao.create(CardImpl.buildCard(dao.getClasse(card.getClassName()), card.getAttributes())));
    }

    @Override
    public void delete(ExistingCard card) {
        dao.delete(card.getClassName(), card.getId());
    }

    @Override
    public Card fetch(ExistingCard card) {
        return converterService.daoCardToApiCard(dao.getCard(card.getClassName(), card.getId()));
    }

    @Override
    public List<Card> fetchCards(QueryClass query) {
        return dao.select(DaoQueryOptionsImpl.builder().withAttrs(query.getRequestedAttributes()).build()).from(query.getClassName()).accept(q -> {
            try {
                map(query.getAttributes()).mapValues(WfWidgetUtils::convertValueForWidget).forEach((k, v) -> {
                    q.where(AttributeFilterConditionImpl.eq(k, v).toAttributeFilter().toCmdbFilters());
                });
            } catch (Exception ex) {
                throw runtime(ex, "error building filter form attributes = %s", query.getAttributes());
            }
        }).where(query.getFilter()).getCards().stream().map(converterService::daoCardToApiCard).collect(toList());
    }

    @Override
    public void create(NewRelation relation) {
        dao.create(RelationImpl.builder()
                .withType(dao.getDomain(relation.getDomainName()))
                .withSourceCard(dao.getCard(relation.getClassName1(), relation.getCardId1()))
                .withTargetCard(dao.getCard(relation.getClassName2(), relation.getCardId2()))
                .withAttributes(relation.getAttributes())
                .build());
    }

    @Override
    public void delete(ExistingRelation relation) {
        CMRelation toDelete = dao.selectAll().from(dao.getDomain(relation.getDomainName()))
                .where(ATTR_IDOBJ1, EQ, relation.getCardId1())
                .where(ATTR_IDOBJ2, EQ, relation.getCardId2())
                //				.where(ATTR_IDCLASS1, EQ, relation.getCardId1()) TODO
                //				.where(ATTR_IDCLASS2, EQ, relation.getCardId2()) TODO
                .getRelation();
        dao.delete(toDelete);
    }

    @Override
    public List<Relation> fetch(RelationsQuery query) {//TODO verify this method code, adapted from DataAccessLogicHelper and PrivateImpl and WsFluentApiExecutor
        String domainName = query.getDomainName();
        Long cardId = (long) query.getCardId();
//        dao.getCard(cardId).getClassName();
        Domain domain = dao.getDomain(domainName);
        Classe cmClass = dao.getType(card(query.getClassName(), cardId));
//        if (cmClass.isSuperclass()) {
//            cmClass = dao.getCard(className, cardId).getType();
//        }
//		DomainWithSource dom;
//		if (domainName != null) {
//			if (cmClass == null) {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._1.toString());
//			} else if (domain.getSourceClass().isAncestorOf(cmClass)) {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._1.toString());
//			} else {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._2.toString());
//			}
//		} else {
//			dom = null;
//		}

//		List<Relation> relations = list();
//		if(isBlank(className)){
//			className=domain.getSourceClass().getName();//meh
//		}
        return dao.selectAll().from(domain).whereExpr("( \"IdClass1\" = ?::regclass AND \"IdObj1\" = ? ) OR ( \"IdClass2\" = ?::regclass AND \"IdObj2\" = ? )",
                entryTypeToSqlExpr(cmClass), cardId, entryTypeToSqlExpr(cmClass), cardId)
                .getRelations().stream().map((r) -> new org.cmdbuild.api.fluent.RelationImpl( //TODO check this
                r.getType().getName(),
                new CardDescriptorImpl(r.getSourceCard().getClassName(), r.getSourceCard().getId()),
                new CardDescriptorImpl(r.getTargetCard().getClassName(), r.getTargetCard().getId())
        //,r.getAllValuesAsMap()//TODO convert
        )).collect(toList());
//		}else{

//		GetRelationListResponse relationList = dataAccessLogic.getRelationList(CardIdAndClassNameImpl.card((className == null) ? domain.getSourceClass().getName() : className, cardId), dom);
//		for (DomainInfo domainInfo : relationList) {
//			for (RelationInfo relationInfo : domainInfo) {
//				Relation relation = new Relation(domainInfo.getQueryDomain().getDomain().getName());
//				if (domainInfo.getQueryDomain().getQuerySource().equals(Source._1.toString())) {
//					relation.setCard1(domain.getSourceClass().getName(), relationInfo.getSourceId());
//					relation.setCard2(relationInfo.getTargetCard().getType().getName(), relationInfo.getTargetId());
//				} else {
//					relation.setCard1(relationInfo.getTargetCard().getType().getName(), relationInfo.getTargetId());
//					relation.setCard2(domain.getTargetClass().getName(), relationInfo.getSourceId());
//				}
//				relations.add(relation);
//			}
//		}
//		return relations;
    }

    @Override
    public Map<String, Object> execute(FunctionCall functionCallParams) {
        StoredFunction function = functionService.getFunctionByName(functionCallParams.getFunctionName());
        Map<String, Object> rawOutput = functionService.callFunction(function, functionCallParams.getInputs()); //TODO conversion of input params (??)

        Map<String, Object> output = map();
        function.getOutputParameters().forEach((param) -> {
            Object value = rawOutput.get(param.getName());
            value = typeConverter.cardValueToFlowValue(value, param.getType());
            output.put(param.getName(), value);
        });

        logger.trace("function output = \n\n{}\n", mapToLoggableStringLazy(output));

        return output;
    }

    @Override
    public DownloadedReport download(CreateReport report) {
        DataHandler dataHandler = reportService.executeReportAndDownload(report.getTitle(), ReportFormat.valueOf(report.getFormat().toUpperCase()), report.getParameters());
        File tempFile = new File(tempDir(Duration.ofHours(1)), dataHandler.getName());
        CmIoUtils.copy(dataHandler, tempFile);
        return new DownloadedReport(tempFile);
    }

    @Override
    public ProcessInstanceDescriptor createProcessInstance(NewProcessInstance processCard, AdvanceProcess advance) {
        Flow flow = workflowService.startProcess(processCard.getClassName(), processCard.getAttributes(), SET_ALL_CLASS_VARIABLES, AdvanceProcess.YES.equals(advance)).getFlowCard();
        return new ProcessInstanceDescriptorImpl(flow.getClassName(), flow.getCardId(), flow.getFlowId());
    }

    @Override
    public void updateProcessInstance(ExistingProcessInstance processCard, AdvanceProcess advance) {
        workflowService.updateProcessWithOnlyTask(processCard.getClassName(), processCard.getId(), processCard.getAttributes(), SET_ALL_CLASS_VARIABLES, equal(AdvanceProcess.YES, advance));
    }

    @Override
    public void suspendProcessInstance(ExistingProcessInstance processCard) {
        workflowService.suspendProcess(processCard.getClassName(), processCard.getId());
    }

    @Override
    public void resumeProcessInstance(ExistingProcessInstance processCard) {
        workflowService.resumeProcess(processCard.getClassName(), processCard.getId());
    }

    @Override
    public Iterable<AttachmentDescriptor> fetchAttachments(CardDescriptor source) {
        List<DocumentInfoAndDetail> attachments = dmsService.getCardAttachments(source.getClassName(), source.getId());
        return list(attachments).map(a -> new AttachmentDescriptorImpl(a.getFileName(), a.getDescription(), a.getCategory(), a.getMetadataMap()));
    }

    @Override
    public void upload(CardDescriptor source, Iterable<? extends Attachment> attachments) {
        attachments.forEach(a -> {
            byte[] data;
            if (a.hasUrl()) {
                data = urlToByteArray(a.getUrl());
            } else {
                Object obj = checkNotNull(a.getDocument(), "missing attachment content");
                if (obj instanceof DataSource dataSource) {//TODO move conversion code elsewhere
                    data = toByteArray(dataSource);
                } else if (obj instanceof DataHandler dataHandler) {
                    data = toByteArray(dataHandler);
                } else if (obj instanceof byte[] bs) {
                    data = bs;
                } else if (obj instanceof String string) {
                    data = string.getBytes(StandardCharsets.UTF_8);
                } else if (obj instanceof File file) {
                    data = toByteArray(file);
                } else {
                    throw runtime("unsupported document type = {}", obj.getClass());
                }
            }
            dmsService.create(source.getClassName(), source.getId(), DocumentDataImpl.builder()
                    .withFilename(a.getName())
                    .withCategory(a.getCategory())
                    .withData(data)
                    .withMetadata(a.getMeta())
                    .withDescription(a.getDescription())
                    .build());
        });
    }

    @Override
    public Iterable<Attachment> download(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        return stream(attachments).map(a -> {
            DataHandler documentData = dmsService.getDocumentData(source.getClassName(), source.getId(), a.getName());
            DataSource data = newDataSource(documentData::getInputStream, documentData.getContentType(), a.getName());
            String category = Optional.ofNullable(emptyToNull(a.getCategory())).map(c -> lookupService.getLookup(Long.valueOf(c)).getCode()).orElse(null);
            return new LazyAttachmentImpl(a.getName(), a.getDescription(), category, memoize(() -> toFileUrl(data)), () -> data);
        }).collect(toList());
    }

    @Override
    public void delete(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        attachments.forEach((a) -> dmsService.delete(source.getClassName(), source.getId(), a.getName()));
    }

    @Override
    public void copy(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        stream(attachments).forEach(a -> dmsService.copy(card(source.getClassName(), source.getId()), a.getName(), card(destination.getClassName(), destination.getId())));
    }

    @Override
    public void copyAndMerge(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        stream(attachments).forEach(a -> dmsService.copyAndMerge(card(source.getClassName(), source.getId()), a.getName(), card(destination.getClassName(), destination.getId())));
    }

    @Override
    public void move(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        stream(attachments).forEach(a -> dmsService.move(card(source.getClassName(), source.getId()), a.getName(), card(destination.getClassName(), destination.getId())));
    }

    @Override
    public void abortProcessInstance(ExistingProcessInstance processCard) {
        workflowService.abortProcess(processCard.getClassName(), processCard.getId());
    }

    private void updateAttachments(ExistingCard<?> card) {
        card.getAttachments().forEach(a -> updateAttachment(card, a));
    }

    private void updateAttachment(ExistingCard card, Attachment a) {
        DocumentInfoAndDetail current = dmsService.getCardAttachmentOrNull(card.getClassName(), card.getId(), a.getName());
        byte[] data = urlToByteArray(a.getUrl());
        DocumentData documentData = DocumentDataImpl.builder().withFilename(a.getName()).withCategory(a.getCategory()).withDescription(a.getDescription()).withData(data).withMetadata(a.getMeta()).build();
        if (current == null) {
            dmsService.create(card.getClassName(), card.getId(), documentData);
        } else {
            dmsService.updateDocumentWithAttachmentId(card.getClassName(), card.getId(), current.getDocumentId(), documentData);//TODO check this
        }
    }
}
