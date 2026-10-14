
Ext.define('CMDBuildUI.view.classes.cards.card.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.card.CreateController',
        'CMDBuildUI.view.classes.cards.card.CreateModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    mixins: [
        'CMDBuildUI.view.classes.cards.card.Mixin',
        'CMDBuildUI.mixins.forms.FormTriggers'
    ],

    alias: 'widget.classes-cards-card-create',
    controller: 'classes-cards-card-create',
    viewModel: {
        type: 'classes-cards-card-create'
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    config: {
        /**
         * @cfg {Object[]} [defaultValues]
         *
         * Can set default values for any of the attributes. An object can be:
         * `{attribute: 'attribute name', value: 'default value', editable: true|false}`
         * used for all attributes or
         * `{domain: 'domain name', value: 'default value', editable: true|false}`
         * used to set default values for references fields.
         */
        defaultValues: null,

        /**
         * @cfg {Object} [defaultValuesForCreate=true]
         *
         * Object to use in create property on object linking.
         */
        defaultValuesForCreate: true,

        /**
         * @cfg {Boolean} [cloneObject]
         *
         * If `true` the form will be populated width data from object to clone.
         */
        cloneObject: false
    },

    publish: [
        'defaultValues'
    ],

    bind: {
        defaultValues: '{defaultValues}'
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    modelValidation: true,
    autoScroll: true,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    tabpaneltools: [CMDBuildUI.view.classes.cards.Util.getHelpTool()],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelbtn',
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'card-create-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        ui: 'management-primary-outline-small',
        itemId: 'saveandclosebtn',
        autoEl: {
            'data-testid': 'card-create-saveandclose'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'savebtn',
        ui: 'management-primary-small',
        autoEl: {
            'data-testid': 'card-create-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }]
});
