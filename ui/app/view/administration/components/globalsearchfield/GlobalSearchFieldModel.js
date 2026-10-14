Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-globalsearchfield-globalsearchfield',

    formulas: {
        emptyText: function () {
            const view = this.getView();
            let type = view.getObjectType();
            if (type === 'etlgates') {
                type += view.getSubType();
            }
            return CMDBuildUI.locales.Locales.administration.globalsearch.emptyText[type];
        }
    }
});