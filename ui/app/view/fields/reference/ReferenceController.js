Ext.define('CMDBuildUI.view.fields.reference.ReferenceController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-referencefield',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view, eOpts) {
        var me = this;
        var domain = Ext.getStore("domains.Domains").getById(view.metadata.domain);
        if (domain) {
            domain.getAttributes().then(function (attributes) {
                me.addRelationAttibutes(attributes.getRange());
            });
        }
    },

    privates: {
        addRelationAttibutes: function (attributes) {
            if (!Ext.isEmpty(attributes)) {
                var view = this.getView(),
                    fields = [];

                attributes.forEach(function (attribute) {
                    if (attribute.get("showInGrid")) { //TODO: check if is rigth attribute
                        var attr_data = attribute.getData();
                        attr_data.cmdbuildtype = attr_data.type;
                        attr_data.name = Ext.String.format(
                            "_{0}_attr_{1}",
                            view.getInitialConfig().name,
                            attr_data.name
                        );
                        attr_data.attributeconf = Ext.merge({}, attr_data);
                        var field = CMDBuildUI.util.helper.FormHelper.getFormField(attr_data, {
                            mode: view.formmode
                        });
                        fields.push(field);
                    }
                });

                if (!Ext.isEmpty(fields)) {
                    view.add({
                        xtype: 'formpaginationfieldset',
                        title: CMDBuildUI.locales.Locales.relations.relationdata,
                        collapsible: true,
                        items: fields
                    });
                }
            }
        }
    }
});
