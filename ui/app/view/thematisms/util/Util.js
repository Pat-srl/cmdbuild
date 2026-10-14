Ext.define('CMDBuildUI.thematisms.util.Util', {
    singleton: true,

    /**
     *                          RULES CALCULATION
     * ----------------------------------------------------------------------------------
     */

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Function} callback;
     * @param {Function} scope
     */
    calculateRules: function (thematism, callback, scope) {
        var analisisType = thematism.get('analysistype');
        switch (analisisType) {
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                this.calculatePunctualRules(thematism).then(function (newRules) {
                    thematism.rules().clearData();
                    thematism.rules().insert(0, newRules);
                    callback.call(scope, thematism, newRules);
                });
                break;
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals:
                this.calculateIntervalsRules(thematism).then(function (newRules) {
                    thematism.rules().clearData();
                    thematism.rules().insert(0, newRules);
                    callback.call(scope, thematism, newRules);
                })
                break;
            default:
                console.error();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     * @returns {Ext.promise.Promise}
     */
    calculatePunctualRules: function (thematism) {
        var me = this;
        var deferred = new Ext.Deferred();

        this._getThematismAttributeParams(thematism).then(function (obj) {
            var attribute = obj.attribute;
            var params = obj.params;

            me.createStore(thematism).then(function (store) {
                store.load({
                    params: Ext.apply(params, {
                        distinct: attribute
                    }),
                    callback: function (records, operation, success) {
                        if (success) {
                            var rules = [];
                            records.forEach(function (record) {
                                if (!Ext.isEmpty(record.get(attribute))) {
                                    //creates the punctual rule
                                    rules.push(
                                        thematism.createRule({
                                            value: [record.get(attribute)],
                                            color: '#' + Math.random().toString(16).substr(-6)
                                        }));
                                }
                            });
                            //default punctual rule
                            rules.push(
                                thematism.createDefaultRule({
                                    color: '#' + Math.random().toString(16).substr(-6)
                                }));
                            deferred.resolve(rules);
                        } else {
                            deferred.reject();
                        }
                    },
                    scope: me
                });
            });
        });

        return deferred;
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     * @returns {Ext.promise.Promise}
     */
    calculateIntervalsRules: function (thematism) {
        var me = this;
        var deferred = new Ext.Deferred();

        me._getThematismAttributeParams(thematism).then(function (obj) {

            var attribute = obj.attribute;
            var params = obj.params;

            me.createStore(thematism).then(function (store) {
                store.load({
                    params: Ext.apply(params, {
                        distinct: attribute,
                        sort: Ext.JSON.encode([{
                            property: attribute,
                            direction: 'ASC'
                        }])
                    }),
                    callback: function (records, operation, success) {
                        if (success) {
                            calculateIntervalsRulesAux(records);
                        } else {
                            deferred.reject();
                        }
                    },
                    scope: me
                });

            });

            function calculateIntervalsRulesAux(records) {
                var rules = [];

                var oldRules = thematism.rules().getRange();
                var defaultOldRule = oldRules.splice(oldRules.length - 1);

                CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, thematism.get('owner')).then(function (model) {
                    var cmdbuildField = model.getField(attribute);
                    var cmdbuildtype = cmdbuildField.cmdbuildtype;

                    //this check will disappear adding an assertion
                    if (cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time ||
                        cmdbuildtype == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime) {

                        if (records.length) {
                            var min = records[0].get(attribute);
                            var max = records[records.length - 1].get(attribute);
                            var segments = thematism.get('segments');

                            var config = {
                                scale: cmdbuildField.attributeconf.scale,
                                showThousandsSeparator: cmdbuildField.attributeconf.showThousandsSeparator,
                                // unitOfMeasure: cmdbuildField.attributeconf.unitOfMeasure,
                                // unitOfMeasureLocation: cmdbuildField.attributeconf.unitOfMeasureLocation,
                                visibleDecimals: cmdbuildField.attributeconf.visibleDecimals,
                                hideSeconds: cmdbuildField.attributeconf.hideSeconds
                            };

                            var rangeHandler = new CMDBuildUI.thematisms.util.RangeHandler.rangeHandler(min, max, segments, cmdbuildtype, config);
                            for (var i = 0; i < segments; i++) {
                                var color;
                                if (oldRules[i]) {
                                    color = oldRules[i].get('style').color;
                                } else {
                                    color = '#' + Math.random().toString(16).substr(-6); //get the previous color of that segment
                                }

                                //creates the interval rule
                                rules.push(
                                    thematism.createRule({
                                        value: [rangeHandler.ranges[i], rangeHandler.ranges[i + 1]],
                                        color: color
                                    }));
                            }
                        }

                        var defaultColor;
                        if (defaultOldRule[0]) {
                            defaultColor = defaultOldRule[0].get('style').color;
                        } else {
                            defaultColor = '#' + Math.random().toString(16).substr(-6); //get the previous color of that segment
                        }

                        //creates the interval default rule
                        rules.push(
                            thematism.createDefaultRule({
                                color: defaultColor
                            })
                        );

                        deferred.resolve(rules);
                    } else {
                        console.error('Selected Attribute not ideal for intervals');
                        deferred.reject();
                    }
                }, me);
            }
        });

        return deferred;
    },

    /**
     *                          LEGEND CALCULATION
     * ----------------------------------------------------------------------------------
     */

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Function} callback
     * @param {Object} scope
     * //TODO: create a static model
     * @returns {Ext.promise.Promise}
     */
    calculateLegend: function (thematism, callback, scope) {
        var analisisType = thematism.get('analysistype');

        switch (analisisType) {
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                this.calculatePunctualLegend(thematism).then(function (newLegendData) {
                    callback.call(scope, thematism, newLegendData);
                });
                break;
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals:
                this.calculateIntervalsLegend(thematism).then(function (newLegendData) {
                    callback.call(scope, thematism, newLegendData);
                });
                break;
            default:
                console.error('analysys type not found for lagend generator');
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Object} cards Optional. If present doesn't request the cards to
     * @returns {Ext.promise.Promise}
     */
    calculatePunctualLegend: function (thematism, cards) {
        var me = this;
        var deferred = new Ext.Deferred();

        me._getThematismAttributeParams(thematism).then(function (obj) {
            var attribute = obj.attribute;
            var params = obj.params;
            if (!cards) {

                me.createStore(thematism).then(function (store) {
                    store.load({
                        params: Ext.apply(params, {
                            distinct: attribute,
                            count: attribute,
                            distinctIncludeNull: true
                        }),
                        callback: function (records, operation, success) {
                            if (success) {
                                calculatePunctualLegendAux(records);
                            } else {
                                deferred.reject();
                            }
                        },
                        scope: me
                    });
                });
            } else {
                calculatePunctualLegendAux(cards);
            }


            function calculatePunctualLegendAux(records) {
                var legenddata = [];
                var rules = thematism.rules();

                var nullCount = 0;

                records.forEach(function (record) {
                    var distinctValue = record.get(attribute);
                    var viewValue = record.get(Ext.String.format('_{0}_description_translation', attribute)) || record.get(Ext.String.format('_{0}_description', attribute)) || distinctValue;
                    var count = record.get("_count");
                    var recordIndex = rules.find('value', record.get(attribute));

                    if (recordIndex != -1) {
                        legenddata.push(Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                            value: distinctValue,
                            viewValue: viewValue,
                            count: count,
                            color: rules.getAt(recordIndex).get('style').color,
                            referenceRule: rules.getAt(recordIndex)
                        }));
                    }
                    // Push the not found values
                    else if (distinctValue != null) {
                        legenddata.push(Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                            value: distinctValue,
                            viewValue: viewValue,
                            count: count,
                            color: thematism.getDefaultStyle().color
                        }));
                    } else if (distinctValue == null) {
                        nullCount = count; //saves the count information of null distinct records
                    }
                });

                if (thematism.hasRules()) { //FIXME: Is not a good check. this should't be handled here be handled 
                    //Push the default value
                    legenddata.push(Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                        value: null,
                        viewValue: CMDBuildUI.locales.Locales.main.preferences.default,
                        count: nullCount, //uses the count information of null distinct records
                        color: thematism.getDefaultStyle().color,
                        referenceRule: thematism.getDefaultRule()
                    }));
                }


                deferred.resolve(legenddata);
            }
        });
        return deferred;
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Object} cards Optional. If present doesn't request the cards to
     * @returns {Ext.promise.Promise}
     */
    calculateIntervalsLegend: function (thematism, cards) {
        var me = this;
        var deferred = new Ext.Deferred();

        me._getThematismAttributeParams(thematism).then(function (obj) {

            var attribute = obj.attribute;
            var params = obj.params;
            if (!cards) {

                me.createStore(thematism).then(function (store) {
                    store.load({
                        params: Ext.apply(params, {
                            distinct: attribute,
                            count: attribute,
                            distinctIncludeNull: true
                        }),
                        callback: function (records, operation, success) {
                            if (success) {
                                calculateintervalsLegendAux(records);
                            } else {
                                deferred.reject();
                            }
                        },
                        scope: me
                    });
                });
            } else {                
                calculateintervalsLegendAux(cards);
            }

            /**
             * 
             * @param {*} records 
             */
            function calculateintervalsLegendAux(records) {
                var legenddata = [];
                var rules = thematism.rules();


                rules.getRange().forEach(function (rule) {
                    var values = rule.get('value');

                    if (values) {
                        // create the legend rows
                        legenddata.push(
                            Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                                value: values,
                                viewValue: Ext.String.format('{0} {1}', values[0], values[1]),
                                count: 0,
                                color: rule.get('style').color,
                                referenceRule: rule
                            })
                        );
                    } else {
                        //create the default legend row
                        legenddata.push(
                            Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                                value: null,
                                viewValue: CMDBuildUI.locales.Locales.main.preferences.default,
                                count: 0,
                                color: thematism.getDefaultStyle().color,
                                referenceRule: rule
                            })
                        );
                    }
                });

                records.forEach(function (record) {
                    //finds the record
                    var recordValue = record.get(attribute);
                    var ruleIndex;
                    if (recordValue != null) {
                        ruleIndex = rules.findBy(function (rule, id) {
                            var ruleValue = rule.get('value');

                            if (!ruleValue) {
                                return true;
                            } else if (recordValue >= ruleValue[0] && recordValue <= ruleValue[1]) {
                                return true;
                            }
                        }, this);
                    } else {
                        ruleIndex = legenddata.length - 1; //map as default when value is null
                    }

                    if (ruleIndex != -1) {
                        var matchLegend = legenddata[ruleIndex];
                        var newCount = matchLegend.get('count') + record.get('count');
                        matchLegend.set('count', newCount);
                    } else {
                        console.error('this should not happend');
                    }
                });

                deferred.resolve(legenddata);
            }
        });
        return deferred;
    },

    privates: {
        /**
         * 
         * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
         * @returns {Ext.data.Store}
         */
        createStore: function (thematism) {
            var me = this;
            var deferred = new Ext.Deferred();
            var objectType = thematism.get('owner');

            me._getThematismAttributeParams(thematism).then(function (obj) {
                var attribute = obj.attribute;

                var store = Ext.create('Ext.data.Store', {
                    fields: [{
                        name: attribute,
                        type: 'auto'
                    }, {
                        name: 'count',
                        mapping: '_count',
                        type: 'number'
                    }],
                    proxy: {
                        type: 'baseproxy',
                        url: CMDBuildUI.util.api.Classes.getCardsUrl(objectType)
                    },
                    autoLoad: false,
                    autoDestroy: true
                });
                deferred.resolve(store);
            });
            return deferred.promise;
        },

        /**
         * This function helps to get informations about the params to use for the store and the attribute
         * avoiding making the if condition;
         * 
         * @param {CMDBuildUI.model.thematisms.Thematism} thematism
         * @returns {Object}
         */
        _getThematismAttributeParams: function (thematism) {
            var deferred = new Ext.Deferred();
            var obj = {
                attribute: null,
                params: null
            }

            switch (thematism.get('type')) {
                case CMDBuildUI.model.thematisms.Thematism.sources.function:
                    if(!thematism.get('functionattribute')){
                        CMDBuildUI.model.Function.load(thematism.get('function'), {
                            callback: function (record, op, success) {
                                if (success) {
                                    record.getAttributes().then(function (attributes) {                                       
                                        var attribute = attributes.first();
                                        thematism.set('functionattribute', attribute.get('name'));
                                        obj.attribute = attribute.get('name');
                                        obj.params = {
                                            functionValue: thematism.get('function'),
                                            limit: 0,
                                            distinctIncludeNull: true
                                        };
                                        CMDBuildUI.model.thematisms.Thematism.defaults.setFunctionAttribute(obj.attribute);
                                        deferred.resolve(obj);
                                    });                                
                                } else {
                                    deferred.reject();
                                }
                            }
    
                        });
                    } else {                        
                        obj.attribute = thematism.get('functionattribute');
                        obj.params = {
                            functionValue: thematism.get('function'),
                            limit: 0,
                            distinctIncludeNull: true
                        };
                        deferred.resolve(obj);
                    }

                    break;
                case CMDBuildUI.model.thematisms.Thematism.sources.table: //use the new model thematism function for the attribute
                    obj.attribute = thematism.get('classattribute');
                    obj.params = {
                        limit: 0
                    };
                    deferred.resolve(obj);
                    break;
            }
            return deferred.promise;
        }
    }


});