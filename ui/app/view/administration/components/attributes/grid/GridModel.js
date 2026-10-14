Ext.define('CMDBuildUI.view.administration.components.attributes.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-grid-grid',
    data: {
        selected: null,
        showInGridText: null,
        isOtherPropertiesHidden: true
    },

    formulas: {
        pluralObjectType: {
            bind: '{objectType}',
            get: function (objectType) {
                this.set(
                    'showInGridText',
                    objectType.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
                        ? CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinmainform
                        : CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid
                );
                var pluralname = Ext.util.Inflector.pluralize(objectType).toLowerCase();
                var theSession = this.get('theSession');
                var objectTypePerm;
                switch (pluralname) {
                    case 'processes':
                    case 'classes':
                        objectTypePerm = Ext.String.format('admin_{0}_modify', pluralname);
                        try {
                            this.set(
                                'toolAction._canAdd',
                                theSession.get('rolePrivileges')[objectTypePerm] &&
                                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                        this.get('objectTypeName')
                                    ).get('_can_modify')
                            );
                        } catch (error) {
                            CMDBuildUI.util.Logger.log(
                                'Unable to set addButton privileges',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                        break;
                    case 'domains':
                        objectTypePerm = Ext.String.format('admin_{0}_modify', pluralname);
                        try {
                            this.set('toolAction._canAdd', theSession.get('rolePrivileges')[objectTypePerm]);
                        } catch (error) {
                            CMDBuildUI.util.Logger.log(
                                'Unable to set addButton privileges',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                        break;
                    case 'dmsmodels':
                        try {
                            this.set(
                                'toolAction._canAdd',
                                CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName')).get(
                                    '_can_modify'
                                ) && theSession.get('rolePrivileges').admin_dms_modify
                            );
                        } catch (error) {
                            CMDBuildUI.util.Logger.log(
                                'Unable to set addButton privileges',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                        break;

                    default:
                        break;
                }

                return;
            }
        },
        canDelete: {
            bind: {
                isInherited: '{theAttribute.inherited}'
            },
            get: function (data) {
                return data.isInherited ? false : true;
            }
        },

        isDomainKeyHidden: {
            bind: {
                objectType: '{objectType}',
                cardinality: '{theDomain.cardinality}'
            },
            get: function (data) {
                if (data.objectType === 'Domain') {
                    return data.cardinality !== 'N:N';
                } else {
                    return true;
                }
            }
        },

        setCurrentType: {
            bind: '{theAttribute.type}',
            get: function (type) {
                this.set('types.isBoolean', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean);
                this.set('types.isDate', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date);
                this.set('types.isDatetime', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime);
                this.set('types.isDecimal', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal);
                this.set('types.isDouble', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double);
                this.set('types.isForeignkey', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey);
                this.set('types.isInteger', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer);
                this.set('types.isIpAddress', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress);
                this.set(
                    'types.isLookup',
                    type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup ||
                        type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                );
                this.set('types.isReference', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference);
                this.set('types.isString', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string);
                this.set('types.isText', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text);
                this.set('types.isTime', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time);
                this.set('types.isTimestamp', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime);
                this.set('types.isBigInteger', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint);
                this.set('types.isLink', type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link);
            }
        },
        allAtrributesGorups: function (get) {
            var allAttributesStore = get('allAttributesStore');

            var groups = [],
                data = [];

            Ext.Array.each(allAttributesStore, function (attribute) {
                var attributeData = attribute.getData();
                if (attributeData.group && attributeData.group.length > 0) {
                    if (!Ext.Array.contains(groups, attributeData.group)) {
                        Ext.Array.include(groups, attributeData.group);
                        Ext.Array.include(data, {
                            label: attributeData.group,
                            value: attributeData.group
                        });
                    }
                }
            });

            return data;
        }
    }
});
