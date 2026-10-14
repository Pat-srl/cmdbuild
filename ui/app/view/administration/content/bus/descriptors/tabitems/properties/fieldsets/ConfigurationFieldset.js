Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.fieldset.ConfigurationFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-bus-descriptors-tabitems-properties-fieldsets-configurationfieldset',

    viewModel: {

    },
    ui: 'administration-formpagination',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.bus.configuration,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.bus.configuration'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 1,
            xtype: 'fieldcontainer',
            items: [{
                xtype: 'aceeditortextarea',
                allowBlank: true,
                vmObjectName: 'theDescriptor',
                inputField: 'data',
                itemId: 'administration-content-bus-descriptors-configuration-editor',
                autoEl: {
                    'data-testid': 'administration-content-bus-descriptors-configuration-editor'
                },
                options: {
                    mode: 'ace/mode/yaml',
                    readOnly: true
                },
                bind: {
                    readOnly: '{actions.view}',
                    config: {
                        options: {
                            readOnly: '{actions.view}'
                        }
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.bus.yaml,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.yaml'
                },
                minHeight: '400px',
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                labelToolIconClick: 'onAceEditorHelpExpand',
                listeners: {
                    render: function (element) {
                        var aceEditor = element.getAceEditor();
                        var vm = element.lookupViewModel();
                        vm.bind({
                            bindTo: {
                                theDescriptor: '{theDescriptor}'
                            },
                            single: true
                        }, function (data) {
                            if (data.theDescriptor) {
                                aceEditor.setValue(data.theDescriptor.get('data'), -1);
                            }
                        });
                        vm.bind({
                            isView: '{actions.view}'
                        }, function (data) {
                            aceEditor.setReadOnly(data.isView);
                        });
                        aceEditor.getSession().on('change', function (event, _editor) {
                            vm.set('theDescriptor.data', _editor.getValue());
                        });
                    }
                },
                name: 'help',
                width: '100%'
            }]
        }, {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            items: [{
                xtype: 'filefield',
                name: 'file',
                columnWidth: 0.5,
                padding: '0 15 0 0',
                itemId: 'file',
                reference: 'file',
                fieldLabel: CMDBuildUI.locales.Locales.administration.bus.file,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.file'
                },
                buttonConfig: {
                    ui: 'administration-secondary-action-small'
                },
                accept: '.yaml',
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                }
            }]
        }]
    }]
});