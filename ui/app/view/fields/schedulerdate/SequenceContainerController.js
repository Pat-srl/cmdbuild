Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-sequencecontainer',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    onBeforeRender: function (view, eOpts) {
        // move this in the schedulerDateField
        this.getViewModel().bind(
            '{sequences}',
            function (sequences) {
                const items = [];

                sequences.getRange().forEach(function (sequence) {
                    const trigger = sequence.triggerRecord;
                    delete sequence.triggerRecord;

                    items.push({
                        xtype: 'fields-schedulerdate-sequencefieldset',
                        viewModel: {
                            data: {
                                theSequence: sequence,
                                theTrigger: trigger
                            }
                        }
                    })
                });

                view.add(items);
            });
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onSaveButtonClick: function (button, eOpts) {
        const view = this.getView();
        view.fireEvent('popupsave', view);
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onCancelButtonClick: function (button, eOpts) {
        const view = this.getView();
        view.fireEvent('popupclose', view);
    }
});
