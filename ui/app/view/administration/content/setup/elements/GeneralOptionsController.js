Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-generaloptions',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#openSubclassCheckBox': {
            change: 'onOpenSubclassClick'
        },
        'iconFile': {
            change: 'onIconFileChange'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content')
            .getViewModel()
            .set('title', CMDBuildUI.locales.Locales.administration.navigation.generaloptions);
        var vm = view.up('administration-content-setup-view').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);

        /**
         * validate the field after the value changes
         */
        this.getViewModel().bind(
            {
                value: '{inactiveusers_value}'
            },
            function (data) {
                Ext.asap(function () {
                    if (!view.destroyed) {
                        view.lookupReference('inactiveusers').validate(data.value);
                    }
                }, this);
            },
            this
        );
    },
    onOpenSubclassClick: function (checkbox, newvalue, oldvalue, eOpts) {
        if (!newvalue) {
            this.getViewModel().set(
                'theSetup.org__DOT__cmdbuild__DOT__ui__DOT__redirectTo__DOT__view__DOT__enabled',
                false
            );
        }
    },

    onIconFileChange: function (field, value) {
        if (field.fileInputEl.dom.files.length > 0) {
            var file = field.fileInputEl.dom.files[0].name;
            const fileExtension = file.split('.').pop().toLowerCase();
            if (!file.includes(field.accept)) {
                field.extractFileInput();
                const view = this.getView().down('previewimage');
                view.setHidden(true);
                CMDBuildUI.util.Msg.alert(
                    'Error',
                    Ext.String.format(CMDBuildUI.locales.Locales.attachments.wrongfileextension, fileExtension)
                );
            }
        }
    }
});
