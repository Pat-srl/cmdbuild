Ext.define('CMDBuildUI.view.processes.instances.instance.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-edit',

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
                instance: '{theObject.Description}',
                activity: '{theActivity._description_translation}'
            },
            get: function (data) {
                var vm = this.getParent().getParent();
                if (vm) {
                    this.set("titledata.item",
                        Ext.String.format("{0} &mdash; {1}",
                            CMDBuildUI.util.helper.FieldsHelper.renderTextField(data.instance, {
                                skipnewline: true
                            }),
                            data.activity
                        )
                    );
                }
            }
        }
    }

});