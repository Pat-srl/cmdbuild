Ext.define('CMDBuildUI.view.bulkactions.edit.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bulkactions-edit-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#attributescombo': {
            select: 'onAttributesComboSelect'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.bulkactions.edit.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        me._modifiedattributes = {};
        me._fieldsets = {};

        const vm = view.lookupViewModel();
        const objectitem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            view.getObjectTypeName(),
            view.getObjectType()
        );

        vm.linkTo('theObject', {
            type: CMDBuildUI.util.helper.ModelHelper.getModelName(view.getObjectType(), view.getObjectTypeName()),
            create: true
        });

        var noteInline = objectitem.get('noteInline');
        const multitenantMode = objectitem ? objectitem.get('multitenantMode') : null;
        const shouldShowTenant =
            (multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed) &&
            CMDBuildUI.util.helper.SessionHelper.getActiveTenants().length > 1;

        // update attributes store
        const attributeslist = [];
        objectitem.getAttributes().then(function (attributes) {
            attributes.getRange().forEach(function (attr) {
                if (attr.get('name') === CMDBuildUI.util.helper.ModelHelper.extraFields.notes && !noteInline) {
                    return;
                }
                if (attr.get('type') !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                    const _attr = attr.copy();
                    if (!_attr.get('_group_description_translation')) {
                        _attr.set(
                            '_group_description_translation',
                            CMDBuildUI.locales.Locales.common.attributes.nogroup
                        );
                    }
                    attributeslist.push(_attr);
                }
                if (attr.get('name') === CMDBuildUI.util.helper.ModelHelper.extraFields.idTenant) {
                    const _attr = attr.copy();
                    if (shouldShowTenant) {
                        _attr.set('writable', true);
                        _attr.set('hidden', false);
                        _attr.set('mode', 'write');
                        _attr.set('_description_translation', CMDBuildUI.util.Utilities.getTenantLabel());
                        _attr.set(
                            '_group_description_translation',
                            CMDBuildUI.locales.Locales.common.attributes.nogroup
                        );
                        attributeslist.push(_attr);
                    }
                }
            });
            vm.set('attributeslist', attributeslist);
        });

        const form = view.lookupReference('bulkeditform');

        // add fieldsets for each group
        objectitem
            .attributeGroups()
            .getRange()
            .forEach(function (group) {
                me._fieldsets[group.get('name')] = form.add({
                    xtype: 'formpaginationfieldset',
                    title: group.get('_description_translation'),
                    collapsible: true,
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    hidden: true
                });
            });

        // add nogroups fieldset
        me._fieldsets[CMDBuildUI.model.AttributeGrouping.nogroup] = form.add({
            xtype: 'formpaginationfieldset',
            title: CMDBuildUI.locales.Locales.common.attributes.nogroup,
            collapsible: true,
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            hidden: true
        });
    },

    /**
     *
     * @param {CMDBuildUI.view.bulkactions.edit.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();
        // callback for add missing fields
        const onAddBindFields = function () {
            const attributesStore = vm.get('attributes');
            const attributescombo = view.down('#attributescombo');
            // find all missing fields
            const missingFields = Ext.Array.difference(
                Ext.Object.getValues(vm.get('keysBindFields')).flat(1),
                Ext.Object.getAllKeys(me._modifiedattributes)
            );
            vm.set('updateErrorMessage', false);
            // add each missing field using the attribute selection combo
            Ext.Array.forEach(missingFields, function (item, index, allitems) {
                if (index === missingFields.length - 1) {
                    vm.set('updateErrorMessage', true);
                }
                attributescombo.setSelection(attributesStore.getById(item));
            });
        };
        // add callback to event listener
        document.getElementById('formValidatorFieldAction').addEventListener('click', onAddBindFields);
    },

    /**
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {CMDBuildUI.model.Attribute} record
     * @param {Object} eOpts
     */
    onAttributesComboSelect: function (combo, record, eOpts) {
        if (record) {
            const me = this;
            const view = this.getView();
            const vm = this.getViewModel();
            let fieldConfig;
            let fieldset;
            const fieldName = record.get('name');

            if (fieldName === CMDBuildUI.util.helper.ModelHelper.extraFields.idTenant) {
                const objectitem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    view.getObjectTypeName(),
                    view.getObjectType()
                );
                const multitenantMode = objectitem ? objectitem.get('multitenantMode') : null;

                fieldConfig = CMDBuildUI.util.helper.FormHelper.getTenantField(
                    CMDBuildUI.util.helper.FormHelper.formmodes.update,
                    multitenantMode,
                    'theObject',
                    record.get('_description_translation')
                );

                fieldset = this._fieldsets[CMDBuildUI.model.AttributeGrouping.nogroup];

                this._modifiedattributes['_tenant'] = record.get('_description_translation');
            } else {
                let model;
                if (fieldName === CMDBuildUI.util.helper.ModelHelper.extraFields.notes) {
                    model = CMDBuildUI.util.helper.ModelHelper.getNotesModel(
                        view.getObjectType(),
                        view.getObjectTypeName()
                    );
                } else {
                    model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(
                        CMDBuildUI.util.helper.ModelHelper.getModelName(view.getObjectType(), view.getObjectTypeName())
                    );
                }
                // get model field
                const field = model.getField(fieldName);

                fieldConfig = CMDBuildUI.util.helper.FormHelper.getFormField(field, {
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
                    ignoreUpdateVisibilityToField: true,
                    ignoreAutovalue: true
                });

                fieldset = field.attributeconf.group
                    ? this._fieldsets[field.attributeconf.group]
                    : this._fieldsets[CMDBuildUI.model.AttributeGrouping.nogroup];

                if (fieldName !== CMDBuildUI.util.helper.ModelHelper.extraFields.notes) {
                    const fieldBinds = CMDBuildUI.util.helper.FormHelper.extractBindFromExpression(
                        field.attributeconf.validationRules,
                        CMDBuildUI.util.helper.FormHelper._default_link_name
                    );

                    const binds = fieldBinds.bindTo;
                    if (Ext.isObject(binds)) {
                        let keysBindFields = vm.get('keysBindFields');
                        keysBindFields[fieldName] = Ext.Object.getAllKeys(binds);
                        vm.set('keysBindFields', keysBindFields);
                    }
                }
                this._modifiedattributes[fieldName] = record.get('_description_translation');
            }

            if (!fieldConfig) {
                return;
            }

            const row = fieldset.add({
                layout: {
                    type: 'hbox',
                    align: 'end'
                },

                items: [
                    Ext.merge(fieldConfig, {
                        flex: 1
                    }),
                    {
                        xtype: 'button',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                        margin: 'auto auto 1 10',
                        ui: 'management-primary',
                        tooltip: CMDBuildUI.locales.Locales.common.actions.remove,
                        handler: function () {
                            // make record writabe again and re-add to combo store
                            record.set('writable', true);
                            combo.getStore().add(record);

                            const keysBindFields = vm.get('keysBindFields');
                            delete keysBindFields[fieldName];
                            delete me._modifiedattributes[fieldName];
                            vm.set('keysBindFields', keysBindFields);
                            me.showMissingFields();

                            if (row.up().items.length === 1) {
                                row.up().setHidden(true);
                            }
                            row.destroy();
                        }
                    }
                ]
            });
            // show fieldset
            fieldset.setHidden(false);
            // remove attribute from combo
            record.set('writable', false);
            // clear combo
            combo.setValue();

            if (vm.get('updateErrorMessage')) {
                this.showMissingFields();
            }
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onSaveBtnClick: function () {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const grid = view.ownerGrid;
        const objectData = vm.get('theObject').getData();

        // get request info
        const requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid);

        function getMetaAttrs(attrname) {
            const re = new RegExp('^_' + attrname + '_([A-Z]\\w+)$');

            return Ext.Array.filter(Ext.Object.getAllKeys(objectData), function (item) {
                return CMDBuildUI.util.api.Client.testRegExp(re, item);
            });
        }

        // get changed data
        const changedData = {},
            changedAttributes = [];
        Ext.Object.each(this._modifiedattributes, function (attrname, attrdesc) {
            changedData[attrname] = objectData[attrname];

            getMetaAttrs(attrname).forEach(function (meta_attr) {
                changedData[meta_attr] = objectData[meta_attr];
            });

            changedAttributes.push(attrdesc);
        });

        if (Ext.Object.isEmpty(changedData)) {
            // show error message
            CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.bulkactions.noattributeselected);
        } else {
            // create confirm message
            const me = this,
                allPromises = [],
                message = Ext.String.format(
                    CMDBuildUI.locales.Locales.bulkactions.confirmedit,
                    '<em>' + changedAttributes.sort().join(', ') + '</em>',
                    requestinfo.count
                );

            CMDBuildUI.util.Msg.confirm(CMDBuildUI.locales.Locales.notifier.attention, message, function (btn) {
                if (btn === 'yes') {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    const loadMask = CMDBuildUI.util.Utilities.addLoadMask(view);

                    if (Ext.isArray(requestinfo.advancedFitler)) {
                        Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                            allPromises.push(me.makePutRequest(requestinfo.url, changedData, item));
                        });
                    } else {
                        allPromises.push(me.makePutRequest(requestinfo.url, changedData, requestinfo.advancedFitler));
                    }

                    Ext.Promise.all(allPromises).then(
                        function (responses, eOpts) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                            // reload store
                            grid.getStore().load();
                            // clear selection
                            grid.getSelectionModel().deselectAll();
                            // close popup
                            view.closePopup();
                        },
                        function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                        }
                    );
                }
            });
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (btn, eOpts) {
        this.getView().closePopup();
    },

    privates: {
        /**
         * @property {Object} _fieldsets
         * Reference to fieldsets added in the form.
         */
        _fieldsets: {},

        /**
         * @property {Object} _modifiedattributes
         * The list of modified attributes
         */
        _modifiedattributes: {},

        /**
         * Make put request
         * @param {String} url
         * @param {Object} changedData
         * @param {String} filter
         * @returns {Ext.promise.Promise}
         */
        makePutRequest: function (url, changedData, filter) {
            const deferred = new Ext.Deferred();

            // make ajax request
            Ext.Ajax.request({
                url: url,
                method: 'PUT',
                jsonData: changedData,
                params: {
                    filter: filter
                },
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        },

        /**
         *
         */
        showMissingFields: function () {
            const vm = this.getViewModel();
            const attributesStore = vm.get('attributes');
            const missingFields = Ext.Array.difference(
                Ext.Object.getValues(vm.get('keysBindFields')).flat(1),
                Ext.Object.getAllKeys(this._modifiedattributes)
            ).map(function (item) {
                return attributesStore.getById(item).get('_description_translation');
            });
            const errorMessage = Ext.isEmpty(missingFields)
                ? ''
                : Ext.String.format(CMDBuildUI.locales.Locales.bulkactions.missingfields, missingFields.join(', '));

            vm.set('errorMessage', errorMessage);
        }
    }
});
