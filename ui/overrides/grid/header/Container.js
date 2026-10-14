Ext.define('Overrides.grid.header.Container', {
    override: 'Ext.grid.header.Container',

    /**
     * @override
     * 
     * Returns an array of menu items to be placed into the shared menu
     * across all headers in this header container.
     * @return {Array} menuItems
     */
    getMenuItems: function () {
        var me = this,
            menuItems = [],
            hideableColumns = me.enableColumnHide ? me.getColumnMenu(me) : null;

        if (me.sortable) {
            menuItems = [{
                itemId: 'ascItem',
                text: me.sortAscText,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-down', 'solid'),
                handler: me.onSortAscClick,
                scope: me
            }, {
                itemId: 'descItem',
                text: me.sortDescText,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-up', 'solid'),
                handler: me.onSortDescClick,
                scope: me
            }];
        }

        if (hideableColumns && hideableColumns.length) {
            if (me.sortable) {
                menuItems.push({
                    itemId: 'columnItemSeparator',
                    xtype: 'menuseparator'
                });
            }

            menuItems.push({
                itemId: 'columnItem',
                text: me.columnsText,
                iconCls: me.menuColsIcon,
                menu: me.createMenuItems(hideableColumns),
                hideOnClick: false
            });
        }

        return menuItems;
    },

    /**
     * @override
     * 
    // Render our menus to the first enclosing scrolling element so that they scroll with the grid
     * @param {*} menu 
     */
    beforeMenuShow: function (menu) {
        var me = this,
            columnItem = menu.child('#columnItem'),
            hideableColumns, insertPoint;

        // If a change of column structure caused destruction of the column menu item
        // or the main menu was created without the column menu item because it began
        // with no hideable headers. Then create it and its menu now.
        if (!columnItem) {
            hideableColumns = me.enableColumnHide ? me.getColumnMenu(me) : null;

            // Insert after the "Sort Ascending", "Sort Descending" menu items if they are present.
            insertPoint = me.sortable ? 2 : 0;

            if (hideableColumns && hideableColumns.length) {
                menu.insert(insertPoint, [
                    {
                        itemId: 'columnItemSeparator',
                        xtype: 'menuseparator'
                    }, {
                        itemId: 'columnItem',
                        text: me.columnsText,
                        iconCls: me.menuColsIcon,
                        menu: me.createMenuItems(hideableColumns),
                        hideOnClick: false
                    }
                ]);
            }
        }

        me.updateMenuDisabledState(me.menu);
    },

    /**
     *
     * @param {Array} hideableColumns 
     * @returns {Object}
     */
    createMenuItems: function (hideableColumns) {
        var me = this;
        Ext.Array.forEach(hideableColumns, function (item, index, allitems) {
            item.columnIndex = index;
        });
        return {
            dockedItems: [{
                xtype: 'toolbar',
                cls: Ext.baseCSSPrefix + 'order-columns-grid',
                dock: 'top',
                items: [
                    {
                        xtype: 'tbfill'
                    }, {
                        xtype: 'tool',
                        itemId: 'sortAsc',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-amount-up', 'solid'),
                        tooltip: me.sortAscText,
                        pressed: false,
                        callback: function (toolbar, tool, event) {
                            toolbar.up("gridcolumn").orderColumns(toolbar, tool, true);
                        }
                    }, {
                        xtype: 'tool',
                        itemId: 'sortDesc',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-amount-down', 'solid'),
                        tooltip: me.sortDescText,
                        pressed: false,
                        callback: function (toolbar, tool, event) {
                            toolbar.up("gridcolumn").orderColumns(toolbar, tool, false);
                        }
                    }
                ]
            }, {
                xtype: 'toolbar',
                cls: Ext.baseCSSPrefix + 'separator-order-columns-grid',
                dock: 'top',
                items: [{
                    xtype: 'menuseparator',
                    itemId: 'separator'
                }]
            }],
            items: hideableColumns
        }
    },

    /**
     *
     * @param {Ext.panel.Panel} toolbar
     * @param {Ext.panel.Tool} tool
     * @param {Boolean} ascendent
     */
    orderColumns: function (toolbar, tool, ascendent) {
        var menu = toolbar.ownerCt;
        if (tool.pressed) {
            tool.removeCls(Ext.baseCSSPrefix + "column-tool-pressed");
            tool.pressed = false;
            menu.items.sort("columnIndex");
        } else {
            tool.addCls(Ext.baseCSSPrefix + "column-tool-pressed");
            tool.pressed = true;
            menu.items.sort(function (a, b) {
                if (a.getItemId() === "separator" || b.getItemId() === "separator" || a.text === b.text) {
                    return 0;
                } else if (a.text > b.text) {
                    return ascendent ? 1 : -1;
                } else {
                    return ascendent ? -1 : 1;
                }
            });
            var itemId = tool.getItemId() === "sortAsc" ? "#sortDesc" : "#sortAsc",
                otherTool = toolbar.down(itemId);
            otherTool.removeCls(Ext.baseCSSPrefix + "column-tool-pressed");
            otherTool.pressed = false;
        }
        menu.hide();
        menu.show();
    }

});
