Ext.define('CMDBuildUI.view.main.header.tenants.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.main.header.tenants.GridController'
    ],

    alias: 'widget.tenants-grid',
    controller: 'main-header-tenants-grid',

    statics: {
        disabledcls: Ext.baseCSSPrefix + "item-disabled"
    },

    bind: {
        store: '{tenantsPreferences}',
        selection: '{tenants.activeTenants}'
    },

    layout: 'fit',
    forceFit: true,
    scrollable: true,

    columns: [{
        text: CMDBuildUI.locales.Locales.main.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.main.description'
        },
        hideable: false,
        dataIndex: 'description'
    }],

    selModel: {
        type: 'cmdbuildcheckboxmodel',
        allowDeselect: true,
        checkOnly: true
    },

    tbar: [{
        xtype: 'textfield',
        itemId: 'searchtenant',
        emptyText: CMDBuildUI.locales.Locales.main.searchtenant,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.main.searchtenant'
        },
        width: 250,
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        }
    }, {
        xtype: 'checkbox',
        itemId: 'checkboxIgnore',
        padding: '0 40',
        boxLabelAlign: 'before',
        hidden: true,
        bind: {
            boxLabel: '{tenants.labelIgnoreTenants}',
            value: '{tenants.ignoreTenants}',
            hidden: '{!tenants.canIgnoreTenants}'
        },
        firstTime: true,
        listeners: {
            change: function (field, newValue, oldValue, eOpts) {
                if (!field.firstTime) {
                    var text;
                    if (newValue) {
                        text = CMDBuildUI.locales.Locales.main.confirmenabletenant;
                    } else {
                        text = CMDBuildUI.locales.Locales.main.confirmdisabletenant;
                    }
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.notifier.attention,
                        text,
                        function (btnText) {
                            if (btnText === "yes") {
                                var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
                                session.set("ignoreTenants", newValue);
                                session.save({
                                    success: function () {
                                        window.location.reload();
                                    }
                                });
                            }
                        }, this);
                }
                field.firstTime = false;
            }
        }
    }, {
        xtype: 'button',
        ui: 'management-neutral-action',
        itemId: 'checkedonly',
        text: CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly,
        localized: {
            text: 'CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly'
        },
        enableToggle: true,
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbitem',
        bind: {
            html: '{fields.gridCounterHtml}'
        }
    }]

});