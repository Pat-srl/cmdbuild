Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-globalsearchfield-globalsearchpanel',

    control: {
        '#': {
            itemclick: 'onItemClick'
        }
    },

    /**
     *
     * @param {Ext.view.View} treeView
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onItemClick: function (treeView, record, item, index, event, eOpts) {
        const view = this.getView();
        const isSubItem = record.get('depth') === 2;
        const objectType = view.getObjectType();
        const subType = view.getSubType();
        const objectTypeName = isSubItem ? record.parentNode.get('name') : record.get('_id');
        let url;
        switch (objectType) {
            case 'domains':
            case 'processes':
            case 'classes':
                url = 'administration/' + objectType + '/' + objectTypeName;
                Ext.getCmp(CMDBuildUI.view.administration.Content.elementId).getViewModel().set(Ext.String.format('activeTabs.{0}', objectType), !isSubItem ? 0 : 1);
                break;
            case 'lookup_types':
                url = 'administration/' + objectType + '/' + CMDBuildUI.util.Utilities.stringToHex(isSubItem ? record.parentNode.get('name') : record.get('name'));
                Ext.getCmp(CMDBuildUI.view.administration.Content.elementId).getViewModel().set(Ext.String.format('activeTabs.{0}', 'lookuptypes'), !isSubItem ? 0 : 1);
                break;
            case 'filters':
                url = 'administration/searchfilters/' + record.get('name');
                break;
            case 'reports':
            case 'dashboards':
                // TODO check for subItem click
                url = 'administration/' + objectType + '/' + objectTypeName;
                break;
            case 'custompages':
                url = 'administration/' + objectType + '/' + subType + '/' + record.get('name');
                break;
            case 'dmsmodels':
                url = 'administration/dmsmodels/' + objectTypeName;
                Ext.getCmp(CMDBuildUI.view.administration.Content.elementId).getViewModel().set(Ext.String.format('activeTabs.{0}', 'dmsmodels'), !isSubItem ? 0 : 1);
                break;
            case 'dmscategories':
                url = 'administration/dmscategories/' + CMDBuildUI.util.Utilities.stringToHex(objectTypeName);
                Ext.getCmp(CMDBuildUI.view.administration.Content.elementId).getViewModel().set(Ext.String.format('activeTabs.{0}', 'dmscategories'), !isSubItem ? 0 : 1);
                break;
            case 'roles':
                url = 'administration/groupsandpermissions/' + record.get('_id');
                Ext.getCmp(CMDBuildUI.view.administration.Content.elementId).getViewModel().set(Ext.String.format('activeTabs.{0}', 'groups'), 0);
                break;
            case 'templates':
                url = 'administration/importexport/datatemplates/' + objectTypeName;
                break;
            case 'views':
                url = 'administration/views/' + record.get('name');
                break;
            case 'busdescriptors':
                url = 'administration/bus/descriptors/' + record.get('name');
                break;
            case 'etlgates':
                const type = isSubItem ? record.parentNode.get('type') : record.get('type');
                url = 'administration/importexport/gatetemplates/' + type + '/' + objectTypeName;
                break;
            default:
                url = 'administration/' + objectType + '/' + objectTypeName;
                break;
        }

        treeView.lookupController().redirectTo(url, true);
        view.destroy();
    }
});