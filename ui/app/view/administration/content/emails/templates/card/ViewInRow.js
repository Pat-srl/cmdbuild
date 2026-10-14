Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.FormController',
        'CMDBuildUI.view.administration.content.emails.templates.card.FormModel',
        'CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper'
    ],

    alias: 'widget.administration-content-emails-templates-card-viewinrow',
    controller: 'administration-content-emails-templates-card-form',
    viewModel: {
        type: 'administration-content-emails-templates-card-form'
    },
    config: {
        theTemplate: null
    },
    cls: 'administration',
    ui: 'administration-tabandtools',
    autoScroll: true,
    bind: {
        userCls: '{formModeCls}',
        activeTab: '{activeTab}'
    },
    items: [],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true, // #editBtn set true for show the button
            activeToggle: true,
            view: true, // #viewBtn set true for show the button
            clone: true, // #cloneBtn set true for show the button
            'delete': true // #deleteBtn set true for show the button    
        },
        /* testId */
        'emailtemplate',

        /* viewModel object needed only for activeTogle */
        'theTemplate'
    )
});