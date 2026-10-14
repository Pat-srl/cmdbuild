Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.DefaultOrdersFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset',

    data: {
        attributesForSorting: null
    },

    formulas: {
        attributesManager: {
            bind: {
                prototype: '{theObject.prototype}'
            },
            get: function (data) {
                const me = this;
                const theObject = me.get('theObject');

                if (!theObject.phantom) {
                    const attributes = [];
                    theObject.getAttributes().then(function (_attributes) {
                        _attributes.each(function (attribute) {
                            if (attribute.canAdminShow()) {
                                attributes.push(CMDBuildUI.model.Attribute.create({
                                    _id: attribute.get('_id'),
                                    name: attribute.get('name'),
                                    description: attribute.get('description')
                                }));
                            }
                        });
                        if (data.prototype) {
                            attributes.push({
                                _id: 'IdClass',
                                name: 'IdClass',
                                description: CMDBuildUI.locales.Locales.common.grid.subtype
                            });
                        }
                        me.set('attributesForSorting', attributes);
                    });
                }
            }
        }
    },

    stores: {
        allAttributesForSorting: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: 'memory',
            data: '{attributesForSorting}',
            sorters: ['description'],
            autoDestroy: true
        }
    }
});