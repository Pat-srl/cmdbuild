Ext.define('CMDBuildUI.view.dms.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-grid',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            rowdblclick: 'onRowDblClick',
            columnhide: 'onColumnVisibility',
            columnshow: 'onColumnVisibility',
            itemclick: 'onItemClick'
        }
    },

    /**
     * Intercept row interation to update record inside view model
     * @param {Ext.app.View} view
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemClick: function (view, record, item, index, e, eOpts) {
        view.lookupViewModel().set('record', record);
    },

    onAfterRender: function (view, eOpts) {
        view.getSelectionModel().excludeToggleOnColumn = [view.getVisibleColumns().length - 1];
    },

    /**
     *
     * @param {Ext.grid.header.Container} header
     * @param {Ext.grid.column.Column} column
     * @param {Object} eOpts
     */
    onColumnVisibility: function (header, column, eOpts) {
        const grid = header.ownerCt,
            excludeToggleOnColumn = [];

        Ext.Array.forEach(grid.getVisibleColumns(), function (item, index, allitems) {
            if (item.dataIndex === '_checkAttachment' || item.text === CMDBuildUI.locales.Locales.attachments.preview) {
                excludeToggleOnColumn.push(index);
            }
        });

        // update exclude toggle columns
        grid.getSelectionModel().excludeToggleOnColumn = excludeToggleOnColumn;
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const gridContainer = view.up('dms-container');
        const title =
                CMDBuildUI.locales.Locales.attachments.editattachment +
                ' ' +
                record.get('_category_description_translation'),
            attachmentsStore = vm.get('attachments'),
            attachmentName = record.get('name'),
            invalidFileNames = Ext.Array.remove(attachmentsStore.collect('name'), attachmentName);

        var theObject;

        if (vm.get('readOnly') || !vm.get('basepermissions.edit') || !record.get('_can_update')) {
            return false;
        }

        if (record.phantom || record.dirty) {
            theObject = record;
        }

        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-attachment-form',
            title,
            {
                xtype: 'dms-attachment-edit',
                ignoreSchedules: gridContainer.getIgnoreSchedules(),
                asyncStore: gridContainer.getIsAsyncSave() ? attachmentsStore : null,
                invalidFileNames: invalidFileNames,
                currentFileName: attachmentName,
                viewModel: {
                    data: {
                        objectType: vm.get('objectType'),
                        objectTypeName: vm.get('objectTypeName'),
                        objectId: vm.get('objectId'),
                        attachmentId: record.getId(),
                        DMSCategoryTypeName: vm.get('DMSCategoryTypeName'),
                        DMSCategoryValue: record.get('category'),
                        theObject: theObject
                    }
                }
            },
            {
                popupsave: {
                    fn: function () {
                        attachmentsStore.load();
                    },
                    scope: this
                },
                popupcancel: function () {}
            }
        );
    }
});
