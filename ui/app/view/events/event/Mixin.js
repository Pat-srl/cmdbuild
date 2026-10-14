Ext.define('CMDBuildUI.view.events.event.Mixin', {
    mixinId: 'view-events-event-mixin',

    config: {
        hideWidgets: false
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
     *
     * @param {Object[]} items
     * @return {Object}
     */
    getMainPanelForm: function (items) {
        // create panel
        const panelitems = [{
            flex: 1,
            scrollable: 'y',
            items: [{
                items: items
            }]
        }];

        if (!this.getHideWidgets()) {
            panelitems.push({
                xtype: 'widgets-launchers',
                formMode: this.formmode,
                bind: {
                    widgets: '{theEvent.widgets}'
                }
            });
        }
        return {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch' //stretch vertically to parent
            },
            height: "100%",
            items: panelitems
        };
    },

    /**
     * Add rules for fields visibility
     */
    addAutoValueRules: function () {
        const vm = this.lookupViewModel();
        this.getForm().getFields().getRange().forEach(function (f) {
            if (f.setValueFromAutoValue !== undefined) {
                vm.bind(f.getAutoValueBind(), function (data) {
                    f.setValueFromAutoValue();
                });
            }
        });
    },

    /**
     * 
     * @returns 
     */
    getUserGroupParticipantsFields: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly)) {
            return this.getuserGroupParticipantsFieldsRead();
        } else {
            return this.getUserGroupParticipantsFieldsWrite();
        }
    },

    /**
     * 
     * @returns 
     */
    getuserGroupParticipantsFieldsRead: function () {
        return {
            xtype: 'container',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                flex: 0.5,
                layout: "anchor",
                minHeight: 1,
                padding: "0 15 0 15",
                xtype: "fieldcontainer"
            },
            items: [{
                xtype: 'displayfield',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantuser,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantuser'
                },
                bind: {
                    value: '{theEvent._participant_user_username}'
                }
            }, {
                xtype: 'displayfield',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantgroup,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantgroup'
                },
                bind: {
                    value: '{theEvent._participant_group_name}'
                },
                itemId: 'partecipantgroup',
                valueField: 'value',
                displayField: 'label',
                store: undefined
            }]
        }
    },

    /**
     * 
     * @returns 
     */
    getUserGroupParticipantsFieldsWrite: function () {
        return {
            xtype: 'container',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                flex: 0.5,
                layout: "anchor",
                minHeight: 1,
                padding: "0 15 0 15",
                xtype: "fieldcontainer"
            },
            items: [{
                xtype: 'combobox',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantuser,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantuser'
                },
                bind: {
                    value: '{theEvent._participant_user_id}',
                    store: '{partecipantuserStore}'
                },
                itemId: 'partecipantuser',
                valueField: 'value',
                displayField: 'label',
                viewModel: {
                    formulas: {
                        partecipantuserStore: {
                            bind: '{theEvent._participant_users}',
                            get: function (partecipantUser) {
                                // set the store of the partecipant user;
                                const theSession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
                                    // get the current user
                                    newData = [{
                                        label: theSession.get('userDescription'),
                                        value: theSession.get('userId')
                                    }];

                                // get the user set by the administration
                                if (partecipantUser && partecipantUser.length && partecipantUser[0]._id != theSession.get('userId')) {
                                    newData.push({
                                        label: partecipantUser[0].username,
                                        value: partecipantUser[0]._id
                                    })
                                }

                                // set the stroe for the comboox
                                return Ext.create('Ext.data.Store', {
                                    model: 'CMDBuildUI.model.base.ComboItem',
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: newData
                                });

                            }
                        }
                    }
                }
            }, {
                xtype: 'combobox',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantgroup,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantgroup'
                },
                bind: {
                    value: '{theEvent._participant_group_id}',
                    store: '{partecipantgroupStore}'
                },
                itemId: 'partecipantgroup',
                valueField: 'value',
                displayField: 'label',
                viewModel: {
                    formulas: {
                        partecipantgroupStore: {
                            bind: '{theEvent._participant_groups}',
                            get: function (partecipantGroup) {
                                // set the store for groupuser;
                                const theSession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
                                    newData = [],
                                    // get all the available groups
                                    availableGroups = theSession.get('availableRolesExtendedData');

                                availableGroups.forEach(function (group) {
                                    newData.push({
                                        label: group._description_translation || group.description,
                                        value: group._id
                                    });
                                }, this);

                                // adds the group from the administration module
                                if (partecipantGroup && partecipantGroup.length) {
                                    const found = Ext.Array.findBy(newData, function (element, index) {
                                        if (element.value == partecipantGroup[0]._id) return true;
                                    }, this);

                                    if (!found) {
                                        newData.push({
                                            label: partecipantGroup[0].name,
                                            value: partecipantGroup[0]._id
                                        })
                                    }
                                }

                                // set the store in the combo
                                return Ext.create('Ext.data.Store', {
                                    model: 'CMDBuildUI.model.base.ComboItem',
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: newData
                                });
                            }
                        }
                    }
                }
            }]
        };
    },

    /**
     * 
     * @returns 
     */
    getNotificationTemplateComboField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly)) {
            return this.getNotificationTemplateComboFieldRead();
        } else {
            return this.getNotificationTemplateComboFieldWrite();
        }
    },

    /**
     * 
     * @returns 
     */
    getNotificationTemplateComboFieldRead: function () {//FIXME:make a better function
        const roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(
            Ext.merge(
                this._getNotificationTemplateComboField(),
                {
                    cmdbuildtype: 'text',
                    attributeconf: {
                        editorType: null
                    },
                    disabled: true,
                    bind: {
                        hidden: '{hiddenField}'
                    },
                    viewModel: {
                        formulas: {
                            hiddenField: {
                                bind: {
                                    value: '{theEvent.notifications___0___template}'
                                },
                                get: function (data) {
                                    return Ext.isEmpty(data.value);
                                }
                            }
                        }
                    }
                }
            ));
        return Ext.merge(
            this._getNotificationTemplateComboField(),
            roField
        );
    },

    /**
     * 
     * @returns 
     */
    getNotificationTemplateComboFieldWrite: function () {
        return this._getNotificationTemplateComboField();
    },

    /**
     * 
     * @returns 
     */
    _getNotificationTemplateComboField: function () {
        return {
            xtype: 'combobox',
            store: Ext.create('Ext.data.Store', {
                autoLoad: true,
                model: 'CMDBuildUI.model.emails.Template',
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                    extraParams: {
                        limit: 0
                    }
                },
                advancedFilter: {
                    attributes: {
                        provider: [{
                            operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                            value: ['email']
                        }]
                    }
                }
            }),
            fieldLabel: CMDBuildUI.locales.Locales.calendar.notificationtemplate,
            hidden: false,
            displayField: 'description',
            valueField: 'name',
            bind: {
                value: '{theEvent.notifications___0___template}'
            }
        };
    },

    /**
     * 
     * NOTIFICATION CONTENT
     * @returns 
     */
    getNotificationContentField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly)) {
            return this.getNotificationContentFieldRead();
        } else {
            return this.getNotificationContentFieldWrite();
        }
    },

    /**
     * 
     * @returns 
     */
    getNotificationContentFieldRead: function () {
        const field = this._getNotificationField(),
            roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(field);
        return Ext.apply(field, roField);
    },

    /**
     * 
     * @returns 
     */
    getNotificationContentFieldWrite: function () {
        const field = this._getNotificationField(),
            editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);
        return Ext.apply(field, editor);
    },

    _getNotificationField: function () {
        return {
            attributeconf: {
                editorType: null
                // showIf: "return !Ext.isEmpty(api.getValue('_notification_template'))"
            },
            fieldLabel: CMDBuildUI.locales.Locales.calendar.notificationtext,
            localized: {
                description: 'CMDBuildUI.locales.Locales.calendar.notificationtext'
            },
            hidden: true,
            bind: {
                value: '{theEvent.notifications___0___content}',
                hidden: '{hiddenField}'
            },
            cmdbuildtype: 'text',
            viewModel: {
                formulas: {
                    hiddenField: {
                        bind: {
                            value: '{theEvent.notifications___0___template}'
                        },
                        get: function (data) {
                            return Ext.isEmpty(data.value);
                        }
                    }
                }
            }
        };
    },

    /**
     * 
     * @returns 
     */
    getDaysAdvanceNofificationField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly)) {
            return this.getDaysAdvanceNofificationFieldRead();
        } else {
            return this.getDaysAdvanceNofificationFieldWrite();
        }
    },
    /**
     * 
     * @returns 
     */
    getDaysAdvanceNofificationFieldRead: function () {
        const field = this._getDaysAdvanceNofificationField(),
            roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(field),
            rField = Ext.applyIf(field, roField);
        return rField;
    },

    /**
     * 
     * @returns 
     */
    getDaysAdvanceNofificationFieldWrite: function () {
        const field = this._getDaysAdvanceNofificationField();
        var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);
        editor = Ext.applyIf(editor, {
            listeners: {
                change: {
                    scope: this,
                    fn: function (editor, value, startValue, eOpts) {
                        this.getViewModel().get("theEvent").set('notifications___0___delay', -value * (60 * 60 * 24));
                    }
                }
            }
        });
        return Ext.applyIf(field, editor);
    },

    /**
     * 
     * @returns 
     */
    _getDaysAdvanceNofificationField: function () {
        return {
            attributeconf: {
                editorType: null,
                showThousandsSeparator: false
                // unitOfMeasure: CMDBuildUI.locales.Locales.calendar.days
            },
            writable: true,
            fieldLabel: CMDBuildUI.locales.Locales.calendar.advancenotification,
            localized: {
                description: 'CMDBuildUI.locales.Locales.calendar.advancenotification'
            },
            hidden: true,
            bind: {
                value: '{daysAdvanceNotificationsField}'/* {theEvent._notification_delay} */,
                hidden: '{hiddenField}'
            },
            cmdbuildtype: 'string', //setting cmdbuildType = integer causes an error. That's because is missing unit of mesure
            viewModel: {
                data: {
                    daysAdvanceNotificationsField: null,
                    hiddenField: true
                },
                formulas: {
                    daysAdvanceNotificationsField: {
                        bind: {
                            value: '{theEvent.notifications___0___delay}'
                        },
                        get: function (data) {
                            if (data.value) {
                                return Math.abs(-data.value / (60 * 60 * 24));
                            }
                        }
                    },
                    hiddenField: {
                        bind: {
                            value: '{theEvent.notifications___0___template}'
                        },
                        get: function (data) {
                            return Ext.isEmpty(data.value);
                        }
                    }
                }
            }
        };
    },

    /**
     * 
     * OPERATION FIELD
     * @returns 
     */
    getOperationField: function () {
        return {
            xtype: 'combobox',
            itemId: 'operationcombo',
            fieldLabel: CMDBuildUI.locales.Locales.calendar.operation,
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: '{theEvent._operation}',
                hidden: '{hiddenField}'
            },
            store: Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.base.ComboItem',
                proxy: {
                    type: 'memory'
                },
                data: [{
                    label: CMDBuildUI.locales.Locales.calendar.complete, value: 'completed'
                }, {
                    label: CMDBuildUI.locales.Locales.calendar.cancel, value: 'canceled'
                }]
            }),
            viewModel: {
                formulas: {
                    hiddenField: {
                        bind: {
                            value: '{theEvent.status}'
                        },
                        get: function (data) {
                            return data.value == "completed" || data.value == 'canceled';
                        }
                    }
                }
            }
        };
    },

    /**
     * 
     * NOTIFICATION DELAY
     * @returns 
     */
    getNotificationDelayField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly)) {
            return this.getNotificationDelayFieldRead();
        } else {
            return this.getNotificationDelayFieldWrite();
        }
    },

    /**
     * 
     * MISSING DAYS
     * @returns 
     */
    getMissingDaysExtraConf: function () {
        return {
            bind: {
                value: '{dateChange}',
                hidden: '{visibility}'
            },
            renderer: function (value, field) {
                const color = (value < 0) ? 'red' : 'black';
                return !Ext.isEmpty(value) ? '<span style="color:' + color + ';">' + value + '</span>' : null;
            },
            viewModel: {
                formulas: {
                    dateChange: {
                        bind: '{theEvent.date}',
                        get: function (date) {
                            var value;
                            if (date) {
                                const now = new Date();
                                value = Ext.Date.diff(now, date, 'd') + 1;
                            }
                            return value;
                        }
                    },
                    visibility: {
                        bind: {
                            status: '{theEvent.status}',
                            date: '{theEvent.date}'
                        },
                        get: function (data) {
                            if (data.status == 'expired' || data.status == 'active') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        };

    },

    /**
     * 
     * @returns 
     */
    getClassCombobox: function () {
        var selectableClasses = CMDBuildUI.util.helper.Configurations.get('cm_system_scheduler_selectableclasses');
        selectableClasses = selectableClasses.split(',');

        const store = Ext.create('Ext.data.ChainedStore', {
            source: Ext.getStore('classes.Classes'),
            grouper: {
                groupFn: function (item) {
                    if (item.isClass) {
                        return CMDBuildUI.locales.Locales.menu.classes;
                    }
                }
            },
            filters: [{
                property: '_id',
                operator: 'in',
                value: selectableClasses
            }]
        }),
            theEvent = this.getViewModel().get("theEvent")

        return {
            xtype: 'groupedcombo',
            store: store,
            fieldLabel: CMDBuildUI.locales.Locales.calendar.class,
            displayField: 'description',
            valueField: '_id',
            value: theEvent.get('_card_type'),
            bind: {
                value: '{theEvent._card_type}'
            },
            queryMode: 'local',
            listeners: {
                change: {
                    fn: function (groupedcombo, newValue, oldValue, eOpts) {
                        const container = groupedcombo.up().up().items.items[1];
                        theEvent.set('card', null);
                        container.removeAll();

                        if (!Ext.isEmpty(newValue)) {
                            container.add(this.getCardCombobox(newValue));
                        }
                    },
                    scope: this
                }
            }
        };
    },

    /**
     * 
     * @param {*} targetType 
     * @returns 
     */
    getCardCombobox: function (targetType) {
        const theEvent = this.getViewModel().get("theEvent");

        targetType = targetType || theEvent.get('_card_type');

        return {
            xtype: 'referencecombofield',
            fieldLabel: CMDBuildUI.locales.Locales.calendar.associatedcard,
            displayField: 'Description',
            valueField: '_id',
            allowBlank: false,
            value: theEvent.get('card'),
            metadata: {
                targetType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(targetType),
                targetClass: targetType
            },
            name: 'card',
            bind: {
                value: '{theEvent.card}'
            }
        };
    },

    privates: {
        _missingdays_row_index: 0,
        _partecipantGroup_row_index: 3,
        _notification_delay_row_index: 4,
        _notificationText_row_index: 5,
        _operation_row_index: 6,
        _class_row_index: 8,
        _card_row_index: 8,

        /**
         * 
         * @returns 
         */
        getFormLayout: function () {
            return {
                _nogroup: {
                    rows: [{
                        columns: [{//row 0
                            fields: [{
                                attribute: 'date'
                            }]
                        }, {
                            fields: [{
                                attribute: 'missingDays'
                            }]
                        }]
                    }, {//row 1
                        columns: [{
                            fields: [{
                                attribute: 'category'
                            }]
                        }, {
                            fields: [{
                                attribute: 'priority'
                            }]
                        }]
                    }, {//row 2
                        columns: [{
                            fields: [{
                                attribute: 'description'
                            }]
                        }, {
                            fields: [{
                                attribute: 'content'
                            }]
                        }]
                    },
                    //Row 3 for partecipants;

                    {//Row 4
                        columns: (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read) || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly) ?
                            /**
                             * for read mode
                             */
                            [{
                                width: '0.5',
                                fields: [{
                                    attribute: ''//daysAdvanceNotification
                                }]
                            }]
                            :
                            /**
                             * for create/edit mode
                             */
                            [{
                                width: '0.5',
                                fields: [{
                                    attribute: '' //_notification_template
                                }]
                            }, {
                                width: '0.5',
                                fields: [{
                                    attribute: ''//daysAdvanceNotification
                                }]
                            }]
                    },

                    {//Row 5
                        columns: [{
                            fields: [{
                                attribute: ''//notifications___0___delay
                            }]
                        }]
                    },
                    {//Row 6
                        columns: [{
                            fields: [{
                                attribute: 'status'
                            }]

                        }, {
                            fields: [{
                                attribute: 'completion'
                            }]
                        }]
                    },
                    { //row 7
                        columns: [{
                            fields: [{
                                attribute: 'Type'
                            }]
                        }, {
                            fields: [{
                                attribute: 'status'
                            }]
                        }]
                    }, { //row 8
                        columns: [{
                            fields: [{
                                attribute: 'class'
                            }]
                        }, {
                            fields: [{
                                attribute: 'card'
                            }]
                        }]
                    }]
                }
            };
        },

        /**
         *
         * @param {[Object]} panel  the result of the function renderForm
         * @param {String} fieldName the field name to search
         */
        findFieldAfterRender: function (panel, fieldName) {
            return this._recursiveVisit(panel[0], fieldName);
        },

        /**
         *
         */
        _recursiveVisit: function (node, fieldName) {
            if (node.name == fieldName) {
                return node;
            }

            var v;
            if (node.items) {

                node.items.forEach(function (item, index, array) {
                    v = v || this._recursiveVisit(item, fieldName);
                }, this);
            }
            return v;
        }
    }
});