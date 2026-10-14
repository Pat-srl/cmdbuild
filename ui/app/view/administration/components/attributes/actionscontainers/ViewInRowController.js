Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-actionscontainers-viewinrow',

    mixins: ['CMDBuildUI.view.administration.components.attributes.actionscontainers.OtherPropertiesControllerMixin'],
    listen: {
        global: {
            attributeupdated: 'onAttributeUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemupdated: 'onAttributeUpdated'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * @override
     * @param {CMDBuildUI.view.administration.components.attributes.actionscontainer.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // Ext.suspendLayouts();
        var vm = view.lookupViewModel();
        view._rowContext.viewModel.bind(
            {
                bindTo: '{allAttributes}'
            },
            function (allAttributes) {
                if (vm && !vm.destroyed) {
                    // vm.set('theAttribute', view._rowContext.record);
                    vm.set('allAttributes', allAttributes);
                    vm.set('objectTypeName', view._rowContext.ownerGrid.getViewModel().get('objectTypeName'));
                    vm.set('objectType', view._rowContext.ownerGrid.getViewModel().get('objectType'));
                    vm.set('attributeName', view._rowContext.record.get('name'));

                    vm.set(
                        'attributes',
                        view._rowContext.ownerGrid.getViewModel().get('allAttributes').getData().items
                    );
                    vm.set('grid', Ext.copy(view._rowContext.ownerGrid));
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);

                    if (vm.data && vm.get('objectType') !== 'Domain' && view.child('#otherproperties')) {
                        view.child('#otherproperties').tab.show();
                        vm.set('isGroupHidden', false);
                    } else {
                        if (view.down('#groupfield')) {
                            view.down('#groupfield').destroy();
                        }
                    }

                    var pluralObjectType;
                    switch (vm.get('objectType')) {
                        case CMDBuildUI.util.helper.ModelHelper.systemClasses.class:
                            pluralObjectType = 'classes';
                            break;
                        case 'Process':
                            pluralObjectType = 'processes';
                            break;
                        case 'Domain':
                            pluralObjectType = 'domains';
                            break;
                        case 'dmsmodel':
                            pluralObjectType = 'dms/models';
                            break;
                    }

                    Ext.ClassManager.get('CMDBuildUI.model.Attribute').setProxy({
                        type: 'baseproxy',
                        url: Ext.String.format('/{0}/{1}/attributes/', pluralObjectType, vm.get('objectTypeName'))
                    });

                    vm.linkTo('theAttribute', {
                        type: 'CMDBuildUI.model.Attribute',
                        id: view._rowContext.record.get('name')
                    });
                }
            }
        );
    },

    onViewMetadataClick: function (event, buttonEl, eOpts) {
        var title = CMDBuildUI.locales.Locales.administration.emails.editvalues;
        var metadata = this.getViewModel().get('theAttribute').get('metadata');
        var _metadata = {};

        for (var key in metadata) {
            if (!Ext.String.startsWith(key, 'cm_')) {
                _metadata[key] = metadata[key];
            }
        }
        var config = {
            xtype: 'administration-components-keyvaluegrid-grid',
            viewModel: {
                data: {
                    theKeyvaluedata: _metadata,
                    theOwnerObject: this.getViewModel().get('theAttribute'),
                    theOwnerObjectKey: 'metadata'
                }
            }
        };
        CMDBuildUI.util.Utilities.openPopup('popup-add-attachmentfromdms-panel', title, config, null, {
            ui: 'administration-actionpanel'
        });
    },

    onAttributeUpdated: function (view, record) {
        new Ext.util.DelayedTask(function () {}).delay(
            100,
            function (view, record) {
                if (view.crudState) {
                    record = Ext.copy(view);
                    view = this.getView();
                }
                try {
                    var vm = view.lookupViewModel();
                    vm.set('theAttribute', record);
                } catch (error) {}
            },
            this,
            [view, record]
        );
    },

    onDeleteBtnClick: function () {
        var me = this;

        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Ajax.setActionId('delete-attribute');
                    CMDBuildUI.util.Utilities.showLoader(true);
                    vm.get('theAttribute').getProxy().type = 'baseproxy';
                    if (vm.get('pluralObjectType') === 'dmsmodels') {
                        vm.get('theAttribute')
                            .getProxy()
                            .setUrl(Ext.String.format('/dms/models/{0}/attributes', vm.get('objectTypeName')));
                    } else {
                        vm.get('theAttribute')
                            .getProxy()
                            .setUrl(
                                Ext.String.format(
                                    '/{0}/{1}/attributes',
                                    vm.get('pluralObjectType'),
                                    vm.get('objectTypeName')
                                )
                            );
                    }

                    vm.get('theAttribute').erase({
                        success: function (record, operation) {
                            if (me && !me.destroyed) {
                                var grid = me.getView().up('grid');
                                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemremoved', [
                                    grid,
                                    record,
                                    me
                                ]);
                                CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                    vm.get('objectTypeName')
                                ).getAttributes(true);
                                CMDBuildUI.util.Utilities.showLoader(false);
                            }
                        },
                        failure: function () {
                            if (me && !me.destroyed) {
                                CMDBuildUI.util.Utilities.showLoader(false);
                                vm.get('theAttribute').reject();
                                Ext.GlobalEvents.fireEventArgs('attributeupdated', [
                                    me.getView(),
                                    vm.get('theAttribute')
                                ]);
                            }
                        }
                    });
                }
            },
            me
        );
    },
    /**
     * @override
     */
    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: {
                data: {
                    theAttribute: vm.get('theAttribute'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getData().items,
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        });
    },

    onOpenBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-view',
            viewModel: {
                data: {
                    theAttribute: vm.get('theAttribute'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getRange(),
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            }
        });
    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var theAttribute = Ext.copy(vm.get('theAttribute').clone());
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: {
                data: {
                    theAttribute: theAttribute,
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getData().items,
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var theAttribute = vm.get('theAttribute');
        Ext.apply(theAttribute.data, theAttribute.getAssociatedData());
        var value = !theAttribute.get('active');
        CMDBuildUI.util.Ajax.setActionId('toggle-active-attribute');
        theAttribute.set('active', value);
        theAttribute.model = Ext.ClassManager.get('CMDBuildUI.model.Attribute');
        var url;
        if (vm.get('pluralObjectType') === 'dmsmodels') {
            url = Ext.String.format('/dms/models/{0}/attributes', vm.get('objectTypeName'));
        } else {
            url = Ext.String.format('/{0}/{1}/attributes', vm.get('pluralObjectType'), vm.get('objectTypeName'));
        }

        theAttribute.model.setProxy({
            type: 'baseproxy',
            url: url
        });

        theAttribute.save({
            success: function (record, operation) {
                var valueString = record.get('active')
                    ? CMDBuildUI.locales.Locales.administration.common.messages.enabled
                    : CMDBuildUI.locales.Locales.administration.common.messages.disabled;
                CMDBuildUI.util.Notifier.showSuccessMessage(
                    Ext.String.format(
                        '{0} {1} {2}.',
                        record.get('name'),
                        CMDBuildUI.locales.Locales.administration.common.messages.was,
                        valueString
                    ),
                    null,
                    'administration'
                );
                if (me && !me.destroyed) {
                    Ext.GlobalEvents.fireEventArgs('attributeupdated', [view, record]);
                }
            }
        });
    },

    onScheduleTriggerMenuClick: function (menuItem, e, eOpts) {
        var triggerId = menuItem.value;
        var config = {
            xtype: 'administration-content-schedules-ruledefinitions-card',
            viewModel: {
                data: {
                    action: menuItem.action,
                    actions: {
                        add: menuItem.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                        edit: menuItem.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        view: menuItem.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                    },
                    showInPopup: true
                },

                links: {
                    theSchedule: {
                        type: 'CMDBuildUI.model.calendar.Trigger',
                        id: triggerId
                    }
                }
            },
            ui: 'administration',
            source: menuItem,
            shownInPopup: true,
            tabpaneltools: [],
            dockedItems: [
                {
                    dock: 'top',
                    xtype: 'container',
                    bind: {
                        hidden: '{!actions.view}'
                    },
                    items: [
                        {
                            xtype: 'components-administration-toolbars-formtoolbar',
                            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                                {
                                    edit: true,
                                    activeToggle: true,
                                    delete: true
                                },
                                'schedules',
                                'theSchedule',
                                [],
                                [
                                    {
                                        xtype: 'tool',
                                        itemId: 'applyRuleBtn',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('play-circle', 'regular'),
                                        tooltip:
                                            CMDBuildUI.locales.Locales.administration.schedules
                                                .applyruletoexistingcards,
                                        localized: {
                                            tooltip:
                                                'CMDBuildUI.locales.Locales.administration.schedules.applyruletoexistingcards'
                                        },
                                        cls: 'administration-tool',
                                        autoEl: {
                                            'data-testid': 'administration-{0}-applyRuleBtn'
                                        },
                                        bind: {
                                            disabled: '{!theSchedule.active}',
                                            hidden: '{!actions.view}'
                                        }
                                    }
                                ]
                            )
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    itemId: 'bottomtoolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
                },
                {
                    xtype: 'toolbar',
                    itemId: 'bottomviewtoolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    hidden: true,
                    bind: {
                        hidden: '{actions.edit|| !actions.view && !showInPopup}'
                    },
                    items: CMDBuildUI.util.administration.helper.FormHelper.getCloseButton()
                }
            ]
        };

        CMDBuildUI.util.Utilities.openPopup(
            null,
            'Schedule',
            config,
            {},
            {
                ui: 'administration'
            }
        );
    },

    /**
     * On translate button click
     * @param {Event} e
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (e, button, eOpts) {
        const vm = this.getViewModel();
        let localeObjectTypeName;
        switch (vm.get('objectType').toLowerCase()) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                localeObjectTypeName = 'attributeclass';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                localeObjectTypeName = 'attributedomain';
                break;
        }
        const translationCode = Ext.String.format(
            '{0}.{1}.{2}.description',
            localeObjectTypeName,
            vm.get('objectTypeName'),
            vm.get('actions.edit') || vm.get('actions.view') ? vm.get('attributeName') : '.'
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theTranslation',
            vm
        );
    },

    privates: {
        setAttributeProxyUrl: function (record, vm) {
            let pluralObjectType;
            if (vm.get('theObject')) {
                pluralObjectType = 'classes';
            } else if (vm.get('theProcess')) {
                pluralObjectType = 'processes';
            } else if (vm.get('theDomain')) {
                pluralObjectType = 'domains';
            }
            record.model = Ext.ClassManager.get('CMDBuildUI.model.Attribute');
            record.model.setProxy({
                type: 'baseproxy',
                url: Ext.String.format('/{0}/{1}/attributes/', pluralObjectType, vm.get('objectTypeName'))
            });
        }
    }
});
