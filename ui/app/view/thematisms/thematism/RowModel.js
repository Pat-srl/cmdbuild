Ext.define('CMDBuildUI.view.thematisms.thematism.RowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.thematisms-thematism-row',

    data: {
        hiddenfields: { attributecombo: true, functioncombo: true },
        values: {
            geoattribute: null,
            attributecombo: null,
            name: null,
            segments: null,
            analisysType: null,
            source: null,
            functioncombo: null
        }
    },

    formulas: {
        updateAttributesData: {
            bind: { objecttype: '{objectType}', objecttypename: '{objectTypeName}' },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    const me = this;
                    CMDBuildUI.util.helper.ModelHelper.getModel(data.objecttype, data.objecttypename).then(
                        function (model) {
                            const d = [];
                            model.getFields().forEach(function (field) {
                                const cmdbuildType = field.cmdbuildtype;
                                if (
                                    !Ext.String.startsWith(field.name, '_') &&
                                    cmdbuildType !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula &&
                                    cmdbuildType !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                                ) {
                                    //only visible fields
                                    d.push({
                                        value: field.name,
                                        label: field.attributeconf.description_localized,
                                        cmdbuildtype: cmdbuildType
                                    });
                                }
                            });
                            me.set('attributesstoredata', d);
                        }
                    );
                }
            }
        },

        updateAttributesDataFilter: {
            bind: { attributesstore: '{attributesstore}', analysisType: '{theThematism.analysistype}' },
            get: function (data) {
                if (data.attributesstore && data.analysisType) {
                    switch (data.analysisType) {
                        case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                            data.attributesstore.removeFilter('intervals_filter');
                            break;
                        case CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals:
                            filter = {
                                id: 'intervals_filter',
                                property: 'cmdbuildtype',
                                operator: 'in',
                                value: [
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime
                                ]
                            };
                            data.attributesstore.addFilter(filter);
                            break;
                    }

                    //This piece of code is responsable for erasing the attribute selection if is not correct type for the analysistype.
                    //Should be done automatically but the proneremoved on the store doesn't work.
                    //TODO: find more indipendend way to achieve this (tried listener on the store, and overriding the proneremoved configuratio )

                    const attribute = this.get('theThematism.classattribute');
                    /**
                     * This part erases attribute selection if no more valid
                     */
                    if (
                        data.attributesstore.findBy(function (element) {
                            return element.get('value') == attribute;
                        }) == -1
                    ) {
                        this.get('theThematism').set('classattribute', null);
                    }
                }
            }
        },

        updateGeoAttributesData: {
            bind: { objectType: '{objectType}', objectTypeName: '{objectTypeName}' },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            const objectTypeNameInstance = CMDBuildUI.util.helper.ModelHelper.getClassFromName(
                                data.objectTypeName
                            );
                            if (objectTypeNameInstance) {
                                objectTypeNameInstance.getGeoAttributes().then(
                                    function (geoattributes) {
                                        if (this.getView() && !this.getView().destroyed) {
                                            const d = [];
                                            geoattributes.getRange().forEach(function (item, index, array) {
                                                if (
                                                    item.get('owner_type') == data.objectTypeName &&
                                                    item.get('type') == CMDBuildUI.model.map.GeoAttribute.type.geometry
                                                ) {
                                                    d.push({ label: item.get('text'), value: item.get('name') });
                                                }
                                            }, this);
                                            this.set('geoattributestoredata', d);
                                        }
                                    },
                                    Ext.emptyFn,
                                    Ext.emptyFn,
                                    this
                                );
                            }
                            break;
                        default:
                            CMDBuildUI.util.Logger.log(
                                Ext.String.format('Object Type not implemented: {0}', data.objectType),
                                CMDBuildUI.util.Logger.levels.debug
                            );
                    }
                }
            }
        },

        updateFunctionData: {
            bind: { objecttype: '{objectType}', objecttypename: '{objectTypeName}' },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    const me = this;
                    const functionFilter = Ext.JSON.encode({
                        Attribute: {
                            and: [
                                { simple: { attribute: 'tags', operator: 'CONTAIN', value: 'card2value' } },
                                { simple: { attribute: 'source', operator: 'CONTAIN', value: data.objecttypename } }
                            ]
                        }
                    });
                    Ext.Ajax.request(
                        {
                            url: CMDBuildUI.util.Config.baseUrl + '/functions',
                            method: 'GET',
                            params: { filter: functionFilter },
                            success: function (response) {
                                //TODO: handle the case in wich there are not function for this class
                                const datas = JSON.parse(response.responseText).data;
                                const d = [];

                                datas.forEach(function (data) {
                                    d.push({ label: data.name, value: data.name });
                                });
                                me.set('functionstoredata', d);
                            },
                            error: function (response) {}
                        },
                        this
                    );
                }
            }
        },

        updateSourceDeps: {
            bind: '{theThematism.type}',
            get: function (source) {
                if (source === CMDBuildUI.model.thematisms.Thematism.sources.function) {
                    this.set('hiddenfields.attributecombo', true);
                    this.set('hiddenfields.functioncombo', false);
                } else if (source === CMDBuildUI.model.thematisms.Thematism.sources.table) {
                    this.set('hiddenfields.attributecombo', false);
                    this.set('hiddenfields.functioncombo', true);
                }
            }
        },

        segmentsHidden: {
            bind: { bindTo: '{theThematism.analysistype}' },
            get: function (analysistypes) {
                const intervals = analysistypes == CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals;
                this.set('options.hidden', intervals ? false : true);
                return !intervals;
            }
        },

        buttonsDisabled: {
            bind: {
                name: '{theThematism.name}',
                analisysType: '{theThematism.analysistype}',
                source: '{theThematism.type}',
                geoAttribute: '{theThematism.attribute}',
                attributeCombo: '{theThematism.classattribute}',
                functioncombo: '{theThematism.function}',
                segments: '{theThematism.segments}'
            },
            get: function (data) {
                const vmPanel = this.getView().lookupViewModel('thematisms-panel');
                if (
                    ((data.source == CMDBuildUI.model.thematisms.Thematism.sources.table && data.attributeCombo) ||
                        (data.source == CMDBuildUI.model.thematisms.Thematism.sources.function &&
                            data.functioncombo)) &&
                    data.analisysType &&
                    data.geoAttribute &&
                    data.name
                ) {
                    if (data.analisysType == CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals) {
                        if (data.segments != null) {
                            vmPanel.set('buttonsDisabled', false);
                            return;
                        }
                    } else if (data.analisysType == CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual) {
                        vmPanel.set('buttonsDisabled', false);
                        return;
                    }
                }
                vmPanel.set('buttonsDisabled', true);
            }
        },

        analysistypesdata: {
            get: function () {
                return [
                    {
                        label: CMDBuildUI.locales.Locales.thematism.intervals, // 'intervals'
                        value: CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals
                    },
                    {
                        label: CMDBuildUI.locales.Locales.thematism.punctual, // 'punctual',
                        value: CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual
                    }
                ];
            }
        },

        sourcesdata: {
            get: function () {
                return [
                    {
                        label: CMDBuildUI.locales.Locales.thematism.table, // 'Table'
                        value: CMDBuildUI.model.thematisms.Thematism.sources.table
                    },
                    {
                        label: CMDBuildUI.locales.Locales.thematism.function, // 'Function',
                        value: CMDBuildUI.model.thematisms.Thematism.sources.function
                    }
                ];
            }
        }
    },

    stores: {
        analysistypes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: { type: 'memory' },
            data: '{analysistypesdata}'
        },

        sources: { model: 'CMDBuildUI.model.base.ComboItem', proxy: { type: 'memory' }, data: '{sourcesdata}' },

        attributesstore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: { type: 'memory' },
            data: '{attributesstoredata}'
        },

        geoAttributes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: { type: 'memory' },
            data: '{geoattributestoredata}'
        },

        functionstore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: { type: 'memory' },
            data: '{functionstoredata}'
        }
    }
});
