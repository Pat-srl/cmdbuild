Ext.define('CMDBuildUI.view.relations.Utils', {
    singleton: true,

    getRelationProxyUrl: function (relation, params) {
        if (params && params.proxyurl) {
            return params.proxyurl;
        }

        const objectType = params && (params.objectType || params.objecttype);
        const objectTypeName = params && (params.objecttypename || params.objectTypeName || params.objecttypeName);
        const objectId = params && (params.objectid || params.objectId);
        const relationSourceType = relation && relation.get('_sourceType');
        const relationSourceId = relation && relation.get('_sourceId');

        if (!objectTypeName || !objectId) {
            return null;
        }

        if (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            return CMDBuildUI.util.api.Processes.getProcessInstanceRelations(objectTypeName, objectId);
        }

        if (relationSourceType && relationSourceId && objectTypeName === relationSourceType) {
            return CMDBuildUI.util.api.Classes.getCardRelations(objectTypeName, objectId);
        }

        return CMDBuildUI.util.api.Classes.getCardRelations(objectTypeName, objectId);
    },

    /**
     * Check if a doman can be added, domain that already 1:N or N:1 already assigned to a reference can't be added
     * @param {CMDBuildUI.model.domains.ObjectDomain} domain
     * @param {'direct' | 'inverse'} direction
     * @param {CMDBuildUI.model.Attribute} referenceDomains
     * @returns
     */
    canAddRelation: function (domain, direction, referenceDomains) {
        if (!referenceDomains.includes(domain.getId())) {
            return true;
        } else {
            if (
                // 1:N Add "N" side if destination class is same of source or subclass
                domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.onetomany &&
                Ext.Array.contains(
                    CMDBuildUI.util.helper.ModelHelper.getClassFromName(domain.get('source')).getHierarchy(),
                    domain.get('destination')
                ) &&
                direction === 'direct'
            ) {
                return true;
            } else if (
                // N:1 Add "N" side if source class is same of destination or subclass
                domain.get('cardinality') === CMDBuildUI.model.domains.Domain.cardinalities.manytoone &&
                Ext.Array.contains(
                    CMDBuildUI.util.helper.ModelHelper.getClassFromName(domain.get('destination')).getHierarchy(),
                    domain.get('source')
                ) &&
                direction === 'inverse'
            ) {
                return true;
            }
        }
        return false;
    },

    /**
     * Check if domain is addable from source
     * @param {CMDBuildUI.model.domains.ObjectDomain} domain
     * @param {string[]} objectHierarchy
     * @param {string} objectTypeName
     * @returns
     */
    canAddDirect: function (domain, objectHierarchy, objectTypeName) {
        return (
            Ext.Array.contains(objectHierarchy, domain.get('source')) &&
            !Ext.Array.contains(domain.get('disabledSourceDescendants'), objectTypeName) &&
            !domain.get('destinationProcess') &&
            domain.get('sourceEditable')
        );
    },

    /**
     * Check if domain is addable from destination
     * @param {CMDBuildUI.model.domains.ObjectDomain} domain
     * @param {string[]} objectHierarchy
     * @param {string} objectTypeName
     * @returns
     */
    canAddInverse: function (domain, objectHierarchy, objectTypeName) {
        return (
            Ext.Array.contains(objectHierarchy, domain.get('destination')) &&
            !Ext.Array.contains(domain.get('disabledDestinationDescendants'), objectTypeName) &&
            !domain.get('sourceProcess') &&
            domain.get('targetEditable')
        );
    },

    canEnableAddButton: function (domain) {
        return domain.get(CMDBuildUI.model.base.Base.permissions.add);
    },

    /**
     * Delete relation
     *
     * @param {CMDBuildUI.model.domains.Relation} relation
     *
     * @return {Ext.promise.Promise}
     */
    deleteRelation: function (relation) {
        var deferred = new Ext.Deferred();

        // show confirm message
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.relations.deleterelationconfirm,
            function (btnText) {
                if (btnText === 'yes') {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    // erase item
                    CMDBuildUI.util.Ajax.setActionId('relation.delete');
                    relation.erase({
                        url: Ext.String.format('/domains/{0}/relations', relation.get('_type')),
                        success: function (record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function (record, operation) {
                            deferred.reject();
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }
        );

        return deferred.promise;
    },

    /**
     * Edit relation
     *
     * @param {CMDBuildUI.model.domains.Relation} relation
     * @param {Object} params
     * @param {String} params.proxyurl
     * @param {String} params.objecttypename
     * @param {String} params.objectid
     *
     * @return {Ext.promise.Promise}
     */
    editRelation: function (relation, params) {
        var deferred = new Ext.Deferred(),
            domain = CMDBuildUI.util.helper.ModelHelper.getDomainFromName(relation.get('_type')),
            destinationdescription = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(
                relation.get('_destinationType')
            ),
            popup,
            targettype,
            reldescription;

        // get relation description
        if (domain) {
            if (relation.get('_direction') === 'direct') {
                reldescription = domain.get('_descriptionDirect_translation');
                targettype = domain.get('destination');
            } else {
                reldescription = domain.get('_descriptionInverse_translation');
                targettype = domain.get('source');
            }
        }

        // popup title
        var title = Ext.String.format(
            '{0} {1} ({2})',
            CMDBuildUI.locales.Locales.relations.editrelation,
            reldescription,
            destinationdescription
        );

        // update relation proxy url
        var proxyUrl = CMDBuildUI.view.relations.Utils.getRelationProxyUrl(relation, params);
        if (proxyUrl) {
            relation.getProxy().setUrl(proxyUrl);
        }

        // popup config
        var popupconfig = {
            xtype: 'relations-list-edit-gridcontainer',
            originTypeName: params.objecttypename,
            originId: params.objectid,
            viewModel: {
                data: {
                    theObject: params.theObject,
                    objectTypeName: targettype,
                    relationDirection: relation.get('_direction'),
                    theRelation: relation,
                    theDomain: domain
                }
            },

            listeners: {
                /**
                 * Custom event to close popup directly from popup
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            },
            onSaveSuccess: function () {
                deferred.resolve();
            }
        };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(null, title, popupconfig);

        return deferred.promise;
    }
});
