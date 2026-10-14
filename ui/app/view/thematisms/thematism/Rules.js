Ext.define('CMDBuildUI.view.thematisms.thematism.Rules', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.thematisms.thematism.RulesController',
        'CMDBuildUI.view.thematisms.thematism.RulesModel'
    ],

    alias: 'widget.thematisms-thematism-rules',
    controller: 'thematisms-thematism-rules',
    viewModel: {
        type: 'thematisms-thematism-rules'
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },
    title: CMDBuildUI.locales.Locales.thematism.defineLegend,
    localized: {
        title: 'CMDBuildUI.locales.Locales.thematism.defineLegend'
    },
    collapsible: true,
    scrollable: true,
    items: [
        {
            xtype: 'container',
            layout: 'hbox',
            items: [
                {
                    xtype: 'button',
                    itemId: 'calculaterules',
                    text: CMDBuildUI.locales.Locales.thematism.calculateRules,
                    ui: 'management-primary-small',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.thematism.calculateRules'
                    },
                    margin: '0 0 15 15',
                    hidden: true,
                    disabled: true,
                    bind: {
                        disabled: '{buttonsDisabled}'
                    }
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tool',
                    itemId: 'modifyrules',
                    cls: 'management-tool no-action',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
                    tooltip: 'CMDBuildUI.locales.Locales.thematism.editThematism',
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.thematism.editThematism'
                    },
                    margin: '0 15 15 0',
                    hidden: true,
                    disabled: true,
                    bind: {
                        disabled: '{options.disabled}',
                        hidden: '{options.hidden}'
                    }
                }
            ]
        },
        {
            xtype: 'grid',
            markDirty: false,
            padding: 10,
            forceFit: true,
            disableSelection: true,
            bind: {
                store: '{legendstore}'
            },
            viewConfig: {
                markDirty: false
            },
            columns: [
                {
                    text: CMDBuildUI.locales.Locales.thematism.value,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.thematism.value'
                    },
                    dataIndex: 'viewValue',
                    sorter: {
                        sorterFn: function (a, b) {
                            if (Ext.isArray(a.get('value')) && Ext.isArray(b.get('value'))) {
                                var array1 = a.get('value').map(Number),
                                    array2 = b.get('value').map(Number);
                                return array1[0] < array2[0]
                                    ? -1
                                    : array1[0] > array2[0]
                                    ? 1
                                    : array1[1] <= array2[1]
                                    ? -1
                                    : 1;
                            } else {
                                return a.get('viewValue') < b.get('viewValue') ? -1 : 1;
                            }
                        }
                    },
                    renderer: function (value) {
                        if (
                            this.lookupViewModel().get('theThematism.analysistype') ==
                            CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals
                        ) {
                            var extremes = value.split(' ');
                            value =
                                extremes.length > 1 ? Ext.String.format('{0} - {1}', extremes[0], extremes[1]) : value;
                        }
                        return value;
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.thematism.quantity,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.thematism.quantity'
                    },
                    dataIndex: 'count'
                },
                {
                    text: CMDBuildUI.locales.Locales.thematism.color,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.thematism.color'
                    },
                    dataIndex: 'color',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return Ext.String.format(
                            '<div style="background-color: {0}; width: inherit; height: 32px;"> </div>',
                            value
                        );
                    }
                }
            ]
        }
    ],

    //this configuration when true, enables some extra behavours:
    //color picker expantion, and datachanged event handle in the view model
    config: {
        needListener: false
    },

    /**
     * @param {string}
     */
    getColorPicker: function () {
        var me = this;

        if (!me.colorpicker) {
            me.colorpicker = new Ext.picker.Color({
                xtype: 'colorpicker',
                floating: true,
                alwaysOnTop: 10,
                shadow: false,
                focusable: true,
                resizable: false,
                draggable: false,
                minWidth: 300,
                maxWidth: 300,
                layout: 'fit',
                value: null,
                viewModel: {
                    theLegendRecord: null,
                    formulas: {
                        updateSelected: {
                            bind: '{theLegendRecord}',
                            get: function (record) {
                                if (record) {
                                    var color = record.get('color').substr(1).toUpperCase();
                                    foundColor = this.getView().colors.find(function (col) {
                                        if (col == color) {
                                            return true;
                                        }
                                    });
                                    if (foundColor) {
                                        this.getView().select(foundColor);
                                    }
                                }
                            }
                        }
                    }
                },
                listeners: {
                    select: function (colorpicker, color, eopts) {
                        var legendRecord = this.getViewModel().get('theLegendRecord');
                        legendRecord.set('color', '#' + color);
                    }
                },
                controller: {
                    listen: {
                        component: {
                            '#': {
                                beforerender: function () {
                                    me.colorpicker.mon(
                                        me,
                                        'destroy',
                                        function () {
                                            me.colorpicker.destroy();
                                        },
                                        me
                                    );
                                }
                            }
                        }
                    }
                }
            });
        }
        return me.colorpicker;
    },

    /**
     *
     * @param {CMDBuildUI.model.thematisms.LegendModel} record
     */
    setColorPickerRecord: function (record) {
        var me = this;
        var colorpicker = me.colorpicker;
        var vm = colorpicker.getViewModel();
        vm.set('theLegendRecord', record);
    },

    /**
     *
     * @param {Object} e
     */
    onWindowClick: function (e) {
        this.collapseIf(e);
    },

    /**
     * @param {HTMLElement} tr the cell clicked
     */
    expandColorPicker: function (tr) {
        var me = this;
        //Ext.suspendLayouts();

        var colorpicker = me.getColorPicker();
        colorpicker.show();
        colorpicker.setZIndex(190000);

        me.isColorPickerExpanded = true;
        me.alignColorPicker(tr);

        Ext.asap(function () {
            me.colorpicker.mon(Ext.getWin(), 'click', me.onWindowClick, me);
        });

        //Ext.resumeLayouts(true);
    },

    /**
     *
     * @param {*} e
     */
    collapseIf: function (e) {
        var me = this;
        //if the click is on the color picker
        if (e.within(me.colorpicker.el, false, true)) {
            return;
        } else {
            var record = e.record;
            if (!record || !e.record.entityName.includes('LegendModel')) {
                me.collapseColorPicker();
            }
        }
    },

    /**
     *
     */
    collapseColorPicker: function () {
        var me = this;

        me.colorpicker.hide();
        me.isColorPickerExpanded = false;

        me.colorpicker.mun(Ext.getWin(), 'click', me.onWindowClick, me);
        // me.colorpicker.destroy();
        // me.colorpicker = null;
    },

    /**
     * @param {HTMLElement} tr the cell clicked
     */
    alignColorPicker: function (tr) {
        var me = this,
            colorpicker;

        if (me.rendered && !me.destroyed) {
            colorpicker = me.getColorPicker();

            if (colorpicker.isVisible() && colorpicker.isFloating()) {
                me.doAlignColorPicker(tr);
            }
        }
    },

    /**
     * sets the data for the legendstore
     */
    setrules: function () {
        var vm = this.getViewModel();
        CMDBuildUI.thematisms.util.Util.calculateLegend(
            vm.get('theThematism'),
            /**
             * @param {CMDBuildUI.model.thematisms.Thematism} thematism
             * @param {[CMDBuildUI.model.thematisms.LegendModel]} legenddata
             */
            function (thematism, legenddata) {
                try {
                    vm.set('legenddata', legenddata);
                } catch (err) {}
            },
            this
        );
    },

    privates: {
        /**
         * @param {HTMLElement} tr the cell clicked
         */
        doAlignColorPicker: function (tr) {
            var me = this,
                colorpicker = me.colorpicker;

            colorpicker.el.alignTo(tr, 'tr');
        }
    }
});
