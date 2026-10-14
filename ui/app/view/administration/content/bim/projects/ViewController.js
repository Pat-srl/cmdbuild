Ext.define('CMDBuildUI.view.administration.content.bim.projects.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-view',

    listen: {
        global: {
            projectbimcreated: 'onProjectBimCreated',
            projectbimupdated: 'onProjectBimUpdated'
        }
    },

    control: {
        '#': {
            afterlayout: 'onAfterLayout'
        },
        '#addproject': {
            click: 'onNewProjectBtnClick'
        },
        '#bimProjectsGrid': {
            rowdblclick: 'onRowDblclick'
        }
    },

    /**
     *
     * @param {Object} response
     */
    onProjectBimCreated: function (response) {
        const grid = this.getView().down("#bimProjectsGrid")
        const plugin = grid.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs("itemcreated", [grid, Ext.create('CMDBuildUI.model.bim.Projects', response), this]);
        }
    },

    /**
     *
     * @param {Object} response
     */
    onProjectBimUpdated: function (response) {
        const grid = this.getView().down("#bimProjectsGrid");
        const plugin = grid.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs("itemupdated", [grid, Ext.create('CMDBuildUI.model.bim.Projects', response), this]);
        }
    },

    /**
     *
     * @param {Ext.container.Container} container
     * @param {Ext.layout.container.Container} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (container, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onNewProjectBtnClick: function (button, event, eOpts) {
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        const vm = this.getViewModel()
        const toolAction = vm.get('toolAction');
        const projectsWithoutParent = vm.get('projectsWithoutParent');
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                links: {
                    theProject: {
                        type: 'CMDBuildUI.model.bim.Projects',
                        create: true
                    }
                },
                data: {
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
     * @param {Ext.view.Table} row
     * @param {Ext.data.Model} record
     * @param {HTMLElement} element
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        const vm = this.getViewModel();
        const toolAction = vm.get('toolAction');
        const projectsWithoutParent = vm.get('projectsWithoutParent');
        const formInRow = this.getView().down("#bimProjectsGrid").getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);

        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: record,
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
    }

});