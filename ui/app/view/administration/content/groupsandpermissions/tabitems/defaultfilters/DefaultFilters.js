Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.defaultfilters.DefaultFilters', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.defaultfilters.DefaultFiltersController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.defaultfilters.DefaultFiltersModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters',
    controller: 'administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters'
    },

    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'border',
    items: [{
        xtype: 'treepanel',
        itemId: 'filtertree',
        rootVisible: false,
        bind: {
            store: '{treeStore}'
        },
        viewConfig: {
            markDirty: false
        },
        ui: 'administration-navigation-tree',
        region: 'center',
        autoScroll: true,
        plugins: [{
            pluginId: 'cellediting',
            ptype: 'cellediting',
            clicksToEdit: 1
        }],
        columns: [{
            xtype: 'treecolumn', //this is so we know which column will show the tree
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.class,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.class'
            },
            width: '50%',
            sortable: false,
            dataIndex: 'text'
        }, {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultfilter,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultfilter'
            },
            width: '50%',
            sortable: false,
            dataIndex: 'filter',
            editor: {
                xtype: 'combobox',
                valueField: '_id',
                displayField: 'description'
            },
            align: 'begin',
            renderer: function (value) {
                if (value) {
                    var searchFiltersStore = Ext.getStore('searchfilters.Searchfilters');
                    var serachFilter = searchFiltersStore.getById(value);
                    if (serachFilter) {
                        return serachFilter.get('description');
                    }
                    return value;
                }
                return value;
            }
        }]
    }],

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            }, 'groupandpermission', 'theGroup')
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});