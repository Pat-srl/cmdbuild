Ext.define('CMDBuildUI.view.relations.list.edit.GridContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-edit-gridcontainer',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        'grid': {
            selectionchange: 'onGridSelectionChange'
        },
        'tableview': {
            selectioncheckrendered: 'onSelectionCheckRendered'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.edit.GridContainer} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel();
        var mode = view.getMultiSelect() ? 'MULTI' : 'SINGLE';
        view.lookupReference('gridcontainer').add({
            xtype: 'relations-list-edit-grid',
            selModel: {
                type: 'cmdbuildcheckboxmodel',
                pruneRemoved: false,
                checkOnly: false,
                allowDeselect: true,
                mode: mode
            },
            viewModel: {
                data: {
                    originId: view.getOriginId(),
                    storeinfo: {
                        type: null,
                        proxyurl: null,
                        autoload: false,
                        ecqlfilter: null
                    }
                }
            }
        });
        vm.bind(
            {
                bindTo: {
                    domain: '{theDomain}'
                }
            },
            function (data) {
                data.domain.getAttributes().then(function (attributes) {
                    if (attributes.getTotalCount()) {
                        me.addAttributesPanel(attributes, view.lookupReference('attributesform'));
                    } else {
                        vm.set('attributesvalid', true);
                    }
                });
            }
        );

        vm.bind(
            {
                theRelation: '{theRelation}'
            },
            function (data) {
                if (data.theRelation) {
                    for (var k in data.theRelation.getData()) {
                        if (!Ext.String.startsWith(k, '_')) {
                            vm.set('values.' + k, Ext.clone(data.theRelation.get(k)));
                        }
                    }
                }
            }
        );
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.edit.GridContainer} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        view.down('filters-launcher').getMenu().destroy();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            view = me.getView(),
            selection = view.down('grid').getSelection(),
            vm = button.lookupViewModel(),
            cancelButton = view.down('#cancelbutton'),
            relation = vm.get('theRelation'),
            oldDestinationId = relation.get('_destinationId'),
            oldDestinationType = relation.get('_destinationType');

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelButton]);

        var sel = selection[0];
        // edit current relation
        relation.set('_destinationId', sel.get('_id'));
        relation.set('_destinationType', sel.get('_type'));

        if (vm.get('values')) {
            var values = vm.get('values');
            for (var k in values) {
                relation.set(k, values[k]);
            }
        }

        var relationProxyUrl = relation.getProxy().getUrl();
        if (!relationProxyUrl) {
            var objectType = vm.get('objectType') || relation.get('_sourceType');
            var objectTypeName = vm.get('objectTypeName') || relation.get('_sourceType');
            var objectId = vm.get('objectId') || relation.get('_sourceId');

            if (objectTypeName && objectId) {
                relationProxyUrl =
                    objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                        ? CMDBuildUI.util.api.Processes.getProcessInstanceRelations(objectTypeName, objectId)
                        : CMDBuildUI.util.api.Classes.getCardRelations(objectTypeName, objectId);
                relation.getProxy().setUrl(relationProxyUrl);
            }
        }

        relation.save({
            success: function () {
                Ext.callback(view.onSaveSuccess);
                view.fireEvent('popupclose');
            },
            failure: function () {
                relation.set('_destinationId', oldDestinationId);
                relation.set('_destinationType', oldDestinationType);
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelButton]);
            },
            callback: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
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
        var vm = tableview.lookupViewModel();
        var domainName = vm.get('theDomain').get('name');

        if (!record.get('_' + domainName + '_available')) {
            // disable cell
            cell.tdCls = CMDBuildUI.view.relations.list.add.Grid.disabledcls;
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        // revert changes relation
        var relation = vm.get('theRelation');
        relation.reject();
        // close popup
        this.getView().fireEvent('popupclose');
    },
    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onGridSelectionChange: function (selection, selected, eOpts) {
        this.getViewModel().set('relselection', selected);
    },

    privates: {
        /**
         *
         * @param {Ext.data.Store} attributes
         * @param {Ext.form.Panel} form
         */
        addAttributesPanel: function (attributes, form) {
            var fields = [],
                requiredattributes = [];
            // generate form fields from attributes list
            attributes.getRange().forEach(function (attr) {
                var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                // replace field name with attribute name
                if (field && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                    field.name = attr.get('name');
                    var config = {
                        mode: field.mode,
                        linkName: 'theRelation'
                    };
                    var item = CMDBuildUI.util.helper.FormHelper.getFormField(field, config);
                    item.bind.value = '{values.' + field.name + '}';

                    if (
                        item.metadata.type.toLowerCase() ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase()
                    ) {
                        item.parseDate = function (value) {
                            if (!value || Ext.isDate(value)) {
                                return value;
                            }

                            var me = this,
                                val = me.safeParse(value, me.format),
                                altFormats = me.altFormats,
                                altFormatsArray = me.altFormatsArray,
                                i = 0,
                                len;

                            if (!val && altFormats) {
                                altFormatsArray = altFormatsArray || altFormats.split('|');
                                len = altFormatsArray.length;
                                for (; i < len && !val; ++i) {
                                    val = me.safeParse(value, altFormatsArray[i]);
                                }
                            }
                            if (!val && value) {
                                val = new Date(value);
                            }
                            return val;
                        };
                    }
                    if (
                        item.metadata.type.toLowerCase() ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase()
                    ) {
                        item.altFormats = 'c';
                    }

                    fields.push(item);

                    // add controls for mandatory fields
                    if (field.mandatory && !field.hidden) {
                        requiredattributes.push(attr.get('name'));
                    }
                }
            });

            var left = [],
                right = [],
                counter = 0;
            tabindex = 0;
            fields.forEach(function (f, fi) {
                if (!f.hidden) {
                    f.tabIndex = tabindex++;
                    if (counter % 2) {
                        right.push(f);
                    } else {
                        left.push(f);
                    }
                    counter++;
                }
            });
            form.add([
                {
                    items: left
                },
                {
                    items: right
                }
            ]);
            form.setHidden(false);

            form.lookupViewModel().bind(
                {
                    bindTo: '{values}',
                    deep: true
                },
                function (values) {
                    if (Object.keys(values)) {
                        var isValid = form.isValid();
                        requiredattributes.forEach(function (a) {
                            if (Ext.isEmpty(values[a])) {
                                isValid = false;
                            }
                        });
                        this.set('attributesvalid', isValid);
                    } else {
                        this.set('attributesvalid', false);
                    }
                }
            );
        }
    }
});
