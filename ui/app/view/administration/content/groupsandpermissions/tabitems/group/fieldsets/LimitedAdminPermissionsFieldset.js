
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.LimitedAdminPermissionsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset',
    requires: ['CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.LimitedAdminPermissionsFieldsetModel'],
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset'
    },
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.limitedpermissions,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.limitedpermissions'
        },

        items: [{
            columnWidth: 1,
            xtype: 'container',
            itemId: 'warningcheckbox',
            margin: '0 5 20 5',
            ui: 'messagewarning',
            hidden: true,
            bind: {
                hidden: '{!errorMessage}'
            },
            items: [{
                ui: 'custom',
                xtype: 'container',
                bind: {
                    html: '{errorMessage}'
                }
            }, {
                xtype: 'button',
                ui: 'administration-warning-action-small',
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.fixpermissions,
                listeners: {
                    click: function () {
                        var view = this.up('administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset');
                        var vm = this.lookupViewModel();
                        var dependencies = vm.get("dependencyNeededKeys");
                        var store = vm.get('limitedPersmissionsStore');
                        store.each(function (dep) {
                            if (dependencies.indexOf(dep.get('_id')) > -1) {
                                vm.get('theGroup').set(Ext.String.format('{0}_view', dep.get('_id')), true);
                                dep.set('view', true);
                            }
                        });
                        view.fireEvent("updatelimitedpermission");
                    }
                }
            }]
        }, {
            columnWidth: 1,
            xtype: 'grid',
            bind: {
                store: '{limitedPersmissionsStore}'
            },
            viewConfig: {
                markDirty: false
            },
            sealedColumns: false,
            sortableColumns: true,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            menuDisabled: true,
            columns: [{
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.modules,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.modules'
                },
                dataIndex: 'description',
                align: 'left'
            }, {
                width: '75px',
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
                },
                dataIndex: 'none',
                align: 'center',
                xtype: 'checkcolumn',
                injectCheckbox: false,
                disabled: true,
                hideable: false,
                headerCheckbox: false,
                sortable: false,
                menuDisabled: true,
                bind: {
                    disabled: '{actions.view}'
                },
                listeners: {
                    beforecheckchange: 'onBeforeLimitedPermissionsCheckChange',
                    checkchange: 'onLimitedPermissionsCheckChange'
                }
            }, {
                width: '75px',
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
                },
                dataIndex: 'view',
                align: 'center',
                xtype: 'checkcolumn',
                injectCheckbox: false,
                hideable: false,
                headerCheckbox: false,
                sortable: false,
                menuDisabled: true,
                bind: {
                    disabled: '{actions.view}'
                },
                renderer: function (value, cell, record) {
                    var vm = this.lookupViewModel();
                    if (vm.get('actions.view')) {
                        cell.tdCls = 'x-item-disabled';
                    }

                    if (Ext.isEmpty(value)) {
                        return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                    }
                    var klass = '';
                    if (value) {
                        klass = 'x-grid-checkcolumn-checked';
                    }
                    return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
                },
                listeners: {
                    beforecheckchange: 'onBeforeLimitedPermissionsCheckChange',
                    checkchange: 'onLimitedPermissionsCheckChange'
                }
            }, {
                width: '75px',
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
                },
                dataIndex: 'modify',
                align: 'center',
                xtype: 'checkcolumn',
                injectCheckbox: false,
                hideable: false,
                headerCheckbox: false,
                sortable: false,
                menuDisabled: true,
                renderer: function (value, cell, record) {
                    var vm = this.lookupViewModel();
                    if (vm.get('actions.view') || record.get('_id') === '_rp_admin_roles') {
                        cell.tdCls = 'x-item-disabled';
                    }

                    if (Ext.isEmpty(value)) {
                        return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                    }
                    var klass = '';
                    if (value) {
                        klass = 'x-grid-checkcolumn-checked';
                    }
                    return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
                },
                listeners: {
                    beforecheckchange: 'onBeforeLimitedPermissionsCheckChange',
                    checkchange: 'onLimitedPermissionsCheckChange'

                }
            }]
        }]
    }]
});