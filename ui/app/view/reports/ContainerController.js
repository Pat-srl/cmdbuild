Ext.define('CMDBuildUI.view.reports.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.reports-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#refreshbtn': {
            click: 'onRefreshBtnClick'
        },
        '#downloadbtn': {
            click: 'onDownloadBtnClick'
        }
    },

    /**************************************************************************************************************
     *
     *                                                  REPORTS
     *
     * EVENTS:
     *  onBeforeRender              (view, eOpts)                           --> render view with selected object
     *
     * METHODS:
     *  addRelationAttibutes        (attributes, ext, title, reportId)      --> add attributes for the current report
     *
     **************************************************************************************************************/

    /**
     * @param {CMDBuildUI.view.reports.ContainerController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel();

        if (!view.hideTitle) {
            view.setTitle({
                xtype: 'management-title',
                bind: {
                    text: '{title}',
                    objectTypeName: '{objectTypeName}',
                    menuType: '{menuType}'
                }
            });
        }

        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get('objectType'), vm.get('objectTypeName')).then(
            function (model) {
                vm.set('objectModel', model);
                vm.bind(
                    {
                        bindTo: {
                            report: '{theReport}'
                        }
                    },
                    function (data) {
                        if (data.report) {
                            data.report.getAttributes().then(function (attributes) {
                                me.addRelationAttibutes(attributes.getRange(), data.report.getId());
                            });
                        }
                    }
                );
            }
        );
    },

    /**
     * add attributes for the current report
     * @param {Object} attributes
     * @param {numeric} reportId
     */
    addRelationAttibutes: function (attributes, reportId) {
        var vm = this.getViewModel();
        var extension = vm.get('extension');

        if (Ext.isEmpty(attributes) && !Ext.isEmpty(extension)) {
            // open report
            this.reportViewer(reportId, extension, null);
            return;
        }

        var form = this.getFormConfig(reportId, attributes, extension);
        if (form) {
            CMDBuildUI.util.Utilities.openPopup(
                this.parameterspopupid,
                vm.get('title'),
                form,
                {},
                {
                    width: '50%',
                    height: '50%'
                }
            );
        }
    },

    /**
     *
     * @param {Number} reportId
     * @param {String} extension One of `CMDBuildUI.model.reports.Report.extensions` items
     * @param {Object} parameters
     */
    reportViewer: function (reportId, extension, parameters) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            queryparams = {
                extension: extension
            };

        if (!Ext.Object.isEmpty(parameters)) {
            queryparams.parameters = Ext.encode(parameters);
        }

        if (vm.get('theReport.config___processing') === CMDBuildUI.model.reports.Report.processingmodes.batch) {
            var msgbox = view.down('#messagesbox'),
                executionUrl = Ext.String.format(
                    '{0}{1}?{2}',
                    CMDBuildUI.util.Config.baseUrl,
                    CMDBuildUI.util.api.Reports.getReportExecutionUrl(reportId),
                    Ext.Object.toQueryString(queryparams)
                );

            Ext.Ajax.request({
                url: executionUrl,
                method: 'POST'
            }).then(
                function (response, opts) {
                    msgbox.add({
                        xtype: 'container',
                        margin: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        cls: Ext.baseCSSPrefix + 'container-messageinfo',
                        html: CMDBuildUI.locales.Locales.reports.msgs.creationstarted
                    });
                },
                function (response, opts) {
                    msgbox.add({
                        xtype: 'container',
                        margin: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        cls: Ext.baseCSSPrefix + 'container-messagewarning',
                        html: CMDBuildUI.locales.Locales.reports.msgs.creationerror
                    });
                }
            );
            vm.set('refreshbtn.disabled', true);
        } else {
            var downloadUrl = Ext.String.format(
                    '{0}{1}?{2}',
                    CMDBuildUI.util.Config.baseUrl,
                    CMDBuildUI.util.api.Reports.getReportDownloadUrl(reportId, extension),
                    Ext.Object.toQueryString(queryparams)
                ),
                viewUrl;

            if (extension == CMDBuildUI.model.reports.Report.extensions.csv) {
                queryparams._contenttype = 'text/plain';
            }

            viewUrl = Ext.String.format(
                '{0}{1}?{2}',
                CMDBuildUI.util.Config.baseUrl,
                CMDBuildUI.util.api.Reports.getReportDownloadUrl(reportId, extension),
                Ext.Object.toQueryString(queryparams)
            );

            // IE 11 Visualization problem
            if (this.useObjectInsteadOfIframe()) {
                /* Microsoft Internet Explorer detected in. */
                view.lookupReference('reportiframe').hide();
                var ct;
                switch (extension) {
                    case CMDBuildUI.model.reports.Report.extensions.csv:
                        ct = 'text/plain';
                        break;
                    case CMDBuildUI.model.reports.Report.extensions.pdf:
                        ct = 'application/pdf';
                        break;
                    case CMDBuildUI.model.reports.Report.extensions.odt:
                        ct = 'application/vnd.oasis.opendocument.text';
                        break;
                    case CMDBuildUI.model.reports.Report.extensions.rtf:
                        ct = 'application/rtf';
                        break;
                }
                var obj = Ext.String.format(
                    '<object style="width:100%;height:100%" data="{0}" type="{1}"> <embed width="100%" height="100%" src="{0}" type="{1}" /></object></div>',
                    viewUrl,
                    ct
                );
                view.setHtml(obj);
            } else {
                // load report
                view.lookupReference('reportiframe').load(viewUrl);
            }
            // save url on view model to enable download button and enable refresh button
            vm.set('downloadbtn.href', downloadUrl + '&_download=true');
            vm.set('refreshbtn.disabled', false);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onRefreshBtnClick: function (button, eOpts) {
        var me = this;
        var report = button.lookupViewModel().get('theReport');
        if (this.useObjectInsteadOfIframe()) {
            this.getView().setHtml('');
        }
        report.getAttributes().then(function (attributes) {
            me.addRelationAttibutes(attributes.getRange(), report.getId());
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, eOpts) {
        var url = button.lookupViewModel().get('downloadbtn.href');
        CMDBuildUI.util.File.download(url, undefined, undefined, undefined, {
            skipUrlEncode: true,
            applyMaskTo: this.getView().up('#CMDBuildManagementContent')
        });
    },

    privates: {
        /**
         * @property {String} parameterspopupid
         * The id used for the popup.
         */
        parameterspopupid: 'report-parameters-popup',

        /**
         * @property {String} reportformatfield
         * The field name used for the extension combo.
         */
        reportformatfield: '_reportformatfield',

        /**
         *
         * @param {CMDBuildUI.model.Attribute[]} attributes
         * @param {String} extension
         * @return {Ext.form.Field[]} Form fields
         */
        getFormFields: function (attributes, extension) {
            var vm = this.getViewModel();
            var defaults = vm.get('defaults') || {};
            var allreadonlyfields = true;

            var attributesOverrides = {};
            var defaultValues = [];

            for (var attrname in defaults) {
                var attrdef = defaults[attrname];
                if (attrdef.value !== undefined) {
                    defaultValues.push({
                        attribute: attrname,
                        value: attrdef.value
                    });
                }
                if (attrdef.editable === false || attrdef.editable === 'false') {
                    attributesOverrides[attrname] = { writable: false };
                }
            }

            var fields = CMDBuildUI.util.helper.FormHelper.getFormFields(vm.get('objectModel'), {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
                linkName: 'theObject',
                defaultValues: defaultValues,
                attributesOverrides: attributesOverrides
            });
            if (
                Ext.Array.findBy(fields, function (i) {
                    return i.xtype !== 'displayfield' && i.xtype !== 'displayfieldwithtriggers';
                })
            ) {
                allreadonlyfields = false;
            }

            // add extension field if extension is empty
            if (Ext.isEmpty(extension)) {
                Ext.Array.insert(fields, 0, [
                    {
                        anchor: '100%',
                        xtype: 'combobox',
                        fieldLabel: CMDBuildUI.locales.Locales.reports.format,
                        name: this.reportformatfield,
                        id: this.reportformatfield,
                        store: {
                            data: [
                                {
                                    value: CMDBuildUI.model.reports.Report.extensions.csv,
                                    label: CMDBuildUI.locales.Locales.reports.csv
                                },
                                {
                                    value: CMDBuildUI.model.reports.Report.extensions.odt,
                                    label: CMDBuildUI.locales.Locales.reports.odt
                                },
                                {
                                    value: CMDBuildUI.model.reports.Report.extensions.pdf,
                                    label: CMDBuildUI.locales.Locales.reports.pdf
                                },
                                {
                                    value: CMDBuildUI.model.reports.Report.extensions.rtf,
                                    label: CMDBuildUI.locales.Locales.reports.rtf
                                }
                            ]
                        },
                        forceSelection: true,
                        queryMode: 'local',
                        displayField: 'label',
                        valueField: 'value',
                        allowBlank: false
                    }
                ]);
                allreadonlyfields = false;
            }

            if (allreadonlyfields) {
                return false;
            }
            return fields;
        },

        /**
         *
         * @param {Number} reportId
         * @param {CMDBuildUI.model.Attribute[]} attributes
         * @param {String} extension
         * @return {Object}
         */
        getFormConfig: function (reportId, attributes, extension) {
            var me = this;
            var vm = this.getViewModel();
            var fields = this.getFormFields(attributes, extension);

            // if all fields are read-only show report without asking parameters
            if (fields === false) {
                var defaults = this.getViewModel().get('defaults') || {};
                var defvalues = {};
                for (var k in defaults) {
                    defvalues[k] = defaults[k].value;
                }
                me.reportViewer(reportId, extension, defvalues);
                return;
            }
            var data = {};
            fields.forEach(function (f) {
                var value = null;
                if (
                    f.metadata &&
                    f.metadata.type.toLowerCase() ===
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase()
                ) {
                    value = false;
                }
                data[f.name] = value;
            });

            return {
                xtype: 'form',
                bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                scrollable: true,
                viewModel: {
                    links: {
                        theObject: {
                            type: vm.get('objectModel').getName(),
                            create: true
                        }
                    }
                },
                items: fields,
                buttons: [
                    {
                        xtype: 'button',
                        ui: 'secondary-action',
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        handler: function (button) {
                            CMDBuildUI.util.Utilities.closePopup(me.parameterspopupid);
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'management-primary',
                        text: CMDBuildUI.locales.Locales.reports.print,
                        formBind: true,
                        handler: function (button) {
                            var form = button.up('form');
                            var params = {};
                            var theObject = form.lookupViewModel().get('theObject');
                            form.getForm()
                                .getFields()
                                .getRange()
                                .forEach(function (field) {
                                    if (field.getName() === me.reportformatfield) {
                                        extension = field.getValue();
                                    } else {
                                        if (!field.isFieldContainer) {
                                            params[field.metadata.name] =
                                                theObject.get(field.getName()) !== undefined
                                                    ? theObject.get(field.getName())
                                                    : '';
                                        }
                                    }
                                });
                            me.reportViewer(reportId, extension, params);
                            CMDBuildUI.util.Utilities.closePopup(me.parameterspopupid);
                        }
                    }
                ]
            };
        },

        /**
         * @return {Boolean}
         * Return `true` if must be used `object` instead of `iframe`.
         */
        useObjectInsteadOfIframe: function () {
            return (
                navigator.userAgent.indexOf('MSIE') !== -1 ||
                navigator.appVersion.indexOf('Trident/') > -1 ||
                Ext.browser.name === 'Safari'
            );
        }
    }
});
