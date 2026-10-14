/**
 * @file CMDBuildUI.util.DependencyTree
 * @module CMDBuildUI.util.DependencyTree
 * @author PAT srl
 * @access private
 */
Ext.define('CMDBuildUI.util.DependencyTree', {
    singleton: true,

    /**
     * Update the descriptions for the dependency trees
     *
     * @param {Object} value represent the description of the dependency trees.
     *
     * @example
     * CMDBuildUI.util.DependencyTree.setDependencyTreeDescription({});
     */
    setDependencyTreeDescription: function (value) {
        this._dependencyTreeDescription = value;
    },

    /**
     * Get the descriptions for the dependency trees
     *
     * @return {Object}
     *
     * @example
     * CMDBuildUI.util.DependencyTree.getDependencyTreeDescription();
     */
    getDependencyTreeDescription: function () {
        return this._dependencyTreeDescription;
    },

    /**
     * Update the value of dependency trees associated to a specific class
     *
     * @param {Object} value represent the classes with the dependency tree associated to them.
     *
     * @example
     * CMDBuildUI.util.DependencyTree.setDependencyTreeClasses({});
     */
    setDependencyTreeClasses: function (value) {
        this._dependencyTreeClasses = value;
    },

    /**
     * Get the dependency trees associated to a specific class
     *
     * @param {Object} objectTypeName represent the class on to return the dependency trees associated.
     * @return {Array}
     *
     * @example
     * CMDBuildUI.util.DependencyTree.getDependencyTreeClasses("Building");
     */
    getDependencyTreeClasses: function (objectTypeName) {
        return this._dependencyTreeClasses[objectTypeName];
    },

    /**
     *
     * @param {CMDBuildUI.model.dependencytrees.DependencyTree} domainTree
     * @param {Function} openButtonHandler
     */
    openPopupToSelectRoot: function (domainTree, openButtonHandler) {
        const rootNode = domainTree.nodes().getRange()[0];
        const targetClass = rootNode.get('targetClass');

        CMDBuildUI.util.Utilities.openPopup(
            'dependencyTree',
            Ext.String.format(
                '{0} {1}',
                CMDBuildUI.locales.Locales.dependencytree.name,
                domainTree.get('_description_translation') || domainTree.get('description')
            ),
            {
                xtype: 'form',
                bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [
                    {
                        xtype: 'referencefield',
                        fieldLabel: CMDBuildUI.locales.Locales.dependencytree.selectcard,
                        allowBlank: false,
                        displayField: 'Description',
                        valueField: '_id',
                        recordLinkName: 'theObject',
                        metadata: {
                            targetType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            targetClass: targetClass,
                            ecqlFilter: rootNode.get('ecqlFilter')
                        }
                    }
                ],
                buttons: [
                    {
                        xtype: 'button',
                        ui: 'secondary-action',
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        handler: function (button) {
                            CMDBuildUI.util.Utilities.closePopup('dependencyTree');
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'management-primary',
                        text: CMDBuildUI.locales.Locales.common.actions.open,
                        formBind: true,
                        handler: openButtonHandler
                    }
                ]
            },
            {},
            {
                width: '35%',
                height: '30%'
            }
        );
    },

    privates: {
        /**
         * @property {Boolean} _dependencyTreeClasses
         * Dependency tree classes
         */
        _dependencyTreeClasses: {},

        /**
         * @property {Boolean} _dependencyTreeDescription
         * Dependency tree description
         */
        _dependencyTreeDescription: {}
    }
});
