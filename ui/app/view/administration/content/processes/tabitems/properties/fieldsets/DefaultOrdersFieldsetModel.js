Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.DefaultOrdersFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',

    data: {
        attributesForSorting: null
    },

    formulas: {
        attributesManager: {
            bind: {
                prototype: '{theProcess.prototype}'
            },
            get: function (data) {
                const me = this;
                const theProcess = me.get('theProcess');
                if (!theProcess.phantom) {
                    const attributes = [];
                    theProcess.getAttributes().then(function (_attributes) {
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
                                description: 'Subtype'
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