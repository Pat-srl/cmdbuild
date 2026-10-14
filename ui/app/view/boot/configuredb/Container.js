Ext.define('CMDBuildUI.view.boot.configuredb.Container', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.boot.configuredb.ContainerController'],

    alias: 'widget.boot-configuredb-container',

    controller: 'boot-configuredb-container',

    cls: Ext.baseCSSPrefix + 'boot-main-container',

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'top'
    },

    items: [
        {
            xtype: 'container',
            width: '80%',
            height: '80%',
            minWidth: 700,
            maxWidth: 1000,

            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'container',
                    margin: '100 0 20 0',
                    layout: {
                        type: 'hbox',
                        pack: 'center',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'main-header-logodark',
                            height: 60,
                            width: '45%'
                        }
                    ]
                },
                {
                    xtype: 'boot-configuredb-panel',
                    flex: 2
                }
            ]
        }
    ]
});
