Ext.define('CMDBuildUI.view.administration.components.localsearchfield.LocalSearchField', {
    extend: 'Ext.form.field.Text',

    requires: [
        'CMDBuildUI.view.administration.components.localsearchfield.LocalSearchFieldController'
    ],
    alias: 'widget.localsearchfield',
    controller: 'administration-components-localsearchfield-localsearchfield',
    name: 'search',
    width: 250,
    emptyText: CMDBuildUI.locales.Locales.administration.home.searchingrid,
    localized: {
        emptyText: 'CMDBuildUI.locales.Locales.administration.home.searchingrid'
    },
    cls: 'administration-input',
    reference: 'searchtext',
    itemId: 'searchtext',
    bind: {
        hidden: '{!canFilter}'
    },
    config: {
        /**
         * @cfg {string} Mandatory es: #my-grid
         */
        gridItemId: null
    },
   
    triggers: {
        // local search fields not need search submit trigger

        // search: {
        //     cls: Ext.baseCSSPrefix + 'form-search-trigger',
        //     itemId: 'searchsubmit',                        
        //     autoEl: {
        //         'data-testid': 'administration-grid-localsearchfield-input-search-trigger'
        //     }
        // },
        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
    },
    autoEl: {
        'data-testid': 'administration-grid-localsearchfield-input'
    },
    initComponent: function () {
        this.callParent(arguments);
        if (!this.getGridItemId()) {
            CMDBuildUI.util.Logger.log("widget.localsearchfield: gridItemId config is not set. es: '#my-grid'", CMDBuildUI.util.Logger.levels.debug);
        }
    }
});