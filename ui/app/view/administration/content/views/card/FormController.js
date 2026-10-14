Ext.define('CMDBuildUI.view.administration.content.views.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-views-card-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        }
    },
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind(
            {
                bindTo: '{viewType}'
            },
            function (viewType) {
                if (viewType === CMDBuildUI.model.views.View.types.sql) {
                    view.down('#fakefiltertextinput_container').allowBlank = true;
                    Ext.StoreManager.get('Functions').load();
                }
                if (viewType !== CMDBuildUI.model.views.View.types.filter) {
                    view.down('allelementscombo').allowBlank = true;
                }
                if (viewType === CMDBuildUI.model.views.View.types.sql) {
                    view.down('[name="filter_input"]').allowBlank = true;
                }
            }
        );
    },

    onAfterRender: function (view) {
        var scheduleDefinitionContainer = view.down('#scheduleDefinitionId');
        if (scheduleDefinitionContainer) {
            scheduleDefinitionContainer.add(
                CMDBuildUI.view.administration.content.views.card.FieldsHelper.getScheduleDefinitions()
            );
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
        }
    },
    onAddBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
            'href',
            Ext.String.format('administration/views/_new/{0}/false', button.lookupViewModel().get('viewType')),
            this
        );
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.up('#viewtabpanel').getViewModel();
        if (vm.get('theViewFilter.sourceClassName') && !view.lookupReference('allelementscombo').getValue()) {
            view.lookupReference('allelementscombo').setValue(vm.get('theViewFilter.sourceClassName'));
        }
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.toggleEnableTabs(0);
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();

        if (form.isValid()) {
            var theViewFilter = vm.get('theViewFilter');
            if (theViewFilter.get('icon_font')) {
                theViewFilter.set(
                    'icon_font',
                    CMDBuildUI.util.helper.IconHelper.setIconDBValue(theViewFilter.get('icon_font'))
                );
            }
            theViewFilter.save({
                success: function (record, operation) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getViewUrl(record.get('_id'));
                    if (vm.get('actions.edit')) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                            'href',
                            nextUrl,
                            record.get('description'),
                            me
                        );
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        me.redirectTo(Ext.String.format('administration/views/{0}', record.get('name')), true);
                    } else {
                        var theTranslation = me.getViewModel().get('theTranslation');
                        if (theTranslation) {
                            var translationCode =
                                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewDescription(
                                    record.get('_id')
                                );
                            theTranslation.set('_id', translationCode);
                            theTranslation.crudState = 'U';
                            theTranslation.crudStateWas = 'U';
                            theTranslation.phantom = false;
                            theTranslation.save({
                                success: function (localeRecord, operation) {
                                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                            'href',
                                            nextUrl,
                                            me
                                        );
                                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                                        me.redirectTo(
                                            Ext.String.format('administration/views/{0}', record.get('name')),
                                            true
                                        );
                                    });
                                }
                            });
                        } else {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                    'href',
                                    nextUrl,
                                    me
                                );
                                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                                me.redirectTo(Ext.String.format('administration/views/{0}', record.get('name')), true);
                            });
                        }
                    }
                },
                failure: function (reason) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                }
            });
        } else {
            if (button.el.dom) {
                button.setDisabled(false);
            }
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var errorrs = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(this.getView().form);

        if (this.getViewModel().get('actions.edit')) {
            vm.get('theViewFilter').reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            this.redirectTo(Ext.History.getToken(), true);
        } else if (this.getViewModel().get('actions.add')) {
            var newHref = Ext.String.format(
                'administration/views/_new/{0}/false',
                this.getViewModel().get('theViewFilter.type')
            );
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNav = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode('href', newHref);
            vmNav.set('selected', currentNode);
            this.redirectTo(newHref, true);
        }
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
        var recordFilter = record.get('filter').length ? Ext.JSON.decode(record.get('filter')) : {};

        var popuTitle = CMDBuildUI.locales.Locales.administration.views.filterforview;

        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
            record.get('sourceClassName') || CMDBuildUI.model.calendar.Trigger.calendarClassName
        );

        popuTitle = Ext.String.format(popuTitle, record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType:
                type === CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            target: record.get('sourceClassName') || CMDBuildUI.model.calendar.Trigger.calendarClassName,
            configuration: recordFilter
        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get('sourceClassName') || CMDBuildUI.model.calendar.Trigger.calendarClassName,
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };
        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
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
        var dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: me.getViewModel().get('actions.view'),
                items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons(
                    {
                        handler: function (button) {
                            CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(
                                me.popup,
                                record,
                                'filter'
                            );
                        }
                    },
                    {
                        handler: function () {
                            me.popup.close();
                        }
                    }
                )
            }
        ];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel],
            dockedItems: dockedItems,
            listeners: listeners
        };
        if (record.get('sourceClassName')) {
            content.items.push(relationsPanel);
        }

        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content,
            {},
            {
                ui: 'administration-actionpanel',
                listeners: {
                    afterrender: function () {
                        var _relationPanel = this.down('administration-components-filterpanels-relationfilters-panel');
                        if (_relationPanel) {
                            var _vm = _relationPanel.getViewModel();
                            _vm.populateRelationStore({
                                filter: _vm.get('theFilter'),
                                type: _vm.get('objectType'),
                                name: _vm.get('objectTypeName')
                            });
                        }
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
        var vm = this.getViewModel();
        var theViewFilter = vm.get('theViewFilter');
        if (theViewFilter.get('type') === CMDBuildUI.model.views.View.types.filter) {
            theViewFilter.set('filter', '');
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
                    var theViewFilter = vm.get('theViewFilter');

                    CMDBuildUI.util.Ajax.setActionId('delete-view');
                    theViewFilter.erase({
                        success: function (record, operation) {
                            var nextUrl = 'administration/views_empty/false' + theViewFilter.get('type');
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                'href',
                                Ext.util.History.getToken(),
                                nextUrl,
                                me
                            );
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                        }
                    });
                } else {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            },
            this
        );
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
        theViewFilter.save();
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getView().up('#viewtabpanel').getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewDescription(
            vm.get('action.add') === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                ? '..'
                : vm.get('theViewFilter').get('_id')
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theTranslation',
            vm
        );
    },
    privates: {
        /**
         *
         * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var me = this;

            var filterPanel = {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },
        getRelationsFilterTab: function (viewmodel, record) {
            var me = this;
            var filterPanel = {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});
