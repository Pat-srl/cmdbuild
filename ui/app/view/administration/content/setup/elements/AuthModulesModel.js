Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuthModulesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-authmodules',
    formulas: {
        modules: {
            get: function (data) {

                var modules = [{
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__default__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.default
                }, {
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__ldap__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.ldap
                }, {
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__rsa__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.rsa
                }, {
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__file__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.file
                }, {
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__header__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.header
                }, {
                    value: this.get('theSetup.org__DOT__cmdbuild__DOT__auth__DOT__customlogin__DOT__enabled') === 'true',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.customlogin
                }];
                
                return modules;
            }
        }
    },

    stores: {
        authModulesStore: {
            fields: ['value', 'label'],
            sorters: [{
                property: 'value',
                direction: 'DESC'
            }, {
                property: 'label',
                direction: 'ASC'
            }],   
            groupDir: 'DESC',         
            proxy: 'memory',
            data: '{modules}',
            groupField: 'value',
            autoDestroy: true
        }
    }

});