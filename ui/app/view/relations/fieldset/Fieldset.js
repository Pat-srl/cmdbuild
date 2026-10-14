
Ext.define('CMDBuildUI.view.relations.fieldset.Fieldset', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.relations.fieldset.FieldsetController',
        'CMDBuildUI.view.relations.fieldset.FieldsetModel'
    ],

    alias: 'widget.relations-fieldset',
    controller: 'relations-fieldset',
    viewModel: {
        type: 'relations-fieldset'
    },

    statics: {
        /**
         * 
         * @param {CMDBuild.model.domains.Domain} domain 
         * @param {Object} target 
         * @param {String} target.objectType
         * @param {String} target.objectTypeName
         * @param {Number} target.objectId
         * @param {Object} config
         * @param {Object} forceinsert
         * @param {Object} directions
         * @return {CMDBuildUI.view.relations.fieldset.Fieldset[]}
         */
        getFieldsetConfig: function (domain, target, config, forceinsert, directions) {
            var conf = [];
            config = config || {};
            var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                target.objectTypeName,
                target.objectType
            );
            var objectHierarchy = object.getHierarchy();

            function pushconf(direction) {
                conf.push(Ext.applyIf({
                    xtype: 'relations-fieldset',
                    viewModel: {
                        data: {
                            domain: domain,
                            direction: direction,
                            current: target
                        }
                    }
                }, config));
            }
            if (Ext.Array.contains(objectHierarchy, domain.get("source")) && (!directions || directions.direct) && (
                forceinsert ||
                domain.get('cardinality') == CMDBuildUI.model.domains.Domain.cardinalities.onetomany ||
                domain.get('cardinality') == CMDBuildUI.model.domains.Domain.cardinalities.manytomany)) {
                pushconf("_2");
            }
            if (Ext.Array.contains(objectHierarchy, domain.get("destination")) && (!directions || directions.inverse) && (
                forceinsert ||
                domain.get('cardinality') == CMDBuildUI.model.domains.Domain.cardinalities.manytoone ||
                domain.get('cardinality') == CMDBuildUI.model.domains.Domain.cardinalities.manytomany)) {
                pushconf("_1");
            }
            return conf;
        }
    },


    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    collapsible: true,


    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
    bind: {
        title: '{title}'
    }
});
