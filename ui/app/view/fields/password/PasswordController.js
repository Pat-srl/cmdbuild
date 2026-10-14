Ext.define('CMDBuildUI.view.fields.password.PasswordController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-password-field',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel(),
            config = view.getInitialConfig();
        if (!Ext.isEmpty(config.bind)) {
            var value = config.bind.value,
                has_value = "{" + view.getRecordLinkName() + "._" + view.name + "_has_value}";
            vm.bind(has_value, function (data) {
                if (data === true && !vm.get(value.substr(1, value.length - 2))) {
                    view.setEmptyText("•••••");
                }
            })
        }
    }

});