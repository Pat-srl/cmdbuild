Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewinrow',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
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
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const theProject = vm.get('theProject');
        const toolAction = vm.get('toolAction');
        const projectsWithoutParent = vm.get('projectsWithoutParent');
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: theProject,
                    toolAction: toolAction,
                    projectsWithoutParent: projectsWithoutParent,
                    actions: {
                        edit: true,
                        view: false,
                        add: false
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
    onOpenBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const theProject = vm.get('theProject');
        const toolAction = vm.get('toolAction');
        const projectsWithoutParent = vm.get('projectsWithoutParent');
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: theProject,
                    toolAction: toolAction,
                    projectsWithoutParent: projectsWithoutParent,
                    actions: {
                        edit: false,
                        view: true,
                        add: false
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
        const me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-project');
                    const vm = me.getViewModel();
                    const theProject = vm.get('theProject');
                    theProject.erase({
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
     * @param {Event} event
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, event, eOpts) {
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
     * @param {Event} event
     * @param {Object} eOpts
     */
    onConvertBtnClick: function (button, event, eOpts) {
        const grid = this.getView().up('#bimProjectsGrid');;
        CMDBuildUI.util.Utilities.showLoader(true, grid);
        button.setDisabled(true);
        const theProject = this.getViewModel().get('theProject');
        Ext.Ajax.request({
            url: Ext.String.format('{0}/{1}/convert/xkt', theProject.getProxy().getUrl(), theProject.getId()),
            method: 'POST',
            failure: function () {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (response) {
                if (grid && !grid.destroyed) {
                    CMDBuildUI.util.Utilities.showLoader(false, grid);
                }
            }
        });
    }
});