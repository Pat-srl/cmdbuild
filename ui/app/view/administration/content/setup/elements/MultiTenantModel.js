Ext.define('CMDBuildUI.view.administration.content.setup.elements.MultiTenantModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-multitenant',

    formulas: {
        messageinfo: function(){
            return Ext.String.format(
                CMDBuildUI.locales.Locales.administration.systemconfig.multitenantinfomessage,
                '<a href="https://www.cmdbuild.org/en/documentation/manuals" target="blank">https://www.cmdbuild.org/en/documentation/manuals</a>'
            );
        }
    }
});
