Ext.define('CMDBuildUI.view.mobile.config.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.mobile-config-panel',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#devicename': {
            change: 'onDeviceNameChange'
        },
        '#regeneratebtn': {
            click: 'onRegenerateBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * Initialize variables.
     *
     * @param {CMDBuildUI.view.mobile.config.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var me = this;
        view.lookupViewModel().set('values', {
            serverurl: CMDBuildUI.util.Config.baseUrl.replace('services/rest/v4', ''),
            customercode: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.customercode),
            devicename:
                (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.devicenameprefix) ||
                    '') + CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get('username')
        });

        Ext.Loader.loadScript({
            url: ['resources/js/qrcode/qrcode.js'],
            onLoad: function () {
                me.generateQRCode();
            }
        });
    },

    /**
     * Fired on Device name field change.
     *
     * @param {Ext.form.field.Text} field
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     */
    onDeviceNameChange: function (field, newValue, oldValue, eOpts) {
        if (!Ext.isEmpty(oldValue)) {
            field.lookupViewModel().set('regeneratebtn.disabled', false);
        }
    },

    /**
     * Fired on Regenerate button click.
     *
     * @param {Ext.button.Button} btn
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onRegenerateBtnClick: function (btn, event, eOpts) {
        this.generateQRCode();
        btn.lookupViewModel().set('regeneratebtn.disabled', true);
    },

    /**
     * Fired on Regenerate button click.
     *
     * @param {Ext.button.Button} btn
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (btn, event, eOpts) {
        this.getView().up('panel').close();
    },

    privates: {
        /**
         * Generates QRCode with mobile data
         */
        generateQRCode: function () {
            var view = this.getView(),
                vm = view.lookupViewModel(),
                canvas = view.down('#qrcode').el,
                text = Ext.String.format(
                    'su: {0}\ncc: {1}\ndn: {2}',
                    vm.get('values.serverurl'),
                    vm.get('values.customercode'),
                    vm.get('values.devicename')
                );
            QRCode.toCanvas(canvas.dom, text, { margin: 0 });
        }
    }
});
