Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContentManagementFieldset', {
    extend: 'Ext.panel.Panel',
    reguires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContentManagementFieldsetModel'
    ],
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-contentmanagementfieldset',
    viewModel: {
        type: 'administration-content-classes-tabitems-properties-fieldsets-contentmanagementfieldsetmodel'
    },

    itemId: 'customRoutesFieldset',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        collapsible: true,
        collapsed: true,
        title: CMDBuildUI.locales.Locales.administration.common.labels.contentmanagement,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.contentmanagement'
        },
        ui: 'administration-formpagination',
        defaults: {
            layout: 'column',
            columnWidth: 1
        },
        items: [{
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                    'uiRouting_mode', {
                        uiRouting_mode: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.routingbehavior, // the localized object for label of field
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.routingbehavior'
                                }
                            }, // config for fieldcontainer
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                store: '{modeComboStore}',
                                value: '{theObject.uiRouting_mode}'
                            }
                        }
                    })
            ]
        }, {
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                'uiRouting_target', {
                    uiRouting_target: {
                        fieldcontainer: {
                            hidden: true,
                            listeners: {
                                beforerender: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        mode: '{theObject.uiRouting_mode}'
                                    }, function (data) {
                                        switch (data.mode) {
                                            case 'custompage':
                                                me.setFieldLabel(CMDBuildUI.locales.Locales.administration.custompages.singular);
                                                me.setHidden(false);
                                                break;
                                            case 'view':
                                                me.setFieldLabel(CMDBuildUI.locales.Locales.administration.localizations.view);
                                                me.setHidden(false);
                                                break;
                                            default:
                                                me.setHidden(true);
                                                break;
                                        }
                                    });
                                }
                            }
                        },

                        displayfield: {
                            bind: {
                                value: '{theObject._uiRouting_target_description}'
                            },
                            listeners: {
                                beforerender: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        mode: '{theObject.uiRouting_mode}'
                                    }, function (data) {
                                        var store;
                                        switch (data.mode) {
                                            case 'custompage':
                                                store = this.get('custompagesStore');
                                                break;
                                            case 'view':
                                                store = this.get('viewsStore');
                                                break;
                                            default:

                                                break;
                                        }
                                        if (store) {
                                            var record = store.findRecord('name', this.get('theObject.uiRouting_target'));
                                            if (record) {
                                                this.set('theObject._uiRouting_target_description', record.get('description'));
                                            }
                                        }
                                    });

                                }
                            }
                        },
                        combofield: {
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                value: '{theObject.uiRouting_target}'
                            },
                            forceSelection: true,
                            listeners: {
                                beforerender: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        mode: '{theObject.uiRouting_mode}'
                                    }, function (data) {
                                        var store;
                                        switch (data.mode) {
                                            case 'custompage':
                                                store = this.get('custompagesStore');
                                                break;
                                            case 'view':
                                                store = this.get('viewsStore');
                                                break;
                                            default:
                                                store = Ext.create('Ext.data.Store');
                                                break;
                                        }
                                        me.setStore(store);
                                    });
                                }
                            }
                        }
                    }
                })]
        }, {
            items: [{
                columnWidth: 1,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.strings.routes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.strings.routes'
                },
                bind: {
                    hidden: '{theObject.uiRouting_mode !== "custom"}'
                },
                items: [{
                    xtype: 'grid',
                    itemId: 'customRoutingsGrid',
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,

                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },

                    bind: {
                        store: '{customRoutingsStore}'
                    },
                    viewConfig: {
                        enableTextSelection: true
                    },
                    forceFit: true,
                    loadMask: true,

                    columns: [{
                        flex: 0.5,
                        text: CMDBuildUI.locales.Locales.administration.common.strings.action,
                        dataIndex: 'action',
                        renderer: function (value) {
                            return CMDBuildUI.locales.Locales.administration.common.strings.routeactions[value];
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.common.labels['default'],
                        flex: 1,
                        dataIndex: 'default'
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.common.strings.route,
                        flex: 1,
                        xtype: 'widgetcolumn',
                        widget: {
                            xtype: 'fieldcontainer',
                            cls: 'grid-displayfield-container',
                            listeners: {
                                beforerender: function (fieldcontainer) {
                                    var vm = this.lookupViewModel();
                                    vm.bind('{actions.view}', function (isView) {
                                        fieldcontainer.removeAll();
                                        if (isView) {
                                            fieldcontainer.add({
                                                style: 'min-height:10px!important; margin-top:0',
                                                xtype: 'displayfield',
                                                hidden: true,
                                                bind: {
                                                    value: '{record.value}',
                                                    hidden: '{!actions.view}'
                                                },
                                                listeners: {
                                                    beforerender: function (view) {
                                                        this.setValue(fieldcontainer.getWidgetRecord().get('value'));
                                                    }
                                                }
                                            });
                                        } else {
                                            fieldcontainer.add({
                                                xtype: 'textfield',
                                                hidden: true,
                                                bind: {
                                                    hidden: '{actions.view}'
                                                },
                                                listeners: {
                                                    beforerender: function (view) {
                                                        this.setValue(fieldcontainer.getWidgetRecord().get('value'));
                                                    },
                                                    change: function (view, newValue, oldValue) {
                                                        fieldcontainer.getWidgetRecord().set('value', newValue);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                        }
                    }],

                    columnWidth: 1,
                    autoEl: {
                        'data-testid': 'contentmanagement-customrouting-grid'
                    }
                }]

            }]
        }]
    }],

    parseAndeSetObjectRoutes: function (theObject) {
        var vm = this.getViewModel();
        var customRoutings = null;
        if (theObject.get('uiRouting_mode') === 'custom') {
            customRoutings = {};
            vm.get('customRoutingsStore').each(function (route) {
                customRoutings[route.get('action')] = route.get('value');
            });
            theObject.set('uiRouting_target', null);
        }
        theObject.set('uiRouting_custom', customRoutings);
    }
});