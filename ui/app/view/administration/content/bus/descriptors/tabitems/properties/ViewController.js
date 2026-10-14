Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-descriptors-tabitems-properties-view',

    require: ['CMDBuildUI.util.administration.helper.FormHelper'],

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndEditBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#downloadBtn': {
            click: 'onDownloadBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
                    var theDescriptor = vm.get('theDescriptor');
                    CMDBuildUI.util.Ajax.setActionId('delete-busdescriptor');
                    theDescriptor.erase({
                        failure: function (error) {
                            theDescriptor.reject();
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(
                                theDescriptor.get('_id')
                            );
                            me.redirectTo(nextUrl, true);
                        },
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                'href',
                                CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(
                                    record.get('_id')
                                ),
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
                } else {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            },
            this
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [
            true,
            null,
            CMDBuildUI.locales.Locales.administration.common.messages.saving
        ]);
        var me = this;
        var view = me.getView();
        var vm = view.lookupViewModel();
        var theDescriptor = me.applyParams(vm, vm.get('theDescriptor'));
        theDescriptor.set('enabled', !theDescriptor.get('enabled'));
        theDescriptor.save({
            callback: function (record) {
                CMDBuildUI.util.administration.Utilities.showToggleActiveMessage(record);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this;
        button.setDisabled(true);
        var vm = this.getViewModel();
        if (vm.get('theDescriptor').isValid()) {
            var method = vm.get('theDescriptor').phantom ? 'POST' : 'PUT',
                code = !vm.get('theDescriptor').phantom ? '/' + vm.get('theDescriptor.code') : '',
                theDescriptor = me.applyParams(vm, vm.get('theDescriptor')),
                file = this.getView().down('filefield').extractFileInput().files[0];

            if (file) {
                vm.get('theDescriptor').set('data', '');
            }
            CMDBuildUI.util.File.uploadFileWithMetadata(
                method,
                vm.get('theDescriptor').getProxy().getUrl() + code,
                file,
                theDescriptor.getData(),
                {
                    filePartName: 'file',
                    metadataPartName: 'data'
                }
            ).then(
                function (response) {
                    // execute after action triggers
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(
                        response._id
                    );
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectNode('href', nextUrl, me);
                        if (button.el && button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                        if (!button || button.itemId !== 'saveAndEditBtn') {
                            me.redirectTo(nextUrl, true);
                        } else {
                            new Ext.util.DelayedTask(function () {
                                var newVm = CMDBuildUI.util.Navigation.getMainAdministrationContainer()
                                    .down('administration-content-bus-descriptors-view')
                                    .getViewModel();
                                newVm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                            }).delay(250);
                        }
                    });
                },
                function (err) {
                    Ext.asap(function () {
                        if (button.el && button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                    });
                },
                Ext.emptyFn,
                this
            );
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var me = this,
            vm = me.getViewModel(),
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(
                vm.get('actions.edit') ? vm.get('theDescriptor._id') : null
            );

        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var fileName = Ext.String.format('{0}.yaml', vm.get('theDescriptor.code'));
        var blob = new Blob([vm.get('theDescriptor').get('data')], {
            type: 'text/yaml'
        });
        if (window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveBlob(blob, fileName);
        } else {
            var elem = window.document.createElement('a');
            elem.href = window.URL.createObjectURL(blob);
            elem.download = fileName;
            document.body.appendChild(elem);
            elem.click();
            window.URL.revokeObjectURL(elem.href);
            document.body.removeChild(elem);
        }
    },

    onAceEditorHelpExpand: function () {
        var vm = this.getViewModel(),
            property = 'data',
            theDescriptor = vm.get('theDescriptor'),
            value = theDescriptor.get(property),
            elementId = Ext.String.format('{0}_{1}', new Date().getMilliseconds(), property),
            popupId = Ext.String.format('popup-busdescriptor-{0}', elementId),
            mode = 'ace/mode/yaml',
            panelTitle = CMDBuildUI.locales.Locales.administration.bus.configuration,
            editor;

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
                                        theDescriptor.set(property, value);
                                        popUp.fireEvent('close');
                                    }
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                                ui: 'administration-action-small',
                                listeners: {
                                    click: function (_button) {
                                        vm.set(Ext.String.format('theDescriptor.{0}', property), editor.getValue());
                                        var mainInput =
                                            CMDBuildUI.util.Navigation.getMainAdministrationContainer().down(
                                                '#administration-content-bus-descriptors-configuration-editor'
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

                                        editor.getSession().setMode(mode);
                                        editor.getSession().setUseWrapMode(true);
                                        editor.setValue(value, -1);

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
                                            theDescriptor.set(property, doc.getValue());
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
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.view.administration.content.classes.ViewModel} vm
         * @param {CMDBuildUI.model.administration.BusDescriptor} theDescriptor
         *
         * @return {CMDBuildUI.model.administration.BusDescriptor}
         */
        applyParams: function (vm, theDescriptor) {
            var params = {};
            vm.get('paramsStore').each(function (param) {
                params[param.get('key')] = param.get('value');
            });
            theDescriptor.set('params', params);
            return theDescriptor;
        }
    }
});
