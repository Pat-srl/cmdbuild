Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper', {
    singleton: true,

    /**
     * Get general properties fieldset
     *
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getGeneralProperties: function (mode) {
        var me = this;
        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            items: [
                me.getContainer([me.getCode(mode), me.getDescription(mode)]),
                me.getContainer([me.getContent(mode)]),
                me.getContainer([me.getOwnerClass(mode), me.getOwnerAttribute(mode)]),
                me.getContainer([me.getUser(mode), me.getGroup(mode)]),
                me.getContainer([me.getTimeZone(mode), me.getSequenceParamsEditMode(mode)]),
                me.getContainer([me.getActive(mode), me.getCreateAlsoViaWS(mode)])
            ]
        };

        return fieldset;
    },

    /**
     * Get general properties fieldset
     *
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getTypeProperties: function (mode) {
        if (!mode) {
            mode = 'both';
        }
        var me = this;
        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.schedules.schedulerule,
            items: [
                me.getContainer([me.getCategory(mode), me.getPriority(mode)]),
                me.getContainer([me.getEditable(mode), me.getCascade(mode)]),
                me.getContainer([me.getCondition(mode)], {
                    columnWidth: 2
                }),

                me.getContainer([me.getFrequency(mode), me.getFrequencyMultiplier(mode)]),

                me.getContainer([
                    me.getEndType(mode),
                    me.getContainer([me.getNumberOfOccurrencies(mode), me.getEndDate(mode)], {
                        columnWidth: 0.5
                    })
                ]),

                me.getContainer([me.getDelayPeriod(mode), me.getDelay(mode)]),
                me.getContainer([me.getMaxActiveEvents(mode)])
            ]
        };
        return fieldset;
    },

    /**
     * @private
     */
    privates: {
        getContainer: function (items, config) {
            var fieldcontainer = Ext.merge(
                {},
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: items
                },
                config || {}
            );

            fieldcontainer.items = items;

            return fieldcontainer;
        },

        // General properties fieldset
        getCode: function (mode) {
            // description
            var propertyName = 'code';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    allowBlank: false
                },
                allowBlank: false,
                bind: {
                    value: '{theSchedule.code}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput(
                config,
                true,
                '[name="description"]',
                mode
            );
        },

        getDescription: function (mode) {
            // description
            var propertyName = 'description';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    allowBlank: false
                },
                allowBlank: false,
                bind: {
                    value: '{theSchedule.description}'
                }
            };
            if (mode !== CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
                config[propertyName].fieldcontainer.labelToolIconCls = CMDBuildUI.util.helper.IconHelper.getIconId(
                    'flag',
                    'solid'
                );
                config[propertyName].fieldcontainer.userCls = 'with-tool';
                config[propertyName].fieldcontainer.labelToolIconQtip =
                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate;
                config[propertyName].fieldcontainer.labelToolIconClick = 'onTranslateClickDescription';
            }
            return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput(config, mode);
        },
        getContent: function (mode) {
            // content
            var propertyName = 'content';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    columnWidth: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.extendeddescription
                },
                columnWidth: 1,
                bind: {
                    value: '{theSchedule.content}'
                }
            };
            if (mode !== CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
                config[propertyName].fieldcontainer.labelToolIconCls = CMDBuildUI.util.helper.IconHelper.getIconId(
                    'flag',
                    'solid'
                );
                config[propertyName].fieldcontainer.labelToolIconQtip =
                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate;
                config[propertyName].fieldcontainer.labelToolIconClick = 'onTranslateClickExtDescription';
            }

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(propertyName, config);
        },
        //
        getOwnerClass: function (mode) {
            // ownerClass
            var config = {};
            var propertyName = 'ownerClass';
            config[propertyName] = {
                fieldcontainer: {
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.klass,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.klass'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._ownerClass_description}'
                    }
                },
                withStandardClasses: true,
                withProcesses: true,
                withDMSModels: true,
                allowBlank: false,
                disabledCls: '',
                bind: {
                    value: '{theSchedule.ownerClass}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                config,
                'ownerClass',
                false,
                'both'
            );
        },
        getOwnerAttribute: function (mode) {
            // ownerAttr
            var config = {};
            var propertyName = 'ownerAttr';
            config[propertyName] = {
                fieldcontainer: {
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.attribute,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.attribute'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._ownerAttr_description}'
                    }
                },
                displayField: 'description',
                valueField: '_id',
                allowBlank: false,
                bind: {
                    store: '{attributesStore}',
                    value: '{theSchedule.ownerAttr}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                'ownerAttr',
                config,
                false,
                false
            );
        },
        //
        getUser: function (mode) {
            var field = {
                xtype: 'bufferedcombo',
                margin: '0 15 0 0',
                labelAlign: 'top',
                fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.user,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.user'
                },
                displayMode: true,
                valueField: '_id',
                displayField: '_description_username',
                columns: [
                    {
                        dataIndex: '_description_username',
                        flex: 1
                    }
                ],
                name: 'userId',
                storealias: 'users',
                modelname: 'CMDBuildUI.model.users.User',
                recordLinkName: 'theSchedule',
                bind: {
                    value: '{theSchedule.userId}'
                },
                /**
                 * @private
                 * @return {Ext.form.DisplayField}
                 */
                _getDisplayField: function () {
                    var display = {
                        xtype: 'displayfield',
                        bind: {
                            value: '{theSchedule._participant_user_username}',
                            hidden: '{!actions.view}'
                        },
                        name: Ext.String.format('{0}_display', this.name),
                        margin: 0
                    };

                    return display;
                }
            };
            return {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                layout: 'hbox',
                items: [field]
            };
        },

        getGroup: function () {
            var field = {
                xtype: 'bufferedcombo',
                margin: '0 15 0 0',
                labelAlign: 'top',
                displayMode: true,
                valueField: '_id',
                displayField: 'description',
                columns: [
                    {
                        dataIndex: 'description',
                        flex: 1
                    }
                ],
                name: 'groupId',
                storealias: 'groups',
                modelname: 'CMDBuildUI.model.users.Group',
                recordLinkName: 'theSchedule',
                bind: {
                    value: '{theSchedule.groupId}'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.group,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.group'
                }
            };
            return {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                layout: 'hbox',
                items: [field]
            };
        },

        //
        getDelayPeriod: function () {
            var config = {};
            var propertyName = 'periodDelay';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.delayfirstdeadline,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.delayfirstdeadline'
                    }
                },
                bind: {
                    store: '{delaysStore}',
                    value: '{delayPeriod}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getDelay: function () {
            // delay
            var config = {};
            var propertyName = 'delay';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.delayfirstdeadlinevalue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.delayfirstdeadlinevalue'
                    }
                },
                minValue: -99999999,
                disabledCls: '',
                bind: {
                    value: '{delay}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                'delay',
                config,
                false,
                false
            );
        },
        getTimeZone: function () {
            // timeZone
            var config = {};
            var propertyName = 'timezone';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.timezone,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.timezone'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._timeZone_description}'
                    },
                    renderer: function (value) {
                        if (!value) {
                            return CMDBuildUI.locales.Locales.administration.common.labels.default;
                        }
                        return value;
                    }
                },
                emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                },
                valueField: '_id',
                displayField: 'description',
                disabledCls: '',
                bind: {
                    store: '{timeZonesStore}',
                    value: '{theSchedule.timeZone}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        //
        getActive: function (mode) {
            // active
            var config = {};
            var propertyName = 'active';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    }
                },
                disabledCls: '',
                bind: {
                    value: '{theSchedule.active}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(config, propertyName, false);
        },

        getCreateAlsoViaWS: function (mode) {
            // active
            var config = {};
            var propertyName = 'createAlsoViaWS';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.createalsoviawebservice,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.createalsoviawebservice'
                    }
                },
                disabledCls: '',
                bind: {
                    value: '{theSchedule.createAlsoViaWS}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(config, propertyName, false);
        },

        getCategory: function (mode) {
            var config = {};
            var propertyName = 'category';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.category,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.category'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._category_description_translation || theSchedule._category_description}'
                    }
                },
                displayField: '_description_translation',
                valueField: 'code',
                forceSelection: true,
                allowBlank: false,
                bind: {
                    store: '{calendarCategoryStore}',
                    value: '{theSchedule.category}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getPriority: function (mode) {
            var config = {};
            var propertyName = 'priority';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.priority,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.priority'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._priority_description_translation || theSchedule._priority_description}'
                    }
                },
                displayField: '_description_translation',
                valueField: 'code',
                forceSelection: true,
                allowBlank: false,
                bind: {
                    store: '{calendarPriorityStore}',
                    value: '{theSchedule.priority}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        //
        getEditable: function (mode) {
            var config = {};
            var propertyName = 'eventEditMode';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.scheduleeditmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.scheduleeditmode'
                    }
                },
                combofield: {
                    bind: {
                        disabled: Ext.String.format(
                            "{theSchedule.sequenceParamsEditMode == '{0}'}",
                            CMDBuildUI.model.calendar.Trigger.sequenceParamsEditModes.hidden
                        )
                    },
                    listeners: {
                        disable: function () {
                            this.lookupViewModel().set(
                                'theSchedule.eventEditMode',
                                CMDBuildUI.model.calendar.Trigger.sequenceParamsEditModes.hidden
                            );
                        }
                    }
                },
                bind: {
                    store: '{eventEditModeStore}',
                    value: '{theSchedule.eventEditMode}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getCascade: function (mode) {
            var config = {};
            var propertyName = 'onCardDeleteAction';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.actionondelete,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.actionondelete'
                    }
                },
                bind: {
                    store: '{cascadeStore}',
                    value: '{theSchedule.onCardDeleteAction}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(propertyName, config, false);
        },
        //
        getCondition: function (mode) {
            // 2 columns
            // conditionScript
            var _config = {};
            var propertyChange = 'conditionScript';
            _config[propertyChange] = {
                fieldcontainer: {},
                fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.condition,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.condition'
                },
                disabledCls: '',
                bind: {
                    value: '{theSchedule.conditionScript}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(propertyChange, _config);
        },
        getNotificationsContainer: function (mode) {
            if (!mode) {
                mode = 'both';
            }
            var me = this;
            return {
                fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
                xtype: 'fieldset',
                layout: 'column',
                columnWidth: 1,
                collapsible: true,
                ui: 'administration-formpagination',
                title: CMDBuildUI.locales.Locales.administration.importexport.texts.notifications,
                items: [
                    {
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        },
                        columnWidth: 1,
                        items: [
                            {
                                xtype: 'button',
                                text: CMDBuildUI.locales.Locales.administration.schedules.addnotification,
                                ui: 'administration-action-small',
                                menu: [
                                    {
                                        text: CMDBuildUI.locales.Locales.administration.emails.email,
                                        type: CMDBuildUI.model.emails.Template.providers.email,
                                        itemId: 'addNotificationEmail',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid')
                                    },
                                    {
                                        text: CMDBuildUI.locales.Locales.administration.emails.inappnotification,
                                        type: CMDBuildUI.model.emails.Template.providers.inappnotification,
                                        itemId: 'addNotificationChat',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bell', 'regular')
                                    },
                                    {
                                        text: CMDBuildUI.locales.Locales.administration.emails.mobilenotification,
                                        type: CMDBuildUI.model.emails.Template.providers.mobilenotification,
                                        itemId: 'addNotificationMobile',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bell', 'regular')
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        columnWidth: 1,
                        padding: '0 10 0 10',
                        bind: {
                            hidden: '{notificationStoreLength}'
                        },
                        items: [
                            {
                                xtype: 'formvalidatorfield',
                                itemId: 'validationField',
                                bind: {
                                    value: '{errorNotificationMandatoryMessage}',
                                    hidden: '{notificationStoreLength}',
                                    errorMessage: '{errorNotificationMandatoryMessage}'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        itemId: 'notificationsContainer',
                        layout: 'column',
                        columnWidth: 1,
                        items: []
                    }
                ],
                listeners: {
                    beforerender: function (view) {
                        var vm = view.lookupViewModel();
                        var notificationsStore = vm.get('notificationsStore');
                        vm.bind(
                            {
                                bindTo: '{theSchedule}',
                                single: true
                            },
                            function (theSchedule) {
                                // find all notifications
                                var notificationsCount = Ext.Array.unique(
                                    Ext.Object.getAllKeys(theSchedule.data)
                                        .join(',')
                                        .match(/notifications___\d{0,2}___template/g)
                                );
                                if (notificationsCount && notificationsCount.length) {
                                    var indexes = Ext.Array.unique(notificationsCount.join(',').match(/\d+/g) || []);
                                    indexes.forEach(function (index) {
                                        var template = theSchedule.get(
                                                Ext.String.format('notifications___{0}___template', index)
                                            ),
                                            templatesStore = Ext.getStore('emails.Templates'),
                                            type = templatesStore.getNotificationProviderOfTemplate(template);

                                        if (template) {
                                            notificationsStore.add({
                                                _id: theSchedule.get(
                                                    Ext.String.format('notifications___{0}____id', index)
                                                ),
                                                type: type,
                                                template: template,
                                                delay: theSchedule.get(
                                                    Ext.String.format('notifications___{0}___delay', index)
                                                ),
                                                report: theSchedule.get(
                                                    Ext.String.format('notifications___{0}___reports___0___code', index)
                                                ),
                                                report_format: theSchedule.get(
                                                    Ext.String.format(
                                                        'notifications___{0}___reports___0___format',
                                                        index
                                                    )
                                                )
                                            });
                                        }
                                    });
                                    notificationsStore.each(function (item, index) {
                                        CMDBuildUI.view.administration.content.schedules.ruledefinitions.card.FormHelper.addNotificationBlock(
                                            vm.getView(),
                                            'both',
                                            index,
                                            item
                                        );
                                    });
                                }
                            }
                        );
                    }
                }
            };
        },
        //
        getNotificationTemplate: function (mode, index) {
            // grid 2 clumns
            // notifications
            var _config = {};
            var propertyName = Ext.String.format('notifications___{0}___template', index);
            _config[propertyName] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.notificationtemplate,
                allowBlank: false,
                displayField: 'description',
                valueField: 'name',
                displayfield: {
                    bind: {
                        value: '{record.template}'
                    },
                    renderer: function (value) {
                        if (value) {
                            try {
                                return Ext.getStore('emails.Templates').findRecord('name', value).get('description');
                            } catch (error) {
                                return value;
                            }
                        }
                        return value;
                    }
                },
                viewModel: {
                    formulas: {
                        storeManager: {
                            bind: {
                                type: '{record.type}',
                                template: '{record.template}',
                                allEmailTemplates: '{allEmailTemplates}'
                            },
                            get: function (data) {
                                var type = data.type;
                                if (!data.type && data.template) {
                                    var templatesStore = Ext.getStore('emails.Templates');
                                    type = templatesStore.getNotificationProviderFromTemplate(data.template);
                                    this.set('record.type', type);
                                }
                                if (type && !this.getStore('templates')) {
                                    var source;
                                    switch (type) {
                                        case CMDBuildUI.model.emails.Template.providers.email:
                                            source = '{allEmailTemplates}';
                                            break;
                                        case CMDBuildUI.model.emails.Template.providers.inappnotification:
                                            source = '{allInAppNotificationTemplates}';
                                            break;
                                        case CMDBuildUI.model.emails.Template.providers.mobilenotification:
                                            source = '{allMobileNotificationTemplates}';
                                            break;
                                    }
                                    this.setStores({
                                        templates: {
                                            source: source
                                        }
                                    });
                                }
                            }
                        }
                    },
                    stores: {}
                },
                bind: {
                    store: '{templates}',
                    value: '{record.template}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(propertyName, _config);
        },
        getNotificationReport: function (mode, index) {
            // grid 2 clumns
            // notifications
            var _config = {};
            var propertyName = Ext.String.format('notifications___{0}___reports___0___code', index);
            _config[propertyName] = {
                fieldcontainer: {
                    allowBlank: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.notificationreport,
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{record.type === "{0}"}',
                            CMDBuildUI.model.emails.Template.providers.inappnotification
                        )
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down(Ext.String.format('#{0}_input', propertyName));
                            input.setValue(null);
                        }
                    },
                    viewModel: {
                        stores: {
                            reports: {
                                source: '{allReports}'
                            }
                        }
                    }
                },
                displayfield: {
                    bind: {
                        value: '{record.report}'
                    },
                    renderer: function (value) {
                        if (value) {
                            try {
                                return Ext.getStore('reports.Reports').findRecord('code', value).get('description');
                            } catch (error) {
                                return value;
                            }
                        }
                        return value;
                    }
                },
                combofield: {
                    displayField: 'description',
                    valueField: 'code',
                    bind: {
                        store: '{reports}',
                        value: '{record.report}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel().getParent();

                            if (newValue) {
                                var report = this.getStore().findRecord('code', newValue);
                                if (report) {
                                    report.getAttributes().then(function (attributesStore) {
                                        vm.set(Ext.String.format('reportAttributesStore_{0}', index), attributesStore);
                                    });
                                }
                            } else {
                                vm.get(Ext.String.format('reportAttributesStore_{0}', index)).setData([]);
                            }
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(propertyName, _config);
        },
        getNotificationReportFormatInput: function (mode, index) {
            var config = {},
                propertyName = Ext.String.format('notifications___{0}___reports___0___format', index);
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
                    },
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{record.type === "{0}"}',
                            CMDBuildUI.model.emails.Template.providers.inappnotification
                        )
                    },
                    viewModel: {
                        stores: {
                            formats: {
                                source: '{reportFormatsStore}'
                            }
                        }
                    }
                },
                bind: {
                    store: '{formats}',
                    value: '{record.report_format}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(propertyName, config);
        },

        getReportParametersGrid: function (mode, index) {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                items: [
                    {
                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        itemId: Ext.String.format('reportParametersGrid_{0}', index),
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        plugins: {
                            ptype: 'actionColumnRowEditing',
                            id: 'actionColumnRowEditing',
                            hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                            clicksToEdit: 10,
                            buttonsUi: 'button-like-tool',
                            errorSummary: false
                        },
                        controller: {
                            control: {
                                '#': {
                                    edit: function (editor, context, eOpts) {
                                        context.record.set('value', editor.editor.items.items[1].getValue());
                                    },
                                    beforeedit: function (editor, context, eOpts) {
                                        if (editor.view.lookupViewModel().get('actions.view')) {
                                            return false;
                                        }
                                        context.record.previousValues = context.record.getData();
                                        return true;
                                    },
                                    canceledit: function (editor, context) {
                                        if (context && context.record) {
                                            var previousValue =
                                                context.record.previousValues && context.record.previousValues.value;
                                            if (previousValue) {
                                                context.record.set('value', previousValue);
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        columnWidth: 1,
                        autoEl: {
                            'data-testid': 'administration-content-schedules-ruledefinition-datatemplates-grid'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{reportParametersStore}'
                        },
                        viewModel: {
                            data: {
                                reportParametersData: null
                            },
                            formulas: {
                                reportAttributesManager: {
                                    bind: {
                                        reportAttributesStore: Ext.String.format('{reportAttributesStore_{0}}', index),
                                        report: '{record.report}'
                                    },
                                    get: function (data) {
                                        var me = this,
                                            _reportParametersData = [];
                                        data.reportAttributesStore.each(function (attribute) {
                                            var param = me.get(
                                                Ext.String.format(
                                                    'theSchedule.notifications___{0}___reports___0___params___{1}',
                                                    index,
                                                    attribute.get('name')
                                                )
                                            );
                                            var storeItem = {
                                                key: attribute.get('name'),
                                                description: attribute.get('description'),
                                                value: param ? param : null
                                            };
                                            _reportParametersData.push(storeItem);
                                        });
                                        me.set('reportParametersData', _reportParametersData);
                                    }
                                }
                            },
                            stores: {
                                reportParametersStore: {
                                    model: 'CMDBuildUI.model.base.KeyDescriptionValue',
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: '{reportParametersData}'
                                }
                            }
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.tasks.parameter,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.parameter'
                                },
                                dataIndex: 'key',
                                align: 'left',
                                editor: {
                                    xtype: 'displayfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                },
                                flex: 1,
                                dataIndex: 'value',
                                align: 'left',
                                editor: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEdit',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            grid.editingPlugin.startEdit(record, 1);
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },

        getMaxActiveEvents: function () {
            var config = {};
            var propertyName = 'maxActiveEvents';
            config[propertyName] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.maxactiveschedules,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.maxactiveschedules'
                    },
                    bind: {
                        hidden: Ext.String.format(
                            '{theSchedule.frequency == "{0}"}',
                            CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once
                        )
                    }
                },
                allowBlank: false,
                minValue: 0,
                bind: {
                    value: '{theSchedule.maxActiveEvents}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                propertyName,
                config,
                false,
                false
            );
        },
        //
        getFrequency: function (mode) {
            var config = {};
            var propertyName = 'frequency';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.frequency,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.frequency'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._frequency_description_translation || theSchedule._frequency_description || theSchedule._frequency}'
                    }
                },
                displayField: '_description_translation',
                valueField: 'code',
                bind: {
                    store: '{calendarFrequencyStore}',
                    value: '{theSchedule.frequency}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getFrequencyMultiplier: function (mode) {
            var config = {};
            var propertyName = 'frequencyMultiplier';
            config[propertyName] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.frequencymultiplier,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.frequencymultiplier'
                    },
                    bind: {
                        hidden: Ext.String.format(
                            '{theSchedule.frequency == "{0}"}',
                            CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once
                        )
                    }
                },
                allowBlank: false,
                minValue: 1,
                bind: {
                    value: '{theSchedule.frequencyMultiplier}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                propertyName,
                config,
                false,
                false
            );
        },
        //
        getSequenceParamsEditMode: function () {
            var config = {};
            var propertyName = 'sequenceParamsEditMode';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.scheduleruleeditmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.scheduleruleeditmode'
                    }
                },
                bind: {
                    store: '{sequenceParamsEditModeStore}',
                    value: '{theSchedule.sequenceParamsEditMode}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getShowGeneratedEventsPreview: function () {
            var config = {};
            var propertyName = 'showGeneratedEventsPreview';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.showschedulepreview,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.showschedulepreview'
                    }
                },
                disabledCls: '',
                bind: {
                    value: '{theSchedule.showGeneratedEventsPreview}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonChekboxInput(
                config,
                propertyName,
                false
            );
        },
        getEndType: function () {
            var config = {};
            var propertyName = 'endType';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.endtype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.endtype'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{theSchedule.frequency === "once"}'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{theSchedule._endType_description_translation || theSchedule._endType_description || theSchedule.endType}'
                    }
                },
                displayField: '_description_translation',
                valueField: 'code',
                bind: {
                    store: Ext.String.format('{{0}Store}', propertyName),
                    value: Ext.String.format('{theSchedule.{0}}', propertyName)
                },
                listeners: {
                    change: function () {
                        try {
                            if (this.getValue() === 'date') {
                                this.up('form').down('#defaultEndDate').show();
                            } else {
                                this.up('form').down('#defaultEndDate').hide();
                            }
                        } catch (error) {}
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                propertyName,
                config,
                false,
                false
            );
        },
        getEndDate: function () {
            var fields = [
                {
                    itemId: 'defaultEndDate',
                    xtype: 'datefield',
                    format: CMDBuildUI.util.helper.UserPreferences.getDateFormat(),
                    formatText: '',
                    hidden: true,
                    bind: {
                        value: '{theSchedule.lastEvent}',
                        hidden: '{action === "VIEW"}'
                    },
                    listeners: {
                        drop: {
                            element: 'el', //bind to the underlying el property on the panel
                            fn: function () {
                                var view = Ext.getCmp(this.id);
                                view.inputEl.focus();
                            }
                        },
                        change: function (input, newValue, oldValue) {
                            var rendered = CMDBuildUI.util.helper.FieldsHelper.renderDateField(this.getValue());
                            if (rendered && !oldValue) {
                                this.setValue(rendered);
                            }
                        }
                    }
                },
                {
                    xtype: 'displayfield',
                    format: CMDBuildUI.util.helper.UserPreferences.getDateFormat(),
                    formatText: '',
                    hidden: true,
                    bind: {
                        value: '{theSchedule.lastEvent}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                    }
                }
            ];
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                fieldLabel: CMDBuildUI.locales.Locales.history.enddate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.history.enddate'
                },
                columnWidth: 1,
                bind: {
                    hidden: '{theSchedule.endType !== "date"}'
                },
                items: fields
            };
        },
        getNumberOfOccurrencies: function () {
            var config = {};
            var propertyName = 'eventCount';
            config[propertyName] = {
                fieldcontainer: {
                    columnWidth: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.numberofoccurrences,
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.numberofoccurrences'
                    },
                    bind: {
                        hidden: Ext.String.format('{theSchedule.endType !== "{0}"}', 'number')
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down(Ext.String.format('#{0}_input', propertyName));
                            input.setMinValue(null);
                            input.setValue(null);
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                input,
                                true,
                                input.up('form')
                            );
                        },
                        show: function (component, eOpts) {
                            var input = component.down(Ext.String.format('#{0}_input', propertyName));
                            input.setMinValue(1);
                            input.setValue(1);
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                input,
                                false,
                                input.up('form')
                            );
                        }
                    }
                },
                minValue: 1,
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{theSchedule.{0}}', propertyName)
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                propertyName,
                config,
                false,
                false
            );
        },

        getDaysAdvanceNotification: function (mode, index) {
            var config = {};
            var propertyName = '_calculated_notification_delay';
            config[propertyName] = {
                fieldcontainer: {
                    viewModel: {
                        formulas: {
                            _calculated_notification_delay: {
                                bind: '{record.delay}',
                                get: function (delay) {
                                    return Math.abs(delay / 60 / 60 / 24);
                                },
                                set: function (value) {
                                    this.set('record.delay', -(value * 60 * 60 * 24));
                                }
                            }
                        }
                    },
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.schedules.daysadvancenotification,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.schedules.daysadvancenotification'
                    },
                    listeners: {
                        listeners: {
                            hide: function (component, eOpts) {
                                var input = component.down(Ext.String.format('#{0}_input', propertyName));
                                input.setMinValue(null);
                                input.setValue(null);
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    true,
                                    input.up('form')
                                );
                            },
                            show: function (component, eOpts) {
                                var input = component.down(Ext.String.format('#{0}_input', propertyName));
                                input.setMinValue(0);
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    false,
                                    input.up('form')
                                );
                            }
                        }
                    }
                },
                minValue: 0,
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{{0}}', propertyName)
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                propertyName,
                config,
                false,
                false
            );
        },

        addNotificationBlock: function (view, mode, index, record) {
            var me = this;
            var container = view.down('#notificationsContainer');
            var fieldset = {
                xtype: 'fieldset',
                bind: {
                    title: '{notificationTitle}'
                },
                viewModel: {
                    data: {
                        record: record
                    },
                    formulas: {
                        notificationTitle: {
                            bind: '{record.type}',
                            get: function (type) {
                                if (!Ext.isEmpty(type)) {
                                    var title;
                                    switch (type) {
                                        case CMDBuildUI.model.emails.Template.providers.email:
                                            title = CMDBuildUI.locales.Locales.administration.emails.email;
                                            break;
                                        case CMDBuildUI.model.emails.Template.providers.inappnotification:
                                            title = CMDBuildUI.locales.Locales.administration.emails.inappnotification;
                                            break;
                                        case CMDBuildUI.model.emails.Template.providers.mobilenotification:
                                            title = CMDBuildUI.locales.Locales.administration.emails.mobilenotification;
                                            break;
                                    }
                                    return title;
                                }
                            }
                        }
                    }
                },
                layout: 'column',
                columnWidth: 1,
                ui: 'administration-formpagination',
                itemId: 'notificationFieldset_' + record.get('_id'),
                items: [
                    {
                        xtype: 'tool',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
                        cls: 'administration-tool',
                        style: 'position: absolute; right: 0;',
                        itemId: 'removeNotificationTool',
                        index: index,
                        recordId: record.get('_id'),
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        }
                    },
                    me.getContainer([
                        me.getNotificationTemplate(mode, index),
                        me.getDaysAdvanceNotification(mode, index)
                    ]),
                    me.getContainer(
                        [me.getNotificationReport(mode, index), me.getNotificationReportFormatInput(mode, index)],
                        {
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format(
                                    '{record.type !== "{0}"}',
                                    CMDBuildUI.model.emails.Template.providers.email
                                )
                            }
                        }
                    ),
                    me.getContainer([me.getReportParametersGrid(mode, index)], {
                        columnWidth: 1,
                        default: {
                            columnWidth: 0.5
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.reportparameters,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.reportparameters'
                        },
                        buttons: undefined,
                        layout: 'fit',
                        hidden: true,
                        bind: {
                            hidden: '{!record.report}'
                        }
                    })
                ]
            };
            container.add(fieldset);
        }
    }
});
