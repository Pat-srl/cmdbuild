Ext.define('CMDBuildUI.model.users.Group', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: ['CMDBuildUI.validator.TrimPresence'],

    statics: {
        mygroup: '@MY_GROUP',
        getTypes: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.normal,
                    value: 'default'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.readonlyadmin,
                    value: 'admin_readonly'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.limitedadmin,
                    value: 'admin_limited'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.usersadmin,
                    value: 'admin_users'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.admin,
                    value: 'admin'
                }
            ];
        }
    },
    fields: [
        {
            name: '_id',
            type: 'number',
            persist: false
        },
        {
            name: 'description',
            description: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            type: 'string',
            critical: true,
            validators: ['trimpresence'],
            showInGrid: true
        },
        {
            name: 'name',
            description: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            type: 'string',
            critical: true,
            validators: ['trimpresence'],
            showInGrid: true
        },
        {
            name: 'email',
            description: CMDBuildUI.locales.Locales.administration.emails.email,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.emails.email'
            },
            type: 'string',
            critical: true,
            showInGrid: true
        },
        {
            name: 'type',
            type: 'string',
            critical: true,
            defaultValue: 'default'
        },
        {
            name: 'bulkUpdate',
            type: 'auto',
            critical: true
        },
        {
            name: 'bulkDelete',
            type: 'auto',
            critical: true
        },
        {
            name: 'bulkAbort',
            type: 'auto',
            critical: true
        },
        {
            name: 'fulltext',
            type: 'string',
            critical: true
        },
        {
            name: 'processWidgetAlwaysEnabled',
            type: 'boolean',
            critical: true
        },
        {
            name: 'disabledCardTabs',
            type: 'auto',
            critical: true
        },
        {
            name: 'disabledModules',
            type: 'auto',
            critical: true
        },
        {
            name: 'disabledProcessTabs',
            type: 'auto',
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: 'startingClass',
            type: 'string',
            critical: true
        },
        {
            // used only for exclude role from clone permissions button if value == true
            name: '_rp_data_all_write',
            type: 'boolean'
        },
        {
            name: '_rp_class_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_process_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_dataview_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_dashboard_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_report_access',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: '_rp_custompages_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_dependencytree_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_card_tab_detail_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_detail_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_note_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_note_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_relation_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_relation_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_history_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_history_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_card_tab_email_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_email_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_attachment_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_attachment_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_schedule_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_card_tab_schedule_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_detail_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_detail_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_flow_tab_note_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_note_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_flow_tab_relation_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_relation_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_flow_tab_history_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_history_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_flow_tab_email_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_email_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_flow_tab_attachment_access_read',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_flow_tab_attachment_access_write',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_bulkupdate_access',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_calendar_event_create',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_calendar_access',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_gis_access',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_bim_access',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: '_rp_admin_classes_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_classes_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_processes_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_processes_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_domains_view',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_domains_modify',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_lookups_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_lookups_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_views_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_views_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_searchfilters_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_searchfilters_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_dashboards_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_dashboards_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_reports_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_reports_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_menus_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_menus_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_uicomponents_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_uicomponents_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_dms_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_dms_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_navtrees_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_navtrees_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_roles_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_roles_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_users_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_users_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_email_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_email_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_etl_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_etl_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_jobs_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_jobs_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_calendar_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_calendar_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_gis_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_gis_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_bim_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_bim_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_localization_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_localization_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: '_rp_admin_sysconfig_view',
            type: 'boolean',
            critical: true
        },
        {
            name: '_rp_admin_sysconfig_modify',
            type: 'boolean',
            critical: true,
            defaultValue: false
        }
    ],

    proxy: {
        url: '/roles/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    },

    clone: function () {
        var newGroup = this.copy();
        newGroup.set('_id', undefined);
        newGroup.set('name', Ext.String.format('{0}_clone', this.get('name')));
        newGroup.set('description', Ext.String.format('{0}_clone', this.get('description')));
        newGroup.set('isCloneOf', this.get('_id'));
        newGroup.crudState = 'C';
        newGroup.phantom = true;
        delete newGroup.crudStateWas;
        delete newGroup.previousValues;
        delete newGroup.modified;
        return newGroup;
    }
});
