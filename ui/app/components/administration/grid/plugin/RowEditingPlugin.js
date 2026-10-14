Ext.define('CMDBuildUI.components.administration.grid.plugin.RowEditingPlugin', {
    extend: 'Ext.grid.plugin.RowEditing',
    alias: 'plugin.actionColumnRowEditing',

    /**
     * Property
     * It hides the default action buttons
     * If it is false, it will work as the default Ext.grid.plugin.RowEditing.
     *
     * Defaults to: true
     */
    hiddenButtons: true,

    /**
     * Property
     * The button ui
     *
     * Defaults to: 'default'
     */
    buttonsUi: 'default',

    /**
     * Property
     * It adds a button in a extra action column into grid.
     *
     * Accepts: boolean (true show | false hide)| button object (not implemented yet)
     * Defaults to: true
     */
    saveButton: true,

    /**
     * Save button icon class
     *
     * Accepts: string
     * Defaults to: CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid')
     */
    saveButtonIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid'),

    /**
     * Save button tool tip
     *
     * Accepts: string
     * Defaults to: 'Save the edited line'
     */
    saveButtonToolTip: CMDBuildUI.locales.Locales.administration.common.actions.save,

    /**
     * Property:
     * It adds a button in a extra action column into grid.
     *
     * Accepts: boolean (true show | false hide)| button object (not implemented yet)
     * Defaults to: true
     */
    cancelButton: true,

    /**
     * Cancel button icon class
     *
     * Accepts: string
     * Defaults to: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid')
     */
    cancelButtonIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),

    /**
     * Cancel button tool tip
     *
     * Accepts: string
     * Defaults to: 'Cancel'
     */
    cancelButtonToolTip: CMDBuildUI.locales.Locales.administration.common.actions.cancel,

    /**
     * A list of columns ids to hide on edit.
     *
     * Accepts: list string (itemId)
     * Defaults to: empty list []
     */
    hiddenColumnsOnEdit: [],

    /**
     * Sets extra buttons on action column
     *
     * Accepts: list of objects
     * Defaults to: empty list []
     */
    extraButtons: [],

    /**
     * Sets extra placeholders buttons on action column
     *
     * Accepts: list of objects
     * Defaults to: empty list []
     */
    placeholdersButtons: [],

    /**
     * Stores the extra columns to hide and show it on events.
     *
     * Defaults to: empty list []
     */
    extraColumns: [],

    /**
     * Property: canCancel
     * It disable cancel action if the value is false.
     *
     * Accepts: boolean
     * Defaults to: true
     */
    canCancel: true,

    /**
     * @override
     *
     * Configure everything
     */
    initEditorConfig: function () {
        // Clear info
        this.extraColumns = [];
        if (!Ext.isArray(this.extraButtons)) {
            this.extraButtons = [];
        }
        var me = this,
            grid = me.grid,
            view = me.view,
            headerCt = grid.headerCt,
            btns = ['saveBtnText', 'cancelBtnText', 'errorsText', 'dirtyText'],
            b,
            bLen = btns.length,
            cfg = {
                autoCancel: me.autoCancel,
                autoUpdate: me.autoUpdate,
                removeUnmodified: me.removeUnmodified,
                errorSummary: me.errorSummary,
                formAriaLabel: me.formAriaLabel,
                formAriaLabelRowBase: me.formAriaLabelRowBase + (grid.hideHeaders ? -1 : 0),
                fields: headerCt.getGridColumns(),
                hidden: true,
                view: view,
                updateButton: function (valid) {},
                editingPlugin: me
            },
            item;
        // Custom configuration.
        if (me.hiddenButtons) {
            cfg.getFloatingButtons = function () {
                var me = this,
                    btns = me.floatingButtons;

                if (!btns && !me.destroying && !me.destroyed) {
                    me.floatingButtons = btns = Ext.create('Ext.container.Container', {
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        },
                        setButtonPosition: function () {}
                    });
                }

                return btns;
            };
        } else {
            for (b = 0; b < bLen; b++) {
                item = btns[b];

                if (Ext.isDefined(me[item])) {
                    cfg[item] = me[item];
                }
            }
        }

        return cfg;
    },

    /**
     * @override
     *
     * Cancel edit. Sets the extra columns to false.
     */
    cancelEdit: function () {
        if (this.canCancel) {
            this.callParent();
            this.setExtraColumnsVisible(false);
            this.grid.fireEventArgs('canceledit', [this.grid]);
            this.extraButtons = [];
        }
    },

    /**
     * @override
     *
     * Save edit. Sets the extra columns to false.
     */
    completeEdit: function () {
        if (!this.editor.form.isValid()) {
            return false;
        }
        this.callParent();
        this.setExtraColumnsVisible(false);
        this.extraButtons = [];
    },

    /**
     * @override
     *
     * Starts editing the specified record, using the specified Column definition to define
     * which field is being edited.
     * @param {Ext.data.Model} record The Store data record which backs the row to be edited.
     * @param {Ext.grid.column.Column/Number} [columnHeader] The Column object defining
     * the column field to be focused, or index of the column. If not specified, it will default
     * to the first visible column.
     * @return {Boolean} `true` if editing was started, `false` otherwise.
     */
    startEdit: function (record, columnHeader) {
        if (columnHeader.itemId && columnHeader.itemId !== 'actionColumnEdit') {
            return false;
        }
        var me = this,
            editor = me.getEditor(),
            context;

        editor.record = record;
        if (me.extraColumns.length === 0) {
            me.addExtraColumns();
        }

        if (Ext.isEmpty(columnHeader)) {
            columnHeader = me.grid.getTopLevelVisibleColumnManager().getHeaderAtIndex(0);
        }

        if (editor.beforeEdit() !== false) {
            context = me.getEditingContext(record, columnHeader, true);

            if (
                context &&
                me.beforeEdit(context) !== false &&
                me.fireEvent('beforeedit', me, context) !== false &&
                !context.cancel
            ) {
                me.context = context;

                // If editing one side of a lockable grid, cancel any edit on the other side.
                if (me.lockingPartner) {
                    me.lockingPartner.cancelEdit();
                }

                editor.startEdit(context.record, context.column, context);
                me.editing = true;

                this.setExtraColumnsVisible(true);

                return true;
            }
        }

        return false;
    },

    /**
     * Hides or shows the extra action columns in the grid.
     * It is used on startEdit(), cancelEdit() and completeEdit() methods.
     * @param {Boolean} isVisible
     */
    setExtraColumnsVisible: function (isVisible) {
        var me = this,
            grid = me.grid;
        me.extraColumns.forEach(function (element) {
            element.setVisible(isVisible);
        });

        if (me.hiddenColumnsOnEdit && !grid.lookupViewModel().get('actions.view')) {
            me.hiddenColumnsOnEdit.forEach(function (columnItemId) {
                var column = grid.down('[itemId=' + columnItemId + ']');
                if (column) {
                    column.setVisible(!isVisible);
                    column.blur();
                }
            });
        }
    },

    /**
     * Add the extra action column
     */
    addExtraColumns: function () {
        var me = this,
            grid = me.grid;

        if (me.placeholdersButtons) {
            Ext.Array.forEach(me.placeholdersButtons, function (button) {
                var column = {
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    align: 'center',
                    editor: button,
                    menuDisabled: true,
                    sortable: false,
                    border: false,
                    resizable: false,
                    hideable: false,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                };
                me.extraColumns.push(grid.headerCt.insert(grid.columns.length, column));
            });
        }
        // Defines the cancel button
        if (me.cancelButton) {
            if (me.cancelButton === true) {
                me.cancelButton = {
                    iconCls: me.cancelButtonIconCls,
                    xtype: 'button',
                    ui: me.buttonsUi,
                    minWidth: 30,
                    maxWidth: 30,
                    tooltip: me.cancelButtonToolTip,
                    handler: function (button, rowIndex, colIndex) {
                        // button.up('grid').getView().refresh();
                        me.cancelEdit();
                    }
                };
            }

            me.extraButtons.push(me.cancelButton);
        }

        // Defines the saveButton
        if (me.saveButton) {
            if (me.saveButton === true) {
                me.saveButton = {
                    iconCls: me.saveButtonIconCls,
                    xtype: 'button',
                    minWidth: 30,
                    maxWidth: 30,
                    ui: me.buttonsUi,
                    tooltip: me.saveButtonToolTip,
                    handler: function (button, rowIndex, colIndex) {
                        me.completeEdit();
                    }
                };
            }

            me.extraButtons.push(me.saveButton);
        }

        // Adds the extra columns
        if (me.extraButtons) {
            Ext.Array.forEach(me.extraButtons, function (button) {
                // for (var index in me.extraButtons) {
                var column = {
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    align: 'center',
                    editor: button,
                    menuDisabled: true,
                    sortable: false,
                    border: false,
                    resizable: false,
                    hideable: false,
                    bind: {
                        hidden: '{actions.view}'
                    }
                };

                me.extraColumns.push(grid.headerCt.insert(grid.columns.length, column));
            });
        }
    }
});
