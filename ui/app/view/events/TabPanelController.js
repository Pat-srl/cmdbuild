Ext.define('CMDBuildUI.view.events.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-tabpanel',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        },
        '#openBtnEvent': {
            click: 'onOpenBtnClick'
        },
        '#editBtnEvent': {
            click: 'onEditButton'
        },
        '#deleteBtnEvent': {
            click: 'onDeleteBtn'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        const vm = this.getViewModel(),
            readonly = view.getReadOnlyTabs(),
            privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");

        // set objectTypeName and objectId for inline-view
        if (!vm.get("selectedId")) {
            const config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                const record = config._rowContext.record; // get widget record
                if (record && record.getData()) {
                    vm.set("selectedId", record.getId());
                }
            }
        }

        var action = vm.get('action');
        if (readonly) {
            action = CMDBuildUI.util.Navigation.getCurrentRowTab();
            vm.set("action", CMDBuildUI.mixins.DetailsTabPanel.actions.readonly);
        }

        if (view.isInDetailWindow()) {
            view.mon(CMDBuildUI.util.Navigation.getManagementDetailsWindow(), 'beforeclose', this.onManagementDetailsWindowBeforeClose, this);
        } else {
            const height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);
            view.setHeight(view.up().getHeight() * height / 100);
        }

        //init tabs
        const configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var card, tabcard, tabnotes, tabhistory, tabemails, tabattachments, operationMode;

        switch (action) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.edit:
                card = {
                    xtype: 'events-event-edit',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit
                };
                operationMode = CMDBuildUI.locales.Locales.common.actions.edit;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.create:
                card = {
                    xtype: 'events-event-create',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create
                };
                operationMode = CMDBuildUI.locales.Locales.common.actions.add;
                break;
            default: // CMDBuildUI.mixins.DetailsTabPanel.actions.view:
                card = {
                    xtype: 'events-event-view',
                    tab: 'view',
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                    hideTools: readonly
                };
                operationMode = CMDBuildUI.locales.Locales.gis.view;
                break;
        }

        view._objectLinkName = "theEvent";

        const baseConfig = view.getObjectFormBaseConfig();
        // delete baseConfig.reference;

        tabcard = view.add({
            xtype: "panel",
            iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
            itemId: card.tabAction, //used by {activeItem}
            items: [Ext.applyIf(card, baseConfig)],
            bodyPadding: 0,
            layout: 'fit',
            autoScroll: true,
            padding: 0,
            tabAction: card.tabAction,
            operationMode: operationMode,
            tabConfig: {
                tabIndex: 0,
                title: readonly ? CMDBuildUI.locales.Locales.calendar.event : undefined,
                tooltip: !readonly ? CMDBuildUI.locales.Locales.calendar.event : undefined
            }
        });

        // Notes tab
        if (privileges.card_tab_note_access_read) {
            var bindtext = '';
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.notes;
            }
            bindtext += '{tabcounters.notes ? \'<span class="badge">\' + 1 + "</span>" : null}';
            tabnotes = view.add({
                xtype: 'events-notes-notes',
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('sticky-note', 'solid'),
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.notes, //used by {activeItem}
                readOnly: readonly || !privileges.card_tab_note_access_write,
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 1,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.notes : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.notes}'
                }
            });
        }

        // History tab
        if (privileges.card_tab_history_access_read) {
            tabhistory = view.add({
                xtype: "panel",
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('history', 'solid'),
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.history, //used by {activeItem}
                items: [{
                    xtype: 'history-grid',
                    autoScroll: true,
                    viewModel: {
                        data: {
                            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
                            objectTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
                            objectId: vm.get("selectedId"),
                            calendarView: true
                        }
                    }
                }],
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 2,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.history : undefined
                },
                layout: 'fit',
                autoScroll: true,
                padding: 0,
                bind: {
                    disabled: '{disabled.history}'
                }
            });
        };

        // Email tab
        if (privileges.card_tab_email_access_read) {
            var bindtext = '';
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.emails;
            }
            bindtext += '{tabcounters.emails ? \'<span class="badge">\' + tabcounters.emails + "</span>" : null}';

            tabemails = view.add({
                xtype: 'emails-container',
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid'),
                itemId: CMDBuildUI.mixins.DetailsTabPanel.actions.emails, //used by {activeItem}
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                operationMode: operationMode,
                bodyPadding: 0,
                readOnly: readonly || !privileges.card_tab_email_access_write,
                tabConfig: {
                    tabIndex: 3,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.emails : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.email}'
                }
            });
        };

        // Attachments tab
        if (configAttachments && privileges.card_tab_attachment_access_read) {
            var bindtext = '';
            if (readonly) {
                bindtext = CMDBuildUI.locales.Locales.common.tabs.attachments;
            }
            bindtext += '{tabcounters.attachments ? \'<span class="badge">\' + tabcounters.attachments + "</span>" : null}';

            tabattachments = view.add({
                xtype: 'dms-container',
                iconCls: readonly ? undefined : CMDBuildUI.util.helper.IconHelper.getIconId('paperclip', 'solid'),
                autoScroll: true,
                //Don't set layout fit,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                operationMode: operationMode,
                tabConfig: {
                    tabIndex: 4,
                    title: readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                    tooltip: !readonly ? CMDBuildUI.locales.Locales.common.tabs.attachments : undefined,
                    bind: {
                        text: bindtext
                    }
                },
                bind: {
                    disabled: '{disabled.attachments}'
                },
                readOnly: readonly || !privileges.card_tab_attachment_access_write,
                ignoreSchedules: true,
                viewModel: {
                    data: {
                        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
                        objectTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event
                    },
                    formulas: {
                        objectId: {
                            bind: '{theEvent._id}',
                            get: function (objectId) {
                                return objectId;
                            }
                        }
                    }
                }
            });
        }

        var activetab;
        switch (action) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                activetab = tabnotes;
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
            activetab = tabcard;
        }
        Ext.asap(function () {
            if (!view.destroyed) {
                view.setActiveTab(activetab);
            }
        });
    },

    /**
    * @param {CMDBuildUI.view.classes.cards.TabPanel} view
    * @param {Ext.Component} newtab
    * @param {Ext.Component} oldtab
    * @param {Object} eOpts
    */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        const vm = view.lookupViewModel(),
            tool = view.down("#openBtnEvent");

        if (newtab && newtab.tabAction) {
            vm.set("activetab", newtab.tabAction)
        }

        if (view.isInDetailWindow()) {
            const eventId = vm.get("record") ? vm.get("record").getId() : vm.get("selectedId") ? vm.get("selectedId") : null;
            CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(newtab.tabAction);
            this.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(eventId, newtab.tabAction));
            vm.set("titledata.operation", newtab.operationMode);
        } else if (tool) {

            //update action
            tool.action = newtab.tabAction
            CMDBuildUI.util.Navigation.updateCurrentRowTab(newtab.tabAction);

            //update tooltip
            const toolel = tool.getEl();
            if (toolel) {
                const tooltip = Ext.String.format(
                    '{0} {1}',
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

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (tool, e, owner, eOpts) {
        const vm = this.getViewModel(),
            eventId = vm.get("record") ? vm.get("record").getId() : vm.get("selectedId") ? vm.get("selectedId") : null;
        this.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(eventId, tool.action));
    },

    /**
     * Triggered on edit tool click.
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditButton: function () {
        const vm = this.getViewModel(),
            eventId = vm.get("record") ? vm.get("record").getId() : vm.get("selectedId") ? vm.get("selectedId") : null;
        CMDBuildUI.util.Ajax.setActionId("calendar.event.edit");
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                eventId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.edit),
            true);
    },

    /**
    * Triggered on open tool click.
    *
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onDeleteBtn: function (tool, event, eOpts) {
        const view = this.getView();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.classes.cards.deleteconfirmation,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    // CMDBuildUI.util.Ajax.setActionId('class.card.delete');
                    // get the object
                    view.getFormObject().erase({
                        url: CMDBuildUI.util.api.Calendar.getEventsUrl(),
                        success: function (record, operation) {

                            // fire global card deleted event
                            Ext.GlobalEvents.fireEventArgs("carddeleted", [record]);

                            if (view.isInDetailWindow()) {
                                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                            }
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }, this);
    },

    /**
     *
     */
    onManagementDetailsWindowBeforeClose: function () {
        const vm = this.getViewModel(),
            eventId = vm.get("record") ? vm.get("record").getId() : vm.get("selectedId") ? vm.get("selectedId") : null;

        //deletes the context action
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);

        //deletes the context associatedId;
        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(undefined);

        //FIXME: could be delated due to http://gitlab.example.com/cmdbuild/cmdbuild/issues/5231 improvements
        //deletes the customPage context object id associated. Is for custom pages
        CMDBuildUI.util.Navigation.updateCurrentManagementContextCustomPageObjectId(undefined);

        //FIXME: could be delated due to http://gitlab.example.com/cmdbuild/cmdbuild/issues/5231 improvements
        //deletes the view context object id. Is for views
        CMDBuildUI.util.Navigation.updateCurrentManagementContextViewObjectId(undefined);

        CMDBuildUI.util.Utilities.redirectTo(CMDBuildUI.util.Navigation.getScheduleBaseUrl(eventId), true);
    }
});
