Ext.define('CMDBuildUI.view.processes.instances.instance.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.ViewController',
        'CMDBuildUI.view.processes.instances.instance.ViewModel'
    ],

    mixins: ['CMDBuildUI.view.processes.instances.instance.Mixin', 'CMDBuildUI.mixins.forms.FormTriggers'],

    alias: 'widget.processes-instances-instance-view',
    controller: 'processes-instances-instance-view',
    viewModel: {
        type: 'processes-instances-instance-view'
    },

    config: {
        buttons: null,
        objectTypeName: null,
        objectId: null,
        activityId: null,
        shownInPopup: false,
        hideTools: false
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    bind: {
        title: '{title}'
    },

    tabpaneltools: CMDBuildUI.view.processes.instances.Util.getTools(),

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

                // generate tabs/fieldsets and fields
                let items = [];

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

                const showOnlyAttributesInLayout =
                    activity.get('_definition') === 'DUMMY_TASK_FOR_CLOSED_PROCESS' && layout ? true : false;

                if (me.getShownInPopup()) {
                    // get form fields as fieldsets
                    const formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get('objectModel'), {
                        mode: me.formmode,
                        attributesOverrides: attrsConf.overrides,
                        visibleAttributes: attrsConf.visibleAttributes,
                        showAsFieldsets: true,
                        grouping: grouping,
                        layout: layout,
                        activityLinkName: 'theActivity',
                        showOnlyAttributesInLayout: showOnlyAttributesInLayout
                    });

                    // create items
                    items = [
                        me.getProcessStatusBar(),
                        {
                            xtype: 'toolbar',
                            cls: 'fieldset-toolbar',
                            items: me.getCurrentActivityInfo(me.hideTools),
                            margin: 0
                        },
                        me.getMainPanelForm(formitems)
                    ];
                } else {
                    // get form fields as tab panel
                    const panel = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get('objectModel'), {
                        mode: me.formmode,
                        showAsFieldsets: false,
                        attributesOverrides: attrsConf.overrides,
                        visibleAttributes: attrsConf.visibleAttributes,
                        grouping: grouping,
                        layout: layout,
                        activityLinkName: 'theActivity',
                        showOnlyAttributesInLayout: showOnlyAttributesInLayout
                    });
                    Ext.apply(panel, {
                        tools: me.tabpaneltools
                    });
                    items.push(panel);
                }
                me.setHtml();
                me.add(items);

                if (me.loadmask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(me.loadmask);
                }
            }
        );
    }
});
