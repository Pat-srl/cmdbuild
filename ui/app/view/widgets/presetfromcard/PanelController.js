Ext.define('CMDBuildUI.view.widgets.presetfromcard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-presetfromcard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#applybtn': {
            click: 'onApplyBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.presetfromcard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var widget = vm.get('theWidget');

        // this type of widget does not support inline mode
        if (widget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }
        var typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget);
        var objectTypeName = typeinfo.objectTypeName,
            objectType = typeinfo.objectType;

        if (objectType) {
            vm.set('objectType', objectType);
            vm.set('objectTypeName', objectTypeName);
        } else {
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showErrorMessage(
                    Ext.String.format(CMDBuildUI.locales.Locales.errors.classnotfound, objectTypeName)
                );
            });
            return;
        }

        CMDBuildUI.util.helper.GridHelper.getColumnsForType(objectType, objectTypeName, {
            allowFilter: false
        }).then(function (columns) {
            var modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(objectType, objectTypeName);
            var model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(modelname);

            vm.set('model', model);

            // add grid
            view.add({
                xtype: 'grid',
                columns: columns,
                forceFit: true,
                loadMask: true,
                itemId: 'grid',
                reference: 'grid',
                selModel: {
                    selType: 'checkboxmodel',
                    showHeaderCheckbox: false,
                    checkOnly: true,
                    mode: 'SINGLE',
                    allowDeselect: true,
                    excludeToggleOnColumn: 1
                },
                bind: {
                    store: '{gridrows}',
                    selection: '{selection}'
                },
                plugins: [
                    {
                        pluginId: 'forminrowwidget',
                        ptype: 'forminrowwidget',
                        id: 'forminrowwidget',
                        expandOnDblClick: true,
                        removeWidgetOnCollapse: true,
                        widget: {
                            xtype: 'classes-cards-card-view',
                            viewModel: {}, // do not remove otherwise the viewmodel will not be initialized
                            tabpaneltools: []
                        }
                    }
                ]
            });
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onApplyBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var vm = button.lookupViewModel();
        var selection = vm.get('selection');
        var attrsmapping = vm.get('theWidget').get('AttributeMapping');
        var target = vm.get('theTarget');
        attrsmapping.split(',').forEach(function (attr) {
            var lattr = attr.split('=');
            if (lattr.length === 2) {
                var selattr = lattr[1] === 'Id' ? '_id' : lattr[1];
                target.set(lattr[0], selection.get(selattr));
            }
        });
        CMDBuildUI.util.helper.FormHelper.endSavingForm();
        this.getView().fireEvent('popupclose');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent('popupclose');
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = field.getValue();
        if (searchTerm) {
            var store = vm.get('gridrows');
            if (store) {
                // add filter
                store.getAdvancedFilter().addQueryFilter(searchTerm);
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('gridrows');
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    }
});
