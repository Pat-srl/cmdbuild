Ext.define('CMDBuildUI.view.filters.ActionControllerMixin', {
    mixinId: 'filteractioncontroller-mixin',

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onFilterBtnBeforeRender: function (button, eOpts) {
        var vm = this.getViewModel();
        var me = this;
        this.storename = button.storename;

        function initMenu(filters) {
            if (filters.length) {
                // TODO: add menu with filter
            } else {
                button.on("click", me.onAddNewFilterClick, me);
            }
        }

        vm.bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            }
        }, function (data) {
            if (data.objectTypeName && data.objectType) {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                var filters = item.filters();
                if (!filters.isLoaded()) {
                    filters.getProxy().setUrl(CMDBuildUI.util.api.Common.getFiltersUrl(data.objectType, data.objectTypeName));
                    filters.load({
                        callback: function (records, operation, success) {
                            initMenu(records);
                        }
                    });
                }
            }
        });
    },

    /**
     * Open a popup to create a new filter
     */
    onAddNewFilterClick: function () {
        this.editFilter();
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.Filter} filter The filter to edit. Leave blank to create new one.
     */
    editFilter: function (filter) {
        var me = this;
        var vm = this.getViewModel();

        // view model definition
        if (!filter) {
            filter = Ext.create('CMDBuildUI.model.base.Filter', {
                name: CMDBuildUI.locales.Locales.filters.newfilter,
                description: CMDBuildUI.locales.Locales.filters.newfilter,
                ownerType: vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.view ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
            });
        }
        var viewmodel = {
            data: {
                objectType: vm.get("objectType"),
                objectTypeName: vm.get("objectTypeName"),
                theFilter: filter
            }
        };

        // popup definition
        var popup = CMDBuildUI.util.Utilities.openPopup(null, filter.get("name"), {
            xtype: 'filters-panel',
            viewModel: viewmodel,
            listeners: {
                /**
                 * 
                 * @param {CMDBuildUI.view.filters.Panel} panel 
                 * @param {CMDBuildUI.model.base.Filter} filter 
                 * @param {Object} eOpts 
                 */
                applyfilter: function (panel, filter, eOpts) {
                    me.onApplyFilter(filter);
                    panel.fireEvent("popupclose");
                },
                /**
                 * 
                 * @param {CMDBuildUI.view.filters.Panel} panel 
                 * @param {CMDBuildUI.model.base.Filter} filter 
                 * @param {Object} eOpts 
                 */
                saveandapplyfilter: function (panel, filter, eOpts) {
                    me.onSaveAndApplyFilter(filter);
                },
                /**
                 * Custom event to close popup directly from popup
                 * @param {Object} eOpts 
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            }
        });
    },

    privates: {
        /**
         * @property {String} storename
         * The store name in ViewModel.
         */
        storename: null
    }
});