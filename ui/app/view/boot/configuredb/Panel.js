Ext.define('CMDBuildUI.view.boot.configuredb.Panel', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.boot.configuredb.PanelController', 'CMDBuildUI.view.boot.configuredb.PanelModel'],

    xtype: 'boot-configuredb-panel',
    alias: 'widget.boot-configuredb-panel',
    controller: 'boot-configuredb-panel',
    viewModel: {
        type: 'boot-configuredb-panel'
    },

    title: 'Configure',
    scrollable: true,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    items: [
        {
            xtype: 'formpaginationfieldset',
            title: 'Database',
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        flex: '0.5',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        layout: 'anchor'
                    },
                    items: [
                        {
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Type',
                                    valueField: 'value',
                                    displayField: 'description',
                                    allowBlank: false,
                                    tabIndex: 1,
                                    bind: {
                                        store: '{types}',
                                        value: '{values.configtype}'
                                    }
                                },
                                {
                                    xtype: 'filefield',
                                    fieldLabel: 'Backup file',
                                    tabIndex: 3,
                                    hidden: true,
                                    reference: 'upload',
                                    bind: {
                                        value: '{values.dbupload}',
                                        hidden: '{hiddenfields.filefield}'
                                    },
                                    validator: function (value) {
                                        var configtype = this.lookupViewModel().get('values.configtype');
                                        if (configtype === 'upload' && Ext.isEmpty(value)) {
                                            return 'Required';
                                        }
                                        return true;
                                    }
                                }
                            ]
                        },
                        {
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Name',
                                    allowBlank: false,
                                    tabIndex: 2,
                                    maskRe: /^[a-z0-9_]+$/,
                                    regex: /^[a-z0-9_]+$/,
                                    labelToolIconQtip:
                                        'Only lowercase letters, numbers and _ (underscore) are allowed.',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                        'question-circle',
                                        'solid'
                                    ),
                                    bind: {
                                        value: '{values.dbname}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'formpaginationfieldset',
            title: 'PostgreSQL connection',
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {
                        xtype: 'fieldcontainer',
                        columnWidth: 0.5,
                        flex: '0.5',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        layout: 'anchor'
                    },
                    items: [
                        {
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Host',
                                    allowBlank: false,
                                    tabIndex: 4,
                                    bind: {
                                        value: '{values.dbhost}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Username',
                                    allowBlank: false,
                                    tabIndex: 6,
                                    bind: {
                                        value: '{values.dbusername}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Admin username',
                                    allowBlank: true,
                                    tabIndex: 8,
                                    labelToolIconQtip: 'Required for database creation and other stuff.',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                        'question-circle',
                                        'solid'
                                    ),
                                    bind: {
                                        value: '{values.dbadminusername}'
                                    }
                                }
                            ]
                        },
                        {
                            items: [
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: 'Port',
                                    allowBlank: false,
                                    tabIndex: 5,
                                    maxValue: 65535,
                                    minValue: 1,
                                    mouseWheelEnabled: false,
                                    hideTrigger: true,
                                    keyNavEnabled: false,
                                    mouseWhellEnabled: false,
                                    bind: {
                                        value: '{values.dbport}'
                                    }
                                },
                                {
                                    xtype: 'passwordfield',
                                    fieldLabel: 'Password',
                                    allowBlank: false,
                                    tabIndex: 7,
                                    bind: {
                                        value: '{values.dbpassword}'
                                    }
                                },
                                {
                                    xtype: 'passwordfield',
                                    fieldLabel: 'Admin password',
                                    allowBlank: true,
                                    tabIndex: 9,
                                    labelToolIconQtip: 'Required for database creation and other stuff.',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                        'question-circle',
                                        'solid'
                                    ),
                                    bind: {
                                        value: '{values.dbadminpassword}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    buttons: [
        {
            text: 'Test database connection',
            ui: 'management-primary-outline-small',
            disabled: false,
            itemId: 'testConnectionBtn',
            bind: {
                disabled: '{testConnectionDisabled}'
            },
            minWidth: 220
        },
        {
            text: 'Configure',
            ui: 'management-primary-small',
            disabled: false,
            formBind: true,
            itemId: 'configureBtn',
            minWidth: 100
        }
    ]
});
