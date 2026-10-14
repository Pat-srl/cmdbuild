Ext.define('CMDBuildUI.view.administration.content.processes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.processes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        Ext.resumeLayouts();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: 'administration-content-processes-tabitems-properties-properties',
                    objectTypeName: vm.get('objectTypeName'),
                    objectId: vm.get('objectId'),

                    autoScroll: true
                }
            ],
            0,
            { disabled: '{disabledTabs.properties}' }
        );

        tabPanelHelper.addTab(
            view,
            'attributes',
            CMDBuildUI.locales.Locales.administration.attributes.attributes,
            [
                {
                    xtype: 'administration-content-processes-tabitems-attributes-attributes',
                    objectTypeName: vm.get('objectTypeName'),
                    objectId: vm.get('objectId')
                }
            ],
            1,
            { disabled: '{disabledTabs.attributes}' }
        );

        tabPanelHelper.addTab(
            view,
            'domains',
            CMDBuildUI.locales.Locales.administration.navigation.domains,
            [
                {
                    xtype: 'administration-content-processes-tabitems-domains-domains'
                }
            ],
            2,
            { disabled: '{disabledTabs.domains}' }
        );

        tabPanelHelper.addTab(
            view,
            'tasks',
            CMDBuildUI.locales.Locales.administration.tasks.plural,
            [
                {
                    xtype: 'administration-content-tasks-view',
                    type: CMDBuildUI.model.tasks.Task.types.workflow,
                    workflowClassName: vm.get('objectTypeName'),
                    viewModel: {
                        data: {
                            isStandalone: false
                        }
                    }
                }
            ],
            3,
            { disabled: '{disabledTabs.tasks}' }
        );

        tabPanelHelper.addTab(
            view,
            'forms',
            CMDBuildUI.locales.Locales.administration.forms.forms,
            [
                {
                    xtype: 'administration-content-processes-tabitems-forms-forms',
                    itemId: 'forms'
                }
            ],
            4,
            { disabled: '{disabledTabs.forms}' }
        );

        tabPanelHelper.addTab(
            view,
            'export',
            CMDBuildUI.locales.Locales.administration.importexport.texts['export'],
            [
                {
                    xtype: 'administration-content-importexport-datatemplates-view',
                    viewModel: {
                        data: {
                            targetType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                            targetName: vm.get('objectTypeName')
                        }
                    }
                }
            ],
            5,
            {
                disabled: '{disabledTabs.export}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'helps',
            CMDBuildUI.locales.Locales.administration.processes.helps.helps,
            [
                {
                    xtype: 'administration-content-processes-tabitems-helps-helps',
                    itemId: 'helps'
                }
            ],
            6,
            { disabled: '{disabledTabs.helps}' }
        );

        tabPanelHelper.addTab(
            view,
            'permissions',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
            [
                {
                    xtype: 'administration-content-processes-tabitems-properties-permissions'
                }
            ],
            7,
            {
                disabled: '{disabledTabs.permissions}'
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.administration.content.processes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.processes',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
        if (newtab.getReference() === 'forms') {
            var vm = view.getViewModel();
            var formViewModel = view.down('#forms').getViewModel();
            var activitiesStore = formViewModel.get('activitiesStore');
            if (activitiesStore) {
                formViewModel.set('activitiesWithForm.isReady', false);
                formViewModel.set('activitiesWithoutForm.isReady', false);
                activitiesStore.load();
            }
            Ext.asap(function () {
                var attributeStore = vm.get('attributesStore');
                if (attributeStore) {
                    attributeStore.load();
                }
            });
        }
    },

    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {
        Ext.ComponentQuery.query('processes-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('processes/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
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
