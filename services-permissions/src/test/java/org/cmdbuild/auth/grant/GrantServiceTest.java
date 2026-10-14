/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth.grant;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.*;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RoleType;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.etl.gate.inner.EtlGateRepository;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.eventbus.EventBusServiceImpl;
import org.cmdbuild.homepage.HomepageRepository;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.view.ViewDefinitionService;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author ataboga
 */
public class GrantServiceTest {

    DashboardRepository dashboardRepository = mock(DashboardRepository.class);
    OfflineService offlineService = mock(OfflineService.class);
    EtlGateRepository etlGateRepository = mock(EtlGateRepository.class);
    EtlTemplateRepository importExportTemplateService = mock(EtlTemplateRepository.class);
    EventBusService grantEventBus = new EventBusServiceImpl();
    ReportService reportService = mock(ReportService.class);
    GrantDataRepository repository = mock(GrantDataRepository.class);
    DaoService dao = mock(DaoService.class);
    ViewDefinitionService viewService = mock(ViewDefinitionService.class);
    CardFilterService filterStore = mock(CardFilterService.class);
    CustomPageService customPagesService = mock(CustomPageService.class);
    CacheService cacheService = mock(CacheService.class);
    HomepageRepository homepageRepository = mock(HomepageRepository.class);
    NavTreeService navTreeService = mock(NavTreeService.class);
    GrantService mockGrantService = new GrantServiceImpl(dashboardRepository, offlineService, etlGateRepository, importExportTemplateService, grantEventBus, reportService, repository, dao, viewService, filterStore, customPagesService, cacheService, homepageRepository, navTreeService);

    AtomicLong ids = new AtomicLong();
    Role role = RoleImpl.builder().withId(ids.incrementAndGet()).withName("Role").withType(RoleType.DEFAULT).build();

    @Test
    @Ignore
    public void grantTest() {
        GrantDataImpl grant1 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_CLASS).withMode(GM_READ).withClassName("InternalEmployee").build();
        GrantDataImpl grant2 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_PROCESS).withMode(GM_READ).withClassName("AssetMgt").build();
        GrantDataImpl grant3 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_VIEW).withMode(GM_READ).withObjectCode("ThisIsAView").build();
        GrantDataImpl grant4 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_FILTER).withMode(GM_READ).withObjectId(ids.incrementAndGet()).build();
        GrantDataImpl grant5 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_CUSTOMPAGE).withMode(GM_READ).withObjectId(ids.incrementAndGet()).build();
        GrantDataImpl grant6 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_REPORT).withMode(GM_READ).withObjectCode("ThisIsAReport").build();
        GrantDataImpl grant7 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_ETLTEMPLATE).withMode(GM_READ).withObjectCode("ThisIsAImportEmport").build();
        GrantDataImpl grant8 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_DASHBOARD).withMode(GM_READ).withObjectCode("ThisIsADashboard").withObjectId(ids.incrementAndGet()).build();
        GrantDataImpl grant9 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_ETLGATE).withMode(GM_READ).withObjectCode("ThisIsAGate").withObjectId(ids.incrementAndGet()).build();
//        GrantDataImpl grant10 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_OFFLINE).withMode(GM_READ).withObjectCode("custompage").withObjectId(ids.incrementAndGet()).build();
        GrantDataImpl grant11 = GrantDataImpl.builder().withRoleId(role.getId()).withType(POT_HOMEPAGE).withMode(GM_READ).withObjectCode("§ThisIsAHomepage").withObjectId(ids.incrementAndGet()).build();
        List<GrantData> grants = list(grant1, grant2, grant3, grant4, grant5, grant6, grant7, grant8, grant9, grant11);
        when(repository.getGrantsForRole(anyLong())).thenReturn(grants);
        GrantData getGrant1 = mockGrantService.getGrantDataByRoleAndTypeAndName(role.getId(), grant1.getType(), grant1.getObjectId().toString());
        assertEquals(grant1.getId(), getGrant1.getId());
    }
}
