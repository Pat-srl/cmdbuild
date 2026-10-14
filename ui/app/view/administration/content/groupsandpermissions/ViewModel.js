Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        // add some model
    ],
    alias: 'viewmodel.administration-content-groupsandpermissions-view',
    data: {
        activeTab: 0,
        isFormHidden: true,
        objectTypeName: null,
        theGroup: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            group: false,
            permissions: true,
            listOfUsers: true,
            uiConfig: true,
            defaultFilters: true
        },
        toolbarHiddenButtons: {
            edit: true,
            enable: true,
            disable: true,
            clone: true
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_roles_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        startingClassManager: {
            bind: {
                startingClass: '{theGroup.startingClass}'
            },
            get: function (data) {
                if (data.startingClass) {
                    var initialPage = data.startingClass,
                        initialPageSplip = initialPage.split(':');

                    if (initialPageSplip.length > 1) {
                        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(initialPageSplip[1], initialPageSplip[0]);
                        if (object) {
                            initialPage = object.get('description');
                        }
                        return this.set('theGroup._startingClass_description', initialPage);
                    }
                    return this.set('theGroup._startingClass_description', initialPage);
                }
            }
        },
        types: {
            get: function () {
                return CMDBuildUI.model.users.Group.getTypes();
            }
        },
        typeDescription: {
            bind: {
                typesStore: '{typesStore}',
                type: '{theGroup.type}'
            },
            get: function (data) {
                if (data.typesStore) {
                    var record = data.typesStore.findRecord('value', data.type);
                    if (record) {
                        return record.get('label');
                    }
                }
                return data.type;
            }
        },
        groupLabel: {
            bind: '{theGroup}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group;
            }
        },
        disablePermissionsTabIfAdmin: {
            bind: '{theGroup}',
            get: function (theGroup) {
                var me = this;
                if (['admin', 'admin_readonly'].indexOf(theGroup.get('type')) > -1 && me.get('activeTabs.groups') === 1) {
                    me.set('activeTab', 0);
                    me.get('activeTabs').groups = 0;
                } else {
                    me.set('activeTab', me.get('activeTabs.groups') || 0);
                }
            }
        },
        action: {
            bind: {
                theGroup: '{theGroup}',
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                var action;

                if (data.isView && data.theGroup.crudState != 'C') {
                    this.set('formModeCls', 'formmode-view');
                    this.set('isFormHidden', false);
                    action = CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit && data.theGroup.crudState != 'C') {
                    this.set('formModeCls', 'formmode-edit');
                    this.set('isFormHidden', false);
                    action = CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd && data.theGroup.crudState === 'C') {
                    this.set('formModeCls', 'formmode-add');
                    this.set('isFormHidden', this.get('isFormHidden') || false);
                    action = CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
                this.configToolbarButtons();
                return action;
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },

    stores: {
        typesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{types}'
        }
    },

    configToolbarButtons: function () {
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.enable', !this.get('actions.view') || (this.get('actions.view') && this.data.theGroup.data.active));
        this.set('toolbarHiddenButtons.disable', !this.get('actions.view') || (this.get('actions.view') && !this.data.theGroup.data.active));
        this.set('toolbarHiddenButtons.clone', !this.get('actions.view'));

        if (this.get('actions.add') || this.get('actions.edit')) {
            var view = this.getView();
            var tabPanel = view.down('administration-content-groupsandpermissions-tabpanel');
            var activeTabIndex = (tabPanel && tabPanel.getActiveTab()) ? tabPanel.getActiveTab().tabIndex : 0;
            this.toggleEnableTabs(activeTabIndex);
        } else {
            this.toggleEnableTabs();
        }
        return true;
    },

    toggleEnableTabs: function (currrentTabIndex) {
        var me = this;
        var view = me.getView().down('administration-content-groupsandpermissions-tabpanel');
        var tabs = view.items.items;
        if (typeof currrentTabIndex === 'undefined') {
            tabs.forEach(function (tab) {
                if (!(['admin', 'admin_readonly'].indexOf(me.get('theGroup.type')) > -1 && tab.reference === 'permissions')) {
                    me.set('disabledTabs.' + tab.reference, false);
                }
            });
        } else {
            tabs.forEach(function (tab) {
                if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                    me.set('disabledTabs.' + tab.reference, true);
                }
            });
        }
    }
});