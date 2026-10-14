Ext.define('CMDBuildUI.view.widgets.createmodifycard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-createmodifycard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#widgetclosebtn': {
            click: 'onWidgetCloseBtnClick'
        },
        '#widgetsavebtn': {
            click: 'onWidgetSaveBtnClick'
        }
    },


    /**************************************************************************************************************
     *
     *                                          WIDGET: CreateModifyCard
     *
     *
     * EVENTS:
     *  onBeforeRender              (view, eOpts)                          --> render view with selected object
     *  onAddCardBtnBeforeRender    (button, eOpts)                        --> manage addcard button with subclasses
     *  onWidgetCloseBtnClick       (button, e, eOpts)                     --> close popup
     *  onWidgetSaveBtnClick        (button, e, eOpts)                     --> save object if form is valid
     *
     * UTILS:
     *  extractVariableFromString   (variable, theTarget)                  --> serialize parameteres
     *  addFormToContainer          (classname, cardid, mode)              --> add form to popup
     *  getFormButtons              ()                                     --> get buttons configuration for
     *                                                                         create and edit forms
     *
     * ************************************************************************************************************/


    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.PanelController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel(),
            theWidget = vm.get('theWidget'),
            theTarget = vm.get('theTarget');

        // this type of widget does not support inline mode
        if (theWidget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }

        if (theTarget.get("_id_attachment")) {
            this.viewDataAttachment(theTarget, theWidget);
            return;
        };

        // read only parameter
        var readonly = theWidget.get("readonly") !== undefined ? (theWidget.get("readonly") == 'true' || theWidget.get("readonly") == 1) : false;
        readonly = theWidget.get("ReadOnly") !== undefined ? (theWidget.get("ReadOnly") == 'true' || theWidget.get("ReadOnly") == 1) : readonly;
        // get object type and id
        var objectId, objectTypeName;
        if (theWidget.get("ClassName")) {
            objectTypeName = theWidget.get("ClassName");
            if (theWidget.get("ObjId")) {
                var objId = theWidget.get("ObjId");
                var theIdObj = me.extractVariableFromString(objId, theTarget);
                if (theIdObj.length) { // is array
                    objectId = theIdObj.pop();
                } else {
                    // is object
                    objectId = Object.values(theIdObj).pop();
                }
            }
        } else if (theWidget.get("Reference")) {
            var targetFieldName = theWidget.get("Reference");
            var refDefinition = theTarget.getField(targetFieldName);
            if (!refDefinition) {
                targetFieldName = me.getView().getOutput();
                refDefinition = theTarget.getField(targetFieldName);
            }
            if (refDefinition) {
                objectId = theTarget.get(targetFieldName);
                objectTypeName = refDefinition.attributeconf.targetClass;
            }
        }

        vm.set("objectTypeName", objectTypeName);

        if (!objectTypeName) {
            CMDBuildUI.util.Logger.log("Widget configuration error", CMDBuildUI.util.Logger.levels.error, null, theWidget.getData());
            vm.set('addbtn.hidden', true);
            return this.getView().setHtml('<div style="margin-left:10px"> Widget configuration error </div>');
        }

        var mode;
        if (readonly) {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.read;
        } else if (objectId) {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.update;
        } else {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.create;
        }

        var klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName);
        if (klass) {
            vm.set("klassdescription", klass.getTranslatedDescription());
            if (klass.get("prototype")) {
                if (mode == CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                    vm.set("addbtn.disabled", false);
                } else if (objectId) {
                    // get submodel
                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, // 'class'
                        objectTypeName
                    ).then(function (model) {
                        model.load(objectId, {
                            success: function (record) {
                                var subModel = record.get('_model');
                                if ((mode == CMDBuildUI.util.helper.FormHelper.formmodes.create && !subModel._can_create) ||
                                    (mode == CMDBuildUI.util.helper.FormHelper.formmodes.update && !subModel._can_modify)) {
                                    mode = CMDBuildUI.util.helper.FormHelper.formmodes.read;
                                }
                                me.addFormToContainer(record.get("_type"), record.getId(), mode);
                            }
                        });
                    });
                }
            } else {
                if (objectId || mode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                    this.addFormToContainer(objectTypeName, objectId, mode);
                }
            }
        } else {
            CMDBuildUI.util.Logger.log("Class not found");
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var classname = vm.get('objectTypeName');
        var view = this.getView();
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                vm.set("addbtn.disabled", false);

                if (view.getForm() != null) {
                    view.removeForm();
                }

                me.addFormToContainer(item.objectTypeName, undefined, CMDBuildUI.util.helper.FormHelper.formmodes.create);
            },
            classname
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetCloseBtnClick: function (button, e, eOpts) {
        button.lookupViewModel().get("theObject").reject();
        this.getView().fireEvent("popupclose");
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            vm = this.getViewModel(),
            view = this.getView(),
            closeButton = view.down("#widgetclosebtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, closeButton]);

        // save data
        view.getForm().getController().saveForm({
            failure: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, closeButton]);
            }
        }).then(function (record) {
            const wconf = vm.get('theWidget');
            const attr = wconf.get("Reference") || me.getView().getOutput();
            if (attr) {
                let readonly = wconf.get("readonly") !== undefined ? (wconf.get("readonly") == 'true' || wconf.get("readonly") == 1) : false;

                readonly = wconf.get("ReadOnly") !== undefined ? (wconf.get("ReadOnly") == 'true' || wconf.get("ReadOnly") == 1) : readonly;
                // form mode cannot be "read-only"
                if (!readonly) {
                    // find output field or reference field on detailwindow form
                    const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
                    const form = detailWindow.down('form').getForm();
                    // Need for update the visualization of edited card on the owner of the widget
                    const field = form.getFields().findBy(function (field) {
                        return field.getName && field.getName() === attr;
                    });
                    if (field) {
                        // getStore is avaible only with a combobox
                        if(field.getStore) {
                            field.setValue(record);
                        } else {
                            field.setValue(record.get('Description'));
                        }
                    }
                }
                vm.get("theTarget").set(attr, record.getId());
            }

            if (wconf.get("_required")) {
                wconf.getOwner().setValue(true);
            }

            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            me.getView().fireEvent("popupclose");
        }).otherwise(function () {
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            CMDBuildUI.util.Utilities.enableFormButtons([button, closeButton]);
        });
    },

    privates: {
        /**
         * @property
         */
        form: null,

        /**
         * Custom configuration parameters for this widget
         */
        parameters: {
            ReadOnly: "ReadOnly",
            ClassName: "ClassName",
            ObjId: "ObjId",
            Reference: "Reference"
        },

        /**
         *
         * @param {String} classname
         * @param {Number} cardid
         * @param {String} mode
         */
        addFormToContainer: function (classname, cardid, mode) {
            var view = this.getView(),
                config;
            if (mode === CMDBuildUI.util.helper.FormHelper.formmodes.update) {
                config = {
                    xtype: 'classes-cards-card-edit',
                    buttons: this.getFormButtons()
                };
                view.lookupViewModel().set("basepermissions", {
                    edit: true
                });
            } else if (mode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                config = {
                    xtype: 'classes-cards-card-create',
                    fireGlobalEventsAfterSave: false,
                    defaultValues: this.getDefaultValues(),
                    buttons: this.getFormButtons()
                };
            } else {
                config = {
                    xtype: 'classes-cards-card-view',
                    objectTypeName: classname,
                    objectId: cardid,
                    shownInPopup: true,
                    tabpaneltools: []
                };
            }
            Ext.apply(config, {
                bodyPadding: "0 10",
                viewModel: {
                    data: {
                        objectTypeName: classname,
                        objectId: cardid,
                        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                    }
                }
            });

            var form = view.add(config);
            view.setForm(form);
        },

        /**
         * get defaults values
         */
        getDefaultValues: function () {
            var me = this;
            var vm = this.getViewModel();
            var theWidget = vm.get('theWidget');
            var theTarget = vm.get("theTarget");
            var data = theWidget.getData();
            var defaults = [];
            // get default values
            for (var key in data) {
                // check that key is not system key or configuration parameter
                if (!Ext.String.startsWith(key, "_") && !this.parameters[key]) {
                    defaults.push({
                        attribute: key,
                        value: me.extractVariableFromString(data[key], theTarget)
                    });
                }
            }
            defaultValues = me.cleanDefaultValues(defaults);
            return defaultValues;
        },

        /**
         * @param {Array} defaultValues
         * @return {Array} The cleaned array.
         * clean default values
         */
        cleanDefaultValues: function (defaultValues) {
            var cleanedValues = [];
            defaultValues.forEach(function (defaultvalue) {
                var theValue = defaultvalue.value;

                if (Ext.isObject(theValue)) {
                    defaultvalue.value = Object.values(theValue).pop();
                }
                cleanedValues.push(defaultvalue);
            });
            return cleanedValues;
        },



        /**
         * Resolve variable.
         * @param {String} variable
         * @param {CMDBuildUI.model.base.Base} theTarget
         * @return {*} The variable resolved.
         */
        extractVariableFromString: function (variable, theTarget) {
            variable = variable.toString();
            variable = variable.replace("{", "").replace("}", "");
            var s_variable = variable.split(":");
            if (s_variable[0] === "server") {
                return CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
            } else if (s_variable[0] === "client") {
                return CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
            } else if (s_variable.length === 1 && theTarget.getField(s_variable[0])) {
                return theTarget.get(s_variable[0]);
            } else {
                return [variable]; //for issue #2553
            }
        },

        /**
         * Return buttons configuration.
         * @return {Object[]}
         */
        getFormButtons: function () {
            return [{
                ui: 'secondary-action',
                itemId: 'widgetclosebtn',
                text: CMDBuildUI.locales.Locales.common.actions.close,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-close'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.close'
                }
            }, {
                ui: 'management-primary',
                itemId: 'widgetsavebtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-save'
                },
                formBind: true,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            }];
        },

        /**
         * View data if an event is created from an attachment
         * @param {CMDBuildUI.model.base.Base} theTarget
         * @param {Ext.panel.Panel} theWidget
         */
        viewDataAttachment: function (theTarget, theWidget) {
            var view = this.getView(),
                className = theTarget.get("_origin_type"),
                objId = theTarget.get("_origin_card"),
                attachmentId = theTarget.get("_id_attachment"),
                attachmentModel = theWidget.get("ClassName");

            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                attachmentModel
            ).then(function (model) {
                var url = CMDBuildUI.util.api.Classes.getAttachments(className, objId),
                    attachment = Ext.create(model.getName(), {
                        _id: attachmentId
                    });
                attachment.getProxy().setUrl(url);
                attachment.load({
                    success: function (record, operation) {
                        config = new Ext.container.Container({
                            items: [{
                                xtype: 'dms-attachment-view',
                                bodyPadding: "0 10",
                                viewModel: {
                                    data: {
                                        DMSCategoryTypeName: "",
                                        DMSModelClassName: attachmentModel,
                                        record: record
                                    }
                                }
                            }, {
                                xtype: 'displayfield',
                                labelSeparator: '',
                                margin: '30 0 0 25',
                                fieldLabel: CMDBuildUI.locales.Locales.attachments.preview,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.attachments.preview'
                                }
                            }, {
                                xtype: 'dms-preview',
                                margin: '10 0 0 25',
                                alt: CMDBuildUI.locales.Locales.attachments.preview,
                                localized: {
                                    alt: 'CMDBuildUI.locales.Locales.attachments.preview'
                                },
                                attachmentId: attachmentId,
                                fileName: record.get("FileName"),
                                fileMimeType: record.get("MimeType"),
                                proxyUrl: url,
                                viewModel: {
                                    data: {
                                        objectType: "class",
                                        objectTypeName: className,
                                        objectId: objId,
                                        record: record
                                    }
                                }
                            }]
                        });

                        var form = view.add(config);
                        view.setForm(form);
                    }
                });
            });
        }
    }
});