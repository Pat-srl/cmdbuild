Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Date', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-datefields',

    items: [{
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.administration.navigation.schedules,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.navigation.schedules'
        },
        bind: {
            hidden: '{actions.add}'
        },
        columnWidth: 1,
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            style: 'border:none; margin-top: 5px; heigth:25px',
            cls: 'components-administration-toolbars-formtoolbar',
            items: [{
                xtype: 'tool',
                align: 'right',
                itemId: 'scheduleBtn',
                cls: 'administration-tool margin-right5',
                iconCls: 'cmdbuildicon-stopwatch',
                tooltip: CMDBuildUI.locales.Locales.administration.navigation.schedules,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.navigation.schedules'
                },
                autoEl: {
                    'data-testid': 'administration-attribute-tool-scheduleBtn'
                },
                bind: {
                    disabled: '{!theAttribute.calendarTriggers.length}'
                },
                listeners: {
                    click: function (tool, e, eOpts) {
                        var vm = tool.lookupViewModel();
                        var record = vm.get('theAttribute');
                        var ctrl = tool.lookupController();
                        var menuItems = [];
                        Ext.Array.forEach(record.get('calendarTriggers'), function (item) {
                            menuItems.push({
                                action: vm.get('action'),
                                text: item.description || item._id,
                                disableClone: true,
                                listeners: {
                                    click: ctrl.onScheduleTriggerMenuClick
                                },
                                value: item._id
                            });
                        });
                        if (!tool.menu) {
                            tool.menu = Ext.create('Ext.menu.Menu', {
                                items: menuItems
                            });
                        }
                        tool.menu.showAt(e.getXY());
                    },
                    destroy: function (tool, eOpts) {
                        if (tool.menu) {
                            tool.menu.destroy();
                        }
                    }
                }
            }]
        }]
    }]
});