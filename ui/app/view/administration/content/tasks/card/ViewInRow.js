Ext.define('CMDBuildUI.view.administration.content.tasks.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper',
        'CMDBuildUI.view.administration.content.tasks.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.tasks.card.ViewInRowModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-tasks-card-viewinrow',
    controller: 'administration-content-tasks-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-tasks-card'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        type: null,
        objectId: null,
        shownInPopup: false,
        theTask: null,
        subType: null
    },
    minHeight: 200,
    items: [

    ],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: true, // #viewBtn set true for show the button
        clone: true, // #cloneBtn set true for show the button
        'delete': true, // #deleteBtn set true for show the button
        activeToggle: false // #enableBtn and #disableBtn set true for show the buttons       
    },

        /* testId */
        'importexporttemplates',

        /* viewModel object needed only for activeTogle */
        'theTask',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [],

        /* add custom tools[] after at the end of the bar*/
        []
    ),

    listeners: {
        afterlayout: function (panel) {
            try {
                panel.unmask();
            } catch (error) {

            }
        }
    },

    initComponent: function () {
        Ext.asap(function () {
            try {
                this.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {

            }
        }, this);

        this.callParent(arguments);
    },
    addStep: function(step, index, content){
        this.add(content);
    }

});