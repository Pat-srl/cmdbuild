Ext.define('CMDBuildUI.view.administration.content.reports.ViewController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.File'],
    alias: 'controller.administration-content-reports-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },

        '#sqlBtn': {
            click: 'onSqlBtnClick'
        },
        '#downloadBtn': {
            click: 'onDownloadBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.reports.View} view
     */
    onBeforeRender: function (view) {
        view.up('administration-content')
            .getViewModel()
            .set('title', CMDBuildUI.locales.Locales.administration.reports.plural);
        this.setFilefieldProperties();
    },

    /**
     * On delete report button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === 'yes') {
                    CMDBuildUI.util.Ajax.setActionId('delete-report');
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
                    me.getViewModel()
                        .get('theReport')
                        .erase({
                            success: function (record, operation) {
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getReportUrl();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                    'href',
                                    Ext.util.History.getToken(),
                                    nextUrl,
                                    me
                                );
                            },
                            callback: function (record, reason) {
                                if (button.el.dom) {
                                    button.setDisabled(false);
                                }
                                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                            }
                        });
                }
            },
            this
        );
    },

    /**
     * On download report button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        var url = Ext.String.format(
            '{0}/reports/{1}/file?extension=ZIP',
            CMDBuildUI.util.Config.baseUrl,
            vm.get('theReport._id')
        );
        CMDBuildUI.util.File.download(url, 'zip');
    },

    /**
     * On edit report button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(0);
        this.setFilefieldProperties();
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel();
        //view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(0);

        if (vm.get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode('objecttype', CMDBuildUI.model.administration.MenuItem.types.report);
            vmNavigation.set('selected', currentNode);
            this.redirectTo('administration/reports', true);
        } else {
            vm.get('theReport').reject();
            //  view.up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(0);
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            this.redirectTo(Ext.String.format('administration/reports/{0}', vm.get('theReport._id')), true);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theReport = vm.get('theReport');
        theReport.set('active', !theReport.get('active'));
        this.save(vm);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();

        if (form.isValid()) {
            const theReport = vm.get('theReport');
            if (theReport.get('icon_font')) {
                theReport.set(
                    'icon_font',
                    CMDBuildUI.util.helper.IconHelper.setIconDBValue(theReport.get('icon_font'))
                );
            }
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
            var afterSave = function (record) {
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getReportUrl(record.get('_id'));
                me.getView().up('administration-content-reports-tabpanel').getViewModel().toggleEnableTabs(0);
                if (vm.get('actions.edit')) {
                    var newDescription = record.get('description');
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                } else {
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                            'href',
                            nextUrl,
                            me
                        );
                        if (button.el && button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                    });
                }
            };
            me.save(
                vm,
                function (res) {
                    var record = res;
                    if (!record.isModel) {
                        record = CMDBuildUI.model.reports.Report.create(record.data);
                    }
                    if (vm.get('theTranslation')) {
                        var key =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfReportDescription(
                                record.get('code')
                            );
                        vm.get('theTranslation').crudState = 'U';
                        vm.get('theTranslation').crudStateWas = 'U';
                        vm.get('theTranslation').phantom = false;
                        vm.get('theTranslation').set('_id', key);
                        vm.get('theTranslation').save({
                            success: function (translation, operation) {
                                afterSave(record);
                            }
                        });
                    } else {
                        afterSave(record);
                    }
                },
                function () {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                }
            );
        }
    },

    /**
     * On show sql button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSqlBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var theQuery = vm.get('theReport.query');
        var content = {
            xtype: 'component',
            html: '<div id="reportSql"></div>',
            listeners: {
                afterrender: function (cmp) {
                    var editor = ace.edit('reportSql', {
                        //set autoscroll
                        autoScrollEditorIntoView: true,
                        maxLines: 90, // this will be changed on "change"
                        // set theme
                        theme: 'ace/theme/chrome',
                        // show line numbers
                        showLineNumbers: true,
                        // hide print margin
                        showPrintMargin: false
                    });
                    cmp.mon(cmp, 'resize', function () {
                        editor.renderer.$updateSizeAsync();
                    });
                    editor.getSession().setMode('ace/mode/pgsql');
                    editor.getSession().setUseWrapMode(true);
                    editor.setValue(theQuery, -1);
                    var heightUpdateFunction = function () {
                        var editorDiv = document.getElementById('reportSql'); // its container
                        var doc = editor.getSession().getDocument(); // a reference to the doc
                        var lineHeight = editor.renderer.lineHeight;
                        editorDiv.style.height = lineHeight * doc.getLength() + 'px'; // set new container height
                        editorDiv.style.width = '100%'; // force container width to 100%
                        editor.setOption('maxLines', Math.ceil((cmp.up().getHeight() - 40) / lineHeight));
                        // its inner structure for adapting to a change in size
                        editor.resize();
                    };

                    // Set initial size to match initial content
                    Ext.asap(function () {
                        heightUpdateFunction();
                    });

                    // Whenever a change happens inside the ACE editor, update
                    // the size again
                    editor.getSession().on('change', heightUpdateFunction);
                    editor.setReadOnly(true);
                }
            }
        };
        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('popup-report-query');
            }
        };
        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-report-query',
            CMDBuildUI.locales.Locales.administration.reports.titles.reportsql,
            content,
            listeners,
            {
                ui: 'administration-actionpanel'
            }
        );
    },

    /**
     * On translate button click (button, e, eOpts) {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfReportDescription(
            !isNaN(vm.get('theReport').get('_id')) ? vm.get('theReport').get('code') : '..'
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theTranslation',
            vm
        );
    },

    /**
     * privates
     */
    privates: {
        /**
         * @private
         */
        setFilefieldProperties: function () {
            var view = this.getView();
            var vm = this.getViewModel();
            if (vm.get('actions.edit')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                    view.down('[name="fileReport"]'),
                    true,
                    this.getView()
                );
            } else if (vm.get('actions.add')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                    view.down('[name="fileReport"]'),
                    false,
                    this.getView()
                );
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.reports.ViewModel} vm
         * @param {Function} successCb
         * @param {Function} errorCb
         */
        save: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('report.upload');
            // define method
            var method = vm.get('actions.add') ? 'POST' : 'PUT';

            var input = this.getView().down('[name="fileReport"]').extractFileInput();

            // init formData
            var formData = new FormData();

            // append attachment json data
            var theReport = vm.get('theReport').getData();
            theReport.config.processing = theReport.config___processing;
            var jsonData = Ext.encode(theReport);
            var fieldName = 'data';
            try {
                formData.append(
                    fieldName,
                    new Blob([jsonData], {
                        type: 'application/json'
                    })
                );
            } catch (err) {
                CMDBuildUI.util.Logger.log(
                    "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " +
                        err,
                    CMDBuildUI.util.Logger.levels.error
                );
                // metadata as 'text/plain' (format compatible with older webviews)
                formData.append(fieldName, jsonData);
            }

            // get url
            var reportsUrl = Ext.String.format('{0}/reports', CMDBuildUI.util.Config.baseUrl);
            var url = vm.get('actions.add')
                ? reportsUrl
                : Ext.String.format('{0}/{1}', reportsUrl, vm.get('theReport._id'));
            // upload
            CMDBuildUI.util.Ajax.initRequestException();
            CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                success: function (response) {
                    if (typeof response === 'string') {
                        response = Ext.JSON.decode(response);
                    }
                    if (Ext.isFunction(successCb)) {
                        Ext.callback(successCb, null, [response]);
                    }
                },
                failure: function (error) {
                    if (typeof response === 'string') {
                        error = Ext.JSON.decode(error);
                    }
                    if (Ext.isFunction(errorCb)) {
                        Ext.callback(errorCb, null, [error]);
                    }
                }
            });
        }
    }
});
