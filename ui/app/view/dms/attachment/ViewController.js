Ext.define('CMDBuildUI.view.dms.attachment.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.dms.attachment.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();

        vm.bind(
            {
                DMSClass: '{DMSClass}',
                DMSModelClass: '{DMSModelClass}'
            },
            function (data) {
                if (data.DMSModelClass && data.DMSClass) {
                    const linkName = 'record';
                    CMDBuildUI.util.helper.FormHelper.renderFormForType(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                        data.DMSClass.getId(),
                        {
                            mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                            linkName: linkName,
                            showAsFieldsets: true,
                            readonly: true
                        }
                    ).then(function (items) {
                        var formView = view.down('#formpanel');

                        formView.removeAll(true);
                        formView.add(view.getFormItems(items, linkName));
                    });
                }
            }
        );
    }
});
