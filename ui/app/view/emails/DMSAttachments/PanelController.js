Ext.define('CMDBuildUI.view.emails.DMSAttachments.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-dmsattachments-panel',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#comboclass': {
            change: 'onComboClassChange'
        },
        '#saveBtn': {
            click: 'onSaveBtn'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.emails.Edit.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        vm.set('firstload', true);
        vm.bind(
            {
                store: '{attributeslist}',
                objectTypeName: '{objectTypeName}'
            },
            function (data) {
                if (data.store && data.objectTypeName) {
                    view.down('#comboclass').setValue(data.objectTypeName);
                }
            }
        );
    },

    /**
     * @param {Ext.form.field.ComboBox} combos
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     *
     */
    onComboClassChange: function (combo, newValue, oldValue, eOpts) {
        const comboClassSelection = combo.getSelection();
        if (comboClassSelection && newValue) {
            this.setContainerGrid(comboClassSelection.get('type'), newValue);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtn: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const attachPanel = this.getView();
        const attachmentStore = attachPanel.config.store;
        const attachmentsgrid = attachPanel.down('#attachmentgrid');
        const attachmentsSelected = attachmentsgrid.getSelection();
        const cardsgrid = attachPanel.down('#cardsgrid');
        const cardSelected = cardsgrid.getSelection();
        let objectTypeName;
        let objectId;

        if (!Ext.isEmpty(cardSelected)) {
            const cardSelect = cardSelected[0];
            objectTypeName = cardSelect.get('_type');
            objectId = cardSelect.getId();
        }

        attachmentsSelected.forEach(function (selatt) {
            if (attachmentStore.findRecord('name', selatt.get('name'))) {
                const w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.notifier.warning,
                    html: CMDBuildUI.locales.Locales.emails.alredyexistfile,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-circle', 'solid'),
                    align: 'br',
                    alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++
                });
                w.show();
            } else {
                selatt.set('objectTypeName', objectTypeName);
                selatt.set('objectId', objectId);
                selatt.set('DMSAttachment', true);
                selatt.set('newAttachment', true);
                attachmentStore.add(selatt.getData());
            }
        });

        CMDBuildUI.util.helper.FormHelper.endSavingForm();
        attachPanel.up('panel').close();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('panel').close();
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        const searchTerm = field.getValue();

        if (searchTerm) {
            const store = this.getView().down('#cardsgrid').getStore();
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        const store = this.getView().down('#cardsgrid').getStore();
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        field.reset();
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    privates: {
        setContainerGrid: function (typeSelected, newValue) {
            const view = this.getView();
            const vm = this.getViewModel();
            const classContainer = view.down('#classcontainer');
            const attachmentContainer = view.down('#attachmentcontainer');
            const preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(typeSelected, newValue);
            const sorters = [];
            let storetype;
            let object;

            // clear containers
            classContainer.removeAll(true);
            attachmentContainer.removeAll(true);

            if (CMDBuildUI.util.helper.ModelHelper.objecttypes.klass == typeSelected) {
                storetype = 'classes-cards';
                object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(newValue);
            } else if (CMDBuildUI.util.helper.ModelHelper.objecttypes.process == typeSelected) {
                storetype = 'processes-instances';
                object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue);
            }

            if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                preferences.defaultOrder.forEach(function (o) {
                    sorters.push({
                        property: o.attribute,
                        direction: o.direction === 'descending' ? 'DESC' : 'ASC'
                    });
                });
            } else if (object && object.defaultOrder().getCount()) {
                object
                    .defaultOrder()
                    .getRange()
                    .forEach(function (o) {
                        sorters.push({
                            property: o.get('attribute'),
                            direction: o.get('direction') === 'descending' ? 'DESC' : 'ASC'
                        });
                    });
            } else if (
                typeSelected !== CMDBuildUI.util.helper.ModelHelper.objecttypes.process &&
                !object.isSimpleClass()
            ) {
                sorters.push({
                    property: 'Description'
                });
            }

            // get columns for selected type
            CMDBuildUI.util.helper.GridHelper.getColumnsForType(typeSelected, newValue).then(function (columns) {
                CMDBuildUI.util.helper.ModelHelper.getModel(typeSelected, newValue).then(function (model) {
                    // define grid
                    const grid = classContainer.add({
                        xtype: 'grid',
                        itemId: 'cardsgrid',
                        scrollable: true,
                        maxHeight: 250,
                        bind: {
                            store: '{cardss}'
                        },
                        viewModel: {
                            stores: {
                                cardss: {
                                    type: storetype,
                                    model: model.getName(),
                                    autoLoad: true,
                                    autoDestroy: true,
                                    proxy: {
                                        type: 'baseproxy',
                                        url: model.getProxy().getUrl()
                                    },
                                    sorters: sorters,
                                    listeners: {
                                        beforeload: {
                                            fn: function (store, operation, eOpts) {
                                                if (
                                                    vm.get('objectType') == typeSelected &&
                                                    vm.get('objectTypeName') == newValue
                                                ) {
                                                    const selId = vm.get('objectId');
                                                    if (selId) {
                                                        const objectTypeName = vm.get('objectTypeName');
                                                        const objectType = vm.get('objectType');
                                                        let model;
                                                        // Get model for sorters
                                                        switch (objectType) {
                                                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                                                model =
                                                                    CMDBuildUI.util.helper.ModelHelper.getClassFromName(
                                                                        objectTypeName
                                                                    );
                                                                break;
                                                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                                                model =
                                                                    CMDBuildUI.util.helper.ModelHelper.getProcessFromName(
                                                                        objectTypeName
                                                                    );
                                                                break;
                                                            default:
                                                                return false;
                                                        }
                                                        const sorters =
                                                            CMDBuildUI.util.helper.GridHelper.getStoreSorters(model);
                                                        const attrs = Ext.Array.map(sorters, function (sorter) {
                                                            return sorter.property;
                                                        }).join(',');

                                                        // Setup the params for Ajax
                                                        const params = {
                                                            positionOf: selId,
                                                            positionOf_goToPage: true,
                                                            attrs: attrs
                                                        };
                                                        params[store.getProxy().getSortParam()] =
                                                            Ext.JSON.encode(sorters);
                                                        params[store.getProxy().getLimitParam()] = store.getPageSize();
                                                        params[store.getProxy().getFilterParam()] = store
                                                            .getAdvancedFilter()
                                                            .encode();

                                                        // Get position of card without store
                                                        Ext.Ajax.request({
                                                            url: store.getProxy().url,
                                                            method: 'GET',
                                                            params: params,
                                                            success: function (response, opts) {
                                                                const responseData = Ext.decode(response.responseText);
                                                                if (responseData.meta.positions[selId].found) {
                                                                    // After position has been found we can load the page
                                                                    store.load();
                                                                    store.on({
                                                                        load: {
                                                                            fn: function () {
                                                                                if (vm.get('firstload')) {
                                                                                    vm.set('firstload', false);
                                                                                    const selId = vm.get('objectId');
                                                                                    if (selId) {
                                                                                        const metaData =
                                                                                            responseData.meta;
                                                                                        // Override reader metadata
                                                                                        store
                                                                                            .getProxy()
                                                                                            .getReader().metaData =
                                                                                            metaData;
                                                                                        const posinfo =
                                                                                            metaData.positions[selId];

                                                                                        if (!posinfo.pageOffset) {
                                                                                            grid.setSelection(
                                                                                                store.getById(selId)
                                                                                            );
                                                                                        } else {
                                                                                            grid.ensureVisible(
                                                                                                posinfo.positionInTable,
                                                                                                {
                                                                                                    focus: true,
                                                                                                    select: true,
                                                                                                    callback: function (
                                                                                                        success,
                                                                                                        record,
                                                                                                        node
                                                                                                    ) {
                                                                                                        grid.setSelection(
                                                                                                            record
                                                                                                        );
                                                                                                    }
                                                                                                }
                                                                                            );
                                                                                        }
                                                                                    }
                                                                                }
                                                                            },
                                                                            scope: this,
                                                                            single: true
                                                                        }
                                                                    });
                                                                } else {
                                                                    Ext.asap(function () {
                                                                        CMDBuildUI.util.Notifier.showWarningMessage(
                                                                            CMDBuildUI.locales.Locales.common.grid
                                                                                .itemnotfound
                                                                        );
                                                                    });
                                                                }
                                                            }
                                                        });
                                                        grid.reconfigure(null, columns);
                                                        return false;
                                                    }
                                                } else {
                                                    vm.set('firstload', false);
                                                }
                                                grid.reconfigure(null, columns);
                                            },
                                            scope: this,
                                            single: true
                                        }
                                    }
                                }
                            }
                        },
                        listeners: {
                            selectionChange: function (selection, record, eOpts) {
                                attachmentContainer.removeAll(true);
                                if (!Ext.isEmpty(record)) {
                                    const rec = record[0];
                                    const cardId = rec.getId();
                                    const cardType = rec.get('_type');
                                    const proxyurl = CMDBuildUI.util.api.Classes.getAttachments(cardType, cardId);

                                    attachmentContainer.add({
                                        xtype: 'attachments-grid',
                                        itemId: 'attachmentgrid',
                                        viewModel: {
                                            stores: {
                                                attachments: {
                                                    type: 'attachments',
                                                    autoLoad: true,
                                                    autoDestroy: true,
                                                    proxy: {
                                                        url: proxyurl,
                                                        type: 'baseproxy',
                                                        extraParams: {
                                                            detailed: true
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                });
            });
        }
    }
});
