Ext.define('CMDBuildUI.view.administration.home.widgets.taskbytype.TaskByType', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.taskbytype.TaskByTypeController',
        'CMDBuildUI.view.administration.home.widgets.taskbytype.TaskByTypeModel'
    ],
    alias: 'widget.administration-home-widgets-taskbytype-taskbytype',
    controller: 'administration-home-widgets-taskbytype-taskbytype',
    viewModel: {
        type: 'administration-home-widgets-taskbytype-taskbytype'
    },

    viewConfig: {
        markDirty: false
    },
    title: CMDBuildUI.locales.Locales.administration.home.taskbytype,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.taskbytype'
    },
    tools: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
            itemId: 'addTaskTool',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
            },
            hidden: true,
            bind: {
                hidden: '{!theSession.rolePrivileges.admin_all}'
            }
        }
    ],
    disableSelection: true,
    ui: 'admindashboard',
    forceFit: true,
    bind: {
        store: '{tasksStats}'
    },
    columns: [
        {
            dataIndex: 'description',
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            flex: 1
        },
        {
            dataIndex: 'active',
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            align: 'right',
            flex: 0.5
        },
        {
            dataIndex: 'nonactive',
            text: CMDBuildUI.locales.Locales.administration.tasks.tooltips.stopped,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.tooltips.stopped'
            },
            align: 'right',
            flex: 0.5
        },
        {
            dataIndex: 'total',
            text: CMDBuildUI.locales.Locales.administration.home.total,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.total'
            },
            align: 'right',
            flex: 0.5,
            renderer: function (value, view, record) {
                return record.get('active') + record.get('nonactive');
            }
        }
    ]
});
