Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.ViewInRowModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-importexport-datatemplates-card-viewinrow',
    controller: 'administration-content-importexport-datatemplates-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-importexport-datatemplates-card'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theGateTemplate: null
    },
    minHeight: 200,
    items: [

    ],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: true, // #viewBtn set true for show the button
        clone: true, // #cloneBtn set true for show the button
        'delete': true, // #deleteBtn set true for show the button
        activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
    },

        /* testId */
        'importexporttemplates',

        /* viewModel object needed only for activeTogle */
        'theGateTemplate'
    )
});