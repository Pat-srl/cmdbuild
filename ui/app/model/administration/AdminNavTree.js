Ext.define('CMDBuildUI.model.administration.AdminNavTree', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: ['CMDBuildUI.validator.TrimPresence'],
    fields: [
        {
            name: 'name',
            type: 'string',
            validators: [
                'trimpresence'
                // {
                //     type: 'format',
                //     matcher:  /^(?![_0-9])[a-zA-Z0-9-_]+$/,
                //     message: Ext.String.format(CMDBuildUI.locales.Locales.administration.common.messages.cantcontainchar, '_ (underscore)')
                // }
            ],
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
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            critical: true,
            persist: true
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'default',
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
            model: 'CMDBuildUI.model.administration.AdminNavTreeItem',
            name: 'nodes'
        }
    ],
    proxy: {
        type: 'baseproxy',
        url: '/domainTrees',
        extraParams: {
            treeMode: 'flat'
        },
        pageSize: 0
    },

    checkDirections: function () {
        var errors = [];
        this.nodes().each(function (serverItem) {
            try {
                var error;
                if (!Ext.isEmpty(serverItem.get('domain'))) {
                    var domainsStore = Ext.getStore('domains.Domains');
                    var serverItemDomain = domainsStore.findRecord('name', serverItem.get('domain'));
                    if (!serverItemDomain) {
                        error = {
                            domain: serverItem.get('domain'),
                            targetClass: serverItem.get('targetClass'),
                            currentDirection: serverItem.get('direction'),
                            error: 'notfound'
                        };
                        errors.push(error);
                    } else {
                        var serverItemTargetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            serverItem.get('direction') === '_1'
                                ? serverItemDomain.get('source')
                                : serverItemDomain.get('destination')
                        );
                        var serverItemTargetHierarchy = serverItemTargetObject.getHierarchy();
                        var serverItemTargetChildren = serverItemTargetObject.getChildren();
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

    fixDirections: function (reason) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('fix-treenav-directions');
        Ext.Ajax.request({
            url: Ext.String.format('{0}/{1}/fixDirections', this.getProxy().getUrl(), this.get('name')),
            method: 'POST',
            success: function (response) {
                var res = Ext.JSON.decode(response.responseText);
                deferred.resolve(res);
            },
            failure: function (response) {
                deferred.reject(response);
            }
        });
        return deferred.promise;
    }
});
