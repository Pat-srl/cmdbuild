Ext.define('CMDBuildUI.view.administration.content.emails.templates.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-grid',
    listen: {
        global: {
            templateupdated: 'onTemplateUpdated',
            templatecreated: 'onTemplateCreated',
            templatedestroyed: 'onTemplateDestroyed'
        }
    },
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblclick',
            deselect: 'onDeselect',
            select: 'onSelect'
        }
    },

    /**
     * @param {Ext.grid.Panel} view
     */
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        var columns = [
            {
                width: 100,
                text: CMDBuildUI.locales.Locales.administration.common.labels.type,
                dataIndex: 'provider',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                },
                hideable: false,
                hidden: true,
                bind: {
                    hidden: '{templateType != "all"}'
                },
                renderer: function (value) {
                    var valueIndex = Object.values(CMDBuildUI.model.emails.Template.providers).indexOf(value);
                    value = Object.keys(CMDBuildUI.model.emails.Template.providers)[valueIndex];
                    return CMDBuildUI.locales.Locales.administration.emails[value];
                }
            },
            {
                flex: 1,
                variableRowHeight: true,
                text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                dataIndex: 'name',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                }
            },
            {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.common.labels.description,
                dataIndex: 'description',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                }
            },
            {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.subject,
                dataIndex: 'subject',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                }
            },
            {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.to,
                dataIndex: 'to',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.to'
                }
            }
        ];
        if (vm.get('isEmailProvider')) {
            columns.splice(columns.length - 1, 0, {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
                dataIndex: '_account_description',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
                },
                hidden: true
            });
            columns.splice(columns.length - 1, 0, {
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.from,
                dataIndex: 'from',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.from'
                },
                hidden: true
            });
            columns.push({
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.cc,
                dataIndex: 'cc',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.cc'
                },
                hidden: true
            });
            columns.push({
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.bcc,
                dataIndex: 'bcc',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.bcc'
                },
                hidden: true
            });
            columns.push({
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.subject,
                dataIndex: 'subject',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                },
                hidden: true
            });
            columns.push({
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.emails.signature,
                dataIndex: '_signature_description',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.signature'
                },
                hidden: true
            });
        }
        view.reconfigure(view.getStore(), columns);
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {},

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {},
    /**
     *
     * @param {Ext.data.Model} record
     */
    onTemplateUpdated: function (record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view, record, this]);
    },

    /**
     *
     * @param {Ext.data.Model} record
     */
    onTemplateDestroyed: function (record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemremoved', [view, record, this]);
    },

    /**
     *
     * @param {Ext.data.Model} record
     */
    onTemplateCreated: function (record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [view, record, this]);
    },

    /**
     * @param {*} row
     * @param {*} record
     * @param {*} element
     * @param {*} rowIndex
     * @param {*} e
     * @param {*} eOpts
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);

        this.getView()
            .ownerGrid.getPlugin('administration-forminrowwidget')
            .view.fireEventArgs('togglerow', [null, record, rowIndex]);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-form',
            viewModel: {
                links: {
                    theTemplate: {
                        type: 'CMDBuildUI.model.emails.Template',
                        id: record.get('_id')
                    }
                },
                data: {
                    actions: {
                        view: !record.get('_can_write'),
                        add: false,
                        edit: record.get('_can_write')
                    }
                }
            }
        });
    }
});
