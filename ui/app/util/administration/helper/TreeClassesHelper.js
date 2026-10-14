Ext.define('CMDBuildUI.util.administration.helper.TreeClassesHelper', {
    singleton: true,
    mixins: ['Ext.mixin.Observable'],

    initialize: function (callback) {
        this.onReloadAdminstrationMenu(callback);
    },

    /**
     *
     */
    getChildren: function (callback) {
        var me = this;

        var childrens = [];

        Ext.Promise.all([
            me.appendClassesAdminMenu().then(
                function (classes) {
                    childrens[0] = classes;
                    // store.sort('index');
                },
                function () {
                    CMDBuildUI.util.Msg.alert('Error', 'Menu Classes NOT LOADED!');
                }
            ),
            me.appendProcessesAdminMenu().then(
                function (processes) {
                    childrens[1] = processes;
                    // store.sort('index');
                },
                function () {
                    CMDBuildUI.util.Msg.alert('Error', 'Menu Processes NOT LOADED!');
                }
            )
        ]).then(function () {});
    },

    /**
     * 0 - Create classes tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendClasses: function (withCheckbox, activeItems, withIcon, expanded, disabled) {
        var deferred = new Ext.Deferred();
        var classesStore = Ext.create('Ext.data.ChainedStore', {
            source: 'classes.Classes'
        });

        classesStore.source.load({
            params: {
                detailed: true,
                active: false
            },
            callback: function (items, operation, success) {
                var simples = {
                    menutype: 'folder',
                    objecttype: 'Simples',
                    iconCls: '',
                    text: CMDBuildUI.locales.Locales.administration.navigation.simples,
                    leaf: false,
                    expanded: expanded,
                    index: 1,
                    children: this.getRecordsAsSubmenu(
                        withCheckbox,
                        activeItems,
                        withIcon,
                        expanded,
                        items
                            .filter(function (rec, id) {
                                return rec.get('type') === 'simple';
                            })
                            .sort(function (a, b) {
                                var nameA = a.get('description').toUpperCase(); // ignore upper and lowercase
                                var nameB = b.get('description').toUpperCase(); // ignore upper and lowercase
                                if (nameA < nameB) {
                                    return -1;
                                }
                                if (nameA > nameB) {
                                    return 1;
                                }

                                // if description are same
                                return 0;
                            }),
                        CMDBuildUI.model.menu.MenuItem.types.klass,
                        '',
                        disabled
                    )
                };

                var standard = {
                    menutype: 'folder',
                    objecttype: 'Standard',
                    iconCls: '',
                    text: CMDBuildUI.locales.Locales.administration.navigation.standard,
                    leaf: false,
                    checke: false,
                    expanded: expanded,
                    index: 0,
                    children: this.getRecordsAsSubmenu(
                        withCheckbox,
                        activeItems,
                        withIcon,
                        expanded,
                        items
                            .filter(function (rec, id) {
                                return rec.get('prototype') === true || rec.get('parent').length > 0;
                            })
                            .sort(this.sortByDescription),
                        CMDBuildUI.model.menu.MenuItem.types.klass,
                        CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                        disabled
                    )
                };

                //TODO: check configuration
                var classesMenu = {
                    menutype: 'folder',
                    objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                    iconCls: '',
                    index: 0,
                    expanded: expanded,
                    text: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    leaf: false,
                    children: [standard, simples]
                };
                deferred.resolve(classesMenu);
            },
            scope: this
        });
        return deferred.promise;
    },
    /**
     * 1 - Create Process tree
     */
    appendProcesses: function (withCheckbox, activeItems, withIcon, expanded, disabled) {
        var deferred = new Ext.Deferred();

        var processesStore = Ext.create('Ext.data.ChainedStore', {
            source: 'processes.Processes'
        });
        processesStore.source.load({
            params: {
                active: false
            },
            callback: function (items, operation, success) {
                var processesMenu = {
                    menutype: 'folder',
                    iconCls: '',
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.process,
                    text: CMDBuildUI.locales.Locales.administration.navigation.processes,
                    leaf: false,
                    expanded: expanded,
                    index: 1,
                    children: this.getRecordsAsSubmenu(
                        withCheckbox,
                        activeItems,
                        withIcon,
                        expanded,
                        items
                            .filter(function (rec, id) {
                                return rec.get('prototype') === true || rec.get('parent').length > 0;
                            })
                            .sort(this.sortByDescription),
                        CMDBuildUI.model.menu.MenuItem.types.process,
                        CMDBuildUI.util.helper.ModelHelper.systemClasses.process,
                        disabled
                    )
                };
                deferred.resolve(processesMenu);
            },
            scope: this
        });
        return deferred.promise;
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     *
     * @returns {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsList: function (records, menutype) {
        var output = [];

        for (var i = 0; i < records.length; i++) {
            var record = records[i].getData();
            var menuitem = {
                menutype: menutype,
                index: i,
                objectid: record._id,
                text: record.description,
                leaf: true
            };
            output.push(menuitem);
        }
        return output;
    },

    privates: {
        /**
         * @private
         */
        sortByDescription: function (a, b) {
            if (a.get && b.get) {
                var nameA = a.get('description').toUpperCase(); // ignore upper and lowercase
                var nameB = b.get('description').toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                // if descriptions are same
                return 0;
            }
        },

        /**
         * Return the json definition for records as tree.
         * @param {Boolean} withCheckbox
         * @param {Object} activeItems
         * @param {Boolean} withIcon
         * @param {Boolean} expanded
         * @param {Ext.data.Model[]} records
         * @param {String} menutype
         * @param {String} parentname
         *
         * @return {Object[]} A list of CMDBuild.model.MenuItem definitions.
         */
        getRecordsAsSubmenu: function (
            withCheckbox,
            activeItems,
            withIcon,
            expanded,
            records,
            menutype,
            parentname,
            disabled
        ) {
            const output = [];
            const me = this;
            const frecords = Ext.Array.filter(records, function (item) {
                return item.getData().hasOwnProperty('parent') && item.getData().parent === parentname;
            });

            for (let i = 0; i < frecords.length; i++) {
                const record = frecords[i].getData();
                const menuitem = {
                    menutype: menutype,
                    index: i,
                    checked: withCheckbox ? (activeItems[record.name] ? true : false) : false,
                    bind: {
                        readOnly: '{actions.view}'
                    },
                    disabled: disabled && disabled.indexOf(record.name) > -1 ? true : false,
                    objecttype: record.name,
                    text: record.description,
                    leaf: true
                };

                switch (menutype) {
                    case CMDBuildUI.model.administration.MenuItem.types.klass:
                        //menuitem.href = 'administration/classes/' + menuitem.objecttype;
                        menuitem.iconCls = withIcon
                            ? CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular')
                            : null;
                        if (record.prototype) {
                            menuitem.iconCls = withIcon ? 'cmdbuildicon-classparent' : null;
                            menuitem.expanded = expanded;
                            menuitem.leaf = false;
                            menuitem.children = me
                                .getRecordsAsSubmenu(
                                    withCheckbox,
                                    activeItems,
                                    withIcon,
                                    expanded,
                                    records,
                                    menutype,
                                    record.name,
                                    disabled
                                )
                                .sort(this.sortByDescription);
                        }
                        break;

                    case CMDBuildUI.model.administration.MenuItem.types.process:
                        menuitem.iconCls = withIcon
                            ? CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid')
                            : null;
                        if (record.prototype) {
                            menuitem.leaf = false;
                            menuitem.expanded = expanded;
                            menuitem.iconCls = withIcon
                                ? CMDBuildUI.util.helper.IconHelper.getIconId('cogs', 'solid')
                                : null;
                            menuitem.children = me
                                .getRecordsAsSubmenu(
                                    withCheckbox,
                                    activeItems,
                                    withIcon,
                                    expanded,
                                    records,
                                    menutype,
                                    record.name,
                                    disabled
                                )
                                .sort(this.sortByDescription);
                        }
                        break;
                }

                output.push(menuitem);
            }
            return output;
        }
    }
});
