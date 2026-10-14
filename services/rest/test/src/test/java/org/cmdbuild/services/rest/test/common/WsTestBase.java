/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.common;

import org.cmdbuild.ai.AiConfiguration;
import org.cmdbuild.api.SystemApi;
import org.cmdbuild.asyncjob.AsyncRequestJob;
import org.cmdbuild.asyncjob.AsyncRequestJobService;
import org.cmdbuild.audit.RequestTrackingRepository;
import org.cmdbuild.auth.grant.GrantDataRepository;
import org.cmdbuild.auth.grant.GrantService;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.auth.login.PasswordService;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserFilteredRepository;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.userrole.UserRoleRepository;
import org.cmdbuild.bim.BimService;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.chat.ChatService;
import org.cmdbuild.chat.PeerService;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.config.*;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.dao.config.inner.PatchService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.FkDomainRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.dao.DocumentInfoRepository;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.loader.EtlTemplateInlineProcessorService;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.webhook.WebhookService;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.stylerules.GisStyleRulesService;
import org.cmdbuild.homepage.HomepageService;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.modeldiff.loader.SchemaLoaderService;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.participant.ParticipantService;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import org.cmdbuild.preload.PreloadService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.helpers.CardsForDomainFetcher;
import org.cmdbuild.service.rest.common.serializationhelpers.*;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.cmdbuild.service.rest.v4.serializationhelpers.UserSerializationHelper;
import org.cmdbuild.services.serialization.EmailTemplateSerializationHelper;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileHelper;
import org.cmdbuild.services.serialization.widget.WidgetHelper;
import org.cmdbuild.systemplugin.SystemPluginService;
import org.cmdbuild.systemplugin.UploadSystemPluginService;
import org.cmdbuild.temp.TempService;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import org.cmdbuild.userconfig.UserConfigService;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowGraphService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.inner.FlowHistoryService;
import static org.mockito.Mockito.mock;

/**
 * Base class for REST web service tests in CMDBuild.
 * <p>
 * This class provides a centralized collection of mock objects and helper
 * factory methods for testing REST web service endpoints. It serves as a
 * foundation for test classes that need to mock the various services,
 * repositories, and configurations used throughout the CMDBuild application.
 * </p>
 * <p>
 * The class includes mocks for:
 * <ul>
 * <li>Configuration objects (Workflow, Multitenant, Core, UI, DMS, Email,
 * etc.)</li>
 * <li>Service implementations (Workflow, User, Widget, Form Structure,
 * etc.)</li>
 * <li>Repository implementations (Classe, Domain, User, Role, etc.)</li>
 * <li>Support utilities and helpers (Translation, Widget, File handling,
 * etc.)</li>
 * </ul>
 * </p>
 * <p>
 * It also provides convenience factory methods to build complex serialization
 * helpers that are commonly used in REST service tests, such as:
 * <ul>
 * <li>Card serialization helpers for v3 API</li>
 * <li>Domain, Report, and Email Template serialization helpers</li>
 * <li>Process and View serialization helpers</li>
 * <li>GIS and Chat peer serialization helpers</li>
 * </ul>
 * </p>
 * <p>
 * Test classes that perform unit tests on webservices should extend this base
 * class to inherit all mock objects and helper factory methods.
 * </p>
 *
 * @author ldare
 * @see org.cmdbuild.services.rest.test.common
 */
public class WsTestBase {

    protected final WorkflowConfiguration workflowConfiguration = mock(WorkflowConfiguration.class);
    protected final MultitenantConfiguration multitenantConfiguration = mock(MultitenantConfiguration.class);
    protected final CoreConfiguration coreConfiguration = mock(CoreConfiguration.class);
    protected final UiConfiguration uiConfiguration = mock(UiConfiguration.class);
    protected final DmsConfiguration dmsConfiguration = mock(DmsConfiguration.class);
    protected final EmailConfiguration emailConfiguration = mock(EmailConfiguration.class);
    protected final WebSocketConfiguration webSocketConfiguration = mock(WebSocketConfiguration.class);
    protected final GisConfiguration gisConfiguration = mock(GisConfiguration.class);
    protected final BimConfiguration bimConfiguration = mock(BimConfiguration.class);
    protected final GraphConfiguration graphConfiguration = mock(GraphConfiguration.class);
    protected final MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    protected final SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);
    protected final AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
    protected final PasswordManagementConfiguration passwordManagementConfiguration = mock(PasswordManagementConfiguration.class);
    protected final SchedulerConfiguration schedulerConfiguration = mock(SchedulerConfiguration.class);
    protected final EtlConfiguration etlConfiguration = mock(EtlConfiguration.class);
    protected final CalendarServiceConfiguration calendarServiceConfiguration = mock(CalendarServiceConfiguration.class);
    protected final UiFilterConfiguration uiFilterConfiguration = mock(UiFilterConfiguration.class);
    protected final AiConfiguration aiConfiguration = mock(AiConfiguration.class);
    protected final HomepageConfiguration homepageConfiguration = mock(HomepageConfiguration.class);

    protected final BuildInfoService buildInfoService = mock(BuildInfoService.class);
    protected final WorkflowService workflowService = mock(WorkflowService.class);
    protected final UserClassService userClassService = mock(UserClassService.class);
    protected final UserCardService userCardService = mock(UserCardService.class);
    protected final WidgetService widgetService = mock(WidgetService.class);
    protected final FormStructureService formStructureService = mock(FormStructureService.class);
    protected final ObjectTranslationService objectTranslationService = mock(ObjectTranslationService.class);
    protected final ContextMenuComponentService contextMenuComponentService = mock(ContextMenuComponentService.class);
    protected final BimService bimService = mock(BimService.class);
    protected final EasyuploadService easyuploadService = mock(EasyuploadService.class);
    protected final LookupService lookupService = mock(LookupService.class);
    protected final DaoService daoService = mock(DaoService.class);
    protected final UserDomainService userDomainService = mock(UserDomainService.class);
    protected final DmsService dmsService = mock(DmsService.class);
    protected final ParticipantService participantService = mock(ParticipantService.class);
    protected final EmailTemplateService emailTemplateService = mock(EmailTemplateService.class);
    protected final CalendarService calendarService = mock(CalendarService.class);
    protected final AsyncRequestJobService asyncRequestJobService = mock(AsyncRequestJobService.class);
    protected final AsyncRequestJob asyncRequestJob = mock(AsyncRequestJob.class);
    protected final CardFilterService cardFilterService = mock(CardFilterService.class);
    protected final CoreComponentService coreComponentService = mock(CoreComponentService.class);
    protected final CustomPageService customPageService = mock(CustomPageService.class);
    protected final DashboardService dashboardService = mock(DashboardService.class);
    protected final SysReportService sysReportService = mock(SysReportService.class);
    protected final UserDomainService domainService = mock(UserDomainService.class);
    protected final EmailAccountService emailAccountService = mock(EmailAccountService.class);
    protected final EmailSignatureService emailSignatureService = mock(EmailSignatureService.class);
    protected final ReportService reportService = mock(ReportService.class);
    protected final EtlGateService etlGateService = mock(EtlGateService.class);
    protected final EtlTemplateService etlTemplateService = mock(EtlTemplateService.class);
    protected final EtlTemplateInlineProcessorService etlTemplateInlineProcessorService = mock(EtlTemplateInlineProcessorService.class);
    protected final CardsForDomainFetcher cardsForDomainFetcher = mock(CardsForDomainFetcher.class);
    protected final TempService tempService = mock(TempService.class);
    protected final WebhookService webhookService = mock(WebhookService.class);
    protected final GisService gisService = mock(GisService.class);
    protected final TranslationService translationService = mock(TranslationService.class);
    protected final NavTreeService navTreeService = mock(NavTreeService.class);
    protected final ViewService viewService = mock(ViewService.class);
    protected final ContextMenuService contextMenuService = mock(ContextMenuService.class);
    protected final WidgetComponentService widgetComponentService = mock(WidgetComponentService.class);
    protected final JobService jobService = mock(JobService.class);
    protected final GrantService grantService = mock(GrantService.class);
    protected final HomepageService homepageService = mock(HomepageService.class);

    protected final DmsProviderService dmsProviderService = mock(DmsProviderService.class);
    protected final EmailService emailService = mock(EmailService.class);
    protected final MinionService minionService = mock(MinionService.class);
    protected final DirectoryService directoryService = mock(DirectoryService.class);
    protected final GlobalConfigService globalConfigService = mock(GlobalConfigService.class);
    protected final PatchService patchService = mock(PatchService.class);
    protected final WaterwayDescriptorService waterwayDescriptorService = mock(WaterwayDescriptorService.class);
    protected final ChatService chatService = mock(ChatService.class);
    protected final PeerService peerService = mock(PeerService.class);
    protected final LockService lockService = mock(LockService.class);
    protected final SessionService sessionService = mock(SessionService.class);
    protected final WaterwayService waterwayService = mock(WaterwayService.class);
    protected final GisStyleRulesService gisStyleRulesService = mock(GisStyleRulesService.class);
    protected final ClasseRepository classeRepository = mock(ClasseRepository.class);
    protected final CardHistoryService cardHistoryService = mock(CardHistoryService.class);
    protected final CardMapperService cardMapperService = mock(CardMapperService.class);
    protected final EmailTemplateProcessorService emailTemplateProcessorService = mock(EmailTemplateProcessorService.class);
    protected final LanguageService languageService = mock(LanguageService.class);
    protected final MobileAppService mobileAppService = mock(MobileAppService.class);
    protected final OfflineLoaderService offlineLoaderService = mock(OfflineLoaderService.class);
    protected final OfflineService offlineService = mock(OfflineService.class);
    protected final FlowHistoryService flowHistoryService = mock(FlowHistoryService.class);
    protected final WorkflowGraphService workflowGraphService = mock(WorkflowGraphService.class);
    protected final SchemaLoaderService schemaLoaderService = mock(SchemaLoaderService.class);
    protected final MenuService menuService = mock(MenuService.class);
    protected final UserConfigService userConfigService = mock(UserConfigService.class);
    protected final MultitenantService multitenantService = mock(MultitenantService.class);
    protected final SystemPluginService systemPluginService = mock(SystemPluginService.class);
    protected final UploadSystemPluginService uploadPluginService = mock(UploadSystemPluginService.class);
    protected final PreloadService preloadService = mock(PreloadService.class);
    protected final PasswordService passwordService = mock(PasswordService.class);
    protected final OperatorService operatorService = mock(OperatorService.class);

    protected final SystemApi systemApi = mock(SystemApi.class);

    protected final RequestTrackingRepository requestTrackingRepository = mock(RequestTrackingRepository.class);
    protected final FkDomainRepository fkDomainRepository = mock(FkDomainRepository.class);
    protected final RoleRepository roleRepository = mock(RoleRepository.class);
    protected final ExtendedRiverPlanRepository extendedRiverPlanRepository = mock(ExtendedRiverPlanRepository.class);
    protected final DocumentInfoRepository documentInfoRepository = mock(DocumentInfoRepository.class);
    protected final UserRepository userRepository = mock(UserRepository.class);
    protected final UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    protected final DomainRepository domainRepository = mock(DomainRepository.class);
    protected final UiComponentRepository uiComponentRepository = mock(UiComponentRepository.class);
    protected final GrantDataRepository grantDataRepository = mock(GrantDataRepository.class);
    protected final UserFilteredRepository userFilteredRepository = mock(UserFilteredRepository.class);

    protected final OperationUserSupplier operationUserSupplier = mock(OperationUserSupplier.class);
    protected final WidgetHelper widgetHelper = mock(WidgetHelper.class);

    public DomainSerializationHelper mockBuildDomainSerializationHelper() {
        return new DomainSerializationHelper(daoService, objectTranslationService);
    }

    public CardWsSerializationHelperv3 mockBuildCardWsSerializationHelperv3() {
        ClassSerializationHelper classSerializationHelper = mockBuildClassSerializationHelper();
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        CardAttributeFileHelper cardAttributeFileHelper = mockBuildCardAttributeFileHelper();
        return new CardWsSerializationHelperv3(
                daoService, objectTranslationService, classSerializationHelper,
                attributeTypeConversionService, widgetService,
                userRepository, cardAttributeFileHelper, widgetHelper
        );
    }

    public ReportSerializationHelper mockBuildReportSerializationHelper() {
        return new ReportSerializationHelper(objectTranslationService, reportService);
    }

    public EmailTemplateSerializationHelper mockBuildEmailTemplateSerializationHelper() {
        return new EmailTemplateSerializationHelper(emailTemplateService, objectTranslationService);
    }

    public CardAttributeFileHelper mockBuildCardAttributeFileHelper() {
        return new CardAttributeFileHelper(daoService, dmsService, documentInfoRepository, userClassService, objectTranslationService, userRepository, new PermissionsHandlerProxyImpl());
    }

    public LookupSerializationHelper mockBuildLookupSerializationHelper() {
        return new LookupSerializationHelper(objectTranslationService, lookupService);
    }

    public CalendarWsSerializationHelper mockBuildCalendarWsSerializationHelper() {
        LookupSerializationHelper lookupSerializationHelper = mockBuildLookupSerializationHelper();
        return new CalendarWsSerializationHelper(lookupSerializationHelper, objectTranslationService, daoService, participantService, emailTemplateService, calendarService);
    }

    public AttributeTypeConversionService mockBuildAttributeTypeConversionService() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        return new AttributeTypeConversionService(daoService, objectTranslationService, userClassService, userDomainService, calendarWsSerializationHelper, lookupService, dmsService);
    }

    public ContextMenuSerializationHelper mockBuildContextMenuSerializationHelper() {
        return new ContextMenuSerializationHelper(contextMenuComponentService, objectTranslationService);
    }

    public ClassSerializationHelper mockBuildClassSerializationHelper() {
        ContextMenuSerializationHelper contextMenuSerializationHelper = mockBuildContextMenuSerializationHelper();
        return new ClassSerializationHelper(widgetService, objectTranslationService, bimService, easyuploadService, userClassService, multitenantConfiguration, workflowConfiguration, coreConfiguration, uiConfiguration, contextMenuSerializationHelper, lookupService, dmsConfiguration, operationUserSupplier, roleRepository);
    }

    public ProcessWsSerializationHelper mockBuildProcessWsSerializationHelper() {
        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        ClassSerializationHelper classSerializationHelper = mockBuildClassSerializationHelper();
        return new ProcessWsSerializationHelper(cardWsSerializationHelperv3, attributeTypeConversionService, widgetService, extendedRiverPlanRepository, workflowService, formStructureService, userClassService, classSerializationHelper, objectTranslationService, roleRepository, workflowConfiguration);
    }

    public ViewSerializer mockBuildViewSerializer() {
        ContextMenuSerializationHelper contextMenuSerializationHelper = mockBuildContextMenuSerializationHelper();
        return new ViewSerializer(viewService, objectTranslationService, roleRepository, formStructureService, operationUserSupplier, contextMenuService, contextMenuSerializationHelper, uiConfiguration);
    }

    public HistorySerializationHelper mockBuildHistorySerializationHelper() {
        return new HistorySerializationHelper(userRepository, objectTranslationService);
    }

    public GisValueSerializationHelper mockBuildGisValueSerializationHelper() {
        return new GisValueSerializationHelper(daoService, dmsService, emailTemplateProcessorService, userClassService);
    }

    public ChatPeerSerializationHelper mockBuildChatPeerSerializationHelper() {
        return new ChatPeerSerializationHelper(peerService);
    }

    public EmailSerializationHelper mockBuildEmailSerializationHelper() {
        return new EmailSerializationHelper(dmsService, emailTemplateService);
    }

    public MenuSerializationHelper mockBuildMenuSerializationHelper() {
        return new MenuSerializationHelper(translationService, daoService, gisService);
    }

    public UserConfigSerializationHelper mockBuildUserConfigSerializationHelper() {
        return new UserConfigSerializationHelper(userConfigService, coreConfiguration);
    }

    public SessionSerializationHelper mockBuildSessionSerializationHelper() {
        return new SessionSerializationHelper(multitenantService, objectTranslationService);
    }

    public UserSerializationHelper mockBuildUserSerializationHelper() {
        return new UserSerializationHelper(userFilteredRepository, roleRepository, userConfigService, multitenantService, sessionService);
    }
}
