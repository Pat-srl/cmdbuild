Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-schedules-ruledefinitions-card-viewinrow',
    controller: 'administration-content-schedules-ruledefinitions-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-schedules-ruledefinitions-card'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    userCls: 'formmode-view', // this is used for hide label localzation icon in `view` mode

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theSchedule: null
    },

    items: [],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true, // #editBtn set true for show the button
            view: true, // #viewBtn set true for show the button
            clone: true, // #cloneBtn set true for show the button
            'delete': true, // #deleteBtn set true for show the button        
            activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
        },

        /* testId */
        'schedules',

        /* viewModel object needed only for activeTogle */
        'theSchedule',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [{
            xtype: 'tool',
            itemId: 'applyRuleBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('play-circle', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.schedules.applyruletoexistingcards,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.schedules.applyruletoexistingcards'
            },
            cls: 'administration-tool',
            autoEl: {
                'data-testid': 'administration-{0}-applyRuleBtn'
            },
            bind: {
                disabled: '{!theSchedule.active}',
                hidden: '{!actions.view}'
            }
        }],

        /* add custom tools[] after at the end of the bar*/
        []
    )
});