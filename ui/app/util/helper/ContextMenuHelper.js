/**
 * @file CMDBuildUI.util.helper.ContextMenuHelper
 * @module CMDBuildUI.util.helper.ContextMenuHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.ContextMenuHelper', {
    singleton: true,

    /**
     * @property {String} height
     * The popup height
     */
    height: null,

    /**
     * @property {String} width
     * The popup width
     */
    width: null,

    /**
     * Open custom context menu.
     *
     * @param {CMDBuildUI.model.ContextMenuItem} item
     * @param {CMDBuildUI.model.classes.Card[]|CMDBuildUI.model.processes.Instance[]} selection
     * @param {CMDBuildUI.view.classes.cards.grid.Grid | CMDBuildUI.view.processes.instances.Grid} grid
     *
     * @example
     * CMDBuildUI.util.helper.ContextMenuHelper.openCustomComponent(item, selection, grid);
     *
     */
    openCustomComponent: function (item, selection, grid) {
        if (item) {
            Ext.require(Ext.String.format('CMDBuildUI.{0}', item.get('jscomponent')), function () {
                // create widget configuration
                const config = {
                    xtype: item.get('alias').replace('widget.', ''),
                    selection: selection,
                    ownerGrid: grid,
                    theComponent: item,
                    listeners: {
                        /**
                         * Custom event to close popup directly from widget
                         */
                        popupclose: function (eOpts) {
                            popup.close();
                        }
                    }
                };
                // custom panel listeners
                const listeners = {
                    /**
                     * @param {Ext.panel.Panel} panel
                     * @param {Object} eOpts
                     */
                    beforeclose: function (panel, eOpts) {
                        panel.removeAll(true);
                    }
                };
                // open popup
                const popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    item.get('_label_translation') || item.get('label'),
                    config,
                    listeners,
                    {
                        width: item.get('width'),
                        height: item.get('height')
                    }
                );
            });
        }
    }
});
