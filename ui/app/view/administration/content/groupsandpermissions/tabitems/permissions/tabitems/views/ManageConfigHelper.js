Ext.define("CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ManageConfigHelper", {
    singleton: true,

    /**
    * On disabled action button click
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onManageConfigClick: function (grid, rowIndex, colIndex, button, event, record) {
        grid.setSelection(record);
        var formMode = grid.grid.getViewModel().get('actions');
        var fbar;
        var initValues = this.getConfigFieldsInitialValues(record);
        switch (formMode.edit) {
            case true:
                fbar = [{
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.cancel
                    },
                    reference: 'cancelbutton',
                    ui: 'administration-secondary-action',
                    viewModel: {},
                    listeners: {
                        click: function (_button, _event, eOpts) {
                            Ext.Array.forEach(Ext.Object.getKeys(initValues), function (element) {
                                record.set(element, initValues[element]);
                                if (record.previousValues && record.modified) {
                                    delete record.previousValues[element];
                                    delete record.modified[element];
                                }
                            });
                            CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.ok
                    },
                    reference: 'savebutton',
                    itemId: 'savebutton',
                    viewModel: {},
                    listeners: {
                        click: function (_button, _event, eOpts) {
                            var _record = this.getViewModel().get('record');
                            var fields = this.up('form').form.getFields().items;
                            if (Ext.Object.getSize(_record.modified)) {
                                Ext.Array.forEach(fields, function (element) {
                                    _record.set(element.config.field, element.checked);
                                });
                            }
                            CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
                        }
                    },
                    ui: 'administration-action'
                }];
                break;

            case false:
                fbar = [{
                    text: CMDBuildUI.locales.Locales.administration.common.actions.close,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.close
                    },
                    reference: 'closebutton',
                    ui: 'administration-secondary-action',
                    handler: function (_button) {
                        _button.up('#popup-grantconfig-config').fireEvent('close');
                    }
                }];
                break;
        }
        var content = {
            xtype: 'form',
            scrollable: 'y',
            reference: 'customPrivilegesChecks',
            bind: {
                actions: '{actions}',
                record: '{record}'
            },
            config: {
                selection: grid.getSelection(),
                record: grid.getSelection()
            },
            viewModel: {
                data: {
                    index: rowIndex
                }
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [{
                xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset',
                viewModel: {
                    data: {
                        grant: record,
                        actions: formMode
                    }
                }
            }, {
                xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
                viewModel: {
                    data: {
                        grant: record,
                        actions: formMode
                    }
                }
            }],
            fbar: fbar
        };
        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                var _record = this.getViewModel().get('record');
                Ext.Array.forEach(Ext.Object.getKeys(initValues), function (element) {
                    _record.set(element, initValues[element]);
                    if (_record.previousValues && record.modified) {
                        delete _record.previousValues[element];
                        delete _record.modified[element];
                    }
                });
                CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
            }
        };

        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-grantconfig-config',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.actions,
            content,
            listeners, {
            ui: 'administration-actionpanel',
            width: '600px',
            height: '70%',
            reference: 'popup-grantconfig-config',
            viewModel: {
                data: {
                    index: rowIndex,
                    grid: grid,
                    record: record
                }
            }
        }
        );
    }

});