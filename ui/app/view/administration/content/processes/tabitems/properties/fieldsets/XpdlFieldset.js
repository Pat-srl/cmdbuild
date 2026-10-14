Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.XpdlFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-xpdlfieldset',
    ui: 'administration-formpagination',
    hidde: true,
    bind: {
        hidden: '{actions.view || theProcess.prototype}'
    },
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.strings.xpdlfile,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.processes.strings.xpdlfile'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* XPDL file **********************/
                xtype: 'filefield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.file,
                emptyText: CMDBuildUI.locales.Locales.administration.processes.strings.selectxpdlfile,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.file',
                    emptyText: 'CMDBuildUI.locales.Locales.administration.processes.strings.selectxpdlfile'
                },
                reference: 'xpdlFile',
                accept: '.xpdl',
                buttonConfig: {
                    ui: 'administration-secondary-action-small'
                }
            }]
        }]
    }]
});