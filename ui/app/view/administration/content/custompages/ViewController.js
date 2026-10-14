Ext.define('CMDBuildUI.view.administration.content.custompages.ViewController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.File'],
    alias: 'controller.administration-content-custompages-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
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
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.custompages.View} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        vm.bind(
            {
                bindTo: {
                    targetDevices: '{targetDevices}',
                    targetDevicesStore: '{targetDevicesStore}',
                    target: '{target}'
                }
            },
            function (data) {
                var device = vm.get('targetDevicesStore').findRecord('value', data.target);
                if (device) {
                    var title = Ext.String.format(
                        '{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.custompages.plural,
                        device.get('label')
                    );
                    view.up('administration-content').getViewModel().set('title', title);
                }
            }
        );
        var title = Ext.String.format('{0}', CMDBuildUI.locales.Locales.administration.custompages.plural);
        view.up('administration-content').getViewModel().set('title', title);
        this.setFilefieldProperties();
    },

    /**
     * On delete custompage button click
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
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
                    CMDBuildUI.util.Ajax.setActionId('delete-custompage');
                    me.getViewModel()
                        .get('theCustompage')
                        .erase({
                            success: function (record, operation) {
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl();
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

    removeVersionBtnClick: function (button) {
        var vm = this.getViewModel();
        vm.set(Ext.String.format('{0}Removed', button.device), true);
    },
    downloadVersionBtnClick: function (button) {
        var vm = this.getViewModel();
        var url = Ext.String.format(
            '{0}/custompages/{1}/{2}',
            CMDBuildUI.util.Config.baseUrl,
            vm.get('theCustompage._id'),
            button.device
        );
        CMDBuildUI.util.File.download(url, 'zip');
    },

    /**
     * On download custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, e, eOpts) {
        var onDownloadForDevice = function () {
            var vm = button.lookupViewModel();
            var url = Ext.String.format(
                '{0}/custompages/{1}/{2}',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('theCustompage._id'),
                this.device
            );
            CMDBuildUI.util.File.download(url, 'zip');
        };
        var devices = this.getViewModel().get('theCustompage.devices');
        var hasDefault = devices.indexOf(CMDBuildUI.model.menu.Menu.device['default']) > -1;
        var hasMobile = devices.indexOf(CMDBuildUI.model.menu.Menu.device.mobile) > -1;

        var items = [];
        if (hasDefault) {
            items.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('desktop', 'solid'),
                text: CMDBuildUI.locales.Locales.administration.common.labels.desktop,
                device: CMDBuildUI.model.menu.Menu.device['default'],
                height: 32,
                autoEl: {
                    'data-testid': 'custompages-downloadDefaultBtn'
                },
                handler: onDownloadForDevice
            });
        }
        if (hasMobile) {
            items.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('mobile-alt', 'solid'),
                text: CMDBuildUI.locales.Locales.administration.common.labels.mobile,
                device: CMDBuildUI.model.menu.Menu.device.mobile,
                height: 32,
                autoEl: {
                    'data-testid': 'custompages-downloadDefaultBtn'
                },
                handler: onDownloadForDevice
            });
        }

        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            ui: 'default',
            items: items
        });
        menu.setMinWidth(35);
        menu.alignTo(button.el.id, 't-b?');
    },

    /**
     * On edit custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.setFilefieldProperties();
        view.up('administration-content-custompages-tabpanel').getViewModel().toggleEnableTabs(0);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var nextUrl;
        if (vm.get('actions.add')) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(null, false);
            this.redirectTo(nextUrl, true);
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode('objecttype', CMDBuildUI.model.administration.MenuItem.types.custompage);
            vmNavigation.set('selected', currentNode);
        } else {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(
                vm.get('theCustompage._id'),
                false
            );
            view.up('administration-content-custompages-tabpanel').getViewModel().toggleEnableTabs(0);
            vm.get('theCustompage').reject();
            vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            this.redirectTo(nextUrl, true);
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
        var theCustompage = vm.get('theCustompage');
        theCustompage.set('active', !theCustompage.get('active'));

        theCustompage.save({
            success: function (record, operation) {},
            failure: function (record, reason) {
                record.reject();
            }
        });
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
            const theCustompage = vm.get('theCustompage');
            if (theCustompage.get('icon_font')) {
                theCustompage.set(
                    'icon_font',
                    CMDBuildUI.util.helper.IconHelper.setIconDBValue(theCustompage.get('icon_font'))
                );
            }
            button.setDisabled(true);
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
            var removeVersion = function (record, device) {
                Ext.Ajax.request({
                    url: Ext.String.format('{0}{1}/{2}', record.getProxy().getUrl(), record.get('_id'), device),
                    method: 'DELETE'
                });
            };
            var afterSave = function (record) {
                if (vm.get('defaultRemoved')) {
                    removeVersion(record, CMDBuildUI.model.menu.Menu.device['default']);
                }
                if (vm.get('mobileRemoved')) {
                    removeVersion(record, CMDBuildUI.model.menu.Menu.device.mobile);
                }
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(
                    record.get('_id')
                );
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
                        record = CMDBuildUI.model.custompages.CustomPage.create(record.data);
                    }
                    if (vm.get('theTranslation')) {
                        var key =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfCustomPageDescription(
                                record.get('name')
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
                function (reason) {
                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                }
            );
        }
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfCustomPageDescription(
                !isNaN(vm.get('theCustompage').get('_id')) ? vm.get('theCustompage').get('name') : '..'
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
                    view.down('[name="fileCustomcomponentDefault"]'),
                    true,
                    this.getView()
                );
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                    view.down('[name="fileCustomcomponentMobile"]'),
                    true,
                    this.getView()
                );
            } else if (vm.get('actions.add')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                    view.down('[name="fileCustomcomponentDefault"]'),
                    false,
                    this.getView()
                );
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                    view.down('[name="fileCustomcomponentMobile"]'),
                    false,
                    this.getView()
                );
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.custompages.ViewModel} vm
         * @param {Function} successCb
         * @param {Function} errorCb
         */
        save: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('custompage.upload');
            // define method
            var method = vm.get('actions.add') ? 'POST' : 'PUT';

            var fileDefault = this.getView().down('[name="fileCustomcomponentDefault"]').extractFileInput();
            var fileMobile = this.getView().down('[name="fileCustomcomponentMobile"]').extractFileInput();

            // init formData
            var formData = new FormData();

            // append attachment json data
            var data = vm.get('theCustompage').getData();
            if (vm.get('actions.add')) {
                delete data._id;
            }
            var jsonData = Ext.encode(data);
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
            var custompageUrl = Ext.String.format('{0}/custompages', CMDBuildUI.util.Config.baseUrl);
            var url = vm.get('actions.add')
                ? custompageUrl
                : Ext.String.format('{0}/{1}', custompageUrl, vm.get('theCustompage._id'));
            // upload
            if (fileDefault.files.length || fileMobile.files.length) {
                CMDBuildUI.util.Ajax.initRequestException();
                var files = [];
                if (fileDefault) {
                    files.push(fileDefault);
                }
                if (fileMobile) {
                    files.push(fileMobile);
                }
                CMDBuildUI.util.administration.File.upload(method, formData, files, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [response]);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                        }
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            } else {
                // file is empty, use default put
                vm.get('theCustompage').save({
                    success: function (record, operation) {
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [record]);
                        }
                    },
                    failure: function (record, reason) {
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [reason]);
                        }
                    }
                });
            }
        }
    }
});
