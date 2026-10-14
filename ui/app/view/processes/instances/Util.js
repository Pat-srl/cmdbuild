Ext.define('CMDBuildUI.view.processes.instances.Util', {
    singleton: true,

    getTools: function () {
        return [{
            // open tool
            xtype: 'tool',
            itemId: 'openProcessTool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
            cls: 'management-tool',
            action: 'view',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.processes.openactivity,
            autoEl: {
                'data-testid': 'processes-instance-tool-open'
            },
            bind: {
                hidden: '{hiddentools.open}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.processes.openactivity'
            }
        }, {
            // edit tool
            xtype: 'tool',
            itemId: 'editBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.processes.editactivity,
            autoEl: {
                'data-testid': 'processes-instance-tool-edit'
            },
            bind: {
                hidden: '{hiddentools.edit}',
                disabled: '{!basepermissions.edit}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.processes.editactivity'
            }
        }, {
            // delete tool
            xtype: 'tool',
            itemId: 'deleteBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.processes.abortprocess,
            autoEl: {
                'data-testid': 'processes-instance-tool-delete'
            },
            bind: {
                hidden: '{hiddentools.delete}',
                disabled: '{!basepermissions.delete}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.processes.abortprocess'
            }
        }, {
            // relation graph
            xtype: 'tool',
            itemId: 'relgraphBtn',
            iconCls: 'cmdbuildicon-relgraph',
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.relationGraph.openRelationGraph,
            autoEl: {
                'data-testid': 'processes-instance-tool-relgraph'
            },
            bind: {
                hidden: '{hiddentools.relgraph}',
                disabled: '{!permissions.relgraph}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relationGraph.openRelationGraph'
            }
        }, this.getHelpTool()];
    },

    getHelpTool: function () {
        return {
            // help tool
            xtype: 'tool',
            itemId: 'helpBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid'),
            cls: 'management-tool no-action',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.common.actions.help,
            autoEl: {
                'data-testid': 'processes-instance-tool-help'
            },
            bind: {
                hidden: '{hiddentools.help}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.help'
            }
        };
    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId
     * @param {String} activityId
     * @param {String} action
     */

    doOpenInstance: function (typeName, objectId, activityId, action) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Utilities.redirectTo(CMDBuildUI.view.processes.instances.Util.getBasePath(typeName, objectId, activityId, true, action), true);
        deferred.resolve();
        return deferred.promise;

    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId     
     * @param {String} activityId
     */
    doEditInstance: function (typeName, objectId, activityId) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId("proc.inst.edit");
        CMDBuildUI.util.Utilities.redirectTo(CMDBuildUI.view.processes.instances.Util.getBasePath(typeName, objectId, activityId, true, CMDBuildUI.mixins.DetailsTabPanel.actions.edit), true);
        deferred.resolve();
        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String} objectTypeName
     * @param {CMDBuildUI.model.processes.Instance} record    
     */
    doAbortInstance: function (objectType, objectTypeName, record) {
        var deferred = new Ext.Deferred();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.processes.abortconfirmation,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    CMDBuildUI.util.Ajax.setActionId("proc.inst.delete");
                    // get the object
                    record.erase({
                        success: function (record, operation) {
                            // fire global card deleted event
                            Ext.GlobalEvents.fireEventArgs("processinstanceaborted", [record]);

                            // execute after delete form triggers
                            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
                            if (item) {
                                // get form triggers
                                var triggers = item.getFormTriggersForAction(CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterDelete);
                                if (triggers && triggers.length) {
                                    var api = Ext.apply({
                                        record: record
                                    }, CMDBuildUI.util.api.Client.getApiForFormAfterDelete());
                                    CMDBuildUI.util.helper.FormHelper.executeFormTriggers(triggers, api);
                                }
                            }
                            deferred.resolve();
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }, this);
        return deferred.promise;
    },

    /**
     * Get resource base path for routing.
     * @param {String} typeName
     * @param {String} objectId
     * @param {String} activityId
     * @param {Boolean} includeactivity
     * @param {String} action
     * @return {String}
     */
    getBasePath: function (typeName, objectId, activityId, includeactivity, action) {
        var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
            typeName,
            objectId,
            (includeactivity && activityId) ? activityId : null,
            action
        );
        return url;
    }
});