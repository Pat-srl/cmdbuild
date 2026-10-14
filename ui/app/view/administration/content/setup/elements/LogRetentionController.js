Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetentionController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-logretention',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnCLick'
        },
        '#resetBtn': {
            click: 'onResetBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.setup.elements.LogRetention} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        me.loadData().then(function () {
            me.createFieldsets();
        });
    },

    onEditBtnCLick: function () {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.set('disabledTabs.log', true);
        vm.set('disabledTabs.retention', false);
        vm.set('disabledTabs.audit', true);
        vm.set('disabledTabs.governancedata', true);
    },

    onResetBtnClick: function () {
        const me = this;
        const vm = me.getViewModel();
        const defaultRules = this.getDefaults();
        const customRules = this.getRules();
        // merge default rules and custom rules
        const merged = Ext.Array.reduce(
            [...defaultRules, ...customRules],
            function (acc, obj) {
                acc[obj.name] = { ...acc[obj.name], ...obj };
                return acc;
            },
            {}
        );
        Ext.Object.each(merged, function (ruleName, rule) {
            Ext.Object.each(rule.keys, function (key) {
                if (rule.defaultParam === key) {
                    // apply default value
                    vm.set(
                        Ext.String.format(
                            'theSetup.{0}__DOT__{1}__DOT__{2}',
                            me._baseParam,
                            rule.name,
                            rule.defaultParam
                        ),
                        rule.defaultValue
                    );
                } else {
                    // delete value
                    vm.set(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, rule.name, key), null);
                }
            });
            vm.set(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, rule.name, 'filter'), null);
            vm.set(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, rule.name, 'match'), 'always');
        });
    },

    onSaveBtnClick: function (button) {
        const me = this;
        const vm = button.lookupViewModel();
        // Show saving mask
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        const mode = vm.get('mode').selection ? vm.get('mode').selection.get('value') : vm.get('mode');

        if (mode !== 'default') {
            // to add rules
            const _rules = [];
            // rules values from saved configuration
            const rulesSettings = me.getRules();

            Ext.Array.forEach(rulesSettings, function (ruleSetting) {
                _rules.push(ruleSetting.name);
            });

            vm.set(
                'theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules',
                Ext.Array.merge([], _rules).join(',')
            );
        }

        var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function (transport) {
            me.onCancelBtnClick();
        });
        setData.always(function () {
            if (!button.destroyed) {
                button.enable();
            }
            if (Ext.getBody().isMasked()) {
                Ext.getBody().unmask();
            }
        });
    },

    onCancelBtnClick: function () {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        vm.set('disabledTabs.log', false);
        vm.set('disabledTabs.retention', false);
        vm.set('disabledTabs.audit', false);
        vm.set('disabledTabs.governancedata', false);
        this.loadData();
    },

    onAceEditorExpandClick: function (event) {
        this.onAceExpand(Ext.getCmp(event.target.closest("[id^='aceeditortextarea'].x-field").id).inputField);
    },

    privates: {
        _baseParam: 'org__DOT__cmdbuild__DOT__database__DOT__cleanup_rule',

        /**
         *
         */
        createFieldsets: function () {
            const me = this;
            const view = this.getView();
            const vm = view.lookupViewModel();
            const fieldsets = me.getDefaults(vm);

            fieldsets.forEach(function (fieldset) {
                // read saved values
                var maxAge = vm.get(
                        Ext.String.format(
                            'theSetup.{0}__DOT__{1}__DOT__{2}',
                            me._baseParam,
                            fieldset.name,
                            'maxRecordAgeToKeepSeconds'
                        )
                    ),
                    maxRecords = vm.get(
                        Ext.String.format(
                            'theSetup.{0}__DOT__{1}__DOT__{2}',
                            me._baseParam,
                            fieldset.name,
                            'maxRecordsToKeep'
                        )
                    ),
                    maxSize = vm.get(
                        Ext.String.format(
                            'theSetup.{0}__DOT__{1}__DOT__{2}',
                            me._baseParam,
                            fieldset.name,
                            'maxSizeMegs'
                        )
                    );

                // set default if empty
                if (Ext.isEmpty(maxAge) && Ext.isEmpty(maxRecords) && Ext.isEmpty(maxSize)) {
                    vm.set(
                        Ext.String.format(
                            'theSetup.{0}__DOT__{1}__DOT__{2}',
                            me._baseParam,
                            fieldset.name,
                            fieldset.defaultParam
                        ),
                        fieldset.defaultValue
                    );
                    vm.set(
                        Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'match'),
                        'always'
                    );
                }

                var validator = function (val) {
                    var errMsg = CMDBuildUI.locales.Locales.errors.fieldrequired;
                    var maxAge = vm.get(
                            Ext.String.format(
                                'theSetup.{0}__DOT__{1}__DOT__{2}',
                                me._baseParam,
                                fieldset.name,
                                'maxRecordAgeToKeepSeconds'
                            )
                        ),
                        maxRecords = vm.get(
                            Ext.String.format(
                                'theSetup.{0}__DOT__{1}__DOT__{2}',
                                me._baseParam,
                                fieldset.name,
                                'maxRecordsToKeep'
                            )
                        ),
                        maxSize = vm.get(
                            Ext.String.format(
                                'theSetup.{0}__DOT__{1}__DOT__{2}',
                                me._baseParam,
                                fieldset.name,
                                'maxSizeMegs'
                            )
                        );
                    return Ext.isEmpty(maxAge) && Ext.isEmpty(maxRecords) && Ext.isEmpty(maxSize) ? errMsg : true;
                };

                view.add({
                    xtype: 'fieldset',
                    ui: 'administration-formpagination',
                    columnWidth: 1,
                    title: fieldset.description,
                    items: [
                        {
                            layout: 'column',
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('match', {
                                    match: {
                                        fieldcontainer: {
                                            columnWidth: 0.33,
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.match
                                        },
                                        combofield: {
                                            forceSelection: true,
                                            viewModel: {
                                                stores: {
                                                    comboStore: {
                                                        proxy: 'memory',
                                                        autoDestroy: true,
                                                        data: [
                                                            {
                                                                value: 'always',
                                                                label: CMDBuildUI.locales.Locales.administration
                                                                    .systemconfig.always
                                                            },
                                                            {
                                                                value: 'filter',
                                                                label: CMDBuildUI.locales.Locales.administration
                                                                    .systemconfig.filter
                                                            }
                                                        ]
                                                    }
                                                }
                                            },
                                            bind: {
                                                store: '{comboStore}',
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'match'
                                                )
                                            }
                                        },
                                        columnWidth: 1,
                                        displayfield: {
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'match'
                                                )
                                            },
                                            renderer: function (value) {
                                                if (value) {
                                                    var combo = this.up('fieldcontainer').down('combo'),
                                                        store = combo.getViewModel().get('comboStore');
                                                    return store.findRecord('value', value).get('label');
                                                }
                                                return value;
                                            }
                                        }
                                    }
                                }),
                                {
                                    xtype: 'fieldcontainer',
                                    columnWidth: 0.66,
                                    hidden: true,
                                    bind: {
                                        hidden: Ext.String.format(
                                            '{theSetup.{0}__DOT__{1}__DOT__{2} != "filter"}',
                                            me._baseParam,
                                            fieldset.name,
                                            'match'
                                        )
                                    },
                                    listeners: {
                                        show: function () {
                                            var editor = this.down('aceeditortextarea');
                                            editor.setAllowBlank(false);
                                            this.up('form').isValid();
                                        },
                                        hide: function () {
                                            var editor = this.down('aceeditortextarea');
                                            editor.setAllowBlank(true);
                                            this.up('form').isValid();
                                        }
                                    },
                                    items: [
                                        {
                                            columnWidth: 0.66,
                                            xtype: 'aceeditortextarea',
                                            allowBlank: true,
                                            vmObjectName: 'theSetup',
                                            inputField: Ext.String.format(
                                                '{0}__DOT__{1}__DOT__{2}',
                                                me._baseParam,
                                                fieldset.name,
                                                'filter'
                                            ),
                                            itemId: Ext.String.format(
                                                '{0}__DOT__{1}__DOT__{2}',
                                                me._baseParam,
                                                fieldset.name,
                                                'filter'
                                            ),
                                            options: {
                                                mode: 'ace/mode/json',
                                                readOnly: true
                                            },
                                            bind: {
                                                readOnly: '{actions.view}',
                                                config: {
                                                    options: {
                                                        readOnly: '{actions.view}'
                                                    }
                                                }
                                            },
                                            validator: validator,
                                            fieldLabel:
                                                CMDBuildUI.locales.Locales.administration.systemconfig.filter + ' *',
                                            minHeight: '85px',
                                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'expand',
                                                'solid'
                                            ),
                                            labelToolIconQtip:
                                                CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                                            labelToolIconClick: 'onAceEditorExpandClick',
                                            /**
                                             * @override
                                             */
                                            onChange: function () {
                                                var vm = this.lookupViewModel();
                                                this.lastValue = this.value;
                                                this.value = this.getAceEditor().getSession().getValue();
                                                vm.set(
                                                    Ext.String.format(
                                                        'theSetup.{0}',
                                                        Ext.String.format(
                                                            '{0}__DOT__{1}__DOT__{2}',
                                                            me._baseParam,
                                                            fieldset.name,
                                                            'filter'
                                                        )
                                                    ),
                                                    this.value
                                                );
                                                this.isCodeValid();
                                                this.fireEvent('change', this, this.value, this.lastValue);
                                                this.getErrors();
                                                this.up('form').isValid();
                                            },
                                            listeners: {
                                                render: function (element) {
                                                    var aceEditor = element.getAceEditor();
                                                    var vm = element.lookupViewModel();
                                                    vm.bind(
                                                        {
                                                            bindTo: {
                                                                theSetup: '{theSetup}'
                                                            },
                                                            single: true
                                                        },
                                                        function (data) {
                                                            if (data.theSetup) {
                                                                aceEditor.setValue(
                                                                    data.theSetup[
                                                                        Ext.String.format(
                                                                            '{0}__DOT__{1}__DOT__{2}',
                                                                            me._baseParam,
                                                                            fieldset.name,
                                                                            'filter'
                                                                        )
                                                                    ] || '',
                                                                    -1
                                                                );
                                                            }
                                                        }
                                                    );
                                                    vm.bind(
                                                        {
                                                            isView: '{actions.view}'
                                                        },
                                                        function (data) {
                                                            aceEditor.setReadOnly(data.isView);
                                                        }
                                                    );
                                                }
                                            },
                                            name: Ext.String.format(
                                                '{0}__DOT__{1}__DOT__{2}',
                                                me._baseParam,
                                                fieldset.name,
                                                'filter'
                                            )
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxRecordAgeToKeepSeconds',
                                    {
                                        maxRecordAgeToKeepSeconds: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxage
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxRecordAgeToKeepSeconds'
                                                )
                                            },
                                            validator: validator
                                        }
                                    }
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxRecordsToKeep',
                                    {
                                        maxRecordsToKeep: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxrecords
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxRecordsToKeep'
                                                )
                                            },
                                            validator: validator
                                        }
                                    }
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxSizeMegs',
                                    {
                                        maxSizeMegs: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxsize
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxSizeMegs'
                                                )
                                            },
                                            validator: validator
                                        }
                                    }
                                )
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            columnWidth: 1,
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format(
                                    '{theSetup.{0}__DOT__{1}__DOT__{2} != "filter"}',
                                    me._baseParam,
                                    fieldset.name,
                                    'match'
                                )
                            },
                            listeners: {
                                hide: function (view) {
                                    var vm = view.lookupViewModel();
                                    vm.set(
                                        Ext.String.format(
                                            'theSetup.{0}__DOT__{1}_else__DOT__{2}',
                                            me._baseParam,
                                            fieldset.name,
                                            'maxRecordAgeToKeepSeconds'
                                        ),
                                        null
                                    );
                                    vm.set(
                                        Ext.String.format(
                                            'theSetup.{0}__DOT__{1}_else__DOT__{2}',
                                            me._baseParam,
                                            fieldset.name,
                                            'maxRecordsToKeep'
                                        ),
                                        null
                                    );
                                    vm.set(
                                        Ext.String.format(
                                            'theSetup.{0}__DOT__{1}_else__DOT__{2}',
                                            me._baseParam,
                                            fieldset.name,
                                            'maxSizeMegs'
                                        ),
                                        null
                                    );
                                }
                            },
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxRecordAgeToKeepSeconds',
                                    {
                                        maxRecordAgeToKeepSeconds: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel: Ext.String.format(
                                                    '{0} - {1}',
                                                    CMDBuildUI.locales.Locales.administration.systemconfig
                                                        .unmatchingrows,
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxage
                                                )
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}_else__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxRecordAgeToKeepSeconds'
                                                )
                                            }
                                        }
                                    }
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxRecordsToKeep',
                                    {
                                        maxRecordsToKeep: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel: Ext.String.format(
                                                    '{0} - {1}',
                                                    CMDBuildUI.locales.Locales.administration.systemconfig
                                                        .unmatchingrows,
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxrecords
                                                )
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}_else__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxRecordsToKeep'
                                                )
                                            }
                                        }
                                    }
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                                    'maxSizeMegs',
                                    {
                                        maxSizeMegs: {
                                            fieldcontainer: {
                                                columnWidth: 0.33,
                                                fieldLabel: Ext.String.format(
                                                    '{0} - {1}',
                                                    CMDBuildUI.locales.Locales.administration.systemconfig
                                                        .unmatchingrows,
                                                    CMDBuildUI.locales.Locales.administration.systemconfig.maxsize
                                                )
                                            },
                                            columnWidth: 1,
                                            step: 0,
                                            minValue: 0,
                                            hideTrigger: true,
                                            bind: {
                                                value: Ext.String.format(
                                                    '{theSetup.{0}__DOT__{1}_else__DOT__{2}}',
                                                    me._baseParam,
                                                    fieldset.name,
                                                    'maxSizeMegs'
                                                )
                                            }
                                        }
                                    }
                                )
                            ]
                        }
                    ]
                });
            });
        },

        /**
         * Load data from server, format keys and set vm data for binding
         * @private
         */
        loadData: function () {
            var me = this,
                deferred = new Ext.Deferred(),
                vm = me.getView().getViewModel();
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (configs) {
                if (!vm.destroyed) {
                    configs.forEach(function (key) {
                        if (key._key === 'org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules') {
                            vm.set('defaultMode', key['default']);
                        }
                        vm.set(Ext.String.format('theSetup.{0}', key._key), key.hasValue ? key.value : key['default']);
                    });
                }
                deferred.resolve(true);
            });
            return deferred.promise;
        },

        /**
         *
         * @private
         */
        getDefaults: function () {
            var vm = this.getViewModel(),
                defaultString = vm.get('defaultMode'),
                regex = /(\w+)_default\[(\w+)=(\d+)\]/g,
                result = [],
                match;

            while ((match = regex.exec(defaultString)) !== null) {
                result.push({
                    name: match[1],
                    description: CMDBuildUI.locales.Locales.administration.systemconfig[match[1]] || match[1],
                    defaultParam: match[2],
                    defaultValue: match[3]
                });
            }

            return result;
        },

        /**
         * @private
         * @returns {Object}
         */
        getRules: function () {
            const me = this;
            const vm = this.getViewModel();
            const regex = /(\w+)_default\[(\w+)=(\d+)\]/g;
            const defaultString = vm.get('defaultMode');
            const keyOptions = ['maxRecordAgeToKeepSeconds', 'maxRecordsToKeep', 'maxSizeMegs'];
            const logRules = [];
            let match;

            while ((match = regex.exec(defaultString)) !== null) {
                const rulesConfig = Ext.Array.reduce(
                    keyOptions,
                    function (configObject, currentKey) {
                        const keyValue = vm.get(
                            Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, match[1], currentKey)
                        );
                        if (keyValue) {
                            configObject[currentKey] = keyValue;
                        }
                        return configObject;
                    },
                    {}
                );
                logRules.push({
                    name: match[1],
                    description: CMDBuildUI.locales.Locales.administration.systemconfig[match[1]] || match[1],
                    keys: rulesConfig
                });
            }
            return logRules;
        },

        /**
         * @private
         */
        onAceExpand: function (property) {
            var panelTitle = CMDBuildUI.locales.Locales.administration.systemconfig.filter;
            var vm = this.getViewModel();
            var theSetup = vm.get('theSetup');
            var value = theSetup[property];
            var elementId = Ext.String.format('filter_{0}', property);
            var popupId = Ext.String.format('popup-setup-logs-{0}', elementId);

            var editor;

            var popUp,
                content = {
                    xtype: 'panel',
                    layout: {
                        type: 'hbox'
                    },
                    ui: 'administration-formpagination',
                    fieldDefaults: {
                        labelAlign: 'top'
                    },
                    viewModel: {},
                    dockedItems: [
                        {
                            hidden: vm.get('actions.view'),
                            xtype: 'toolbar',
                            dock: 'bottom',
                            ui: 'footer',
                            items: [
                                {
                                    xtype: 'component',
                                    flex: 1
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                                    ui: 'administration-secondary-action-small',
                                    listeners: {
                                        click: function () {
                                            vm.set(Ext.String.format('theSetup.{0}', property), value);
                                            popUp.fireEvent('close');
                                        }
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                                    ui: 'administration-action-small',
                                    listeners: {
                                        click: function (_button) {
                                            vm.set(Ext.String.format('theSetup.{0}', property), editor.getValue());
                                            var mainInput =
                                                CMDBuildUI.util.Navigation.getMainAdministrationContainer().down(
                                                    Ext.String.format('#{0}', property)
                                                );
                                            if (mainInput) {
                                                mainInput.getAceEditor().setValue(editor.getValue(), -1);
                                            }
                                            popUp.fireEvent('close');
                                        }
                                    }
                                }
                            ]
                        }
                    ],

                    items: [
                        {
                            flex: 1,
                            layout: 'card',
                            height: '100%',
                            viewModel: {},
                            items: [
                                {
                                    xtype: 'component',
                                    layout: 'card',
                                    height: '100%',
                                    viewModel: {},
                                    html: Ext.String.format('<div id="{0}"></div>', elementId),
                                    listeners: {
                                        afterrender: function (cmp) {
                                            editor = ace.edit(elementId, {
                                                //set autoscroll
                                                autoScrollEditorIntoView: true,
                                                maxLines: 90, // this will be changed on "heightUpdateFunction"
                                                // set theme
                                                theme: 'ace/theme/chrome',
                                                // show line numbers
                                                showLineNumbers: true,
                                                // hide print margin
                                                showPrintMargin: false,
                                                wrap: true
                                            });
                                            if (vm.get('actions.view')) {
                                                editor.setReadOnly(true);
                                            }

                                            editor.getSession().setMode('ace/mode/json');
                                            editor.getSession().setUseWrapMode(true);
                                            editor.setValue(value || '', -1);

                                            var heightUpdateFunction = function (editor) {
                                                var editorDiv = document.getElementById(elementId); // its container
                                                var lineHeight = editor.renderer.lineHeight;
                                                editorDiv.style.height = '100%'; // set new container height
                                                editorDiv.style.width = '100%'; // force container width to 100%
                                                editor.setOption(
                                                    'maxLines',
                                                    Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight)
                                                );
                                                editor.setOption(
                                                    'minLines',
                                                    Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight)
                                                );
                                            };
                                            cmp.mon(cmp, 'resize', function () {
                                                editor.renderer.$updateSizeAsync();
                                            });

                                            // Set initial size to match initial content
                                            Ext.asap(
                                                function (editor) {
                                                    heightUpdateFunction(editor);
                                                },
                                                this,
                                                [editor]
                                            );

                                            // Whenever a change happens inside the ACE editor, update
                                            // the size again
                                            onUpdateFunction = function (event, doc) {
                                                // update stored value
                                                vm.set(Ext.String.format('theSetup.{0}', property), doc.getValue());
                                            };

                                            editor.getSession().on('change', onUpdateFunction);
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                };

            popUp = CMDBuildUI.util.Utilities.openPopup(
                popupId,
                panelTitle,
                content,
                {
                    closed: function () {
                        CMDBuildUI.util.Utilities.closePopup(popupId);
                    },
                    /**
                     * @param {Ext.panel.Panel} panel
                     * @param {Object} eOpts
                     */
                    close: function (panel, eOpts) {
                        CMDBuildUI.util.Utilities.closePopup(popupId);
                    }
                },
                {
                    ui: 'administration',
                    width: '80%',
                    height: '80%',
                    viewModel: {},
                    draggable: true
                }
            );
        }
    }
});
