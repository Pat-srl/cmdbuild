Ext.define('CMDBuildUI.view.processes.instances.instance.CreateModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-create',

    data: {
        activity_action: {
            fieldname: null,
            value: null
        }
    },

    formulas: {
        hideSaveButton: {
            bind: '{theProcess}',
            get: function (theProcess) {
                var hideSaveButton = CMDBuildUI.util.helper.Configurations.get('cm_system_workflow_hideSaveButton');
                if (theProcess) {
                    return hideSaveButton || !theProcess.get("enableSaveButton");
                }
            }
        },
        popupTitle: {
            bind: {
                process: '{theProcess}',
                activity: '{theActivity._description_translation}'
            },
            get: function (data) {
                var vm = this.getParent().getParent();
                if (vm) {
                    this.set("titledata.item", Ext.String.format("&mdash; {0}", data.activity));
                }
            }
        }
    }

});