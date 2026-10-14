Ext.define("CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ManageConfigHelper", {
    singleton: true,

    /**
    * On disabled action button click
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onManageConfigClick: function (grid, rowIndex, colIndex, button, event, record) {

        grid.setSelection(record);
        var me = this;
        var vm = grid.lookupViewModel();
        var grantRecord = vm.get('grantsChainedStore').findRecord('_id', record.get('_id'));
        var formMode = vm.get('actions');
        var fbar;

        var initValues = me.getConfigFieldsInitialValues(record);
        switch (formMode.edit) {
            case true:
                fbar = [{
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                    },
                    reference: 'cancelbutton',
                    ui: 'administration-secondary-action',
                    viewModel: {},
                    listeners: {
                        click: function (_button, _event, eOpts) {
                            me.setConfigInitValues(record, initValues);
                            me.setConfigInitValues(grantRecord, initValues);
                            CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
                    },
                    reference: 'savebutton',
                    itemId: 'savebutton',
                    viewModel: {},
                    listeners: {
                        click: function (_button, _event, eOpts) {
                            Ext.Object.getAllKeys(grantRecord.getData()).forEach(function (key) {
                                grantRecord.set(key, record.get(key));
                            });
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
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
                    },
                    reference: 'closebutton',
                    ui: 'administration-secondary-action',
                    handler: function (_button) {
                        _button.up('#popup-grantconfig-config').fireEvent('close');
                    }
                }];
                break;
        }
        var content = grid.grid.getActionContent(formMode, record, grid, rowIndex, fbar);

        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                me.setConfigInitValues(record, initValues);
                me.setConfigInitValues(grantRecord, initValues);
                CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
            }
        };

        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-grantconfig-config',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.configurations,
            content,
            listeners, {
            ui: 'administration-actionpanel',
            width: '600px',
            height: '70%',
            reference: 'popup-grantconfig-config'
        }
        );
    },

    /**
     *
     * @param {Ext.view.Table} view The owning TableView.
     * @param {Number} rowIndex The row index clicked on.
     * @param {Number} colIndex The column index clicked on.
     * @param {Object} item The clicked item (or this Column if multiple cfg-items were not configured).
     * @param {Event} e The click event.
     * @param {CMDBuildUI.model.users.Grant} record The Record underlying the clicked row.
     */
    onClearConfigClick: function (grid, rowIndex, colIndex, button, event, record) {
        var fields = this.getConfigFieldsByObjectType(record);
        var defaultValues = this.getConfigFieldsDefaultValues(record);
        var vm = grid.lookupViewModel();
        var grantRecord = vm.get('grantsChainedStore').findRecord('_id', record.get('_id'));
        Ext.Array.forEach(fields, function (field) {
            record.set(field, defaultValues[field]);
            grantRecord.set(field, defaultValues[field]);
        });
        record.set('dmsPrivileges', {});
        grantRecord.set('dmsPrivileges', {});
        record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
        grantRecord.crudState = grantRecord.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
    },

    /**
     *
     * @param {Ext.view.Table} view The owning TableView.
     * @param {Number} rowIndex The row index clicked on.
     * @param {Number} colIndex The column index clicked on.
     * @param {Object} item The clicked item (or this Column if multiple cfg-items were not configured).
     * @param {Event} e The click event.
     * @param {CMDBuildUI.model.users.Grant} record The Record underlying the clicked row.
     */
    onRemoveFilterActionClick: function (grid, rowIndex, colIndex, button, event, record) {
        if (record.previousValues && (record.previousValues.filter || record.previousValues.attributePrivileges || record.previousValues.gisPrivileges)) {
            delete record.previousValues.filter;
            delete record.previousValues.attributePrivileges;
            delete record.previousValues.gisPrivileges;
            if (record.modified) {
                delete record.modified.filter;
                delete record.modified.attributePrivileges;
                delete record.modified.gisPrivileges;
            }
        }

        var grantRecord = grid.lookupViewModel().get('grantsChainedStore').findRecord('_id', record.get('_id'));
        record.set('filter', '');
        record.set('attributePrivileges', {});
        grantRecord.set('filter', '');
        grantRecord.set('attributePrivileges', {});
        record.set('gisPrivileges', {});
        record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
        grantRecord.crudState = grantRecord.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
    }

});