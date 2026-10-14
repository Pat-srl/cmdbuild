/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.workflow.WorkflowService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("administration/search")
@Tags({
        @Tag(name = "Search", description = "Search API"),
        @Tag(name = "Administration")
})
@Component
public class SearchWs {

    private final CmMapUtils.FluentMap<String, ModelSearch> searchStrategy = map();

    public SearchWs(UserClassService userClassService, DaoService daoService, LookupService lookupService, DashboardService dashboardService, ReportService reportService, UiComponentRepository uiComponentRepository, RoleRepository roleRepository, JobService jobService, EtlGateService etlGateService, EtlTemplateService etlTemplateService, CardFilterService cardFilterService, ViewService viewService, WorkflowService workflowService, UserDomainService userDomainService, WaterwayDescriptorService waterwayDescriptorService) {
        this.searchStrategy.with(
                "classes", new ClasseSearch(checkNotNull(userClassService)),
                "processes", new ProcessSearch(checkNotNull(workflowService)),
                "dms/models", new DmsModelSearch(checkNotNull(daoService)),
                "domains", new DomainSearch(checkNotNull(userDomainService)),
                "lookup_types", new LookupSearch(checkNotNull(lookupService)),
                "lookup/types", new LookupSearch(lookupService),
                "dms/categories", new DmsCategoriesSearch(lookupService),
                "dashboards", new DashboardSearch(checkNotNull(dashboardService)),
                "reports", new ReportSearch(checkNotNull(reportService)),
                "custompages", new CustomPageSearch(checkNotNull(uiComponentRepository)),
                "components/contextmenu", new ContextMenuSearch(uiComponentRepository),
                "components/widget", new WidgetSearch(uiComponentRepository),
                "roles", new RoleSearch(checkNotNull(roleRepository)),
                "jobs", new JobSearch(checkNotNull(jobService)),
                "etl/templates", new EtlTemplateSearch(checkNotNull(etlTemplateService), daoService),
                "etl/gates", new EtlGateSearch(checkNotNull(etlGateService), etlTemplateService, daoService),
                "filters", new StoredFilterSearch(checkNotNull(cardFilterService), daoService),
                "views", new ViewSearch(checkNotNull(viewService), daoService),
                "busdescriptors", new BusdescriptorSearch(checkNotNull(waterwayDescriptorService)));

    }

    @GET
    @Path("/{itemType:.+}")
    @Operation(
            summary = "Search for items",
            description = "Search for items",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The requested item type is not supported"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object search(
            @PathParam("itemType") @Parameter(description = "Type of the item to search for", required = true) String type,
            @BeanParam WsQueryOptions query
    ) {
        if (!searchStrategy.containsKey(type)) {
            throw runtime("Model object =< %s > not supported", type);
        }
        return response(paged(searchStrategy.get(type).search(query.getQuery().getFilter()), query.getOffset(), query.getLimit()));
    }

    @GET
    @Path("/{itemType1}/{itemType2}")//TODO fix ws framework, improve this
    @Operation(
            summary = "Search for items with two-level type",
            description = "Search for items with two-level type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The requested item type is not supported"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object search2(
            @PathParam("itemType1") @Parameter(description = "Type of the first level item to search for", required = true) String type1,
            @PathParam("itemType2") @Parameter(description = "Type of the second level item to search for", required = true) String type2,
            @BeanParam WsQueryOptions query
    ) {
        return search(type1 + "/" + type2, query);
    }
}
