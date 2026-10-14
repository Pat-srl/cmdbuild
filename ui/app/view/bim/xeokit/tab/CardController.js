Ext.define('CMDBuildUI.view.bim.xeokit.tab.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-tab-card',

    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.bim.xeokit.tab.Card} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.getViewModel(),
            container = view.getContainer();

        vm.bind(
            {
                entity: '{entity}'
            },
            function (data) {
                var mappingInfo = data.entity.mappingInfo;
                if (mappingInfo.exists) {
                    if (!mappingInfo.type) {
                        data.entity.mappingInfo.type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
                            mappingInfo.ownerType
                        );
                    }
                    CMDBuildUI.util.helper.ModelHelper.getModel(mappingInfo.type, mappingInfo.ownerType).then(function (
                        model
                    ) {
                        model.load(mappingInfo.ownerId, {
                            callback: function (record, operation, success) {
                                view.removeAll();
                                view.add(me.getCard(data.entity.mappingInfo, record));
                            }
                        });
                    });
                } else {
                    var viewPanel = container.getTabPanel();
                    if (viewPanel.getActiveTab().xtype === 'bim-xeokit-tab-card') {
                        view.removeAll();
                        viewPanel.setActiveTab(0);
                        container.getViewModel().set('enabledTabs.card', false);
                    }
                }
            }
        );
    },

    privates: {
        /**
         * Get the card to show for this entity
         * @param {Object} info contains informations for specific entity
         * @param {Object} theObject
         * @returns
         */
        getCard: function (info, theObject) {
            function actionsCard(type, id, action) {
                CMDBuildUI.util.Utilities.closePopup('bimPopup');
                url = CMDBuildUI.util.Navigation.getClassBaseUrl(type, id, action);
                CMDBuildUI.util.Utilities.redirectTo(url);
            }

            return [
                {
                    xtype: 'displayfield',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    labelWidth: 'auto',
                    cls: Ext.baseCSSPrefix + 'process-action-field',
                    fieldLabel: CMDBuildUI.locales.Locales.relationGraph.class,
                    bind: {
                        value: '{nameClass}'
                    }
                },
                {
                    xtype: 'classes-cards-card-view',
                    hideWidgets: true,
                    shownInPopup: true,
                    hideTools: false,
                    viewModel: {
                        data: {
                            objectType: info.type,
                            objectTypeName: info.ownerType,
                            objectId: info.ownerId,
                            isInBimPopup: true
                        }
                    },
                    tabpaneltools: [
                        {
                            xtype: 'tool',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                            cls: 'management-tool',
                            tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                            callback: function (owner, tool, event) {
                                actionsCard(
                                    info.ownerType,
                                    info.ownerId,
                                    CMDBuildUI.mixins.DetailsTabPanel.actions.view
                                );
                            }
                        },
                        {
                            xtype: 'tool',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-v', 'solid'),
                            id: 'menuTool',
                            cls: 'management-tool',
                            callback: function (owner, tool, event) {
                                if (owner.cardMenu) {
                                    owner.cardMenu.show();
                                } else {
                                    var model = theObject.get('_model'),
                                        isSimpleClass = model.type === CMDBuildUI.model.classes.Class.classtypes.simple,
                                        enabledservices = CMDBuildUI.util.helper.Configurations.getEnabledFeatures(),
                                        menu = Ext.create('Ext.menu.Menu', {
                                            alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++,
                                            minWidth: 35,
                                            maxWidth: 35,
                                            ui: 'actionmenu',
                                            defaults: {
                                                height: 30
                                            },
                                            items: [
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'pencil-alt',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                                                    disabled: !model._can_update
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'th-list',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.details,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[CMDBuildUI.model.users.Grant.permissions.detail_read]
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'sticky-note',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.notes,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[CMDBuildUI.model.users.Grant.permissions.note_read]
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'link',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.relations,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.relations,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[CMDBuildUI.model.users.Grant.permissions.relation_read]
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'history',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.history,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[CMDBuildUI.model.users.Grant.permissions.history_read]
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'envelope',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.emails,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[CMDBuildUI.model.users.Grant.permissions.email_read]
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'paperclip',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[
                                                            CMDBuildUI.model.users.Grant.permissions.attachment_read
                                                        ] ||
                                                        !enabledservices.dms
                                                },
                                                {
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'calendar-alt',
                                                        'solid'
                                                    ),
                                                    cls: 'management-tool',
                                                    tooltip: CMDBuildUI.locales.Locales.common.tabs.schedules,
                                                    mode: CMDBuildUI.mixins.DetailsTabPanel.actions.schedules,
                                                    hidden:
                                                        isSimpleClass ||
                                                        !model[
                                                            CMDBuildUI.model.users.Grant.permissions.schedule_read
                                                        ] ||
                                                        !enabledservices.scheduler
                                                }
                                            ],
                                            listeners: {
                                                click: function (menu, item, e, eOpts) {
                                                    actionsCard(info.ownerType, info.ownerId, item.mode);
                                                }
                                            }
                                        });
                                    menu.show();
                                    menu.alignTo('menuTool', 't-b?');
                                    owner.cardMenu = menu;
                                }
                            }
                        }
                    ]
                }
            ];
        }
    }
});
