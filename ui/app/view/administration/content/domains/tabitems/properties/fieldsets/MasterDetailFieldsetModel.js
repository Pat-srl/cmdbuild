Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset',

    data: {
        descriptionMasterDetailInputHidden: true,
        descriptionMasterDetailDisplayHidden: true
    },

    formulas: {
        masterDetailAggregateAttrsGridManager: {
            bind: {
                allDetailAttributes: '{allDetailAttributes}',
                allAggregateAttrs: '{theDomain.masterDetailAggregateAttrs}'
            },
            get: function (data) {
                const me = this;
                if (data.allDetailAttributes && data.allAggregateAttrs) {
                    Ext.Array.forEach(data.allAggregateAttrs, function (id) {
                        const record = me.get('allDetailAttributes').getById(id);
                        if (record) {
                            me.getStore('masterDetailAggregateAttrsStore').add(record);
                        }
                    });
                    me.get('newSelectedAttributesStore').add(CMDBuildUI.model.Attribute.create());
                }
            }
        },

        disabledAttributesGridManager: {
            bind: {
                allDetailAttributesStore: '{allDetailAttributesStore.complete}',
                allCardAttributesForDisabled: '{allCardAttributesForDisabled}',
                disabledAttributes: '{theDomain.masterDetailDisabledCreateAttrs}'
            },
            get: function (data) {
                const me = this;
                if (data.allDetailAttributesStore && data.disabledAttributes) {
                    Ext.Array.forEach(data.disabledAttributes, function (id) {
                        const record = me.get('allCardAttributesForDisabled').getById(id);
                        if (record) {
                            me.getStore('disabledAttributesStore').add(record);
                        }
                    });
                    if (!me.get('newDisabledAttributesStore').getRange().length) {
                        me.get('newDisabledAttributesStore').add(CMDBuildUI.model.Attribute.create());
                    }
                }
            }
        },

        allDetailAttributesProxy: {
            bind: {
                cardinality: '{theDomain.cardinality}',
                source: '{theDomain.source}',
                destination: '{theDomain.destination}',
                isMasterDetail: '{theDomain.isMasterDetail}'
            },
            get: function (data) {
                const me = this;
                if (
                    data.isMasterDetail &&
                    !Ext.isEmpty(data.cardinality) &&
                    !Ext.isEmpty(data.source) &&
                    !Ext.isEmpty(data.destination)
                ) {
                    me.set('allAttributeProxyAutoLoad', false);
                    const className = data.cardinality === '1:N' ? data.destination : data.source;
                    const objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(className);
                    Ext.asap(function () {
                        me.set('allAttributeProxyAutoLoad', true);
                    });
                    return {
                        url: Ext.String.format(
                            '/{0}/{1}/attributes',
                            Ext.util.Inflector.pluralize(objectType),
                            className
                        ),
                        type: 'baseproxy',
                        extraParams: {
                            limit: 0
                        }
                    };
                }
                return false;
            }
        },

        updateMasterDetail: {
            bind: {
                isMasterDetail: '{theDomain.isMasterDetail}',
                isView: '{actions.view}',
                isAdd: '{actions.add}',
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                if (data.isMasterDetail) {
                    if (data.isView) {
                        this.set('descriptionMasterDetailInputHidden', true);
                        this.set('descriptionMasterDetailDisplayHidden', false);
                    } else if (data.isEdit) {
                        this.set('descriptionMasterDetailInputHidden', false);
                        this.set('descriptionMasterDetailDisplayHidden', true);
                    } else {
                        this.set('descriptionMasterDetailInputHidden', false);
                        this.set('descriptionMasterDetailDisplayHidden', true);
                    }
                } else {
                    this.getParent().set('theMasterDetailTranslation', null);
                    this.set('theDomain.descriptionMasterDetail', '');
                    this.set('descriptionMasterDetailInputHidden', true);
                    this.set('descriptionMasterDetailDisplayHidden', true);
                }
            }
        }
    },

    stores: {
        masterDetailAggregateAttrsStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            sorters: ['description']
        },
        freeAttributeForAggregateStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            sorters: ['description']
        },
        newSelectedAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },
        allDetailAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: '{allDetailAttributesProxy}',
            autoLoad: '{allAttributeProxyAutoLoad}',
            autoDestroy: true,
            listeners: {
                datachanged: 'onAllDetailAttributesDatachanged'
            }
        },
        allCardAttributesForDisabled: {
            source: '{allDetailAttributesStore}'
        },
        freeDisabledAttributeStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            sorters: ['description']
        },
        disabledAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            sorters: ['description']
        },
        newDisabledAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        }
    }
});
