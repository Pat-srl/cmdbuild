Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-jobruns-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender',
            selectionchange: 'onSelectionChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.tasks.jobruns.Grid} view
     */
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind(
            {
                bindTo: '{statusesStore}'
            },
            function (statusesStore) {
                var columns = [
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.jobcode,
                        dataIndex: 'jobCode',
                        filter: {
                            type: 'string',
                            dataIndex: 'jobCode'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.nodeid,
                        dataIndex: 'nodeId',
                        filter: {
                            type: 'string',
                            dataIndex: 'nodeId'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.common.labels.status,
                        dataIndex: 'status',
                        filter: {
                            type: 'list',
                            store: statusesStore,
                            idField: 'value',
                            labelField: 'label',
                            loadOnShow: false,
                            operator: 'in',
                            menuDefaults: {
                                scrollable: true,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretchmax',
                                    overflowHandler: null
                                }
                            }
                        },
                        renderer: function (value, cell, record) {
                            return record.get('_status_description');
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.timestamp,
                        dataIndex: 'timestamp',
                        filter: {
                            type: 'date'
                        },
                        renderer: function (value) {
                            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.elapsedmillis,
                        dataIndex: 'elapsedMillis',
                        renderer: function (value) {
                            return Ext.String.format('{0} ms', value);
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.errors,
                        dataIndex: 'errors',
                        menuDisabled: true,
                        sortable: false,
                        producesHTML: false,
                        renderer: function (value) {
                            return value.length;
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.jobruns.logs,
                        producesHTML: false,
                        dataIndex: 'logs',
                        sortable: false,
                        menuDisabled: true,
                        renderer: function (value) {
                            if (value && value.length) {
                                value = Ext.util.Format.substr(value, 0, 200);
                                return Ext.util.Format.stripTags(Ext.String.format('{0} ...', value));
                            }
                            return value;
                        }
                    }
                ];

                view.reconfigure(null, columns);
            }
        );
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.tasks.jobruns.Grid} view
     */
    onAfterRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind(
            {
                bindTo: '{statusesStore}'
            },
            function (statusesStore) {
                var column = view.getColumnManager().getColumns()[3];
                var columnFilter = column.filter;
                if (!columnFilter.menu) {
                    columnFilter.createMenu();
                }
                statusesStore.each(function (status) {
                    var filter = columnFilter.menu.down(
                        Ext.String.format('menuitem[value="{0}"]', status.get('value'))
                    );
                    filter.on('checkchange', function (input, value) {
                        var tool = view
                            .up('administration-content-tasks-jobruns-view')
                            .down(Ext.String.format('#{0}FilterTool', status.get('value')));
                        tool.setIconCls(
                            value
                                ? 'cmdbuildicon-filter-remove'
                                : CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid')
                        );
                    });
                });
            }
        );
    },
    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.administration.JobRunner[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        var me = this;
        if (selected.length) {
            Ext.asap(function () {
                me.getViewModel().set('theJobrun', selected[0]);
            });
        }
    }
});
