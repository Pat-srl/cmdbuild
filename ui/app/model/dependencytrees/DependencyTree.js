Ext.define('CMDBuildUI.model.dependencytrees.DependencyTree', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: ['CMDBuildUI.validator.TrimPresence'],

    fields: [
        {
            name: 'name',
            type: 'string',
            validators: ['trimpresence'],
            critical: true,
            persist: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true,
            persist: true
        },
        {
            name: 'nodes',
            type: 'auto',
            defaultValue: [],
            critical: true,
            persist: true
        },
        {
            name: 'sourceClass',
            type: 'string',
            critical: true,
            persist: true
        },
        {
            name: 'initialExpansionDepth',
            type: 'number',
            defaultValue: 1,
            critical: true,
            persist: true
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            critical: true,
            persist: true
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'dependencytree',
            persist: true,
            critical: true
        },
        {
            name: 'icon_font',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_description',
            type: 'string',
            persist: false
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.DependencyTreeItem',
            name: 'nodes'
        }
    ],

    proxy: {
        type: 'baseproxy',
        url: '/domainTrees',
        extraParams: {
            treeMode: 'tree',
            detailed: true
        },
        pageSize: 0
    },

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('description');
        }
        return this.get('_description_translation') || this.get('description');
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    },

    /**
     *
     * @returns {Array}
     */
    checkDirections: function () {
        const errors = [];
        this.nodes().each(function (serverItem) {
            try {
                let error;
                if (!Ext.isEmpty(serverItem.get('domain'))) {
                    const domainsStore = Ext.getStore('domains.Domains');
                    const serverItemDomain = domainsStore.findRecord('name', serverItem.get('domain'));
                    if (!serverItemDomain) {
                        error = {
                            domain: serverItem.get('domain'),
                            targetClass: serverItem.get('targetClass'),
                            currentDirection: serverItem.get('direction'),
                            error: 'notfound'
                        };
                        errors.push(error);
                    } else {
                        const serverItemTargetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            serverItem.get('direction') === '_1'
                                ? serverItemDomain.get('source')
                                : serverItemDomain.get('destination')
                        );
                        const serverItemTargetHierarchy = serverItemTargetObject.getHierarchy();
                        const serverItemTargetChildren = serverItemTargetObject.getChildren();
                        Ext.Array.forEach(serverItemTargetChildren, function (children) {
                            serverItemTargetHierarchy.push(children.get('name'));
                        });
                        if (serverItemTargetHierarchy.indexOf(serverItem.get('targetClass')) < 0) {
                            error = {
                                domain: serverItem.get('domain'),
                                targetClass: serverItem.get('targetClass'),
                                currentDirection: serverItem.get('direction'),
                                error: 'direction'
                            };
                            errors.push(error);
                        }
                    }
                }
            } catch (error) {
                console.error(error);
            }
        });
        return errors;
    },

    /**
     *
     * @param {*} reason
     * @returns
     */
    fixDirections: function (reason) {
        const deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('fix-treenav-directions');
        Ext.Ajax.request({
            url: Ext.String.format('{0}/{1}/fixDirections', this.getProxy().getUrl(), this.get('name')),
            method: 'POST',
            success: function (response) {
                const res = Ext.JSON.decode(response.responseText);
                deferred.resolve(res);
            },
            failure: function (response) {
                deferred.reject(response);
            }
        });
        return deferred.promise;
    }
});
