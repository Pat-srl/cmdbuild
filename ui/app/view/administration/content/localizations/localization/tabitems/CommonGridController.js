Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-tabitems-commongrid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            cellclick: 'onCellClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGrid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().set('section', view.getSection());
        Ext.asap(function () {
            CMDBuildUI.util.Utilities.showLoader(true, view);
        });
    },

    /**
     *
     * @param {Ext.view.Table} table
     * @param {HTMLElement} td
     * @param {Number} cellindex
     * @param {Ext.data.Model} record
     * @param {HTMLElement} tr
     * @param {Number} rowindex
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onCellClick: function (table, td, cellIndex, record, tr, rowIndex, event, eOpts) {
        if (event.getTarget('.fa-expand')) {
            const dataIndex = this.getView().getColumns()[cellIndex].dataIndex;
            this.showEditPopup(record, dataIndex);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const grid = this.getView();
        const vm = this.getViewModel();
        vm.set('actions.view', true);
        this.getView()
            .getColumns()
            .forEach(function (column) {
                if (!column.locked) {
                    column.setEditor(false);
                }
            });
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        grid.getStore().reload();
        vm.getParent().toggleEnableTabs();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const grid = this.getView();
        const vm = this.getViewModel();
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.getParent().toggleEnableTabs();
        grid.getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(false);
            }
        });

        const modifiedRecords = vm.get('localizationsStore').getModifiedRecords();
        let requestsCount = 0;
        modifiedRecords.forEach(function (record) {
            const data = {};
            const languges = record.get('values');
            Ext.Object.each(languges, function (key, value, myself) {
                data[key] = value;
            });
            const code = record.get('code');
            requestsCount++;
            Ext.Ajax.request({
                url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, code),
                method: 'PUT',
                jsonData: data,
                callback: function () {
                    requestsCount--;
                    if (requestsCount === 0) {
                        grid.getStore().reload();
                    }
                }
            });
        });
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onStoreLoaded: function (store, records, successful, operation, eOpts) {
        const grid = this.getView();
        const vm = this.getViewModel();

        const languagesStore = vm.get('languages');
        if (languagesStore.isLoaded()) {
            const renderer = function (value, metadata, record, rowindex, colindex, store, view, ciao) {
                if (value && this.section == 'notifications' && record.get('code').endsWith('body')) {
                    return `
                        <div style="display:flex; flex-direction:column;">
                            <span style="
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                display: block;
                                width: 100%;
                            ">${CMDBuildUI.util.Utilities.extractTextFromHTML(value)}</span>
                            <span class="x-fas fa-expand" style="text-align: right; cursor:pointer;"></span>
                        </div>
                    `;
                } else {
                    return value;
                }
            };
            const languageRecords = languagesStore.getRange();
            const columns = [
                {
                    text: CMDBuildUI.locales.Locales.administration.localizations.element,
                    dataIndex: 'element',
                    align: 'left',
                    locked: true,
                    width: 200
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.type,
                    dataIndex: 'type',
                    align: 'left',
                    locked: true,
                    width: 200
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.code,
                    dataIndex: 'code',
                    align: 'left',
                    locked: true,
                    hidden: true,
                    width: 200
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.localizations.defaulttranslation,
                    dataIndex: 'default',
                    align: 'left',
                    locked: true,
                    width: 200,
                    renderer: renderer
                }
            ];

            languageRecords.forEach(function (record) {
                const lang = record.get('description');
                const code = record.get('code');
                const flag =
                    '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' +
                    code +
                    '.png" />';

                const column = {
                    text: flag + lang,
                    dataIndex: code,
                    align: 'left',
                    locked: false,
                    renderer: renderer
                };

                if ((grid.getWidth() - 600) / languageRecords.length < 150) {
                    column.minWidth = 150;
                    column.maxWidth = 150;
                } else {
                    column.flex = 1;
                }

                columns.push(column);
            });
            grid.reconfigure(store, columns);
            CMDBuildUI.util.Utilities.showLoader(false, grid);
        }
    },

    /**
     *
     * @param {Ext.grid.plugin.Editing} editor
     * @param {Object} context
     * @param {Object} eOpts
     * @returns
     */
    onBeforeEdit: function (editor, context, eOpts) {
        const record = context.record;
        if (this.getView().section == 'notifications' && record.get('code').endsWith('body')) {
            if (!context.value) {
                this.showEditPopup(record, context.field);
            }
            return false;
        }
    },

    /**
     *
     * @param {Ext.grid.plugin.Editing} editor
     * @param {Object} context
     * @param {Object} eOpts
     * @returns
     */
    editedCell: function (editor, context, eOpts) {
        const field = context.field;
        const modvalue = context.value;
        const store = this.getViewModel().get('localizationsStore');
        const key = context.record.get('code');

        const res = store.findRecord('code', key);
        if (res && res.get('values')[field] !== modvalue) {
            res.crudState = 'U';
            res.dirty = true;
            res.get('values')[field] = modvalue;
        }
    },

    privates: {
        showEditPopup: function (record, dataIndex) {
            const vm = this.getViewModel();
            const readOnly = vm.get('actions.view') || dataIndex == 'default';
            const config = {
                xtype: 'panel',
                layout: 'fit',
                items: CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    value: record.get(dataIndex),
                    readOnly: readOnly
                })
            };

            if (!readOnly) {
                config.buttons = [
                    {
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                        },
                        ui: 'administration-secondary-action-small',
                        handler: function () {
                            popup.close();
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.common.actions.save,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.save'
                        },
                        ui: 'administration-action-small',
                        handler: function (button, event, eOpts) {
                            const modvalue = button.up('panel').down('cmdbuildhtmleditor').getValue();
                            const store = vm.get('localizationsStore');
                            const key = record.get('code');

                            const res = store.findRecord('code', key);
                            if (res && res.get('values')[dataIndex] !== modvalue) {
                                res.crudState = 'U';
                                res.dirty = true;
                                res.get('values')[dataIndex] = modvalue;
                                record.set(dataIndex, modvalue);
                            }
                            popup.close();
                        }
                    }
                ];
            }

            const popup = CMDBuildUI.util.Utilities.openPopup(null, record.get('type'), config, null, {
                ui: 'administration',
                width: '60%',
                height: '60%'
            });
        }
    }
});
