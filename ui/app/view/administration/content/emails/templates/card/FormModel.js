Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-card-form',
    data: {
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        showOnClassesViewData: null
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_email_modify}',
                canWrite: '{theTemplate._can_write}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify);
                this.set('toolAction._canClone', data.canModify);
                this.set('toolAction._canUpdate', data.canModify && data.canWrite);
                this.set('toolAction._canDelete', data.canModify && data.canWrite);
                this.set('toolAction._canActiveToggle', data.canModify && data.canWrite);
            }
        },
        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        delaylistdata: {
            get: function () {
                return CMDBuildUI.model.emails.Email.getDelays(true);
            }
        },
        isEmailProvider: {
            bind: '{theTemplate.provider}',
            get: function (provider) {
                if (provider) {
                    return provider === CMDBuildUI.model.emails.Template.providers.email;
                }
            }
        },
        isInAppNotificationProvider: {
            bind: '{theTemplate.provider}',
            get: function (provider) {
                if (provider) {
                    return (
                        provider === CMDBuildUI.model.emails.Template.providers.inappnotification ||
                        provider === CMDBuildUI.model.emails.Template.providers.mobilenotification
                    );
                }
            }
        },
        isTemplateHTML: {
            bind: '{theTemplate.contentType}',
            get: function (contentType) {
                if (contentType) {
                    return contentType === 'text/html';
                }
            }
        },
        updateStoreVariables: {
            bind: {
                theKeyvaluedata: '{theTemplate.data}'
            },
            get: function (data) {
                var me = this;
                var resList = [];
                if (data.theKeyvaluedata) {
                    var templatedataArray = Object.entries(data.theKeyvaluedata);
                    var systemKeys = ['showOnClasses', 'uploadAttachments'];
                    var reports = this.get('theTemplate.reports');
                    var reportsCode = reports.map(function (r) {
                        return r.code;
                    });
                    var keysToSkip = Ext.Array.merge(systemKeys, reportsCode);
                    templatedataArray.forEach(function (meta) {
                        if (keysToSkip.indexOf(meta[0]) == -1) {
                            switch (meta[0]) {
                                case 'cm_lang_expr':
                                    me.set('theTemplate.cm_lang_expr', meta[1]);
                                    break;
                                case 'action':
                                    me.set('theTemplate.action', meta[1]);
                                    break;
                                case 'actionLabel':
                                    me.set('theTemplate.actionLabel', meta[1]);
                                    break;

                                default:
                                    resList.push({
                                        key: meta[0],
                                        value: meta[1]
                                    });
                                    break;
                            }
                            // if (meta[0] === 'cm_lang_expr') {
                            //     me.set('theTemplate.cm_lang_expr', meta[1]);
                            // } else {
                            //     resList.push({
                            //         key: meta[0],
                            //         value: meta[1]
                            //     });
                            // }
                        }
                    });
                }
                return resList;
            }
        },
        newKeyvaluedata: function () {
            return [CMDBuildUI.model.base.KeyValue.create()];
        },
        showOnClassesData: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.emails.showonallclasses,
                    value: true
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.emails.selectedclasses,
                    value: false
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.noone,
                    value: 'noone'
                }
            ];
        }
    },

    stores: {
        delaylist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{delaylistdata}',
            autoDestroy: true
        },
        allEmailAccounts: {
            type: 'chained',
            source: 'emails.Accounts',
            sorters: ['name'],
            filters: [
                function (account) {
                    return account.get('active');
                }
            ],
            autoLoad: true,
            autoDestroy: true
        },
        signaturesStore: {
            type: 'chained',
            source: 'emails.Signatures',
            sorters: ['description'],
            autoLoad: true,
            autoDestroy: true,
            filters: [
                function (item) {
                    return item.get('active');
                }
            ]
        },
        keyvaluedataStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoDestroy: true,
            proxy: 'memory',
            data: '{updateStoreVariables}',
            getDataObject: function () {
                var data = this.getRange();
                var obj = {};
                data.forEach(function (record) {
                    obj[record.get('key')] = record.get('value');
                });
                return obj;
            }
        },
        newKeyvaluedataStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoDestroy: true,
            proxy: 'memory',
            data: '{newKeyvaluedata}'
        },

        showOnClassesStore: {
            model: 'CMDBuildUI.model.base.KeyValue',
            autoDestroy: true,
            proxy: 'memory',
            data: '{showOnClassesData}'
        },

        classesStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description', 'enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        },

        showOnClassesViewStore: {
            proxy: {
                type: 'memory'
            },
            fields: ['description'],
            data: '{showOnClassesViewData}',
            autoDestroy: true
        }
    }
});
