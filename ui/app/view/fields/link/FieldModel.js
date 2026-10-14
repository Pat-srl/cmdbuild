Ext.define('CMDBuildUI.view.fields.link.FieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-link-field',

    data: {
        linkfield: {
            urlvalue: null,
            labelvalue: null
        }
    },

    formulas: {
        updateValue: {
            bind: {
                url: '{linkfield.urlvalue}',
                label: '{linkfield.labelvalue}'
            },
            get: function(data) {
                var view = this.getView();
                view.setValue(
                    CMDBuildUI.util.Utilities.createLinkByUrlAndLabel(
                        data.url,
                        data.label
                    )
                );

                // update data url and label on the target object
                if (view._bindurl && view._bindlabel) {
                    this.set(view._bindurl, data.url);
                    this.set(view._bindlabel, data.label);
                }
            }
        }
    }

});
