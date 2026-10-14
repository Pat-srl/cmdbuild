Ext.define('CMDBuildUI.view.administration.content.tasks.View', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-tasks-view',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.ViewController',
        'CMDBuildUI.view.administration.content.tasks.ViewModel'
    ],

    controller: 'administration-content-tasks-view',
    viewModel: {
        type: 'administration-content-tasks-view'
    },
    config: {
        type: null,
        workflowClassName: null
    },
    bind: {
        type: '{type}',
        workflowClassName: '{workflowClassName}'
    },
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
            xtype: 'administration-content-tasks-topbar',
            region: 'north'
        },
        {
            xtype: 'administration-content-tasks-grid',
            region: 'center',
            bind: {
                hidden: '{isGridHidden}'
            }
        }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var me = this;
        var vm = me.getViewModel();
        if (me.getType()) {
            vm.set('type', me.getType());
        } else {
            // CMDBuildUI.util.Logger.log("type is not declared", CMDBuildUI.util.Logger.levels.error);
        }

        var types = CMDBuildUI.model.tasks.Task.getTypes();
        var type = Ext.Array.findBy(types, function (item) {
            if (!vm.get('subType')) {
                return item.group === me.getType();
            }
            return item.value === me.getType() && item.subType === vm.get('subType');
        });
        vm.getParent().set('title', Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.tasks.plural, (type && type.groupLabel) ? type.groupLabel : ''));
        me.callParent(arguments);
    }
});