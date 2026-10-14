/**
 * This is not a real model. It is a singleton with base information
 * used to create history models.
 */
Ext.define('CMDBuildUI.model.History', {
    singleton: true,

    fields: [{
        name: '_beginDate',
        type: 'date'
    }, {
        name: '_endDate',
        type: 'date'
    }, {
        name: '_user',
        type: 'string'
    }]
});