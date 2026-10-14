Ext.define('CMDBuildUI.view.administration.content.localizations.imports.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-imports-view',

    control: {
        '#cancelBtn': {
            click: 'onCancelButtonClick'
        },
        '#importBtn': {
            click: 'onImportButtonClick'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, event, eopts) {
        this.getView().up().close();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onImportButtonClick: function (button, event, eopts) {
        const me = this;
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);
        const inputFile = me.getView().down('#addfileattachment').extractFileInput();
        const separatorCombobox = me.getView().down('#localizationImportSeparator');
        const url = Ext.String.format(
            '{0}/translations/import?separator={1}',
            CMDBuildUI.util.Config.baseUrl,
            separatorCombobox.getValue()
        );
        CMDBuildUI.util.administration.File.upload('POST', new FormData(), inputFile, url, function (success, error) {
            CMDBuildUI.util.Utilities.showLoader(false);
            if (button) {
                button.setDisabled(false);
            }
            if (success) {
                me.getView().up().close();
                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                    'administration-content-localizations-localization-view',
                    {
                        viewModel: {
                            data: {
                                actions: {
                                    view: true,
                                    edit: false,
                                    add: false
                                }
                            }
                        }
                    }
                );
            }
        });
    }
});
