Ext.define('CMDBuildUI.util.administration.helper.AjaxHelper', {
    requires: ['Ext.Ajax', 'CMDBuildUI.util.Config'],
    singleton: true,
    /**
     * Drop cache
     *
     */

    dropCache: function () {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-dropcache');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getDropCacheUrl(),
            method: 'POST',
            success: function (transport) {
                deferred.resolve();
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Update of menu for gropus
     *
     * @argument {Number} menuId
     * @argument {CMDBuildUI.model.menu.Menu} data
     */
    getMenuForGroup: function (menuId, data) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-updatemenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(menuId),
            method: 'GET',
            success: function (transport) {
                var response = Ext.JSON.decode(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Update of menu for gropus
     *
     * @argument {Number} menuId
     * @argument {CMDBuildUI.model.menu.Menu} data
     */
    updateMenuForGroup: function (menuId, data) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-updatemenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(menuId), // CMDBuildUI.util.Config.baseUrl + '/menu/' + menuId,
            method: 'PUT',
            jsonData: data,
            success: function (transport) {
                var response = Ext.JSON.decode(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Create menu for groups
     *
     *
     */
    createMenuForGroup: function (data) {
        var deferred = new Ext.Deferred();

        CMDBuildUI.util.Ajax.setActionId('administration-createmenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(),
            method: 'POST',
            jsonData: data,
            params: {
                regenerateNodeCodes: false
            },
            success: function (transport) {
                var response = Ext.JSON.decode(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Save groups for filters
     */
    setGroupsForFilter: function (filterId, data) {
        var deferred = new Ext.Deferred();

        var promises = [];
        Ext.Array.forEach(data, function (element) {
            promises.push(CMDBuildUI.util.administration.helper.AjaxHelper.getGrantsOfGroup(element._id));
        });
        Ext.Ajax.request({
            url: Ext.String.format('{0}/classes/_ANY/filters/{1}/defaultFor', CMDBuildUI.util.Config.baseUrl, filterId),
            method: 'POST',
            jsonData: data,
            success: function (transport) {
                // check grants
                Ext.Promise.all(promises).then(function (roleGrants) {
                    var groupStore = Ext.getStore('groups.Groups');
                    var groupsWithNonePermission = [];
                    var grantsWithNonePermission = [];
                    Ext.Array.forEach(roleGrants, function (grants) {
                        var grant = Ext.Array.findBy(grants, function (grant) {
                            if (grant.objectType === 'filter') {
                                return Ext.String.format(grant.objectTypeName) === Ext.String.format(filterId);
                            }
                            return false;
                        });
                        if (grant.mode === 'none') {
                            groupsWithNonePermission.push(groupStore.findRecord('_id', grant.role));
                            grantsWithNonePermission.push(grant);
                        }
                    });
                    var response = Ext.JSON.decode(transport.responseText).data;
                    if (!groupsWithNonePermission.length) {
                        deferred.resolve(response);
                    } else {
                        var descriptions = [];
                        Ext.Array.forEach(groupsWithNonePermission, function (group) {
                            descriptions.push(
                                Ext.String.format('<li><strong>{0}</strong></li>', group.get('description'))
                            );
                        });
                        Ext.MessageBox.show({
                            title: CMDBuildUI.locales.Locales.administration.common.messages.attention,
                            message: Ext.String.format(
                                CMDBuildUI.locales.Locales.administration.searchfilters.texts.grantpermissions,
                                Ext.String.format('<ul>{0}</ul>', descriptions.join(''))
                            ),
                            buttons: Ext.Msg.YESNO,
                            icon: Ext.Msg.QUESTION,
                            buttonText: {
                                yes: CMDBuildUI.locales.Locales.administration.common.actions.yes,
                                no: CMDBuildUI.locales.Locales.administration.common.actions.no
                            },
                            fn: function (buttonText) {
                                if (buttonText === 'yes') {
                                    var _promises = [];
                                    Ext.Array.forEach(grantsWithNonePermission, function (element) {
                                        element.mode = 'read';
                                        promises.push(
                                            CMDBuildUI.util.administration.helper.AjaxHelper.setFilterPermissionForGroup(
                                                element
                                            )
                                        );
                                    });
                                    Ext.Promise.all(_promises).then(function (roleGrants) {
                                        deferred.resolve();
                                    });
                                } else {
                                    deferred.resolve(response);
                                }
                            }
                        });
                    }
                });
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    getGrantsOfGroup: function (groupId) {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/roles/{1}/grants?includeObjectDescription=true&includeRecordsWithoutGrant=true&ext=true',
                CMDBuildUI.util.Config.baseUrl,
                groupId
            ),
            method: 'GET',
            success: function (response) {
                var _response = Ext.JSON.decode(response.responseText).data;
                deferred.resolve(_response);
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });

        return deferred.promise;
    },

    /**
     * Save filter permission (grant) on group
     */
    setFilterPermissionForGroup: function (data) {
        var deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: Ext.String.format('{0}/roles/{1}/grants/_ANY', CMDBuildUI.util.Config.baseUrl, data.role),
            method: 'POST',
            jsonData: Ext.Array.from(data),
            success: function (transport) {
                var response = Ext.JSON.decode(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function (e) {
                deferred.reject(e);
            }
        });

        return deferred.promise;
    },

    runJob: function (record) {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: Ext.String.format('{0}/jobs/{1}/run', CMDBuildUI.util.Config.baseUrl, record.get('code')),
            method: 'POST',
            success: function (response) {
                var res = Ext.JSON.decode(response.responseText);
                deferred.resolve(res);
            },
            error: function (response) {
                deferred.resolve(true);
            }
        });
        return deferred.promise;
    },

    unlockAllCards: function (button) {
        var deferred = new Ext.Deferred();

        if (button) {
            button.setDisabled(true);
        }
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);

        Ext.Ajax.request({
            url: Ext.String.format('{0}/locks/_ANY', CMDBuildUI.util.Config.baseUrl),
            method: 'DELETE',
            success: function (response) {
                deferred.resolve();

                if (button && button.el.dom) {
                    button.setDisabled(false);
                }
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);

                CMDBuildUI.util.Notifier.showSuccessMessage(
                    CMDBuildUI.locales.Locales.administration.common.messages.allcardsunlocked,
                    null,
                    'administration'
                );
            },
            callback: function () {
                if (button && button.el.dom) {
                    button.setDisabled(false);
                }
            }
        });

        return deferred.promise;
    },

    getMenuTranslations: function (group, device, originId, newId) {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/translations/{1}',
                CMDBuildUI.util.Config.baseUrl,
                CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(
                    group,
                    device,
                    originId
                )
            ),
            method: 'GET',
            success: function (transport) {
                var response = Ext.JSON.decode(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function (error) {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    setMenuTranslations: function (locale) {
        var deferred = new Ext.Deferred();
        var _id = locale._id;
        delete locale.default;
        delete locale._id;
        Ext.Ajax.request({
            url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, _id),
            method: 'PUT',
            jsonData: locale,
            success: function (transport) {
                deferred.resolve(transport);
            },
            failure: function (error) {
                deferred.reject();
            }
        });

        return deferred.promise;
    }
});
