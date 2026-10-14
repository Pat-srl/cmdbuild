/**
 * @file CMDBuildUI.mixins.ContextMenuComponent
 * @mixin CMDBuildUI.mixins.ContextMenuComponent
 * @author PAT srl
 * @access public
 *
 * @classdesc
 * This mixin must be used in the main view of a custom menu component.
 */
Ext.define('CMDBuildUI.mixins.ContextMenuComponent', {
    mixinId: 'contextmenucomponent-mixin',

    config: {
        /**
         * Use `getSelection` to get the value of this property. <br />
         * Use `setSelection` to set the value of this property.
         *
         * @type {CMDBuildUI.model.classes.Card[]|CMDBuildUI.model.processes.Instance[]}
         *
         * @memberof CMDBuildUI.mixins.ContextMenuComponent
         */
        selection: null,

        /**
         * Component definition.
         *
         * Use `getTheComponent` to get the value of this property. <br />
         * Use `setTheComponent` to set the value of this property.
         *
         * @type {CMDBuildUI.model.customcomponents.ContextMenu}
         *
         * @memberof CMDBuildUI.mixins.ContextMenuComponent
         */
        theComponent: null
    }
});
