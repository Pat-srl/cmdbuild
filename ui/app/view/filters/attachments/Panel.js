Ext.define('CMDBuildUI.view.filters.attachments.Panel', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.filters.attachments.PanelController', 'CMDBuildUI.view.filters.attachments.PanelModel'],

    alias: 'widget.filters-attachments-panel',
    controller: 'filters-attachments-panel',
    viewModel: {
        type: 'filters-attachments-panel'
    },

    title: CMDBuildUI.locales.Locales.filters.attachments,
    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.attachments'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    scrollable: true,

    config: {
        isDms: false
    },

    items: [
        {
            xtype: 'container',
            layout: 'column',
            defaults: {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                minHeight: 1
            },
            items: [
                {
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: CMDBuildUI.locales.Locales.filters.attachmentssearchtext,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.filters.attachmentssearchtext'
                            },
                            margin: 'auto auto 25 auto',
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: 'onSearchTextClearClick'
                                }
                            },
                            disabled: true,
                            bind: {
                                value: '{attachments.searchtext}',
                                visible: '{visibletextfield}',
                                disabled: '{displayOnly}'
                            },
                            autoEl: {
                                'data-testid': 'filters-attachments-panel-searchtext'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            cls: 'panel-with-gray-background',
            bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            bind: {
                hidden: '{isAdministrationModule && actions.view == true}'
            },
            items: [
                {
                    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                    layout: 'anchor',
                    xtype: 'fieldcontainer',
                    flex: 0.3,
                    items: [
                        {
                            xtype: 'groupedcombo',
                            valueField: 'value',
                            displayField: 'label',
                            queryMode: 'local',
                            forceSelection: true,
                            margin: 'auto 10px auto auto',
                            itemId: 'metadatacombo',
                            fieldLabel: CMDBuildUI.locales.Locales.attachments.metadata,
                            autoEl: {
                                'data-testid': 'filters-attachments-row-metadatacombo'
                            },
                            bind: {
                                store: '{metadatalist}'
                            },
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.attachments.metadata'
                            }
                        }
                    ]
                },
                {
                    xtype: 'component', // fulfill value space
                    flex: 0.7
                },
                {
                    xtype: 'component', // fulfill button space
                    width: 45
                }
            ]
        },
        {
            xtype: 'container',
            scrollable: true,
            reference: 'attributescontainer',
            itemId: 'attributescontainer'
        }
    ],

    /**
     * @return {Object} Attachment data
     */
    getAttachmentsData: function () {
        const vm = this.lookupViewModel();
        const filter = {};

        const value = vm.get('attachments.searchtext');
        if (!Ext.isEmpty(value)) {
            filter.query = value;
        }

        // get metadata filter data
        const rowsitems = this.query('filters-attributes-row'),
            attrs = {},
            cats = {};

        rowsitems.forEach(function (row) {
            // TODO: change with new filter
            const rowdata = row.getFilterData();
            if (rowdata.category) {
                if (!cats[rowdata.category]) {
                    cats[rowdata.category] = {
                        model: rowdata.model,
                        attributes: {}
                    };
                }
                rowdata.attribute = rowdata.attribute.replace(rowdata.category + '_', '');
                if (!cats[rowdata.category].attributes[rowdata.attribute]) {
                    cats[rowdata.category].attributes[rowdata.attribute] = [];
                }
                cats[rowdata.category].attributes[rowdata.attribute].push(rowdata);
            } else {
                if (!attrs[rowdata.attribute]) {
                    attrs[rowdata.attribute] = [];
                }
                attrs[rowdata.attribute].push(rowdata);
            }
        });

        // encode metadata filter
        const metadataFilter = CMDBuildUI.util.helper.FiltersHelper.encodeAttachmentsMetadataFilter(attrs, cats);
        if (!Ext.isEmpty(metadataFilter)) {
            Ext.merge(filter, metadataFilter);
        }

        // return filter
        if (vm.get('isAdministrationModule')) {
            return !Ext.Object.isEmpty(filter)
                ? filter
                : vm.get('theFilter.configuration.attachment')
                  ? vm.get('theFilter.configuration.attachment')
                  : null;
        } else {
            return !Ext.Object.isEmpty(filter) ? filter : null;
        }
    }
});
