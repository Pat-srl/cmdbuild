Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-group-properties',

    require: [
        'CMDBuildUI.util.Utilities',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#limitedadminpermissionsfieldset': {
            updatelimitedpermission: 'checkPermissions'
        }
    },

    /**
    * 
    * @param {Ext.button.Button} button 
    */
    onEditBtnClick: function (button) {
        var view = this.getView().up('administration-content-groupsandpermissions-view');
        var vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.down('administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset').down('grid').reconfigure();
        vm.toggleEnableTabs(0);
    },

    /**
    * 
    * @param {Ext.button.Button} button 
    */
    onToggleEnableBtnClick: function (button) {
        button.setDisabled(false);
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);

        var value = !vm.get('theGroup.active');
        vm.set('theGroup.active', value);
        vm.get('theGroup').save({
            success: function (record, operation) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.messages.success'
                    },
                    html: Ext.String.format('Group was {0} correctly.', (value) ? 'activated' : 'deactivated'), // todo: translate
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'solid'),
                    align: 'br'
                });
                vm.configToolbarButtons();
                w.show();
            },
            callback: function (record, reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },


    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onCloneBtnClick: function (button) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        var nextUrl = Ext.String.format('administration/groupsandpermissions/clone/{0}', vm.get('theGroup._id'));
        this.redirectTo(nextUrl, true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        button.setDisabled(true);
        var vm = this.getViewModel();
        var mainVM = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        if (vm.get('theGroup').isValid()) {
            var theGroup = vm.get('theGroup');
            delete theGroup.data.system;
            Ext.apply(theGroup.data, theGroup.getAssociatedData());

            me.manageAdminPermissions(theGroup);

            theGroup.save({
                success: function (record, operation) {
                    me.saveLocales(Ext.copy(vm), record);
                    var nextUrl = Ext.String.format('administration/groupsandpermissions/{0}', record.get('_id'));
                    if (vm.get('actions.edit')) {
                        var treestore = Ext.getCmp('administrationNavigationTree').getStore();
                        var selected = treestore.findNode("href", nextUrl);
                        selected.set('text', record.get('description'));
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                    } else {
                        if (vm.get('isCloneOf')) {

                            var proxyUrl = CMDBuildUI.util.administration.helper.ApiHelper.server.getRoleGrantsUrl(vm.get('isCloneOf')),
                                grantsStore = Ext.create('Ext.data.Store', {
                                    extend: 'CMDBuildUI.store.Base',
                                    requires: [
                                        'CMDBuildUI.store.Base',
                                        'CMDBuildUI.model.users.Grant'
                                    ],
                                    model: 'CMDBuildUI.model.users.Grant',
                                    pageSize: 0,
                                    autoLoad: false,
                                    autoDestroy: true
                                });

                            grantsStore.getProxy().type = 'baseproxy';
                            grantsStore.getModel().getProxy().setUrl(proxyUrl);
                            grantsStore.load({
                                callback: function (items) {
                                    items.filter(function (item) {
                                        return item.get('_id') !== null;
                                    });
                                    var jsonData = items.map(function (item) {
                                        return item.getData();
                                    });

                                    Ext.Ajax.request({
                                        url: CMDBuildUI.util.administration.helper.ApiHelper.server.getRoleGrantsPostUrl(record.get('_id')),
                                        method: 'POST',
                                        jsonData: jsonData,
                                        callback: function () {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                                function () {
                                                    var treeComponent = Ext.getCmp('administrationNavigationTree');
                                                    var treeComponentStore = treeComponent.getStore();
                                                    var selected = treeComponentStore.findNode("href", nextUrl);

                                                    treeComponent.setSelection(selected);
                                                });
                                            grantsStore.destroy();
                                            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPermissionUrl(record.get('_id'));
                                            if (button.el.dom) {
                                                button.setDisabled(false);
                                            }
                                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                                            me.redirectTo(nextUrl, true);
                                        }
                                    });
                                }
                            });
                        } else {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                function () {
                                    var treeComponent = Ext.getCmp('administrationNavigationTree');
                                    var treeComponentStore = treeComponent.getStore();
                                    var selected = treeComponentStore.findNode("href", nextUrl);

                                    treeComponent.setSelection(selected);
                                });
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            me.redirectTo(nextUrl, true);
                        }
                    }
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.toggleEnableTabs();
        vm.get('theGroup').reject();
        if (vm.get('actions.add')) {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPermissionUrl();
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
        } else {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theGroup = vm.get('theGroup');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGroupDescription(vm.get('actions.edit') ? theGroup.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theGroupDescriptionTranslation', vm.getParent());
    },

    /**
     * 
     * @param {Ext.grid.column.Check} checkcolumn 
     * @param {Number} index 
     * @param {Boolean} value 
     * @param {Ext.data.Model} record 
     * @returns {Boolean}
     */
    onBeforeLimitedPermissionsCheckChange: function (checkcolumn, index, value, record) {
        return value;
    },

    /**
     * 
     * @param {Ext.grid.column.Check} checkcolumn 
     * @param {Number} index 
     * @param {Boolean} value 
     * @param {Ext.data.Model} record 
     * 
     */
    onLimitedPermissionsCheckChange: function (checkcolumn, index, value, record) {
        if (value) {
            var me = this, vm = checkcolumn.lookupViewModel(),
                theGroup = vm.get('theGroup'), errors = [];

            switch (checkcolumn.dataIndex) {
                case 'none':
                    record.set('view', false);
                    record.set('modify', false);
                    theGroup.set(Ext.String.format('{0}_view', record.get('_id')), false);
                    theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), false);
                    break;
                case 'view':
                    record.set('view', true);
                    record.set('modify', false);
                    theGroup.set(Ext.String.format('{0}_view', record.get('_id')), true);
                    theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), false);
                    break;
                case 'modify':
                    record.set('view', false);
                    record.set('modify', true);
                    theGroup.set(Ext.String.format('{0}_view', record.get('_id')), true);
                    theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), true);
                    break;
            }
        }
        setTimeout(function () {
            me.checkPermissions();
        });
    },

    /**
    * 
    * @param {Ext.grid.column.Check} checkcolumn     
    * @param {Boolean} value     
    * 
    */
    onLimitedPermissionsHeaderCheckChange: function (checkcolumn, value) {
        if (value) {
            var vm = checkcolumn.lookupViewModel(),
                store = vm.get('limitedPersmissionsStore'),
                theGroup = vm.get('theGroup'),
                records = store.getRange();

            switch (checkcolumn.dataIndex) {
                case 'none':
                    records.forEach(function (record) {
                        theGroup.set(Ext.String.format('{0}_view', record.get('_id')), false);
                        theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), false);
                        record.set('view', false);
                        record.set('modify', false);
                    });
                    break;
                case 'view':
                    records.forEach(function (record) {
                        theGroup.set(Ext.String.format('{0}_view', record.get('_id')), true);
                        theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), false);
                        record.set('view', true);
                        record.set('modify', false);
                    });
                    break;
                case 'modify':
                    records.forEach(function (record) {
                        theGroup.set(Ext.String.format('{0}_view', record.get('_id')), true);
                        theGroup.set(Ext.String.format('{0}_modify', record.get('_id')), true);
                        record.set('view', false);
                        record.set('modify', true);
                    });
                    break;

                default:
                    break;
            }
        }
    },


    privates: {
        saveLocales: function (vm, record) {
            if (vm.get('actions.add')) {
                if (vm.get('theGroupDescriptionTranslation')) {
                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGroupDescription(record.get('name'));
                    vm.get('theGroupDescriptionTranslation').crudState = 'U';
                    vm.get('theGroupDescriptionTranslation').crudStateWas = 'U';
                    vm.get('theGroupDescriptionTranslation').phantom = false;
                    vm.get('theGroupDescriptionTranslation').set('_id', translationCode);
                    vm.get('theGroupDescriptionTranslation').save({
                        success: function (translations, operation) {
                            CMDBuildUI.util.Logger.log('theGroupDescriptionTranslation' + " localization was saved", CMDBuildUI.util.Logger.levels.debug);
                        }
                    });
                }
            }
        },

        checkPermissions: function () {
            notAutorized = new Set();
            var view = this.getView().down('administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset'),
                vm = view.lookupViewModel(),
                theGroup = vm.get('theGroup'),
                limitedPermissionStore = vm.get('limitedPersmissionsStore'),
                dependencyNeededKeys = [];

            limitedPermissionStore.each(function (permission) {
                if (theGroup.get(permission.get('_id') + '_view'))
                    permission.get('dependencies').forEach(function (dep) {
                        if (dep !== '_rp_admin_corecomponents_view' && !theGroup.get(dep)) {
                            var dependency = limitedPermissionStore.findRecord('_id', dep.replace('_view', '').replace('_update', ''));
                            if (dependency && !theGroup.get(dependency.get('_id') + '_view')) {
                                dependencyNeededKeys.push(dependency.get('_id'));
                                notAutorized.add(dependency.get('description'));
                            }
                        }
                    });
            });
            vm.set('dependencyNeededKeys', dependencyNeededKeys);
            if (notAutorized.size) {
                vm.set('errorMessage', Ext.String.format('{0}: {1}', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.followingreadpermissions, Array.from(notAutorized).join(', ')));
            } else {
                vm.set('errorMessage', null);
            }
            return notAutorized;
        },

        manageAdminPermissions: function (theGroup) {
            var modelKeys = Ext.Object.getKeys(theGroup.getData());
            switch (theGroup.get('type')) {
                case 'admin':
                    modelKeys.forEach(function (modelKey) {
                        if (modelKey.startsWith('_rp_admin_')) {
                            theGroup.set(modelKey, true);
                        }
                    });
                    theGroup.set('_rp_admin_all_readonly', false);
                    break;
                case 'admin_readonly':
                    modelKeys.forEach(function (modelKey) {
                        if (modelKey.startsWith('_rp_admin_') && modelKey.endsWith('_view')) {
                            theGroup.set(modelKey, true);
                        }
                        if (modelKey.startsWith('_rp_admin_') && modelKey.endsWith('_modify')) {
                            theGroup.set(modelKey, false);
                        }
                    });
                    theGroup.set('_rp_admin_all_readonly', true);
                    theGroup.set('_rp_admin_all', true);
                    theGroup.set('_rp_admin_access', true);
                    break;
                case 'admin_users':
                    modelKeys.forEach(function (modelKey) {
                        if (modelKey.startsWith('_rp_admin_') && modelKey.endsWith('_view')) {
                            theGroup.set(modelKey, true);
                        }
                        if (modelKey.startsWith('_rp_admin_') && modelKey.endsWith('_modify')) {
                            theGroup.set(modelKey, false);
                        }
                    });
                    theGroup.set('_rp_admin_roles_view', true);
                    theGroup.set('_rp_admin_users_view', true);

                    theGroup.set('_rp_admin_roles_modify', true);
                    theGroup.set('_rp_admin_users_modify', true);
                    break;
                case 'default':
                    modelKeys.forEach(function (modelKey) {
                        if (modelKey.startsWith('_rp_admin_')) {
                            theGroup.set(modelKey, false);
                        }
                    });
                    break;
                default:
                    // limited admin have manual settings
                    break;
            }
        }
    }

});