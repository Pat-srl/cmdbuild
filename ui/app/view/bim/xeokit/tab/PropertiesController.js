Ext.define('CMDBuildUI.view.bim.xeokit.tab.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-tab-properties',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
    * 
    * @param {CMDBuildUI.view.bim.xeokit.tab.Properties} view 
    * @param {Object} eOpts 
    */
    onBeforeRender: function (view, eOpts) {
        var vm = view.getViewModel();
        vm.bind({
            entity: '{entity}'
        }, function (data) {
            var items = [],
                propertiesItem = [],
                fieldset = [],
                entity = data.entity,
                element = Ext.Array.findBy(Ext.Object.getValues(entity.scene.viewer.metaScene.metaObjects), function (item, index) {
                    return entity.id === item.id;
                }),
                formfield = CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                valuesXYZ = entity.aabb;

            if (element.name) {
                items.push(Ext.apply({
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.name,
                    value: element.name
                }, formfield));
            }

            if (element.type) {
                var value = element.type;
                if (Ext.String.startsWith(value, "ifc", true)) {
                    value = value.slice(3);
                }
                value = value.replace(/([a-z])([A-Z])/g, "$1 $2")
                items.push(Ext.apply({
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.type,
                    value: value
                }, formfield));
            }

            if (element.id) {
                items.push(Ext.apply({
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.guid,
                    value: element.id
                }, formfield));
            }

            fieldset.push({
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.attributes.nogroup,
                collapsible: true,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                items: items
            });


            propertiesItem.push(Ext.apply({
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.BOUNDING_BOX_SIZE_ALONG_X,
                value: valuesXYZ[3] - valuesXYZ[0]
            }, formfield));

            propertiesItem.push(Ext.apply({
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.BOUNDING_BOX_SIZE_ALONG_Y,
                value: valuesXYZ[4] - valuesXYZ[1]
            }, formfield));

            propertiesItem.push(Ext.apply({
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.BOUNDING_BOX_SIZE_ALONG_Z,
                value: valuesXYZ[5] - valuesXYZ[2]
            }, formfield));

            fieldset.push({
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.bim.ifcproperties.calculated,
                collapsible: true,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                items: propertiesItem
            });


            view.removeAll();
            view.add(fieldset);
        });
    }

});