Ext.define("CMDBuildUI.components.grid.features.Grouping", {
    extend: 'Ext.grid.feature.Grouping',

    alias: 'feature.customgrouping',

    /**
     * @cfg {Boolean|Numeric} [startCollapsed=false]
     * True to start all groups collapsed. 
     * If a number, that number is used as max lenght for 
     * expanded groups at start.
     */
    startCollapsed: false,

    /**
     * @override
     *
     * @returns
     */
    createDataSource: function () {
        var me = this,
            view = me.view,
            lockPartner = me.lockingPartner,
            dataSource;

        // Share the GroupStore between both sides of a locked grid
        if (lockPartner && lockPartner.dataSource) {
            me.dataSource = view.dataSource = dataSource = lockPartner.dataSource;
        }
        else {
            me.dataSource = view.dataSource = dataSource = new CMDBuildUI.components.grid.features.GroupStore(me, me.gridStore);
        }

        return dataSource;
    },

});