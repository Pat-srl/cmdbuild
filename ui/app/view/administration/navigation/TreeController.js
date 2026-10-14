Ext.define('CMDBuildUI.view.administration.navigation.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-navigation-tree',

    control: {
        '#': {
            selectionchange: 'onSelectionChange'
        }
    },

    /**
     * @param {Ext.list.Tree} view
     * @param {Ext.data.TreeModel} record
     * @param {Object} eOpts
     */
    onSelectionChange: function (view, record, eOpts) {
        // redirect to selected

        if (!record || record.get('disabled')) {
            return false; // veto selection
        }

        var node = view.getItem(record);

        var data = record.getData();
        this.expandNodeHierarchy(node);
        var menutype = data.menutype;
        var url;
        if (menutype !== CMDBuildUI.model.menu.MenuItem.types.folder) {
            switch (menutype) {
                case CMDBuildUI.model.menu.MenuItem.types.klass:
                    // expand nodes
                    url = 'administration/classes/' + data.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.lookuptype:
                    url = 'administration/lookup_types/' + data.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.domain:
                    url = 'administration/domains/' + data.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.process:
                    url = 'administration/processes/' + data.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.user:
                    url = 'administration/users';
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.schedule:
                    url = 'administration/schedules/ruledefinitions';
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                    url = 'administration/groupsandpermissions/' + data.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.view:
                // debugger;
                // url = 'administration/views/' + data.objecttype;
                // break;
                case CMDBuildUI.model.administration.MenuItem.types.menu:
                case CMDBuildUI.model.administration.MenuItem.types.setup:
                case CMDBuildUI.model.administration.MenuItem.types.report:
                case CMDBuildUI.model.administration.MenuItem.types.custompage:
                case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
                case CMDBuildUI.model.administration.MenuItem.types.widget:
                case CMDBuildUI.model.administration.MenuItem.types.script:
                case CMDBuildUI.model.administration.MenuItem.types.email:
                case CMDBuildUI.model.administration.MenuItem.types.notification:
                case CMDBuildUI.model.administration.MenuItem.types.emailqueue:
                case CMDBuildUI.model.administration.MenuItem.types.localization:
                case CMDBuildUI.model.administration.MenuItem.types.gis:
                case CMDBuildUI.model.administration.MenuItem.types.bim:
                case CMDBuildUI.model.administration.MenuItem.types.dependencytree:
                case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
                case CMDBuildUI.model.administration.MenuItem.types.menunavigationtree:
                case CMDBuildUI.model.administration.MenuItem.types.task:
                case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
                case CMDBuildUI.model.administration.MenuItem.types.importexport:
                case CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.dashboard:
                case CMDBuildUI.model.administration.MenuItem.types.homepage:
                case CMDBuildUI.model.administration.MenuItem.types.dmsmodel:
                case CMDBuildUI.model.administration.MenuItem.types.dmscategory:
                case CMDBuildUI.model.administration.MenuItem.types.home:
                case CMDBuildUI.model.administration.MenuItem.types.bus:
                case CMDBuildUI.model.administration.MenuItem.types.pluginmanager:
                case CMDBuildUI.model.administration.MenuItem.types.ai:
                    url = data.href;
                    break;
                default:
                    CMDBuildUI.util.Msg.alert('Warning', 'Menu type not implemented!');
            }
        } else {
            //
            switch (data.objecttype.toLowerCase()) {
                case CMDBuildUI.model.administration.MenuItem.types.klass:
                case 'simples':
                case 'standard':
                case CMDBuildUI.model.administration.MenuItem.types.view:
                case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
                case CMDBuildUI.model.administration.MenuItem.types.process:
                case CMDBuildUI.model.administration.MenuItem.types.lookuptype:
                case CMDBuildUI.model.administration.MenuItem.types.domain:
                case CMDBuildUI.model.administration.MenuItem.types.report:
                case CMDBuildUI.model.administration.MenuItem.types.custompage:
                case CMDBuildUI.model.administration.MenuItem.types.customcomponent:
                case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
                case CMDBuildUI.model.administration.MenuItem.types.widget:
                case CMDBuildUI.model.administration.MenuItem.types.script:
                case CMDBuildUI.model.administration.MenuItem.types.email:
                case CMDBuildUI.model.administration.MenuItem.types.task:
                case CMDBuildUI.model.administration.MenuItem.types.gis:
                case CMDBuildUI.model.administration.MenuItem.types.bim:
                case CMDBuildUI.model.administration.MenuItem.types.localization:
                case CMDBuildUI.model.administration.MenuItem.types.menu:
                case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                case CMDBuildUI.model.administration.MenuItem.types.user:
                case CMDBuildUI.model.administration.MenuItem.types.schedule:
                case CMDBuildUI.model.administration.MenuItem.types.setup:
                case CMDBuildUI.model.administration.MenuItem.types.dependencytree:
                case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
                case CMDBuildUI.model.administration.MenuItem.types.importexport:
                case CMDBuildUI.model.administration.MenuItem.types.dashboard:
                case CMDBuildUI.model.administration.MenuItem.types.dmsmodel:
                case CMDBuildUI.model.administration.MenuItem.types.dmscategory:
                case CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate:
                case CMDBuildUI.model.administration.MenuItem.types.bus:
                case CMDBuildUI.model.administration.MenuItem.types.pluginmanager:
                case CMDBuildUI.model.administration.MenuItem.types.homepage:
                case CMDBuildUI.model.administration.MenuItem.types.ai:
                    url = record.get('href');
                    break;
                default:
                    this.expandNodeHierarchy(view.getItem(record));
                    break;
            }
        }
        if (url !== undefined) {
            this.redirectTo(url);
        }
    },

    privates: {
        expandNodeHierarchy: function (node) {
            node.expand();
            if (node.getParentItem()) {
                this.expandNodeHierarchy(node.getParentItem());
            }
        }
    }
});
