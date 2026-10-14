Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.AttachmentsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-attachmentsfieldset',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.strings.processattachments,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.processes.strings.processattachments'
        },
        ui: 'administration-formpagination',
        items: [{
            xtype: 'fieldcontainer',
            columnWidth: 1,
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory, // Category Lookup
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory'
                    },
                    items: [{
                        /********************* Category Lookup **********************/
                        // create / edit
                        xtype: 'combobox',
                        typeAhead: true,
                        queryMode: 'local',
                        displayField: 'name',
                        valueField: 'name',
                        emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                        },
                        name: 'attachmentTypeLookup',
                        hidden: true,
                        bind: {
                            store: '{dmsCategoryTypesStore}',
                            value: '{theProcess.dmsCategory}',
                            hidden: '{actions.view}'
                        },
                        triggers: {
                            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                        }
                    }, {
                        // view
                        xtype: 'displayfieldwithtriggers',
                        name: 'attachmentTypeLookup',
                        hidden: true,
                        bind: {
                            value: '{theProcess.dmsCategory}',
                            hidden: '{!actions.view}',
                            hideTrigger: '{!theProcess.dmsCategory}'

                        },
                        triggers: {
                            open: {
                                cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                                handler: function (f, trigger, eOpts) {
                                    var value = f.lookupViewModel().get('theProcess.dmsCategory'),
                                        url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl(CMDBuildUI.util.Utilities.stringToHex(value));
                                    CMDBuildUI.util.Utilities.closeAllPopups();
                                    CMDBuildUI.util.Utilities.redirectTo(url);
                                }
                            }
                        },
                        renderer: function (value) {
                            if (!Ext.isEmpty(value)) {
                                var store = Ext.getStore('dms.DMSCategoryTypes');
                                var record = store.findRecord('name', value);
                                if (record) {
                                    return record.get('description');
                                }
                            }
                            return value || CMDBuildUI.locales.Locales.administration.common.labels.default;
                        }
                    }]
                }]
            }]
        }]
    }]
});