Ext.define('CMDBuildUI.view.processes.instances.TabPanel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.processes.instances.TabPanelController',
        'CMDBuildUI.view.processes.instances.TabPanelModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    alias: 'widget.processes-instances-tabpanel',
    controller: 'processes-instances-tabpanel',
    viewModel: {
        type: 'processes-instances-tabpanel'
    },

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,
    header: false,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },
    layout: 'fit',

    tabtools: CMDBuildUI.view.processes.instances.Util.getTools(),

    /**
     * Get view tab configuration
     *
     * @returns {Object} Configuration for view tab
     */
    getViewTabConfig: function () {
        return {
            xtype: 'processes-instances-instance-view',
            shownInPopup: true,
            hideTools: this.getReadOnlyTabs()
        };
    },

    /**
     * Get edit tab configuration
     *
     * @returns {Object} Configuration for edit tab
     */
    getEditTabConfig: function () {
        return {
            xtype: 'processes-instances-instance-edit'
        };
    },

    /**
     * Get create tab configuration
     *
     * @returns {Object} Configuration for create tab
     */
    getCreateTabConfig: function () {
        return {
            xtype: 'processes-instances-instance-create'
        };
    },

    /**
     * Get notes tab configuration
     *
     * @returns {Object} Configuration for notes tab
     */
    getNotesTabConfig: function () {
        return {
            xtype: 'notes-panel',
            readOnly: true,
            bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding
        };
    },

    /**
     * Get relations list tab configuration
     *
     * @returns {Object} Configuration for relations list tab
     */
    getRelationsTabConfig: function () {
        return {
            xtype: 'relations-list-container',
            showRelGraphBtn: !this.getReadOnlyTabs(),
            showEditCardBtn: !this.getReadOnlyTabs(),
            readOnly: true
        };
    },

    /**
     * Get history tab configuration
     *
     * @returns {Object} Configuration for history tab
     */
    getHistoryTabConfig: function () {
        return {
            xtype: 'history-grid',
            autoScroll: true
        };
    },

    /**
     * Get emails tab configuration
     *
     * @returns {Object} Configuration for emails tab
     */
    getEmailsTabConfig: function () {
        return {
            xtype: 'emails-container',
            readOnly: true
        };
    },

    /**
     * Get dms tab configuration
     *
     * @param {Object} processDef 
     * @returns {Object} Configuration for dms tab
     */
    getDmsTabConfig: function (processDef) {
        var theObject = this.getViewModel().get('theObject'),
            objectModel = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(theObject.get('_type'), CMDBuildUI.util.helper.ModelHelper.objecttypes.process),
            attachmentsReadOnly = true,
            can_fc_attachment = objectModel.get('_can_fc_attachment');

        if (can_fc_attachment) {
            var flowstatus = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
                CMDBuildUI.model.processes.Process.flowstatus.lookuptype
            );
            if (flowstatus) {
                var closed = flowstatus.values().findRecord('code', 'closed.completed');
                if (closed && closed.getId() == theObject.get('status')) {
                    attachmentsReadOnly = this.getReadOnlyTabs();
                }
            }
        }
        return {
            xtype: 'dms-container',
            readOnly: !processDef[CMDBuildUI.model.users.Grant.permissions.attachment_write] || attachmentsReadOnly,
            viewModel: {
                data: {
                    basepermissions: {
                        edit: true
                    }
                }
            }
        };
    }
});