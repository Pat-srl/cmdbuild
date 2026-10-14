Ext.define('CMDBuildUI.view.administration.content.domains.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage',
            itemupdated: 'onItemUpdated',
            cancelcreation: 'onCancelCreation',
            cancelupdating: 'onCancelUpdating'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.domains.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const currentTabIndex = vm.get('activeTabs.domains') || 0;

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: 'administration-content-domains-tabitems-properties-properties',
                    autoScroll: true
                }
            ],
            0,
            {
                disabled: '{actions.edit && activeTab !== 0}'
            }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'attributes',
            CMDBuildUI.locales.Locales.administration.attributes.attributes,
            [
                {
                    xtype: 'administration-content-domains-tabitems-attributes-attributes'
                }
            ],
            1,
            { disabled: '{actions.edit}' }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'enabledClasses',
            CMDBuildUI.locales.Locales.administration.domains.texts.enabledclasses,
            [
                {
                    xtype: 'administration-content-domains-tabitems-domains-classes'
                }
            ],
            2,
            { disabled: '{actions.edit && activeTab !== 2}' }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'import_export',
            CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile,
            [
                {
                    xtype: 'administration-content-importexport-datatemplates-view',
                    viewModel: {
                        data: {
                            targetName: vm.get('objectTypeName')
                        }
                    }
                }
            ],
            3,
            {
                disabled: '{actions.edit}'
            }
        );

        vm.set('activeTab', currentTabIndex);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.domains.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.domains',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {
        Ext.ComponentQuery.query('domains-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('domains/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
    },

    /**
     * @param {Object} eOpts
     */
    onCancelCreation: function (eOpts) {
        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    },

    /**
     * @param {Object} eOpts
     */
    onCancelUpdating: function (eOpts) {
        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    }
});
