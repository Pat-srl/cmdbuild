Ext.define('CMDBuildUI.view.filters.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#applybutton': {
            click: 'onApplyButtonClick'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.filters.Panel} view
     * @param {Event} event
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, event, eOpts) {
        if (!view.getIsDms()) {
            var saveandapplybutton = view.down('#savebutton');
            saveandapplybutton.setHidden(false);
        }

        // add attributes panel
        if (view.getShowAttributesPanel()) {
            view.add({
                xtype: 'filters-attributes-panel',
                reference: 'attributespanel',
                allowInputParameter: view.getAllowInputParameterForAttributes()
            });
        }

        // add relations panel
        if (view.getShowRelationsPanel()) {
            view.add({
                xtype: 'filters-relations-panel',
                reference: 'relationspanel'
            });
        }

        // add attachments panel
        if (
            view.getShowAttachmentsPanel() &&
            CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms // dms is enabled
        ) {
            view.add({
                xtype: 'filters-attachments-panel',
                reference: 'attachmentspanel',
                isDms: view.getIsDms()
            });
        }

        view.setActiveTab(0);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onApplyButtonClick: function (button, eOpts) {
        this.getView().fireEvent('applyfilter', this.getView(), this.getFilter());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            view = this.getView(),
            filter = this.getFilter(),
            applyBtn = view.down("#applybutton"),
            cancelBtn = view.down("#cancelbutton"),
            w = Ext.create('Ext.window.Window', {
                title: filter.get('description'),
                width: 400,
                layout: 'fit',
                alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++,
                modal: true,
                ui: 'management',
                viewModel: {
                    data: {
                        theFilter: filter,
                        filterName: filter.get('name')
                    },
                    formulas: {
                        /**
                         * The filter name must be not null and not in use in another filter
                         */
                        validateName: {
                            bind: '{filterName}',
                            get: function (value) {
                                if (!value) {
                                    return false;
                                }
                                var filterId = this.get('theFilter._id'),
                                    filters = this.get('filters'),
                                    isValid = true;
                                if (filters) {
                                    var sameNameRecord = filters.findRecord('name', value);
                                    isValid = !sameNameRecord;
                                    // if the filter is already added check if the record with same name
                                    // is not the current record
                                    if (this.get('theFilter').crudState != 'C') {
                                        isValid = !(sameNameRecord && sameNameRecord.get('_id') != filterId);
                                    }
                                }
                                return isValid ? true : CMDBuildUI.locales.Locales.filters.errorname;
                            }
                        }
                    }
                },

                items: {
                    xtype: 'form',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                    items: [{
                        xtype: 'textfield',
                        name: 'name',
                        fieldLabel: CMDBuildUI.locales.Locales.filters.name,
                        allowBlank: false,  // requires a non-empty value
                        bind: {
                            value: '{filterName}',
                            validation: '{validateName}'
                        }
                    }],
                    listeners: {
                        destroy: function () {
                            CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, button, cancelBtn]);
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    }
                },

                buttons: [{
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    ui: 'secondary-action-small',
                    handler: function () {
                        CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, button, cancelBtn]);
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities._popupAlwaysOnTop--;
                        w.destroy();
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    ui: 'management-primary-small',
                    formBind: true,
                    bind: {
                        disabled: '{!validateName}'
                    },
                    handler: function (button) {
                        var vm = button.lookupViewModel(),
                            filterName = Ext.String.htmlEncode(vm.get('filterName'));
                        vm.set("filterName", filterName);
                        filter.set('name', filterName);
                        filter.set('_description_translation', filterName);
                        filter.set('description', filterName);
                        me.getView().fireEvent('saveandapplyfilter', me.getView(), filter);
                        CMDBuildUI.util.Utilities._popupAlwaysOnTop--;
                        w.destroy();
                    }
                }]
            });

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([applyBtn, button, cancelBtn]);
        w.show();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, eOpts) {
        this.getView().fireEvent('popupclose');
    },

    privates: {
        /**
         * @return {CMDBuildUI.model.base.Filter}
         */
        getFilter: function () {
            var filter = this.getViewModel().get("theFilter");
            var conf = {};
            var attributespanel = this.lookup("attributespanel");
            var relationspanel = this.lookup("relationspanel");
            var attachmentspanel = this.lookup("attachmentspanel");
            var attrdata = attributespanel && attributespanel.getAttributesData() || null;
            var reldata = relationspanel && relationspanel.getRelationsData() || null;
            var attachmentsdata = attachmentspanel && attachmentspanel.getAttachmentsData() || null;

            if (attrdata && !Ext.isEmpty(attrdata) && !Ext.Object.isEmpty(attrdata)) {
                conf.attributesCustom = attrdata;
            }

            if (reldata && !Ext.isEmpty(reldata) && !Ext.Object.isEmpty(reldata)) {
                conf.relation = reldata;
            }

            if (attachmentsdata && !Ext.isEmpty(attachmentsdata) && !Ext.Object.isEmpty(attachmentsdata)) {
                conf.attachment = attachmentsdata;
            }

            filter.set("configuration", conf);

            return filter;
        }
    }
});
