Ext.define('CMDBuildUI.view.administration.content.dashboards.common.mixin.DataSourceControllerMixin', {
    extend: 'Ext.Mixin',

    mixinConfig: {
        id: 'dataSourceController'
    },

    // ======================================================
    // STORE LISTENERS
    // ======================================================

    onFunctionAttributesStoreLoad: function () {
        this.refreshDataSourceFieldsDisplay();
    },

    // ======================================================
    // ON FIELD CHANGE LISTENERS
    // ======================================================

    /**
     * Called when the data source function of the main component changes.
     * @param {Ext.form.field.Field} combo
     * @param {String} newValue
     * @param {String} oldValue
     */
    onFunctionChange: function (combo, newValue, oldValue) {
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            owner = vm.get('owner'),
            ownerVm = owner.getViewModel(),
            theDashboard = ownerVm.get('theDashboard'),
            theComponent = vm.get('theComponent'),
            parameters = Ext.copy(theComponent.dataSourceParameters().getData()),
            type = theComponent.get('type');

        if (
            type === 'tile' &&
            theComponent.get('dataSourceType') !== CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion
        ) {
            theComponent.set('dataSourceType', CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion);
        }

        if (type === 'tile') {
            theComponent.set('valueField', null);
        } else if (type === 'list') {
            me.resetDataSourceDependentFields(theComponent);
        }

        combo.forceSelection = true;
        if (combo.getStore().findExact('name', newValue) > -1) {
            if (oldValue) {
                if (parameters.length) {
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.administration.common.messages.attention,
                        CMDBuildUI.locales.Locales.administration.dashboards.deletecomponentparametertranslations,
                        function (btnText) {
                            me.undoParametersLocalizations(
                                theDashboard,
                                theComponent,
                                parameters,
                                vm,
                                btnText === 'yes'
                            );
                            theComponent.dataSourceParameters().removeAll();
                            me.generateParameterBySourceType(newValue);
                        },
                        me
                    );
                } else {
                    theComponent.dataSourceParameters().removeAll();
                    me.generateParameterBySourceType(newValue);
                }
            } else {
                me.generateParameterBySourceType(newValue);
            }
        }
    },

    /**
     * Called when the data source (class, process, view or function) of the main component changes.
     * @param {Ext.form.field.Field} field
     * @param {String} newName
     * @param {String} oldValue
     */
    onDataSourceChange: function (field, newName, oldValue) {
        const me = this;
        const vm = me.getViewModel();
        const store = vm.getStore('functionAttributesStore');
        const theComponent = vm.get('theComponent');

        if (!theComponent) return;

        if (!newName) {
            const hadPreviousValue = !Ext.isEmpty(this.lastDataSourceName);
            this.lastDataSourceName = newName;
            if (store) store.removeAll();
            if (hadPreviousValue) {
                me.resetDataSourceDependentFields(theComponent, { includeListSearchFilter: true });
            }
            return;
        }

        const dataSourceType = theComponent.get('dataSourceType');
        const isSameSource = Ext.isEmpty(this.lastDataSourceName) || this.lastDataSourceName === newName;

        this.lastDataSourceName = newName;
        this.loadDataSourceAttributes(newName, dataSourceType);

        if (isSameSource) return;

        me.resetDataSourceDependentFields(theComponent);
    },

    /**
     * Called when the data source type of the main component changes (function/class/view).
     * @param {Ext.form.field.Field} field
     * @param {String} newType
     * @param {String} oldValue
     */
    onSourceTypeChange: function (field, newType, oldValue) {
        if (Ext.isEmpty(this.lastDataSourceType) || this.lastDataSourceType === newType) {
            this.lastDataSourceType = newType;
            return;
        }
        this.lastDataSourceType = newType;

        const vm = this.getViewModel();
        const theComponent = vm.get('theComponent');
        if (!theComponent) return;

        theComponent.set('dataSourceName', null);
        theComponent.dataSourceParameters().removeAll();
        this.resetDataSourceDependentFields(theComponent, { includeListSearchFilter: true });
    },

    privates: {
        /**
         * Clears the fields that depend on the selected data source, since they
         * no longer make sense once the source (or its type) changes.
         * @param {CMDBuildUI.model.dashboards.Component} theComponent
         * @param {Object} [options]
         * @param {Boolean} [options.includeListSearchFilter=false]
         */
        resetDataSourceDependentFields: function (theComponent, options) {
            options = options || {};
            var fields = {
                titleField: null,
                descriptionField: null,
                metaField: null,
                tagField: null,
                seeAllElements: null,
                linkToClass: null,
                dataSourceFilter: null
            };
            if (options.includeListSearchFilter) {
                fields.listSearchFilter = null;
            }
            theComponent.set(fields);
        },

        generateFunctionParameterFieldsets: function (newValue) {
            const me = this;
            const vm = me.getViewModel();
            const theComponent = vm.get('theComponent');
            const container = me.getView().down('#functionparameterscontiner');
            const globalParameters = me.getGlobalParameters();
            container.removeAll();

            if (newValue) {
                vm.get('functionsStore')
                    .model.load(newValue)
                    .getParameters()
                    .then(function (parameters) {
                        // remove parameters not belonging to the selected function
                        const dataSourceParameters = theComponent.dataSourceParameters();
                        dataSourceParameters.remove(
                            dataSourceParameters.getRange().filter(function (dataSourceParameter) {
                                return parameters.findExact('name', dataSourceParameter.get('name')) === -1;
                            })
                        );
                        parameters.each(function (parameter, index) {
                            me.createDataSourceIfNotExist(theComponent, parameter);
                            const theParameter = me.getParameter(parameter.get('name'));
                            const parameterType = CMDBuildUI.model.dashboards.DataSourceParameter.normalizeType(
                                theParameter.get('type')
                            );
                            const filteredGlobalParameters = Ext.create('Ext.data.ChainedStore', {
                                source: globalParameters,
                                filters: [
                                    function (globalParameter) {
                                        return (
                                            CMDBuildUI.model.dashboards.DataSourceParameter.normalizeType(
                                                globalParameter.get('type')
                                            ) === parameterType
                                        );
                                    }
                                ]
                            });
                            container.add({
                                xtype: 'administration-dashboards-common-parameters-parameter',
                                parameter: theParameter,
                                viewModel: {
                                    data: {
                                        theParameter: theParameter,
                                        globalParametersStore: filteredGlobalParameters,
                                        parameterIndex: index,
                                        componentId: theComponent.get('_id'),
                                        dashboardName: vm.get('owner').getViewModel().get('theDashboard.name')
                                    }
                                }
                            });
                        });
                    });
            }
        },

        generateParameterBySourceType: function (newValue) {
            if (!newValue) {
                CMDBuildUI.util.Logger.log(
                    'missing function arguments (generateParameterBySourceType)',
                    CMDBuildUI.util.Logger.levels.error
                );
                return;
            }
            var me = this,
                view = me.getView(),
                vm = view.lookupViewModel(),
                theComponent = vm.get('theComponent');

            if (theComponent.get('dataSourceType') === CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion) {
                me.generateFunctionParameterFieldsets(newValue);
                if (newValue) {
                    var funcktion = vm.getStore('functionsStore').findRecord('name', newValue);
                    if (funcktion) {
                        funcktion.getAttributes().then(function (attributes) {
                            var store = vm.getStore('functionAttributesStore');
                            store.getProxy().setData(me.applyAttributeDisplayOverrides(attributes.getRange()));
                            store.load();

                            view.down('#valueAxisFields').setBind({
                                value: '{theComponent.valueAxisFields}'
                            });
                        });
                    }
                }
            }
        },

        createDataSourceIfNotExist: function (theComponent, parameter) {
            var me = this;
            if (!me.getParameter(parameter.get('name'))) {
                theComponent.dataSourceParameters().add({
                    name: parameter.get('name'),
                    type: parameter.get('type').toLowerCase(),
                    defaultValue: parameter.get('type').toLowerCase() === 'boolean' ? 'null' : ''
                });
            }
        },

        getParameter: function (parameter) {
            var me = this,
                vm = me.getViewModel(),
                theComponent = vm.get('theComponent');
            return theComponent.dataSourceParameters().findRecord('name', parameter);
        },

        getGlobalParameters: function () {
            const me = this;
            const vm = me.getViewModel();
            return vm.get('owner').getViewModel().get('globalParametersStore');
        },

        loadDataSourceAttributes: function (overrideName, overrideType) {
            var me = this,
                vm = me.getViewModel(),
                store = vm.getStore('functionAttributesStore'),
                theComponent = vm.get('theComponent'),
                view = me.getView();

            if (theComponent && typeof theComponent.getSourceAttributes === 'function') {
                view.setLoading(true);

                theComponent.getSourceAttributes(overrideName, overrideType).then(
                    function (attributes) {
                        if (!me.destroyed && store) {
                            store.getProxy().setData(me.applyAttributeDisplayOverrides(attributes.getRange() || []));
                            store.load();
                        }
                        if (!me.destroyed) view.setLoading(false);
                    },
                    function () {
                        if (!me.destroyed && store) {
                            store.getProxy().setData([]);
                            store.load();
                        }
                        if (!me.destroyed) view.setLoading(false);
                    }
                );
            }
        },

        refreshDataSourceFieldsDisplay: function () {
            var view = this.getView();
            var containerIds = [
                'titleField_fieldcontainer',
                'descriptionField_fieldcontainer',
                'metaField_fieldcontainer',
                'tagField_fieldcontainer'
            ];
            Ext.Array.forEach(containerIds, function (itemId) {
                var container = view.down('#' + itemId);
                var displayfield = container && container.down('displayfield');
                if (displayfield && !displayfield.destroyed) {
                    displayfield.setValue(displayfield.getValue());
                }
            });
        },

        applyAttributeDisplayOverrides: function (attributes) {
            Ext.Array.forEach(attributes, function (attribute) {
                if (attribute.get('name') === 'IdClass') {
                    attribute.set('description', CMDBuildUI.locales.Locales.administration.dashboards.classname);
                }
            });
            return attributes;
        }
    }
});
