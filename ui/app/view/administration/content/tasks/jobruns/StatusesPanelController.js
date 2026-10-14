Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.StatusesPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-jobruns-statusespanel',


    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#runningFilterTool': {
            click: 'onFilterToolClick'
        },
        '#failedFilterTool': {
            click: 'onFilterToolClick'
        },
        '#completedFilterTool': {
            click: 'onFilterToolClick'
        }
    },
    /**
     * 
     * @param {CMDBuildUI.view.administration.content.tasks.jobruns.StatusesPanel} view 
     */
    onAfterRender: function (view) {
        view.fetchData();
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.tasks.jobruns.StatusesPanel} view 
     */
    onFilterToolClick: function (view) {

        var grid = this.getView().up('administration-content-tasks-jobruns-view').down('administration-content-tasks-jobruns-grid');
        var filterBy = '';
        switch (view.getItemId()) {
            case 'runningFilterTool':
                filterBy = 'running';
                break;
            case 'failedFilterTool':
                filterBy = 'failed';
                break;
            case 'completedFilterTool':
                filterBy = 'completed';
                break;
            default:
                break;
        }
        var column = grid.getColumnManager().columns[3];
        var columnFilter = column.filter;
        if (!columnFilter.menu) {
            columnFilter.createMenu();
        }
        var filterCheck = columnFilter.menu
            .down('menuitem[value="' + filterBy + '"]');

        filterCheck.setChecked(!filterCheck.checked);
        this.getView().fetchData();
    }
});