Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.ViewController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.ViewModel'
    ],
    alias: 'widget.administration-content-importexport-gatetemplates-card-tabitems-importon-view',
    controller: 'administration-content-importexport-gatetemplates-card-tabitems-importon-view',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-card-tabitems-importon-view'
    },
    bind: {
        userCls: '{formModeCls}'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        // hidden: true,
        bind: {
            // hidden: '{!actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true // #editBtn set true for show the button               
            },

            /* testId */
            'importexportimporton',

            /* viewModel object needed only for activeTogle */
            'theGate',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            []
        )
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }],

    items: [{
        xtype: 'administration-content-importexport-gatetemplates-card-tabitems-importon-tree',
        viewModel: {}
    }]
});