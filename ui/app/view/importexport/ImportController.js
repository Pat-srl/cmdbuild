Ext.define('CMDBuildUI.view.importexport.ImportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.importexport-import',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#templatescombo': {
            change: 'onTemplatesComboChange'
        },
        "#importbtn": {
            click: 'onImportBtnClick'
        },
        '#downloadreportbtn': {
            afterrender: 'onDownloadReportBtnAfterRender',
            click: 'onDownloadReportBtnClick'
        },
        '#sendreportbtn': {
            click: 'onSendReportBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.importexport.Import} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, view.getObject().get("name")).then(function (model) {
            vm.set("classmodel", model);
        });
    },

    /**
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {Number} value
     * @param {Object} eOpts
     */
    onTemplatesComboChange: function (combo, value, eOpts) {
        var me = this,
            vm = combo.lookupViewModel(),
            container = this.getView().child("#ifcCardContainer"),
            fileField = this.getView().down("filefield"),
            containerhidden = true;

        if (value) {
            var selected = combo.getSelectedRecord();
            if (!selected && value.split(':')[1]) {
                selected = combo.getStore().findRecord('_id', value.split(':')[1]);
                combo.select(selected);
            }
            // set selected template to vm
            vm.set("selectedtemplate", selected);

            if (selected && selected.isTemplate) {
                // set accept on inputFile
                fileField.fileInputEl.dom.accept = Ext.String.format('.{0}', selected.get('fileFormat'));

                this.getView().getObject().getAttributes().then(function (attrs) {
                    // set import key attribute label
                    if (selected.get("importKeyAttribute")) {
                        // get field description from model
                        var field = vm.get("classmodel").getField(selected.get("importKeyAttribute"));
                        if (field) {
                            vm.set("labels.importKeyAttribute", field.attributeconf.description_localized);
                        } else {
                            field = Ext.Array.findBy(attrs.getRange(), function (item, index, allitems) {
                                return item.getId() == value;
                            });
                            vm.set("labels.importKeyAttribute", field.get('_description_translation') || field.get('description'));
                        }
                    } else if (selected.get("importKeyAttributes")) {
                        var labelKeys = [];
                        var attributes = selected.get("importKeyAttributes").split(',');
                        attributes.forEach(function (value, key) {
                            var field = vm.get("classmodel").getField(value);
                            if (field) {
                                labelKeys.push(field.attributeconf.description_localized);
                            } else {
                                field = Ext.Array.findBy(attrs.getRange(), function (item, index, allitems) {
                                    return item.getId() == value;
                                });
                                labelKeys.push(field.get('_description_translation') || field.get('description'));
                            }
                        });
                        vm.set("labels.importKeyAttribute", labelKeys.join(', '));
                    }

                    // merge mode
                    if (selected.get("mergeMode")) {
                        var r = Ext.Array.findBy(
                            CMDBuildUI.model.importexports.Template.getMergeModes(),
                            function (item, index) {
                                return item.value === selected.get("mergeMode");
                            });
                        vm.set("labels.mergeMode", r && r.label || "");
                    }

                    me.addTemplateGrid(selected);
                });
            } else if (selected && selected.isGate) {
                this.addGateGrid(selected);
                if (selected.get("_handler_type") === CMDBuildUI.model.importexports.Gate.gateType.ifc) {
                    // set accept on inputFile
                    fileField.fileInputEl.dom.accept = Ext.String.format('.{0}', selected.get('_handler_type'));
                    var config = selected.get("config");

                    containerhidden = false;

                    // empty container
                    container.removeAll();

                    // add reference field
                    container.add({
                        xtype: 'referencefield',
                        fieldLabel: CMDBuildUI.locales.Locales.importexport.ifc.card,
                        displayField: 'Description',
                        allowBlank: !(Ext.isEmpty(selected.get('config').bimserver_project_master_card_key_source) && Ext.isEmpty(selected.get('config').bimserver_project_master_card_key_attr)),
                        valueField: '_id',
                        metadata: {
                            targetType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            targetClass: config.bimserver_project_master_card_target_class
                        },
                        bind: {
                            hidden: '{hidden.ifccard}',
                            value: '{values.ifccard}',
                            disabled: '{disabled.ifccard}'
                        }
                    });
                }
            }
        }
        container.setHidden(containerhidden);
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onImportBtnClick: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var view = this.getView();
        var vm = this.getViewModel();

        // disable fields
        vm.set("disabled.template", true);
        vm.set("disabled.file", true);
        vm.set("disabled.importbtn", true);
        vm.set("disabled.closebtn", true);
        vm.set("disabled.sourcetype", true);
        vm.set("disabled.bimproject", true);
        vm.set("disabled.ifccard", true);

        var selectedtemplate = vm.get("selectedtemplate");

        // prepare form data for file upload
        var formData = new FormData();
        var input = this.lookupReference("filefield").extractFileInput().files[0];
        this.input = input;

        var url, params;
        if (selectedtemplate.isTemplate) {
            url = Ext.String.format("{0}/etl/templates/{1}/import", CMDBuildUI.util.Config.baseUrl, selectedtemplate.get("_id"));
        } else {
            url = Ext.String.format("{0}/../../etl/gate/private/{1}", CMDBuildUI.util.Config.baseUrl, selectedtemplate.get("code"));
            if (selectedtemplate.get("config").tag === CMDBuildUI.model.importexports.Gate.gateType.ifc) {
                if (vm.get("values.sourcetype") === "project") {
                    var project = vm.get("bimprojects").findRecord('_id', vm.get("values.bimproject"));
                    url = url + "?" + Ext.urlEncode({
                        BIMSERVER_SOURCE_PROJECT_ID: project.get("_id")
                    });
                } else {
                    url = url + "?" + Ext.urlEncode({
                        bimserver_project_master_card_id: vm.get("values.ifccard")
                    });
                }
            }
        }
        var lm = CMDBuildUI.util.Utilities.addLoadMask(view);
        CMDBuildUI.util.File.upload("POST", formData, input, url, {
            success: function (responseText, seOpts) {
                var response = Ext.JSON.decode(responseText);

                if (response.data && response.data.report) {
                    var fileName = response.data.report.filename;
                    view.lookupReference("downloadreportbtn").el.set({
                        "download": fileName
                    });

                    vm.set("responsetext", response.data.report.content);
                    vm.set("hidden.sendreportbtn", false);
                    vm.set("hidden.downloadreportbtn", false);
                }
                view.lookupReference("templatedefinition").collapse();
                vm.set("response", response.data || response);
                vm.set("disabled.template", false);
                vm.set("disabled.file", false);
                vm.set("disabled.importbtn", false);
                vm.set("hidden.response", false);
                vm.set("disabled.closebtn", false);
                view.refreshGrid(selectedtemplate.get("code"));

                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(lm);
            },
            failure: function (error, seOpts) {
                vm.set("disabled.template", false);
                vm.set("disabled.file", false);
                vm.set("disabled.sourcetype", false);
                vm.set("disabled.bimproject", false);
                vm.set("disabled.closebtn", false);
                vm.set("disabled.ifccard", false);
                vm.set("disabled.importbtn", false);
                CMDBuildUI.util.Ajax.showMessages({
                    responseText: error
                }, {});

                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(lm);
            }
        });
    },

    onDownloadReportBtnAfterRender: function (btn) {
        btn.el.set({
            "download": "report.txt"
        });
    },

    /**
     *
     * @param {Ext.button.Button} btn
     */
    onDownloadReportBtnClick: function (btn) {
        var vm = btn.lookupViewModel();
        var fileId = vm.get("response.report.content");
        var fileName = vm.get("response.report.filename");
        var fileExtension = fileName.split('.').length > 0 ? fileName.split('.')[1] : this.input.name.split('.').length > 0 ? this.input.name.split('.')[1] : 'xlsx';
        var url = Ext.String.format('{0}/downloads/{1}/{2}', CMDBuildUI.util.Config.baseUrl, fileId, fileName);
        CMDBuildUI.util.File.download(url, fileExtension);
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (btn, eOpts) {
        var view = this.getView();
        if (view.closePopup) {
            view.closePopup();
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onSendReportBtnClick: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            vm = this.getViewModel();
        var url = CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Emails.getCardEmailsUrl("_ANY", "_ANY");

        btn.disable();

        var currentEtl = vm.get('values.template'),
            etlStore = vm.get('templates'),
            etlTemplate = etlStore.getById(currentEtl),
            emailTemplate = etlTemplate.get('notificationTemplate') || "cm_send_to_current_user",
            tempFileId = vm.get('response.report.content'),
            tempFileName = vm.get('response.report.filename');

        // create email
        Ext.Ajax.request({
            method: 'POST',
            url: url,
            jsonData: {
                template: emailTemplate,
                subject: CMDBuildUI.locales.Locales.importexport.emailsubject,
                body: Ext.String.format(
                    CMDBuildUI.locales.Locales.importexport.emailmessage,
                    tempFileName,
                    CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(new Date())
                ),
                status: "outgoing"
            },
            params: {
                apply_template: true,
                attachments: tempFileId
            },
            success: function () {
                delete me.input;
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.importexport.emailsuccess);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            },
            failure: function () {
                btn.enable();
                CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.importexport.emailfailure);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     *
     * @param {Ext.container.Container} panel
     * @param {Object} eOpts
     */
    onBeforePanelRender: function (panel, eOpts) {
        var vm = panel.lookupViewModel(),
            selectedTemplate = vm.get("templates").getById(vm.get("values.template"));
        if (selectedTemplate.isTemplate) {
            var html = '',
                data = panel.getInitialConfig()._rowContext.record.get("record");

            Ext.Array.forEach(selectedTemplate.columns().getRange(), function (item, index, allitems) {
                html += Ext.String.format('<div><strong>{0}</strong>: {1}</div>', item.get("columnName"), data[item.get("attribute")]);
            });

            panel.setHtml(html);
        }
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.importexports.Template} template
         */
        addTemplateGrid: function (template) {
            // get fielset and empty it
            const fieldset = this.lookupReference("templatedefinition");
            fieldset.removeAll();

            // add grid
            fieldset.add({
                xtype: 'grid',
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,

                selModel: {
                    pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                },

                forceFit: true,
                loadMask: true,

                store: template.columns(),

                columns: [{
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                    dataIndex: 'attribute',
                    align: 'left',
                    renderer: function (value, metadata, record, rowindex, colindex, store, view) {
                        var f = view.lookupViewModel().get("classmodel").getField(value);
                        if (f) {
                            return f.attributeconf.description_localized;
                        }
                        return value;
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.columnname,
                    flex: 1,
                    dataIndex: 'columnName',
                    align: 'left'
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                    flex: 1,
                    dataIndex: 'mode',
                    align: 'left'
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.default,
                    flex: 1,
                    dataIndex: 'default',
                    align: 'left'
                }],

                columnWidth: 1,
                autoEl: {
                    'data-testid': 'importexport-import-columns-grid'
                }
            }, {
                xtype: 'fieldcontainer',
                layout: 'column',
                defaults: {
                    xtype: 'displayfield',
                    columnWidth: 0.5,
                    layout: 'anchor',
                    labelAlign: "left"
                },
                items: [{
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattributes,
                    labelWidth: 150,
                    labelSeparator: ':',
                    fieldBodyCls: 'field-with-top-margin',
                    bind: {
                        value: '{labels.importKeyAttribute}'
                    }
                }, {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                    labelWidth: 150,
                    labelSeparator: ':',
                    fieldBodyCls: 'field-with-top-margin',
                    bind: {
                        value: '{labels.mergeMode}'
                    }
                }]
            });
        },

        /**
         *
         * @param {CMDBuildUI.model.importexports.Gate} gate
         */
        addGateGrid: function (gate) {
            // get fielset and empty it
            const fieldset = this.lookupReference("templatedefinition");
            let sourcelabel;
            fieldset.removeAll();
            switch (gate.get("_handler_type")) {
                case CMDBuildUI.model.importexports.Gate.gateType.database:
                    sourcelabel = CMDBuildUI.locales.Locales.administration.importexport.texts.tablename;
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.ifc:
                    sourcelabel = CMDBuildUI.locales.Locales.administration.importexport.texts.filepath;
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.gis:
                case CMDBuildUI.model.importexports.Gate.gateType.cad:
                    sourcelabel = CMDBuildUI.locales.Locales.administration.gates.sourcelayer;
                    break;
            }

            // get templates info
            const data = [];
            const modelsToLoad = [];
            gate.get("_templates").forEach(function (tpl, i) {
                const k = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(tpl.targetName);
                if (k) {
                    const mergetmode = Ext.Array.findBy(
                        CMDBuildUI.model.importexports.Template.getMergeModes(),
                        function (item, index) {
                            return item.value === tpl.mergeMode;
                        }
                    );
                    const desc = Ext.String.format(
                        "<strong>{0}</strong>: {1} &mdash; <strong>{2}</strong>: {3} &mdash; <strong>{4}</strong>: {5} &mdash; <strong>{6}</strong>: {7}",
                        CMDBuildUI.locales.Locales.administration.localizations.class,
                        k.getTranslatedDescription(),
                        sourcelabel,
                        tpl.source,
                        CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattribute,
                        tpl.importKeyAttributes.join(", "),
                        CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                        mergetmode && mergetmode.label || null
                    );
                    tpl.columns.forEach(function (col) {
                        col.targetName = tpl.targetName;
                        col.targetType = tpl.targetType;
                        col._tpl = Ext.String.leftPad(i, 5, '0') + tpl.code;
                        col._tpl_description = desc;
                        data.push(col);
                    });

                    if (!Ext.ClassManager.isCreated(CMDBuildUI.util.helper.ModelHelper.getModelName(tpl.targetType, tpl.targetName))) {
                        modelsToLoad.push(CMDBuildUI.util.helper.ModelHelper.getModel(tpl.targetType, tpl.targetName));
                    }
                }
            });

            // gate configuration
            let gateinfo, colproptext;
            const handler = gate.get("_handler_config");
            switch (handler.type) {
                case CMDBuildUI.model.importexports.Gate.gateType.cad:
                case CMDBuildUI.model.importexports.Gate.gateType.gis:
                    colproptext = CMDBuildUI.locales.Locales.administration.gates.dwgproperty;

                    gateinfo = Ext.String.format(
                        "<p><em>{0}</em>: <strong>{1}</strong>: {2} &mdash; <strong>{3}</strong>: {4} &mdash; <strong>{5}</strong>: {6}</p>",
                        CMDBuildUI.locales.Locales.importexport.gis.importconfiguration,
                        CMDBuildUI.locales.Locales.administration.localizations.class,
                        handler.shape_import_target_class,
                        CMDBuildUI.locales.Locales.administration.gates.importkeyattributes,
                        handler.shape_import_key_attr.replaceAll(",", ", "),
                        CMDBuildUI.locales.Locales.administration.gates.importkeysource,
                        handler.shape_import_key_source
                    );

                    if (handler.shape_import_enabled === true || handler.shape_import_enabled === "true") {
                        gateinfo += Ext.String.format("<p><em>{0}</em></p>", CMDBuildUI.locales.Locales.importexport.gis.shapeimportenabled);
                    } else {
                        gateinfo += Ext.String.format("<p><em>{0}</em></p>", CMDBuildUI.locales.Locales.importexport.gis.shapeimportdisabled);
                    }
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.database:
                    colproptext = CMDBuildUI.locales.Locales.administration.importexport.texts.columnname;
                    const config = gate.get("config");
                    gateinfo = Ext.String.format(
                        "<strong>{0}</strong>: {1}  &mdash; <strong>{2}</strong>: {3}",
                        CMDBuildUI.locales.Locales.importexport.database.uri,
                        config.jdbcUrl,
                        CMDBuildUI.locales.Locales.importexport.database.user,
                        config.jdbcUsername
                    )
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.ifc:
                    colproptext = CMDBuildUI.locales.Locales.administration.gates.ifcproperty;
                    break;
            }

            Ext.Promise.all(modelsToLoad).then(function () {
                // add grid
                fieldset.add({
                    xtype: 'container',
                    html: gateinfo
                }, {
                    xtype: 'grid',
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,

                    ui: 'cmdbuildgrouping',

                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },

                    forceFit: true,
                    loadMask: true,

                    features: [{
                        ftype: 'grouping',
                        groupHeaderTpl: '{[values.rows[0].data._tpl_description]}'
                    }],

                    // store: template.columns(),
                    store: {
                        data: data,
                        groupField: '_tpl'
                    },

                    columns: [{
                        flex: 1,
                        text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.attributes.attribute'
                        },
                        dataIndex: 'attribute',
                        align: 'left',
                        renderer: function (value, metadata, record, rowindex, colindex, store, view) {
                            let f;
                            if (record.get("targetName") && record.get("targetType")) {
                                const model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(
                                    CMDBuildUI.util.helper.ModelHelper.getModelName(record.get("targetType"), record.get("targetName"))
                                );
                                f = model.getField(value);
                            } else {
                                f = view.lookupViewModel().get("classmodel").getField(value);
                            }

                            if (f) {
                                return f.attributeconf.description_localized;
                            }
                            return value;
                        }
                    }, {
                        text: colproptext,
                        flex: 1,
                        dataIndex: 'columnName',
                        align: 'left'
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                        flex: 1,
                        dataIndex: 'mode',
                        align: 'left'
                    }],

                    columnWidth: 1,
                    autoEl: {
                        'data-testid': 'importexport-import-columns-grid'
                    }
                });
            });
        }
    }
});