/**
 * @file CMDBuildUI.view.widgets.attachmentwidget
 * @module CMDBuildUI.view.widgets.attachmentwidget
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.attachmentwidget.Panel', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.widgets.attachmentwidget.PanelController'],

    statics: {
        /**
         * Function executed before save/execute action
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @return {Ext.promise.Promise}
         */
        beforeTargetSave: function (target, widget, config) {
            if (config.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.update) {
                return this.saveAttachments(target, widget, config);
            } else {
                var deferred = new Ext.Deferred();
                deferred.resolve(true);
                return deferred.promise;
            }
        },

        /**
         * Function executed after save/execute action
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @return {Ext.promise.Promise}
         */
        afterTargetSave: function (target, widget, config) {
            if (config.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                return this.saveAttachments(target, widget, config);
            } else {
                var deferred = new Ext.Deferred();
                deferred.resolve(true);
                return deferred.promise;
            }
        },

        saveAttachments: function (target, widget, config) {
            var deferred = new Ext.Deferred();

            // TODO: save attachments
            var store = widget.get('_attachmentsStore');
            if (store) {
                CMDBuildUI.util.helper.FormHelper.startSavingForm();
                var url = Ext.String.format('{0}/{1}/attachments', target.getProxy().getUrl(), target.getId()),
                    deletePromises = [],
                    savePromises = [];

                /**
                 *
                 * @param {Object} metadata
                 * @return {Object} metadata without attributes which starts whit "_"
                 */
                function clearMetadata(metadata) {
                    for (key in metadata) {
                        if (Ext.String.startsWith(key, '_')) {
                            delete metadata[key];
                        }
                    }
                    return metadata;
                }

                /**
                 *
                 * @param {CMDBuildUI.model.dms.Attachment} record
                 *
                 */
                function deleteAttachment(record) {
                    var deferred = new Ext.Deferred();
                    if (!store.getById(record.getId())) {
                        record.getProxy().setUrl(url);
                        record.erase({
                            success: function () {
                                deferred.resolve(true);
                            },
                            failure: function () {
                                deferred.reject();
                            }
                        });
                    } else {
                        deferred.resolve(true);
                    }
                    return deferred;
                }

                /**
                 * Used to resolve the deferred and end saving form status
                 */
                function resolveDeferred() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.resolve(true);
                }

                // delete removed records
                store.getRemovedRecords().forEach(function (record) {
                    deletePromises.push(deleteAttachment(record));
                });

                // add new attachments and edit modified attachments
                store.getRange().forEach(function (record) {
                    record.getProxy().setUrl(url);
                    var file;
                    if (record.get('_filedata')) {
                        file = record.get('_filedata').get('file');
                    }
                    savePromises.push(
                        record.saveAttachmentAndSequences(file, {
                            type: target.isCard
                                ? CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                                : CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                            typeName: target.get('_type'),
                            id: target.get('_id')
                        })
                    );
                });

                Ext.Promise.resolve()
                    .then(function () {
                        if (deletePromises.length) {
                            Ext.Promise.all(deletePromises).then(
                                function (responses) {},
                                function (err) {
                                    CMDBuildUI.util.Ajax.showMessages(
                                        {
                                            responseText: err
                                        },
                                        {
                                            hideErrorNotification: false
                                        }
                                    );
                                }
                            );
                        }
                    })
                    .then(function () {
                        if (savePromises.length) {
                            Ext.Promise.all(savePromises).then(
                                function (responses) {
                                    resolveDeferred();
                                },
                                function (err) {
                                    CMDBuildUI.util.Ajax.showMessages(
                                        {
                                            responseText: err
                                        },
                                        {
                                            hideErrorNotification: false
                                        }
                                    );

                                    resolveDeferred();
                                }
                            );
                        } else {
                            resolveDeferred();
                        }
                    });
            } else {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                deferred.resolve(true);
            }

            return deferred;
        },

        /**
         * Function executed before cancel action
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         *
         * @return {Ext.promise.Promise}
         */
        onTargetCancel: function (target, widget, config) {
            var me = this,
                deferred = new Ext.Deferred(),
                store = widget.get('_attachmentsStore');
            // use filterDataSource(store.filterNewOnly) to get new filters because
            // getNewRecords get only valid records and attachments can be not valid for current model
            if (
                store &&
                (store.filterDataSource(store.filterNewOnly).length > 0 ||
                    store.getUpdatedRecords().length > 0 ||
                    store.getRemovedRecords().length > 0)
            ) {
                // dialog definition
                var dialog = CMDBuildUI.util.Msg.openDialog(CMDBuildUI.locales.Locales.notifier.attention, {
                    closable: false,
                    resizable: false,
                    items: [
                        {
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            html: Ext.String.format(
                                '<p>{0}</p>{1}',
                                CMDBuildUI.locales.Locales.widgets.attachment.savebeforeexit,
                                target.phantom
                                    ? Ext.String.format(
                                          '<p>{0}</p>',
                                          CMDBuildUI.locales.Locales.widgets.attachment.saveprocessmessage
                                      )
                                    : ''
                            )
                        }
                    ],
                    buttons: [
                        {
                            xtype: 'button',
                            itemId: 'exitWithoutSave',
                            text: CMDBuildUI.locales.Locales.widgets.attachment.actions.exitwithoutsave,
                            ui: 'secondary-action-small',
                            listeners: {
                                click: function () {
                                    // close dialog and exit process
                                    dialog.destroy();
                                    deferred.resolve();
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'stayInEdit',
                            text: CMDBuildUI.locales.Locales.widgets.attachment.actions.stayinedit,
                            ui: 'secondary-action-small',
                            listeners: {
                                click: function () {
                                    // close dialog and stay in process panel
                                    dialog.destroy();
                                    deferred.resolve(false);
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            ui: 'management-primary-small',
                            text: CMDBuildUI.locales.Locales.common.actions.save,
                            listeners: {
                                click: function (button, e, eOpts) {
                                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                                    var exitWithoutSave = dialog.down('#exitWithoutSave'),
                                        stayInEdit = dialog.down('#stayInEdit');
                                    button.showSpinner = true;
                                    CMDBuildUI.util.Utilities.disableFormButtons([button, exitWithoutSave, stayInEdit]);

                                    if (target.phantom) {
                                        target.set('_advance', false);
                                        target.save({
                                            callback: function (record, operation, success) {
                                                if (success) {
                                                    config.action = 'save';
                                                    me.saveAttachments(target, widget, config).then(function () {
                                                        dialog.destroy();
                                                        deferred.resolve();
                                                    });
                                                } else {
                                                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                                    dialog.destroy();
                                                    deferred.resolve(false);
                                                }
                                            }
                                        });
                                    } else {
                                        me.saveAttachments(target, widget, config).then(function () {
                                            dialog.destroy();
                                            deferred.resolve();
                                        });
                                    }
                                }
                            }
                        }
                    ]
                });
            } else {
                deferred.resolve();
            }
            return deferred.promise;
        }
    },

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-attachmentwidget-panel',
    controller: 'widgets-attachmentwidget-panel',
    viewModel: {
        data: {
            editmode: false
        }
    },

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,

    layout: 'fit',

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            hidden: true,
            ui: 'footer',
            enableFocusableContainer: false,
            bind: {
                hidden: '{theWidget._inline}'
            },
            items: [
                {
                    xtype: 'component',
                    flex: 1
                },
                {
                    ui: 'secondary-action-small',
                    itemId: 'closebtn',
                    hidden: true,
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    bind: {
                        hidden: '{editmode}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    autoEl: {
                        'data-testid': 'widgets-attachmentwidget-close'
                    }
                }
            ]
        }
    ]
});
