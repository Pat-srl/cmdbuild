Ext.define('CMDBuildUI.view.processes.instances.instance.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.CreateController',
        'CMDBuildUI.view.processes.instances.instance.CreateModel'
    ],

    mixins: ['CMDBuildUI.view.processes.instances.instance.Mixin', 'CMDBuildUI.mixins.forms.FormTriggers'],

    alias: 'widget.processes-instances-instance-create',
    controller: 'processes-instances-instance-create',
    viewModel: {
        type: 'processes-instances-instance-create'
    },

    modelValidation: true,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,

    tabpaneltools: [CMDBuildUI.view.processes.instances.Util.getHelpTool()],

    /**
     * Load activity data and display form.
     * Overrides Mixin loadActivity
     *
     * @param {CMDBuildUI.model.processes.Instance} model
     */
    loadActivity: function (model) {
        const vm = this.getViewModel();
        const activitiesStore = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.processes.Activity',
            autoLoad: false,
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Processes.getStartActivitiesUrl(vm.get('objectTypeName'))
            }
        });

        // load activity and save variables in ViewModel
        activitiesStore.load({
            scope: this,
            callback: function (records, operation, success) {
                if (success && records && records.length) {
                    vm.set('activityId', records[0].getId());
                    vm.set('theActivity', records[0]);
                    // get the process definition
                    const processes = Ext.getStore('processes.Processes');
                    const theProcess = processes.getById(vm.get('objectTypeName'));
                    vm.set('theProcess', theProcess);
                    vm.set(
                        'help.text',
                        !Ext.isEmpty(records[0].get('_instructions_translation'))
                            ? records[0].get('_instructions_translation')
                            : theProcess.get('_help_translation') || theProcess.get('help')
                    );
                    // render form
                    this.showForm();
                }
            }
        });
    },

    /**
     * Render form fields
     */
    showForm: function () {
        const me = this;
        const vm = this.getViewModel();

        vm.bind(
            {
                bindTo: '{theObject}'
            },
            function (theObject) {
                // attributes configuration from activity
                const attrsConf = me.getAttributesConfigFromActivity();

                // message panel
                const message_panel = me.getMessageBox();

                // action combobox
                const action_field = me.getActionField();

                const process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    vm.get('objectTypeName'),
                    vm.get('objectType')
                );
                const grouping = process.attributeGroups().getRange();
                const activity = vm.get('theActivity');

                let layout;
                if (
                    activity.get('formStructure') &&
                    activity.get('formStructure').active &&
                    !Ext.isEmpty(activity.get('formStructure').form)
                ) {
                    layout = activity.get('formStructure').form;
                }

                // get form fields as fieldsets
                const formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get('objectModel'), {
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
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

                me.setHtml();
                me.add(me.getMainPanelForm(formitems));

                // validate form before edit
                Ext.asap(function () {
                    me.isValid();
                });
            }
        );
    }
});
