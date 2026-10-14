Ext.define('CMDBuildUI.model.administration.Configuration', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [
        {
            name: 'org.cmdbuild.activitylog.entries',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'org.cmdbuild.audit.enabled',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.audit.exclude',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'org.cmdbuild.audit.include',
            type: 'string',
            defaultValue: '^/services/'
        },
        {
            name: 'org.cmdbuild.audit.includePayload',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.audit.includeResponse',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.audit.maxPayloadSize',
            type: 'string',
            defaultValue: '100000'
        },
        {
            name: 'org.cmdbuild.audit.maxRecordAgeToKeepSeconds',
            type: 'string',
            defaultValue: '-1'
        },
        {
            name: 'org.cmdbuild.audit.maxRecordsToKeep',
            type: 'string',
            defaultValue: '100000'
        },
        {
            name: 'org.cmdbuild.auth.cas.login.page',
            type: 'string',
            defaultValue: '/login'
        },
        {
            name: 'org.cmdbuild.auth.cas.server.url',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.cas.service.param',
            type: 'string',
            defaultValue: 'service'
        },
        {
            name: 'org.cmdbuild.auth.cas.ticket.param',
            type: 'string',
            defaultValue: 'ticket'
        },
        {
            name: 'org.cmdbuild.auth.case.insensitive',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.auth.force.ws.password.digest',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.auth.force.ws.password.digest',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.auth.header.attribute.name',
            type: 'string',
            defaultValue: 'username'
        },
        {
            name: 'org.cmdbuild.auth.ldap.basedn',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.bind.attribute',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.search.auth.method',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.search.auth.password',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.search.auth.principal',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.search.filter',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.server.address',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.auth.ldap.server.port',
            type: 'string',
            defaultValue: '389'
        },
        {
            name: 'org.cmdbuild.auth.ldap.use.ssl',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.auth.methods',
            type: 'string',
            defaultValue: 'DBAuthenticator,file' // TODO: add in edit in line "String comma separated whitout space"
        },
        {
            name: 'org.cmdbuild.bim.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.bim.password',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.bim.url',
            type: 'string',
            defaultValue: 'http://localhost:8080/bimserver' //TODO: check correct address
        },
        {
            name: 'org.cmdbuild.bim.username',
            type: 'string',
            defaultValue: 'admin@bimserver.com'
        },
        {
            name: 'org.cmdbuild.cluster.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.cmdbf.mdrid',
            type: 'string',
            defaultValue: 'http://www.cmdbuild.org'
        },
        {
            name: 'org.cmdbuild.cmdbf.reconciliationrules',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'org.cmdbuild.cmdbf.schemalocation',
            type: 'string',
            defaultValue: 'http://localhost:8080/cmdbuild' //TODO: check default value
        },
        {
            // TODO: is needed??
            name: 'org.cmdbuild.core.card_tab_position',
            type: 'string',
            defaultValue: 'bottom'
        },
        {
            name: 'org.cmdbuild.core.demomode',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.core.enabled_languages',
            type: 'string',
            defaultValue: '' // TODO: add in edit in line "String comma separated whitout space"
        },
        {
            name: 'org.cmdbuild.core.grid_card_ratio',
            type: 'string',
            defaultValue: '50'
        },
        {
            name: 'org.cmdbuild.core.import_csv.one_by_one',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.core.instance_name',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.core.language',
            type: 'string',
            defaultValue: 'en'
        },
        {
            name: 'org.cmdbuild.core.languageprompt',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.core.lockcardenabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.core.lockcardtimeout',
            type: 'string',
            defaultValue: '300'
        },
        {
            name: 'org.cmdbuild.core.lockcarduservisible',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.core.login_languages',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.core.logout.redirect',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.core.popuppercentageheight',
            type: 'string',
            defaultValue: '80'
        },
        {
            name: 'org.cmdbuild.core.popuppercentagewidth',
            type: 'string',
            defaultValue: '80'
        },
        {
            name: 'org.cmdbuild.core.referencecombolimit',
            type: 'string',
            defaultValue: '500'
        },
        {
            name: 'org.cmdbuild.core.relationlimit',
            type: 'string',
            defaultValue: '20'
        },
        {
            name: 'org.cmdbuild.core.rowlimit',
            type: 'string',
            defaultValue: '20'
        },
        {
            name: 'org.cmdbuild.core.session.activeSessionPeriodForStatistics',
            type: 'string',
            defaultValue: '1200'
        },
        {
            name: 'org.cmdbuild.core.session.timeout',
            type: 'string',
            defaultValue: '3600'
        },
        {
            name: 'org.cmdbuild.core.startingclass',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.custompage.js.compression.enabled',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.dms.alfresco.custom.model.filename',
            type: 'string',
            defaultValue: 'cmdbuildCustomModel.xml'
        },
        {
            name: 'org.cmdbuild.dms.alfresco.custom.prefix',
            type: 'string',
            defaultValue: 'cmdbuild'
        },
        {
            name: 'org.cmdbuild.dms.alfresco.custom.uri',
            type: 'string',
            defaultValue: 'org.cmdbuild.dms.alfresco'
        },
        {
            name: 'org.cmdbuild.dms.category.lookup',
            type: 'string',
            defaultValue: 'AlfrescoCategory'
        },
        {
            name: 'org.cmdbuild.dms.credential.password',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.dms.credential.user',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.dms.delay',
            type: 'string',
            defaultValue: '1000'
        },
        {
            name: 'org.cmdbuild.dms.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.dms.fileserver.port',
            type: 'string',
            defaultValue: '1121'
        },
        {
            name: 'org.cmdbuild.dms.fileserver.url',
            type: 'string',
            defaultValue: 'localhost'
        },
        {
            name: 'org.cmdbuild.dms.metadata.autocompletion.filename',
            type: 'string',
            defaultValue: 'metadataAutocompletion.xml'
        },
        {
            name: 'org.cmdbuild.dms.repository.app',
            type: 'string',
            defaultValue: 'cm:cmdbuild'
        },
        {
            name: 'org.cmdbuild.dms.repository.fspath',
            type: 'string',
            defaultValue: '/Alfresco/UserHomes/cmdbuild'
        },
        {
            name: 'org.cmdbuild.dms.repository.wspath',
            type: 'string',
            defaultValue: '/app:company_home/app:user_...' // TODO: check default value
        },
        {
            name: 'org.cmdbuild.dms.server.url',
            type: 'string',
            defaultValue: 'http: //localhost:10080/alfr...' // TODO: check default value
        },
        {
            name: 'org.cmdbuild.dms.service.cmis.model',
            type: 'string',
            defaultValue: 'alfresco'
        },
        {
            name: 'org.cmdbuild.dms.service.cmis.password',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.dms.service.cmis.path',
            type: 'string',
            defaultValue: '/UserHomes/cmdbuild'
        },
        {
            name: 'org.cmdbuild.dms.service.cmis.url',
            type: 'string',
            defaultValue: 'http://localhost:10080/alfr...' // TODO: check default value
        },
        {
            name: 'org.cmdbuild.dms.service.cmis.user',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.dms.service.type',
            type: 'string',
            defaultValue: 'alfresco'
        },
        {
            name: 'org.cmdbuild.email.queue.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.email.queue.time',
            type: 'string',
            defaultValue: '0'
        },
        {
            name: 'org.cmdbuild.gis.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.gis.geoserver',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.gis.geoserver.admin.password',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.gis.geoserver.admin.user',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.gis.geoserver.url',
            type: 'string',
            defaultValue: 'http://localhost:8080/geose...'
        },
        {
            name: 'org.cmdbuild.gis.geoserver.workspace',
            type: 'string',
            defaultValue: 'cmdbuild'
        },
        {
            name: 'org.cmdbuild.gis.dwg2dxf.url',
            type: 'string',
            defaultValue: 'http://localhost:8080/dwg2dxf/api/v1'
        },
        {
            name: 'org.cmdbuild.gis.dwg2dxf.username',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.gis.dwg2dxf.password',
            type: 'string',
            defaultValue: 'password'
        },
        {
            name: 'org.cmdbuild.bim.ifc2xkt.url',
            type: 'string',
            defaultValue: 'http://localhost:8080/ifc2xkt/api/v1'
        },
        {
            name: 'org.cmdbuild.bim.ifc2xkt.username',
            type: 'string',
            defaultValue: 'admin'
        },
        {
            name: 'org.cmdbuild.bim.ifc2xkt.password',
            type: 'string',
            defaultValue: 'password'
        },
        {
            name: 'org.cmdbuild.graph.baseLevel',
            type: 'string',
            defaultValue: '1'
        },
        {
            name: 'org.cmdbuild.graph.clusteringThreshold',
            type: 'string',
            defaultValue: '100'
        },
        {
            name: 'org.cmdbuild.graph.displayLabel',
            type: 'string',
            defaultValue: 'none'
        },
        {
            name: 'org.cmdbuild.graph.edgeColor',
            type: 'string',
            defaultValue: '#3D85C6'
        },
        {
            name: 'org.cmdbuild.graph.enableEdgeTooltip',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.graph.enableNodeTooltip',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.graph.enabled',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.graph.spriteDimension',
            type: 'string',
            defaultValue: '20'
        },
        {
            name: 'org.cmdbuild.graph.stepRadius',
            type: 'string',
            defaultValue: '60'
        },
        {
            name: 'org.cmdbuild.graph.viewPointDistance',
            type: 'string',
            defaultValue: '50'
        },
        {
            name: 'org.cmdbuild.graph.viewPointHeight',
            type: 'string',
            defaultValue: '50'
        },
        {
            name: 'org.cmdbuild.multitenant.dbFunction',
            type: 'string',
            defaultValue: 'cm_get_tenants_for_user'
        },
        {
            name: 'org.cmdbuild.multitenant.mode',
            type: 'string',
            defaultValue: 'DISABLED'
        },
        {
            name: 'org.cmdbuild.multitenant.tenantClass',
            type: 'string',
            defaultValue: 'Tenant'
        },
        {
            name: 'org.cmdbuild.multitenant.tenantDomain',
            type: 'string',
            defaultValue: 'value' // TODO: default to "User" + tenant_class_name
        },
        {
            name: 'org.cmdbuild.notification.email.dms.account',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.notification.email.dms.destination',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.notification.email.dms.silence',
            type: 'string',
            defaultValue: '0'
        },
        {
            name: 'org.cmdbuild.notification.email.dms.template',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'org.cmdbuild.notification.enable',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.password.validation.differentFromPrevious',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.password.validation.differentFromPreviousCount',
            type: 'string',
            defaultValue: '3'
        },
        {
            name: 'org.cmdbuild.password.validation.differentFromUsername',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.password.validation.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.password.forewarningDays',
            type: 'string',
            defaultValue: '7'
        },
        {
            name: 'org.cmdbuild.password.maxAgeDays',
            type: 'string',
            defaultValue: '365'
        },
        {
            name: 'org.cmdbuild.password.validation.minLength',
            type: 'string',
            defaultValue: '6'
        },
        {
            name: 'org.cmdbuild.password.validation.requireDigit',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.password.validation.requireLowercase',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.password.validation.requireUppercase',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.password.change.enabled',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.session.userMapping.mode',
            type: 'string',
            defaultValue: 'DISABLED'
        },
        {
            name: 'org.cmdbuild.session.userMapping.class',
            type: 'string'
        },
        {
            name: 'org.cmdbuild.session.userMapping.attribute',
            type: 'string'
        },
        {
            name: 'org.cmdbuild.session.userMapping.userProperty',
            type: 'string'
        },
        {
            name: 'org.cmdbuild.sql.ddl_log.enabled',
            type: 'string',
            defaultValue: 'true'
        },
        {
            name: 'org.cmdbuild.sql.ddl_log.include',
            type: 'string',
            defaultValue: 'cm_delete_attribute|_cm3_at...' // TODO: check default value
        },
        {
            name: 'org.cmdbuild.sql.log.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.sql.log.exclude',
            type: 'string',
            defaultValue: '(SET SESSION|RESET) cmdbuil...' // TODO: check default value
        },
        {
            name: 'org.cmdbuild.sql.log.trackTimes',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.ui.uiServiceBaseUrl',
            type: 'string',
            defaultValue: 'auto'
        },
        {
            name: 'org.cmdbuild.workflow.disableSynchronizationOfMissingVariables',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.workflow.enableAddAttachmentOnClosedActivities',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.workflow.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.workflow.taskParamValidation.enabled',
            type: 'string',
            defaultValue: 'false'
        },
        {
            name: 'org.cmdbuild.workflow.user',
            type: 'string',
            defaultValue: 'admin'
        }
    ],

    proxy: {
        type: 'baseproxy',
        url: '/system/config/',
        extraParams: {
            detailed: true
        }
    }
});
