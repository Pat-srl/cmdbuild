Ext.define('CMDBuildUI.view.administration.content.dms.models.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-models-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.dms.models.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        if (!vm.get('actions.empty')) {
            tabPanelHelper.addTab(
                view,
                'properties',
                CMDBuildUI.locales.Locales.administration.common.strings.properties,
                [
                    {
                        xtype: 'administration-content-dms-models-tabitems-properties-properties',
                        objectTypeName: vm.get('objectTypeName'),
                        objectId: vm.get('objectId'),
                        autoScroll: true
                    }
                ],
                0,
                {
                    disabled: '{disabledTabs.properties}'
                }
            );

            tabPanelHelper.addTab(
                view,
                'attributes',
                CMDBuildUI.locales.Locales.administration.attributes.attributes,
                [
                    {
                        xtype: 'administration-content-dms-models-tabitems-attributes-attributes',
                        objectTypeName: vm.get('objectTypeName'),
                        objectId: vm.get('objectId')
                    }
                ],
                1,
                {
                    disabled: '{disabledTabs.attributes}'
                }
            );

            tabPanelHelper.addTab(
                view,
                'fieldsmanagement',
                CMDBuildUI.locales.Locales.administration.forms.form,
                [
                    {
                        xtype: 'administration-attributes-fieldsmanagement-panel',
                        objectTypeName: vm.get('objectTypeName'),
                        objectId: vm.get('objectId'),
                        itemId: 'form',
                        theComposer: vm.get('theModel.formStructure')
                    }
                ],
                2,
                {
                    disabled: '{disabledTabs.fieldsmanagement}'
                }
            );
        }
        vm.set('activeTab', 0);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.dms.models.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        if (
            newtab &&
            ((newtab.reference === 'fieldsmanagement' &&
                this.getView().lookupViewModel().get('theModel.isPrototype')) ||
                ((newtab.reference === 'import_export' || newtab.reference === 'fieldsmanagement') &&
                    this.getView().lookupViewModel().get('isSimpleClass')))
        ) {
            this.getView().lookupViewModel().set('activeTab', 0);
            return;
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.dmsmodels',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
        if (newtab.getReference() === 'fieldsmanagement') {
            var formView = view.down('#form');
            formView.fireEvent('show');
        }
    }
});
