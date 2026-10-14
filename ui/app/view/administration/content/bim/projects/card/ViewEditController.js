Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewedit',

    control: {
        '#': {
            beforeRender: 'onBeforeRender'
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
        '#cloneBtn': {
            click: 'onCloneBtnClick'
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
        '#convertBtn': {
            click: 'onConvertBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().bind('{panelTitle}', function (panelTitle) {
            if (panelTitle) {
                view.up().setTitle(panelTitle);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const form = this.getView();

        if (form.isValid()) {
            CMDBuildUI.util.Utilities.showLoader(true);
            const theProject = this.getViewModel().get('theProject');
            const getData = function () {
                const data = theProject.getData();
                if (theProject.phantom) {
                    delete data._id;
                }
                for (let key in data) {
                    if (Ext.String.startsWith(key, "_")) {
                        delete data[key];
                    }
                }
                return data;
            };

            CMDBuildUI.util.File.uploadFileWithMetadata(
                theProject.phantom ? 'POST' : 'PUT',
                Ext.String.format('{0}/bim/projects{1}', CMDBuildUI.util.Config.baseUrl, !theProject.phantom ? '/' + theProject.get('_id') : ''),
                form.down("#fileIFC").extractFileInput().files[0],
                getData(),
                {
                    filePartName: 'file',
                    metadataPartName: 'data'
                }
            ).then(function (response) {
                const eventtocall = theProject.phantom ? 'projectbimcreated' : 'projectbimupdated';
                Ext.GlobalEvents.fireEventArgs(eventtocall, [response]);
                CMDBuildUI.util.Utilities.showLoader(false);
                form.up("panel").close();
            }, function () {
                CMDBuildUI.util.Utilities.showLoader(false);
            });
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.getView().up('panel').close();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        vm.set('actions.edit', true);
        vm.set('actions.view', false);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, event, eOpts) {
        const url = Ext.String.format('{0}/bim/projects/{1}/file?bimFormat={2}', CMDBuildUI.util.Config.baseUrl, this.getViewModel().get('theProject').getId(), button.fileType);
        CMDBuildUI.util.File.download(url, 'ifc');
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const clonedProject = Ext.copy(vm.get('theProject').clone());
        const toolAction = vm.get('toolAction');
        const projectsWithoutParent = vm.get('projectsWithoutParent');
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: clonedProject,
                    toolAction: toolAction,
                    projectsWithoutParent: projectsWithoutParent,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-project');
                    const theProject = vm.get('theProject');
                    theProject.erase({
                        success: function (record, operation) {
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        },
                        failure: function (opration) {
                            theProject.reject();
                        }
                    });
                }
            });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        const theProject = this.getViewModel().get('theProject');
        theProject.set('active', !theProject.get('active'));
        theProject.save({
            failure: function (record, reason) {
                record.reject();
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onConvertBtnClick: function (button, e, eOpts) {
        const view = this.getView();
        const vm = this.getViewModel();
        const theProject = vm.get('theProject');
        CMDBuildUI.util.Utilities.showLoader(true, view);

        Ext.Ajax.request({
            url: Ext.String.format('{0}/{1}/convert/xkt', theProject.getProxy().getUrl(), theProject.getId()),
            method: 'POST',
            failure: function () {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (response) {
                if (view && !view.destroyed) {
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                }
            }
        });
    }
});