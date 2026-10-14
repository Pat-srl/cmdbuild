Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-dmscategorytypes-view',

    data: {
        pluginMode: false,
        activeTab: 0,
        objectTypeName: null,
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            list: false,
            values: false,
            assignedon: false
        },
        allDMSCategoryTypeProxy: {
            type: 'memory'
        },
        parentTypeName: null,
        parentstoredata: {
            url: null,
            autoLoad: false
        },
        toolAction: {
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        DMSCategoryLabel: {
            bind: '{theDMSCategoryType.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory;
            }
        },
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_dms_modify}',
                theDMSCategoryType: '{theDMSCategoryType}'
            },
            get: function (data) {
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set(
                    'toolAction._canDelete',
                    !data.theDMSCategoryType.get('_is_system') && data.canModify === true
                );
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        tabManager: {
            bind: {
                action: '{action}',
                theDMSCategoryType: '{theDMSCategoryType}'
            },
            get: function (bind) {
                this.set(
                    'activeTab',
                    this.getView().up('administration-content').getViewModel().get('activeTabs.dmscategories') || 0
                );
                this.set(
                    'actions.view',
                    bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                );
                this.set(
                    'actions.edit',
                    bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                );
                this.set(
                    'actions.add',
                    bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                );

                if (bind.action !== CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
                    this.set('allDMSCategoryTypeProxy', {
                        url: '/dms/categories',
                        type: 'baseproxy',
                        extraParams: {
                            active: true
                        }
                    });
                } else {
                    this.set('allDMSCategoryTypeProxy', {
                        type: 'memory'
                    });
                }

                if (
                    bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add ||
                    bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                ) {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', true);
                    this.set('disabledTabs.assignedon', true);
                } else {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', false);
                    this.set('disabledTabs.assignedon', false);
                }
            }
        },

        DMSCategoryValuesProxy: {
            bind: '{theDMSCategoryType.name}',
            get: function (objectTypeName) {
                if (objectTypeName && !this.get('theDMSCategoryType').phantom) {
                    return {
                        url: Ext.String.format(
                            '/dms/categories/{0}/values',
                            CMDBuildUI.util.Utilities.stringToHex(objectTypeName)
                        ),
                        type: 'baseproxy',
                        extraParams: {
                            active: false
                        }
                    };
                }
            }
        },

        parentDMSCategoryValuesProxy: {
            bind: '{theDMSCategoryType.parent}',
            get: function (parentTypeName) {
                if (parentTypeName && !this.get('theDMSCategoryType').phantom) {
                    this.set(
                        'parentstoredata.url',
                        Ext.String.format(
                            '/dms/categories/{0}/values',
                            CMDBuildUI.util.Utilities.stringToHex(parentTypeName)
                        )
                    );
                    this.set('parentstoredata.autoLoad', true);
                }
            }
        },

        toolAction: {
            bind: '{theDMSCategoryType}',
            get: function (theDMSCategoryType) {
                if (!theDMSCategoryType.phantom) {
                    return {
                        _canDelete: !theDMSCategoryType.get('_is_system')
                    };
                }
            }
        }
    },

    stores: {
        allDMSCategoriesStore: {
            model: 'CMDBuildUI.model.dms.DMSCategory',
            proxy: '{allDMSCategoryTypeProxy}',
            pageSize: 0, // disable pagination
            fields: ['id', 'name'],
            autoLoad: true,
            sorters: ['name']
        },
        parentDMSCategoriesStore: {
            model: 'CMDBuildUI.model.dms.DMSCategory',
            proxy: {
                url: '{parentstoredata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{parentstoredata.autoLoad}',
            sorters: ['description']
        }
    }
});
