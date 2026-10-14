Ext.define('CMDBuildUI.view.widgets.sequenceview.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-sequenceview-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.sequenceview.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel(),
            theWidget = vm.get('theWidget');
        // this type of widget does not support inline mode
        if (theWidget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }

        var modelname = 'CMDBuildUI.model.calendar.Sequence',
            model = Ext.ClassManager.get(modelname);

        var form = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            readonly: true,
            linkName: 'theSequence',
            layout: CMDBuildUI.model.calendar.Sequence.getFormLayout()
        });

        view.add(form);

        vm.linkTo("theSequence", {
            type: modelname,
            id: vm.get("theWidget.SequenceId")
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    }
});