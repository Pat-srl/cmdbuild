Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.VisibilityTree', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-visibilitytree',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            itemId: 'geoattributevisibilitytree',
            xtype: 'treepanel',
            rootVisible: false,
            bind: {
                store: '{gridStore}'
            },
            cls: 'nogridborder',
            ui: 'administration-navigation-tree',
            header: false,
            listeners: {
                beforecheckchange: {
                    fn: function (node, value, element, eOpts) {
                        if (node.get('disabled')) {
                            return false;
                        }
                        return !this.lookupViewModel().get('actions.view');
                    }
                },
                checkchange: {
                    fn: function (node, value, element, eOpts) {
                        const vm = this.lookupViewModel();
                        const visibility = vm.get('theGeoAttribute.visibility');
                        if (value) {
                            visibility[node.get('objecttype')] = true;
                        } else {
                            delete visibility[node.get('objecttype')];
                        }
                        vm.get('theGeoAttribute').set('visibility', visibility);
                    }
                }
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    dataIndex: 'text',
                    flex: 1,
                    readOnly: true
                }
            ]
        }
    ]
});
