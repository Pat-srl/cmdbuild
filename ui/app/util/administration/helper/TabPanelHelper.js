Ext.define('CMDBuildUI.util.administration.helper.TabPanelHelper', {
    requires: ['Ext.util.Format'],

    singleton: true,

    /**
     * Add tab
     * @param {Ext.tab.Panel} view
     * @param {String} name
     * @param {Array} items
     * @param {Number} index
     * @param {Object} bind
     * @param {Object} config
     *
     * @return {Ext.tab.Panel}
     */
    addTab: function (view, name, localizedName, items, index, bind, config) {
        try {
            return view.add({
                xtype: 'panel',
                items: items,
                reference: config && config.reference ? config.reference : name,
                layout: 'fit',
                viewModel: config || {},
                autoScroll: true,
                config: config || {},
                padding: 0,
                tabIndex: index,
                tabConfig: {
                    tabIndex: index,
                    title: localizedName,
                    tooltip: localizedName,
                    autoEl: {
                        'data-testid': Ext.String.format('administration-tab-{0}', name)
                    },
                    bind: bind || {}
                }
            });
        } catch (error) {
            CMDBuildUI.util.Logger.log(
                Ext.String.format('{0} tab generation exception', name),
                CMDBuildUI.util.Logger.levels.error
            );
            CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
        }
    },

    onTabChage: function (tabgroup, ctx, view, newtab, oldtab, eOpts) {
        var vm = ctx.getViewModel();
        vm.set('titledata.action', newtab.tabConfig.tooltip);
        if (
            (newtab && newtab.tab && newtab.tab.isDisabled()) ||
            vm.get('actions.add') ||
            vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add
        ) {
            vm.set('activeTab', 0);
            view.up('administration-content').getViewModel().set(tabgroup, 0);
        } else if (oldtab) {
            vm.set('activeTab', newtab.tabConfig.tabIndex);
            view.up('administration-content').getViewModel().set(tabgroup, newtab.tabConfig.tabIndex);
        }
        Ext.asap(
            function (view) {
                view.updateLayout();
            },
            this,
            [view]
        );
    }
});
