Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginModulesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-loginmodules',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        loginModulesData: {
            bind: '{theSetup}',
            get: function (theSetup) {
                var data = [];
                if (theSetup) {
                    var modules = theSetup.org__DOT__cmdbuild__DOT__auth__DOT__modules.split(',');
                    Ext.Array.forEach(modules, function (module) {
                        var startVith = new RegExp(Ext.String.format('^{0}{1}', 'org__DOT__cmdbuild__DOT__auth__DOT__module__DOT__', module));
                        Ext.Array.forEach(Ext.Object.getAllKeys(theSetup), function (key) {
                            if (startVith.test(key)) {
                                var value = theSetup[key];
                                var description = key.replace(new RegExp(Ext.String.format('^{0}{1}__DOT__', 'org__DOT__cmdbuild__DOT__auth__DOT__module__DOT__', module)), "").replace(/__DOT__/g, ".");
                                if(/data.image/g.test(value)){                                    
                                    value = Ext.String.format('<img height="16px" width="16px" src="{0}" />', value);
                                }
                                data.push({
                                    key: key.replace(/__DOT__/g, "."),
                                    value: value,
                                    type: module,
                                    description: description
                                });
                            }
                        });

                    });
                }
                return data;
            }
        }
    },
    stores: {
        loginModulesStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            groupField: 'type',
            sorters: ['key'],
            proxy: 'memory',
            data: '{loginModulesData}'
        }
    }

});