
Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceFieldSet', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.fields.schedulerdate.SequenceFieldSetController',
        'CMDBuildUI.view.fields.schedulerdate.SequenceFieldSetModel'
    ],

    alias: 'widget.fields-schedulerdate-sequencefieldset',
    controller: 'fields-schedulerdate-sequencefieldset',
    viewModel: {
        type: 'fields-schedulerdate-sequencefieldset'
    },

    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    bind: {
        title: '{theTrigger._description_translation}'
    },

    collapsible: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    items: [{
        modelValidation: true,
        xtype: 'form',
        itemId: 'form',
        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.calendar.recalculate,
            localized: {
                text: 'CMDBuildUI.locales.Locales.calendar.recalculate'
            },
            hidden: true,
            bind: {
                hidden: '{recalculateHidden}'
            },
            ui: 'management-primary-small',
            handler: function (button, eOpts) {
                var ct = this.lookupController();
                ct.recalculateEvents();
            },
            margin: '20 5 20 15',
            formBind: true
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.calendar.add,
            localized: {
                text: 'CMDBuildUI.locales.Locales.calendar.add'
            },
            hidden: true,
            bind: {
                hidden: '{addHidden}'
            },
            ui: 'management-primary-small',
            handler: 'onAddButtonClick',
            margin: '20 0 20 5',
            formBind: true
        }]
    }, {
        xtype: 'events-grid',
        controller: 'fields-schedulerdate-gridcontroller',
        hidden: true,
        padding: '15',
        bind: {
            store: '{theSequence.events}',
            hidden: '{gridHidden}'
        },
        rowViewModel: {
            type: 'events-event-view'
        },
        hideTools: false,
        plugins: [
            'gridfilters', {
                pluginId: 'forminrowwidget',
                ptype: 'forminrowwidget',
                expandOnDblClick: true,
                removeWidgetOnCollapse: true,
                widget: {
                    xtype: 'events-event-view',
                    controller: 'fields-schedulerdate-view',
                    viewModel: {
                        data: {
                            schedulerFieldPopup: true
                        }
                    }
                }
            }
        ]
    }],

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addConditionalVisibilityRules: Ext.emptyFn,

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addAutoValueRules: Ext.emptyFn
});
