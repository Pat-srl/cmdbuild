Ext.define('CMDBuildUI.view.events.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-tabpanel',
    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
        objectTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
        action: null,
        activetab: null,
        disabled: {
            attachments: true,
            email: true,
            history: true,
            notes: true
        },
        permissions: {
            delete: false,
            edit: false
        },
        hiddentools: {
            delete: true,
            edit: true,
            open: true
        },
        tabcounters: {
            attachments: null,
            emails: null,
            notes: null
        },
    },

    formulas: {
        createLink: {
            bind: '{selectedId}',
            get: function (selectedId) {
                const parentModel = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
                parentModel.setProxy({
                    type: 'baseproxy',
                    url: '/calendar/events'
                });
                if (selectedId) {
                    this.setLinks({
                        theEvent: {
                            type: 'CMDBuildUI.model.calendar.Event',
                            id: selectedId
                        }
                    });
                } else {
                    this.setLinks({
                        theEvent: {
                            type: 'CMDBuildUI.model.calendar.Event',
                            create: {
                                _id: null,
                                source: CMDBuildUI.model.calendar.Sequence.source.user,
                                eventEditMode: CMDBuildUI.model.calendar.Event.eventEditMode.write,
                                onCardDeleteAction: CMDBuildUI.model.calendar.Calendar.onCardDeleteAction.clear,
                                type: CMDBuildUI.model.calendar.Trigger.eventtypes.instant,
                                status: CMDBuildUI.model.calendar.Event.status.active
                            }
                            // create: true
                        }
                    })
                }
            }
        },

        updatePermissions: {
            bind: {
                action: '{action}',
                can_write: '{theEvent._can_write}'
            },
            get: function (data) {
                if (data.action !== CMDBuildUI.mixins.DetailsTabPanel.actions.create) {
                    this.set("disabled.attachments", false);
                    this.set("disabled.email", false);
                    this.set("disabled.history", false);
                    this.set("disabled.notes", false);
                }

                this.set("permissions", {
                    delete: data.can_write,
                    edit: data.can_write
                });
            }
        },

        updateWindowTitle: {
            bind: '{theEvent.description}',
            get: function (description) {
                this.set('titledata.type', CMDBuildUI.locales.Locales.calendar.event);
                this.set('titledata.item', CMDBuildUI.util.helper.FieldsHelper.renderTextField(description, {
                    skipnewline: true
                }));
            }
        },

        updateToolsVisibility: {
            bind: {
                action: '{action}',
                activetab: '{activetab}'
            },
            get: function (data) {
                if (data.action === CMDBuildUI.mixins.DetailsTabPanel.actions.readonly) {
                    const isview = data.activetab === CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                        permissionTools = this.get("permissionTools") ? this.get("permissionTools") : { edit: true, delete: true };
                    this.set("hiddentools", {
                        edit: !isview || !permissionTools.edit,
                        delete: !isview || !permissionTools.delete,
                        open: false
                    });
                } else {
                    this.set("hiddentools", {
                        edit: false,
                        delete: false,
                        open: true
                    });
                }
            }
        }
    }
});
