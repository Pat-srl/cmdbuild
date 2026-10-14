Ext.define('CMDBuildUI.model.views.JoinViewAttribute', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'expr', // ex. MyClass_1.MyAttr
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: '_attributeClassAlias',
        type: 'string',
        calculate: function (data) {
            return data.expr.split('.')[0];
        }
    }, {
        name: 'name', // ex MySpecialAttr
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description', // default from expr descr
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'group', // target class type, must e valid of subclass of domain (optionsl, default to domain target)
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'showInGrid',
        type: 'boolean', // optional
        persist: true,
        critical: true
    }, {
        name: 'showInReducedGrid',
        type: 'boolean', // optional
        persist: true,
        critical: true
    }, {
        name: 'targetAlias',
        type: 'string',
        persist: true,
        critical: true,
        calculate: function (data) {
            return data.expr.split('.')[0];
        }
    }, {
        name: '_deepIndex',
        type: 'string',
        persist: false,
        critical: false
    }],
    // only for layout composer
    /**
     * Return description with the name like: `Last Name [LastName]`
     * 
     * @return {String}
     */
    getDescriptionWithName: function () {
        return Ext.String.format('{0} [{1}]', this.get('description'), this.get('expr'));

    },
    proxy: {
        type: 'memory'
    }
});