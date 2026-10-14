/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.workflow.WorkflowService;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.*;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import static org.cmdbuild.uicomponents.data.UiComponentType.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * @author ldare
 */
class ClasseSearch extends AbstractModelSearch {

    private final UserClassService userClassService;

    public ClasseSearch(UserClassService userClassService) {
        this.userClassService = userClassService;
    }

    @Override
    protected FluentMap<String, Object> applyMapping(EntryType classe) {
        return mapOf(String.class, Object.class).with("_id", classe.getName(), "name", classe.getName(), "description", classe.getDescription())
                .with("type", serializeEnum(((Classe) classe).getClassType()));
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                userClassService.getAllUserClasses().stream().filter(c -> !equal(c.getName(), BASE_CLASS_NAME)),
                this::getSummaryFields,
                this::applyMapping,
                this::getPublicAttributes,
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class ProcessSearch extends AbstractModelSearch {

    private final WorkflowService workflowService;

    public ProcessSearch(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                workflowService.getAllProcessClasses().stream().filter(c -> !equal(c.getName(), BASE_PROCESS_CLASS_NAME)),
                this::getSummaryFields,
                this::applyMapping,
                this::getPublicAttributes,
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class DmsModelSearch extends AbstractModelSearch {

    private final DaoService daoService;

    public DmsModelSearch(DaoService daoService) {
        this.daoService = daoService;
    }


    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                daoService.getAllClasses().stream().filter(Classe::isDmsModel),
                this::getSummaryFields,
                this::applyMapping,
                this::getPublicAttributes,
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class DomainSearch extends AbstractModelSearch {

    private final UserDomainService userDomainService;

    public DomainSearch(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                userDomainService.getUserDomains().stream(),
                this::getSummaryFields,
                this::applyMapping,
                this::getPublicAttributes,
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class LookupSearch extends AbstractModelSearch {

    private final LookupService lookupService;

    public LookupSearch(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                lookupService.getAllTypes().stream().filter(LookupType::isDefaultSpeciality).filter(LookupType::isAccessDefault),
                this::getSummaryFields,
                this::applyMapping,
                t -> getLookupValues(t, lookupService),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class DmsCategoriesSearch extends AbstractModelSearch {

    private final LookupService lookupService;

    public DmsCategoriesSearch(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                lookupService.getAllTypes().stream().filter(LookupType::isDmsCategorySpeciality),
                this::getSummaryFields,
                this::applyMapping,
                t -> getLookupValues(t, lookupService),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class DashboardSearch extends AbstractModelSearch {

    private final DashboardService dashboardService;

    public DashboardSearch(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                dashboardService.getAll().stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class ReportSearch extends AbstractModelSearch {

    private final ReportService reportService;

    public ReportSearch(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                reportService.getAll().stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class CustomPageSearch extends AbstractModelSearch {

    private final UiComponentRepository uiComponentRepository;

    public CustomPageSearch(UiComponentRepository uiComponentRepository) {
        this.uiComponentRepository = uiComponentRepository;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                uiComponentRepository.getAllByType(UCT_CUSTOMPAGE).stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class ContextMenuSearch extends AbstractModelSearch {

    private final UiComponentRepository uiComponentRepository;

    public ContextMenuSearch(UiComponentRepository uiComponentRepository) {
        this.uiComponentRepository = uiComponentRepository;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                uiComponentRepository.getAllByType(UCT_CONTEXTMENU).stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class WidgetSearch extends AbstractModelSearch {

    private final UiComponentRepository uiComponentRepository;

    public WidgetSearch(UiComponentRepository uiComponentRepository) {
        this.uiComponentRepository = uiComponentRepository;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                uiComponentRepository.getAllByType(UCT_WIDGET).stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class RoleSearch extends AbstractModelSearch {

    private final RoleRepository roleRepository;

    public RoleSearch(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                roleRepository.getAllGroups().stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class JobSearch extends AbstractModelSearch {

    private final JobService jobService;

    public JobSearch(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                jobService.getAllJobs().stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}

class EtlTemplateSearch extends AbstractModelSearch {

    private final EtlTemplateService etlTemplateService;
    private final DaoService daoService;

    public EtlTemplateSearch(EtlTemplateService etlTemplateService, DaoService daoService) {
        this.etlTemplateService = etlTemplateService;
        this.daoService = daoService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                etlTemplateService.getTemplates().stream(),
                t -> getSummaryFields(t, daoService),
                t -> applyMapping(t, daoService),
                filter);
    }
}

class EtlGateSearch extends AbstractModelSearch {

    private final EtlGateService etlGateService;
    private final EtlTemplateService etlTemplateService;
    private final DaoService daoService;

    public EtlGateSearch(EtlGateService etlGateService, EtlTemplateService etlTemplateService, DaoService daoService) {
        this.etlGateService = etlGateService;
        this.etlTemplateService = etlTemplateService;
        this.daoService = daoService;
    }

    @Override
    protected CmCollectionUtils.FluentList<String> getSummaryFields(EtlTemplate etlTemplate, DaoService daoService) {
        return list(etlTemplate.getCode(), etlTemplate.getDescription(), etlTemplate.getTargetName(), equal(ET_CLASS, etlTemplate.getTargetType()) ? getClassDescriptionIfExists(etlTemplate.getTargetName(), daoService) : null);
    }

    @Override
    protected FluentMap<String, Object> applyMapping(EtlTemplate etlTemplate, DaoService daoService) {
        return map("_id", etlTemplate.getCode(), "name", etlTemplate.getCode(), "description", etlTemplate.getDescription(), "type", serializeEnum(etlTemplate.getType()), "target", etlTemplate.getTargetName(), "target_description", equal(ET_CLASS, etlTemplate.getTargetType()) ? getClassDescriptionIfExists(etlTemplate.getTargetName(), daoService) : null);
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                etlGateService.getAll().stream().filter(g -> g.hasSingleHandlerOfType(ETLHT_IFC, ETLHT_CAD, ETLHT_DATABASE)),
                this::getSummaryFields,//TODO target class ???
                this::applyMapping,//TODO target class ???
                g -> getEtlTemplates(g, etlTemplateService),
                t -> getSummaryFields(t, daoService),
                t -> applyMapping(t, daoService),
                filter);
    }
}

class StoredFilterSearch extends AbstractModelSearch {

    private final CardFilterService cardFilterService;
    private final DaoService daoService;

    public StoredFilterSearch(CardFilterService cardFilterService, DaoService daoService) {
        this.cardFilterService = cardFilterService;
        this.daoService = daoService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                cardFilterService.readAllSharedFilters().stream(),
                f -> getSummaryFields(f, daoService),
                f -> applyMapping(f, daoService),
                filter);
    }
}

class ViewSearch extends AbstractModelSearch {

    private final ViewService viewService;
    private final DaoService daoService;

    public ViewSearch(ViewService viewService, DaoService daoService) {
        this.viewService = viewService;
        this.daoService = daoService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                viewService.getAllSharedViews().stream(),
                v -> getSummaryFields(v, daoService),
                v -> applyMapping(v, daoService),
                filter);
    }
}

class BusdescriptorSearch extends AbstractModelSearch {

    private final WaterwayDescriptorService waterwayDescriptorService;

    public BusdescriptorSearch(WaterwayDescriptorService waterwayDescriptorService) {
        this.waterwayDescriptorService = waterwayDescriptorService;
    }

    @Override
    public List<Map<String, Object>> search(CmdbFilter filter) {
        return doSearch(
                waterwayDescriptorService.getAllDescriptors().stream(),
                this::getSummaryFields,
                this::applyMapping,
                filter);
    }
}