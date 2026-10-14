Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-datatemplates-grid',
    data: {
        theGateTemplate: null,
        search: {
            value: null
        },
        selected: null,
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        isStandalone: false
    },
    formulas: {
        templateTypes: {
            get: function (data) {
                return CMDBuildUI.model.importexports.Template.getTemplateTypes();
            }
        },
        targetTypes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getTargetTypes();
            }
        },
        fileTypes: {
            bind: '{theGateTemplate.type}',
            get: function (type) {
                return CMDBuildUI.model.importexports.Template.getFileTypes(type);
            }
        }
    },
    stores: {
        templateTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{templateTypes}'
        },
        fileTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{fileTypes}'
        }
    }
});
