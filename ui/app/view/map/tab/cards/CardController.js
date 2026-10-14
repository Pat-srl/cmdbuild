Ext.define('CMDBuildUI.view.map.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    listen: {
        global: {
            refreshCard: 'onRefreshBtnClick',
            refreshMap: 'onRefreshBtnClick'
        }
    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onBeforeRender: function (view, eOpts) {
        const me = this,
            vm = view.lookupViewModel();

        vm.bind('{theObject}',
            function (theObject) {
                if (theObject) {
                    me.refreshTheObject(theObject)
                } else {
                    view.remove('classes-cards-card-view');
                }
            });
    },

    /**
     *
     * @param {Boolean} keepSelection
     */
    onRefreshBtnClick: function (keepSelection) {
        const view = this.getView(),
            theObject = view.lookupViewModel().get("theObject");

        if (theObject) {
            this.refreshTheObject(theObject);
            if (!keepSelection) {
                view.up("map-tab-tabpanel").down("map-tab-cards-list").setSelection();
            }
        }
    },

    privates: {
        /**
         *
         * @param {Ext.data.Model} theObject
         */
        refreshTheObject: function (theObject) {
            const view = this.getView(),
                objectId = theObject.getId(),
                objectTypeName = theObject.get('_type'),
                objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);

            switch (objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    view.remove('classes-cards-card-view');
                    view.add(this.getCardObject(objectType, objectTypeName, objectId));
                    break;
                default:
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('Object Type not implemented card tab: {0}', objectType),
                        CMDBuildUI.util.Logger.levels.debug);
                    break;
            }
        },

        /**
         *
         * @param {String} objectType
         * @param {String} objectTypeName
         * @param {String} objectId
         * @returns
         */
        getCardObject: function (objectType, objectTypeName, objectId) {
            var me = this,
                vm = this.getView().lookupViewModel();
            return {
                xtype: 'classes-cards-card-view',
                itemId: 'classes-cards-card-view',
                shownInPopup: true,
                hideTools: false,
                viewModel: {
                    data: {
                        objectId: objectId,
                        objectType: objectType,
                        objectTypeName: objectTypeName
                    },
                    formulas: {
                        canUpdate: {
                            bind: '{theObject._model.' + CMDBuildUI.model.base.Base.permissions.edit + '}',
                            get: function (can_update) {
                                return !can_update;
                            }
                        }
                    }
                },
                hideInlineElements: false,
                tabpaneltools: [{
                    xtype: 'tool',
                    itemId: 'opentool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    cls: 'management-tool',
                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                    tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                    autoEl: {
                        'data-testid': 'cards-card-view-opentool'
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
                    },
                    listeners: {
                        click: function (tool, e, owner, eOpts) {
                            me.clickHandler(objectTypeName, objectId, tool.action);
                        }
                    }
                }, {
                    xtype: 'tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-v', 'solid'),
                    cls: 'management-tool',
                    autoEl: {
                        'data-testid': 'cards-card-view-openTabs'
                    },
                    callback: function (owner, tool, event) {
                        if (tool.menu) {
                            tool.menu.show();
                        } else {
                            const configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled),
                                classDef = vm.get("theObject").get("_model"),
                                items = [];

                            // edit action
                            items.push({
                                tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                                cls: 'management-tool',
                                action: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                                disabled: true,
                                bind: {
                                    disabled: '{canUpdate}'
                                }
                            });

                            // notes action
                            if (classDef[CMDBuildUI.model.users.Grant.permissions.note_read]) {
                                items.push({
                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.notes,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sticky-note', 'solid'),
                                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.notes
                                });
                            }

                            // relations action
                            if (classDef[CMDBuildUI.model.users.Grant.permissions.relation_read]) {
                                items.push({
                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.relations,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('link', 'solid'),
                                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.relations
                                });
                            }

                            // history action
                            if (classDef[CMDBuildUI.model.users.Grant.permissions.history_read]) {
                                items.push({
                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.history,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('history', 'solid'),
                                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.history
                                });
                            }

                            // email action
                            if (classDef[CMDBuildUI.model.users.Grant.permissions.email_read]) {
                                items.push({
                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.emails,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid'),
                                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.emails
                                });
                            }

                            // attachments action
                            if (configAttachments && classDef[CMDBuildUI.model.users.Grant.permissions.attachment_read]) {
                                items.push({
                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('paperclip', 'solid'),
                                    action: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments
                                });
                            }

                            tool.menu = Ext.create('Ext.menu.Menu', {
                                autoShow: true,
                                items: items,
                                ui: 'default',
                                defaults: {
                                    height: 32,
                                    listeners: {
                                        click: function (tool, e, owner, eOpts) {
                                            me.clickHandler(objectTypeName, objectId, tool.action);
                                        }
                                    }
                                },
                                viewModel: {
                                    parent: tool.lookupViewModel()
                                }
                            });
                            tool.menu.setMinWidth(35);
                            tool.menu.setWidth(35);
                            tool.menu.alignTo(tool.el.id, 't-b?');
                        }
                    },
                    listeners: {
                        destroy: function (tool, eOpts) {
                            if (tool.menu) {
                                tool.menu.destroy();
                            }
                        }
                    }
                }],
                hideWidgets: true
            };
        },

        /**
         *
         * @param {String} objectTypeName
         * @param {String} objectId
         * @param {String} action
         */
        clickHandler: function (objectTypeName, objectId, action) {
            this.redirectTo(CMDBuildUI.util.Navigation.getClassBaseUrl(objectTypeName, objectId, action));
        }
    }

});
