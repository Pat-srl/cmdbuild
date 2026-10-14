Ext.define('CMDBuildUI.view.filters.Launcher', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.filters.LauncherController', 'CMDBuildUI.view.filters.LauncherModel'],

    alias: 'widget.filters-launcher',
    controller: 'filters-launcher',
    viewModel: {
        type: 'filters-launcher'
    },

    statics: {
        /**
         *
         * @param {Object|Object[]} obj
         * @param {Function} callback
         */
        analyzeAttributeRecursive: function (obj, callback) {
            const keys = Ext.Object.getKeys(obj);
            keys.forEach(function (k) {
                const v = obj[k];
                if (k === 'simple') {
                    callback(v);
                } else if (Ext.isObject(v)) {
                    CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(v, callback);
                } else if (Ext.isArray(v)) {
                    v.forEach(function (o) {
                        CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(o, callback);
                    });
                }
            });
        },

        /**
         *
         * @param {String} objectType
         * @param {String} objectTypeName
         */
        getDefaultFilter: function (objectType, objectTypeName) {
            // get filter from preferences
            const preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName);
            if (preferences && !Ext.isEmpty(preferences.defaultFilter)) {
                return preferences.defaultFilter;
            }
            // get class default filter
            const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
            if (item && item.get('defaultFilter')) {
                return item.get('defaultFilter');
            }
        }
    },

    layout: 'hbox',
    cls: 'x-filters-launcher',

    config: {
        /**
         * @cfg {String} storeName
         * The name of the store within viewModel to apply the filter.
         */
        storeName: null,

        /**
         * @cfg {CMDBuildUI.model.base.Filter|Number} selected
         * Selected filter. Can be a `CMDBuildUI.model.base.Filter` instance or
         * the id of a filter.
         */
        selected: null,

        /**
         * @cfg {Boolean} showAttributesPanel
         */
        showAttributesPanel: true,

        /**
         * @cfg {Boolean} showRelationsPanel
         */
        showRelationsPanel: true,

        /**
         * @cfg {Boolean} showAttachmentsPanel
         */
        showAttachmentsPanel: true,

        /**
         * @cfg {Boolean} isDms
         */
        isDms: false
    },

    publishes: ['selected'],

    autoEl: {
        'data-testid': 'filters-launcher'
    },

    items: [
        {
            xtype: 'button',
            ui: 'management-neutral-action',
            itemId: 'mainbtn',
            reference: 'mainbtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
            arrowVisible: false,
            tooltip: CMDBuildUI.locales.Locales.filters.filterdata,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.filters.filterdata'
            },
            autoEl: {
                'data-testid': 'filters-launcher-mainbutton'
            }
        },
        {
            xtype: 'button',
            reference: 'filterdesc',
            itemId: 'filterdesc',
            ui: 'noui',
            bind: {
                html: '{appliedfilter.description}',
                hidden: '{!showClearBtn}'
            },
            autoEl: {
                'data-testid': 'filters-launcher-filterdesc'
            }
        },
        {
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.filters.clearfilter,
            itemId: 'clearfiltertool',
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.filters.clearfilter'
            },
            bind: {
                hidden: '{!showClearBtn}'
            },
            autoEl: {
                'data-testid': 'filters-launcher-clearfilter'
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

    /**
     * @param {CMDBuildUI.model.base.Filter|Number} newvalue
     * @param {CMDBuildUI.model.base.Filter|Number} oldvalue
     */
    updateSelected: function (newvalue, oldvalue) {
        this.fireEvent('selectionchange', this, newvalue, oldvalue);
    },

    /**
     *
     * @param {Boolean} skipReload
     */
    clearFilter: function (skipReload) {
        const vm = this.lookupViewModel();

        // remove filter
        const store = vm.get(this.getStoreName());
        const advancedFitler = store.getAdvancedFilter();
        advancedFitler.clearAttributesCustomFilter();
        advancedFitler.clearRelationsFilter();
        advancedFitler.clearAttachmentsFilter();
        advancedFitler.clearCqlFitler();
        advancedFitler.clearFunctionFilter();
        if (!skipReload) {
            store.load();
        }

        this.fireEventArgs('clearfilter', [this]);
        // update selected filter data
        vm.set('appliedfilter.id', null);
        vm.set('appliedfilter.description', null);

        if (vm.get('item').setCurrentFilter) {
            vm.get('item').setCurrentFilter(null);
        }
    },

    /**
     * Get menu
     * @return {Ext.menu.Menu}
     */
    getMenu: function () {
        const me = this;
        const vm = me.lookupViewModel();
        const objectType = vm.get('objectType');
        const objectTypeName = vm.get('objectTypeName');
        const clsdefaultfilter = CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid');
        const clsundefaultfilter = CMDBuildUI.util.helper.IconHelper.getIconId('star', 'regular');

        if (!me.menu) {
            me.menu = new Ext.container.Container({
                baseCls: Ext.baseCSSPrefix + 'boundlist',
                cls: Ext.baseCSSPrefix + 'filtermenu',
                floating: true,
                ownerCt: me,
                width: 300,
                items: [
                    {
                        xtype: 'grid',
                        layout: 'fit',
                        forceFit: true,
                        header: {
                            cls: 'mycls',
                            padding: 0,
                            tools: [
                                {
                                    type: 'plus',
                                    userCls: 'mycls',
                                    tooltip: CMDBuildUI.locales.Locales.filters.addfilter,
                                    handler: function (event, toolEl, panelHeader) {
                                        me.getController().onAddNewFilterClick();
                                    }
                                }
                            ]
                        },
                        columns: [
                            {
                                dataIndex: 'description',
                                align: 'left',
                                flex: 1,
                                renderer: function (value, metadata, r, rowIndex, colIndex, store, view) {
                                    metadata.tdAttr = 'data-qtip="' + Ext.htmlEncode(value) + '"';
                                    return value;
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                stopSelection: true,
                                minWidth: 110,
                                maxWidth: 110,
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                                        tooltip: CMDBuildUI.locales.Locales.common.actions.edit,
                                        handler: function (grid, rowIndex, colIndex) {
                                            const record = grid.getStore().getAt(rowIndex);
                                            me.getController().editFilter(record);
                                        },
                                        isActionDisabled: function (grid, rowindex, colindex, item, record) {
                                            return record.get('shared');
                                        }
                                    },
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular'),
                                        tooltip: CMDBuildUI.locales.Locales.common.actions.clone,
                                        handler: function (grid, rowIndex, colIndex) {
                                            const record = grid.getStore().getAt(rowIndex);
                                            const newrecord = {
                                                name: Ext.String.format(
                                                    '{0} {1}',
                                                    CMDBuildUI.locales.Locales.filters.copyof,
                                                    record.get('name')
                                                ),
                                                description: Ext.String.format(
                                                    '{0} {1}',
                                                    CMDBuildUI.locales.Locales.filters.copyof,
                                                    record.get('description')
                                                ),
                                                ownerType: record.get('ownerType'),
                                                target: record.get('target'),
                                                isOnlyDmsFilter: me.getIsDms() ? true : undefined,
                                                configuration: record.get('configuration'),
                                                shared: false
                                            };
                                            const filter = Ext.create('CMDBuildUI.model.base.Filter', newrecord);
                                            me.getController().editFilter(filter);
                                        },
                                        isActionDisabled: function (grid, rowindex, colindex, item, record) {
                                            const configuration = record.get('configuration') || {};
                                            return (
                                                configuration &&
                                                configuration.functions &&
                                                configuration.functions.length
                                            );
                                        }
                                    },
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                                        tooltip: CMDBuildUI.locales.Locales.common.actions.delete,
                                        handler: function (grid, rowIndex, colIndex) {
                                            const record = grid.getStore().getAt(rowIndex);
                                            me.getController().deleteFilter(record);
                                        },
                                        isActionDisabled: function (grid, rowindex, colindex, item, record) {
                                            return record.get('shared');
                                        }
                                    },
                                    {
                                        getClass: function (v, metadata, record, rowindex, colindex, store) {
                                            const deffilter = CMDBuildUI.view.filters.Launcher.getDefaultFilter(
                                                objectType,
                                                objectTypeName
                                            );
                                            if (record.getId() == deffilter) {
                                                return clsdefaultfilter;
                                            }
                                            return clsundefaultfilter;
                                        },
                                        getTip: function (v, metadata, record, rowindex, colindex, store) {
                                            const deffilter = CMDBuildUI.view.filters.Launcher.getDefaultFilter(
                                                objectType,
                                                objectTypeName
                                            );
                                            if (record.getId() == deffilter) {
                                                return CMDBuildUI.locales.Locales.filters.defaultunset;
                                            }
                                            return CMDBuildUI.locales.Locales.filters.defaultset;
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            // clear selected filter
                                            const selected = Ext.query('#' + grid.getItemId() + ' .fa-star');
                                            if (!Ext.isEmpty(selected)) {
                                                selected.forEach(function (t) {
                                                    t.setAttribute(
                                                        'class',
                                                        t.className.replace(clsdefaultfilter, clsundefaultfilter)
                                                    );
                                                    event.target.setAttribute(
                                                        'data-qtip',
                                                        CMDBuildUI.locales.Locales.filters.defaultset
                                                    );
                                                });
                                            }
                                            // set preferences
                                            let prefs;
                                            //
                                            if (
                                                record.getId() ==
                                                CMDBuildUI.view.filters.Launcher.getDefaultFilter(
                                                    objectType,
                                                    objectTypeName
                                                )
                                            ) {
                                                prefs = {
                                                    defaultFilter: undefined
                                                };
                                            } else {
                                                prefs = {
                                                    defaultFilter: record.getId()
                                                };
                                                event.target.setAttribute(
                                                    'class',
                                                    event.target.className.replace(clsundefaultfilter, clsdefaultfilter)
                                                );
                                                event.target.setAttribute(
                                                    'data-qtip',
                                                    CMDBuildUI.locales.Locales.filters.defaultunset
                                                );
                                            }
                                            // save preferences
                                            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences(
                                                objectType,
                                                objectTypeName,
                                                prefs
                                            );
                                        },
                                        isActionDisabled: function (grid, rowindex, colindex, item, record) {
                                            return !record.get('active');
                                        }
                                    }
                                ]
                            }
                        ],
                        listeners: {
                            cellclick: {
                                fn: me.getController().onFiltersGridCellClick,
                                scope: me.getController()
                            },
                            beforerender: function (view) {
                                this.setMaxHeight(window.innerHeight * 0.65);
                            }
                        },
                        bind: {
                            store: '{filters}'
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
        const me = this;
        Ext.suspendLayouts();

        const menu = this.getMenu();
        menu.show();
        me.isMenuExpanded = true;
        me.alignMenu();

        if (!me.ariaEl.dom.hasAttribute('aria-owns')) {
            me.ariaEl.dom.setAttribute('aria-owns', menu.el.id);
        }
        me.ariaEl.dom.setAttribute('aria-expanded', true);

        Ext.asap(function () {
            me.menu.mon(Ext.getWin(), 'click', me.onWindowClick, me);
        });

        Ext.resumeLayouts(true);
    },

    collapseMenuIf: function (e) {
        const me = this;
        if (!e.within(me.menu.el, false, true)) {
            me.collapseMenu();
        }
    },

    collapseMenu: function () {
        const me = this;
        if (me.isMenuExpanded && !me.destroyed && !me.destroying) {
            const menu = this.getMenu();
            menu.hide();
            me.isMenuExpanded = false;
            me.ariaEl.dom.setAttribute('aria-expanded', false);

            me.menu.mun(Ext.getWin(), 'click', me.onWindowClick, me);
        }
    },

    /**
     * Aligns the picker to the input element
     * @protected
     */
    alignMenu: function () {
        const me = this;
        let menu;

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
            const me = this;
            const menu = me.menu;
            const aboveSfx = '-above';
            let newPos, isAbove;

            // Align to the trigger wrap because the border isn't always on the input element, which
            // can cause the offset to be off
            menu.el.alignTo(me.el.id, 'tl-bl?');

            // We used *element* alignTo to bypass the automatic reposition on scroll which
            // Floating#alignTo does. So we must sync the Component state.
            newPos = menu.floatParent ? menu.getOffsetsTo(menu.floatParent.getTargetEl()) : menu.getXY();
            menu.x = newPos[0];
            menu.y = newPos[1];

            // add the {openCls}-above class if the picker was aligned above
            // the field due to hitting the bottom of the viewport

            // isAbove = menu.el.getY() < me.el.getY();
            me.el[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
            menu[isAbove ? 'addCls' : 'removeCls'](menu.baseCls + aboveSfx);
        }
    }
});
