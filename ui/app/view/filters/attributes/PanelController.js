Ext.define('CMDBuildUI.view.filters.attributes.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#attributecombo': {
            select: 'onAttributeComboSelect',
            blur: 'onAttributeBlur'
        },
        '#removebutton': {
            click: 'onRemoveButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view._fieldsetsreferences = [];
        const me = this;
        const vm = me.getViewModel();
        const fields = {};
        const store = vm.get('attributeslist');

        if (view.getAllowArbitraryAttributeName()) {
            view.down('#attributecombo').setEmptyText(CMDBuildUI.locales.Locales.filters.typeorchoosetheattributename);
        }

        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get('objectType'), vm.get('objectTypeName')).then(
            function (model) {
                if (view && !view.getOnlyOneLevel()) {
                    CMDBuildUI.util.helper.FiltersHelper.addOperators(store);
                }

                // multitenant field
                me._addTenantFieldIfNeeded(model, store, fields);

                // activity field (if object type is process)
                me._addActivityFieldIfNeeded(model, store, fields);

                // notes field
                me._addNotesFieldIfNeeded(model, store, fields);

                // get model fields
                me._addModelFields(model, store, fields);

                vm.set('allfields', fields);

                const config = vm.get('theFilter').get('configuration');
                if ((config && config.attributesCustom) || config.attribute) {
                    CMDBuildUI.util.helper.FiltersHelper.populateAttributeContainer(
                        view,
                        config.attributesCustom || config.attribute
                    );
                }

                if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                    const disable = !!vm.get('actions.view');
                    vm.set('displayOnly', disable);
                }
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveButtonClick: function (button, e, eOpts) {
        // destroy row
        const parent = button.up('filters-attributes-row') || button.up('filters-attributes-block');
        parent.destroy();
    },

    /**
     * @param {Ext.form.field.ComboBox} combo
     * @param {CMDBuildUI.model.base.ComboItem} record
     * @param {Object} eOpts
     */
    onAttributeComboSelect: function (combo, record, eOpts) {
        if (record) {
            const block = combo.getParentBlock().down('#blockitems');
            if (record.get('operator')) {
                block.add(this.getBlockConfig(record.get('value'), block.up().getLevel() + 1));
            } else {
                block.add(
                    this.getAttributeRowConfig({
                        attribute: record.get('value')
                    })
                );
            }
            combo.setValue();
        }
    },

    /**
     *
     * @param {Ext.Component} combo
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAttributeBlur: function (combo, event, eOpts) {
        const view = this.getView();
        if (
            view.allowArbitraryAttributeName &&
            view.getAllowArbitraryAttributeName() &&
            !combo.getSelectedRecord() &&
            combo.getRawValue()
        ) {
            const block = combo.getParentBlock().down('#blockitems');
            block.add(
                this.getAttributeRowConfig({
                    attribute: combo.getRawValue()
                })
            );
        }
        combo.setValue(null);
    },

    privates: {
        /**
         * Returns filter block configuration
         * @param {String} operator
         * @param {Integer} level
         * @returns {Object}
         */
        getBlockConfig: function (operator, level) {
            return {
                xtype: 'filters-attributes-block',
                operator: operator,
                level: level
            };
        },

        /**
         * Returns filter attribute configuration
         * @param {Object} filter
         * @param {String} filterRowPanel.attribute
         * @param {String} filterRowPanel.operator
         * @param {Boolean} filterRowPanel.typeinput
         * @param {*} filterRowPanel.value1
         * @param {*} filterRowPanel.value2
         * @returns {Object}
         */
        getAttributeRowConfig: function (filter) {
            const view = this.getView();
            const allFields = view.lookupViewModel().get('allfields');
            var attribute = allFields && allFields[filter.attribute];

            if (attribute) {
                const attrConf = attribute.attributeconf;
                filter.attribute_description =
                    attrConf.description_localized ||
                    attribute._localized_description ||
                    attribute._attributeDescription ||
                    attribute.description ||
                    attrConf.description;
                if (attribute._attributeClassAlias) {
                    filter.attribute_description = Ext.String.format(
                        '{0} - {1}',
                        attribute._attributeClassAlias,
                        attribute._attributeDescription
                    );
                }
            } else {
                filter.attribute_description = filter.attribute;
                filter.cmdbuildtype = 'ignore';
                attribute = {};
                attribute.cmdbuildtype = 'ignore';
            }

            const filterRowPanel = {
                xtype: 'filters-attributes-row',
                allowInputParameter: view.getAllowInputParameter(),
                allowArbitraryAttributeName: view.getAllowArbitraryAttributeName(),
                viewModel: {
                    data: {
                        values: Ext.apply({}, filter)
                    }
                }
            };

            if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                Ext.apply(filterRowPanel, {
                    allowCurrentUser: view.getAllowCurrentUser(),
                    allowCurrentGroup: view.getAllowCurrentGroup()
                });
            }

            return filterRowPanel;
        },

        /**
         *
         * @param {Ext.data.Model} model
         * @param {Ext.data.Store} store
         * @param {Object} fields
         */
        _addTenantFieldIfNeeded: function (model, store, fields) {
            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
                const objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    model.objectTypeName,
                    model.objectType
                );
                const multitenantMode = objectdefinition ? objectdefinition.get('multitenantMode') : null;
                if (
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
                ) {
                    const valueName = this.getView().lookupViewModel().get('rowFilterPermission')
                        ? 'IdTenant'
                        : '_tenant';
                    store.add({
                        value: valueName,
                        label: CMDBuildUI.util.Utilities.getTenantLabel(),
                        group: ''
                    });
                    fields[valueName] = {
                        attributeconf: {
                            description_localized: CMDBuildUI.util.Utilities.getTenantLabel()
                        },
                        cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                    };
                }
            }
        },

        /**
         *
         * @param {Ext.data.Model} model
         * @param {Ext.data.Store} store
         * @param {Object} fields
         */
        _addNotesFieldIfNeeded: function (model, store, fields) {
            const objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                model.objectTypeName,
                model.objectType
            );

            if (objectdefinition && objectdefinition.get('noteInline')) {
                store.add({
                    value: CMDBuildUI.util.helper.ModelHelper.extraFields.notes,
                    label: CMDBuildUI.locales.Locales.administration.common.labels.note,
                    group: CMDBuildUI.locales.Locales.common.attributes.nogroup
                });
                fields[CMDBuildUI.util.helper.ModelHelper.extraFields.notes] = {
                    attributeconf: {
                        description_localized: CMDBuildUI.locales.Locales.administration.common.labels.note
                    },
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                };
            }
        },

        /**
         *
         * @param {Ext.data.Model} model
         * @param {Ext.data.Store} store
         * @param {Object} fields
         */
        _addActivityFieldIfNeeded: function (model, store, fields) {
            if (model.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                store.add({
                    value: '_activity_definition',
                    label: CMDBuildUI.locales.Locales.common.tabs.activity,
                    group: ''
                });
                fields['_activity_definition'] = {
                    attributeconf: {
                        description_localized: CMDBuildUI.locales.Locales.common.tabs.activity
                    },
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity
                };
            }
        },

        /**
         *
         * @param {Ext.data.Model} model
         * @param {Ext.data.Store} store
         * @param {Object} fields
         */
        _addModelFields: function (model, store, fields) {
            const view = this.getView();
            model.getFields &&
                model.getFields().forEach(function (field) {
                    const typesToExclude = [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ];
                    const attrconf = field.attributeconf;

                    if (
                        attrconf &&
                        !Ext.String.startsWith(field.name, '_') &&
                        !field.hidden &&
                        !attrconf.password &&
                        !attrconf.hideInFilter
                    ) {
                        if (view.getUseTextFieldForValue() && typesToExclude.indexOf(field.cmdbuildtype) == -1) {
                            field.attributeconf.type = field.cmdbuildtype = 'string';
                        }
                        fields[field.name] = field;
                        store.addSorted(
                            Ext.create('CMDBuildUI.model.base.ComboItem', {
                                value: field.name,
                                label:
                                    attrconf.description_localized || field._localized_description || field.description,
                                group:
                                    attrconf._group_description_translation ||
                                    attrconf._group_description ||
                                    CMDBuildUI.locales.Locales.common.attributes.nogroup
                            })
                        );
                    }
                });
        }
    }
});
