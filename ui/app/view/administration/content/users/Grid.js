(function () {
    var formInRowPlugin = 'forminrowwidget';
    Ext.define('CMDBuildUI.view.administration.content.users.Grid', {
        extend: 'Ext.grid.Panel',

        requires: [
            'CMDBuildUI.view.administration.content.users.GridController',
            'CMDBuildUI.view.administration.content.users.GridModel',

            // plugins
            'Ext.grid.filters.Filters',
            'CMDBuildUI.components.grid.plugin.FormInRowWidget'
        ],

        mixins: [
            'CMDBuildUI.mixins.grids.Grid'
        ],
        viewConfig: {
            markDirty: false
        },
        alias: 'widget.administration-content-users-grid',
        controller: 'administration-content-users-grid',
        viewModel: {
            type: 'administration-content-users-grid'
        },
        bind: {
            store: '{allUsers}',
            selection: '{selected}'
        },
        reserveScrollbar: true,
        columns: [{
            text: CMDBuildUI.locales.Locales.administration.emails.username,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.username'
            },
            dataIndex: 'username',
            filter: {
                type: 'string',
                dataIndex: 'username'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            filter: {
                type: 'string',
                dataIndex: 'description'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
            },
            dataIndex: 'email',
            filter: {
                type: 'string',
                dataIndex: 'email'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
            },
            dataIndex: '_language_description',
            hidden: true,
            menuDisabled: true,
            sortable: false,
            renderer: function (value, context) {
                var vm = this.lookupViewModel(),
                    record = context.record;
                vm.bind({
                    bindTo: '{languages}',
                    single: true
                }, function (languagesStore) {
                    var lang = languagesStore.findRecord('code', record.get('language'));
                    if (lang) {
                        record.set('_language_description', lang.get('description'));
                    }
                });
                return value;
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
            },
            dataIndex: 'initialPage',
            hidden: true,
            menuDisabled: true,
            sortable: false,
            renderer: function (value) {
                var object = value.split(':'),
                    objectType = object[0],
                    objectTypeName = object[1];
                return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(objectTypeName, objectType);
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup'
            },
            dataIndex: 'multiGroup',
            align: 'center',
            xtype: 'checkcolumn',
            sortable: false,
            hidden: true,
            headerCheckbox: false,
            renderer: function (value, cell, record) {
                cell.tdCls = 'x-item-disabled';
                if (Ext.isEmpty(value)) {
                    return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                }
                var klass = '';
                if (value) {
                    klass = 'x-grid-checkcolumn-checked';
                }
                return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
            },
            listeners: {
                beforecheckchange: function (column, rowIndex, checked, record) {
                    return false;
                }

            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
            },
            dataIndex: '_defaultUserGroup_description',
            hidden: true,
            sortable: false
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            align: 'center',
            xtype: 'checkcolumn',
            sortable: true,
            headerCheckbox: false,
            renderer: function (value, cell, record) {
                cell.tdCls = 'x-item-disabled';
                if (Ext.isEmpty(value)) {
                    return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                }
                var klass = '';
                if (value) {
                    klass = 'x-grid-checkcolumn-checked';
                }
                return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
            },
            listeners: {
                beforecheckchange: function (column, rowIndex, checked, record) {
                    return false;
                }

            }
        }],

        plugins: ['gridfilters', {
            ptype: formInRowPlugin,
            pluginId: formInRowPlugin,
            scrollIntoViewOnExpand: true,
            removeWidgetOnCollapse: true,
            widget: {
                xtype: 'administration-content-users-card-viewinrow',
                ui: 'administration-tabandtools',
                viewModel: {
                    data: {
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                },
                bind: {
                    theUser: '{selected}'
                }
            }
        }],
        config: {
            objectTypeName: null,
            allowFilter: true,
            showAddButton: true,
            selected: null,
            formInRowPlugin: formInRowPlugin
        },
        autoEl: {
            'data-testid': 'administration-content-users-grid'
        },

        forceFit: true,
        loadMask: true,

        selModel: {
            mode: 'multi',
            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
        },
        labelWidth: "auto",
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',

            items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text'
                },
                ui: 'administration-action-small',
                reference: 'adduser',
                itemId: 'adduser',
                autoEl: {
                    'data-testid': 'administration-user-toolbar-addUserBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }, {
                xtype: 'textfield',
                name: 'search',
                width: 250,
                emptyText: CMDBuildUI.locales.Locales.administration.users.toolbar.searchTextInput.emptyText,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.users.toolbar.searchTextInput.emptyText'
                },
                cls: 'administration-input',
                reference: 'searchtext',
                itemId: 'searchtext',
                bind: {
                    value: '{search.value}',
                    hidden: '{!canFilter}'
                },
                listeners: {
                    specialkey: 'onSearchSpecialKey'
                },
                triggers: {
                    search: {
                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                        handler: 'onSearchSubmit',
                        autoEl: {
                            'data-testid': 'administration-user-toolbar-form-search-trigger'
                        }
                    },
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear',
                        autoEl: {
                            'data-testid': 'administration-user-toolbar-form-clear-trigger'
                        }
                    }
                },
                autoEl: {
                    'data-testid': 'administration-user-toolbar-search-form'
                }
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'tbtext',
                dock: 'right',
                itemId: 'userGridCounter'
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.toolbar.onlyactiveusers,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.toolbar.onlyactiveusers'
                },
                labelAlign: 'left',
                labelStyle: 'width:auto',
                labelWidth: false,
                value: false,
                listeners: {
                    change: 'onOnlyActiveUsersChange'
                }
            }]
        }]
    });
})();