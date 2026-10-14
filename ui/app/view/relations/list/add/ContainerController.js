Ext.define('CMDBuildUI.view.relations.list.add.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-add-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        },
        'tableview': {
            selectioncheckrendered: 'onSelectionCheckRendered'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.add.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = view.lookupViewModel();
        vm.set('originId', view.getOriginId());
        vm.set('singleSelection', !view.getMultiSelect());
        const mode = view.getMultiSelect() ? 'MULTI' : 'SINGLE';
        const domain = vm.get('theDomain');
        me._relattrs = [];

        Ext.Promise.all([
            CMDBuildUI.util.helper.GridHelper.getColumnsForType(vm.get('objectType'), vm.get('objectTypeName'), {
                allowFilter: true
            }),
            domain.getAttributes()
        ]).then(function (res) {
            const columns = res[0];
            const domainattrs = res[1];

            domainattrs.getRange().forEach(function (attr) {
                const field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                field.name = field.attributeconf.name = 'relattr_' + field.name;
                // get column definition
                const relationcol = CMDBuildUI.util.helper.GridHelper.getColumn(field);

                if (relationcol && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                    // get column editor
                    relationcol.editor = CMDBuildUI.util.helper.GridHelper.getEditorForField(field);
                    // override hidden and sortable configs
                    relationcol.hidden = false;
                    relationcol.sortable = false;
                    relationcol.mandatory = field.mandatory;

                    if (field.mandatory) {
                        relationcol.text = relationcol.text + ' *';
                    }

                    columns.push(relationcol);

                    me._relattrs.push({
                        name: field.name,
                        oname: attr.get('name'),
                        mandatory: field.mandatory
                    });
                }
            });
            // add table
            view.add({
                xtype: 'relations-list-add-grid',
                columns: columns,
                reference: 'relationslistgrid',
                relationAttributes: me._relattrs,
                selModel: {
                    type: 'cmdbuildcheckboxmodel',
                    pruneRemoved: false,
                    checkOnly: false,
                    allowDeselect: true,
                    mode: mode
                }
            });
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const me = this;
        const view = me.getView();
        const vm = view.lookupViewModel();
        const otypename = view.getOriginTypeName();
        const oid = view.getOriginId();
        const selection = view.lookupReference('relationslistgrid').getSelection();
        const cancelButton = view.down('#cancelbutton');
        var attrsvalid = true;

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelButton]);

        // create relations
        const _is_direct = vm.get('relationDirection') === 'direct';
        const store = Ext.create(Ext.data.Store, {
            model: 'CMDBuildUI.model.domains.Relation',
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format('/classes/{0}/cards/{1}/relations', otypename, oid)
            }
        });

        selection.forEach(function (item) {
            const relation = {
                _type: view.getViewModel().get('theDomain').get('name'),
                _destinationId: item.get('_id'),
                _destinationType: item.get('_type'),
                _sourceType: otypename,
                _sourceId: oid,
                _is_direct: _is_direct
            };
            me._relattrs.forEach(function (relattr) {
                const v = item.get(relattr.name);
                if (relattr.mandatory && Ext.isEmpty(v)) {
                    attrsvalid = false;
                }
                relation[relattr.oname] = v;
            });
            store.add(relation);
        });
        if (attrsvalid) {
            store.sync({
                success: function (batch, options) {
                    Ext.callback(view.onSaveSuccess);
                    view.fireEvent('popupclose');
                },
                callback: function (batch, options) {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.enableFormButtons([button, cancelButton]);
                }
            });
        } else {
            CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.relations.missingattributes);
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            CMDBuildUI.util.Utilities.enableFormButtons([button, cancelButton]);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, e, eOpts) {
        const view = this.getView();
        view.fireEvent('popupclose');
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.add.Container} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        view.down('filters-launcher').getMenu().destroy();
    },

    /**
     * @param {String|Numeric} value
     * @param {Object} cell
     * @param {Ext.data.Model} record
     * @param {Numeric} rowIndex
     * @param {Numeric} colIndex
     * @param {Ext.data.Store} store
     * @param {Ext.grid.column.Check} check
     * @param {Ext.view.Table}
     */
    onSelectionCheckRendered: function (value, cell, record, rowIndex, colIndex, store, check, tableview) {
        const vm = tableview.lookupViewModel();
        const domainName = vm.get('theDomain').get('name');

        if (!record.get('_' + domainName + '_available')) {
            // disable cell
            cell.tdCls = CMDBuildUI.view.relations.list.add.Grid.disabledcls;
        }
    },

    privates: {
        /**
         * @property {String[]} _relattrs
         */
        _relattrs: []
    }
});
