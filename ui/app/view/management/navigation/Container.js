Ext.define('CMDBuildUI.view.management.navigation.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.management.navigation.ContainerController',
        'CMDBuildUI.view.management.navigation.ContainerModel',

        'CMDBuildUI.view.management.navigation.Tree'
    ],

    xtype: 'management-navigation-container',
    controller: 'management-navigation-container',
    viewModel: {
        type: 'management-navigation-container'
    },

    layout: 'container',
    width: 250,
    scrollable: true,
    collapseFirst: false,
    cls: Ext.baseCSSPrefix + 'panel-bold-header',
    ui: 'main-navigation',

    items: [
        {
            xtype: 'management-navigation-tree'
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            itemId: 'navTopBar',
            ui: 'navigation-search',
            items: [
                {
                    xtype: 'groupedcombo',
                    itemId: 'navSearchField',
                    flex: 1,

                    displayField: 'text',
                    hideLabel: true,
                    hideTrigger: true,
                    anyMatch: true,
                    queryMode: 'local',
                    queryDelay: 250,
                    cls: Ext.baseCSSPrefix + 'navigation-search-input',

                    emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
                    },

                    listConfig: {
                        emptyText: CMDBuildUI.locales.Locales.errors.notfound,
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.errors.notfound'
                        }
                    },

                    bind: {
                        store: '{menusearch}'
                    }
                }
            ]
        },
        {
            xtype: 'management-chat-conversationslist',
            dock: 'bottom'
        }
    ],

    autoEl: {
        'data-testid': 'management-navigation-container'
    },

    /**
     * Open CMDBuild resource by node.
     *
     * @param {CMDBuildUI.model.menu.MenuItem} node
     */
    openResourceByNode: function (node) {
        const menutype = node.get('menutype');
        var navtree = false;
        var url;

        switch (menutype) {
            case CMDBuildUI.model.menu.MenuItem.types.folder:
            case CMDBuildUI.model.menu.MenuItem.types.favourites:
                // do nothing
                break;
            case CMDBuildUI.model.menu.MenuItem.types.klass:
                url = CMDBuildUI.util.Navigation.getClassBaseUrl(node.get('objecttypename'), null, null, true);
                break;
            case CMDBuildUI.model.menu.MenuItem.types.process:
                url = CMDBuildUI.util.Navigation.getProcessBaseUrl(node.get('objecttypename'), null, null, null, true);
                break;
            case CMDBuildUI.model.menu.MenuItem.types.custompage:
                url = 'custompages/' + node.get('objecttypename');
                break;
            case CMDBuildUI.model.menu.MenuItem.types.report:
                url = 'reports/' + node.get('objecttypename');
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                url = 'reports/' + node.get('objecttypename') + '/pdf';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                url = 'reports/' + node.get('objecttypename') + '/csv';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                url = 'reports/' + node.get('objecttypename') + '/odt';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                url = 'reports/' + node.get('objecttypename') + '/rtf';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.view:
                url = 'views/' + node.get('objecttypename') + '/items';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                url = 'dashboards/' + node.get('objecttypename');
                break;
            case CMDBuildUI.model.menu.MenuItem.types.homepage:
                url = 'homepage/';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.homepage:
                url = 'homepages/' + node.get('objecttypename');
                break;
            case CMDBuildUI.model.menu.MenuItem.types.calendar:
                url = 'events';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.dependencytree:
                url = 'dependencytree/' + node.get('objecttypename');
                break;
            case CMDBuildUI.model.menu.MenuItem.types.navtree:
            case CMDBuildUI.model.menu.MenuItem.types.navtreeitem:
                const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
                if (detailWindow && detailWindow.navTreeNode) {
                    const selectedNode = detailWindow.navTreeNode;
                    if ((selectedNode.get('_objectid') || selectedNode.getId()) != node.get('_objectid')) {
                        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
                    }
                }
                url = 'navigation/' + node.get('objecttypename');
                navtree = true;
                break;
            default:
                CMDBuildUI.util.Msg.alert('Warning', 'Menu type not implemented!');
        }

        if (url !== undefined) {
            CMDBuildUI.util.Navigation.clearCurrentContext();
            CMDBuildUI.util.Navigation.updateCurrentRowTab();
            CMDBuildUI.util.Utilities.redirectTo(url, true);
            if (navtree) {
                // Used to see navigation page correctly when change url
                Ext.defer(function () {
                    Ext.GlobalEvents.fireEventArgs('menunavtreeitemchanged', [node]);
                }, 100);
            }
        }
    }
});
