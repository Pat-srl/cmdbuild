Ext.define('CMDBuildUI.view.administration.content.classes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: 'administration-content-classes-tabitems-properties-properties',
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
                    xtype: 'administration-content-classes-tabitems-attributes-attributes',
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
            'domains',
            CMDBuildUI.locales.Locales.administration.navigation.domains,
            [
                {
                    xtype: 'administration-content-classes-tabitems-domains-domains'
                }
            ],
            2,
            {
                disabled: '{disabledTabs.domains}'
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
                    theComposer: vm.get('theObject.formStructure')
                }
            ],
            3,
            {
                disabled: '{disabledTabs.fieldsmanagement}'
            }
        );

        tabPanelHelper.addTab(
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
            4,
            {
                disabled: '{disabledTabs.import_export}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'layers',
            CMDBuildUI.locales.Locales.administration.classes.strings.levels,
            [
                {
                    xtype: 'administration-content-classes-tabitems-layers-layers'
                }
            ],
            5,
            {
                disabled: '{disabledTabs.layers}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'geoattributes',
            CMDBuildUI.locales.Locales.administration.classes.strings.geaoattributes,
            [
                {
                    xtype: 'administration-content-classes-tabitems-geoattributes-geoattributes'
                }
            ],
            6,
            {
                disabled: '{disabledTabs.geoattributes}'
            }
        );
        tabPanelHelper.addTab(
            view,
            'permissions',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
            [
                {
                    xtype: 'administration-content-classes-tabitems-permissions-permissions'
                }
            ],
            7,
            {
                disabled: '{disabledTabs.permissions}'
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        if (
            newtab &&
            ((newtab.reference === 'fieldsmanagement' &&
                this.getView().lookupViewModel().get('theObject.isPrototype')) ||
                ((newtab.reference === 'import_export' || newtab.reference === 'fieldsmanagement') &&
                    this.getView().lookupViewModel().get('isSimpleClass')))
        ) {
            this.getView().lookupViewModel().set('activeTab', 0);
            return;
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.classes',
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
