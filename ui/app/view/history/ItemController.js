Ext.define('CMDBuildUI.view.history.ItemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.history-item',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.history.Item} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // get element id
        var config = view.getInitialConfig(),
            vm = this.getViewModel();

        if (!Ext.isEmpty(config._rowContext)) {
            var record = config._rowContext.record; // get widget record
            if (record && record.getData()) {
                if (record.get("_fieldName")) {
                    vm.set("record", record);
                } else {
                    view.setObjectId(record.get("_id"));
                    view.setHistoryType(record.get("_historyType"));
                    view.setType(record.get('_type'));
                }
            }
        }

        if (!vm.get("record")) {
            switch (view.getHistoryType()) {
                case 'reference':
                case 'relation':
                    this.renderRelationHistory();
                    break;
                case 'system':
                case 'card':
                    this.renderCardHistory();
                    break;
                default:
                    break;
            }
        } else {
            this.renderAttributeHistory();
        }
    },

    /**
     * @private
     */
    privates: {

        /**
         * Render history for attribute
         */
        renderAttributeHistory: function () {
            var item,
                record = this.getViewModel().get("record");

            if (record.get("_fieldName") == CMDBuildUI.locales.Locales.common.tabs.notes) {
                item = [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.common.tabs.notes,
                    name: "Notes",
                    anchor: '100%',
                    labelPad: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelPad,
                    labelSeparator: CMDBuildUI.util.helper.FormHelper.fieldDefaults.labelSeparator,
                    bind: {
                        value: Ext.String.format("{{0}.{1}}", 'record', "Notes")
                    }
                }]
            } else {
                item = CMDBuildUI.util.helper.FormHelper.getFormField(
                    record.get("_modelField"), {
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                    linkName: 'record'
                });
            }

            this.getView().add({
                xtype: 'container',
                layout: 'fit',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                items: item
            })
        },

        /**
         * 
         * @param {Ext.data.Store} attributes 
         * @returns 
         */
        createFormModel: function (attributes) {
            var modelConfig = {
                extend: 'CMDBuildUI.model.base.Base',
                fields: [{
                    name: 'type',
                    type: 'string',
                    description: CMDBuildUI.locales.Locales.calendar.type,
                    attributeconf: {
                        showInGrid: false,
                        group: '',
                        name: 'type',
                        linkName: 'theObject'
                    },
                    writable: false,
                    cmdbuildtype: 'string'
                }, {
                    name: 'relatedDescription',
                    type: 'string',
                    description: CMDBuildUI.locales.Locales.relations.description,
                    attributeconf: {
                        showInGrid: false,
                        group: '',
                        name: 'relatedDescription',
                        linkName: 'theObject'
                    },
                    writable: false,
                    cmdbuildtype: 'string'
                }],
                proxy: 'memory'
            };

            attributes.each(function (attribute) {
                Ext.Array.push(modelConfig.fields, CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attribute));
            });

            return Ext.define(null, modelConfig);
        },

        /**         
         * @param {Ext.data.Model} formModel 
         * @returns 
         */
        createForm: function (formModel) {
            var tabpanel = CMDBuildUI.util.helper.FormHelper.renderForm(formModel, {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                showNotes: false,
                showAsFieldsets: true,
                grouping: [],
                excludeAttributeTypes: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula]
            });
            return tabpanel;
        },

        /**
         * 
         * @param {Ext.data.Model} theObject 
         * @param {String} fieldName 
         * @param {Ext.form.field.Field} field 
         */
        markFieldsIfChanged: function (theObject, fieldName, field) {
            if (theObject.get("_" + fieldName + "_changed")) {
                field.addCls("highlight-field");
                var panel = field.up("panel"); //.down('panel');
                if (!panel.tab) {
                    panel = panel.up();
                }
                if (!panel._haschanges && panel.getTitle) {
                    panel.setTitle(panel.getTitle() + " <small class='" + CMDBuildUI.util.helper.IconHelper.getIconId('circle', 'solid') + " mark-changes'></small>");
                    panel._haschanges = true;
                }
            }
        },

        /**
         * Render history for card
         */
        renderCardHistory: function () {
            var me = this,
                view = this.getView(),
                vm = view.lookupViewModel(),
                objectType = vm.get("objectType"),
                objectTypeName = vm.get("objectTypeName"),
                model = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(objectType, objectTypeName);

            model.setProxy({
                url: vm.get("storedata.proxyurl"),
                type: 'baseproxy'
            });
            vm.linkTo("theObject", {
                type: model.getName(),
                id: view.getObjectId()
            });

            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
            var grouping = item.attributeGroups().getRange();
            // get form fields
            var tabpanel = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                showNotes: true,
                isCalendar: objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
                grouping: grouping,
                excludeAttributeTypes: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula]
            });

            Ext.apply(tabpanel, {
                tools: view.tabpaneltools
            });
            view.add(tabpanel);

            // mark changes
            vm.bind({
                bindTo: '{theObject}'
            }, function (theObject) {
                if (theObject) {
                    view.getForm().getFields().getRange().forEach(function (field) {
                        if (!field.hidden || (field.config && field.config.isCalendarNotes)) {
                            me.markFieldsIfChanged(theObject, field.getName(), field);
                        }
                    });
                }
            });
        },

        /**
         * Render history for relations       
         */
        renderRelationHistory: function () {
            var me = this,
                view = this.getView(),
                vm = view.lookupViewModel(),
                objectType = vm.get("objectType"),
                objectTypeName = vm.get("objectTypeName"),
                damainName = view.getType(),
                model = CMDBuildUI.util.helper.ModelHelper.getHistoryModel('domains', 'Relation');

            model.setProxy({
                url: Ext.String.format('/domains/{0}/relations/history', view.getType()),
                type: 'baseproxy'
            });
            vm.linkTo("theObject", {
                type: model.getName(),
                id: view.getObjectId()
            });

            vm.bind({
                bindTo: '{theObject}'
            }, function (theObject) {
                if (theObject) {
                    var domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(damainName, 'domain');
                    domain.getAttributes(true).then(function (attributes) {

                        var mainObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
                        var typeFieldNameValue, descriptionFieldName;
                        if (mainObject.getHierarchy().indexOf(theObject.get('_sourceType')) > -1) {
                            typeFieldName = '_destinationType';
                            typeFieldNameValue = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(theObject.get('_destinationType'));
                            descriptionFieldName = '_destinationDescription';
                        } else {
                            typeFieldName = '_sourceType';
                            typeFieldNameValue = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(theObject.get('_sourceType'));
                            descriptionFieldName = '_sourceDescription';
                        }
                        var formModel = me.createFormModel(attributes);
                        var tabpanel = me.createForm(formModel);
                        view.add(tabpanel);
                        view.getForm().getFields().getRange().forEach(function (field) {
                            switch (field.getName()) {
                                case 'type':
                                    field.setValue(typeFieldNameValue);
                                    me.markFieldsIfChanged(theObject, typeFieldName, field);
                                    break;
                                case 'relatedDescription':
                                    field.setValue(theObject.get(descriptionFieldName));
                                    me.markFieldsIfChanged(theObject, descriptionFieldName, field);
                                    break;
                                default:
                                    me.markFieldsIfChanged(theObject, field.getName(), field);
                                    break;
                            }
                        });
                    });
                }
            });
        }
    }
});