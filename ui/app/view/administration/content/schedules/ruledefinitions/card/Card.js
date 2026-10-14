Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-schedules-ruledefinitions-card',

    requires: [
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardController',
        'CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.CardModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-schedules-ruledefinitions-card',
    viewModel: {
        type: 'view-administration-content-schedules-ruledefinitions-card'
    },
    bubbleEvents: [
        'itemcreated',
        'itemupdated',
        'cancelcreated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        theSchedule: null
    },
    bind: {
        theSchedule: '{theSchedule}',
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    cls: 'administration tab-hidden',

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true,
                    activeToggle: true,
                    delete: true,
                    clone: true
                }, 'schedules', 'theSchedule', [],
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
                }])
        }]
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }, {
        xtype: 'toolbar',
        itemId: 'bottomviewtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.edit|| !actions.view && !showInPopup}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getCloseButton()
    }]
});