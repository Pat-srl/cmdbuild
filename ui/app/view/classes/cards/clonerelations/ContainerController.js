Ext.define('CMDBuildUI.view.classes.cards.clonerelations.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-clonerelations-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        'form': {
            validitychange: 'onValidityChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.classes.cards.clonerelations.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const panel = view.down('#classes-cards-card-create');
        if (view.onAfterSave) {
            panel.setOnAfterSave(view.onAfterSave);
        }
        if (view.defaultValues) {
            panel.setDefaultValues(view.defaultValues);
        }
        if (Ext.isBoolean(view.fireGlobalEventsAfterSave)) {
            panel.setFireGlobalEventsAfterSave(view.fireGlobalEventsAfterSave);
        }
    },

    /**
     * Cancel button
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.getView().up('#CMDBuildManagementDetailsWindow').close();
    },

    /**
     * Save and Close button
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, event, eOpts) {
        this.saveAction(button, this.getView().down('#savebtn'));
    },

    /**
     * Save button
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        this.saveAction(button, this.getView().down('#saveandclosebtn'));
    },

    /**
     *
     * @param {Ext.form.Basic} form
     * @param {Boolean} valid
     * @param {Object} eOpts
     */
    onValidityChange: function (form, valid, eOpts) {
        this.disableSaveButton(form, this.getView().down('#classes-cards-clonerelations-panel').getStore());
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {Object} details
     * @param {Object} eOpts
     */
    onStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        this.disableSaveButton(this.getView().down('#classes-cards-card-create'), store);
    },

    onHeaderCheckChange: function (columnHeader, checked) {
        const store = this.getViewModel().get('relations');
        const records = store.getRange();
        if (checked) {
            records.forEach(function (record) {
                switch (columnHeader.dataIndex) {
                    case 'ignore':
                        record.set('ignore', checked);
                        record.set('migrates', !checked);
                        record.set('clone', !checked);
                        record.set('mode', CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore);
                        break;
                    case 'migrates':
                        record.set('ignore', !checked);
                        record.set('migrates', checked);
                        record.set('clone', !checked);
                        record.set('mode', CMDBuildUI.util.helper.FiltersHelper.cloneFilters.migrates);
                        break;
                    case 'clone':
                        if (!record.get('isDisabled')) {
                            record.set('ignore', !checked);
                            record.set('migrates', !checked);
                            record.set('clone', checked);
                            record.set('mode', CMDBuildUI.util.helper.FiltersHelper.cloneFilters.clone);
                            break;
                        }
                }
            });
        }

        const grid = this.getView().down('#classes-cards-clonerelations-panel');
        if (grid) {
            const checkColumns = grid.query('checkcolumn');
            Ext.Array.each(checkColumns, function (column) {
                column.updateHeaderState();
            });
        }
    },

    onCheckChange: function (column, rowindex, checked, record) {
        if (checked && !record.get(column.dataIndex)) {
            record.set('ignore', !checked);
            record.set('migrates', !checked);
            record.set('clone', checked);
            record.set('mode', CMDBuildUI.util.helper.FiltersHelper.cloneFilters[column.dataIndex]);
        }
        const grid = column.up('grid');
        if (grid) {
            const checkColumns = grid.query('checkcolumn');
            Ext.Array.each(checkColumns, function (column) {
                column.updateHeaderState();
            });
        }
    },

    privates: {
        /**
         *
         * @param {Ext.button.Button} button
         * @param {Ext.button.Button} otherButton
         */
        saveAction: function (button, otherButton) {
            const me = this;
            const view = this.getView();
            const vm = this.getViewModel();
            const domains = vm.get('relations').getRange();
            const objectId = vm.get('objectId');
            const BreakException = {};
            const cancelBtn = view.down('#cancelbtn');

            button.showSpinner = true;
            CMDBuildUI.util.Utilities.disableFormButtons([button, otherButton, cancelBtn]);

            try {
                domains.forEach(function (domain) {
                    if (!domain.get('mode')) {
                        CMDBuildUI.util.Notifier.showWarningMessage(
                            'Cannot save data, please make sure you selected an action for every domain'
                        );
                        BreakException[0] = 'error';
                        throw BreakException;
                    }
                });
            } catch (e) {
                if (e !== BreakException) throw e;
            }

            if (!BreakException[0]) {
                const formcontroller = view.down('#classes-cards-card-create').getController();
                formcontroller
                    .saveForm({
                        failure: function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.enableFormButtons([button, otherButton, cancelBtn]);
                        }
                    })
                    .then(function (record) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        const clonedDomains = me.domainsFilter(domains, 'clone');
                        const migratesDomains = me.domainsFilter(domains, 'migrates');
                        const urlClone = Ext.String.format(
                            CMDBuildUI.util.Config.baseUrl + '/domains/_ANY/relations/_ANY/copy'
                        );
                        const urlMigrate = Ext.String.format(
                            CMDBuildUI.util.Config.baseUrl + '/domains/_ANY/relations/_ANY/move'
                        );
                        const destination = record.getId();

                        Ext.Promise.all([
                            me.saveDomains(objectId, destination, clonedDomains, urlClone),
                            me.saveDomains(objectId, destination, migratesDomains, urlMigrate)
                        ]).then(function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            if (button.getItemId()) {
                                let url;
                                switch (button.getItemId()) {
                                    case 'savebtn':
                                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(
                                            record.get('_type'),
                                            record.getId(),
                                            'view'
                                        );
                                        me.redirectTo(url);
                                        break;
                                    case 'saveandclosebtn':
                                        // close details window
                                        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                                        // redirect to the card

                                        url = CMDBuildUI.util.Navigation.getClassBaseUrl(
                                            record.get('_type'),
                                            record.getId()
                                        );
                                        me.redirectTo(url);
                                        break;
                                }
                            }
                        });
                    })
                    .otherwise(function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.enableFormButtons([button, otherButton, cancelBtn]);
                    });
            }
        },

        /**
         * Filter domain array with given key
         * @param {Array} domain
         * @param {String} filter
         *
         * @returns {filtered Array}
         */
        domainsFilter: function (domains, filter) {
            const result = [];
            domains.forEach(function (domain) {
                if (domain.get('mode') == filter) {
                    result.push({
                        _id: domain.get('domain'),
                        direction: domain.get('direction')
                    });
                }
            });
            return result;
        },

        /**
         * Async save call for domains
         * @param {String} source
         * @param {String} destination
         * @param {Array} domains
         * @param {String} url
         *
         * @returns {Ext.Ajax.request}
         */
        saveDomains: function (source, destination, domains, url) {
            if (Ext.isEmpty(domains)) {
                const deferred = new Ext.Deferred();
                deferred.resolve();
                return deferred;
            }

            return Ext.Ajax.request({
                url: url,
                method: 'POST',
                jsonData: {
                    source: source,
                    destination: destination,
                    domains: domains
                }
            });
        },

        /**
         *
         * @param {Ext.form.Basic} form
         * @param {Ext.data.Store} store
         */
        disableSaveButton: function (form, store) {
            const formValid = !form.hasInvalidField();
            const storeValid = this._isStoreValid(store);

            this.getViewModel().set('saveButtonDisabled', !(formValid && storeValid));
        },

        /**
         *
         * @param {Ext.data.Store} store
         * @returns
         */
        _isStoreValid: function (store) {
            const records = store.getRange();
            for (let i = 0; i < records.length; i++) {
                const record = records[i];

                if (!record.hasChecks()) {
                    return false;
                }
            }

            return true;
        }
    }
});
