Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-schedules-ruledefinitions-card-viewinrow',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#applyRuleBtn': {
            click: 'onApplyRuleBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var vm = this.getViewModel();
        this.linkSchedule(view, vm);
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getGeneralProperties('display'));
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getTypeProperties('display'));
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getNotificationsContainer('display'));
        view.setActiveTab(0);
    },

    /**
     * On description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theSchedule = vm.get('theSchedule');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfScheduleDescription(!vm.get('actions.view') ? theSchedule.get('code') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDescriptionTranslation', vm.getParent(), true);
    },

    /**
     * On extended description translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClickExtDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theSchedule = vm.get('theSchedule');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfScheduleExtDescription(!vm.get('actions.view') ? theSchedule.get('code') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theExtDescriptionTranslation', vm.getParent(), true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            viewModel = {
                data: {
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                },
                links: {
                    theSchedule: {
                        type: 'CMDBuildUI.model.calendar.Trigger',
                        id: view.getViewModel().get('theSchedule._id')
                    }
                }
            };
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-schedules-ruledefinitions-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onViewBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            viewModel = {
                data: {
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                },
                links: {
                    theSchedule: {
                        type: 'CMDBuildUI.model.calendar.Trigger',
                        id: view.getViewModel().get('theSchedule._id')
                    }
                }
            };
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-schedules-ruledefinitions-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theSchedule = vm.get('theSchedule');
        theSchedule.set('active', !theSchedule.get('active'));
        theSchedule.save({
            success: function (record, operation) {
                var ctx = view._rowContext;
                ctx.ownerGrid.fireEventArgs('itemupdated', [theSchedule]);
            }
        });

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this,
            view = me.getView(),
            ctx = view._rowContext;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    if (ctx.record.isModel) {
                        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('delete-{0}', ctx.record.store.model.objectTypeName));
                    } else {
                        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('delete-{0}', 'unknowObjectName'));
                    }
                    ctx.record.erase({
                        success: function (record, operation) {
                            var nextIndex = ctx.record.store.isLast(ctx.record) ? (ctx.recordIndex - 1) < 0 ? null : ctx.recordIndex - 1 : ctx.recordIndex + 1;
                            ctx.ownerGrid.fireEventArgs('itemdeleted', [nextIndex]);
                        }
                    });
                }
            }, this);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-schedules-ruledefinitions-card',
            viewModel: {

                data: {

                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    theSchedule: vm.get('theSchedule').clone(),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    grid: this.getView().up('grid')
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onApplyRuleBtnClick: function (button, e, eOpts) {

        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theSchedule');
        var recordFilter = {};
        /**
         *
         *
         */
        var getAttributesFilterTab = function (_viewmodel, _record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes, // Attributes
                allowInputParameter: false,
                allowCurrentUser: true,
                allowCurrentGroup: true,
                viewModel: _viewmodel
            };
            return filterPanel;
        };

        var getRelationsFilterTab = function (_viewmodel, _record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations, // Relations
                viewModel: _viewmodel
            };
            return filterPanel;
        };

        var popuTitle = CMDBuildUI.locales.Locales.administration.schedules.applyruletoexistingcards;

        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("ownerClass"));


        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType: type === CMDBuildUI.util.helper.ModelHelper.objecttypes.view ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            target: record.get("ownerClass"),
            configuration: recordFilter,
            shared: true

        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("ownerClass"),

                actions: {
                    edit: true
                }
            }
        };
        var attrbutePanel = getAttributesFilterTab(viewmodel, record);

        var relationsPanel;
        if (CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel !== type) {
            relationsPanel = getRelationsFilterTab(viewmodel, record);
        }

        var listeners = {
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
            hidden: false,
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                text: CMDBuildUI.locales.Locales.administration.schedules.apply,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.schedules.apply'
                },
                handler: function (_button) {
                    var value = CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(me.popup, null, null, true);
                    var msg = Ext.isEmpty(value) ? CMDBuildUI.locales.Locales.administration.schedules.applyonallcards : CMDBuildUI.locales.Locales.administration.schedules.applyonmatchingcards;
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.administration.common.messages.attention,
                        msg,
                        function (btnText) {
                            if (btnText === "yes") {
                                CMDBuildUI.util.Utilities.showLoader(true);
                                CMDBuildUI.util.Ajax.setActionId('apply-schedulerule');
                                Ext.Ajax.request({
                                    url: Ext.String.format("{0}/calendar/triggers/{1}/create-events", CMDBuildUI.util.Config.baseUrl, record.get('_id')),
                                    method: 'POST',
                                    jsonData: {},
                                    timeout: 0,
                                    params: {
                                        filter: value
                                    },
                                    success: function (response) {
                                        var res = Ext.JSON.decode(response.responseText);
                                        if (res.success) {
                                            me.popup.close();
                                        }
                                    },
                                    callback: function () {
                                        CMDBuildUI.util.Utilities.showLoader(false);
                                    }
                                });
                            }
                        }, this);
                }
            }, {
                handler: function () {
                    me.popup.close();
                }
            })
        }];

        var filterTabs = [attrbutePanel];
        if (relationsPanel) {
            filterTabs.push(relationsPanel);
        }
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: filterTabs,
            dockedItems: dockedItems,
            listeners: listeners
        };

        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
            ui: 'administration-actionpanel',
            listeners: {
                afterrender: function () {
                    var relPanel = this.down('administration-filters-relations-panel');
                    if (relPanel) {
                        var _vm = relPanel.getViewModel();
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
                    theFilter: filter,
                    index: '0',
                    grid: {},
                    record: record,
                    canedit: true
                }
            }
        });
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.ViewInRow} view
     * @param {CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardModel} vm
     */
    linkSchedule: function (view, vm) {
        if (view) {
            view.mask();
            var selected = view._rowContext.record;
            vm.set('theSchedule', null);
            vm.bind({
                bindTo: {
                    theSchedule: '{theSchedule}'
                }
            }, function (data) {
                if (view && view.isMasked()) {
                    view.unmask();
                }
            });
            vm.linkTo('theSchedule', {
                type: 'CMDBuildUI.model.calendar.Trigger',
                id: selected.get('_id')
            });
        }
    },

    /**
     *
     * @param {Ext.data.Store} store
     */
    onNotificationStoreChanged: function (store) {
        var vm = this.getViewModel();
        vm.set('notificationStoreLength', store.getRange().length);
    }
});