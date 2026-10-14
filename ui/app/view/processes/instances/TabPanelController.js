Ext.define('CMDBuildUI.view.processes.instances.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChange'
        },
        '#tab-attachments': {
            beforerender: 'onTabAttachmentsBeforeRender'
        },
        '#openProcessTool': {
            click: 'onOpenToolClick'
        },
        '#editBtn': {
            clicK: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#relgraphBtn': {
            click: 'onRelationGraphBtnClick'
        },
        '#helpBtn': {
            click: 'onHelpBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.processes.instances.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        if (CMDBuildUI.util.Ajax.getActionId() === CMDBuildUI.util.Ajax.processStatAbort) {
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            return;
        }

        // set view model variables
        var me = this,
            vm = view.lookupViewModel(),
            readonly = view.getReadOnlyTabs();

        // set objectTypeName and objectId for inline-view
        if (!vm.get("objectTypeName") && !vm.get("objectId")) {
            var rowContext = view.getInitialConfig()._rowContext;
            if (!Ext.isEmpty(rowContext)) {
                var record = rowContext.record; // get widget record
                if (record && record.getData()) {
                    vm.set("objectTypeName", record.get('_type'));
                    vm.set("objectId", record.get('_id'));
                    vm.set("activityId", record.get("_activity_id"));
                }
            }
        }

        var action;
        if (readonly) {
            vm.set("action", CMDBuildUI.mixins.DetailsTabPanel.actions.readonly);
            action = CMDBuildUI.util.Navigation.getCurrentRowTab();
        } else {
            action = vm.get("action");
        }

        // load the object
        var objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId");
        if (objectTypeName) {
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                objectTypeName
            ).then(function (model) {
                if (vm && !vm.destroyed) {
                    vm.set("objectModel", model);

                    if (objectId) {
                        vm.linkTo("theObject", {
                            type: model.getName(),
                            id: objectId
                        });
                    } else {
                        vm.linkTo("theObject", {
                            type: model.getName(),
                            create: {
                                _type: objectTypeName
                            }
                        });
                    }

                }
            });
        }

        // update url on window close
        // if panel is shown in detail window
        if (view.isInDetailWindow()) {
            // update url on window close
            view.mon(
                CMDBuildUI.util.Navigation.getManagementDetailsWindow(),
                'beforeclose',
                this.onManagementDetailsWindowBeforeClose,
                this
            );
        }

        var singleinit = false;
        vm.bind({
            bindTo: '{theObject}'
        }, function (theObject) {
            if (!Ext.isEmpty(theObject) && !singleinit) {
                me.addTabs(view, theObject, action, readonly);
                singleinit = true;
            }
        });
    },

    onManagementDetailsWindowBeforeClose: function () {
        var me = this;
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(undefined);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(undefined);
        CMDBuildUI.util.Utilities.redirectTo(me.getBasePath(false), true);
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        var vm = view.lookupViewModel(),
            tool = view.down("#openProcessTool");

        if (newtab && newtab.tabAction) {
            vm.set("activetab", newtab.tabAction)
        }

        if (view.isInDetailWindow()) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(newtab.tabAction);
            vm.set("titledata.operation", newtab.operationMode);
            vm.set("titledata.action", newtab.tabConfig.tooltip);
            CMDBuildUI.util.Ajax.setActionId("proc.inst." + newtab.tabAction + ".open");
            CMDBuildUI.util.Utilities.redirectTo(this.getBasePath(true) + '/' + newtab.tabAction);
        } else if (tool) {

            // update action
            tool.action = newtab.tabAction;
            CMDBuildUI.util.Navigation.updateCurrentRowTab(newtab.tabAction);

            // update tooltip
            var toolel = tool.getEl();
            if (toolel) {
                var tooltip = Ext.String.format(
                    "{0} {1}",
                    CMDBuildUI.locales.Locales.common.actions.open,
                    newtab.tabConfig.title
                );
                toolel.set({
                    "data-qtip": tooltip,
                    "aria-label": tooltip
                });
            }
        }

    },

    onTabAttachmentsBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(vm.get('objectTypeName'));

        if (process.get('_can_fc_attachment') == true) {
            vm.set('basepermissions.edit', true);
        }
    },

    /**
     * Triggered on open tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onOpenToolClick: function (tool, e, owner, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId"),
            activityId = vm.get("activityId");
        CMDBuildUI.view.processes.instances.Util.doOpenInstance(objectTypeName, objectId, activityId, tool.action);
    },

    /**
     * Triggered on edit tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (tool, event, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            objectTypeName = vm.get("objectTypeName"),
            objectId = vm.get("objectId"),
            activityId = vm.get("activityId");
        CMDBuildUI.view.processes.instances.Util.doEditInstance(objectTypeName, objectId, activityId);
    },

    /**
     * Triggered on delete tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (tool, event, eOpts) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        var objectTypeName = vm.get("objectTypeName");
        var objectType = vm.get("objectType");
        CMDBuildUI.view.processes.instances.Util.doAbortInstance(objectType, objectTypeName, view.getFormObject()).then(function () {
            // close detail window
            if (view && !view.destroyed && view.isInDetailWindow && view.isInDetailWindow()) {
                vm.set("objectId", null);
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            }
        });
    },

    /**
     * triggered on the relation graph btn click
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onRelationGraphBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.relgraph.open");
        var me = this;
        var obj = this.getView().getFormObject();
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: obj.get('_id'),
            _type_name: obj.get('_type_name'),
            _type: obj.get('_type')
        });
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onHelpBtnClick: function (tool, event, eOpts) {
        var vm = tool.lookupViewModel();
        var item = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(vm.get("objectTypeName"));
        var helpContent = !Ext.isEmpty(vm.get('theActivity._instructions_translation')) ? vm.get('theActivity._instructions_translation') : vm.get('help.text') || item.get('_help_translation') || item.get("help");
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.common.actions.help,
            {
                xtype: 'panel',
                html: helpContent,
                layout: 'fit',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                cls: 'x-selectable',
                scrollable: true
            }
        );
    },

    privates: {
        addTabs: function (view, theObject, action, readonly) {
            var objectTypeName = theObject.get('_type'),
                procItem = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(objectTypeName),
                processDef = theObject.get('_model') || (procItem && procItem.getData() || {}),
                disabletabs = false,
                enabledservices = CMDBuildUI.util.helper.Configurations.getEnabledFeatures(),
                activity, tabactivity, tabnotes, tabrelations,
                tabhistory, tabemails, tabattachments, operationMode;

            switch (action) {
                case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                    activity = Ext.apply(view.getEditTabConfig(), {
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit
                    });
                    operationMode = CMDBuildUI.locales.Locales.common.actions.edit;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                    disabletabs = true;
                    activity = Ext.apply(view.getCreateTabConfig(), {
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create
                    });
                    operationMode = CMDBuildUI.locales.Locales.processes.startworkflow;
                    break;
                default:
                    activity = Ext.apply(view.getViewTabConfig(), {
                        tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view
                    });
                    operationMode = CMDBuildUI.locales.Locales.gis.view;
            }

            // Activity tab
            tabactivity = view.add({
                xtype: 'panel',
                items: [Ext.apply(activity, view.getObjectFormBaseConfig())],
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
                reference: 'activity',
                layout: 'fit',
                autoScroll: true,
                padding: 0,
                bodyPadding: 0,
                tabAction: activity.tabAction,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 0,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.activity : undefined,
                    tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.activity).toLowerCase() : undefined
                }
            });

            // Notes tab
            if (processDef[CMDBuildUI.model.users.Grant.permissions.note_read]) {
                tabnotes = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('sticky-note', 'solid'),
                    items: [view.getNotesTabConfig()],
                    reference: 'notes',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 1,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.notes).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : '') + '{tabcounters.notes ? \'<span class="badge">\' + 1 + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            // Relations tab
            if (processDef[CMDBuildUI.model.users.Grant.permissions.relation_read]) {
                tabrelations = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('link', 'solid'),
                    items: [view.getRelationsTabConfig()],
                    reference: 'relations',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.relations,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 2,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.relations : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.relations).toLowerCase() : undefined
                    },
                    disabled: disabletabs
                });
            }

            // History tab
            if (processDef[CMDBuildUI.model.users.Grant.permissions.history_read]) {
                tabhistory = view.add({
                    xtype: 'panel',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('history', 'solid'),
                    items: [view.getHistoryTabConfig()],
                    reference: 'history',
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 3,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.history).toLowerCase() : undefined
                    },
                    disabled: disabletabs
                });
            }

            // Email tab
            if (processDef[CMDBuildUI.model.users.Grant.permissions.email_read]) {
                tabemails = view.add({
                    xtype: 'panel',
                    itemId: 'tab-emails',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid'),
                    items: [view.getEmailsTabConfig()],
                    reference: view._emailReference,
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 4,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.emails).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : '') + '{tabcounters.emails ? \'<span class="badge">\' + tabcounters.emails + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            // Attachments tab
            if (enabledservices.dms && processDef[CMDBuildUI.model.users.Grant.permissions.attachment_read]) {
                tabattachments = view.add({
                    xtype: 'panel',
                    itemId: 'tab-attachments',
                    iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('paperclip', 'solid'),
                    items: [view.getDmsTabConfig(processDef)],
                    bodyPadding: 0,
                    layout: 'fit',
                    autoScroll: true,
                    padding: 0,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                    operationMode: operationMode,
                    tabConfig: {
                        tabIndex: 5,
                        title: readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                        tooltip: !readonly ? (CMDBuildUI.locales.Locales.common.tabs.attachments).toLowerCase() : undefined,
                        bind: {
                            text: (readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : '') + '{tabcounters.attachments ? \'<span class="badge">\' + tabcounters.attachments + "</span>" : null}'
                        }
                    },
                    disabled: disabletabs
                });
            }

            // set view active tab
            var activetab;
            switch (action) {
                case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                    activetab = tabnotes;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.relations:
                    activetab = tabrelations;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                    activetab = tabhistory;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                    activetab = tabemails;
                    break;
                case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                    activetab = tabattachments;
                    break;
            }
            if (!activetab) {
                activetab = tabactivity;
            }
            Ext.asap(function () {
                if (view && !view.destroyed) {
                    view.setActiveTab(activetab);
                }
            });
        },

        /**
         * Get resource base path for routing.
         *
         * @param {Boolean} includeactivity
         * @param {String} action
         * @return {String}
         */
        getBasePath: function (includeactivity, action) {
            var vm = this.getViewModel(),
                typeName = vm.get("objectTypeName"),
                objectId = vm.get("objectId"),
                activityId = vm.get("activityId");
            var url = CMDBuildUI.view.processes.instances.Util.getBasePath(typeName, objectId, activityId, includeactivity, action);
            return url;
        }
    }
});