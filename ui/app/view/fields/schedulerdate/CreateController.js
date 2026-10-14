Ext.define('CMDBuildUI.view.fields.schedulerdate.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-create',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        //hides the save and close button
        const saveCloseButton = view.down('#saveandclosebtn');
        saveCloseButton.setHidden(true);

        //generates the form
        const vm = view.getViewModel();
        vm.bind('{theEvent}', function (theEvent) {
            view.generateForm();
        });

        //binds 'begin' and 'end' to the value of 'date' field
        vm.bind('{theEvent.date}', function (dateChange) {
            const theEvent = vm.get("theEvent");
            theEvent.set('begin', dateChange);
        }, view);
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const view = this.getView(),
            theEvent = this.getViewModel().get("theEvent");
        view.fireEventArgs('popupsave', [theEvent]);
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().fireEventArgs('popupclose');
    }

});
