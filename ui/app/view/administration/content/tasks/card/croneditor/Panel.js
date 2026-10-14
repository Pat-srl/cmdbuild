Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.Panel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-card-croneditor-panel',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.PanelController',
        'CMDBuildUI.view.administration.content.tasks.card.croneditor.PanelModel',
        'CMDBuildUI.util.helper.FormHelper',
        'CMDBuildUI.util.administration.helper.CronValidatorHelper'
    ],
    controller: 'administration-content-tasks-card-croneditor-panel',
    viewModel: {
        type: 'administration-content-tasks-card-croneditor-panel'
    },
    config: {
        theTask: null
    },

    bind: {
        theTask: '{theTask}'
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,

    layout: 'column',
    columnWidth: 1,
    items: [{
        columnWidth: 1,
        variableHeights: true,
        xtype: 'grid',
        hidden: true,
        bind: {
            store: '{cronStore}',
            hidden: '{actions.view}'
        },
        cls: 'administration-reorder-grid',
        rowLines: false,
        overItemClass: 'null',
        markDirty: false,
        headerBorders: false,
        sealedColumns: false,
        sortableColumns: false,
        enableColumnHide: false,
        enableColumnMove: false,
        enableColumnResize: false,
        columns: [{
            xtype: 'widgetcolumn',
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.minutes,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.minutes'
            },
            dataIndex: 'minutes',
            widget: {
                xtype: 'textfield',
                dataIndex: 'minutes',
                relatedTab: 0,
                listeners: {
                    focus: 'onPartFocus',
                    blur: function () {
                        var vm = this.lookupViewModel();
                        vm.set('cronParts.minutes', this.getValue());
                    }
                },
                validator: function (value) {
                    return CMDBuildUI.util.administration.helper.CronValidatorHelper.hasValidMinutes(value) || 'Invalid value';
                }
            }
        }, {
            xtype: 'widgetcolumn',
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.hours,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.hours'
            },
            dataIndex: 'hours',
            widget: {
                xtype: 'textfield',
                dataIndex: 'hours',
                relatedTab: 1,
                listeners: {
                    focus: 'onPartFocus',
                    blur: function () {
                        var vm = this.lookupViewModel();
                        vm.set('cronParts.hours', this.getValue());
                    }
                },
                validator: function (value) {
                    return CMDBuildUI.util.administration.helper.CronValidatorHelper.hasValidHours(value) || 'Invalid value';
                }
            }
        }, {
            xtype: 'widgetcolumn',
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.dayofmonth,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.dayofmonth'
            },
            dataIndex: 'dayOfTheMonth',
            widget: {
                xtype: 'textfield',
                dataIndex: 'dayOfTheMonth',
                relatedTab: 2,
                listeners: {
                    focus: 'onPartFocus',
                    blur: function () {
                        var vm = this.lookupViewModel();
                        vm.set('cronParts.dayOfTheMonth', this.getValue());
                    }
                },
                validator: function (value) {
                    return CMDBuildUI.util.administration.helper.CronValidatorHelper.hasValidDays(value, true) || 'Invalid value';
                }
            }
        }, {
            xtype: 'widgetcolumn',
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.month,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.month'
            },
            dataIndex: 'month',
            paddingLeft: 0,
            widget: {
                xtype: 'textfield',
                dataIndex: 'month',
                relatedTab: 3,
                listeners: {
                    focus: 'onPartFocus',
                    blur: function () {
                        var vm = this.lookupViewModel();
                        vm.set('cronParts.month', this.getValue());
                    }
                },
                validator: function (value) {
                    return CMDBuildUI.util.administration.helper.CronValidatorHelper.hasValidMonths(value, true) || 'Invalid value';
                }
            }
        }, {
            xtype: 'widgetcolumn',
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.dayofweek,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.dayofweek'
            },
            dataIndex: 'dayOfTheWeek',
            widget: {
                xtype: 'textfield',
                dataIndex: 'dayOfTheWeek',
                relatedTab: 2,
                listeners: {
                    focus: 'onPartFocus',
                    blur: function () {
                        var vm = this.lookupViewModel();
                        vm.set('cronParts.dayOfTheWeek', this.getValue());
                    }
                },
                validator: function (value) {
                    return CMDBuildUI.util.administration.helper.CronValidatorHelper.hasValidWeekdays(value, true, true) || 'Invalid value';
                }
            }
        }]
    }, {
        columnWidth: 1,
        variableHeights: true,
        xtype: 'grid',
        hidden: true,
        bind: {
            store: '{cronStore}',
            hidden: '{!actions.view}'
        },
        cls: 'administration-reorder-grid',
        rowLines: false,
        overItemClass: 'null',
        markDirty: false,
        headerBorders: false,
        sealedColumns: false,
        sortableColumns: false,
        enableColumnHide: false,
        enableColumnMove: false,
        enableColumnResize: false,
        columns: [{
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.minutes,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.minutes'
            },
            dataIndex: 'minutes'
        }, {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.hours,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.hours'
            },
            dataIndex: 'hours'
        }, {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.dayofmonth,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.dayofmonth'
            },
            dataIndex: 'dayOfTheMonth'
        }, {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.month,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.month'
            },
            dataIndex: 'month'
        }, {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.tasks.dayofweek,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.dayofweek'
            },
            dataIndex: 'dayOfTheWeek'
        }]
    }, {
        // tabpanel
        xtype: 'administration-content-tasks-card-croneditor-tabpanel',
        layout: 'column',
        columnWidth: 1,
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        }
    }]
});