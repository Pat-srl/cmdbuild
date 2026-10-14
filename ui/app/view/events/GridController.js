Ext.define('CMDBuildUI.view.events.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-grid',
    listen: {
        global: {
            carddeleted: 'onCardDeleted',
            cardcreated: 'onCardCreated',
            cardupdated: 'onCardUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick',
            selectionchange: 'onSelectionChange'
        },
        'tableview': {
            afterRender: 'onAfterRenderTableView',
            expandbody: 'onExpandBody'
        }
    },

    /**
     * @param {} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
        const vm = this.getViewModel();
        const objectType = vm.get('objectType');
        const objectTypeName = vm.get('objectTypeName');
        const columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
            objectType: objectType,
            objectTypeName: objectTypeName,
            reducedGrid: !view.getMaingrid(),
            preferences: CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName)
        });

        //This code make possible change the color for the column "missingDays" to set it red if che value is < 0
        const missingdayscol = Ext.Array.findBy(
            columns,
            function (element, index) {
                if (element.dataIndex == 'missingDays') {
                    return true;
                }
            },
            this
        );
        missingdayscol.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            var renderValue;
            if (value) {
                var color = value < 0 ? 'red' : 'black',
                    content = '<span style="color:' + color + ';">' + value + '</span>';
                renderValue = Ext.String.format('<div class="{0}cell-content">{1}</div>', Ext.baseCSSPrefix, content);
            }
            return renderValue;
        };

        Ext.Array.forEach(columns, function (item, index) {
            if (['Type', 'missingDays'].indexOf(item.dataIndex) > -1) {
                item.sortable = false;
            }
        });

        view.reconfigure(null, columns);
        // hide selection column
        setTimeout(function () {
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
        }, 200);
    },

    /**
     *
     * @param {Ext.View.Table} tableView
     * @param {Object} eOpts
     */
    onAfterRenderTableView: function (tableView, eOpts) {
        const loadMask = CMDBuildUI.util.Utilities.addLoadMask(tableView);
        this.getViewModel().bind(this.getView().getInitialConfig().bind.store, function (store) {
            if (store) {
                if (store.isLoaded()) {
                    CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                } else {
                    store.addListener('load', function (store, records, successful, operation, eOpts) {
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    });
                }
            }
        });
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                record.getId(),
                CMDBuildUI.mixins.DetailsTabPanel.actions.edit
            ),
            true
        );
        return false;
    },

    /**
     * @param {CMDBuildUI.model.calendar.Event} record
     */
    onCardUpdated: function (record) {
        this.getView().updateRowWithExpader(record);
    },

    /**
     *
     */
    onCardDeleted: function () {
        const store = this.getView().getStore();
        store.load();
    },

    /**
     *
     * @param {CMDBuildUI.model.calendar.Event} record
     */
    onCardCreated: function (record) {
        const view = this.getView(),
            store = view.getStore(),
            newid = record.getId();

        // add event listener. Use event listener instaed of callback
        // otherwise the load listener used within afterLoadWithPosition
        store.on({
            load: {
                fn: function () {
                    view.expandRowAfterLoadWithPosition(store, newid); //TODO: make an afterLoadWithPosition function like in class/gridController.i
                },
                scope: this,
                single: true
            }
        });

        store.load({
            proxy: {
                extraParams: {
                    positionOf: record.getId(),
                    positionOf_goToPage: false
                }
            }
        });
    },

    /**
     *
     * @param {*} view
     * @param {*} selected
     * @param {*} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        const view = this.getView(),
            vm = this.getViewModel();
        if (view.isMultiSelectionEnabled()) {
            return;
        } else {
            var selectedId = null;
            if (selected.length == 1) {
                selectedId = selected[0].getId();
            }
            vm.set('selectedId', selectedId);

            if (view.getMaingrid()) {
                if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {
                    if (!Ext.util.History.currentToken.includes(selectedId)) {
                        //if the url is not yet set from other components

                        const currentAction = CMDBuildUI.util.Navigation.getCurrentContext().currentaction,
                            url = CMDBuildUI.util.Navigation.getScheduleBaseUrl(selectedId, currentAction, false);

                        Ext.util.History.add(url);
                    }
                }
            }
        }
    },

    /**
     *
     * @param {*} rowNode
     * @param {*} record
     * @param {*} expandRow
     * @param {*} eOpts
     */
    onExpandBody: function (rowNode, record, expandRow, eOpts) {
        if (this.getView().isMultiSelectionEnabled()) {
            this.getViewModel().set('selectedId', record.getId());
        }
    }
});
