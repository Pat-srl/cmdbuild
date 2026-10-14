Ext.define('CMDBuildUI.view.relations.list.Grid', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.relations.list.GridController', 'CMDBuildUI.view.relations.list.GridModel'],

    alias: 'widget.relations-list-grid',
    controller: 'relations-list-grid',
    reference: 'relations-list-grid',
    viewModel: {
        type: 'relations-list-grid'
    },

    ui: 'cmdbuildgrouping',

    forceFit: true,
    loadMask: true,
    height: 0, //do not change to not disable renderedBuffer

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.relations.type,
            dataIndex: '_destinationType',
            align: 'left',
            renderer: function (value) {
                if (value) {
                    return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
                }
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.type'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.begindate,
            dataIndex: '_beginDate',
            sorter: 'BeginDate',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            },
            hidden: true,
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.begindate'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.code,
            dataIndex: '_destinationCode',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.code'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.description,
            dataIndex: '_destinationDescription',
            align: 'left',
            renderer: Ext.util.Format.stripTags,
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.description'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.relations.attributes,
            dataIndex: '_relationAttributesDesc',
            align: 'left',
            sortable: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.attributes'
            },
            renderer: function (value, cell, record) {
                if (value) {
                    return value;
                }
                const domain = CMDBuildUI.util.helper.ModelHelper.getDomainFromName(record.get('_type'));
                if (domain) {
                    const values = record.get('_relationAttributes');
                    domain.getAttributes().then(function (attributes) {
                        const items = [];
                        attributes.getRange().forEach(function (a) {
                            var value = values[a.get('name')];
                            if (value) {
                                a.set('cmdbuildtype', a.get('type'));
                                if (a.getData().password) {
                                    value = '•••••';
                                }
                                if (
                                    [
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase(),
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase(),
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase(),
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray.toLowerCase(),
                                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase()
                                    ].indexOf(a.get('type').toLowerCase()) === -1
                                ) {
                                    value = CMDBuildUI.util.helper.FieldsHelper.renderAttributeValue(
                                        a.getData(),
                                        value
                                    );
                                }
                                if (
                                    a.get('type').toLowerCase() ===
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase()
                                ) {
                                    value = value
                                        ? CMDBuildUI.locales.Locales.common.attributes.booltrue
                                        : CMDBuildUI.locales.Locales.common.attributes.boolfalse;
                                }
                                items.push(
                                    Ext.String.format(
                                        '{0}: {1}',
                                        a.get('_description_translation'),
                                        Ext.util.Format.stripTags(value)
                                    )
                                );
                            }
                        });
                        record.set('_relationAttributesDesc', items.join(', ') || ' ');
                    });
                }
            }
        }
    ],

    bind: {
        store: '{allRelations}'
    },

    initComponent: function () {
        /**
         * Get group header template
         */
        const actionColumns = this.getController().getActionColumns();
        const vm = this.getViewModel();
        const domains = vm.get('domains');
        const allChildDomains = [];
        const headerTpl = Ext.create(
            'Ext.XTemplate',
            '<div>{children:this.formatName} ({rows:this.getTotalRows})</div>',
            {
                formatName: function (children) {
                    if (children.length) {
                        const child = children[0];
                        const domain = domains.getById(child.get('_type'));
                        allChildDomains.push(domain);
                        if (domain) {
                            return child.get('_is_direct')
                                ? domain.getTranslatedDescriptionDirect()
                                : domain.getTranslatedDescriptionInverse();
                        }
                    }
                },
                getTotalRows: function (rows) {
                    return rows.length;
                }
            }
        );
        Ext.apply(this, {
            columns: Ext.Array.merge(this.columns, actionColumns),
            features: [
                {
                    ftype: 'customgrouping',
                    // groupHeaderTpl: '{name}',
                    groupHeaderTpl: headerTpl,
                    depthToIndent: 50,
                    enableGroupingMenu: false,
                    enableNoGroups: false,
                    startCollapsed: CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.ui.relationlimit
                    )
                }
            ]
        });

        this.callParent(arguments);
    },

    calculateHeight: function () {
        const container = this.up('relations-list-container');
        const tbar = container.getDockedItems()[0];
        const containerheight = container.getHeight();
        const tbarheight = tbar.getHeight();
        this.setHeight(containerheight - tbarheight);
    }
});
