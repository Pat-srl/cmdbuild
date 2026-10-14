Ext.define('CMDBuildUI.model.views.JoinViewJoin', {
    extend: 'Ext.data.Model',
    statics: {
        jointypes: {
            inner_join: 'inner_join',
            outer_join: 'left_join'
        }
    },
    fields: [{
        name: 'source', // source alias
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'domain', // ex MyClassToMyOtherClass
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'direction', // direct/inverse
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'targetType', // target class type, must be valid of subclass of domain (optionsl, default to domain target)
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'joinType',
        type: 'string', // inner_join / left_join
        persist: true,
        critical: true
    }, {
        name: 'domainAlias', // ex. MyClassToMyOtherClass_1
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'targetAlias', // ex. MyOtherClass_1
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'filter', // join filter
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: '{}'
    }],

    proxy: {
        type: 'memory'
    }
});