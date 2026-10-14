Ext.define('CMDBuildUI.model.bim.Projects', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_id',
        type: 'number',
        critical: true
    }, {
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'lastCheckin',
        type: 'string',
        critical: true
    }, {
        name: 'projectId',
        type: 'string',
        critical: true
    }, {
        name: 'parentId',
        type: 'string',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'ownerClass',
        type: 'string',
        critical: true
    }, {
        name: 'ownerCard',
        type: 'string',
        critical: true
    }, {
        name: 'fileMapping',
        type: 'string',
        critical: true
    }],

    /**
     * Return a clean clone of a geoattribute.
     * 
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function () {
        var newGeoAttribute = this.copy();
        newGeoAttribute.set('_id', undefined);
        newGeoAttribute.set('name', Ext.String.format('{0}_copy', newGeoAttribute.get('name')));
        newGeoAttribute.set('description', Ext.String.format('{0}_copy', newGeoAttribute.get('description')));
        newGeoAttribute.set('lastCheckin', null);
        newGeoAttribute.crudState = "C";
        newGeoAttribute.phantom = true;
        delete newGeoAttribute.crudStateWas;
        delete newGeoAttribute.previousValues;
        delete newGeoAttribute.modified;
        return newGeoAttribute;
    },

    proxy: {
        url: '/bim/projects',
        type: 'baseproxy',
        reader: {
            type: 'json'
        }
    },
});