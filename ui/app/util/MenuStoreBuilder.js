/**
 * @file CMDBuildUI.util.MenuStoreBuilder
 * @module CMDBuildUI.util.MenuStoreBuilder
 * @author PAT srl
 * @access private
 */
Ext.define('CMDBuildUI.util.MenuStoreBuilder', {
    singleton: true,

    allItems: {},

    initialize: function (callback) {
        const store = Ext.getStore('menu.Menu');
        this.allItems = {};
        const children = [];

        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get('rolePrivileges');

        if (privileges.homepage_access) {
            const homepageConfig = this.getHomepagesMenu();
            if (homepageConfig) {
                const homepageMenuItem = Ext.create('CMDBuildUI.model.menu.MenuItem', homepageConfig);
                store.getRoot().insertBefore(homepageMenuItem, store.getRoot().firstChild);
            }
        }

        // add scheduler to menu
        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) &&
            (privileges.calendar_access || privileges.calendar_event_create)
        ) {
            const calendarMenuItem = Ext.create('CMDBuildUI.model.menu.MenuItem', {
                menutype: 'calendar',
                objecttypename: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
                objectdescription_translation: CMDBuildUI.locales.Locales.calendar.scheduler,
                objectdescription: CMDBuildUI.locales.Locales.calendar.scheduler,
                leaf: true
            });

            store.getRoot().appendChild(calendarMenuItem);
        }

        if (privileges.class_access) {
            const classes = this.getClassesMenu();
            if (classes) {
                children.push(classes);
            }
        }

        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled) &&
            privileges.process_access
        ) {
            const processes = this.getProcessesMenu();
            if (processes) {
                children.push(processes);
            }
        }

        if (privileges.report_access) {
            const reports = this.getReportsMenu();
            if (reports) {
                children.push(reports);
            }
        }

        if (privileges.dashboard_access) {
            const dashboards = this.getDashboardsMenu();
            if (dashboards) {
                children.push(dashboards);
            }
        }

        // the privileges check is moved inside the function
        // because if uer has not access to views it can view only
        // its views (shared = false)
        const views = this.getViewsMenu(privileges.dataview_access);
        if (views) {
            children.push(views);
        }

        if (privileges.custompages_access) {
            const custompages = this.getCustomPagesMenu();
            if (custompages) {
                children.push(custompages);
            }
        }

        if (privileges.dependencytree_access) {
            const dependencytrees = this.getDependencyTreesMenu();
            if (dependencytrees) {
                children.push(dependencytrees);
            }
        }

        if (children.length && store.getRoot()) {
            this.allItems = this.getAllItemsNodeDef(children, store.getData().length);
            store.getRoot().appendChild(this.allItems);
        }
    },

    /**
     * classes list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getClassesMenu: function () {
        var classesMenu;
        var store = Ext.getStore('classes.Classes');
        var itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        var items = itemsCollection.getRange();
        // create standard classes menu
        var standard = {
            menutype: 'folder',
            objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
            objectdescription: 'Standard',
            objectdescription_translation: CMDBuildUI.locales.Locales.classes.standard,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.klass,
            leaf: false,
            children: this.getRecordsAsSubmenu(
                items,
                CMDBuildUI.model.menu.MenuItem.types.klass,
                CMDBuildUI.util.helper.ModelHelper.systemClasses.class
            )
        };

        var simpleclassesCollection = store.query('type', CMDBuildUI.model.classes.Class.classtypes.simple);
        // sort by translated description
        simpleclassesCollection.sort('_description_translation', 'ASC');
        var simpleclasses = simpleclassesCollection.getRange();
        // create simple classes menu
        if (simpleclasses.length) {
            var chidren = [];
            var simple = {
                menutype: 'folder',
                objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                objectdescription: 'Simple',
                objectdescription_translation: CMDBuildUI.locales.Locales.classes.simple,
                leaf: false,
                children: this.getRecordsAsList(simpleclasses, CMDBuildUI.model.menu.MenuItem.types.klass)
            };

            // append standard classes tree
            if (!Ext.isEmpty(standard.children)) {
                chidren.push(standard);
            }
            // append simple classes tree
            if (!Ext.isEmpty(simple.children)) {
                chidren.push(simple);
            }

            // create class menu
            classesMenu = {
                menutype: 'folder',
                objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                objectdescription: 'Classes',
                objectdescription_translation: CMDBuildUI.locales.Locales.menu.classes,
                leaf: false,
                children: chidren
            };
        } else {
            // create class menu
            classesMenu = Ext.apply(standard, {
                objectdescription: 'Classes',
                objectdescription_translation: CMDBuildUI.locales.Locales.menu.classes
            });
        }

        // append menu item to the store if has children
        if (!Ext.isEmpty(classesMenu.children)) {
            return classesMenu;
        }

        return;
    },

    /**
     * process list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getProcessesMenu: function () {
        var store = Ext.getStore('processes.Processes');

        // get active items
        var itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        var items = itemsCollection.getRange();

        // return menu item if there are active processes
        if (!Ext.isEmpty(items)) {
            return this.getAllProcessesNodeDef(
                this.getRecordsAsSubmenu(
                    items,
                    CMDBuildUI.model.menu.MenuItem.types.process,
                    CMDBuildUI.util.helper.ModelHelper.systemClasses.process
                )
            );
        }
    },

    /**
     * report list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getReportsMenu: function () {
        var store = Ext.getStore('reports.Reports');

        // get active items
        var itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        var items = itemsCollection.getRange();

        // return menu item if there are active reports
        if (!Ext.isEmpty(items)) {
            return this.getAllReportsNodeDef(this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.report));
        }
    },

    /**
     * dashboard list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getDashboardsMenu: function () {
        var store = Ext.getStore('dashboards.Dashboards');

        // get active items
        var itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        var items = itemsCollection.getRange();

        // return menu item if there are active dashboards
        if (!Ext.isEmpty(items)) {
            return this.getAllDashboardsNodeDef(
                this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.dashboard)
            );
        }
    },

    /**
     * current user homepage menu item
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getHomepagesMenu: function () {
        if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.homepage.enabled)) {
            return;
        }

        var record = CMDBuildUI.util.Stores.currentHomepage;

        if (!record) {
            return;
        }

        return {
            menutype: CMDBuildUI.model.menu.MenuItem.types.homepage,
            objecttypename: CMDBuildUI.model.menu.MenuItem.types.homepage,
            objectdescription: CMDBuildUI.locales.Locales.menu.homepage,
            leaf: true
        };
    },

    /**
     * views list menu
     * @param {Boolean} dataviewAccess
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getViewsMenu: function (dataviewAccess) {
        var store = Ext.getStore('views.Views');

        // get active items
        var items;
        if (dataviewAccess) {
            items = store.query('active', true);
        } else {
            items = store.queryBy(function (r) {
                return r.get('active') && !r.get('shared');
            });
        }
        // sort by translated description
        items.sort('_description_translation', 'ASC');

        items = items.getRange();

        // return menu item if there are active views
        if (!Ext.isEmpty(items)) {
            return this.getAllViewsNodeDef(this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.view));
        }
    },

    /**
     * custompage list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getCustomPagesMenu: function () {
        var store = Ext.getStore('custompages.CustomPages');

        // get active items
        var itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        var items = itemsCollection.getRange();

        // return menu item if there are active custom pages
        if (!Ext.isEmpty(items)) {
            return this.getAllCustomPagesNodeDef(
                this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.custompage)
            );
        }
    },

    /**
     * Dependencytree list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getDependencyTreesMenu: function () {
        const store = Ext.getStore('dependencyTrees.DependencyTrees');

        // get active items
        const itemsCollection = store.query('active', true);
        // sort by translated description
        itemsCollection.sort('_description_translation', 'ASC');
        const items = itemsCollection.getRange();

        // return menu item if there are active custom pages
        if (!Ext.isEmpty(items)) {
            return this.getAllDependencyTreesNodeDef(
                this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.dependencytree)
            );
        }
    },

    /**
     *
     * @param {*} callback
     */
    onMenuStoreReady: function (callback) {
        Ext.callback(callback);
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * @param {String} parentmenu The name of the parent item.
     *
     * @return {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsSubmenu: function (records, menutype, parentname) {
        var output = [];
        var me = this;

        var frecords = Ext.Array.filter(records, function (item) {
            return item.getData().parent && item.getData().parent === parentname;
        });

        frecords.forEach(function (record, i) {
            var menuitem = {
                menutype: menutype,
                index: i,
                objecttypename: record.getObjectTypeForMenu(),
                objectdescription: record.getTranslatedDescription(),
                leaf: true
            };
            if (record.get('prototype')) {
                menuitem.leaf = false;
                menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.get('name'));
            }
            output.push(menuitem);
        });
        return output;
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     *
     * @return {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsList: function (records, menutype) {
        var output = [];

        records.forEach(function (record, i) {
            var menuitem = {
                menutype: menutype,
                index: i,
                objecttypename: record.getObjectTypeForMenu(),
                objectdescription: record.getTranslatedDescription(),
                leaf: true
            };
            output.push(menuitem);
        });
        return output;
    },

    /**
     * all classes list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getAllItemsMenu: function () {
        this.initialize();
        this.allItems.expanded = true;
        return this.allItems;
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @param {Number} index
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllItemsNodeDef: function (children, index) {
        return {
            menutype: 'folder',
            objectdescription: 'All items',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.allitems,
            allitemsfolder: 'root',
            leaf: false,
            index: index,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllProcessesNodeDef: function (children) {
        return {
            menutype: 'folder',
            objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.process,
            objectdescription: 'Processes',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.processes,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.process,
            leaf: false,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllReportsNodeDef: function (children) {
        return {
            menutype: 'folder',
            objectdescription: 'Reports',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.reports,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.report,
            leaf: false,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllDashboardsNodeDef: function (children) {
        return {
            menutype: 'folder',
            objectdescription: 'Dashboards',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.dashboards,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.dashboard,
            leaf: false,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllViewsNodeDef: function (children) {
        return {
            menutype: 'folder',
            objectdescription: 'Views',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.views,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.view,
            leaf: false,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllCustomPagesNodeDef: function (children) {
        return {
            menutype: 'folder',
            objectdescription: 'Custom pages',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.custompages,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.custompage,
            leaf: false,
            children: children
        };
    },

    /**
     * @param {CMDBuildUI.model.menu.MenuItem[]} children
     * @return {Object} CMDBuildUI.model.menu.MenuItem definition
     */
    getAllDependencyTreesNodeDef: function (children) {
        return {
            menutype: 'folder',
            objectdescription: 'Dependency trees',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.dependencytrees,
            allitemsfolder: CMDBuildUI.model.menu.MenuItem.types.dependencytree,
            leaf: false,
            children: children
        };
    }
});
