Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-card',

    data: {
        theTranslation: null
    },

    formulas: {
        formModeCls: {
            bind: '{actions}',
            get: function (actions) {
                if (actions.edit) {
                    return 'formmode-edit';
                } else if (actions.add) {
                    return 'formmode-add';
                } else {
                    return 'formmode-view';
                }
            }
        },

        iconTypes: {
            get: function () {
                return CMDBuildUI.model.lookups.Lookup.getIconTypes();
            }
        },

        manageIconType: {
            bind: '{theValue.icon_type}',
            get: function (iconType) {
                this.set(
                    'iconTypeIsImage',
                    iconType && iconType === CMDBuildUI.model.lookups.Lookup.icontypes.image ? true : false
                );
                this.set('iconTypeIsFont', iconType && iconType === 'font' ? true : false);
            }
        },

        panelTitle: {
            bind: {
                description: '{theValue.description}',
                lookupType: '{theLookupType}'
            },
            get: function (data) {
                let title;
                if (data.description) {
                    title = Ext.String.format('{0} - {1}', data.lookupType.get('description'), data.description);
                } else {
                    title = Ext.String.format('{0}', data.lookupType.get('description'));
                }
                this.getParent().set('title', title);
            }
        }
    },

    stores: {
        iconTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{iconTypes}'
        }
    }
});
