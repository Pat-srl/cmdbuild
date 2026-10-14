Ext.define('CMDBuildUI.view.administration.content.setup.elements.AiController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-ai',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },
    onBeforeRender: function () {
        const view = this.getView();
        view.up('administration-content')
            .getViewModel()
            .set(
                'title',
                Ext.String.format(
                    '{0} - {1}',
                    CMDBuildUI.locales.Locales.administration.navigation.ai,
                    CMDBuildUI.locales.Locales.administration.navigation.settings
                )
            );
    }
});
