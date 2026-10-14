Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabpanel',
    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "group", CMDBuildUI.locales.Locales.administration.common.strings.properties, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-group-properties',
            objectType: vm.get("objectType"),
            autoScroll: true
        }], 0, {
            disabled: '{disabledTabs.group}'
        });
        tabPanelHelper.addTab(view, "permissions", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-permissions'
        }], 1, {
            disabled: '{disabledTabs.permissions}'
        });
        tabPanelHelper.addTab(view, "listOfUsers", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.userslist, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-users-users'
        }], 2, {
            disabled: '{disabledTabs.listOfUsers || !theGroup._can_users_read}'
        });
        tabPanelHelper.addTab(view, "uiConfig", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.uiconfig, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig'
        }], 3, {
            disabled: '{disabledTabs.uiConfig}'
        });
        tabPanelHelper.addTab(view, "defaultFilters", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultfilters, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters'
        }], 4, {
            disabled: '{disabledTabs.defaultFilters}'
        });
        if (vm.get('actions.add')) {
            vm.getParent().toggleEnableTabs(0);
        }

    },

    onAfterRender: function (view) {
        var vm = view.getViewModel();
        vm.bind({
            bindTo: '{activeTab}',
            single: true
        }, function (activeTab) {
            view.setActiveTab(activeTab);
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.groups', this, view, newtab, oldtab, eOpts);
    },

    /**
     * @param {CMDBuildUI.model.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {
        Ext.ComponentQuery.query('groupsandpermissions-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('groupsandpermissions/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
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