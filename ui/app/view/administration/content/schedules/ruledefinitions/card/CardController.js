Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-schedules-ruledefinitions-card',
    requires: ['CMDBuildUI.util.Msg'],
    control: {
        '#': {
            afterrender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#closeBtn': {
            click: 'onCancelBtnClick'
        },
        '#applyRuleBtn': {
            click: 'onApplyRuleBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#addNotificationEmail': {
            click: 'onAddNotificationEmail'
        },
        '#addNotificationChat': {
            click: 'onAddNotificationEmail'
        },
        '#addNotificationMobile': {
            click: 'onAddNotificationEmail'
        },
        '#removeNotificationTool': {
            click: 'onRemoveNotificationToolClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.Card} view 
     */
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();

        vm.bind({
            bindTo: {
                category: '{theSchedule.category}',
                storeLoaded: '{calendarCategoryStore.complete}'
            },
            single: true
        }, function (data) {
            if (data.category && data.storeLoaded) {
                var store = vm.get('calendarCategoryStore');
                if (!store.findRecord('code', data.category, 0, true)) {
                    // create notification
                    Ext.asap(function () {
                        var message = Ext.String.format(CMDBuildUI.locales.Locales.administration.schedules.fieldvalueunavailable, CMDBuildUI.locales.Locales.administration.schedules.category);
                        CMDBuildUI.util.Notifier.showWarningMessage(message);
                    });
                }
            }
        });

        vm.bind({
            bindTo: {
                priority: '{theSchedule.priority}',
                storeLoaded: '{calendarPriorityStore.complete}'
            },
            single: true
        }, function (data) {
            if (data.priority && data.storeLoaded) {
                var store = vm.get('calendarPriorityStore');
                if (!store.findRecord('code', data.priority, 0, true)) {
                    // create notification
                    Ext.asap(function () {
                        var message = Ext.String.format(CMDBuildUI.locales.Locales.administration.schedules.fieldvalueunavailable, CMDBuildUI.locales.Locales.administration.schedules.priority);
                        CMDBuildUI.util.Notifier.showWarningMessage(message);
                    });
                }
            }
        });
        CMDBuildUI.util.Stores.load('emails.Templates');
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getGeneralProperties('both'));
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getTypeProperties('both'));
        view.add(CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.getNotificationsContainer('both'));
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.showLoader(true);
        var me = this,
            form = me.getView(),
            vm = me.getViewModel(),
            grid = vm.get('grid'),
            theSchedule = vm.get('theSchedule'),
            notificationsStore = vm.get('notificationsStore');
        Ext.Object.getAllKeys(theSchedule.data).forEach(function (property) {
            if (property.indexOf('notifications_') > -1) {
                delete theSchedule.data[property];
            }
        });
        notificationsStore.each(function (notification, index) {
            theSchedule.set(Ext.String.format('notifications___{0}____id', index), notification.get('_id'));
            theSchedule.set(Ext.String.format('notifications___{0}___template', index), notification.get('template'));
            theSchedule.set(Ext.String.format('notifications___{0}___delay', index), notification.get('delay'));
            if (notification.get('report')) {
                theSchedule.set(Ext.String.format('notifications___{0}___reports___0___code', index), notification.get('report'));
                theSchedule.set(Ext.String.format('notifications___{0}___reports___0___format', index), notification.get('report_format'));
            }
            var parametersGrid = form.down(Ext.String.format('#reportParametersGrid_{0}', index));
            // if is in app notification parametersGrid can be null
            if (parametersGrid) {
                parametersGrid.getStore().each(function (parameter) {
                    theSchedule.set(Ext.String.format('notifications___{0}___reports___0___params___{1}', index, parameter.get('key')), parameter.get('value'));
                });
            }
        });

        // set partecipants
        theSchedule.save({
            success: function (record, operation) {
                me.saveLocales(vm, record);
                if (grid) {
                    grid.fireEventArgs('itemupdated', [record]);
                }
                form.container.component.fireEvent("closed");
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = this.getViewModel();
        vm.get("theSchedule").reject(); // discard changes
        view.container.component.fireEvent("closed");
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
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theSchedule = vm.get('theSchedule'),
            viewModel = {
                data: {
                    grid: vm.get('grid'),
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
                        id: theSchedule.get('_id')
                    }
                }
            };

        var container = view.container.component;
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
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            theSchedule = vm.get('theSchedule');
        Ext.Msg.alwaysOnTop = true;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-schedule');
                    theSchedule.erase({
                        success: function (record, operation) {
                            if (grid) {
                                grid.fireEventArgs('reload', [record, 'delete']);
                            }
                            if (view.source) {
                                view.source.container.component.remove(view.source);
                            }
                            view.container.component.fireEvent("closed");
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
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theSchedule = vm.get('theSchedule'),
            viewModel = {
                data: {
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                },
                links: {
                    theSchedule: {
                        type: 'CMDBuildUI.model.calendar.Trigger',
                        create: theSchedule.clone().getData()
                    }
                }
            };

        var container = view.container.component;
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

    onToggleBtnClick: function (button, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            theSchedule = vm.get('theSchedule');
        theSchedule.set('active', !theSchedule.get('active'));
        theSchedule.save({
            success: function (record, operation) {
                if (grid) {
                    grid.fireEventArgs("itemupdated", [view, record]);
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
        var relationsPanel = getRelationsFilterTab(viewmodel, record);
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
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel],
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
     * @param {CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardModel} vm 
     * @param {Ext.data.Model} record 
     */
    saveLocales: function (vm, record) {
        var translations = [
            'theDescriptionTranslation',
            'theExtDescriptionTranslation'
        ];
        var keyFunction = [
            'getLocaleKeyOfScheduleDescription',
            'getLocaleKeyOfScheduleExtDescription'
        ];
        Ext.Array.forEach(translations, function (item, index) {
            if (vm.get(item)) {
                var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[keyFunction[index]](record.get('code'));
                vm.get(item).crudState = 'U';
                vm.get(item).crudStateWas = 'U';
                vm.get(item).phantom = false;
                vm.get(item).set('_id', translationCode);
                vm.get(item).save({
                    success: function (translations, operation) {
                        CMDBuildUI.util.Logger.log(item + " localization was saved", CMDBuildUI.util.Logger.levels.debug);
                    }
                });
            }
        });
    },

    /**
     * 
     */
    manageNotifications: function () {
        var vm = this.getViewModel();
        var notificationsStore = vm.get('notificationsStore');
        vm.bind({
            bindTo: '{theSchedule}',
            single: true
        }, function (theSchedule) {
            // find all notifications 
            var notificationsCount = Ext.Array.unique(Ext.Object.getAllKeys(theSchedule.data).join(',').match(/notifications___\d{0,2}___template/g));
            if (notificationsCount && notificationsCount.length) {
                var indexes = Ext.Array.unique(notificationsCount.join(',').match(/\d+/g) || []);
                indexes.forEach(function (index) {
                    var template = theSchedule.get(Ext.String.format('notifications___{0}___template', index)),
                        templatesStore = Ext.getStore('emails.Templates'),
                        type = templatesStore.getNotificationProviderOfTemplate(template);

                    if (template) {
                        notificationsStore.add({
                            '_id': theSchedule.get(Ext.String.format('notifications___{0}____id', index)),
                            'type': type,
                            'template': template,
                            'delay': theSchedule.get(Ext.String.format('notifications___{0}___delay', index)),
                            'report': theSchedule.get(Ext.String.format('notifications___{0}___reports___0___code', index)),
                            'report_format': theSchedule.get(Ext.String.format('notifications___{0}___reports___0___format', index))
                        });
                    }
                });
                notificationsStore.each(function (item, index) {
                    CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.addNotificationBlock(vm.getView(), 'both', index, item);
                });
            }
        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onAddNotificationEmail: function (button) {
        var vm = button.lookupViewModel(),
            notificationsStore = vm.get('notificationsStore'),
            parameters = {},
            type = button.type;

        notificationsStore.add({
            '_id': CMDBuildUI.util.Utilities.generateUUID(),
            'type': type,
            'template': '',
            'delay': -86400,
            'report': '',
            'report_format': 'pdf',
            'parameters': parameters
        });
        CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.addNotificationBlock(vm.getView(), 'both', notificationsStore.getCount() - 1, notificationsStore.last());
    },

    /**
     * 
     * @aram {Ext.data.Store} store 
     */
    onNotificationStoreChanged: function (store) {
        var vm = this.getViewModel();
        vm.set('notificationStoreLength', store.getRange().length);
    },

    /**
     * 
     * @param {Ext.tool.Tool} tool 
     */
    onRemoveNotificationToolClick: function (tool) {
        var me = this,
            vm = me.getViewModel(),
            notificationsStore = vm.get('notificationsStore');
        notificationsStore.findRecord('_id', tool.recordId).drop();
        Ext.asap(function () {
            tool.up('fieldset').destroy();
        });
    }
});