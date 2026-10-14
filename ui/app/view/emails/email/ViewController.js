Ext.define('CMDBuildUI.view.emails.email.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.emails.email.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            email = vm.get('theEmail');
        if (email && email.get('status') === CMDBuildUI.model.emails.Email.statuses.received && !email.get('isReadByUser')) {
            email.set('isReadByUser', true);
            email.save();
        }
        // card attribute is evaluated on details load
        // then check for its value to know if the email
        // is already loaded or not
        if (email && !(email.phantom || email.get('card'))) {
            email.load({
                callback: function (record, operation, success) {
                    if (vm && !vm.destroyed) {
                        vm.set('emailloaded', true);
                    }
                }
            });
        } else {
            vm.set('emailloaded', true);
        }
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Object} eOpts
     */
    onAttachmentsDatachanged: function (store, eOpts) {
        this.getViewModel().set('attachmentsTotalCount', store.getCount());
    }

});