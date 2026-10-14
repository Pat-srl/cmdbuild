Ext.define('CMDBuildUI.view.dashboards.common.components.basechart.BaseChartController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.ComponentController',
    alias: 'controller.dashboards-components-basechart-basechart',
    mixins: ['CMDBuildUI.view.dashboards.Mixin'],

    listen: {
        global: {
            paramschanged: 'onParametersChanged'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#paramsForm': {
            beforerender: 'onParamsFormBeforeRender'
        },
        '#applyBtn': {
            click: 'onApplyBtnClick'
        },
        '#dataTable': {
            beforerender: 'onDataTableBeforeRender'
        }
    },

    /**
     * On component before render
     * @param {CMDBuildUI.view.dashboards.common.components.components.barchart.BarChart} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        const component = view.getItemDefinition();

        vm.set('form.hidden', !!component.get('hidelocalparam'));

        const me = this;
        component.getSourceAttributes().then(function (attributes) {
            // set translations for axis value fields
            attributes.getRange().forEach(function (attribute) {
                component.set(
                    Ext.String.format('_{0}_translation', attribute.get('name')),
                    attribute.get('_description_translation')
                );
            });
            view.initChart();
        });
        this.configureStore(view, component);

        me.updateParametersAsText(vm, component);
    },

    onParametersChanged: function (view) {
        const vm = this.getViewModel();
        this.updateParametersAsText(vm, this.getView().getItemDefinition());
        const store = vm.get('records');
        if (store) {
            store.load();
        }
    },

    /**
     *
     * @param {Ext.form.Form} form
     * @param {Object} eOpts
     */
    onParamsFormBeforeRender: function (form, eOpts) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const me = this;
        const component = view.getItemDefinition();
        const parameters = component.dataSourceParameters();

        const hasParameters = parameters && parameters.getCount() > 0;

        if (!hasParameters) {
            form.setHidden(true);
            vm.set('form.hidden', true);
            return;
        }

        let fields = [];
        let localValues = {};
        const paramsnames = vm.get('paramsnames');
        let localParamsNames = {};
        let lookupTypes = {};

        let visibleFieldsCounter = 0;
        const globalParameters = this.getView().lookupViewModel().get('globalParametersStore');
        parameters.getRange().forEach(function (parameter, index) {
            const useGlobalParameter = parameter.get('useGlobalParameter');
            let usedParameter = parameter;
            if (useGlobalParameter) {
                const index = globalParameters.find('_id', parameter.get('globalParameter'));
                if (index > -1) {
                    usedParameter = globalParameters.getAt(index);
                }
            }
            const field = me.getEditorForParameter(usedParameter, useGlobalParameter, {
                linkName: useGlobalParameter ? 'parameters' : 'localParameters'
            });
            fields.push(field);
            if (useGlobalParameter) {
                paramsnames[parameter.get('name')] = field.metadata.attributename;
            } else {
                visibleFieldsCounter++;
                localValues[field.name] = usedParameter.get('defaultValue');
                localParamsNames[field.name] = field.metadata.attributename;
            }
            if (
                usedParameter.get('fieldType') &&
                usedParameter.get('fieldType').toLowerCase() ===
                    CMDBuildUI.model.dashboards.DataSourceParameter.integerFieldTypes.lookup &&
                usedParameter.get('lookupType')
            ) {
                lookupTypes[usedParameter.get('lookupType')] = true;
            }
        });
        vm.set('paramsnames', paramsnames);
        vm.set('localParamsNames', localParamsNames);
        vm.set('localParameters', Ext.create('Ext.data.Model', localValues));

        Object.keys(lookupTypes).forEach(function (lookupType) {
            const lookupTypeModel = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(lookupType);
            if (lookupTypeModel) {
                lookupTypeModel.getLookupValues().then(function () {
                    if (!me.destroyed) {
                        me.updateParametersAsText(vm, component);
                    }
                });
            }
        });

        if (visibleFieldsCounter === 0) {
            form.setHidden(true);
            vm.set('form.hidden', true);
            return;
        }
        form.insert(0, fields);
    },

    /**
     *
     * @param {Ext.data.Grid} table
     * @param {Object} eOpts
     */
    onDataTableBeforeRender: function (table, eOpts) {
        const view = this.getView();
        const component = view.getItemDefinition();
        const me = this;

        component.getSourceAttributes().then(function (attributes) {
            if (!me.destroyed) {
                const columns = [];
                const allAttributes = Ext.Array.merge(attributes.getRange(), view.getGridCustomAttributes());
                allAttributes.forEach(function (attribute) {
                    if (attribute.get('name') === component.get('categoryAxisField')) {
                        attribute.set(
                            '_description_translation',
                            component.get('_categoryAxisLabel_translation') ||
                                component.get('categoryAxisLabel') ||
                                attribute.get('description') ||
                                attribute.get('name')
                        );
                    }
                    if (component.get('valueAxisFields').indexOf(attribute.get('name')) > -1) {
                        attribute.set(
                            '_description_translation',
                            attribute.get('_description_translation') ||
                                component.get('_valueAxisLabel_translation') ||
                                component.get('valueAxisLabel') ||
                                attribute.get('description') ||
                                attribute.get('name')
                        );
                    }
                    const field = CMDBuildUI.util.helper.ModelHelper.getModelField(attribute.getData());
                    const column = field ? CMDBuildUI.util.helper.GridHelper.getColumn(field) : null;
                    if (column) {
                        if (attribute.get('name') === component.get('labelField')) {
                            column.text =
                                component.get('_labelField_translation') || component.get('labelField') || column.text;
                        }
                        column.dataIndex = column.attributename;
                        columns.push(column);
                    }
                });
                table.reconfigure(null, columns);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onApplyBtnClick: function (button, e, eOpts) {
        const vm = button.lookupViewModel();
        const view = this.getView();
        Ext.asap(function () {
            this.updateParametersAsText(vm, view.getItemDefinition());
            vm.get('records').load();
        }, this);
    },

    /**
     *
     * @param {CMDBuild.model.dashboards.Component} component
     * @return {Ext.chart.CartesianChart}
     */
    intiChartConfig: function (view, component) {},

    /********************************************
     * CUSTOM ACTIONS
     ********************************************/

    /**
     * Click event listener on open popup menu item click
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     */
    actionOpenInPopupHandler: function (item, callback) {
        const view = this.getView();
        const vm = view.getViewModel();
        const clone = {
            xtype: view.xtype,
            itemDefinition: view.getItemDefinition(),
            customActions: view.getCustomActions().filter(function (action) {
                return action.handler !== 'actionOpenInPopupHandler';
            }),
            collapsible: false,
            cls: Ext.baseCSSPrefix + 'dashboard-component-popup',
            margin: 20,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            viewModel: {
                data: {
                    parameters: vm.get('parameters'),
                    localParameters: vm.get('localParameters'),
                    globalParametersStore: vm.get('globalParametersStore')
                }
            }
        };
        const popup = CMDBuildUI.util.Utilities.openPopup(null, view.getTitle(), clone);
    },

    /**
     * Click event listener on open popup menu item click
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     */
    actionReloadHandler: function (item, callback) {
        const view = this.getView();
        view.lookupViewModel().get('records').load();
    },

    /**
     * Click event listener on show hide table menu item click
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     */
    actionShowHideTableHandler: function (item, callback) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const isVisible = vm.get('grid.hidden');
        vm.set('grid.hidden', !isVisible);
        if (isVisible) {
            item.setText(CMDBuildUI.locales.Locales.dashboards.tools.gridhide);
        } else {
            item.setText(CMDBuildUI.locales.Locales.dashboards.tools.gridshow);
        }
    },

    /**
     * Click event listener on download data menu item click
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     */
    actionDownloadDataHandler: function (item, callback) {
        const view = this.getView();
        const store = view.lookupViewModel().get('records');
        const records =
            store.type == 'buffered'
                ? Object.values(store.data.map).flatMap(function (r) {
                      return r.value;
                  })
                : store.getRange();
        const jsonData = Ext.Array.map(records, function (record) {
            const raw = record.getData();
            const data = {};
            Object.keys(raw).forEach(function (key) {
                if (key !== 'id') {
                    data[key] = raw[key];
                }
            });
            return data;
        });
        CMDBuildUI.util.File.downloadFromJSON(jsonData, 'csv');
    },

    /**
     * Click event listener on download chart menu item click
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     */
    actionDownloadChartHandler: function (btn, eOpts) {
        const view = this.getView();
        const component = view.down('draw');
        if (component) {
            if (Ext.os.is.Desktop) {
                CMDBuildUI.util.File.downloadBase64(component.getImage().data, view.lookupViewModel().get('title'));
            } else {
                component.preview();
            }
        }
    },

    privates: {
        updateParametersAsText: function (vm, component) {
            let text = '';
            const dataSourceParameters = component.dataSourceParameters();
            const globalParameters = vm.get('globalParametersStore');
            const localParameters = vm.get('localParameters');
            const me = this;

            dataSourceParameters.getRange().forEach(function (parameter) {
                const isGlobalParameter = parameter.get('useGlobalParameter');
                if (isGlobalParameter) {
                    const id = parameter.get('globalParameter');
                    const index = globalParameters.find('_id', id);
                    if (index > -1) {
                        const globalParameter = globalParameters.getAt(index);
                        const name = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(
                            globalParameter.get('name')
                        );
                        const value = me.getParameterValue(globalParameter, vm, 'parameters', name);
                        text = me.concatParameter(text, globalParameter.get('description'), value);
                    }
                } else {
                    const name = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(parameter.get('name'));
                    const value = me.getParameterValue(parameter, vm, 'localParameters', name, localParameters);
                    text = me.concatParameter(text, parameter.get('description'), value);
                }
            });
            vm.set('parametersAsText', text);
        },

        getParameterValue: function (parameter, vm, parameterPath, name, parameterModel) {
            const fieldType = parameter.get('fieldType');
            const getValue = function (key) {
                return parameterModel && parameterModel.get
                    ? parameterModel.get(key)
                    : vm.get(Ext.String.format('{0}.{1}', parameterPath, key));
            };

            let value;
            if (
                fieldType &&
                fieldType.toLowerCase() === CMDBuildUI.model.dashboards.DataSourceParameter.integerFieldTypes.lookup
            ) {
                value = getValue(name);
                if (!Ext.isEmpty(value)) {
                    const lookupValue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(
                        parameter.get('lookupType'),
                        value
                    );
                    value = lookupValue ? lookupValue.getTranslatedDescription() : value;
                }
            } else {
                value = getValue(Ext.String.format('_{0}_description', name)) || getValue(name);
            }

            return value;
        },

        /**
         *
         * @param {CMDBuildUI.view.dashboards.common.components.components.barchart.BarChart} view
         * @param {CMDBuild.model.dashboards.Component} component
         */
        configureStore: function (view, component) {
            var sourcetype = component.get('dataSourceType');
            var storeinfo = component.getSourceStoreParameters();
            var config = {
                type: 'store',
                proxyurl: storeinfo.proxyurl,
                autoload: component.get('autoLoad'),
                filter: storeinfo.filter,
                extraparams: null
            };
            // TODO: check if parameter is mandatory
            component.dataSourceParameters().each(function (item) {
                if (item.get('required') && Ext.isEmpty(item.get('defaultValue'))) {
                    config.autoload = false;
                }
            });

            view.lookupViewModel().set('storedata', config);
        },

        /**
         * Appends a formatted parameter description and value to the text.
         *
         * @param {String} text The text to which the parameter is appended.
         * @param {String} description The parameter description.
         * @param {String} value The parameter value.
         * @returns {String} The formatted parameter description and value.
         */
        concatParameter: function (text, description, value) {
            if (Ext.isEmpty(value)) {
                return text;
            }

            return (
                text +
                Ext.String.format(
                    '<span class="' +
                        Ext.baseCSSPrefix +
                        'dashboard-applied-param">' +
                        '<span class="' +
                        Ext.baseCSSPrefix +
                        'dashboard-applied-param-label">{0}:</span>' +
                        '<span class="' +
                        Ext.baseCSSPrefix +
                        'dashboard-applied-param-value">{1}</span>' +
                        '</span>',
                    Ext.String.htmlEncode(description),
                    Ext.String.htmlEncode(value)
                )
            );
        }
    }
});
