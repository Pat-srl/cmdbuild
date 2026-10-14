Ext.define(
    'CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-localizations-localization-tabitems-translationsmenutreepanel',

        control: {
            '#': {
                beforecelldblclick: 'onBeforeDblClick'
            },
            '#saveBtn': {
                click: 'onSaveBtnClick'
            },
            '#cancelBtn': {
                click: 'onCancelBtnClick'
            }
        },

        onBeforeDblClick: function () {
            return false;
        },

        onStoreLoad: function (store, records) {
            var me = this;
            var emptyTreeStore = this.getView().getStore();
            var view = me.getView();
            var vm = me.getViewModel();
            var menuTreeStore = vm.getStore('completeTranslationsStore');

            menuTreeStore.getProxy().setUrl('/menu?detailed=true');
            emptyTreeStore.getRoot().removeAll();
            menuTreeStore.load({
                callback: function (data, operation) {
                    vm.set('allMenusStore', menuTreeStore);
                    me.generateMenu(view, emptyTreeStore, menuTreeStore);
                }
            });
        },

        generateMenu: function (grid, emptyTreeStore) {
            const me = this;
            const vm = grid.lookupViewModel();
            const menuTreeStore = vm.getStore('completeTranslationsStore');
            const menuTrees = menuTreeStore.getRange();

            menuTrees.forEach(function (menuTree) {
                menuTree.set('menuType', 'folder');
                menuTree.set('text', menuTree.get('group'));
                const resultArray = me.getRawTree(
                    menuTree.get('group'),
                    menuTree.get('device'),
                    menuTree.getData(),
                    menuTree.get('type')
                );
                emptyTreeStore.getRoot().appendChild(resultArray);
            });

            const languagesStore = vm.get('languages');
            if (languagesStore.isLoaded()) {
                const languageRecords = languagesStore.getRange();
                const columns = [
                    {
                        xtype: 'treecolumn',
                        dataIndex: 'text',
                        text: CMDBuildUI.locales.Locales.administration.localizations.element,
                        align: 'left',
                        locked: true,
                        minWidth: 250,
                        width: 250,
                        renderer: function (value, metadata, record, rowInde, colIndex, store, view) {
                            if (record.get('parentId') === 'root') {
                                if (record.get('objectDescription') === '_default') {
                                    return CMDBuildUI.locales.Locales.administration.common.strings.default;
                                } else {
                                    const group = Ext.getStore('groups.Groups').findRecord(
                                        'name',
                                        record.get('objectDescription')
                                    );
                                    if (group) {
                                        return group.get('description');
                                    } else {
                                        return record.get('objectDescription');
                                    }
                                }
                            }
                            return value;
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.common.labels.code,
                        dataIndex: 'code',
                        align: 'left',
                        locked: true,
                        hidden: true,
                        minWidth: 200,
                        width: 200
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.localizations.defaulttranslation,
                        dataIndex: 'default',
                        align: 'left',
                        locked: true,
                        minWidth: 200,
                        width: 200
                    }
                ];

                languageRecords.forEach(function (record) {
                    const lang = record.get('description');
                    const code = record.get('code');
                    const flag =
                        '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' +
                        code +
                        '.png" />';

                    const column = {
                        text: flag + lang,
                        dataIndex: code,
                        align: 'left',
                        locked: false
                    };

                    if ((grid.getWidth() - 450) / languageRecords.length < 150) {
                        column.minWidth = 150;
                        column.maxWidth = 150;
                    } else {
                        column.flex = 1;
                    }

                    columns.push(column);
                });
                grid.reconfigure(grid.getStore(), columns);
            }
        },

        buildTranslationsRow: function (translationsQuery, _item, languages) {
            var rows = translationsQuery.getRange();
            rows.forEach(function (row) {
                var languges = row.get('values');
                _item.default = row.get('default');
                _item.code = row.get('code');
                Ext.Object.each(languges, function (key, value, myself) {
                    _item[key] = value;
                });
            });
        },

        getRawTree: function (group, device, childNodes, type) {
            var me = this;
            var output = [];
            var translationsStore = this.getViewModel().get('localizationsStore');
            var languages = this.getViewModel().get('languages').getRange();
            var languageRecords = [];
            languages.forEach(function (language) {
                languageRecords.push(language.get('code'));
            });

            if (childNodes.objectDescription === 'ROOT') {
                childNodes.text =
                    childNodes.type === CMDBuildUI.model.menu.Menu.types.gismenu
                        ? CMDBuildUI.locales.Locales.administration.navigation.gismenu
                        : childNodes.group;
            }
            if (childNodes.group) {
                var _item = {
                    children: this.getRawTree(group, device, childNodes.children, type),
                    menuType: 'folder',
                    objectDescription: childNodes.text,
                    _id: childNodes._id,
                    expanded: true,
                    text: childNodes.text
                };
                output.push(_item);
            } else {
                childNodes.forEach(function (item, index) {
                    var _item = {
                        children: [],
                        menuType: item.menuType,
                        objectDescription: item.objectDescription || item._actualDescription,
                        _id: item._id,
                        expanded: true
                    };

                    if (item.children.length) {
                        _item.children = me.getRawTree(group, device, item.children, type);
                    } else {
                        _item.children = undefined;
                        _item.leaf = true;
                    }
                    var row = {};
                    row.element = _item._id;
                    row.type = CMDBuildUI.locales.Locales.administration.localizations.menuitem;
                    var key;
                    if (type !== CMDBuildUI.model.menu.Menu.types.gismenu) {
                        key =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(
                                group,
                                device,
                                _item._id
                            );
                    } else {
                        key =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLayerMenuItemDescription(
                                CMDBuildUI.model.menu.Menu.types.gismenu,
                                _item._id
                            );
                    }
                    var translationsQuery = translationsStore.query('code', key, false, false, true);
                    if (translationsQuery.length) {
                        me.buildTranslationsRow(translationsQuery, _item, languageRecords);
                    }
                    if (!Ext.isEmpty(_item.default)) {
                        output.push(_item);
                    }
                });
            }

            return output;
        },

        onCancelBtnClick: function (button, e, eOpts) {
            var me = this;
            var grid = me.getView();
            var vm = me.getViewModel();

            vm.get('localizationsStore').load(function () {
                var clearStore = Ext.create('Ext.data.TreeStore', {
                    model: 'CMDBuildUI.model.menu.MenuItem',
                    storeId: 'menuTreeStore',
                    reference: 'menuTreeStore',
                    root: {
                        text: 'Root',
                        expanded: true
                    },
                    proxy: {
                        type: 'memory'
                    },
                    autoLoad: true
                });

                vm.set('actions.view', true);
                grid.getColumns().forEach(function (column) {
                    if (!column.locked) {
                        column.setEditor(false);
                    }
                });
                vm.set('actions.view', true);
                vm.set('actions.edit', false);
                vm.getParent().toggleEnableTabs();
                grid.setStore(clearStore);
                me.onStoreLoad();
            });
        },
        onBeforCellEdit: function (editor, context, eOpts) {
            if (context.record.get('parentId') === 'root') {
                return false;
            }
            return true;
        },
        editedCell: function (editor, context, eOpts) {
            var me = this;
            var field = context.field;
            var modvalue = context.value;
            var store = me.getViewModel().get('localizationsStore');
            var key = context.record.get('code');

            var res = store.findRecord('code', key);
            if (res && res.get('values')[field] !== modvalue) {
                res.crudState = 'U';
                res.dirty = true;
                res.get('values')[field] = modvalue;
                res.set(field, modvalue);
            }
        },

        onSaveBtnClick: function (button, e, eOpts) {
            var grid = this.getView();
            var vm = this.getViewModel();
            vm.set('actions.view', true);
            vm.set('actions.edit', false);
            vm.getParent().toggleEnableTabs();
            this.getView()
                .getColumns()
                .forEach(function (column) {
                    if (!column.locked) {
                        column.setEditor(false);
                    }
                });

            var modifiedRecords = vm.get('localizationsStore').getModifiedRecords();
            var requestsCount = 0;

            modifiedRecords.forEach(function (record) {
                var data = {};
                var languges = record.get('values');
                Ext.Object.each(languges, function (key, value, myself) {
                    data[key] = value;
                });
                var code = record.get('code');
                requestsCount++;
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, code),
                    method: 'PUT',
                    jsonData: data,
                    callback: function () {
                        requestsCount--;
                        if (requestsCount === 0) {
                            grid.getView().refresh();
                        }
                    }
                });
            });
        }
    }
);
