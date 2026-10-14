/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.common;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.networknt.schema.utils.StringUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestDataImpl;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.audit.RequestInfoImpl;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantDataImpl;
import org.cmdbuild.auth.role.GroupConfig;
import org.cmdbuild.auth.role.GroupConfigImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.session.SessionExpirationStrategy;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionImpl;
import org.cmdbuild.auth.user.*;
import org.cmdbuild.bim.*;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;

import org.cmdbuild.calendar.beans.*;
import org.cmdbuild.cardfilter.*;
import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.chat.ChatMessageImpl;
import org.cmdbuild.chat.ChatPeer;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassImpl;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.cleanup.ViewType;
import org.cmdbuild.common.beans.CardIdAndClassNameImpl;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.TempDataSource;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.contextmenu.ContextMenuItemImpl;
import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentImpl;
import org.cmdbuild.corecomponents.CoreComponentType;
import org.cmdbuild.dao.beans.*;

import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;

import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dao.config.inner.PatchImpl;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.postgres.q3.stats.AggregateOperation;
import org.cmdbuild.dao.driver.postgres.q3.stats.AggregateQuery;
import org.cmdbuild.dao.driver.postgres.q3.stats.AggregateResult;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponseImpl;
import org.cmdbuild.dao.entrytype.*;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;

import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.function.FunctionMetadata;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionImpl;
import org.cmdbuild.dao.postgres.q3.beans.AbstractResultRow;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.inner.DashboardDataImpl;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemImpl;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.*;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.config.WaterwayItemType;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecordImpl;
import org.cmdbuild.etl.config.inner.WaterwayItemImpl;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateHandler;
import org.cmdbuild.etl.gate.inner.EtlGateHandlerImpl;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import org.cmdbuild.etl.loader.EtlProcessingResultDetailsImpl;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultErrorImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.*;
import org.cmdbuild.etl.waterway.storage.EtlMessage;
import org.cmdbuild.etl.waterway.storage.EtlMessageImpl;
import org.cmdbuild.etl.waterway.storage.WaterwayMessagesStatsImpl;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.etl.webhook.WebhookConfigImpl;
import org.cmdbuild.etl.webhook.WebhookMethod;
import org.cmdbuild.fault.FaultEventsData;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.formtrigger.FormTriggerImpl;
import org.cmdbuild.gis.*;
import org.cmdbuild.gis.geoserver.GeoserverLayerImpl;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.stylerules.GisStyleRuleset;
import org.cmdbuild.gis.stylerules.GisStyleRulesetImpl;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.inner.HomepageDataImpl;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.beans.JobRunImpl;
import org.cmdbuild.lock.*;
import org.cmdbuild.lookup.*;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.minions.*;
import org.cmdbuild.mobile.MobileAppMessageImpl;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import static org.cmdbuild.navtree.NavTreeType.NT_MENU;

import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus;
import org.cmdbuild.participant.ParticipantHeader;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.model.WsViewData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import org.cmdbuild.services.serialization.EmailTemplateSerializationHelper;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempInfoImpl;
import org.cmdbuild.template.TemplateBindings;
import org.cmdbuild.template.TemplateBindingsImpl;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.UiComponentVersionInfo;
import org.cmdbuild.uicomponents.UiComponentVersionInfoImpl;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentDataImpl;
import org.cmdbuild.uicomponents.data.UiComponentType;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

import org.cmdbuild.utils.date.Interval;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.widget.dao.WidgetDataFromDbImpl;
import org.cmdbuild.widget.model.*;
import org.cmdbuild.workflow.model.*;
import org.cmdbuild.workflow.model.ProcessImpl;
import org.jspecify.nullness.Nullable;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import static org.cmdbuild.chat.ChatMessageSourceType.CMST_SYSTEM;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_NEW;
import static org.cmdbuild.chat.ChatMessageType.CMT_OUTGOING;
import static org.cmdbuild.contextmenu.ContextMenuType.SEPARATOR;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_GATE;
import static org.cmdbuild.etl.gate.inner.EtlGate.ETL_GATE_CONFIG_SHOW_ON_CLASSES;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_TEMPLATES;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_TYPE;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_IFC;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_REALTIME;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.*;
import static org.cmdbuild.formtrigger.FormTriggerBinding.beforeInsert;
import static org.cmdbuild.gis.GisAttributeType.GAT_POINT;
import static org.cmdbuild.jobs.JobRunStatus.JRS_COMPLETED;
import static org.cmdbuild.lock.LockScope.LS_SESSION;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.navtree.NavTreeType.NT_MENU;
import static org.cmdbuild.report.ReportFormat.PDF;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_WIDGET;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import static org.cmdbuild.contextmenu.ContextMenuType.SEPARATOR;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.etl.gate.inner.EtlGate.ETL_GATE_CONFIG_SHOW_ON_CLASSES;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_TEMPLATES;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_TYPE;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_IFC;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_REALTIME;
import static org.cmdbuild.formtrigger.FormTriggerBinding.beforeInsert;
import static org.cmdbuild.gis.GisAttributeType.GAT_POINT;
import static org.cmdbuild.navtree.NavTreeType.NT_MENU;
import static org.cmdbuild.report.ReportFormat.PDF;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_WIDGET;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

/**
 *
 * @author afelice
 */
public class TestHelper_Model {

//###########################################################################################
//############################## - ATTRIBUTE - ##############################################
//###########################################################################################
    public static AttributeWithoutOwner mockBuildAttributeWithoutOwner(final String attributeName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    public static Attribute mockBuildAttribute(final String attributeName, final Classe ownerClass) {
        return AttributeImpl.builder()
                .withOwner(ownerClass)
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    public static List<Attribute> mockBuildAttributeList(Classe ownerClass) {
        return list(
                mockBuildAttribute(A_KNOWN_ATTR_NAME1, ownerClass),
                mockBuildAttribute(A_KNOWN_ATTR_NAME2, ownerClass),
                mockBuildAttribute(A_KNOWN_ATTR_NAME3, ownerClass)
        );
    }

    public static Attribute mockBuildAttribute_ForeignKeyAttributeType(final String attributeName, final Classe ownerClass) {
        return AttributeImpl.builder()
                .withOwner(ownerClass)
                .withName(attributeName)
                .withType(new ForeignKeyAttributeType("exampDestinationClassName"))
                .build();
    }

    public static Attribute mockBuildAttributeInverse(final String attributeName, Domain floorRoomDomain) {
        Classe room = floorRoomDomain.getTargetClass();
        return AttributeImpl.builder()
                .withOwner(room)
                .withName(attributeName)
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .build();
    }

    public static AttributeMetadataImpl mockBuildAttributeMetadata() {
        return AttributeMetadataImpl.builder()
                .build();
    }

//###########################################################################################
//################################# - CLASSE - ##############################################
//###########################################################################################
    public static Classe mockBuildClasse(String classeName) {
        final List<String> ancestors = emptyList();
        final List<AttributeWithoutOwner> attributes = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    public static Classe mockBuildClasse(String classeName, List<AttributeWithoutOwner> attributes) {
        final List<String> ancestors = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    public static Classe mockBuildClasse(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(attributes);
        if (!ancestors.isEmpty()) {
            builder.withAncestors(ancestors);
        }
        return builder.build();
    }

    public static ClasseImpl mockBuildClasseWithMetadata(String classeName, ClassMetadata.@Nullable ClassSpeciality classSpeciality) {
        final List<String> ancestors = emptyList();
        List<AttributeWithoutOwner> attributes = list(
                mockBuildAttributeWithoutOwner(TestHelper_Variable.A_KNOWN_ATTR_NAME1),
                mockBuildAttributeWithoutOwner(TestHelper_Variable.A_KNOWN_ATTR_NAME2),
                mockBuildAttributeWithoutOwner(TestHelper_Variable.A_KNOWN_ATTR_NAME3)
        );
        ClasseImpl.ClasseBuilder classeBuilder = ClasseImpl.builder()
                .withId(Long.valueOf(UniqueTestIdUtils.tuid()))
                .withName(classeName)
                .withMetadata(mockBuildClassMetadata())
                .withAncestors(ancestors)
                .withAttributes(attributes);

        if (classSpeciality != null) {
            classeBuilder.withMetadata(new ClassMetadataImpl(map(CLASS_SPECIALITY, classSpeciality.toString())));
        }

        return classeBuilder.build();
    }

    public static Classe mockBuildClasseWithAttr(String classeName) {
        final List<String> ancestors = emptyList();
        List<AttributeWithoutOwner> attributes = list(
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME1),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME2),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME3)
        );
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    public static ClasseImpl mockBuildClasse(Long id, String classeName) {
        return ClasseImpl.builder()
                .withId(id)
                .withName(classeName)
                .withMetadata(mockBuildClassMetadata())
                .build();
    }

    public static Classe mockBuildClasse_Process(String classeName, List<AttributeWithoutOwner> processAttributes,
            String dmsCategoryType) {
        CmMapUtils.FluentMap<String, String> metadata = map(CLASS_SPECIALITY, serializeEnum(ClassMetadata.ClassSpeciality.CS_PROCESS));

        if (isNotBlank(dmsCategoryType)) {
            metadata.with(DMS_CATEGORY, dmsCategoryType);
        }

        return ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(processAttributes)
                .withMetadata(new ClassMetadataImpl(metadata)) // Without this, the ProcessImpl constructor will complain
                .build();
    }

    public static ClassMetadata mockBuildClassMetadata() {
        return ClassMetadataImpl.builder()
                .withClassType(ClassType.CT_SIMPLE)
                .build();
    }

    public static Classe mockBuildClasseWithGisPermissions(String classeName, List<String> gisAttributesNames) {
        return ClasseImpl.builder()
                .withId(Long.valueOf(UniqueTestIdUtils.tuid()))
                .withName(classeName)
                .withMetadata(mockBuildClassMetadata())
                .withPermissions(mockBuildGisClassPermissions(gisAttributesNames))
                .build();
    }

    //###########################################################################################
//###################################### - PROCESS - ########################################
//###########################################################################################
    public static org.cmdbuild.workflow.model.Process mockBuildProcess() {
        return ProcessImpl.builder()
                .withInner(mockBuildClasse_Process(TestHelper_Variable.A_KNOWN_CLASS_NAME1, emptyList(), "exampDmsCategoryType"))
                .withPlanId(TestHelper_Variable.A_KNOWN_PLAN_ID)
                .withEntryTasks(map("exampEntryTask", mockBuildTaskDefinition()))
                .withTasksById(map("examptasksById", mockBuildTaskDefinition()))
                .build();
    }

    public static org.cmdbuild.workflow.model.Process mockBuildProcess(Classe inner) {
        return ProcessImpl.builder()
                .withInner(inner)
                .withEntryTasks(emptyMap())
                .withTasksById(emptyMap())
                .withPlanId("planId")
                .build();
    }

    //###########################################################################################
//################################# - EXTENDEDCLASS - #######################################
//###########################################################################################
    public static ExtendedClass mockBuildExtendedClass(String name) {
        FormStructure formStructure = new FormStructureImpl("{ \"color\" : \"Black\", \"type\" : \"FIAT\" }");
        return ExtendedClassImpl.builder()
                .withClasse(mockBuildClasse(name))
                .withFormTriggers(list(mockBuildFormTrigger()))
                .withContextMenuItems(list(mockBuildContextMenuItem()))
                .withWidgets(list(mockBuildWidgetDbData()))
                .withFormStructure(formStructure)
                .withCalendarTriggers(list(mockBuildCalendarTriggerInfo()))
                .withLookupValuesByAttr(map())
                .build();
    }

    public static Classe toDeactivated(Classe classe) {
        return ClasseImpl.copyOf(classe).withMetadata(ClassMetadataImpl.copyOf(classe.getMetadata()).withActive(false).build()).build();
    }

    //###########################################################################################
//################################# - CLASSPERMISSION - #####################################
//###########################################################################################
    public static ClassPermissions mockBuildClassPermissions() {
        Set<ClassPermission> set = new HashSet<>();
        set.add(ClassPermission.CP_ALL);
        return ClassPermissionsImpl.builder()
                .withPermissions(map(PS_CORE, set))
                .build();
    }

    public static ClassPermissions mockBuildGisClassPermissions(List<String> gisAttributesNames) {
        Map<String, Set<GrantAttributePrivilege>> gisPermissions = gisAttributesNames.stream().collect(Collectors.toMap(k -> k, k -> Set.of(GAP_READ)));
        return ClassPermissionsImpl.builder()
                .withPermissions(map(PS_CORE, Set.of(ClassPermission.CP_ALL), PS_SERVICE, Set.of(ClassPermission.CP_ALL)))
                .withGisPermissions(gisPermissions)
                .build();
    }

//###########################################################################################
//######################################## - CARD - #########################################
//###########################################################################################

    public static Card mockBuildCard(Long cardId, Classe classe, Map<String, Object> attributes) {
        return CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttributes(attributes)
                .build();
    }

    public static CardFilterAsDefaultForClass mockBuildCardFilterAsDefaultForClass() {
        return new CardFilterAsDefaultForClassImpl(mockBuildActiveStoredFilter(TestHelper_Variable.A_KNOWN_FILTER_ID1), "Owner Name", 777L);
    }

    public static List<Card> mockBuildCardList() {
        Classe classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        return list(
                mockBuildCard(A_KNOWN_CARD_ID1, classe, map("attr1", 1, "attr2", 2)),
                mockBuildCard(A_KNOWN_CARD_ID2, classe, map("attr3", 3, "attr4", 4)),
                mockBuildCard(A_KNOWN_CARD_ID3, classe, map("attr5", 5, "attr6", 6))
        );
    }

    //###########################################################################################
//######################################## - DOMAIN - #######################################
//###########################################################################################
    public static Domain mockBuildDomainDirect(String domainName, Classe sourceClass, Classe targetClass) {
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withClass1(sourceClass)
                .withClass2(targetClass);
        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.MANY_TO_ONE));

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    public static Domain mockBuildDomainInverse(String domainName, Classe sourceClass, Classe targetClass) {
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withClass1(sourceClass)
                .withClass2(targetClass);

        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.ONE_TO_MANY));
        Domain domain = domainBuilder.build();

        return domain;
    }

    public static Domain mockBuildDomainWithAttributes(String domainName, String className1, String className2, String attrName) {
        Classe sourceClass = mockBuildClasseWithAttr(className1);
        Classe targetClass = mockBuildClasseWithAttr(className2);
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass) // Employee1
                .withAttribute(mockBuildAttributeWithoutOwner(attrName))
                .withAttribute(mockBuildAttribute_ForeignKeyAttributeType(domainName, targetClass));
        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.MANY_TO_ONE));

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    public static List<Domain> mockBuildDomainsList() {
        Classe sourceClass1 = mockBuildClasse(TestHelper_Variable.A_SOURCE_CLASS_NAME1);
        Classe targetClass1 = mockBuildClasse(TestHelper_Variable.A_TARGET_CLASS_NAME1);
        Domain domainA = mockBuildDomainDirect(TestHelper_Variable.A_SOURCE_CLASS_NAME1 + TestHelper_Variable.A_TARGET_CLASS_NAME1, sourceClass1, targetClass1);
        Classe sourceClass2 = mockBuildClasse(TestHelper_Variable.A_SOURCE_CLASS_NAME2);
        Classe targetClass2 = mockBuildClasse(TestHelper_Variable.A_TARGET_CLASS_NAME2);
        Domain domainB = mockBuildDomainDirect(TestHelper_Variable.A_SOURCE_CLASS_NAME2 + TestHelper_Variable.A_TARGET_CLASS_NAME2, sourceClass2, targetClass2);
        return list(domainA, domainB);
    }

    public static RelationImpl mockBuildCMRelation(Long id) {
        return RelationImpl.builder()
                .withId(id)
                .withType(mockBuildDomainWithAttributes(A_KNOWN_DOMAIN_NAME1, A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_ATTR_NAME1))
                .withSourceCard(CardIdAndClassNameImpl.card(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_ID1))
                .withTargetCard(CardIdAndClassNameImpl.card(A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_ID2))
                .withBeginDate(ZonedDateTime.now())
                .withStatus(Card.CardStatus.A)
                .build();
    }

    //###########################################################################################
//###################################### - WIDGET - #########################################
//###########################################################################################
    public static Widget mockBuildWidget() {
        Map<String, Object> data = map();
        data.put("key1", "value1");
        Map<String, Object> context = map();
        context.put("key1", "value1");
        return WidgetImpl.builder()
                .withType("exampleType")
                .withLabel("Example Widget")
                .withId("widget01")
                .withActive(true)
                .withData(data)
                .withContext(context)
                .build();
    }

    public static WidgetData mockBuildWidgetData() {
        return WidgetDataImpl.builder()
                .withType("exampleType")
                .withLabel("Example Widget")
                .withId("widget01")
                .withIsActive(true)
                .withData("key1", "value1")
                .withData("key2", 100)
                .build();
    }

    public static WidgetDbData mockBuildWidgetDbData() {
        Map<String, String> mapData = map();
        mapData.put("key1", "value1");
        mapData.put("key2", "value2");
        return WidgetDataFromDbImpl.builder()
                .withOwner("exampleOwner")
                .withLabel("Example Widget")
                .withType("exampleType")
                .withId("widget01")
                .withData(mapData)
                .withActive(true)
                .build();
    }

    //###########################################################################################
//################################## - FORMTRIGGER - ########################################
//###########################################################################################
    public static FormTrigger mockBuildFormTrigger() {
        return FormTriggerImpl.builder()
                .withJsScript("exampJsScript")
                .withActive(true)
                .withBindings(list(beforeInsert))
                .build();
    }

    //###########################################################################################
//############################## - CALENDARTRIGGER - ########################################
//###########################################################################################
    public static CalendarTriggerInfo mockBuildCalendarTriggerInfo() {
        return CalendarTriggerImpl.builder()
                .withCode("exampCode")
                .withOwnerClass("exampOwnerClass")
                .withOwnerClass("exampOwnerClass")
                .withOwnerAttr("exampOwnerAttr")
                .withActive(true)
                .withDescription("exampDescription")
                .withContent("exampContent")
                .withType(CT_DATE)
                .withCategory(mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1, TestHelper_Variable.A_KNOWN_LOOKUP_ID1))
                .withPriority(mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME2, TestHelper_Variable.A_KNOWN_LOOKUP_ID2))
                .withParticipants(list(ParticipantHeader.USER.name() + ".123", ParticipantHeader.GROUP.name() + ".456"))
                .withNotifications(list(mockBuildEmailTemplateInlineData()))
                .build();
    }

    public static CalendarTrigger mockBuildCalendarTrigger(Long id, String code) {
        org.cmdbuild.common.beans.LookupValue category = mockBuildLookupValueWithType(A_KNOWN_LONG_ID1, CALENDAR_CATEGORY_LOOKUP_TYPE);
        org.cmdbuild.common.beans.LookupValue priority = mockBuildLookupValueWithType(A_KNOWN_LONG_ID2, CALENDAR_PRIORITY_LOOKUP_TYPE);

        return CalendarTriggerImpl.builder()
                .withId(id)
                .withCode(code)
                .withDescription("Test Trigger")
                .withContent("Test Content")
                .withOwnerClass("TestClass")
                .withOwnerAttr("TestAttr")
                .withType(CalendarEventType.CT_INSTANT)
                .withTimeZone("UTC")
                .withCategory(category)
                .withPriority(priority)
                .withParticipants(emptyList())
                .withNotifications(emptyList())
                .withActive(true)
                .withEventTime(LocalTime.of(10, 0))
                .withDelay(Interval.ZERO)
                .withConfig(CalendarSequenceConfigImpl.builder().build())
                .withScope(CalendarTrigger.TriggerScope.TS_INTERACTIVE_ONLY)
                .build();
    }

    public static List<CalendarTrigger> mockBuildCalendarTriggerList() {
        return list(
                mockBuildCalendarTrigger(A_KNOWN_LONG_ID1, A_KNOWN_NAME1),
                mockBuildCalendarTrigger(A_KNOWN_LONG_ID1, A_KNOWN_NAME2),
                mockBuildCalendarTrigger(A_KNOWN_LONG_ID1, A_KNOWN_NAME3)
        );
    }

    //###########################################################################################
//####################################### - EMAIL - #########################################
//###########################################################################################
    public static EmailAccount mockBuildEmailAccount(String name) {
        return EmailAccountImpl.builder()
                .withName(name)
                .withSmtpSsl(true)
                .withSmtpStartTls(true)
                .withImapSsl(true)
                .withImapStartTls(true)
                .withConfig("config", "config")
                .withActive(true)
                .withPassword("myPassword")
                .build();
    }

    public static List<EmailAccount> mockBuildListEmailAccount() {
        return list(
                mockBuildEmailAccount(TestHelper_Variable.A_KNOWN_EMAIL_NAME1),
                mockBuildEmailAccount(TestHelper_Variable.A_KNOWN_EMAIL_NAME2),
                mockBuildEmailAccount(TestHelper_Variable.A_KNOWN_EMAIL_NAME3)
        );
    }

    public static EmailTemplateInlineData mockBuildEmailTemplateInlineData() {
        return EmailTemplateInlineDataImpl.builder()
                .withId("exampId")
                .withTemplate("exampTemplate")
                .withContent("exampContent")
                .build();
    }

    public static EmailSignature mockBuildEmailSignature(Long id) {
        return EmailSignatureImpl.builder()
                .withId(id)
                .withActive(true)
                .withCode("exampCode")
                .withDescription("exampDescription")
                .withContentHtml("someContent_html")
                .build();
    }

    public static EmailSignature mockBuildEmailSignatureNotActive(Long id) {
        return EmailSignatureImpl.builder()
                .withId(id)
                .withActive(false)
                .withCode("exampCode")
                .withDescription("exampDescription")
                .withContentHtml("someContent_html")
                .build();
    }

    public static EmailTemplate mockBuildEmailTemplate(String code) {
        return EmailTemplateImpl.builder()
                .withNotificationProvider("exampType")
                .withCode(code)
                .withActive(true)
                .withContentType("exampContentType")
                .withKeepSynchronization(true)
                .withPromptSynchronization(true)
                .withMeta(map("meta", "exampMeta"))
                .withReports(list(mockBuildReportConfig()))
                .build();
    }

    public static List<EmailTemplate> mockBuildListEmailTemplate() {
        return list(
                mockBuildEmailTemplate(TestHelper_Variable.A_EMAIL_TEMPLATE_NAME1),
                mockBuildEmailTemplate(TestHelper_Variable.A_EMAIL_TEMPLATE_NAME2),
                mockBuildEmailTemplate(TestHelper_Variable.A_EMAIL_TEMPLATE_NAME3)
        );
    }

    public static TemplateBindings mockBuildTemplateBindings() {
        Collection<String> clientBindings = list("clientBindings1", "clientBindings2");
        Collection<String> serverBindings = list("serverBindings1", "serverBindings2");
        return new TemplateBindingsImpl(clientBindings, serverBindings);
    }

    public static Email mockBuildEmail(Long id) {
        return EmailImpl.builder()
                .withFrom("me@example.com")
                .withTo("you@example.com")
                .withSubject("Subject")
                .withContent("Body")
                .withId(id)
                .withStatus(EmailStatus.ES_DRAFT)
                .build();
    }

    //###########################################################################################
//################################### - LOOKUPTYPE - ########################################
//###########################################################################################
    public static PagedElements<LookupType> mockBuildPagedElementsLookupType(LookupSpeciality lookupSpeciality) {
        List<LookupType> listLookUpType = mockBuildLookupTypeList(lookupSpeciality);
        return new PagedElements(listLookUpType, 1L, null);
    }

    public static LookupType mockBuildLookupType(String name) {
        return LookupTypeImpl.builder()
                .withId(0L)
                .withName(name)
                .withParent(1L)
                .withAccessType(LookupAccessType.LT_SYSTEM)
                .withSpeciality(LookupSpeciality.LS_DMSCATEGORY)
                .build();
    }

    public static LookupType mockBuildLookupType(String name, LookupSpeciality lookupSpeciality) {
        return LookupTypeImpl.builder()
                .withId(0L)
                .withName(name)
                .withParent(1L)
                .withAccessType(LookupAccessType.LT_DEFAULT)
                .withSpeciality(lookupSpeciality)
                .build();
    }

    public static List<LookupType> mockBuildLookupTypeList(LookupSpeciality lookupSpeciality) {
        List<LookupType> list = list(
                mockBuildLookupType(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1, lookupSpeciality),
                mockBuildLookupType(TestHelper_Variable.A_KNOWN_LOOKUP_NAME2, lookupSpeciality),
                mockBuildLookupType(TestHelper_Variable.A_KNOWN_LOOKUP_NAME3, lookupSpeciality));
        return list;
    }

    //###########################################################################################
//################################## - LOOKUPVALUE - ########################################
//###########################################################################################
    public static LookupValue mockBuildLookupValue(String name, Long id) {
        return LookupValueImpl.builder()
                .withId(id)
                .withCode(name)
                .withDescription("myDescription")
                .withNotes("someNotes")
                .withActive(true)
                .withTypeName("typeName")
                .withParentId(Long.MIN_VALUE)
                .withType(mockBuildLookupType(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1))
                .build();
    }

    public static org.cmdbuild.common.beans.LookupValue mockBuildLookupValueWithType(Long id, String lookupType) {
        return org.cmdbuild.dao.beans.LookupValueImpl.builder()
                .withLookupType(lookupType)
                .withId(id)
                .withCode("test_code")
                .withDescription("Test Lookup Value")
                .build();
    }

    public static List<LookupValue> mockBuildListLookupValue() {
        return list(
                mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1, TestHelper_Variable.A_KNOWN_LOOKUP_ID1),
                mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME2, TestHelper_Variable.A_KNOWN_LOOKUP_ID2),
                mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME3, TestHelper_Variable.A_KNOWN_LOOKUP_ID3));
    }

    public static PagedElements<LookupValue> mockBuildPagedElementsLookupValue() {
        List<LookupValue> listOfLookupValue = list(mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1, TestHelper_Variable.A_KNOWN_LOOKUP_ID1));
        PagedElements<LookupValue> elements = new PagedElements(listOfLookupValue, 1L, null);
        return elements;
    }

    public static PagedElements<LookupValue> mockBuildPagedElementsLookupValue2() {
        List<LookupValue> listLookUpValue = mockBuildListLookupValue();
        return new PagedElements(listLookUpValue, 1L, null);
    }

    public static PagedElements<LookupValue> mockBuild2PagedElementsLookupValue() {
        List<LookupValue> listOfLookupValue = list(mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME1, TestHelper_Variable.A_KNOWN_LOOKUP_ID1), mockBuildLookupValue(TestHelper_Variable.A_KNOWN_LOOKUP_NAME2, TestHelper_Variable.A_KNOWN_LOOKUP_ID2));
        PagedElements<LookupValue> elements = new PagedElements(listOfLookupValue, 1L, null);
        return elements;
    }

    //###########################################################################################
//################################## - CORECOMPONENT - ######################################
//###########################################################################################
    public static CoreComponent mockBuildCoreComponent(String code) {
        return CoreComponentImpl.builder()
                .withActive(true)
                .withCode(code)
                .withDescription("This is a description")
                .withData("Some data")
                .withType(CoreComponentType.CCT_SCRIPT)
                .build();
    }

    public static List<CoreComponent> mockBuildListCoreComponent() {
        return list(
                mockBuildCoreComponent(TestHelper_Variable.A_KNOWN_COMPONENT_CODE1),
                mockBuildCoreComponent(TestHelper_Variable.A_KNOWN_COMPONENT_CODE2),
                mockBuildCoreComponent(TestHelper_Variable.A_KNOWN_COMPONENT_CODE3)
        );
    }

    //###########################################################################################
//################################## - STOREDFILTER - #######################################
//###########################################################################################
    public static StoredFilter mockBuildActiveStoredFilter(Long id) {
        StoredFilter filter = StoredFilterImpl.builder()
                .withId(id)
                .withName("Active Filter " + id)
                .withDescription("Description for active filter " + id)
                .withOwnerName("Owner Name")
                .withOwnerType(StoredFilterOwnerType.SFO_CLASS)
                .withActive(true)
                .withConfiguration("{}")
                .withShared(true)
                .build();
        return filter;
    }

    public static StoredFilter mockBuildInactiveStoredFilter(Long id) {
        StoredFilter filter = StoredFilterImpl.builder()
                .withId(id)
                .withName("Inactive Filter " + id)
                .withDescription("Description for inactive filter " + id)
                .withOwnerName("exampleOwnerName")
                .withOwnerType(StoredFilterOwnerType.SFO_CLASS)
                .withActive(false)
                .withConfiguration("{}")
                .withShared(true)
                .build();
        return filter;
    }

    public static List<StoredFilter> mockBuildTwoStoredFilters(Long idFirstFilter, Long idSecondFilter) {
        return list(
                mockBuildActiveStoredFilter(idFirstFilter),
                mockBuildInactiveStoredFilter(idSecondFilter)
        );
    }

    //###########################################################################################
//########################################## - WATERWAY- ####################################
//###########################################################################################
    public static WaterwayDescriptorRecord mockBuildWaterwayDescriptorRecord(String code) {
        return WaterwayDescriptorRecordImpl.builder()
                .withId(A_KNOWN_WATERWAY_DESCRIPTOR_ID1)
                .withCode(code)
                .withData("data: waterwayData")
                .build();
    }

    public static List<WaterwayDescriptorRecord> mockBuildWaterwayDescriptorRecordList() {
        return list(
                mockBuildWaterwayDescriptorRecord(A_KNOWN_CODE1),
                mockBuildWaterwayDescriptorRecord(A_KNOWN_CODE2),
                mockBuildWaterwayDescriptorRecord(A_KNOWN_CODE3)
        );
    }

    public static WaterwayItem mockBuildWaterwayItem(String code, WaterwayItemType type) {
        return WaterwayItemImpl.builder()
                .withCode(code)
                .withDescriptorKey("testConfig_v0")
                .withType(type)
                .build();
    }

    public static List<WaterwayItem> mockBuildWaterWayItemList() {
        return list(
                mockBuildWaterwayItem(A_KNOWN_CODE1, WYCIT_GATE),
                mockBuildWaterwayItem(A_KNOWN_CODE2, WYCIT_GATE),
                mockBuildWaterwayItem(A_KNOWN_CODE3, WYCIT_GATE)
        );
    }

    public static WaterwayMessageAttachment mockBuildWaterwayMessageAttachment(String name) {
        return WaterwayMessageAttachmentImpl.builder()
                .withName(name)
                .withType(WaterwayMessageAttachmentType.WMAT_TEXT)
                .withObject("test content")
                .build();
    }

    public static WaterwayMessagesStats mockBuildWaterwayMessagesStats() {
        Map<WaterwayMessageStatus, Long> messagesStats = map(
                WMS_DRAFT, 10L,
                WMS_QUEUED, 15L,
                WMS_PROCESSING, 20L
        );
        return new WaterwayMessagesStatsImpl(messagesStats);
    }

    //###########################################################################################
//######################################### - VIEW - ########################################
//###########################################################################################
    public static View mockBuildView(String name) {
        return ViewImpl.builder()
                .withName(name)
                .withId(1L)
                .withType(ViewType.VT_SQL)
                .withSourceFunction("sourceFunction")
                .build();
    }

    public static View mockBuildViewCalendarType(String name) {
        return ViewImpl.builder()
                .withName(name)
                .withId(1L)
                .withType(ViewType.VT_CALENDAR)
                .withSourceFunction("sourceFunction")
                .withFilter(A_TEST_FILTER)
                .build();
    }

    public static List<View> mockBuildListView() {
        return list(
                mockBuildView(TestHelper_Variable.A_KNOWN_VIEW_NAME1),
                mockBuildView(TestHelper_Variable.A_KNOWN_VIEW_NAME2),
                mockBuildView(TestHelper_Variable.A_KNOWN_VIEW_NAME3)
        );
    }

    //###########################################################################################
//##################################### - USERDATA - ########################################
//###########################################################################################
    public static UserDataImpl mockBuildUserDataImpl(Long id) {
        return UserDataImpl.builder()
                .withId(id)
                .withUsername("testUsername")
                .build();
    }

    //###########################################################################################
//################################## - OPERATIONUSER - ######################################
//###########################################################################################
    public static OperationUser mockBuildOperationUser() {
        return OperationUserImpl.builder()
                .withAuthenticatedUser(mockBuildLoginUser())
                .build();
    }

    //###########################################################################################
//##################################### - LOGINUSER - #######################################
//###########################################################################################
    public static LoginUser mockBuildLoginUser() {
        return LoginUserImpl.builder()
                .withId(1L)
                .withUsername("username")
                .withActiveStatus(false)
                .withServiceStatus(false)
                .build();
    }

    //###########################################################################################
//##################################### - UICOMPONENT- ######################################
//###########################################################################################
    public static UiComponentData mockBuildUiComponent(String name, UiComponentType uiComponentType) {
        byte[] dataDefault = "dataDefault".getBytes();
        byte[] dataMobile = "dataMobile".getBytes();
        return UiComponentDataImpl.builder()
                .withName(name)
                .withId(1L)
                .withActive(true)
                .withType(uiComponentType)
                .withDataDefault(dataDefault)
                .withDataMobile(dataMobile)
                .withSynthesized(true)
                .build();
    }

    public static List<UiComponentData> mockBuildListUiComponent(UiComponentType uiComponentType) {
        UiComponentData uiComponentData1 = mockBuildUiComponent(TestHelper_Variable.A_KNOWN_UI_COMPONENT_DATA_NAME1, uiComponentType);
        UiComponentData uiComponentData2 = mockBuildUiComponent(TestHelper_Variable.A_KNOWN_UI_COMPONENT_DATA_NAME2, uiComponentType);
        UiComponentData uiComponentData3 = mockBuildUiComponent(TestHelper_Variable.A_KNOWN_UI_COMPONENT_DATA_NAME3, uiComponentType);
        return list(uiComponentData1, uiComponentData2, uiComponentData3);
    }

    public static UiComponentInfo mockBuildUiComponentInfo(String name) {
        UiComponentType type = UCT_WIDGET;
        TargetDevice targetDevice = TD_DEFAULT;
        UiComponentVersionInfo uiComponentVersionInfo = new UiComponentVersionInfoImpl(targetDevice);
        List<UiComponentVersionInfo> versions = list(uiComponentVersionInfo);
        return UiComponentInfoImpl.builder()
                .withName(name)
                .withActive(true)
                .withDescription("exampleDescription")
                .withExtjsComponentId("someExtjsComponentId")
                .withExtjsAlias("someExtjsAlias")
                .withType(type)
                .withVersions(versions)
                .build();
    }

    public static List<UiComponentInfo> mockBuildListUiComponentInfo() {
        return list(
                mockBuildUiComponentInfo(TestHelper_Variable.A_KNOWN_UI_COMPONENT_NAME1),
                mockBuildUiComponentInfo(TestHelper_Variable.A_KNOWN_UI_COMPONENT_NAME2),
                mockBuildUiComponentInfo(TestHelper_Variable.A_KNOWN_UI_COMPONENT_NAME3)
        );
    }

    //###########################################################################################
//################################## - DASHBOARDDATA - ######################################
//###########################################################################################
    public static DashboardData mockBuildDashboardData(String name) {
        return DashboardDataImpl.builder()
                .withCode(name)
                .withDescription("someDescription")
                .withConfig("{\"components\":[{\"_id\":\"1\",\"description\":\"desc\"}], \"parameters\":[{\"_id\":\"1\",\"description\":\"paramDesc\"}]}")
                .withId(5L)
                .withActive(true)
                .build();
    }

    public static List<DashboardData> mockBuildListDashBoardData() {
        DashboardData dashboardData1 = mockBuildDashboardData(TestHelper_Variable.A_KNOWN_DASHBOARD_NAME1);
        DashboardData dashboardData2 = mockBuildDashboardData(TestHelper_Variable.A_KNOWN_DASHBOARD_NAME2);
        DashboardData dashboardData3 = mockBuildDashboardData(TestHelper_Variable.A_KNOWN_DASHBOARD_NAME3);
        return list(dashboardData1, dashboardData2, dashboardData3);
    }

    //###########################################################################################
//################################## - EASYUPLOADITEM - #####################################
//###########################################################################################
    public static EasyuploadItem mockBuildEasyuploadItem() {
        return EasyuploadItemImpl.builder()
                .withPath("exampPath")
                .withFileName("exampFileName")
                .withHash("someHash")
                .withMimeType("exampMimeType")
                .withSize(10)
                .withContent("randomText".getBytes())
                .withDescription("exampDescription")
                .build();
    }

    //###########################################################################################
//######################################## - REPORT - #######################################
//###########################################################################################
    public static ReportConfig mockBuildReportConfig() {
        return ReportConfigImpl.builder()
                .withCode(TestHelper_Variable.A_KNOWN_REPORT_CONFIG_NAME)
                .withFormat(PDF)
                .withParams(map("params", "exampParams"))
                .build();
    }

    public static ReportInfo mockBuildReportInfo(String name) {
        return ReportInfoImpl.builder()
                .withCode(name)
                .withActive(true)
                .build();
    }

    public static List<ReportInfo> mockBuildReportInfoList() {
        ReportInfo reportInfo1 = mockBuildReportInfo(TestHelper_Variable.A_KNOWN_REPORT_INFO_NAME1);
        ReportInfo reportInfo2 = mockBuildReportInfo(TestHelper_Variable.A_KNOWN_REPORT_INFO_NAME2);
        ReportInfo reportInfo3 = mockBuildReportInfo(TestHelper_Variable.A_KNOWN_REPORT_INFO_NAME3);
        return list(reportInfo1, reportInfo2, reportInfo3);
    }

    public static ReportDataImpl mockBuildReportData(Long id) {
        return ReportDataImpl.builder()
                .withId(id)
                .withCode("sample code")
                .withSourceReports(list("rep1", "rep2", "rep3"))
                .build();
    }

    //###########################################################################################
//########################################## - ETL - ########################################
//###########################################################################################
    public static EtlGate mockBuildEtlGate(String code) {
        return EtlGateImpl.builder()
                .withCode(code)
                .withDescription("exampDescription")
                .withAllowPublicAccess(true)
                .withEnabled(true)
                .withProcessingMode(PM_REALTIME)
                .withConfig(
                        map(ETL_GATE_CONFIG_SHOW_ON_CLASSES, TestHelper_Variable.A_KNOWN_CLASS_ID,
                                ETL_HANDLER_CONFIG_TEMPLATES, TestHelper_Variable.A_KNOWN_ETL_TEMPLATE_NAME1)
                )
                .addHandler(mockBuildEtlGateHandler())
                .build();
    }

    public static List<EtlGate> mockBuildListEtlGate() {
        return list(
                mockBuildEtlGate(TestHelper_Variable.A_KNOWN_ETL_GATE_NAME1),
                mockBuildEtlGate(TestHelper_Variable.A_KNOWN_ETL_GATE_NAME2),
                mockBuildEtlGate(TestHelper_Variable.A_KNOWN_ETL_GATE_NAME3));
    }

    public static EtlTemplate mockBuildEtlTemplate(String code, Boolean active) {
        return EtlTemplateImpl.builder()
                .withCode(code)
                .withDescription("exampDescription")
                .withActive(active)
                .build();
    }

    public static List<EtlTemplate> mockBuildListEtlTemplate() {
        return list(
                mockBuildEtlTemplate(TestHelper_Variable.A_KNOWN_ETL_TEMPLATE_NAME1, TestHelper_Variable.ACTIVE),
                mockBuildEtlTemplate(TestHelper_Variable.A_KNOWN_ETL_TEMPLATE_NAME2, TestHelper_Variable.ACTIVE),
                mockBuildEtlTemplate(TestHelper_Variable.A_KNOWN_ETL_TEMPLATE_NAME3, TestHelper_Variable.NOT_ACTIVE)
        );
    }

    public static EtlGateHandler mockBuildEtlGateHandler() {
        return new EtlGateHandlerImpl(map(
                ETL_HANDLER_CONFIG_TYPE, ETLHT_IFC
        ));
    }

    public static EtlProcessingResultErrorImpl mockBuildEtlProcessingResultErrorImpl() {
        return new EtlProcessingResultErrorImpl(
                1L,
                1L,
                emptyMap(),
                "userErrorMessage",
                "techErrorMessage"
        );
    }

    public static EtlProcessingResultDetailsImpl mockBuildEtlProcessingResultDetailsImpl() {
        return new EtlProcessingResultDetailsImpl(emptyList(), emptyList(), emptyList());
    }

    public static EtlProcessingResultImpl mockBuildEtlProcessingResult() {
        DateTimeFormatter formatter
                = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return new EtlProcessingResultImpl(
                ZonedDateTime.parse("2018-12-16T20:28:33.213+05:30", formatter),
                ZonedDateTime.parse("2026-12-16T20:28:33.213+05:30", formatter),
                100L,
                15L,
                85L,
                5L,
                30L,
                list(mockBuildEtlProcessingResultErrorImpl()),
                mockBuildEtlProcessingResultDetailsImpl()
        );
    }

    public static EtlMessage mockBuildEtlMessage(String messageId, WaterwayMessageStatus status) {
        return EtlMessageImpl.builder()
                .withMessageId(messageId)
                .withNodeId("test-node")
                .withQueue("test-queue")
                .withStorage("test-storage")
                .withTransactionId(1)
                .withTimestamp(ZonedDateTime.now())
                .withStatus(status)
                .build();
    }

    public static PagedElements<WaterwayMessage> mockBuildEtlMessagePagedElements() {
        return new PagedElements<>(list(
                mockBuildEtlMessage(A_KNOWN_GENERIC_ID1, WMS_PROCESSING),
                mockBuildEtlMessage(A_KNOWN_GENERIC_ID2, WMS_PROCESSING),
                mockBuildEtlMessage(A_KNOWN_GENERIC_ID3, WMS_PROCESSING)
        ));
    }

    //###########################################################################################
//################################### - WEBHOOKCONFIG - #####################################
//###########################################################################################
    public static WebhookConfig mockBuildWebhookConfig(String code) {
        return WebhookConfigImpl.builder()
                .withCode(code)
                .withMethod(WebhookMethod.WHM_GET)
                .withUrl("exampUrl")
                .withEvents("exampEvents")
                .withActive(true)
                .withFilter("")
                .build();
    }

    //###########################################################################################
//################################### - GISATTRIBUTE - ######################################
//###########################################################################################
    public static GisAttribute mockBuildGisAttribute(String name, Boolean isPostgis, Boolean active, Boolean withIcon) {
        GisAttributeImpl.GisAttributeImplBuilder builder = GisAttributeImpl.builder()
                .withLayerName(name)
                .withDescription("exampDescription")
                .withActive(active)
                .withType(GisAttributeType.GAT_SHAPE)
                .withIndex(2)
                .withMinimumZoom(1)
                .withDefaultZoom(2)
                .withMaximumZoom(3)
                .withVisibility(map("visibility", true))
                .withOwnerClassName("exampOwnerClass")
                .withBeginDate(ZonedDateTime.now())
                .withConfig(mockBuildGisAttributeConfig());

        if (withIcon) {
            builder.withMapStyle(map("mapStyle", true, "externalGraphic", "exampIcon"));
        } else {
            builder.withMapStyle(map("mapStyle", true));
        }

        if (isPostgis) {
            builder.withType(GAT_POINT);
        }

        return builder.build();
    }

    public static GisAttribute mockBuildGisAttribute_RIP(String name, Boolean isPostgis, Boolean active) {
        GisAttributeImpl.GisAttributeImplBuilder builder = GisAttributeImpl.builder()
                .withLayerName(name)
                .withDescription("exampDescription")
                .withActive(active)
                .withMapStyle(map("mapStyle", true))
                .withType(GisAttributeType.GAT_SHAPE)
                .withIndex(2)
                .withMinimumZoom(1)
                .withDefaultZoom(2)
                .withMaximumZoom(3)
                .withVisibility(map("visibility", true))
                .withOwnerClassName("exampOwnerClass")
                .withBeginDate(ZonedDateTime.now())
                .withConfig(mockBuildGisAttributeConfig());

        if (isPostgis) {
            builder.withType(GAT_POINT);
        }

        return builder.build();
    }

    public static GisAttribute mockBuildInactiveGisAttribute(String name, Boolean isPostgis) {
        GisAttributeImpl.GisAttributeImplBuilder builder = GisAttributeImpl.builder()
                .withLayerName(name)
                .withDescription("exampDescription")
                .withActive(false)
                .withMapStyle(map("mapStyle", true))
                .withType(GisAttributeType.GAT_SHAPE)
                .withIndex(2)
                .withMinimumZoom(1)
                .withDefaultZoom(2)
                .withMaximumZoom(3)
                .withVisibility(map("visibility", true))
                .withOwnerClassName("exampOwnerClass")
                .withBeginDate(ZonedDateTime.now())
                .withConfig(mockBuildGisAttributeConfig());

        if (isPostgis) {
            builder.withType(GAT_POINT);
        }

        return builder.build();
    }

    public static GisAttributeConfig mockBuildGisAttributeConfig() {
        return GisAttributeConfigImpl.builder()
                .withInfoWindowEnabled(true)
                .build();
    }

    //###########################################################################################
//################################# - GEOSERVERLAYER - ######################################
//###########################################################################################
    public static GeoserverLayer mockBuildGeoserverLayer() {
        DateTimeFormatter formatter
                = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return GeoserverLayerImpl.builder()
                .withAttributeName("exampAttributeName")
                .withOwnerClass("exampOwnerClass")
                .withGeoserverStore("exampGeoserverStore")
                .withGeoserverLayer("exampGeoserverLayer")
                .withOwnerCard(3L)
                .withBeginDate(ZonedDateTime.parse("2018-12-16T20:28:33.213+05:30", formatter))
                .withActive(true)
                .withCenter(new CadPoint(2, 3))
                .build();
    }

    //###########################################################################################
//###################################### - NAVTREE - ########################################
//###########################################################################################
    public static NavTree mockBuildNavTree(String name) {
        return NavTreeImpl.builder()
                .withName(name)
                .withDescription("exampDescription")
                .withData(mockBuildNavTreeNode())
                .withActive(true)
                .withType(NT_MENU)
                .build();
    }

    public static List<NavTree> mockBuildListNavTree() {
        return list(
                mockBuildNavTree(TestHelper_Variable.A_KNOWN_NAVTREE_NAME1),
                mockBuildNavTree(TestHelper_Variable.A_KNOWN_NAVTREE_NAME2),
                mockBuildNavTree(TestHelper_Variable.A_KNOWN_NAVTREE_NAME3));
    }

    public static NavTreeNode mockBuildNavTreeNode() {
        return NavTreeNodeImpl.builder()
                .withTargetClassName("exampTargetClassName")
                .withTargetClassDescription("exampTargetClassDescription")
                .withId("exampId")
                .withShowOnlyOne(true)
                .withEnableRecursion(true)
                .withDirection(RD_DIRECT.name())
                .build();
    }

    //###########################################################################################
//######################################## - TASK - #########################################
//###########################################################################################
    public static TaskDefinition mockBuildTaskDefinition() {
        return TaskDefinitionImpl.builder()
                .withId("exampId")
                .withDescription("exampDescription")
                .withInstructions("exampInstructions")
                .withMetadata(mockBuildTaskMetadata())
                .build();
    }

    public static Iterable<TaskMetadata> mockBuildTaskMetadata() {
        TaskMetadata[] taskMetadataArr = new TaskMetadata[1];
        taskMetadataArr[0] = new TaskMetadata("exampName", "exampValue");
        return () -> Arrays.stream(taskMetadataArr).iterator();
    }

    //###########################################################################################
//################################## - CONTEXTMENU - ########################################
//###########################################################################################
    public static ContextMenuItem mockBuildContextMenuItem() {
        return ContextMenuItemImpl.builder()
                .withType(SEPARATOR)
                .withActive(true)
                .build();
    }

    //###########################################################################################
//######################################### - JOB - #########################################
//###########################################################################################
    public static JobData mockBuildJobData(Long id) {
        return JobDataImpl.builder()
                .withId(id)
                .withCode("sampleCode")
                .withType("test")
                .withConfig(map("test", "test"))
                .build();
    }

    public static List<JobData> mockBuildJobDataList() {
        return list(
                mockBuildJobData(TestHelper_Variable.A_KNOWN_JOBDATA_ID1),
                mockBuildJobData(TestHelper_Variable.A_KNOWN_JOBDATA_ID2),
                mockBuildJobData(TestHelper_Variable.A_KNOWN_JOBDATA_ID3)
        );
    }

    public static JobRun mockBuildCompletedJobRun(String jobCode) {
        return JobRunImpl.builder()
                .withJobCode(jobCode)
                .withTimestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .withCompleted(true)
                .withElapsedTime(100L)
                .withJobStatus(JRS_COMPLETED)
                .withErrorMessageData(new FaultEventsData(List.of()))
                .build();
    }

    public static PagedElements<JobRun> mockBuildJobRunPagedElements() {
        return new PagedElements<>(list(
                mockBuildCompletedJobRun(A_KNOWN_CODE1),
                mockBuildCompletedJobRun(A_KNOWN_CODE2),
                mockBuildCompletedJobRun(A_KNOWN_CODE3)
        ));
    }

    //###########################################################################################
//######################################## - ROLE - #########################################
//###########################################################################################
    public static Role mockBuildRole(Long id) {
        return RoleImpl.builder()
                .withId(id)
                .withName(TestHelper_Variable.A_KNOWN_ROLE_NAME1)
                .build();
    }

    public static Role mockBuildRole(Long id, String name) {
        return RoleImpl.builder()
                .withId(id)
                .withName(name)
                .build();
    }

    public static List<Role> mockBuildRoleList() {
        return list(
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID1, A_KNOWN_ROLE_NAME1),
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID2, A_KNOWN_ROLE_NAME2),
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID3, A_KNOWN_ROLE_NAME3));
    }

    public static List<Role> mockBuildListRoleWithDifferentNames() {
        return list(
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID1, TestHelper_Variable.A_KNOWN_ROLE_NAME1),
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID2, TestHelper_Variable.A_KNOWN_ROLE_NAME2),
                mockBuildRole(TestHelper_Variable.A_KNOWN_ROLE_ID3, TestHelper_Variable.A_KNOWN_ROLE_NAME3)
        );
    }

    //###########################################################################################
//################################### - GROUPCONFIG - #######################################
//###########################################################################################
    public static GroupConfig mockBuildGroupConfig() {
        return GroupConfigImpl.builder()
                .withProcessWidgetAlwaysEnabled(true)
                .build();
    }

    //###########################################################################################
//##################################### - XPDILNFO - ########################################
//###########################################################################################
    public static XpdlInfo mockBuildXpdlInfo() {
        DateTimeFormatter formatter
                = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return XpdlInfoImpl.builder()
                .withVersion("exampVersion")
                .withPlanId(TestHelper_Variable.A_KNOWN_XPDL_ID)
                .withProvider("exampProvider")
                .withLastUpdate(ZonedDateTime.parse("2018-12-16T20:28:33.213+05:30", formatter))
                .withDefault(true)
                .build();
    }

    //###########################################################################################
//##################################### - EMIAILTEMPLATE - ##################################
//###########################################################################################
    public static EmailTemplateSerializationHelper mockBuildEmailTemplateSerializationHelper(EmailTemplateService service) {
        ObjectTranslationService translationService = mock(ObjectTranslationService.class);
        Mockito.when(translationService.translateEmailTemplateDescription(Matchers.anyString(), Matchers.anyString())).thenReturn("examp_description_translation");
        return new EmailTemplateSerializationHelper(service, translationService);
    }

    //###########################################################################################
//####################################### - DATASOURCE - ####################################
//###########################################################################################

    public static TempDataSource mockBuildTempDataSource() {
        return (TempDataSource) TempDataSource.newInstance()
                .withName(TestHelper_Variable.A_KNOWN_DATASOURCE_NAME)
                .build();
    }

    //###########################################################################################
    //####################################### - FKDOMAIN - ######################################
    //###########################################################################################
    public static FkDomain mockBuildFkDomain() {
        return new FkDomainImpl(mockBuildAttribute_ForeignKeyAttributeType("exampAttributeName", mockBuildClasse("exampClasseName")));
    }

    //###########################################################################################
    //####################################### - GRANTDATA - #####################################
    //###########################################################################################
    public static GrantData mockBuildGrantData(Long grantId, Long roleId) {
        return GrantDataImpl.builder()
                .withId(grantId)
                .withRoleId(roleId)
                .withType(POT_CLASS)
                .withMode(GM_READ)
                .withClassName(TestHelper_Variable.A_KNOWN_CLASS_NAME1)
                .build();
    }

    public static List<GrantData> mockBuildGrantDataList() {
        return list(
                mockBuildGrantData(A_KNOWN_GRANTDATA_ID1, A_KNOWN_ROLE_ID1),
                mockBuildGrantData(A_KNOWN_GRANTDATA_ID2, A_KNOWN_ROLE_ID2),
                mockBuildGrantData(A_KNOWN_GRANTDATA_ID3, A_KNOWN_ROLE_ID3)
        );
    }

    public static DocumentInfoAndDetail mockBuildDocumentInfoAndDetail(String fileName) {
        return DocumentInfoAndDetailImpl.builder()
                .withAuthor("author")
                .withDescription("description")
                .withDocumentId("documentId")
                .withFileName(fileName)
                .withMimeType("mimeType")
                .withVersion("version")
                .withCategory("category")
                .withFileSize(100)
                .withHash("hash")
                .withCreated(ZonedDateTime.now())
                .withModified(ZonedDateTime.now())
                .hasContent(true)
                .build();
    }

    public static TempInfo mockBuildTempInfo(String fileName) {
        return TempInfoImpl.builder()
                .withContentType("contentType")
                .withFileName(fileName)
                .withSize(100L)
                .withTimeToLive(10000L)
                .build();
    }

    public static DocumentData mockBuildDocumentData(String fileName) {
        return DocumentDataImpl.builder()
                .withAuthor("author")
                .withCategory("category")
                .withFilename(fileName)
                .withMajorVersion(true)
                .withData(new byte[]{1, 2, 3, 4, 5})
                .build();
    }
//
//    public static Attachment mockBuildAttachment() {
//
//        DataHandler dh = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
//
//        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
//        headers.add("Content-Type", "text/plain");
//        headers.add("Content-Disposition", "form-data; name=\"file\"; filename=\"file.txt\"");
//
//        return new AttachmentImpl("email", dh, headers);
//    }
//
//    public static List<Attachment> mockBuildAttachmentList() {
//        return list(
//                mockBuildAttachment(),
//                mockBuildAttachment(),
//                mockBuildAttachment()
//        );
//    }

    //###########################################################################################
    //####################################### - BIMPROJECT - ####################################
    //###########################################################################################
    public static BimProject mockBuildBimProject(Long id, boolean isActive) {
        return BimProjectImpl.builder()
                .withId(id)
                .withProjectId("test-project-id")
                .withName("Test Project")
                .withDescription("Test Description")
                .withParentId(null)
                .withActive(isActive)
                .withImportMapping(null)
                .withLastCheckin(null)
                .withIfcFormat("IFC2x3")
                .withXktFile(null)
                .build();
    }

    public static BimProjectExt mockBuildBimProjectExt(Long id, String cardName, Long cardId, Boolean isActive) {
        return new BimProjectExtImpl(
                mockBuildBimProject(id, isActive),
                new CardIdAndClassNameImpl(cardName, cardId)
        );
    }

    public static List<BimProjectExt> mockBuildBimProjectExtList() {
        return list(
                mockBuildBimProjectExt(A_KNOWN_LONG_ID1, A_KNOWN_CLASS_NAME1, A_KNOWN_CARD_ID1, ACTIVE),
                mockBuildBimProjectExt(A_KNOWN_LONG_ID2, A_KNOWN_CLASS_NAME2, A_KNOWN_CARD_ID2, ACTIVE),
                mockBuildBimProjectExt(A_KNOWN_LONG_ID3, A_KNOWN_CLASS_NAME3, A_KNOWN_CARD_ID3, NOT_ACTIVE)
        );
    }

    public static BimObject mockBuildBimObject(Long id) {
        return BimObjectImpl.builder()
                .withId(id)
                .withOwnerClassId("TestClass")
                .withOwnerCardId(123L)
                .withProjectId("test-project-id")
                .withGlobalId("test-global-id")
                .build();
    }

    public static RequestInfo mockBuildRequestInfo(String requestId) {
        return RequestInfoImpl.builder()
                .withRequestId(requestId)
                .withActionId("test-action-id")
                .withPath("/test/path")
                .withMethod("GET")
                .withTimestamp(java.time.ZonedDateTime.now())
                .build();
    }

    public static List<RequestInfo> mockBuildRequestInfoList() {
        return list(
                mockBuildRequestInfo(A_KNOWN_GENERIC_ID1),
                mockBuildRequestInfo(A_KNOWN_GENERIC_ID2),
                mockBuildRequestInfo(A_KNOWN_GENERIC_ID3)
        );
    }

    public static RequestData mockBuildRequestData(String requestId) {
        return RequestDataImpl.builder()
                .withSessionId("test-session-id")
                .withRequestId(requestId)
                .withActionId("test-action-id")
                .withUser("test-user")
                .withPath("/test/path")
                .withMethod("GET")
                .withClient("test-client")
                .withUserAgent("test-agent")
                .withPayloadSize(0L)
                .withTimestamp(java.time.ZonedDateTime.now())
                .withStatusCode(200)
                .withElapsedTimeMillis(100)
                .withNodeId("test-node")
                .build();
    }

    public static DataHandler mockBuildDataHandler(String type) {
        DataSource ds = new ByteArrayDataSource(
                "test-content".getBytes(),
                type
        );
        return new DataHandler(ds);
    }

    public static List<DataHandler> mockBuildDataHandlerList() {
        return list(
                mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE),
                mockBuildDataHandler("text/html")
        );
    }

    public static Patch mockBuildPatch(String description) {
        return PatchImpl.builder()
                .withVersion("1.0.0-01_test")
                .withDescription(description)
                .withContent("-- Test SQL patch content\nSELECT 1;")
                .build();
    }

    public static List<Patch> mockBuildPatchList() {
        return list(
                mockBuildPatch(A_KNOWN_PATCH1),
                mockBuildPatch(A_KNOWN_PATCH2),
                mockBuildPatch(A_KNOWN_PATCH3)
        );
    }

    public static CalendarSequence mockBuildCalendarSequence(Long id) {
        org.cmdbuild.common.beans.LookupValue category = mockBuildLookupValueWithType(A_KNOWN_LONG_ID1, CALENDAR_CATEGORY_LOOKUP_TYPE);
        org.cmdbuild.common.beans.LookupValue priority = mockBuildLookupValueWithType(A_KNOWN_LONG_ID2, CALENDAR_PRIORITY_LOOKUP_TYPE);

        return CalendarSequenceImpl.builder()
                .withId(id)
                .withDescription("Test Sequence")
                .withContent("Test Content")
                .withOwner("testuser")
                .withType(CalendarEventType.CT_DATE)
                .withTimeZone("UTC")
                .withCategory(category)
                .withPriority(priority)
                .withParticipants(emptyList())
                .withNotifications(emptyList())
                .withFirstEvent(LocalDate.now())
                .withLastEvent(LocalDate.now().plusDays(7))
                .withConfig(CalendarSequenceConfigImpl.builder().build())
                .withEventTime(LocalTime.of(10, 0))
                .withSource(CalendarEventSource.CSO_SYSTEM)
                .withEvents(emptyList())
                .build();
    }

    public static List<CalendarSequence> mockBuildCalendarSequenceList() {
        return list(
                mockBuildCalendarSequence(A_KNOWN_LONG_ID1),
                mockBuildCalendarSequence(A_KNOWN_LONG_ID2),
                mockBuildCalendarSequence(A_KNOWN_LONG_ID3)
        );
    }

    public static CalendarEvent mockBuildCalendarEvent(Long id) {
        org.cmdbuild.common.beans.LookupValue category = mockBuildLookupValueWithType(A_KNOWN_LONG_ID1, CALENDAR_CATEGORY_LOOKUP_TYPE);
        org.cmdbuild.common.beans.LookupValue priority = mockBuildLookupValueWithType(A_KNOWN_LONG_ID2, CALENDAR_PRIORITY_LOOKUP_TYPE);

        return CalendarEventImpl.builder()
                .withId(id)
                .withDescription("Test Event")
                .withType(CalendarEventType.CT_INSTANT)
                .withBegin(ZonedDateTime.now())
                .withTimeZone("UTC")
                .withCategory(category)
                .withPriority(priority)
                .withSource(CalendarEventSource.CSO_SYSTEM)
                .withStatus(CalendarEventStatus.CST_ACTIVE)
                .build();
    }

    public static List<CalendarEvent> mockBuildCalendarEventList() {
        return list(
                mockBuildCalendarEvent(A_KNOWN_LONG_ID1),
                mockBuildCalendarEvent(A_KNOWN_LONG_ID2),
                mockBuildCalendarEvent(A_KNOWN_LONG_ID3)
        );
    }

    public static GisValue mockBuildGisValue(String layerName) {
        CmGeometry geometry = mock(CmGeometry.class);

        return GisValueImpl.builder()
                .withLayerName(layerName)
                .withOwnerClassId("ownerClassId")
                .withOwnerCardId(A_KNOWN_CARD_ID1)
                .withOwnerCardDescription("Test Card Description")
                .withGeometry(new PointImpl(1.0, 1.0))
                .build();
    }

    public static List<GisValue> mockBuildGisValueList() {
        return list(
                mockBuildGisValue(A_KNOWN_NAME1),
                mockBuildGisValue(A_KNOWN_NAME2),
                mockBuildGisValue(A_KNOWN_NAME3)
        );
    }

    public static ChatMessage mockBuildChatMessage(Long id) {
        return ChatMessageImpl.builder()
                .withId(id)
                .withTarget("user@example.com")
                .withSubject("Test subject")
                .withContent("Hello from test")
                .withThread(null)
                .withSourceName("test-source")
                .withMessageId("msg-1")
                .withTimestamp(java.time.ZonedDateTime.now())
                .withSourceType(CMST_SYSTEM)
                .withStatus(CMS_NEW)
                .withType(CMT_OUTGOING)
                .build();
    }

    public static List<ChatMessage> mockBuildChatMessageList() {
        return list(
                mockBuildChatMessage(A_KNOWN_LONG_ID1),
                mockBuildChatMessage(A_KNOWN_LONG_ID2),
                mockBuildChatMessage(A_KNOWN_LONG_ID3)
        );
    }

    public static ItemLock mockBuildItemLock(String itemId, String sessionId, LockScope scope) {
        ItemLockImpl.ItemLockImplBuilder b = ItemLockImpl.builder()
                .withItemId(itemId)
                .withSessionId(sessionId)
                .withScope(scope)
                .withTimeToLiveSeconds(60)
                .withBeginDate(ZonedDateTime.now())
                .withLastActiveDate(ZonedDateTime.now());

        if (scope == LockScope.LS_REQUEST) {
            b.withRequestId("req-" + UniqueTestIdUtils.tuid());
        } else {
            b.withRequestId("_any");
        }

        return b.build();
    }

    public static List<ItemLock> mockBuildItemLockList() {
        return list(
                mockBuildItemLock(A_KNOWN_GENERIC_ID1, A_KNOWN_CODE1, LS_SESSION),
                mockBuildItemLock(A_KNOWN_GENERIC_ID2, A_KNOWN_CODE2, LS_SESSION),
                mockBuildItemLock(A_KNOWN_GENERIC_ID3, A_KNOWN_CODE3, LS_SESSION)
        );
    }

    public static LockResponse mockBuildSuccessfulLockResponse(ItemLock lock) {
        AutoCloseableItemLock autoLock = new AutoCloseableItemLock() {
            @Override
            public void close() {
            }

            @Override
            public String getItemId() {
                return lock.getItemId();
            }

            @Override
            public String getSessionId() {
                return lock.getSessionId();
            }

            @Override
            public String getRequestId() {
                return lock.getRequestId();
            }

            @Override
            public ZonedDateTime getBeginDate() {
                return lock.getBeginDate();
            }

            @Override
            public ZonedDateTime getLastActiveDate() {
                return lock.getLastActiveDate();
            }

            @Override
            public int getTimeToLiveSeconds() {
                return lock.getTimeToLiveSeconds();
            }

            @Override
            public LockScope getScope() {
                return lock.getScope();
            }
        };

        return new LockResponse() {
            @Override
            public boolean isAquired() {
                return true;
            }

            @Override
            public AutoCloseableItemLock getLock() {
                return autoLock;
            }
        };
    }

    public static LockResponse mockBuildSuccessfulLockResponse() {
        ItemLock defaultLock = mockBuildItemLock(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, LS_SESSION);
        return mockBuildSuccessfulLockResponse(defaultLock);
    }

//##########################################################################################
//################################## - HOMEPAGEDATA - ######################################
//##########################################################################################
    public static HomepageData mockBuildHomepageData(String name) {
        return HomepageDataImpl.builder()
                .withCode(name)
                .withDescription("someDescription")
                .withConfig("{\"components\":[{\"_id\":\"1\",\"description\":\"desc\"}]}")
                .withId(5L)
                .withActive(true)
                .build();
    }

    public static List<HomepageData> mockBuildListHomepageData() {
        HomepageData homepageData1 = mockBuildHomepageData(TestHelper_Variable.A_KNOWN_HOMEPAGE_NAME1);
        HomepageData homepageData2 = mockBuildHomepageData(TestHelper_Variable.A_KNOWN_HOMEPAGE_NAME2);
        HomepageData homepageData3 = mockBuildHomepageData(TestHelper_Variable.A_KNOWN_HOMEPAGE_NAME3);
        return list(homepageData1, homepageData2, homepageData3);
    }


        public static LockResponse mockBuildUnsuccessfulLockResponse(String itemId) {
            final String id = itemId == null ? "item-unknown" : itemId;
            return new LockResponse() {
                @Override
                public boolean isAquired() {
                    return false;
                }
                @Override
                public AutoCloseableItemLock getLock() {
                    throw new IllegalStateException(String.format("unable to acquire lock for item id =< %s >", id));
                }
        };
    }

    public static LockResponse mockBuildUnsuccessfulLockResponse() {
        return mockBuildUnsuccessfulLockResponse("item-1");
    }

    public static OperationUser mockBuildOperationUser(String username) {
        return OperationUserImpl.builder()
                .withAuthenticatedUser(LoginUserImpl.builder()
                        .withUsername(username)
                        .build())
                .build();
    }

    public static Session mockBuildSession(String sessionId) {
        return SessionImpl.builder()
                .withSessionId(sessionId)
                .withOperationUser(mockBuildOperationUser())
                .withBeginDate(java.time.ZonedDateTime.now())
                .withLastActiveDate(java.time.ZonedDateTime.now())
                .withExpirationStrategy(SessionExpirationStrategy.ES_DEFAULT)
                .build();
    }

    public static AggregateQuery mockBuildAggregateQuery(final String attribute) {
        return new AggregateQuery() {
            @Override
            public String getAttribute() {
                return attribute;
            }

            @Override
            public AggregateOperation getOperation() {
                return AggregateOperation.SUM; // default, change if needed
            }

            @Override
            public String toString() {
                return "AggregateQuery(attribute=" + attribute + ", op=" + getOperation() + ")";
            }
        };
    }

    public static <T> AggregateResult<T> mockBuildAggregateResult(final AggregateQuery query, final T result) {
        final AggregateQuery q = query == null ? mockBuildAggregateQuery(A_KNOWN_ATTR_NAME1) : query;
        return new AggregateResult<T>() {
            @Override
            public T getResult() {
                return result;
            }

            @Override
            public AggregateQuery getQuery() {
                return q;
            }

            @Override
            public String toString() {
                return "AggregateResult(query=" + q + ", result=" + result + ")";
            }
        };
    }

    public static StatsQueryResponseImpl mockBuildStatsQueryResponse() {
        return StatsQueryResponseImpl.builder()
                .withAggregateResults(list(mockBuildAggregateResult(null, "result")))
                .build();
    }

    public static UserDomainService.CardDomainRelationStats mockBuildCardDomainRelationStats(
            final long relationCount,
            final RelationDirection direction,
            final Domain domain) {
        return new UserDomainService.CardDomainRelationStats() {
            @Override
            public long getRelationCount() {
                return relationCount;
            }

            @Override
            public RelationDirection getDirection() {
                return direction;
            }

            @Override
            public Domain getDomain() {
                return domain;
            }
        };
    }

    public static ChatPeer mockBuildChatPeer(String username) {
        return new ChatPeer() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getDescription() {
                return "test-user";
            }

            @Override
            public boolean hasMessages() {
                return false;
            }

            @Override
            public long getNewMessageCount() {
                return 0L;
            }

            @Override
            public java.time.ZonedDateTime getLastMessageTimestamp() {
                return null;
            }

            @Override
            public String toString() {
                return "ChatPeer{user=test-user}";
            }
        };
    }

    public static List<ChatPeer> mockBuildChatPeerList() {
        return list(
                mockBuildChatPeer(A_KNOWN_NAME1),
                mockBuildChatPeer(A_KNOWN_NAME2),
                mockBuildChatPeer(A_KNOWN_NAME3)
        );
    }

    public static StoredFunction mockBuildStoredFunction(String name) {
        FunctionMetadata metadata = new FunctionMetadataImpl(emptyMap());
        return StoredFunctionImpl.builder()
                .withName(name)
                .withId(1L)
                .withReturnSet(false)
                .withInputParameter(A_KNOWN_NAME1, new BooleanAttributeType())
                .withOutputParameter(A_KNOWN_NAME2, new BooleanAttributeType(), new AttributeMetadataImpl(emptyMap()))
                .withMetadata(metadata)
                .build();
    }

    public static PagedElements<StoredFunction> mockBuildStoredFunctionPagedElements() {
        return new PagedElements<>(list(
                mockBuildStoredFunction(A_KNOWN_NAME1),
                mockBuildStoredFunction(A_KNOWN_NAME2),
                mockBuildStoredFunction(A_KNOWN_NAME3)
        ));
    }

    public static ResultRow mockBuildResultRow(Map<String, Object> data) {
        return new AbstractResultRow() {
            @Override
            public <T> T toModel(Class<T> type) {
                return null;
            }

            @Override
            public Map<String, Object> asMap() {
                return data;
            }

            @Override
            public <T> T toModel() {
                return null;
            }

            @Override
            public Card toCard() {
                return null;
            }

            @Override
            public CMRelation toRelation() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static List<ResultRow> mockBuildResultRowList() {
        return list(
                mockBuildResultRow(map("resultRow1", 1)),
                mockBuildResultRow(map("resultRow2", 2)),
                mockBuildResultRow(map("resultRow3", 3))
        );
    }

    public static PreparedQuery mockBuildPreparedQuery(List<ResultRow> rows) {
        return new PreparedQuery() {
            @Override
            public List<ResultRow> run() {
                return rows;
            }
        };
    }

    public static GisStyleRuleset mockBuildGisStyleRuleset(String code) {
        return GisStyleRulesetImpl.builder()
                .withId(A_KNOWN_LONG_ID1)
                .withCode(code)
                .withGisAttribute(mockBuildGisAttribute(A_KNOWN_NAME1, true, ACTIVE, true))
                .withRules(List.of())
                .build();
    }

    public static List<GisStyleRuleset> mockBuildGisStyleRulesetList() {
        return list(
                mockBuildGisStyleRuleset(A_KNOWN_CODE1),
                mockBuildGisStyleRuleset(A_KNOWN_CODE2),
                mockBuildGisStyleRuleset(A_KNOWN_CODE3)
        );
    }

    public static GisNavTreeNode mockBuildGisNavTreeNode(String navTreeNodeId) {
        return GisNavTreeNodeImpl.builder()
                .withClassId(A_KNOWN_CLASS_ID)
                .withCardId(A_KNOWN_CARD_ID1)
                .withNavTreeNodeId(navTreeNodeId)
                .build();
    }

    public static List<GisNavTreeNode> mockBuildGisNavTreeNodeList() {
        return list(
                mockBuildGisNavTreeNode(A_KNOWN_GENERIC_ID1),
                mockBuildGisNavTreeNode(A_KNOWN_GENERIC_ID2),
                mockBuildGisNavTreeNode(A_KNOWN_GENERIC_ID3)

        );
    }

    public static Minion mockBuildMinion(GlobalConfigService globalConfigService) {

        class MinionImpl implements Minion {

            private final MinionHandler handler;

            public MinionImpl(MinionComponent component) {
                this.handler = checkNotNull(component.getMinionHandler());
            }

            @Override
            public String toString() {
                return "MinionImpl{" + "name=" + getName() + '}';
            }

            @Override
            public String getName() {
                return handler.getName();
            }

            @Override
            public String getDescription() {
                return handler.getDescription();
            }

            @Override
            public String getConfigEnabler() {
                return handler.getConfigEnabler();
            }

            @Override
            public MinionStatus getStatus() {
                return handler.getStatus();
            }

            @Override
            public void startService() {
                checkArgument(canStart(), "cannot start this service");
                globalConfigService.putString(getConfigEnabler(), "true");
            }

            @Override
            public void stopService() {
                checkArgument(canStop(), "cannot stop this service");
                globalConfigService.putString(getConfigEnabler(), "false");
            }

            @Override
            public boolean isEnabled() {
                return handler.isEnabled();
            }

            @Override
            public boolean canStart() {
                return StringUtils.isNotBlank(getConfigEnabler()) ? !isEnabled() : false;
            }

            @Override
            public boolean canStop() {
                return StringUtils.isNotBlank(getConfigEnabler()) ? isEnabled() : false;
            }

            @Override
            public boolean isHidden() {
                return handler.isHidden();
            }

        }
        MinionHandler minionHandler = mockBuildMinionHandler(A_KNOWN_NAME1);
        MinionComponent minionComponent = mock(MinionComponent.class);
        when(minionComponent.getMinionHandler()).thenReturn(minionHandler);
        return new MinionImpl(minionComponent);
    }

    public static List<Minion> mockBuildMinionList(GlobalConfigService globalConfigService) {
        return list(mockBuildMinion(globalConfigService));
    }

    public static MinionHandler mockBuildMinionHandler(String name) {
        return MinionHandlerImpl.builder()
                .withName(name)
                .withDescription(name + " description")
                .withConfigEnabler(null)
                .withEnabledChecker(true)
                .withHidden(false)
                .withRestartable(true)
                .withOrder(0)
                .withStatus(MRS_NOTRUNNING)
                .build();
    }

    public static MobileAppMessage mockBuildMobileAppMessage(String target, String sourceName) {
        return MobileAppMessageImpl.builder()
                .withTarget(target)
                .withSubject("Test Subject")
                .withContent("Test Content")
                .withSourceName(sourceName)
                .withSourceDescription("Test Description")
                .withMessageId("test-message-" + randomId())
                .withTimestamp(now())
                .withSourceType(MobileAppMessageSourceType.MAMST_SYSTEM)
                .withStatus(MobileAppMessageStatus.MAMS_NEW)
                .build();
    }

    public static PagedElements<MobileAppMessage> mockBuildMobileAppMessageList() {
        return new PagedElements<>(list(
                mockBuildMobileAppMessage("target1", A_KNOWN_NAME1),
                mockBuildMobileAppMessage("target2", A_KNOWN_NAME2),
                mockBuildMobileAppMessage("target3", A_KNOWN_NAME3)
        ));
    }

}