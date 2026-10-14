Ext.define('CMDBuildUI.model.administration.AdminNavTreeItem', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'direction',
        type: "string"
    }, {
        name: 'domain',
        type: "string"
    }, {
        name: 'filter',
        type: "string"
    }, {
        name: 'nodes',
        type: "auto"
    }, {
        name: 'parent',
        type: "string"
    }, {
        name: 'recursionEnabled',
        type: "boolean"
    }, {
        name: 'showOnlyOne',
        type: "boolean"
    }, {
        name: 'targetClass',
        type: "string"
    }, {
        name: "subclassViewMode",
        type: "string"
    }, {
        name: "subclassViewShowIntermediateNodes",
        type: "boolean"
    }],

    proxy: {
        type: 'memory'
    }

    // _id: CMDBuildUI.util.Utilities.generateUUID(),
    // text: d.get("description") + ' [' + d.getTranslatedDescriptionDirect() + ' ' + destinationObject.get("description") + ']',
    // targetClass: '', // d.get("destination"),
    // domainTargetClass: d.get("source"),
    // targetIsProcess: d.get("destinationProcess"),
    // domain: d.get("name"),
    // checked: false,
    // direction: '_1',
    // filter: '',
    // parent: node.get('_id'),
    // showOnlyOne: false,
    // recursionEnabled: false,
    // expanded: false,
    // leaf: false
    // subclassViewMode: "cards"
    // subclassViewShowIntermediateNodes: true
});