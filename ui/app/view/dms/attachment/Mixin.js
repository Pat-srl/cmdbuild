Ext.define('CMDBuildUI.view.dms.attachment.Mixin', {
    mixinId: 'dms-mixin',

    mixins: ['CMDBuildUI.mixins.forms.FormTriggers'],

    config: {
        /**
         * @cfg {Boolean}
         * Ignore schedules generation for date field with 'schedules rule definition' associated.
         */
        ignoreSchedules: false,

        /**
         * @cfg {Ext.data.Store}
         */
        asyncStore: null,

        /**
         * @cfg {String []} invalidFileNames
         */
        invalidFileNames: [],

        /**
         * @cfg {String} currentFileName
         */
        currentFileName: null
    },

    /**
     * @property {String} formmode
     * Override this property in each form.
     * Used in common functions to know form mode.
     */
    formmode: null,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

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
     * Initialize before action form triggers.
     *
     * @param {String} action
     * @param {Object} base_api
     */
    initBeforeActionFormTriggers: function (action, base_api) {
        const me = this,
            vm = this.getViewModel(),
            model = vm.get('DMSModelClass'),
            item = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(model.objectTypeName);

        if (item) {
            // get form triggers
            const triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                // bind object creation
                vm.bind(
                    {
                        bindTo: {
                            theObject: '{theObject}'
                        }
                    },
                    function (data) {
                        const api = Ext.apply(
                            {
                                record: data.theObject
                            },
                            base_api
                        );
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
        const me = this,
            vm = this.getViewModel(),
            model = vm.get('DMSModelClass'),
            item = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(model.objectTypeName);

        if (item) {
            // get form triggers
            const triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                const api = Ext.apply(
                    {
                        record: record
                    },
                    base_api
                );
                me.executeFormTriggers(triggers, api);
            }
        }
    },

    /**
     *
     * @param {Object} base_api
     * @return {Object}
     */
    getApiForTrigger: function (base_api) {
        const vm = this.getViewModel();
        base_api._attachmentOwner = {
            type: vm.get('objectType'),
            typeName: vm.get('objectTypeName'),
            id: vm.get('objectId')
        };
        return base_api;
    },

    /**
     * @return {Boolean}
     */
    isAsyncSave: function () {
        return !Ext.isEmpty(this.getAsyncStore());
    },

    privates: {
        /**
         *
         * @param {Object[]} items
         */
        getFormItems: function (items, linkName) {
            linkName = linkName ? linkName : 'theObject';
            // add notes fieldset
            items.push(this.getInlineNotesConfig(linkName));

            return {
                flex: 1,
                layout: {
                    type: 'hbox',
                    align: 'stretch' //stretch vertically to parent
                },
                height: '100%',
                items: [
                    {
                        flex: 1,
                        scrollable: 'y',
                        items: [
                            {
                                items: items
                            }
                        ]
                    },
                    {
                        xtype: 'widgets-launchers',
                        formMode: this.formmode,
                        padding: 10,
                        bind: {
                            widgets: '{DMSWidgets}'
                        }
                    }
                ]
            };
        },

        /**
         *
         * @param {String} linkName
         * @returns {Object}
         */
        getInlineNotesConfig: function (linkName) {
            const bindvalue = Ext.String.format('{{0}.Notes}', linkName);
            const collapsed = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(
                this.getViewModel().get('DMSModelClass').objectTypeName
            ).get('noteInlineClosed');

            var field;
            var containerbind = {};
            var viewModel = null;
            var containerhidden = false;

            if (this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                field = {
                    xtype: 'displayfield',
                    bind: {
                        value: bindvalue
                    }
                };
                containerbind = {
                    hidden: '{notesHidden}'
                };
                viewModel = {
                    data: {
                        notesHidden: true
                    },
                    formulas: {
                        notesHidden: {
                            bind: bindvalue,
                            get: function (value) {
                                return Ext.isEmpty(value);
                            }
                        }
                    }
                };
                containerhidden = true;
            } else {
                field = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    bind: {
                        value: bindvalue
                    }
                });
            }
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: collapsed,
                collapsible: true,
                hidden: containerhidden,
                items: [field],
                bind: containerbind,
                viewModel: viewModel ? viewModel : null
            };
        },

        /**
         *
         * @param {String} DMSCategoryTypeName
         * @param {Number} DMSCategoryValue
         * @param {Ext.data.model} DMSModelClass
         * @returns {Number} The size allowed max size.
         */
        getMaxFileSize: function (DMSCategoryTypeName, DMSCategoryValue, DMSModelClass) {
            var maxFileSize = null;
            if (DMSCategoryTypeName && DMSCategoryValue && DMSModelClass) {
                const lk = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(DMSCategoryTypeName),
                    lookupValuesStore = lk.values();

                //we suppose that the lookupValues for the lookupType are already loaded.
                //If not make this function asincronous or load lookupValues before calling this function
                const lookupValue = lookupValuesStore.getById(DMSCategoryValue);

                if (lookupValue) {
                    maxFileSize = lookupValue.get('maxFileSize');

                    if (Ext.isEmpty(maxFileSize)) {
                        const modelName = DMSModelClass.objectTypeName,
                            DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);

                        maxFileSize = DMSClass.get('maxFileSize');
                        if (Ext.isEmpty(maxFileSize)) {
                            if (Ext.isEmpty(maxFileSize)) {
                                maxFileSize = CMDBuildUI.util.helper.Configurations.get(
                                    CMDBuildUI.model.Configuration.dms.maxfilesize
                                );
                            }
                        }
                    }
                }
            }
            CMDBuildUI.util.Logger.log('maxFileSize = ' + Number(maxFileSize), CMDBuildUI.util.Logger.levels.debug);
            return Number(maxFileSize);
        }
    }
});
