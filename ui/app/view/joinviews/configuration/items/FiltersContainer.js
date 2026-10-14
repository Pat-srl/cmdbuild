Ext.define('CMDBuildUI.view.joinviews.configuration.items.FiltersContainer', {
    extend: 'Ext.form.FieldSet',
    requires: [
        'CMDBuildUI.view.joinviews.configuration.items.FiltersController'
    ],
    alias: 'widget.joinviews-configuration-items-filterscontainer',
    title: CMDBuildUI.locales.Locales.joinviews.filters,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.filters'
    },
    viewModel: {},
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        ui: '{fieldsetUi}'
    },
    layout: {
        type: 'fit'
    },
    items: [{
        xtype: 'filters-attributes-panel',
        itemId: 'attributesfilterpanel',
        allowInputParameter: false,
        allowCurrentGroup: true,
        allowCurrentUser: true,
        title: null,
        localized: {
            title: null
        },
        controller: 'joinviews-configuration-items-filters',
        viewModel: {
            type: 'joinviews-configuration-items-filters'
        }
    }],

    goingNextStep: function () {
        var vm = this.lookupViewModel(),
            attributePanel = this.down('filters-attributes-panel');

        // refresh theView.filter
        var filter = new CMDBuildUI.util.AdvancedFilter();
        filter.applyAdvancedFilter({
            attribute: attributePanel.getAttributesData()
        });

        if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(filter)) {
            filter = (Ext.Object.isEmpty(filter._attributesCustom)) ? '' : Ext.encode(filter._attributesCustom);
            vm.get('theView').set('filter', filter);
            return true;
        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(
                Ext.String.format(
                    '<span data-testid="message-window-text">{0}</span>',
                    CMDBuildUI.locales.Locales.errors.invalidfilter
                )
            );
        }
    },
    goingPreviousStep: function () {
        var vm = this.lookupViewModel(),
            attributePanel = this.down('filters-attributes-panel');

        // refresh theView.filter
        var filter = new CMDBuildUI.util.AdvancedFilter();
        filter.applyAdvancedFilter({
            attribute: attributePanel.getAttributesData()
        });

        if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(filter)) {
            filter = (Ext.Object.isEmpty(filter._attributesCustom)) ? '' : Ext.encode(filter._attributesCustom);
            vm.get('theView').set('filter', filter);
            return true;
        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(
                Ext.String.format(
                    '<span data-testid="message-window-text">{0}</span>',
                    CMDBuildUI.locales.Locales.errors.invalidfilter
                )
            );
        }
    }
});