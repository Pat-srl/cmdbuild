Ext.define('CMDBuildUI.view.administration.content.pluginmanager.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-pluginmanager-view',
    mixins: ['CMDBuildUI.view.administration.content.pluginmanager.Mixin'],

    control: {
        '#': {
            afterlayout: 'onAfterLayout',
            beforerender: 'onBeforeRender'
        },
        '#addplugin': {
            click: 'onAddPluginClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#reloadButton': {
            click: 'onReloadBtnClick'
        },
        '#filterWarningsBtn': {
            click: 'onFilterWarningsClick'
        },
        '#resetFilterWarningsBtn': {
            click: 'onResetFilterWarningsClick'
        },
        '#tagsCombo': {
            cleartrigger: 'onClearTags',
            change: 'onTagsChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.pluginmanager.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();

        vm.bind('{plugins.count}', function (value) {
            view.down('#pluginsCount').setHtml(
                Ext.String.format(
                    CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords,
                    null,
                    null,
                    value
                )
            );
        });
    },

    /**
     *
     * @param {Ext.form.Panel} panel
     * @param {Ext.layout.form.Panel} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (panel, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onAddPluginClick: function (button, event, eOpts) {
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
            'href',
            'administration/pluginmanager_empty/true',
            this
        );
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const formData = new FormData();
        const url = Ext.String.format('{0}/system/plugins', CMDBuildUI.util.Config.baseUrl);
        const input = this.getView().down('#pluginFile').extractFileInput();
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        CMDBuildUI.util.administration.File.upload('POST', formData, input, url, {
            success: function () {
                CMDBuildUI.util.Utilities.checkBootStatus().then(function () {
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                });
            },
            callback: function () {
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            }
        });
    },

    /**
     * Search plugin
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onSearchChange: function (field, newValue, oldValue, eOpts) {
        const vm = this.getViewModel();
        const plugins = vm.get('plugins');
        plugins.filter([
            {
                id: 'name',
                property: 'name',
                value: newValue,
                exactMatch: false,
                anyMatch: true
            }
        ]);
    },

    /**
     * Clear search field
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        field.setValue();
    },

    /**
     * Filter plugins with warnings
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onFilterWarningsClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const plugins = vm.get('plugins');
        this.getView().down('#tagsCombo').clearValue();
        this.getView().down('#searchtext').setValue();
        plugins.clearFilter();
        plugins.filter([
            {
                id: 'patch',
                property: '_hasPatches',
                value: true,
                exactMatch: true
            }
        ]);
        vm.set('overview.isFilteredByWarnings', true);
    },

    /**
     * Filter plugins with warnings
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onResetFilterWarningsClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const plugins = vm.get('plugins');
        plugins.clearFilter();
        vm.set('overview.search', '');
        vm.set('overview.isFilteredByWarnings', false);
    },

    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onTagsChange: function (field, newValue, oldValue, eOpts) {
        const vm = this.getViewModel();
        const plugins = vm.get('plugins');
        plugins.filter([
            {
                id: 'tag',
                property: 'tag',
                value: newValue,
                exactMatch: true,
                caseSensitive: false
            }
        ]);
    },

    /**
     * @param {Ext.form.field.Combobox} combo
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onClearTags: function (combo, trigger, eOpts) {
        combo.clearValue();
        const vm = this.getViewModel();
        const plugins = vm.get('plugins');
        plugins.removeFilter('tag');
    }
});
