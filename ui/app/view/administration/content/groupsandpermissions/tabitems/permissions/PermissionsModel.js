Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-permissions',

    data: {
        activeTab: 0,
        currentObjectType: null,

        disabledTabs: {
            classes_tab: false,
            processes_tab: true,
            views_tab: false,
            filters_tab: false,
            dashboards_tab: false,
            reports_tab: false,
            custompages_tab: false,
            importexports_tab: false,
            etlgate_tab: false,
            dependencytree_tab: false,
            other: false
        }
    },

    formulas: {
        hiddenColumns: {
            bind: '{objectType}',
            get: function (objectType) {
                switch (objectType) {
                    case 'classes':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: true,
                            modeTypeRead: false,
                            modeTypeWFPlus: true,
                            modeTypeWFBasic: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: false,
                            actionFilter: false,
                            actionResetFilter: false,
                            actionActionDisabled: false,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true
                        };
                    case 'processes':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: true,
                            modeTypeRead: true,
                            modeTypeWFBasic: false,
                            modeTypeWFPlus: false,
                            modeTypeWFDefault: false,
                            modeTypeWrite: true,
                            actionFilter: false,
                            actionResetFilter: false,
                            actionActionDisabled: false,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true
                        };
                    case 'views':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: true,
                            modeTypeRead: false,
                            modeTypeWFBasic: true,
                            modeTypeWFPlus: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: true,
                            actionFilter: true,
                            actionResetFilter: true,
                            actionActionDisabled: false,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true
                        };
                    case 'searchFilters':
                    case 'dashboards':
                    case 'reports':
                    case 'custompages':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: true,
                            modeTypeRead: false,
                            modeTypeWFBasic: true,
                            modeTypeWFPlus: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: true,
                            actionFilter: true,
                            actionResetFilter: true,
                            actionActionDisabled: true,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true
                        };
                    case 'etltemplate':
                    case 'etlgate':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: false,
                            modeTypeRead: true,
                            modeTypeWFPlus: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: true,
                            actionFilter: true,
                            actionResetFilter: true,
                            actionActionDisabled: true,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true,
                            modeTypeWFBasic: true
                        };
                    case 'dependencytree':
                        return {
                            modeTypeNone: false,
                            modeTypeAllow: true,
                            modeTypeRead: false,
                            modeTypeWFBasic: true,
                            modeTypeWFPlus: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: true,
                            actionFilter: true,
                            actionResetFilter: true,
                            actionActionDisabled: true,
                            modeTypeNoneOther: true,
                            modeTypeReadOther: true,
                            modeTypeWriteOther: true
                        };
                    case 'other':
                        return {
                            modeTypeNone: true,
                            modeTypeAllow: true,
                            modeTypeRead: true,
                            modeTypeWFPlus: true,
                            modeTypeWFDefault: true,
                            modeTypeWrite: true,
                            actionFilter: true,
                            actionResetFilter: true,
                            actionActionDisabled: true,
                            modeTypeNoneOther: false,
                            modeTypeReadOther: false,
                            modeTypeWriteOther: false,
                            modeTypeWFBasic: true
                        };
                    default:
                        break;
                }
            }
        }
    },

    stores: {
        grantsChainedStore: {
            type: 'chained',
            source: 'groups.Grants',
            sorters: ['_object_description'],
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0 // disable pagination
        },
        treeStore: {
            type: 'tree',
            model: 'CMDBuildUI.model.users.ClassGrant',
            sorters: ['text'],
            defaultRootId: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
            parentIdProperty: 'parentId'
        }
    },

    /**
     * Enable/disable one or multiple tabs of
     * "CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.Permissions" tab panel
     * @param {Number} currrentTabIndex
     */
    toggleEnableTabs: function (currentTabIndex) {
        var me = this;
        var view = me.getView();
        var tabs = view.items.items;
        var mainTabs = view.up('administration-content-groupsandpermissions-tabpanel').items.items;

        if (typeof currentTabIndex === 'undefined') {
            tabs.forEach(function (tab) {
                tab.enable();
            });
            mainTabs.forEach(function (tab) {
                tab.enable();
            });
        } else {
            tabs.forEach(function (tab) {
                if (tab.tabConfig.tabIndex !== currentTabIndex) {
                    tab.disable();
                } else {
                    tab.enable();
                }
            });
            mainTabs.forEach(function (tab) {
                if (tab.tabConfig.tabIndex !== 1) {
                    tab.disable();
                } else {
                    tab.enable();
                }
            });
        }
    },

    /**
     * Change form mode
     *
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:
                me.set('actions.view', true);
                me.set('actions.edit', false);
                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                break;
        }
    }
});
