/**
 * @file CMDBuildUI.util.helper.GridHelper
 * @module CMDBuildUI.util.helper.GridHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.GridHelper', {
    singleton: true,

    /**
     * Get columns for given type.
     *
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Object} config Config object used in CMDBuildUI.util.helper.GridHelper.getColumns().
     *
     * @returns {Ext.promise.Promise<Ext.grid.column.Column[]>} Resolve method has as argument an
     *      instance of Ext.grid.column.Column. Reject method has as argument
     *      a String containing error message.
     *
     * @example
     * CMDBuildUI.util.helper.GridHelper.getColumnsForType(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      'Employee', {
     *          allowFilter: view.getAllowFilter()
     *      }
     * ).then(function (columns) {
     *      grid.reconfigure(null, columns);
     * });
     */
    getColumnsForType: function (objectType, objectTypeName, config) {
        const deferred = new Ext.Deferred();
        const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
        if (item) {
            config = Ext.applyIf(config || {}, {
                preferences: CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName),
                objectType: objectType,
                objectTypeName: objectTypeName
            });

            CMDBuildUI.util.helper.ModelHelper.getModel(objectType, objectTypeName).then(function (model) {
                const columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), config);
                deferred.resolve(columns);
            });
        } else {
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Returns columns definition for grids.
     *
     * @param {Ext.data.field.Field[]} fields Array of model fields.
     * @param {Object} config
     * @param {String[]|Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.addTypeColumn If `true` a new column is added as first item with object type.
     * @param {Boolean} config.reducedGrid If true shows the reducedGrid columns.
     * @param {Object} config.preferences Grid preferences.
     * @param {String[]} config.aggregate The columns that must show the sum.
     * @param {String} config.objectType the object type
     * @param {String} config.objectTypeName the name object
     *
     * @returns {Ext.grid.column.Column[]} An array of Ext.grid.column.Column definitions.
     *
     * @example
     * var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
     *      allowFilter: false,
     *      reducedGrid: true,
     *      objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      objectTypeName: 'Employee'
     * });
     */
    getColumns: function (fields, config) {
        const me = this;
        const columns = [];
        const haspreferences = config.preferences && !Ext.isEmpty(config.preferences.columns);
        const object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(config.objectTypeName, config.objectType);

        config = Ext.applyIf(config || {}, {
            allowFilter: false,
            addTypeColumn: object ? object.get('prototype') : false,
            reducedGrid: false,
            aggregate: []
        });

        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
            const type = fields[0].owner.objectType;
            const name = fields[0].owner.objectTypeName;
            const objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(name, type);
            const isAView = type == CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
            var multitenantMode = objectdefinition ? objectdefinition.get('multitenantMode') : null;

            if (objectdefinition && objectdefinition.get('prototype')) {
                Ext.Array.each(objectdefinition.getChildren(true), function (item, index, allitems) {
                    const multitenantModeLeaf = item.get('multitenantMode');
                    if (
                        multitenantModeLeaf === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                        multitenantModeLeaf === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
                    ) {
                        multitenantMode = multitenantModeLeaf;
                        return false;
                    }
                });
            }

            if (isAView) {
                const masterClassDefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    objectdefinition.get('masterClass'),
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                );
                multitenantMode = masterClassDefinition ? masterClassDefinition.get('multitenantMode') : null;
            }

            if (
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
            ) {
                const tenantFields = Ext.Array.filter(fields, function (item, index, allitems) {
                    return item.name.includes('_tenant');
                });

                Ext.Array.forEach(tenantFields, function (tenantField, index, allTenantFields) {
                    const tenantFieldName = tenantField.name;
                    var hidden = true;
                    var flex = 0.8;
                    var filter;
                    var orderIndex = -2;
                    var tenantLabel = CMDBuildUI.util.Utilities.getTenantLabel();

                    // get hidden and flex properties from preferences
                    if (haspreferences) {
                        const tenantcolumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                            return item.attribute === tenantFieldName;
                        });
                        hidden = tenantcolumn ? false : true;
                        flex = tenantcolumn ? tenantcolumn.width : 0;
                        orderIndex = tenantcolumn && tenantcolumn.orderIndex >= 0 ? tenantcolumn.orderIndex : -2;

                        const tenantDescription = tenantcolumn ? tenantcolumn.description : tenantField.description;
                        if (tenantDescription && tenantDescription !== 'IdTenant') {
                            tenantLabel = tenantDescription;
                        }
                    } else if (isAView) {
                        if (tenantField) {
                            hidden = !tenantField.attributeconf.showInGrid;
                            const tenantDescription = tenantField.description;
                            if (tenantDescription !== 'IdTenant') {
                                tenantLabel = tenantDescription;
                            }
                        }
                    }

                    // add filter
                    if (
                        config &&
                        config.allowFilter !== undefined &&
                        ((Ext.isBoolean(config.allowFilter) && config.allowFilter === true) ||
                            (Ext.isArray(config.allowFilter) && config.allowFilter.indexOf(field.name) !== -1))
                    ) {
                        filter = {
                            type: 'list',
                            store: {
                                proxy: 'memory',
                                data: CMDBuildUI.util.helper.SessionHelper.getActiveTenants()
                            },
                            idField: 'code',
                            labelField: 'description'
                        };
                    }

                    // add column
                    columns.push({
                        text: tenantLabel,
                        dataIndex: tenantFieldName,
                        attributename: tenantFieldName,
                        hidden: hidden,
                        flex: flex,
                        filter: filter,
                        orderIndex: orderIndex,
                        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                            var tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants(),
                                t = Ext.Array.findBy(tenants, function (i) {
                                    return i.code == value;
                                });
                            return t && t.description;
                        }
                    });
                });
            }
        }

        if (config.addTypeColumn) {
            const children = object.getChildren(true);
            const filterItems = Ext.Array.map(children, function (item, index, array) {
                return { _id: item.getId(), text: item.get('_description_translation') || item.get('description') };
            });
            var filter, hidden, flex, orderIndex;

            if (config.allowFilter) {
                filter = {
                    type: 'list',
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    options: Ext.Array.sort(filterItems, function (a, b) {
                        return a.text < b.text ? -1 : a.text > b.text ? 1 : 0;
                    }),
                    idField: '_id',
                    labelField: 'text',
                    menuDefaults: {
                        scrollable: true,
                        layout: {
                            type: 'vbox',
                            align: 'stretchmax',
                            overflowHandler: null
                        }
                    }
                };
            }

            if (!haspreferences) {
                hidden = false;
                flex = 0.8;
                orderIndex = -1;
            } else {
                const typecolumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                    return item.attribute === '_type';
                });

                hidden = typecolumn ? false : true;
                flex = typecolumn ? typecolumn.width : 0;
                orderIndex = typecolumn && typecolumn.orderIndex >= 0 ? typecolumn.orderIndex : -1;
            }

            columns.push({
                text: CMDBuildUI.locales.Locales.common.grid.subtype,
                dataIndex: '_type',
                attributename: '_type',
                hidden: hidden,
                flex: flex,
                filter: filter,
                orderIndex: orderIndex,
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
                }
            });
        }

        var hasvisiblefields = false;
        Ext.Array.each(fields, function (field, index) {
            const column = me.getColumn(field, config);
            if (column) {
                // override column based on preferences
                if (haspreferences) {
                    const name = column.attributename || column.dataIndex;
                    const colpref = Ext.Array.findBy(config.preferences.columns, function (item) {
                        return item.attribute === name;
                    });
                    if (colpref) {
                        column.hidden = false;
                        column.flex = colpref.width;
                        column.orderIndex = colpref.orderIndex >= 0 ? colpref.orderIndex : index;
                    } else {
                        column.hidden = true;
                        column.orderIndex = index;
                    }
                }
                // add column
                columns.push(column);
                hasvisiblefields = !column.hidden ? true : hasvisiblefields;
            }
        });

        if (object && object.get('noteInline')) {
            var noteColumn;

            if (haspreferences) {
                noteColumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                    return item.attribute === CMDBuildUI.util.helper.ModelHelper.extraFields.notes;
                });
            }

            columns.push({
                text: CMDBuildUI.locales.Locales.common.tabs.notes,
                dataIndex: CMDBuildUI.util.helper.ModelHelper.extraFields.notes,
                hidden: noteColumn ? false : !object.get('noteInGrid'),
                flex: noteColumn ? noteColumn.width : 1,
                orderIndex: noteColumn && noteColumn.orderIndex >= 0 ? noteColumn.orderIndex : null,
                filter: {
                    type: 'string'
                },
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    return me.renderTextColumn(value, true);
                }
            });
            hasvisiblefields = noteColumn ? true : hasvisiblefields;
        }

        /**
         * Add in classes Last Update column
         */
        const type = fields[0].owner.objectType;
        const klassOrProcess =
            type == CMDBuildUI.util.helper.ModelHelper.objecttypes.klass ||
            type == CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        if (klassOrProcess) {
            var beginDatecolumn;

            if (haspreferences) {
                beginDatecolumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                    return item.attribute === '_beginDate';
                });
            }
            columns.push({
                text: CMDBuildUI.locales.Locales.common.grid.lastUpdate,
                dataIndex: '_beginDate',
                attributename: '_beginDate',
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                },
                flex: beginDatecolumn ? beginDatecolumn.width : 1,
                hidden: beginDatecolumn ? false : true,
                orderIndex: beginDatecolumn && beginDatecolumn.orderIndex >= 0 ? beginDatecolumn.orderIndex : null,
                filter: {
                    type: 'date'
                }
            });
            hasvisiblefields = beginDatecolumn ? true : hasvisiblefields;
        }

        if (!hasvisiblefields) {
            const desccol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === 'Description' || c.dataIndex === 'Code';
            });
            if (desccol) {
                desccol.hidden = false;
            }
            const namecol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === 'Name';
            });
            if (namecol) {
                namecol.hidden = false;
            }
            if (!Ext.isEmpty(columns) && (!desccol || !namecol)) {
                columns[0].hidden = false;
            }
        }
        // sort attributes on orderIndex property
        return columns.sort(function (a, b) {
            return a.orderIndex - b.orderIndex;
        });
    },

    /**
     * Returns column definition.
     *
     * @param {Ext.data.field.Field} field Model field.
     * @param {Object} config
     * @param {String[]|Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.reducedGrid If true shows the reducedGrid columns.
     * @param {String} config.objectType the object type
     * @param {String} config.objectTypeName the name object
     *
     * @returns {Ext.grid.column.Column} An Ext.grid.column.Column definition.
     *
     * @example
     * var field = model.getField("Requester");
     * var column = CMDBuildUI.util.helper.GridHelper.getColumn(field);
     */
    getColumn: function (field, config) {
        const me = this;
        const attrconf = field.attributeconf;
        var column;
        config = config || {};
        if (
            !Ext.String.startsWith(field.name, '_') &&
            (!field.hidden || config.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.view) &&
            !attrconf.hideInGrid
        ) {
            column = {
                text: field.isInstance
                    ? field.getDescription() === 'IdClass'
                        ? CMDBuildUI.locales.Locales.common.grid.subtype
                        : field.getDescription()
                    : attrconf._description_translation || field.description || field.name,
                dataIndex: field.name,
                attributename: field.attributename,
                hidden: config.reducedGrid ? !attrconf.showInReducedGrid : !attrconf.showInGrid,
                sortable: !Ext.isEmpty(attrconf.sortingEnabled) ? attrconf.sortingEnabled : true
            };

            if (field.attributename && field.name !== field.attributename) {
                column.sorter = {
                    property: field.attributename
                };
            }

            // add resize listener
            column.listeners = {
                resize: function (column) {
                    if (column.textEl.dom.scrollWidth > column.textEl.dom.clientWidth) {
                        column.el.dom.dataset.qtip = column.text;
                        column.el.dom.dataset.qalign = 'tl-bl';
                    } else if (column.el.dom.dataset.qtip) {
                        delete column.el.dom.dataset.qtip;
                    }
                }
            };

            switch (field.cmdbuildtype.toLowerCase()) {
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                    column.flex = 0.3;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderThreeStateBooleanField(value);
                    };
                    break;
                /**
                 * Date fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                    column.flex = 0.5;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                    column.flex = 0.5;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                /**
                 * Numeric fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + 'numericcell';
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                '<div class="{0}cell-content">{1}</div>',
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                                    scale: field.attributeconf.scale,
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + 'numericcell';
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                '<div class="{0}cell-content">{1}</div>',
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                                    visibleDecimals: field.attributeconf.visibleDecimals,
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + 'numericcell';
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                '<div class="{0}cell-content">{1}</div>',
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                /**
                 * Relation fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray.toLowerCase():
                    column.flex = 0.8;
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (value) {
                            return CMDBuildUI.util.helper.FieldsHelper.renderLookupArrayField(value, {
                                lookupType: field.attributeconf.lookupType,
                                fieldName: field.name,
                                record: record,
                                showOnlyIconIfPresent: field.attributeconf.showOnlyIconIfPresent
                            });
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                    column.flex = field.attributeconf.showOnlyIconIfPresent ? 0.3 : 0.8;
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        const output = CMDBuildUI.util.helper.FieldsHelper.renderLookupField(value, {
                            lookupIdField: field.attributeconf.lookupIdField,
                            lookupType: field.attributeconf.lookupType,
                            fieldName: field.name,
                            record: record,
                            showOnlyIconIfPresent: field.attributeconf.showOnlyIconIfPresent
                        });
                        if (field.attributeconf.showOnlyIconIfPresent) {
                            metaData.style = 'text-align: center;';
                        }
                        return output;
                    };
                    break;

                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                    column.flex = 1;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderReferenceField(value, {
                            fieldName: field.name,
                            attributeName: field.attributeconf.name,
                            isHtml: field.attributeconf._html,
                            targetType: field.attributeconf.targetType,
                            targetTypeName: field.attributeconf.targetClass,
                            stripTags: true,
                            record: record
                        });
                    };
                    break;
                /**
                 * File field
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderFileField(value, {
                            record: record,
                            fieldName: field.name
                        });
                    };
                    break;
                /**
                 * IP Address
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                    column.flex = 0.8;
                    if (field.attributeconf.ipType === 'ipv4') {
                        column.flex = 0.5;
                    }
                    break;
                /**
                 * Text fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                    column.flex = 0.3;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                    column.flex = 1;
                    if (field.attributeconf.maxLength < 100 && field.attributeconf.maxLength >= 50) {
                        column.flex = 0.8;
                    } else if (field.attributeconf.maxLength < 50) {
                        column.flex = 0.5;
                    }
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (
                            field.attributeconf.password &&
                            record.get(Ext.String.format('_{0}_has_value', field.name))
                        ) {
                            value = '•••••';
                        }
                        return me.renderTextColumn(value, field.attributeconf._html);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                    column.flex = 1;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return me.renderTextColumn(value, field.attributeconf._html);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return me.renderTextColumn(value, true);
                    };
                    break;
                /**
                 * Formula field
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula.toLowerCase():
                    column.sortable = false;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.entrytype.toLowerCase():
                    column.flex = 1;
                    break;
            }

            // add column filter
            if (
                config &&
                config.allowFilter !== undefined &&
                ((Ext.isBoolean(config.allowFilter) && config.allowFilter === true) ||
                    (Ext.isArray(config.allowFilter) && config.allowFilter.indexOf(field.name) !== -1))
            ) {
                var store;
                switch (field.cmdbuildtype.toLowerCase()) {
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                        column.filter = {
                            type: 'boolean',
                            serializer: function (value) {
                                return !!value;
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                        column.filter = {
                            type: 'string',
                            itemDefaults: {
                                maxLength: 1
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                        column.filter = {
                            type: 'date',
                            isDateField: true
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                        column.filter = {
                            type: 'date'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: field.attributeconf.scale
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                        column.filter = {
                            type: 'numeric'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: 0
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray.toLowerCase():
                        if (
                            config.objectType &&
                            [
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                            ].indexOf(config.objectType) > -1
                        ) {
                            store = Ext.create('Ext.data.Store', {
                                model: 'CMDBuildUI.model.lookups.Lookup',
                                proxy: {
                                    type: 'baseproxy',
                                    url: CMDBuildUI.util.api.Lookups.getLookupValues(field.attributeconf.lookupType),
                                    extraParams: {
                                        forClass: config.objectTypeName,
                                        forAttr: attrconf.name
                                    }
                                },
                                pageSize: 0,
                                autoLoad: true,
                                autoDestroy: true
                            });
                        } else {
                            const lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
                                field.attributeconf.lookupType
                            );
                            store = lt ? lt.values() : [];
                        }
                        column.filter = {
                            type: 'list',
                            cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray,
                            store: store,
                            idField: '_id',
                            labelField: 'text',
                            loadOnShow: false,
                            operator: 'overlap',
                            menuDefaults: {
                                scrollable: true,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretchmax',
                                    overflowHandler: null
                                }
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                        if (
                            config.objectType &&
                            [
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                            ].indexOf(config.objectType) > -1
                        ) {
                            store = Ext.create('Ext.data.Store', {
                                model: 'CMDBuildUI.model.lookups.Lookup',
                                proxy: {
                                    type: 'baseproxy',
                                    url: CMDBuildUI.util.api.Lookups.getLookupValues(field.attributeconf.lookupType),
                                    extraParams: {
                                        forClass: config.objectTypeName,
                                        forAttr: attrconf.name
                                    }
                                },
                                pageSize: 0,
                                autoLoad: true,
                                autoDestroy: true
                            });
                        } else {
                            const lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
                                field.attributeconf.lookupType
                            );
                            store = lt ? lt.values() : [];
                        }
                        column.filter = {
                            type: 'list',
                            cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            store: store,
                            idField: '_id',
                            labelField: 'text',
                            loadOnShow: false,
                            menuDefaults: {
                                scrollable: true,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretchmax',
                                    overflowHandler: null
                                }
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                        column.filter = {
                            type: 'reference'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                        if (!field.attributeconf.password) {
                            column.filter = {
                                type: 'string'
                            };
                        }
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                }

                if (column.filter) {
                    // use original attribute name for filter
                    column.filter.dataIndex = field.attributeconf.name;

                    // Used to see column filters correctly on administration
                    if (['string', 'numeric', 'reference'].indexOf(column.filter.type) > -1) {
                        column.filter.itemDefaults = column.filter.itemDefaults || {};
                        Ext.apply(column.filter.itemDefaults, {
                            maxWidth: 220,
                            labelAlign: 'left'
                        });
                    }
                }
            }
            // add column sum
            if (config.aggregate && config.aggregate.indexOf(field.name) !== -1) {
                column.summaryType = 'sum';
                column.summaryRenderer = function (value) {
                    return column.renderer.call({}, value);
                };
            }
        }
        return column;
    },

    /**
     * Returns column editor definition for given field.
     * This method gets the field configuration from CMDBuild.util.helper.FormHelper.getEditorForField()
     * and adapts for the column.
     *
     * @param {Ext.data.field.Field} field Model field.
     * @param {Object} config Config object to use in CMDBuild.util.helper.FormHelper.getEditorForField() method.
     *
     * @returns {Ext.form.field.Field} An Ext.form.field.Field definition.
     *
     * @example
     * var column = {
     *      name: 'BooleanField',
     *      text: 'Boolean column',
     *      editor: CMDBuildUI.util.helper.GridHelper.getEditorForField(model.getField('BooleanField));
     * }
     */
    getEditorForField: function (field, config) {
        const editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field, config);

        switch (field.cmdbuildtype.toLowerCase()) {
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                editor.xtype = 'referencecombofield';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                editor.padding = 'auto auto auto 10px';
                break;
        }
        return editor;
    },

    /**
     * Return Print button definition.
     *
     * @param {Object} config View Ext.button.Button for custom configuration.
     *
     * @returns {Ext.button.Button} Print button definition.
     *
     * @example
     * var grid = {
     *      xtype: 'grid',
     *      tbar: [
     *          CMDBuildUI.util.helper.GridHelper.getPrintButtonConfig({
     *              disabled: true,
     *              bind: {
     *                  disabled: '{disabledbuttons.print}'
     *              }
     *          })
     *      ]
     * }
     */
    getPrintButtonConfig: function (config) {
        config = config || {};
        const buttonConfig = {
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('print', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.common.grid.print,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'grid-printbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.grid.print'
            },
            menu: [
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-pdf', 'regular'),
                    itemId: 'printPdfBtn',
                    text: CMDBuildUI.locales.Locales.common.grid.printpdf,
                    printformat: 'pdf',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.grid.printpdf'
                    }
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-excel', 'regular'),
                    itemId: 'printCsvBtn',
                    text: CMDBuildUI.locales.Locales.common.grid.printcsv,
                    printformat: 'csv',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.grid.printcsv'
                    }
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('map', 'regular'),
                    itemId: 'printMapBtn',
                    text: CMDBuildUI.locales.Locales.common.grid.printmap,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.grid.printmap'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{!btnHide}'
                    }
                }
            ]
        };

        return Ext.applyIf(config, buttonConfig);
    },

    /**
     * Return Buffered grid counter definition.
     *
     * @param {String} storeName
     *
     * @returns {Object}
     *
     * @example
     * var grid = {
     *      xtype: 'grid',
     *      tbar: [
     *          {
     *              xtype: 'tbfill'
     *          },
     *          CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("instances")
     *      ]
     * };
     *
     */
    getBufferedGridCounterConfig: function (storeName, config) {
        if (storeName) {
            config = config || {};
            const bind = Ext.applyIf(
                {
                    store: '{' + storeName + '}'
                },
                config.bind
            );

            return Ext.applyIf(
                {
                    xtype: 'bufferedgridcounter',
                    padding: '0 20 0 0',
                    bind: bind
                },
                config
            );
        }
    },

    /**
     * Get process flow status column
     *
     * @returns {Ext.grid.column.Column} Column definition.
     *
     * @example
     * CMDBuildUI.util.helper.GridHelper.getColumnsForType(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
     *      "IncidentMgt", {
     *          allowFilter: true
     *      }
     * ).then(function (columns) {
     *      columns.push(CMDBuildUI.util.helper.GridHelper.getProcessFlowStatusColumn());
     *
     *      // reconfigure columns
     *      grid.reconfigure(null, columns);
     * });
     */
    getProcessFlowStatusColumn: function () {
        return {
            dataIndex: 'status',
            enableColumnHide: false,
            hideable: false,
            draggable: false,
            sortable: false,
            menuDisabled: true,
            minWidth: 38,
            maxWidth: 38,
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var output = '';
                // get formatted description
                if (value) {
                    const lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(
                        CMDBuildUI.model.processes.Process.flowstatus.lookuptype,
                        value
                    );
                    if (lookupvalue) {
                        var icon = CMDBuildUI.util.helper.IconHelper.getIconId('square', 'solid');
                        if (lookupvalue.get('icon_font')) {
                            const icon_font_splitted = lookupvalue.get('icon_font').split(':');
                            icon = CMDBuildUI.util.helper.IconHelper.getIconId(
                                icon_font_splitted[0],
                                icon_font_splitted.length > 1 ? icon_font_splitted[1] : ''
                            );
                        }
                        const icon_color = lookupvalue.get('icon_color') || 'inherit';
                        const txt = lookupvalue.get('_description_translation') || lookupvalue.get('description');

                        output = Ext.String.format(
                            '<span class="{0}" style="color: {1}; cursor: help;" data-qtip="{2}"></span>',
                            icon,
                            icon_color,
                            txt
                        );
                    }
                }
                return output;
            }
        };
    },

    /**
     * Return Save grid preferences tool definition.
     *
     * @returns {Ext.panel.Tool}
     *
     * @example
     * var grid = {
     *      xtype: 'grid',
     *      tbar: [
     *          {
     *              xtype: 'tbfill'
     *          },
     *          CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("instances"),
     *          CMDBuildUI.util.helper.GridHelper.getSaveGridPreferencesTool()
     *      ]
     * };
     */
    getSaveGridPreferencesTool: function () {
        return {
            xtype: 'tool',
            cls: Ext.baseCSSPrefix + 'tool-primary',
            itemId: 'savePreferencesBtn',
            tooltip: CMDBuildUI.locales.Locales.main.preferences.gridpreferencessave,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.main.preferences.gridpreferencessave'
            },
            bind: {
                hidden: '{btnHide}'
            }
        };
    },

    /**
     * Set the icon for grid preferences
     * @param {*} view
     */
    setIconGridPreferences: function (view) {
        const vm = view.getViewModel();
        const toolSave = view.down('#savePreferencesBtn');

        if (
            Ext.Object.isEmpty(
                CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                    vm.get('objectType'),
                    vm.get('objectTypeName')
                )
            )
        ) {
            toolSave.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'));
        } else {
            toolSave.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'solid'));
        }
    },

    /**
     * Save grid preferences.
     *
     * @param {Ext.grid.Panel} grid
     * @param {Ext.panel.Tool} tool
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     *
     * @example
     * CMDBuildUI.util.helper.GridHelper.saveGridPreferences(
     *      grid,
     *      tool,
     *      vm.get("objectType"),
     *      vm.get("objectTypeName")
     * );
     */
    saveGridPreferences: function (grid, tool, objectType, objectTypeName) {
        function savePreferences() {
            const config = {
                columns: [],
                defaultOrder: [],
                statusFilter: ''
            };
            // columns
            const columns = grid.getVisibleColumns();
            columns.forEach(function (column, index) {
                const attribute = column.attributename || column.initialConfig.dataIndex;
                if (attribute) {
                    config.columns.push({
                        attribute: attribute,
                        width: Math.round((column.getWidth() / grid.getWidth()) * 100) / 100,
                        orderIndex: index
                    });
                }
            });
            // sorters
            grid.getStore()
                .getSorters()
                .getRange()
                .forEach(function (sorter) {
                    config.defaultOrder.push({
                        attribute: sorter.getProperty(),
                        direction: sorter.getDirection() === 'ASC' ? 'ascending' : 'descending'
                    });
                });

            // status filter
            if (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                const combo = grid.down('#statuscombo'),
                    data = combo.publishedState;

                if (data.value) {
                    config.statusFilter = data.value;
                }
            }

            // update preferences
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences(objectType, objectTypeName, config).then(
                function () {
                    CMDBuildUI.util.Notifier.showSuccessMessage(
                        CMDBuildUI.locales.Locales.main.preferences.gridpreferencessaved
                    );
                    tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'solid'));
                }
            );
        }

        function clearPreferences() {
            // clear preferences
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences(objectType, objectTypeName, {
                columns: undefined,
                defaultOrder: undefined,
                statusFilter: undefined
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(
                    CMDBuildUI.locales.Locales.main.preferences.gridpreferencescleared
                );
                tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'));
            });
        }

        if (Ext.Object.isEmpty(CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName))) {
            // save preferences if not saved yet
            savePreferences();
        } else {
            // show menu to chose action update or clear
            if (tool.menu) {
                tool.menu.show();
            } else {
                tool.menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: [
                        {
                            text: CMDBuildUI.locales.Locales.main.preferences.gridpreferencesupdate,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'),
                            handler: function () {
                                savePreferences();
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.main.preferences.gridpreferencesclear,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                            handler: function () {
                                clearPreferences();
                            }
                        }
                    ]
                });
                tool.menu.alignTo(tool.el.id, 't-b?');
            }
        }
    },

    /**
     * Return Form in row widget definition
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {Object} config Base configuration.
     *
     * @returns {Ext.tab.Panel}
     *
     * @example
     * var grid = {
     *      xtype: 'grid',
     *
     *      plugins: [{
     *          ptype: 'forminrowwidget',
     *          expandOnDblClick: false,
     *          removeWidgetOnCollapse: true,
     *          widget: CMDBuildUI.util.helper.GridHelper.getFormInRowWidget(
     *              CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel
     *          )
     *      }]
     * };
     */
    getFormInRowWidget: function (type, config) {
        var xtype;
        config = config || {};

        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                xtype = 'classes-cards-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                xtype = 'processes-instances-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                xtype = 'events-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                xtype = 'dms-tabpanel';
                break;
        }

        const baseconf = {
            xtype: xtype,
            ui: 'managementlighttabpanel',
            cls: 'tabbarwithtools',
            padding: '0 0 8 0',
            tabPosition: 'top',
            readOnlyTabs: true,
            bind: {}
        };
        return Ext.apply(baseconf, config);
    },

    /**
     * Return the attributes used to sort the grid
     *
     * @param {Ext.data.Model} object The model for a specific class/Process/View/...
     * @param {Boolean} sortByDescription True if you want to sort data by description if there are no preferences or object sorters
     * @returns {Array} the attributes to use to sort the grid
     *
     * @example
     * CMDBuildUI.util.helper.GridHelper.getStoreSorters(object)
     */
    getStoreSorters: function (object, sortByDescription) {
        const sorters = [];
        const preferences = object
            ? CMDBuildUI.util.helper.UserPreferences.getGridPreferences(object.objectType, object.getId())
            : {};

        if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
            preferences.defaultOrder.forEach(function (o) {
                sorters.push({
                    property: o.attribute === 'IdClass' ? '_type' : o.attribute,
                    direction: o.direction === 'descending' ? 'DESC' : 'ASC'
                });
            });
        } else if (object && object.defaultOrder && object.defaultOrder().getCount()) {
            object
                .defaultOrder()
                .getRange()
                .forEach(function (o) {
                    sorters.push({
                        property: o.get('attribute') === 'IdClass' ? '_type' : o.get('attribute'),
                        direction: o.get('direction') === 'descending' ? 'DESC' : 'ASC'
                    });
                });
        } else if (sortByDescription || (object && object.isSimpleClass && !object.isSimpleClass())) {
            sorters.push({
                property: 'Description',
                direction: 'ASC'
            });
        }

        return sorters;
    },

    privates: {
        /**
         * Remove tags if is html or convert tags to string if is plain.
         *
         * @param {String} value
         * @param {Boolean} html
         */
        renderTextColumn: function (value, html) {
            if (!html) {
                value = CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            }
            return Ext.util.Format.stripTags(value);
        }
    }
});
