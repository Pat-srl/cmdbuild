Ext.define('CMDBuildUI.view.emails.Container', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.emails.ContainerController'
    ],

    alias: 'widget.emails-container',
    controller: 'emails-container',

    layout: 'fit',

    config: {
        /**
         * @cfg {Boolean} readOnly 
         * 
         * Set to `true` to show details tabs in read-only mode
         */
        readOnly: false
    }
});