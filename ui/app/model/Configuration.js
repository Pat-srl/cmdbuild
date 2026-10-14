(function () {
    var statics = {
        ai: {
            chatavatar: 'cm_system_ai_chat_avatar',
            chatavatarmode: 'cm_system_ai_chat_avatar_mode',
            chatname: 'cm_system_ai_chat_name',
            enabled: 'cm_system_ai_enabled'
        },
        bim: {
            enabled: 'cm_system_bim_enabled',
            password: 'bimPassword',
            user: 'bimEmail',
            viewer: 'cm_system_bim_viewer'
        },
        chat: {
            enabled: 'cm_system_chat_enabled'
        },
        common: {
            defaultlanguage: 'cm_system_language_default',
            instancename: 'cm_system_instance_name',
            companylogo: 'cm_system_company_logo',
            uselanguageprompt: 'cm_system_use_language_prompt',
            keepfilteronupdatedcard: 'cm_system_keep_filter_on_updated_card',
            version: 'cm_system_version',
            fullversion: 'cm_system_version_full',
            ajaxtimeout: 'cm_system_timeout',
            redirectonlogout: 'cm_system_logout_redirect',
            keepalive: 'cm_system_keepalive_enabled'
        },
        cardlock: {
            enabled: 'cm_system_cardlock_enabled',
            showuser: 'cm_system_cardlock_showuser'
        },
        dms: {
            enabled: 'cm_system_dms_enabled',
            category: 'cm_system_dms_category',
            descriptionmode: 'cm_system_dms_description_mode',
            filterextensions: 'cm_system_dms_fileExtensionCheckEnabled',
            allowedextensions: 'cm_system_dms_allowedFileExtensions',
            maxfilesize: 'cm_system_dms_maxFileSize',
            previewLimit: 'cm_system_ui_dms_maxpreviewlimit'
        },
        email: {
            maxattachmentsize: 'cm_system_email_maxAttachmentSizeForEmail'
        },
        gis: {
            enabled: 'cm_system_gis_enabled',
            geoserverEnabled: 'cm_system_gis_geoserver_enabled',
            navigationTreeEnabled: 'cm_system_gis_navigation_enabled',
            initialZoom: 'cm_system_gis_initialZoomLevel',
            initialLat: 'cm_system_gis_centerLat',
            initialLon: 'cm_system_gis_centerLon',
            keepZoomAndPosition: 'cm_system_gis_keepZoomAndPosition',
            maxZoom: 'cm_system_gis_maxZoomLevel',
            minZoom: 'cm_system_gis_minZoomLevel'
        },
        login: {
            default: 'cm_system_login_default_enabled',
            modules: 'cm_system_login_modules',
            module_description: 'cm_system_login_module_{0}_description',
            module_icon: 'cm_system_login_module_{0}_icon',
            text: 'cm_system_login_help'
        },
        lookuparray: {
            separator: 'cm_system_lookuparray_value_separator'
        },
        multitenant: {
            enabled: 'cm_system_multitenant_enabled',
            mode: 'cm_system_multitenant_mode',
            name: 'cm_system_multitenant_name'
        },
        mobile: {
            enabled: 'cm_system_mobile_enabled',
            devicenameprefix: 'cm_system_mobile_devicename_prefix',
            customercode: 'cm_system_mobile_customer_code'
        },
        passwordrules: {
            diffprevious: 'cm_system_password_diff_previous',
            diffusername: 'cm_system_password_diff_username',
            enabled: 'cm_system_password_enable',
            minlength: 'cm_system_password_min_length',
            reqdigit: 'cm_system_password_req_digit',
            reqlowercase: 'cm_system_password_req_lowercase',
            requppercase: 'cm_system_password_req_uppercase',
            defaultchangepasswordfirstlogin: 'cm_system_admin_users_changePasswordRequiredForNewUser'
        },
        processes: {
            enabled: 'cm_system_workflow_enabled',
            addAttachmentsClosedActivities: 'cm_system_workflow_enableAddAttachmentOnClosedActivities'
        },
        relgraph: {
            enabled: 'cm_system_relgraph_enabled',
            baselevel: 'cm_system_relgraph_baseLevel',
            clusteringThreshold: 'cm_system_relgraph_clusteringThreshold',
            edge: {
                color: 'cm_system_relgraph_edgeColor'
            },
            node: {
                tooltipEnabled: 'cm_system_relgraph_enableNodeTooltip',
                stepRadius: 'cm_system_relgraph_stepRadius'
            }
        },
        scheduler: {
            enabled: 'cm_system_calendar_service_enabled'
        },
        services: {
            websocketsEnabled: 'cm_system_services_websocket_enabled',
            pollingInterval: 'cm_system_services_websocket_fallback_polling_interval'
        },
        ui: {
            detailwindow: {
                width: 'cm_system_ui_detailwindow_width',
                height: 'cm_system_ui_detailwindow_height'
            },
            email: {
                defaultDelay: 'cm_system_ui_email_defaultDelay',
                groupByStatus: 'cm_system_ui_email_groupByStatus'
            },
            popupwindow: {
                width: 'cm_system_ui_popupwindow_width',
                height: 'cm_system_ui_popupwindow_height'
            },
            inlinecard: {
                height: 'cm_system_ui_inlinecard_height'
            },
            redirectTo: {
                subclass: 'cm_system_ui_redirectTo_subclass_enabled',
                view: 'cm_system_ui_redirectTo_view_enabled'
            },
            notificationsoundenabled: 'cm_system_ui_notifications_sound_enabled',
            referencecombolimit: 'cm_system_ui_referencecombolimit',
            relationlimit: 'cm_system_ui_relationlimit',
            systempasswordchangeenabled: 'cm_system_password_change_enabled',
            fields: {
                decimalsSeparator: 'cm_system_ui_decimalsSeparator',
                thousandsSeparator: 'cm_system_ui_thousandsSeparator',
                dateFormat: 'cm_system_ui_dateFormat',
                timeFormat: 'cm_system_ui_timeFormat',
                startDay: 'cm_system_ui_startDay'
            }
        },
        homepage: {
            enabled: 'cm_system_homepage_enabled',
            type: 'cm_system_homepage_type',
            searchclasses: 'cm_system_homepage_searchclasses'
        }
    };

    Ext.define('CMDBuildUI.model.Configuration', {
        extend: 'Ext.data.Model',

        statics: statics,
        fields: [
            {
                name: statics.ai.chatavatar,
                type: 'string'
            },
            {
                name: statics.ai.chatavatarmode,
                type: 'string'
            },
            {
                name: statics.ai.chatname,
                type: 'string'
            },
            {
                name: statics.ai.enabled,
                type: 'boolean'
            },
            {
                name: statics.dms.enabled,
                type: 'boolean'
            },
            {
                name: statics.dms.categorylookup,
                type: 'string'
            },
            {
                name: statics.dms.descriptionmode,
                type: 'string'
            },
            {
                name: statics.dms.allowedextensions,
                type: 'string'
            },
            {
                name: statics.dms.maxfilesize,
                type: 'number'
            },
            {
                name: statics.dms.previewLimit,
                type: 'integer'
            },
            {
                name: statics.processes.enabled,
                type: 'boolean'
            },
            {
                name: statics.common.ajaxtimeout,
                type: 'integer',
                defaultValue: 60
            },
            {
                name: statics.common.redirectonlogout,
                type: 'string',
                defaultValue: null
            },
            {
                name: statics.common.instancename,
                type: 'string'
            },
            {
                name: statics.common.companylogo,
                type: 'integer'
            },
            {
                name: statics.common.defaultlanguage,
                type: 'string',
                defaultValue: 'en'
            },
            {
                name: statics.common.uselanguageprompt,
                type: 'boolean'
            },
            {
                name: statics.common.version,
                type: 'string'
            },
            {
                name: statics.common.fullversion,
                type: 'string'
            },
            {
                name: statics.common.keepalive,
                type: 'boolean'
            },
            {
                name: statics.multitenant.enabled,
                type: 'boolean'
            },
            {
                name: statics.gis.enabled, //'cm_system_gis_enabled',
                type: 'boolean'
            },
            {
                name: statics.gis.geoserverEnabled, //'cm_system_gis_geoserver_enabled',
                type: 'boolean'
            },
            {
                name: statics.gis.keepzoomandposition, //'cm_system_gis_keepzoomandposition',
                type: 'boolean'
            },
            {
                name: statics.ai.enabled, //'cm_system_ai_enabled',
                type: 'boolean'
            },
            {
                name: statics.bim.enabled, //'cm_system_bim_enabled',
                type: 'boolean'
            },
            {
                name: statics.bim.user, //'bimEmail',
                type: 'string',
                defaultValue: '__bimserver_username_placeholder__'
            },
            {
                name: statics.bim.password, //'bimPassword',
                type: 'string',
                defaultValue: '__bimserver_password_placeholder__'
            },
            {
                name: statics.gis.navigationTreeEnabled, //'cm_system_gis_navigation_enabled',
                type: 'boolean',
                defaultValue: false
            },
            {
                //RELATION GRAPH
                name: statics.relgraph.enabled, //cm_system_relgraph_enabled ok
                type: 'boolean',
                defaultValue: true
            },
            {
                name: statics.relgraph.baselevel, //'cm_system_relgraph_baseLevel',
                type: 'integer',
                defaultValue: 3
            },
            {
                name: statics.relgraph.clusteringThreshold, //'cm_system_relgraph_clusteringThreshold', //ok
                type: 'integer',
                defaultValue: 20
            },
            {
                name: statics.relgraph.edge.color, //'cm_system_relgraph_edgeColor',
                type: 'string',
                defaultValue: '#3D85C6'
            },
            {
                name: statics.relgraph.node.tooltipEnabled, //'cm_system_relgraph_nodeTooltipEnabled',
                type: 'boolean',
                defaultValue: true
            },
            {
                name: statics.relgraph.node.stepRadius, //'cm_system_relgraph_stepRadius',
                type: 'integer',
                defaultValue: 60
            },
            {
                name: statics.ui.detailwindow.width,
                type: 'integer',
                defaultValue: 75
            },
            {
                name: statics.ui.detailwindow.height,
                type: 'integer',
                defaultValue: 95
            },
            {
                name: statics.ui.popupwindow.width,
                type: 'integer',
                defaultValue: 80
            },
            {
                name: statics.ui.popupwindow.height,
                type: 'integer',
                defaultValue: 80
            },
            {
                name: statics.ui.systempasswordchangeenabled, //'cm_system_password_change_enabled',
                type: 'boolean'
            },
            {
                name: statics.ui.inlinecard.height,
                type: 'integer',
                defaultValue: 80
            },
            {
                name: statics.ui.redirectTo.subclass,
                type: 'boolean',
                defaultValue: false
            },
            {
                name: statics.ui.redirectTo.view,
                type: 'boolean',
                defaultValue: false
            },
            {
                name: statics.ui.referencecombolimit,
                type: 'integer',
                defaultValue: 500
            },
            {
                name: statics.ui.relationlimit,
                type: 'integer',
                defaultValue: 20
            },
            {
                name: statics.ui.fields.thousandsSeparator,
                type: 'string',
                defaultValue: ','
            },
            {
                name: statics.ui.fields.decimalsSeparator,
                type: 'string',
                defaultValue: '.'
            },
            {
                name: statics.ui.fields.dateFormat,
                type: 'string'
            },
            {
                name: statics.ui.fields.startDay,
                type: 'string'
            },
            {
                name: statics.ui.fields.timeFormat,
                type: 'string'
            },
            {
                name: statics.cardlock.enabled,
                type: 'boolean',
                defaultValue: false
            },
            {
                name: statics.cardlock.showuser,
                type: 'boolean',
                defaultValue: false
            },
            {
                name: statics.common.keepfilteronupdatedcard,
                type: 'boolean',
                defaultValue: false
            },
            {
                name: statics.passwordrules.enabled,
                type: 'boolean',
                defaultValue: true // TODO: default - false
            },
            {
                name: statics.passwordrules.minlength,
                type: 'integer',
                defaultValue: 8 // TODO: remove default
            },
            {
                name: statics.passwordrules.diffprevious,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.passwordrules.diffusername,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.passwordrules.reqdigit,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.passwordrules.reqlowercase,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.passwordrules.requppercase,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.passwordrules.defaultchangepasswordfirstlogin,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.scheduler.active,
                type: 'boolean',
                defaultValue: true // TODO: remove default
            },
            {
                name: statics.login.default,
                defaultValue: true,
                type: 'boolean'
            },
            {
                name: statics.login.modules,
                convert: function (value) {
                    return value ? value.split(',') : [];
                }
            },
            {
                name: statics.chat.enabled,
                type: 'boolean'
            }
        ]
    });
})();
