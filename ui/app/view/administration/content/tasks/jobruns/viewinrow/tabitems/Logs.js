Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.tabitems.Logs', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-jobruns-viewinrow-tabitems-logs',
    ui: 'administration-formpagination',
    layout: 'column',
    items: [{
        xtype: 'fieldcontainer',
        layout: 'column',
        columnWidth: 1,
        items: [{
            xtype: 'textarea',
            readOnly: true,
            height: 300,
            labelAlign: 'top',
            columnWidth:1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.logs,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.logs'
            },
            bind: {
                value: '{theJobrun.logs}'
            }
        }]
    }]
});