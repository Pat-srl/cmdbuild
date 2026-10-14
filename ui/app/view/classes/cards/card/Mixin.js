Ext.define('CMDBuildUI.view.classes.cards.card.Mixin', {
    mixinId: 'card-mixin',

    config: {
        /**
         * @cfg {String} [objectType]
         *
         * The object type.
         */
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,

        /**
         * @cfg {String} [objectTypeName]
         *
         * Class name
         */
        objectTypeName: null,

        /**
         * @cfg {Numeric} objectTypeId
         */
        objectId: null,

        /**
         * @cfg {Boolean} redirectAfterSave
         *
         * `true` to open the created card after save action.
         * Defaults to true.
         */
        redirectAfterSave: true,

        /**
         * @cfg {Boolean} fireGlobalEventsAfterSave
         *
         * `true` to fire global events after save action.
         * Defaults to true.
         */
        fireGlobalEventsAfterSave: true,

        /**
         * @cfg {Boolean} hideWidgets
         * Set to `true` to hide card widgets.
         */
        hideWidgets: false,

        /**
         * @cfg {Boolean} hideInlineElements
         * Set to `true` to hide inline elements (notes, attachments, domains).
         */
        hideInlineElements: true,

        /**
         * @cfg {Function} onAfterSave
         * A function to call when th form is saved.
         */
        onAfterSave: null,

        /**
         * This function is called each time hideInlineElements changes.
         * Manipulates the value before changing it
         * @param {String || Object} value
         */
        applyHideInlineElements: function (value) {
            if (Ext.isBoolean(value)) {
                return {
                    inlineNotes: value,
                    inlineDomains: value,
                    inlineAttachments: value
                };
            } else if (Ext.isObject(value)) {
                return Ext.applyIf(value, {
                    inlineNotes: true,
                    inlineDomains: true,
                    inlineAttachments: true
                });
            }
        },

        /**
         * @cfg {String[]} overrideReadOnlyFields
         *
         * An array of read-only attributes
         */
        overrideReadOnlyFields: []
    },

    publish: ['objectType', 'objectTypeName', 'objectId'],

    twoWayBindable: ['objectType', 'objectTypeName', 'objectId'],

    bind: {
        objectType: '{objectType}',
        objectTypeName: '{objectTypeName}',
        objectId: '{objectId}'
    },

    formmode: null,

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addConditionalVisibilityRules: Ext.emptyFn,

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addAutoValueRules: Ext.emptyFn,

    /**
     * Return form fields
     *
     * @return {Ext.Component[]}
     */
    getDynFormFields: function () {
        var vm = this.getViewModel();
        var defaultValues,
            overrides = {};

        // get default values
        if (this.getDefaultValues) {
            defaultValues = this.getDefaultValues();
        }

        // get object overrides
        var obj = vm.get('theObject');
        if (obj) {
            overrides = obj.getOverridesFromPermissions();
        }
        var visibleAttributes;
        if (!Ext.Object.isEmpty(overrides)) {
            visibleAttributes = Object.keys(overrides);
        }

        // Override read-only property
        if (this.getOverrideReadOnlyFields()) {
            this.getOverrideReadOnlyFields().forEach(function (attr) {
                if (!overrides[attr]) {
                    overrides[attr] = {};
                }
                overrides[attr].writable = false;
            });
        }

        // get klass
        var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            vm.get('objectTypeName'),
            vm.get('objectType')
        );
        var grouping = klass.attributeGroups().getRange();
        var layout;
        if (klass.get('formStructure') && klass.get('formStructure').active) {
            layout = klass.get('formStructure').form;
        }

        // return dynamic fields
        return CMDBuildUI.util.helper.FormHelper.renderForm(vm.get('objectModel'), {
            mode: this.formmode,
            defaultValues: defaultValues,
            attributesOverrides: overrides,
            visibleAttributes: visibleAttributes,
            showAsFieldsets: true,
            grouping: grouping,
            layout: layout,
            formValidation: klass.get('validationRule'),
            formAutoValue: klass.get('autoValue')
        });
    },

    privates: {
        /**
         *
         * @param {Object[]} items
         * @return {Object}
         */
        getMainPanelForm: function (items, hideTools) {
            var me = this,
                classObject = CMDBuildUI.util.helper.ModelHelper.getClassFromName(
                    me.lookupViewModel().get('objectTypeName')
                ),
                hideInlineElements = this.getHideInlineElements(),
                vm = this.getViewModel();

            if (vm.get('widgets')) {
                var widgets = vm.get('widgets').query('_inline', true);

                // add inline widgets
                CMDBuildUI.view.widgets.Launchers.addInlineWidgets(vm.get('theObject'), widgets, me, items);
            }

            // add inline notes
            if (
                !hideInlineElements.inlineNotes &&
                !classObject.isSimpleClass() &&
                classObject.get('noteInline') &&
                classObject.get(CMDBuildUI.model.users.Grant.permissions.note_read)
            ) {
                items.push(me.getInlineNotesConfig(classObject));
            }

            // add inline attachments
            var configAttachments = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.dms.enabled
            );
            if (
                !hideInlineElements.inlineAttachments &&
                !classObject.isSimpleClass() &&
                classObject.get('attachmentsInline') &&
                classObject.get(CMDBuildUI.model.users.Grant.permissions.attachment_read) &&
                configAttachments &&
                this.formmode !== CMDBuildUI.util.helper.FormHelper.formmodes.create
            ) {
                items.push(me.getInlineAttachmentsConfig(classObject));
            }

            if (!hideTools && !Ext.isEmpty(this.tabpaneltools)) {
                Ext.Array.insert(items, 0, [
                    {
                        xtype: 'toolbar',
                        cls: 'fieldset-toolbar',
                        items: Ext.Array.merge([{ xtype: 'tbfill' }], this.tabpaneltools)
                    }
                ]);
            }

            // create panel
            var panelitems = [
                {
                    flex: 1,
                    scrollable: 'y',
                    items: [{ items: items }],
                    listeners: [
                        {
                            added: function (panel, container, position, eOpts) {
                                if (!hideInlineElements.inlineDomains && !classObject.isSimpleClass()) {
                                    if (me.formmode !== CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                                        me.addInlineDomains(panel, classObject);
                                    }
                                }
                            }
                        }
                    ]
                }
            ];

            if (!this.getHideWidgets()) {
                panelitems.push({
                    xtype: 'widgets-launchers',
                    formMode: this.formmode,
                    bind: {
                        widgets: '{widgets}'
                    }
                });
            }
            return {
                flex: 1,
                layout: {
                    type: 'hbox',
                    align: 'stretch' //stretch vertically to parent
                },
                height: '100%',
                items: panelitems
            };
        },

        /**
         *
         * @param {Object} classObject
         */
        getInlineNotesConfig: function (classObject) {
            var field;
            var bindvalue = '{theObject.Notes}';
            var containerbind = {};
            var containerhidden = false;
            if (
                this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read ||
                !classObject.get(CMDBuildUI.model.users.Grant.permissions.note_write)
            ) {
                field = {
                    xtype: 'displayfield',
                    bind: {
                        value: bindvalue
                    }
                };
                containerbind = {
                    hidden: '{!theObject.Notes}'
                };
                containerhidden = true;
            } else {
                field = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({ bind: { value: bindvalue } });
            }
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: classObject.get('noteInlineClosed'),
                collapsible: true,
                hidden: containerhidden,
                items: [field],
                bind: containerbind
            };
        },

        /**
         *
         * @param {Object} classObject
         */
        getInlineAttachmentsConfig: function (classObject) {
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.attachments,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: classObject.get('attachmentsInlineClosed'),
                collapsible: true,
                items: [
                    {
                        xtype: 'dms-container',
                        readOnly:
                            this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read ||
                            !classObject.get(CMDBuildUI.model.users.Grant.permissions.attachment_write)
                                ? true
                                : false
                    }
                ]
            };
        },

        addInlineDomains: function (panel, classObject) {
            var me = this,
                vm = this.lookupViewModel();
            classObject.getDomains().then(function (domains) {
                var inlinedomains = [];
                domains.getRange().forEach(function (domain) {
                    // add direct domains
                    if (
                        Ext.Array.contains(domain.get('sources'), classObject.get('name')) &&
                        domain.get('sourceInline') &&
                        (domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.manytomany ||
                            domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.onetomany)
                    ) {
                        inlinedomains.push({ domain: domain, closed: domain.get('sourceDefaultClosed'), direct: true });
                    }

                    // add inverse domains
                    if (
                        Ext.Array.contains(domain.get('destinations'), classObject.get('name')) &&
                        domain.get('destinationInline') &&
                        (domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.manytomany ||
                            domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.manytoone)
                    ) {
                        inlinedomains.push({
                            domain: domain,
                            closed: domain.get('destinationDefaultClosed'),
                            inverse: true
                        });
                    }
                });

                // get object config
                var config = {
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectId: vm.get('objectId')
                };

                // add domains
                inlinedomains.forEach(function (domain) {
                    panel.add(
                        CMDBuildUI.view.relations.fieldset.Fieldset.getFieldsetConfig(
                            domain.domain,
                            config,
                            {
                                collapsed: domain.closed,
                                formmode: me.formmode,
                                readOnly: !classObject.get(CMDBuildUI.model.users.Grant.permissions.relation_write)
                            },
                            false,
                            {
                                direct: domain.direct,
                                inverse: domain.inverse
                            }
                        )
                    );
                });
            });
        },

        /**
         * Initialize before action form triggers.
         *
         * @param {String} action
         * @param {Object} base_api
         */
        initBeforeActionFormTriggers: function (action, base_api) {
            var me = this;
            var vm = this.getViewModel();
            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                vm.get('objectTypeName'),
                vm.get('objectType')
            );
            if (item) {
                // get form triggers
                var triggers = item.getFormTriggersForAction(action);
                if (triggers && triggers.length) {
                    // bind object creation
                    vm.bind(
                        {
                            bindTo: {
                                theObject: '{theObject}'
                            }
                        },
                        function (data) {
                            var api = Ext.apply({ record: data.theObject }, base_api);
                            me.executeFormTriggers(triggers, api);
                        }
                    );
                }
            }
        },

        /**
         * Execute after action form triggers.
         *
         * @param {String} action
         * @param {CMDBuildUI.model.classes.Card} record
         * @param {Object} base_api
         */
        executeAfterActionFormTriggers: function (action, record, base_api) {
            var me = this;
            var vm = this.getViewModel();
            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                vm.get('objectTypeName'),
                vm.get('objectType')
            );
            if (item) {
                // get form triggers
                var triggers = item.getFormTriggersForAction(action);
                if (triggers && triggers.length) {
                    var api = Ext.apply({ record: record }, base_api);
                    me.executeFormTriggers(triggers, api);
                }
            }
        },

        /**
         * @return {Ext.tab.Panel}
         */
        getParentTabPanel: function () {
            return this.up('classes-cards-tabpanel');
        },

        /**
         * Save the object
         * @param {Object/Function} callback
         * @param {Function} callback.success
         * @param {Function} callback.failure
         * @param {Function} callback.callback
         * @return {Ext.promise.Promise}
         */
        saveObject: function (callback) {
            var deferred = new Ext.Deferred();

            // get widgets
            var vm = this.lookupViewModel(),
                theObject = vm.get('theObject'),
                widgets = vm.get('widgets').getRange();
            CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(theObject, widgets, {
                formmode: this.formmode
            }).then(
                function (success) {
                    if (success == false) {
                        deferred.reject();
                    } else {
                        // save card
                        theObject.save({
                            success: function (record, operation) {
                                // execute widgets after save action
                                CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(theObject, widgets, {
                                    formmode: this.formmode
                                }).then(
                                    function (actionsSuccess) {
                                        deferred.resolve(record);
                                    },
                                    function () {
                                        CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
                                    }
                                );
                            },
                            failure: function () {
                                deferred.reject();
                            }
                        });
                    }
                },
                function () {
                    CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
                }
            );
            return deferred.promise;
        },

        /**
         * @return {Ext.promise.Promise}
         */
        cancelChanges: function () {
            var deferred = new Ext.Deferred();

            var me = this,
                vm = this.lookupViewModel();

            // add load mask
            this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(me);

            // execute widget actions
            CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetCancel(
                vm.get('theObject'),
                vm.get('widgets').getRange(),
                { formmode: me.formmode }
            ).then(function (success) {
                // save process
                deferred.resolve(success);
                CMDBuildUI.util.Utilities.removeLoadMask(me.loadMask);
            });

            return deferred.promise;
        }
    }
});
