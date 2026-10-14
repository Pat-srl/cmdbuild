Ext.define('CMDBuildUI.model.thematisms.Thematism', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        analysistypes: {
            intervals: 'intervals',
            punctual: 'punctual',
            graduated: 'graduated'
        },
        sources: {
            table: 'table',
            function: 'function'
        },
        defaults: {
            function_default_attribute: '_value',
            setFunctionAttribute: function (attribute) {
                this.function_default_attribute = attribute;
            }
        }
    },
    fields: [
        {
            name: 'name',
            type: 'string',
            critical: true
        },
        {
            name: 'description', //intervallo, punctual, graduated
            type: 'string',
            critical: true
        },
        {
            name: 'owner',
            type: 'string',
            critical: true
        },
        {
            name: 'attribute',
            type: 'string',
            critical: true
        },
        {
            name: 'type', //Tabella, funzione
            type: 'string',
            critical: true
        },
        {
            name: 'function',
            type: 'string',
            serialize: function (value, record) {
                switch (record.get('type')) {
                    case CMDBuildUI.model.thematisms.Thematism.sources.table:
                        return null;
                    case CMDBuildUI.model.thematisms.Thematism.sources.function:
                        return value;
                }
            },
            critical: true
        },
        {
            name: 'analysistype',
            type: 'string',
            critical: true
        },
        {
            name: 'segments',
            type: 'number',
            serialize: function (value, record) {
                switch (record.get('analysistype')) {
                    case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                        return null;
                    case CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals:
                        return value;
                }
            },
            critical: true
        },
        {
            name: 'rules',
            type: 'auto',
            critical: true,
            convert: function (value, record) {
                if (!value) return [];
                return JSON.parse(value);
            },
            serialize: function (value, record) {
                return JSON.parse(record.stringifyThematism());
            }
        },
        {
            name: '_public',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: 'accessType',
            type: 'string',
            critical: true,
            defaultValue: 'private',
            validators: {
                type: 'inclusion',
                list: ['public', 'private']
            }
        },
        {
            name: 'userOwner',
            type: 'string',
            persist: false
        },
        {
            name: 'classattribute',
            type: 'string',
            serialize: function (value, record) {
                switch (record.get('type')) {
                    case CMDBuildUI.model.thematisms.Thematism.sources.table:
                        return value;
                    case CMDBuildUI.model.thematisms.Thematism.sources.function:
                        return null;
                }
            },
            critical: true
        },
        {
            name: 'ollayername',
            type: 'string',
            calculate: function (data) {
                return Ext.String.format(
                    '{0}_{1}_{2}',
                    CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                    data.attribute,
                    data.owner
                );
            },
            persist: false
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.thematisms.Rules',
            name: 'rules'
        }
    ],

    proxy: {
        type: 'baseproxy'
    },

    stringifyThematism: function () {
        var stringrules = '';
        var s;

        this.rules()
            .getRange()
            .forEach(function (rule) {
                var attribute = rule.get('attribute');
                //handle the default case
                if (attribute == CMDBuildUI.model.thematisms.Rules.default.attribute) {
                    s = Ext.String.format('{"condition": {}, "style": {0}}', JSON.stringify(rule.get('style')));
                } else {
                    var operator = rule.get('operator');

                    //value formatting
                    var value = rule.get('value');
                    if (Ext.isArray(value)) {
                        //improve a better check for the array
                        var valueComposed = '';
                        for (var i = 0; i < value.length; i++) {
                            if (!Ext.isEmpty(valueComposed)) {
                                valueComposed = Ext.String.format('{0},"{1}"', valueComposed, value[i]);
                            } else {
                                valueComposed = Ext.String.format('"{0}"', value[i]);
                            }
                        }
                        value = valueComposed;
                    } else {
                        value = Ext.String.format('"{0}"', value);
                    }
                    // --- value formatting

                    var style = JSON.stringify(rule.get('style'));

                    //FIXME: when between operato is inserted will need a review
                    s = Ext.String.format(
                        '{"condition" : {"attribute": {"simple": {"attribute": "{0}", "operator": "{1}", "value": [{2}]}}}, "style": {3}}',
                        attribute,
                        operator,
                        value,
                        style
                    );
                }

                if (!stringrules) {
                    stringrules = s;
                } else {
                    stringrules = Ext.String.format('{0},{1}', stringrules, s);
                }
            }, this);

        return Ext.String.format('[{0}]', stringrules);
    },

    /**
     *
     */
    getOperationType: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[0].get('operator');
        }
    },

    getDefaultStyle: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[rulesRange.length - 1].get('style');
        }
    },

    getDefaultRule: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[rulesRange.length - 1];
        }
    },

    /**
     *
     * @param {Function} callback
     * @param {Object} scope
     * @param {Boolean} calltype Specify if calling the tryRules or using the id
     */
    calculateResults: function (callback, scope, calltype) {
        var owner = this.get('owner');
        var thematismId = this.getId();
        var calltype = this.get('tryRules'); //In future, check if the thematism or the rules store is modified;
        var me = this;

        var analysistype = this.get('analysistype');

        //FIXME: change the if condition to make it more readable
        //here is NOT applied the tryRules
        if (!(analysistype == CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals) && !calltype) {
            Ext.Ajax.request({
                url: CMDBuildUI.util.api.Classes.getThematismResultUrl(owner, thematismId),
                method: 'GET',
                callback: function (request, success, response) {
                    var parsedResponse = JSON.parse(response.responseText);
                    var data = parsedResponse.data || [];

                    // manipulates the results
                    var results = [];
                    data.forEach(function (item, index, array) {
                        results.push({
                            owner_id: item._id,
                            geostyle: Ext.create('CMDBuildUI.model.gis.GeoStyle', {
                                fillColor: item.style.color
                            })
                        });
                    }, this);
                    me.set('result', results);

                    if (callback) {
                        callback.call(scope, data);
                    }
                }
            });
            //here is applied the tryRules
        } else {
            //if the thematism is not saved make the tryRules calls
            var jsonData = {
                name: this.get('name'),
                description: this.get('description'),
                owner: owner,
                attribute: this.get('attribute'),
                type: this.get('type'),
                rules: JSON.parse(this.stringifyThematism()),
                function: this.get('function')
            };
            Ext.Ajax.request({
                url: CMDBuildUI.util.api.Classes.getThematismResultUrl(owner),
                method: 'POST',
                jsonData: jsonData,
                callback: function (request, success, response) {
                    var parsedResponse = JSON.parse(response.responseText);
                    var data = parsedResponse.data;

                    //manipulates the results
                    var results = [];
                    data.forEach(function (item, index, array) {
                        results.push({
                            owner_id: item._id,
                            geostyle: Ext.create('CMDBuildUI.model.gis.GeoStyle', {
                                fillColor: item.style.color
                            })
                        });
                    }, this);
                    me.set('result', results);

                    if (callback) {
                        callback.call(scope, data);
                    }
                }
            });
        }
    },

    hasRules: function () {
        return this.rules().getRange().length ? true : false;
    },

    /**
     *  @param {Object} config
     *  @param {[String || Number]} config.value
     *  @param {string} config.color
     *  @returns {CMDBuildUI.model.thematisms.Rules} the new rule created
     */
    createRule: function (config) {
        var attribute = this.thematismTargetAttribute();
        var analysistypes = this.get('analysistype');

        switch (analysistypes) {
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                return Ext.create('CMDBuildUI.model.thematisms.Rules', {
                    attribute: attribute,
                    operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                    value: config.value[0],
                    style: {
                        color: config.color
                    }
                });
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals:
                return Ext.create('CMDBuildUI.model.thematisms.Rules', {
                    attribute: attribute,
                    operator: CMDBuildUI.util.helper.FiltersHelper.operators.between,
                    value: [config.value[0], config.value[1]],
                    style: {
                        color: config.color
                    }
                });
        }
    },

    /**
     * @returns {CMDBuildUI.model.thematisms.Rules} the default rule
     */
    createDefaultRule: function (config) {
        return Ext.create('CMDBuildUI.model.thematisms.Rules', {
            attribute: CMDBuildUI.model.thematisms.Rules.default.attribute,
            style: {
                color: config.color
            }
        });
    },

    /**
     * This function return the thematism attribute used for the rules
     */
    thematismTargetAttribute: function () {
        switch (this.get('type')) {
            case CMDBuildUI.model.thematisms.Thematism.sources.function:
                return CMDBuildUI.model.thematisms.Thematism.defaults.function_default_attribute;
            case CMDBuildUI.model.thematisms.Thematism.sources.table:
                return this.get('classattribute');
        }
    }
});
