Ext.define('CMDBuildUI.view.thematisms.Launcher', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.thematisms.LauncherController', 'CMDBuildUI.view.thematisms.LauncherModel'],

    alias: 'widget.thematisms-launcher',
    controller: 'thematisms-launcher',
    viewModel: {
        type: 'thematisms-launcher'
    },

    layout: 'hbox',
    cls: 'x-filters-launcher', //TODO: create class for thematism

    autoEl: {
        'data-testid': 'filters-launcher' //TODO: create class for thematism
    },

    items: [
        {
            itemId: 'mainbtnThematism',
            reference: 'mainbtn',
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('paint-brush', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.common.grid.thematism
        },
        {
            xtype: 'button',
            ui: 'noui',
            itemId: 'thematismdesc',
            bind: {
                html: '{appliedthematism.name}',
                hidden: '{!showClearBtn}'
            }
        },
        {
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.thematism.clearThematism,
            itemId: 'clearthematismtool',
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.thematism.clearThematism'
            },
            bind: {
                hidden: '{!showClearBtn}'
            },
            autoEl: {
                'data-testid': 'filters-launcher-clearfilter' //TODO: create class for thematism
            }
        },
        {
            xtype: 'tbspacer',
            hidden: true,
            bind: {
                hidden: '{!showClearBtn}'
            }
        }
    ],

    clearThematism: function (skipReload) {
        var vm = this.lookupViewModel();

        //TODO: remove the thematism in the map
        vm.set('appliedthematism.id', null);
        vm.set('appliedthematism.name', null);
    },

    /**
     * @
     */
    getMenu: function () {
        var me = this;
        var vm = this.lookupViewModel();
        if (!me.menu) {
            me.menu = new Ext.panel.Panel({
                baseCls: Ext.baseCSSPrefix + 'boundlist',
                cls: Ext.baseCSSPrefix + 'filtermenu',
                floating: true,
                alwaysOnTop: true,
                shrinkWrap: 2,
                manageHeight: false,
                minWidth: 300,
                shadow: false,
                focusable: true,
                resizable: false,
                draggable: false,
                viewModel: {},
                layout: 'container',
                tools: [
                    {
                        type: 'plus',
                        tooltip: CMDBuildUI.locales.Locales.thematism.addThematism,
                        handler: function () {
                            me.getController().onAddNewThematismClick();
                        }
                    }
                ],

                items: [
                    {
                        xtype: 'grid',
                        focusable: true,
                        hideHeaders: true,
                        layout: 'fit',
                        shrinkWrap: 2,
                        manageHeight: false,
                        disableSelection: true,
                        forceFit: true,
                        viewConfig: {
                            markDirty: false
                        },
                        store: vm.get('item').thematisms(),
                        columns: [
                            {
                                dataIndex: 'name',
                                align: 'left'
                            },
                            {
                                xtype: 'actioncolumn',
                                stopSelection: true,
                                align: 'right',
                                width: 50,
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                                        tooltip: CMDBuildUI.locales.Locales.common.actions.edit,
                                        handler: function (grid, rowIndex, colIndex) {
                                            var record = grid.getStore().getAt(rowIndex);
                                            me.getController().editThematism(record);
                                        }
                                    },
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                                        tooltip: CMDBuildUI.locales.Locales.common.actions.delete,
                                        handler: function (grid, rowIndex, colIndex) {
                                            var record = grid.getStore().getAt(rowIndex);
                                            me.getController().deleteThematism(grid, record);
                                        }
                                    }
                                ]
                            }
                        ],

                        listeners: {
                            cellclick: {
                                fn: me.getController().onThematismGridCellClick,
                                scope: me.getController()
                            }
                        }
                    }
                ]
            });
        }
        return me.menu;
    },

    onWindowClick: function (e) {
        this.collapseMenuIf(e);
    },

    expandMenu: function () {
        var me = this;
        Ext.suspendLayouts();
        var menu = me.getMenu();
        menu.show();
        me.isMenuExpanded = true;
        me.alignMenu();

        Ext.resumeLayouts(true);

        Ext.asap(function () {
            me.menu.mon(Ext.getWin(), 'click', me.onWindowClick, me);
        });
    },

    collapseMenuIf: function (e) {
        var me = this;
        if (!e.within(me.menu.el, false, true)) {
            me.collapseMenu();
        }
    },

    collapseMenu: function () {
        var me = this;
        if (me.isMenuExpanded && !me.destroyed && !me.destroying) {
            var menu = this.getMenu();
            me.isMenuExpanded = false;
            menu.destroy();
            me.menu = undefined;
        }
    },

    alignMenu: function () {
        var me = this,
            menu;
        if (me.rendered && !me.destroyed) {
            menu = me.getMenu();

            if (menu.isVisible() && menu.isFloating()) {
                me.doAlignMenu();
            }
        }
    },

    privates: {
        openCls: 'open',

        doAlignMenu: function () {
            var me = this,
                menu = me.menu,
                aboveSfx = '-above',
                newPos,
                isAbove;

            // Align to the trigger wrap because the border isn't always on the input element, which
            // can cause the offset to be off
            menu.el.alignTo(me.el.id, 'tl-bl?');

            // We used *element* alignTo to bypass the automatic reposition on scroll which
            // Floating#alignTo does. So we must sync the Component state.
            newPos = menu.floatParent ? menu.getOffsetsTo(menu.floatParent.getTargetEl()) : menu.getXY();
            menu.x = newPos[0];
            menu.y = newPos[1];

            // isAbove = menu.el.getY() < me.el.getY();
            me.el[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
            menu[isAbove ? 'addCls' : 'removeCls'](menu.baseCls + aboveSfx);
        }
    }
});
