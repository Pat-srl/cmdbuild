Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-gatetemplates-tabitems-templates-grid',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        gateTypes: function () {
            CMDBuildUI.util.administration.helper.ModelHelper.getGateTypes();
        }
    },
    stores: {
        gateTypesTore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{gateTypes}'
        }
    }

});