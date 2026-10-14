Ext.define('CMDBuildUI.view.processes.instances.instance.Mixin', {
    mixinId: 'processinstance-mixin',

    showForm: Ext.emptyFn,

    config: {
        buttons: [
            {
                itemId: 'cancelBtn',
                ui: 'secondary-action-small',
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                autoEl: {
                    'data-testid': 'processinstance-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                }
            },
            {
                itemId: 'saveAndCloseBtn',
                text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
                ui: 'management-primary-outline-small',
                hidden: true,
                bind: {
                    hidden: '{hideSaveButton}'
                },
                autoEl: {
                    'data-testid': 'processinstance-saveandclose'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
                }
            },
            {
                itemId: 'saveBtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                ui: 'management-primary-outline-small',
                hidden: true,
                bind: {
                    hidden: '{hideSaveButton}'
                },
                autoEl: {
                    'data-testid': 'processinstance-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            },
            {
                itemId: 'executeBtn',
                text: CMDBuildUI.locales.Locales.common.actions.execute,
                ui: 'management-primary-small',
                formBind: true, //only enabled once the form is valid
                disabled: true,
                autoEl: {
                    'data-testid': 'processinstance-execute'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.execute'
                }
            }
        ]
    },

    /**
     * Load activity data and display form
     */
    loadActivity: function () {
        const vm = this.getViewModel();
        const me = this;

        // get model and set proxy url
        const activityModel = Ext.ClassManager.get('CMDBuildUI.model.processes.Activity');
        activityModel
            .getProxy()
            .setUrl(
                CMDBuildUI.util.api.Processes.getInstanceActivitiesUrl(vm.get('objectTypeName'), vm.get('objectId'))
            );

        function loadActivity() {
            activityModel.load(vm.get('activityId'), {
                success: function (record, operation) {
                    if (me && me.getViewModel()) {
                        vm.set('theActivity', record);
                        // get the process definition
                        const theProcess = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(
                            vm.get('objectTypeName')
                        );
                        vm.set('theProcess', theProcess);
                        vm.set(
                            'help.text',
                            !Ext.isEmpty(record.get('_instructions_translation'))
                                ? record.get('_instructions_translation')
                                : theProcess.get('_help_translation') || theProcess.get('help')
                        );
                        // render form
                        me.showForm();
                    }
                },
                scope: me
            });
        }

        if (vm.get('activityId')) {
            loadActivity();
        } else {
            vm.bind('{theObject}', function (theObject) {
                const tasks = theObject.get('_tasklist');
                if (tasks && tasks.length === 1) {
                    vm.set('activityId', tasks[0]._id);
                    loadActivity();
                } else {
                    const url = CMDBuildUI.util.Navigation.getProcessBaseUrl(theObject.get('_type'), theObject.getId());
                    CMDBuildUI.util.Utilities.redirectTo(url, true);
                }
            });
        }
    },

    /**
     *
     * @return {Object}
     */
    getProcessStatusBar: function () {
        // default config
        let config = {
            type: 'FlowStatus',
            field: 'status',
            ecqlFilter: null
        };
        const vm = this.getViewModel();
        const model = vm.get('objectModel');

        // get flow status attribute configuration
        const flowStatusAttr = vm.get('theProcess').get('flowStatusAttr');
        if (flowStatusAttr) {
            const attr = model.getField(flowStatusAttr);
            if (attr) {
                config = {
                    type: attr.attributeconf.lookupType,
                    field: flowStatusAttr,
                    ecqlFilter: CMDBuildUI.util.ecql.Resolver.resolve(attr.attributeconf.ecqlFilter)
                };
            }
        }
        return {
            xtype: 'statuses-progress-bar',
            lookuptype: config.type,
            ecqlFilter: config.ecqlFilter,
            bind: {
                lookupvalue: '{theObject.' + config.field + '}'
            }
        };
    },

    /**
     * Get attributes configuration from activity
     * @return {Object} containing visibleAttributes and overrides
     */
    getAttributesConfigFromActivity: function () {
        const vm = this.getViewModel();
        const overrides = {};
        const visibleAttributes = [];
        const messageAttr = this.getMessageAttribute();

        // iterate on activity attributes
        Ext.Array.each(vm.get('theActivity').attributes().getRange(), function (attr, index) {
            if (attr.getId() !== messageAttr && !attr.get('action')) {
                // TODO: check permissions
                visibleAttributes.push(attr.getId());
                overrides[attr.getId()] = attr.getData();
            }
        });
        return {
            visibleAttributes: visibleAttributes,
            overrides: overrides
        };
    },

    /**
     * Get action field
     *
     * @return {Ext.form.field.ComboBox}
     */
    getActionField: function () {
        const vm = this.getViewModel();
        const model = vm.get('objectModel');

        // iterate on activity attributes
        const action = Ext.Array.findBy(vm.get('theActivity').attributes().getRange(), function (attr, index) {
            return attr.get('action');
        });

        let actionfielddef = {
            xtype: 'displayfield',
            labelAlign: 'left',
            cls: Ext.baseCSSPrefix + 'process-action-field',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            fieldLabel: CMDBuildUI.locales.Locales.processes.action.label,
            value: CMDBuildUI.locales.Locales.processes.action.advance,
            columnWidth: 0.5,
            flex: '0.5',
            layout: 'anchor'
        };

        if (action) {
            const fieldname = action.getId();
            vm.set('activity_action.fieldname', fieldname);

            // get action attribute lookup definition
            const attr = model.getField(fieldname);
            if (
                attr.cmdbuildtype.toLowerCase() ===
                CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()
            ) {
                // override attribute
                const attrclone = Ext.merge({}, attr, {
                    mandatory: true,
                    description: CMDBuildUI.locales.Locales.processes.action.label,
                    metadata: {
                        description_localized: CMDBuildUI.locales.Locales.processes.action.label
                    }
                });
                const formfield = CMDBuildUI.util.helper.FormHelper.getFormField(attrclone, {
                    mode: this.formmode
                });
                actionfielddef = Ext.apply(formfield, {
                    cls: Ext.baseCSSPrefix + 'process-action-field',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    fieldsDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                    labelAlign: 'left',
                    columnWidth: 0.5,
                    flex: '0.5',
                    layout: 'anchor'
                });
            }
        }
        return {
            layout: 'column',
            items: [
                actionfielddef,
                {
                    columnWidth: 0.5,
                    flex: '0.5',
                    margin: '10px 0 0',
                    align: 'right',
                    layout: 'hbox',
                    items: this.getCurrentActivityInfo(true)
                }
            ]
        };
    },

    /**
     *
     * @param {Object[]} items
     * @return {Object}
     */
    getMainPanelForm: function (items) {
        const me = this;
        const vm = this.getViewModel();
        const widgets = vm.get('theActivity.widgets').query('_inline', true);
        let messageBox;

        if (items[0].itemId == 'messageBox') {
            messageBox = items[0];
            Ext.Array.removeAt(items, 0);
        }

        // add inline widgets
        CMDBuildUI.view.widgets.Launchers.addInlineWidgets(vm.get('theObject'), widgets, me, items);

        return {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch' //stretch vertically to parent
            },
            height: '100%',
            padding: '0 10 0 0',
            items: [
                {
                    flex: 1,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        messageBox,
                        {
                            flex: 1,
                            scrollable: 'y',
                            items: [
                                {
                                    items: items
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'widgets-launchers',
                    formMode: this.formmode,
                    bind: {
                        widgets: '{theActivity.widgets}'
                    }
                }
            ]
        };
    },

    /**
     * Get message box
     *
     * @return {Object} Message box configuration
     */
    getMessageBox: function () {
        const vm = this.getViewModel();
        const model = vm.get('objectModel');
        let conf = {};
        let messageAttr = this.getMessageAttribute();
        if (messageAttr) {
            const hasmessage = vm.get('theActivity').attributes().find('_id', messageAttr) !== -1;
            if (hasmessage) {
                const field = model.getField(messageAttr);
                if (field) {
                    if (
                        field.cmdbuildtype.toLowerCase() ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()
                    ) {
                        messageAttr = Ext.String.format('_{0}_description_translation', messageAttr);
                    } else if (
                        field.cmdbuildtype.toLowerCase() ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()
                    ) {
                        messageAttr = Ext.String.format('_{0}_description', messageAttr);
                    }
                    conf = {
                        xtype: 'container',
                        ui: 'messageinfo',
                        itemId: 'messageBox',
                        messageAttr: field.name,
                        hidden: true,
                        bind: {
                            html: Ext.String.format('{theObject.{0}}', messageAttr),
                            hidden: Ext.String.format('{!theObject.{0}}', field.name)
                        }
                    };
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Attribute ' + messageAttr + ' not found in process.',
                        CMDBuildUI.util.Logger.levels.warn
                    );
                }
            }
        }
        return conf;
    },

    /**
     * @param {Boolean} hideTools
     * @return The string to use as activity info tooltip.
     */
    getCurrentActivityInfo: function (hideTools) {
        const vm = this.lookupViewModel();
        const html = Ext.String.format(
            '<small class="x-selectable">{0} | {1}</small>',
            vm.get('theActivity').get('_description_translation'),
            vm.get('theActivity').get('_performer_description_translation') ||
                vm.get('theActivity').get('_performer_description') ||
                vm.get('theActivity').get('performer')
        );

        let tools = [
            {
                xtype: 'tbfill'
            },
            {
                xtype: 'tbtext',
                itemId: 'infotool',
                cls: Ext.baseCSSPrefix + 'process-infotool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('info-circle', 'solid'),
                html: html,
                autoEl: {
                    'data-testid': 'processes-instance-infotool'
                }
            }
        ];

        if (!hideTools && !Ext.isEmpty(this.tabpaneltools)) {
            tools = Ext.Array.merge(tools, this.tabpaneltools);
        } else if (
            this.up('processes-instances-tabpanel') &&
            this.up('processes-instances-tabpanel').isInDetailWindow &&
            this.up('processes-instances-tabpanel').isInDetailWindow()
        ) {
            tools = Ext.Array.merge(tools, CMDBuildUI.view.processes.instances.Util.getHelpTool());
        }
        return tools;
    },

    /**
     * @param {Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     */
    executeProcess: function (callback) {
        const me = this;
        const theObject = this.getViewModel().get('theObject');
        const newInstance = theObject.phantom;
        const action = 'execute';

        this.saveProcess(
            {
                success: function (record, operation) {
                    // set advance attribute
                    record.set('_advance', true);
                    const tasks = record.get('_tasklist');
                    if (newInstance && !Ext.isEmpty(tasks)) {
                        record.set('_activity', tasks[0]._id);
                    }

                    CMDBuildUI.util.Ajax.setActionId('proc.inst.execute');
                    me.saveObject(record, null, action, callback);
                },
                failure: function () {
                    CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
                }
            },
            action
        );
    },

    /**
     * @param {Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     * @param {String} action
     */
    saveProcess: function (callback, action) {
        const me = this;
        const vm = this.getViewModel();
        action = action ? action : 'save';

        const theObject = vm.get('theObject');
        // set advance attribute
        theObject.set('_advance', false);
        theObject.set('_activity', vm.get('activityId'));
        // set type if empty
        if (!theObject.get('_type')) {
            theObject.set('_type', vm.get('objectTypeName'));
        }

        CMDBuildUI.util.Ajax.setActionId('proc.inst.save');
        me.loadMask = CMDBuildUI.util.Utilities.addLoadMask(me);

        // execute widgets before save actions
        const widgets = vm.get('theActivity').widgets().getRange();
        CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(theObject, widgets, {
            formmode: me.formmode,
            action: action
        }).then(
            function (success) {
                // save and advance
                if (success) {
                    me.saveObject(theObject, widgets, action, callback);
                } else {
                    CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
                }
            },
            function () {
                CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
            }
        );
    },

    /**
     * @param {Object} callback
     */
    cancelChanges: function (callback) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const me = this;
        const vm = this.lookupViewModel();
        // add load mask
        me.loadMask = CMDBuildUI.util.Utilities.addLoadMask(me);

        // execute widget actions
        CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetCancel(
            vm.get('theObject'),
            vm.get('theActivity').widgets().getRange(),
            {
                formmode: me.formmode
            }
        ).then(function (success) {
            // do callback if success if not false
            if (success !== false) {
                Ext.callback(callback);
            }
            CMDBuildUI.util.Utilities.removeLoadMask(me.loadMask);
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        });
    },

    /**
     * Close detail window
     */
    closeDetailWindow: function () {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
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
     * Return the Id of open running lookup state
     */
    getOpenRunningStatusId: function () {
        const type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
            CMDBuildUI.model.processes.Process.flowstatus.lookuptype
        );
        if (type) {
            return type.values().findRecord('code', 'open.running').getId();
        }
        return;
    },

    /**
     * Initialize before action form triggers.
     *
     * @param {String} action
     * @param {Object} base_api
     */
    initBeforeActionFormTriggers: function (action, base_api) {
        const me = this;
        const vm = this.getViewModel();
        const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            vm.get('objectTypeName'),
            vm.get('objectType')
        );
        if (item) {
            // get form triggers
            const triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                // bind object creation
                vm.bind(
                    {
                        bindTo: {
                            theObject: '{theObject}',
                            theActivity: '{theActivity}'
                        }
                    },
                    function (data) {
                        const api = Ext.apply(
                            {
                                record: data.theObject,
                                activity: data.theActivity
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
    executeAfterActionFormTriggers: function (action, record, activity, base_api) {
        const me = this;
        const vm = this.getViewModel();
        const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            vm.get('objectTypeName'),
            vm.get('objectType')
        );
        if (item) {
            // get form triggers
            const triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                if (
                    (action === CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsert ||
                        action === CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsertExecute ||
                        action === CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterEditExecute) &&
                    !Ext.isEmpty(record.get('_tasklist'))
                ) {
                    record.set('_activity', record.get('_tasklist')[0]._id);
                }
                record.set('_advance', false);
                const api = Ext.apply(
                    {
                        record: record,
                        activity: activity
                    },
                    base_api
                );
                me.executeFormTriggers(triggers, api);
            }
        }
    },

    privates: {
        /**
         * Return message attribute name
         */
        getMessageAttribute: function () {
            return this.getViewModel().get('theProcess').get('messageAttr');
        },

        /**
         * @return {Ext.tab.Panel}
         */
        getParentTabPanel: function () {
            return this.up('processes-instances-tabpanel');
        },

        /**
         *
         * @param {CMDBuildUI.model.processes.Instance} theObject
         * @param {CMDBuildUI.model.WidgetDefinition[]} widgets
         * @param {String} action
         * @param {Function} callback
         */
        saveObject: function (theObject, widgets, action, callback) {
            const me = this;
            delete theObject.data._tasklist;

            theObject.save({
                callback: function (record, operation, success) {
                    const executeCallback = function () {
                        // execute success function
                        if (success && Ext.isFunction(callback.success)) {
                            Ext.callback(callback.success, record, [record, operation]);
                        }
                        // execute failure function
                        if (!success && Ext.isFunction(callback.failure)) {
                            Ext.callback(callback.failure, record, [record, operation]);
                        }
                        // execute callback function
                        if (Ext.isFunction(callback.callback)) {
                            Ext.callback(callback.callback, record, [record, operation, success]);
                        }
                    };

                    if (!widgets) {
                        executeCallback();
                    } else {
                        CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(theObject, widgets, {
                            formmode: me.formmode,
                            action: action
                        }).then(
                            function () {
                                executeCallback();
                            },
                            function () {
                                CMDBuildUI.util.Utilities.manageCallbacks(callback, theObject);
                            }
                        );
                    }
                }
            });
        }
    }
});
