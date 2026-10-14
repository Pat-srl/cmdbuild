Ext.define('CMDBuildUI.view.administration.components.viewfilters.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-viewfilters-card-form',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        },

        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },
    onAfterRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.searchfilters);
    },


    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.up('administration-content-searchfilters-tabpanel').getViewModel();
        button.up('administration-content-searchfilters-tabpanel').getViewModel().toggleEnableTabs(0);
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },
    onDeleteBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var theViewFilter = vm.get('theViewFilter');
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-serachfilter');
                    theViewFilter.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(record.get('name')));
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        }
                    });
                }
            }, me);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        if (form.isValid()) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            var theViewFilter = vm.get('theViewFilter');
            var groups = vm.getStore('rolesStore') ? vm.getStore('rolesStore').getRange() : [];
            var defaultFor = [];
            Ext.Array.forEach(groups, function (item) {
                if (item.get('active')) {
                    defaultFor.push({
                        _id: item.get('_id')
                    });
                }
            });
            theViewFilter.save({
                success: function (record, operation) {
                    Ext.GlobalEvents.fireEventArgs("itemcreated", [record]);
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    CMDBuildUI.util.administration.helper.AjaxHelper.setGroupsForFilter(theViewFilter.getId(), defaultFor).then(
                        function (response) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(theViewFilter.get('name'));
                            if (vm.get('actions.edit')) {
                                var newDescription = record.get('description');
                                CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            } else {
                                var theTranslation = me.getViewModel().get('theTranslation');
                                if (theTranslation) {
                                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSearchFiltreDescription(record.get('target'), record.get('name'));
                                    theTranslation.set('_id', translationCode);
                                    theTranslation.crudState = 'U';
                                    theTranslation.crudStateWas = 'U';
                                    theTranslation.phantom = false;
                                    theTranslation.save({
                                        success: function (localeRecord, operation) {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                                function () {
                                                    if (button.el && button.el.dom) {
                                                        button.setDisabled(false);
                                                    }
                                                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                                                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                                });
                                        }
                                    });
                                } else {
                                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                        function () {
                                            if (button.el && button.el.dom) {
                                                button.setDisabled(false);
                                            }
                                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                        });
                                }
                            }
                        },
                        function (error) {
                            CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                            if (button.el && button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        }
                    );
                },
                failure: function (reason) {
                    if (button.el && button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);

                }
            });
        } else {

            button.setDisabled(false);
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var nextUrl;
        var me = this;
        var view = me.getView();
        var vm = view.up('administration-content-searchfilters-tabpanel').getViewModel();
        vm.get("theViewFilter").reject();
        if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl();
        } else {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            button.up().fireEvent("closed");
            nextUrl = Ext.History.getToken();

        }
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditFilterBtn: function (button, e, eOpts) {
        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theViewFilter');
        var actions = vm.get('actions');
        var recordFilter = record.get('configuration').length ? JSON.parse(record.get('configuration')) : {};


        var popuTitle = actions.view ?
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.viewfilters :
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.editfilters;

        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("target"));


        popuTitle = Ext.String.format(
            popuTitle,
            type,
            record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            target: record.get("target"),
            configuration: recordFilter,
            shared: true

        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("target"),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };
        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var functionPanel = this.getFunctionFilterTab(viewmodel, record)
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
        var attachmentsPanel = this.getDmsFilterTab(viewmodel);
        var listeners = {
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            applyfilter: function (panel, filter, eOpts) {
                me.onApplyFilter(filter);
                me.popup.close();
            },
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            saveandapplyfilter: function (panel, filter, eOpts) {
                me.onSaveAndApplyFilter(filter);
                me.popup.close();
            },
            /**
             * Custom event to close popup directly from popup
             * @param {Object} eOpts 
             */
            popupclose: function (eOpts) {
                me.popup.close();
            }
        };
        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (_button) {
                    CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(me.popup, record, 'configuration');
                }
            }, {
                handler: function () {
                    me.popup.close();
                }
            })
        }];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel, functionPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };
        if (attachmentsPanel) {
            content.items.push(attachmentsPanel);
        }

        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
            ui: 'administration-actionpanel',
            listeners: {
                afterrender: function () {
                    var _vm = this.down('administration-components-filterpanels-relationfilters-panel').getViewModel();
                    _vm.populateRelationStore({
                        filter: _vm.get('theFilter'),
                        type: _vm.get('objectType'),
                        name: _vm.get('objectTypeName')
                    });
                }
            },
            viewModel: {
                data: {
                    index: '0',
                    grid: {},
                    record: record,
                    canedit: true
                }
            }
        }
        );
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveFilterBtn: function (button, e, eOpts) {
        button.lookupViewModel().get('theViewFilter').set('configuration', '');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theViewFilter = vm.get('theViewFilter');
        theViewFilter.set('active', !theViewFilter.get('active'));

        theViewFilter.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });

    },
    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var className = vm.get('action') !== CMDBuildUI.util.administration.helper.FormHelper.formActions.add ? vm.get('theViewFilter').get('target') : '..';
        var filterName = vm.get('action') !== CMDBuildUI.util.administration.helper.FormHelper.formActions.add ? vm.get('theViewFilter').get('name') : '..';
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSearchFiltreDescription(className, filterName);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },
    privates: {
        /**
         * 
         * 
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes, // Attributes
                allowInputParameter: true,
                allowCurrentUser: true,
                allowCurrentGroup: true,
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getRelationsFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations, // Relations
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getDmsFilterTab: function (viewmodel) {
            // add attachments panel
            viewmodel.data.displayOnly = viewmodel.data.actions.view;
            if (
                CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms // dms is enabled
            ) {
                return {
                    xtype: 'filters-attachments-panel',
                    reference: 'attachmentspanel',
                    viewModel: viewmodel
                };
            }
        },

        getFunctionFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-functionfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});