Ext.define('CMDBuildUI.view.administration.content.bus.messages.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-messages-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.bus.messages.Grid} view
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
                        text: CMDBuildUI.locales.Locales.administration.busmessages.messageid,
                        dataIndex: 'messageId',
                        filter: {
                            type: 'string',
                            dataIndex: 'messageId'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.busmessages.nodeid,
                        dataIndex: 'nodeId',
                        filter: {
                            type: 'string',
                            dataIndex: 'nodeId'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.busmessages.queue,
                        dataIndex: 'queue',
                        filter: {
                            type: 'string',
                            dataIndex: 'queue'
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
                        text: CMDBuildUI.locales.Locales.administration.busmessages.timestamp,
                        dataIndex: 'timestamp',
                        filter: {
                            type: 'date'
                        },
                        renderer: function (value) {
                            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                        }
                    }
                ];

                view.reconfigure(null, columns);
            }
        );
    },
    /**
     *
     * @param {CMDBuildUI.view.administration.content.bus.messages.Grid} view
     */
    onAfterRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind(
            {
                bindTo: '{statusesStore}'
            },
            function (statusesStore) {
                var column = view.getColumnManager().getColumns()[4];

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
                            .up('administration-content-bus-messages-view')
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
    }
});
