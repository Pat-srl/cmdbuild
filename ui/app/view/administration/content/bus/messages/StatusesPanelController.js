Ext.define('CMDBuildUI.view.administration.content.bus.messages.StatusesPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-messages-statusespanel',


    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#draftFilterTool': {
            click: 'onFilterToolClick'
        },
        '#queuedFilterTool': {
            click: 'onFilterToolClick'
        },
        '#processingFilterTool': {
            click: 'onFilterToolClick'
        },
        '#processedFilterTool': {
            click: 'onFilterToolClick'
        },
        '#errorFilterTool': {
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
     * @param {CMDBuildUI.view.administration.content.bus.messages.StatusesPanel} view 
     */
    onAfterRender: function (view) {
        view.fetchData();
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.bus.messages.StatusesPanel} view 
     */
    onFilterToolClick: function (view) {

        var grid = this.getView().up('administration-content-bus-messages-view').down('administration-content-bus-messages-grid');
        var filterBy = '';
        switch (view.getItemId()) {
            case 'draftFilterTool':
                filterBy = 'draft';
                break;
            case 'queuedFilterTool':
                filterBy = 'queued';
                break;
            case 'processingFilterTool':
                filterBy = 'processing';
                break;
            case 'processedFilterTool':
                filterBy = 'processed';
                break;
            case 'errorFilterTool':
                filterBy = 'error';
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
        var column = grid.getColumnManager().columns[4];
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