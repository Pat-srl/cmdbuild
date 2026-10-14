Ext.define('CMDBuildUI.view.widgets.createreport.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-createreport-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = me.getViewModel(),
            theWidget = vm.get('theWidget'),
            reportType = theWidget.get(this.parameters.ReportCode),
            report = CMDBuildUI.util.helper.ModelHelper.getReportFromName(reportType),
            extension;
        if (!report) {
            return;
        }
        // this type of widget does not support inline mode
        if (theWidget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }

        if (theWidget.get("_required")) {
            theWidget.getOwner().setValue(true);
        }

        // check for extension
        if (theWidget.get(this.parameters.ForcePDF)) {
            extension = CMDBuildUI.model.reports.Report.extensions.pdf;
        } else if (theWidget.get(this.parameters.ForceCSV)) {
            extension = CMDBuildUI.model.reports.Report.extensions.csv;
        }

        // get report attributes
        report.getAttributes().then(function (attributes) {
            // get defaults
            me.getDefaultValues(attributes).then(function (defaults) {
                // add report component
                view.add({
                    xtype: 'reports-container',
                    hideTitle: true,
                    viewModel: {
                        data: {
                            objectTypeName: theWidget.get('ReportCode'),
                            extension: extension,
                            defaults: defaults
                        }
                    },
                    listeners: {
                        closeparameterspopup: function (reportcontainer, reportid) {
                            view.fireEvent("popupclose");
                        }
                    }
                });
            })
        });
    },

    /**
     * Get default values
     * 
     * @param {Ext.data.Store} attributes
     * 
     * @returns {Ext.promise.Promise}
     */
    getDefaultValues: function (attributes) {
        var deferred = new Ext.Deferred();

        var me = this,
            vm = me.getViewModel(),
            theWidget = vm.get('theWidget'),
            theTarget = vm.get("theTarget"),
            data = theWidget.getData(),
            promises = [];

        // get default values
        for (var key in data) {
            // check that key is not system key or configuration parameter
            if (!Ext.String.startsWith(key, "_") && !me.parameters[key]) {
                var attr = attributes.findRecord("name", key);
                promises.push(me.extractVariableFromString(key, data[key], theTarget, attr));
            }
        }

        // return defaults if there are promises
        if (promises.length) {
            Ext.Promise.all(promises).then(function (defs) {
                var defaults = {};
                defs.forEach(function (def) {
                    defaults[def.key] = {
                        value: def.value
                    };
                });

                // get read only fields
                if (!Ext.isEmpty(theWidget.get(me.parameters.ReadOnlyAttributes))) {
                    var readonly = theWidget.get(me.parameters.ReadOnlyAttributes).split(",");
                    readonly.forEach(function (f) {
                        if (!defaults[f]) {
                            defaults[f] = {};
                        }
                        defaults[f].editable = false;
                    });
                }
                deferred.resolve(defaults);
            });
        } else {
            // return empty defaults
            deferred.resolve({});
        }

        return deferred.promise;
    },

    /**
     * Resolve variable.
     * 
     * @param {String} key
     * @param {String} variable
     * @param {CMDBuildUI.model.base.Base} theTarget 
     * @param {CMDBuildUI.model.Attribute} [attribute]
     * 
     * @return {*} The variable resolved.
     */
    extractVariableFromString: function (key, variable, theTarget, attribute) {
        var deferred = new Ext.Deferred();

        var dyn_variable = Ext.isString(variable) ? /^{(client|server|cql)+:(.+)}$/.exec(variable) : null,
            response = {
                key: key
            };

        if (dyn_variable) {
            var vartype = dyn_variable[1],
                varvalue = dyn_variable[2];

            switch (vartype) {
                // server variable
                case "server":
                    var result = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([varvalue], theTarget);
                    response.value = result[varvalue];
                    deferred.resolve(response);
                    break;
                // client variable
                case "client":
                    var result = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([varvalue], theTarget);
                    response.value = result[varvalue];
                    deferred.resolve(response);
                    break;
                // cql
                case "cql":
                    var proxyDef;
                    switch (attribute.get("type")) {
                        // lookup
                        case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup:
                            proxyDef = {
                                type: 'baseproxy',
                                url: CMDBuildUI.util.api.Lookups.getLookupValues(attribute.get("lookupType"))
                            }
                            break;
                        // foreign key
                        case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey:
                            proxyDef = {
                                type: 'baseproxy',
                                url: attribute.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass ?
                                    CMDBuildUI.util.api.Classes.getCardsUrl(attribute.get("targetClass")) : CMDBuildUI.util.api.Processes.getInstancesUrl(attribute.get("targetClass"))
                            }
                            break;
                    }

                    if (proxyDef) {
                        // create the store
                        var store = Ext.create("Ext.data.Store", {
                            proxy: proxyDef,
                            advancedFilter: {
                                cql: varvalue.replace(/\\"/g, '"')
                            },
                            autoLoad: false
                        });
                        store.load({
                            callback: function (records, operation, success) {
                                // if the response has only one value get the id 
                                // of that record otherwise show error
                                if (success && records.length === 1) {
                                    response.value = records[0].get("_id");
                                } else {
                                    CMDBuildUI.util.Logger.log(
                                        !success ? "Error on response" : "Auto value needs 1 record but the response has " + records.length + " records",
                                        CMDBuildUI.util.Logger.levels.error
                                    );
                                }
                                // resolve
                                deferred.resolve(response);

                                // destroy store
                                Ext.asap(function () {
                                    store.destroy();
                                });
                            }
                        });
                    } else {
                        deferred.resolve(response);
                    }
                    break;
            }
        } else {
            // static variable
            response.value = variable;
            deferred.resolve(response);
        }

        return deferred.promise;
    },

    privates: {
        /**
         * Custom configuration parameters for this widget
         */
        parameters: {
            ReportCode: "ReportCode",
            ForcePDF: "ForcePDF",
            ForceCSV: "ForceCSV",
            ReadOnlyAttributes: "ReadOnlyAttributes"
        }
    }
});