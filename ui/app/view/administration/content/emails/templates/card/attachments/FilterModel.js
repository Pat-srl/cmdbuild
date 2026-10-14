Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.FilterModel', {
    extend: 'CMDBuildUI.view.filters.attachments.PanelModel',
    alias: 'viewmodel.administration-content-emails-templates-card-attachments-filter',

    formulas: {
        visibletextfield: {
            get: function () {
                return false;
            }
        }
    },


    stores: {
        dmscategories: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{catetoriesvalues}',
            grouper: {
                property: '_type'
            }
        }
    }
});