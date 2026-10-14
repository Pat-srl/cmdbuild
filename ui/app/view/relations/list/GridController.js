Ext.define('CMDBuildUI.view.relations.list.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-grid',
    mixins: ['CMDBuildUI.view.relations.Mixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemdblclick: 'onItemDblClick'
        },
        'tableview': {
            actionopencard: 'onActionOpenCard',
            actioneditrelation: 'onActionEditRelation',
            actiondeleterelation: 'onActionDeleteRelation',
            actioneditcard: 'onActionEditCard'
        }
    },

    onBeforeRender: function (view) {
        //sets the height of the grid;
        view.calculateHeight();

        view.lookupViewModel().bind(
            {
                bindTo: '{allRelations}'
            },
            function (store) {
                const loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
                // enable remote sort here because
                // autoLoad=false is ignored when grouping is actived
                // and remoteSort is set to true. See EXTJS-19781.
                store.setRemoteSort(true);
                // load store
                store.load({
                    callback: function () {
                        CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                    },
                    scope: this
                });
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Boolean} openInGrid
     *
     */
    onActionOpenCard: function (grid, record, rowIndex, colIndex, openInGrid) {
        const me = this;
        const destinationType = record.get('_destinationType');
        const destinationId = record.get('_destinationId');
        var action = null;
        const destinationObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType);

        // Need both for prevent the opening of the superclass on the view
        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.view) &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.subclass)
        ) {
            action = CMDBuildUI.mixins.DetailsTabPanel.actions.view;
        } else if (!openInGrid) {
            // Configuration win over params
            action = CMDBuildUI.mixins.DetailsTabPanel.actions.view;
        }

        /**
         *
         * @param {String} objectName
         */
        const redirectTo = function (objectName) {
            me.redirectToRelationTarget(objectName, destinationType, destinationId, action);
        };
        // is a superclass?
        if (
            destinationObj.get('prototype') &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.subclass)
        ) {
            CMDBuildUI.util.api.Client.getRemoteCard(
                CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                destinationId
            ).then(function (card) {
                redirectTo(card.get('_type'));
            });
        } else {
            redirectTo(destinationType);
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionEditRelation: function (grid, record, rowIndex, colIndex) {
        this.editRelationItem(grid, record);
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionDeleteRelation: function (grid, record, rowIndex, colIndex) {
        this.deleteRelationItem(grid, record);
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     *
     */
    onActionEditCard: function (grid, record, rowIndex, colIndex) {
        this.editRelatedCard(grid, record);
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemDblClick: function (grid, record, item, index, e, eOpts) {
        this.onActionOpenCard(grid, record, null, null, grid.lookupViewModel().get('readonly'));
    }
});
