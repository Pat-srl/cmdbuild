Ext.define('CMDBuildUI.view.joinviews.configuration.items.AttributesChoice', {
    extend: 'Ext.form.FieldSet',

    requires: ['CMDBuildUI.view.joinviews.configuration.items.AttributesChoiceController'],

    alias: 'widget.joinviews-configuration-items-attributeschoice',
    controller: 'joinviews-configuration-items-attributeschoice',

    title: CMDBuildUI.locales.Locales.joinviews.attributeschoice,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.attributeschoice'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    bind: {
        ui: '{fieldsetUi}'
    },

    scrollable: true,

    items: [
        {
            xtype: 'container',
            itemId: 'warningattribute',
            height: '40px',
            margin: '0 20 5 5',
            ui: 'messagewarning',
            items: [
                {
                    ui: 'custom',
                    xtype: 'container',
                    html: CMDBuildUI.locales.Locales.joinviews.selectatleastoneattribute,
                    localized: {
                        html: 'CMDBuildUI.locales.Locales.joinviews.selectatleastoneattribute'
                    }
                }
            ]
        },
        {
            xtype: 'button',
            ui: 'secondary-action-small',
            itemId: 'checkedonly',
            margin: '0 0 5 5',
            text: CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly,
            localized: {
                text: 'CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly'
            },
            enableToggle: true
        },
        {
            xtype: 'grid',
            forceFit: true,
            ui: 'cmdbuildgrouping',
            itemId: 'attributegrid',

            selModel: {
                pruneRemoved: true, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                selType: 'checkboxmodel',
                checkOnly: true,
                mode: 'MULTI'
            },

            features: [
                {
                    ftype: 'grouping',
                    collapsible: true,
                    groupHeaderTpl: [
                        '<div>{[this.formatGroupLabel(values)]}</div>',
                        {
                            formatGroupLabel: function (data) {
                                return Ext.String.format(
                                    CMDBuildUI.locales.Locales.joinviews.attributesof,
                                    data.children[0].get('targetAlias')
                                );
                            }
                        }
                    ]
                }
            ],

            viewConfig: {
                markDirty: false
            },

            bind: {
                selection: '{selectedAttributes}',
                store: '{allAttributesStore}'
            },

            columns: [
                {
                    hideable: false,
                    text: CMDBuildUI.locales.Locales.joinviews.attribute,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.attribute'
                    },
                    dataIndex: '_attributeDescription'
                }
            ]
        }
    ],

    goingNextStep: function () {
        if (!Ext.isEmpty(this.down('#attributegrid').getSelection())) {
            return true;
        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(
                Ext.String.format(
                    '<span data-testid="message-window-text">{0}</span>',
                    CMDBuildUI.locales.Locales.joinviews.selectatleastoneattribute
                )
            );
        }
    }
});
