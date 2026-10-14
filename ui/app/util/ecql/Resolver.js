/**
 * @file CMDBuildUI.util.ecql.Resolver
 * @module CMDBuildUI.util.ecql.Resolver
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.ecql.Resolver', {
    singleton: true,

    /**
     * Resolve eCQL
     *
     * @param {Object} filter eCQL configuration.
     * @param {String} filter.id eCQL id.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @param {String[]} filter.bindings.server List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     *
     * @returns {Obejct}
     * The eCQL filter.
     * {
     *      id: "ecqlId",
     *      context: "{client:{some:value},server:{wath:ever}}"
     * }
     *
     * @example
     * var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
     *      widget.get("_DefaultSelection_ecql"),
     *      card
     * );
     */
    resolve: function (filter, target) {
        if (filter) {
            const context = {
                client: this.resolveClientVariables(filter.bindings.client, target),
                server: this.resolveServerVariables(filter.bindings.server, target)
            };

            return {
                id: filter.id,
                context: Ext.JSON.encode(context)
            };
        }
    },

    /**
     * Resolve client variables.
     *
     * @param {String[]} variables List of binding client attributes.
     * @param {Ext.data.Model} target The target model instance.
     *
     * @returns {Object}
     *
     * @example
     * var resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([variable], theTarget);
     */
    resolveClientVariables: function (variables, target) {
        if (variables && variables.length) {
            const data = target ? target.getData() : {};
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Resolve server variables.
     *
     * @param {String[]} variables List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     *
     * @returns {Object}
     *
     * @example
     * var resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([variable], theTarget);
     */
    resolveServerVariables: function (variables, target) {
        // TODO: get original data
        if (variables && variables.length) {
            const data = target ? target.getData() : {};
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Get bindings.
     *
     * @param {Object} filter eCQL configuration.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @param {String} linkname The link name to use to build the binds.
     * @returns {Object} An object containing all keys
     *
     * @example
     * var binds = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(
     *      me.getFilter(),
     *      me.getRecordLinkName()
     * );
     */
    getViewModelBindings: function (filter, linkname) {
        const bindings = {};
        for (var i = 0; i < filter.bindings.client.length; i++) {
            const attr = filter.bindings.client[i];
            const sattr = attr.split('.');
            if (sattr.length === 1) {
                bindings[sattr[0]] = Ext.String.format(
                    '{{0}.{1}}',
                    linkname,
                    CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0])
                );
            } else if (sattr.length === 2 && (sattr[1] === 'Id' || sattr[1] === 'Code' || sattr[1] === 'Description')) {
                bindings[sattr[0]] = Ext.String.format(
                    '{{0}.{1}}',
                    linkname,
                    CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0])
                );
            }
        }
        return bindings;
    },

    privates: {
        /**
         * @private
         * @param {Object} data
         * @param {String[]} keys
         * @return {Object}
         */
        getValuesFromData: function (data, keys) {
            const values = {};

            keys.forEach(function (attr) {
                const sattr = attr.split('.');
                let field, val;
                if (sattr.length === 1) {
                    field = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]);
                    if (field === 'Id') {
                        field = '_id';
                    }
                    val = data[field];
                    if (Ext.isDate(val)) {
                        val = Ext.Date.format(val, 'Y-m-d');
                    }
                } else if (sattr.length === 2) {
                    const value = data[sattr[0]];
                    const isLookupArray = Ext.isArray(value);
                    if (!isLookupArray) {
                        switch (sattr[1]) {
                            case 'Id':
                                field = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]);
                                break;
                            case 'Description':
                                field = Ext.String.format('_{0}_description', sattr[0]);
                                break;
                            case 'Code':
                                field = Ext.String.format('_{0}_code', sattr[0]);
                                break;
                        }

                        //get the value
                        if (field) {
                            val = data[field] !== 0 ? data[field] : null;
                        }
                    } else if (!Ext.isEmpty(value)) {
                        val = [];
                        Ext.Array.forEach(data['_' + sattr[0] + '_details'], function (item, index, allitems) {
                            const prop = sattr[1] === 'Id' ? '_id' : sattr[1].toLowerCase();
                            const elem = item[prop];
                            if (elem) {
                                val.push(elem);
                            }
                        });
                    }
                }
                values[attr] = Ext.isEmpty(val) && !Ext.isArray(val) ? null : val;
            });

            return values;
        }
    }
});
