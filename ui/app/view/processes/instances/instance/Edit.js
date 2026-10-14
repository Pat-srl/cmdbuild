
Ext.define('CMDBuildUI.view.processes.instances.instance.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.EditController',
        'CMDBuildUI.view.processes.instances.instance.EditModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin',
        'CMDBuildUI.mixins.forms.FormTriggers'
    ],

    alias: 'widget.processes-instances-instance-edit',
    controller: 'processes-instances-instance-edit',
    viewModel: {
        type: 'processes-instances-instance-edit'
    },

    modelValidation: true,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    tabpaneltools: [CMDBuildUI.view.processes.instances.Util.getHelpTool()],

    /**
     * Render form fields
     *
     * @param {CMDBuildUI.model.processes.Instance} model
     */
    showForm: function () {
        var me = this;
        var vm = this.getViewModel();

        vm.bind({
            bindTo: '{theObject}'
        }, function (object) {
            var activity = vm.get("theActivity");

            function redirectToView() {
                if (activity) {
                    var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                        object.get("_type"),
                        object.getId(),
                        vm.get("theActivity").getId(),
                        'view'
                    );

                    CMDBuildUI.util.Utilities.redirectTo(url);
                } else {
                    var url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                        object.get("_type"),
                        object.getId(),
                        'view'
                    );

                    CMDBuildUI.util.Utilities.redirectTo(url);
                }
            }

            // check edit permission
            if (activity && vm.get("theActivity").get("writable")) {
                object.addLock().then(function (success) {
                    if (success) {
                        me._isLocked = true;
                        // attributes configuration from activity
                        var attrsConf = me.getAttributesConfigFromActivity();

                        // message panel
                        var message_panel = me.getMessageBox();

                        // action combobox
                        var action_field = me.getActionField();

                        var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"));
                        var grouping = process.attributeGroups().getRange();
                        var activity = vm.get("theActivity");
                        var layout;
                        if (activity.get("formStructure") && activity.get("formStructure").active && !Ext.isEmpty(activity.get("formStructure").form)) {
                            layout = activity.get("formStructure").form;
                        }

                        // get form fields as fieldsets
                        var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                            mode: me.formmode,
                            showAsFieldsets: true,
                            attributesOverrides: attrsConf.overrides,
                            visibleAttributes: attrsConf.visibleAttributes,
                            grouping: grouping,
                            layout: layout,
                            activityLinkName: 'theActivity',
                            formAutoValue: process.get('autoValue')
                        });

                        // add action_field as first element in form items
                        Ext.Array.insert(formitems, 0, [message_panel, action_field]);
                        // create items
                        var items = [
                            me.getProcessStatusBar(),
                            me.getMainPanelForm(formitems)
                        ];

                        me.setHtml();
                        me.add(items);

                        // validate form before edit
                        Ext.asap(function () {
                            if (me && !me.destroyed) {
                                me.isValid();
                            }
                        });
                    } else {
                        redirectToView();
                    }
                });
            } else {
                redirectToView();
            }
        });
    }
});
