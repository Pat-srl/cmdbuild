Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.builder.ColumnController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-builder-column',

    control: {
        '#': {
            itemcreated: 'onItemCreated',
            itemshowMenuclick: 'onItemEllipsisClick',
            itemeditclick: 'onItemEditClick',
            itemopenclick: 'onItemOpenClick',
            itemdeleteclick: 'onItemDeleteClick',
            itemdisableclick: 'onItemEnableToggleClick',
            itemenableclick: 'onItemEnableToggleClick',
            itemcloneclick: 'onItemCloneClick'
        }
    },
    onItemCreated: function (record) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.get('_id'));

        var config = {
            xtype: 'administration-content-dashboards-common-form',
            viewModel: {
                data: {
                    theComponent: theComponent,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    owner: view.up('#entityCard'),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        };
        this.openDetailWindow(config);
    },
    onItemEllipsisClick: function (record, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            isReadOnly = vm.get('actions.view'),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.data || record.get('_id')),
            isActive = theComponent ? theComponent.get('active') : record.get('active'),
            target = e.getTarget();

        var menu = Ext.create('Ext.menu.Menu', {
            items: [
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    hidden: isReadOnly,
                    handler: function () {
                        me.onItemEditClick(record, e, eOpts);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.open,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    handler: function () {
                        me.onItemOpenClick(record, e, eOpts, true);
                    }
                },
                {
                    // Se isActive è true mostra 'Disabilita', altrimenti mostra 'Abilita'
                    text: !isActive
                        ? CMDBuildUI.locales.Locales.administration.common.actions.enable
                        : CMDBuildUI.locales.Locales.administration.common.actions.disable,
                    iconCls: !isActive
                        ? CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular')
                        : CMDBuildUI.util.helper.IconHelper.getIconId('ban', 'solid'),
                    hidden: isReadOnly,
                    handler: function () {
                        me.onItemEnableToggleClick(record, e, eOpts);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.clone,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                    hidden: isReadOnly,
                    handler: function () {
                        me.onItemCloneClick(record, e, eOpts);
                    }
                },
                '-',
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    hidden: isReadOnly,
                    handler: function () {
                        me.onItemDeleteClick(record, e, eOpts);
                    }
                }
            ]
        });

        menu.showBy(target, 'tr-br?');
        return false;
    },

    onItemEditClick: function (record, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.data);
        var config = {
            xtype: 'administration-content-dashboards-common-form',
            viewModel: {
                data: {
                    owner: view.up('#entityCard'),
                    theComponent: theComponent,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        };
        this.openDetailWindow(config);
        return false;
    },
    onItemOpenClick: function (record, e, eOpts, hideToolbar) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.data);
        var config = {
            xtype: 'administration-content-dashboards-common-form',
            viewModel: {
                data: {
                    ownerColumn: view,
                    owner: view.up('#entityCard'),
                    theComponent: theComponent,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    hideToolbar: hideToolbar
                }
            }
        };

        this.openDetailWindow(config);
        return false;
    },
    onItemDeleteClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.data),
            theDashboard = vm.get('theDashboard'),
            components = theDashboard.components(),
            rows = vm.get('rows');

        components.remove(theComponent);
        rows.each(function (row) {
            Ext.Array.forEach(row.get('columns'), function (column) {
                if (column.components && Ext.Array.contains(column.components, theComponent.getId())) {
                    Ext.Array.remove(column.components, theComponent.getId());
                }
            });
        });
        rows.fireEventArgs('datachanged', [rows]);
        return false;
    },

    onItemEnableToggleClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theComponent = vm.get('theDashboard.components').findRecord('_id', record.data),
            theDashboard = vm.get('theDashboard'),
            components = theDashboard.components(),
            component = components.getAt(
                components.findBy(function (item) {
                    return item.get('_id') === theComponent.get('_id');
                })
            ),
            rows = vm.get('rows');
        component.set('active', !component.get('active'));
        rows.fireEventArgs('datachanged', [rows]);
        return false;
    },

    onItemCloneClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            newComponent = vm.get('theDashboard.components').findRecord('_id', record.data).copyForClone();

        var gridStore = vm.get('rows').getRange();
        var columns = gridStore[view.getRowIndex()].columns || gridStore[view.getRowIndex()].get('columns');
        columns[view.getColIndex()].components.push(newComponent.get('_id'));
        view.up('#entityCard').getViewModel().get('theDashboard').components().add(newComponent);

        var config = {
            xtype: 'administration-content-dashboards-common-form',
            viewModel: {
                data: {
                    owner: view.up('#entityCard'),
                    theComponent: newComponent,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        };

        this.openDetailWindow(config);
        return false;
    },

    privates: {
        openDetailWindow: function (config, title) {
            if (!config) {
                return false;
            }
            var vm = this.getViewModel(),
                container =
                    Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                    Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.addListener('beforeclose', function (panel) {
                var cancelBtn = panel.down('#cancelBtn');
                if (cancelBtn) {
                    cancelBtn.fireEventArgs('click', [cancelBtn]);
                }
            });
            container.add(config);
        }
    }
});
