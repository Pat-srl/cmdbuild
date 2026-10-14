Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-topbar',

    control: {
        '#addgroup': {
            click: 'onAddGroupClick'
        }
    },

    /**
     * 
     * @param {Ext.button} button 
     * @param {*} event 
     * @param {*} eOpts 
     */
    onAddGroupClick: function (button, event, eOpt) {        
        var tabPanel = button.up('administration-content-groupsandpermissions-view').down('administration-content-groupsandpermissions-tabpanel');
        var vm = button.up('administration-content-groupsandpermissions-view').getViewModel();
        tabPanel.setActiveTab(0);
        vm.toggleEnableTabs(0);
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', 'administration/groupsandpermissions_empty/true', this);        
    }
});