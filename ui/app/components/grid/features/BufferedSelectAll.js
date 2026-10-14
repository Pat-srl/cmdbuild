Ext.define('CMDBuildUI.components.grid.features.BufferedSelectAll', {
    extend: 'Ext.grid.feature.Feature',
    alias: 'feature.bufferedsselectall',

    containerCls: Ext.baseCSSPrefix + 'buffered-select-all',

    /**
     * @override
     * Listen for store updates. Eg, from an Editor.
     * 
     * @returns
     */
    init: function (grid) {
        var me = this;
        me.view.bufferedSelectAllFeature = me;

        // create container
        var container = me.selectAllContainer = Ext.create({
            xtype: 'container',
            cls: me.containerCls,
            hidden: grid.isMultiSelectionEnabled ? !grid.isMultiSelectionEnabled() : true,
            dock: 'top',
            weight: 101
        });
        // create select all button
        var button = me.selectAllButton = container.add({
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.bulkactions.selectall,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'),
            ui: 'secondary-action',
            enableToggle: true
        });
        // add container to docked items
        grid.dockedItems = grid.dockedItems ? grid.dockedItems.unshift(container) : [container];

        // add events to button
        button.on({
            toggle: me.onSelectAllToggle,
            scope: me
        });

        // listen for selection model
        grid.getSelectionModel().on({
            selectionmodechange: 'onGridSelectionModeChange',
            scope: me
        });

        // add property to the grid to get pressed for select all button
        grid.isSelectAllPressed = false;
    },

    /**
     * On select all toggle button handler
     * 
     * @param {Ext.button.Button} button 
     * @param {Boolean} pressed 
     * @param {Object} eOpts 
     */
    onSelectAllToggle: function (button, pressed, eOpts) {
        var me = this,
            mapContainer = me.grid.getViewModel().get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass ? me.grid.getGridContainer().getMapContainer() : null;
        if (pressed) {
            // update button text
            button.setText(CMDBuildUI.locales.Locales.bulkactions.cancelselection);
            button.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular'));
            // clear selection
            me.grid.setSelection();

            if (mapContainer) {
                mapContainer.fireEventArgs("selectdeselectall", [true])
            }
        } else {
            // update button text
            button.setText(CMDBuildUI.locales.Locales.bulkactions.selectall);
            button.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'));

            if (mapContainer) {
                mapContainer.fireEventArgs("selectdeselectall", [false])
            }
        }
        // update select all status
        me.grid.isSelectAllPressed = pressed;
        // update selection column style
        me.grid.selModel.column.setDisabled(pressed);
        me.grid.body.toggleCls(me.grid.extraBodyCls + "-selectall", pressed);
    },

    /**
     * 
     * @param {Ext.selection.Model} selmodel 
     * @param {String} mode 
     */
    onGridSelectionModeChange: function (selmodel, mode) {
        var me = this,
            grid = this.grid;
        if (grid.isMultiSelectionEnabled && grid.isMultiSelectionEnabled()) {
            me.selectAllContainer.setHidden(false);
        } else {
            me.selectAllContainer.setHidden(true);
            if (me.grid.isSelectAllPressed) {
                me.onSelectAllToggle(me.selectAllButton, false);
            }
        }
    }
});
