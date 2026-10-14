
Ext.define('CMDBuildUI.view.fields.bufferedcombo.SelectionPopup', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.fields.bufferedcombo.SelectionPopupController',
        'CMDBuildUI.view.fields.bufferedcombo.SelectionPopupModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.AddButtonMixin'
    ],

    alias: 'widget.fields-bufferedcombo-selectionpopup',
    controller: 'fields-bufferedcombo-selectionpopup',
    viewModel: {
        type: 'fields-bufferedcombo-selectionpopup'
    },

    bind: {
        store: '{records}',
        selection: '{selection}'
    },

    selModel: {
        type: 'checkboxmodel',
        mode: 'SINGLE'
    },

    config: {
        defaultSearchFilter: ""
    },

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.classes.cards.addcard,
        reference: 'addcardbtn',
        itemId: 'addcardbtn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
        ui: 'management-primary',
        autoEl: {
            'data-testid': 'selection-popup-addcardbtn'
        },
        bind: {
            text: '{addbtn.text}',
            disabled: '{addbtn.disabled}',
            hidden: '{addbtn.hidden}'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.classes.cards.searchtext,
        reference: 'searchtextinput',
        itemId: 'searchtextinput',
        cls: 'management-input',
        localized: {
            text: 'CMDBuildUI.locales.Locales.classes.cards.searchtext'
        },
        autoEl: {
            'data-testid': 'selection-popup-searchtextinput'
        },
        bind: {
            value: '{searchvalue}'
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
        xtype: 'tbfill'
    }, CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("records")],

    fbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'savebtn',
        ui: 'management-primary-small',
        autoEl: {
            'data-testid': 'selection-popup-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        bind: {
            disabled: '{saveBtnDisabled}'
        }
    }],

    layout: 'fit',
    forceFit: true,

    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass
});
