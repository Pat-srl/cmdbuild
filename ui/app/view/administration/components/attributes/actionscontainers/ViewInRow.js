Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.administration-components-attributes-actionscontainers-viewinrow',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldscontainers.GeneralProperties',
        'CMDBuildUI.view.administration.components.attributes.fieldscontainers.TypeProperties',
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.ViewInRowController'
    ],
    controller: 'administration-components-attributes-actionscontainers-viewinrow',
    viewModel: {
        type: 'administration-components-attributes-actionscontainers-card'
    },
    hidden: true,
    bind: {
        hidden: '{!theAttribute}'
    },
    cls: 'administration',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null,
        objectType: null,
        objectId: null,
        shownInPopup: false,
        theAttribute: null,
        pluralObjectType: null
    },

    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        autoEl: {
            "data-testid": "administration-components-attributes-fieldscontainers-generalproperties"
        },
        items: [{
            xtype: 'administration-components-attributes-fieldscontainers-generalproperties',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            }
        }]
    }, {
        xtype: "fieldset",
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties'
        },
        autoEl: {
            "data-testid": "administration-components-attributes-fieldscontainers-typeproperties"
        },
        items: [{
            xtype: 'administration-components-attributes-fieldscontainers-typeproperties',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            }
        }]
    }, {
        xtype: "fieldset",
        ui: 'administration-formpagination',
        itemId: 'otherproperties',
        style: 'margin-bottom:10px',
        title: CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties'
        },
        autoEl: {
            "data-testid": "administration-components-attributes-fieldscontainers-otherproperties"
        },
        hidden: true,
        items: [{
            xtype: 'administration-components-attributes-fieldscontainers-otherproperties'
        }]
    }],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: true, // #viewBtn set true for show the button
        clone: true, // #cloneBtn set true for show the button
        'delete': true, // #deleteBtn set true for show the button
        activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
        download: false // #downloadBtn set true for show the buttons
    },
        /* testId */
        'attribute',

        /* viewModel object needed only for activeTogle */
        'theAttribute'
    ),
    listeners: {
        beforerender: function (view) {
            var vm = view.lookupViewModel();

            if (vm.data && !vm.get('isOtherPropertiesHidden')) {
                view.child('#otherproperties').tab.show();
            }
        }
    }
});