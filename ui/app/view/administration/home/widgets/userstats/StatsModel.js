Ext.define('CMDBuildUI.view.administration.home.widgets.userstats.StatsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-userstats-stats',

    data: {
        userGroupData: [],
        userData: []
    },

    formulas: {
        initData: function () {
            var me = this;
            me.set('showLoader', true);

            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/functions/_cm3_dashboard_user_group_session/outputs',
                method: "GET",
                timeout: 0
            }).then(function (response, opts) {
                if (!me.destroyed) {

                    var responseJson = Ext.JSON.decode(response.responseText, true),
                        userData = [],
                        userStandard = {},
                        userService = {},
                        group = {},
                        session = {};

                    Ext.Array.forEach(responseJson.data, function (item) {
                        var count = item.count,
                            itemSplit = item.type.split("_"),
                            type = itemSplit.length === 3 ? itemSplit[1] : itemSplit[0];
                        switch (type) {
                            case 'standard':
                                userStandard.label = CMDBuildUI.locales.Locales.administration.home.usersstandard;
                                if (Ext.String.endsWith(item.type, "nonactive")) {
                                    userStandard.countInactive = count;
                                    userData.push({
                                        label: CMDBuildUI.locales.Locales.administration.home.usersstandardinactive,
                                        type: 'standardna',
                                        index: 1,
                                        count: count
                                    });
                                } else {
                                    userStandard.countActive = count;
                                    userData.push({
                                        label: CMDBuildUI.locales.Locales.administration.home.usersstandardactive,
                                        type: 'standarda',
                                        index: 0,
                                        count: count
                                    });
                                }
                                break;
                            case 'service':
                                userService.label = CMDBuildUI.locales.Locales.administration.home.userservice;
                                if (Ext.String.endsWith(item.type, "nonactive")) {
                                    userService.countInactive = count;
                                    userData.push({
                                        label: CMDBuildUI.locales.Locales.administration.home.userserviceinactive,
                                        type: 'servicena',
                                        index: 3,
                                        count: count
                                    });
                                } else {
                                    userService.countActive = count;
                                    userData.push({
                                        label: CMDBuildUI.locales.Locales.administration.home.userserviceactive,
                                        type: 'servicea',
                                        index: 2,
                                        count: count
                                    });
                                }
                                break;
                            case 'group':
                                group.label = CMDBuildUI.locales.Locales.administration.home.groups;
                                Ext.String.endsWith(item.type, "nonactive") ? group.countInactive = count : group.countActive = count;
                                break;
                            case 'session':
                                session.label = CMDBuildUI.locales.Locales.administration.home.sessions;
                                session.countActive = count;
                                break;
                            default:
                                break;
                        }
                    });

                    var maxValue = Ext.Array.max(Ext.Array.pluck(userData, "count")),
                        max = Math.floor(maxValue + Math.ceil(maxValue / 15));

                    me.set('userData', userData);
                    me.set('userGroupData', [userStandard, userService, group, session]);
                    me.set('showLoader', false);
                    me.getView().down("#statsChart").getAxes()[1].setMaximum(max);
                }
            }, function () {
                if (!me.destroyed) {
                    me.set('showLoader', false);
                }
            });
        }

    },

    stores: {
        userGroupStats: {
            proxy: 'memory',
            fields: [{
                type: 'string',
                name: 'label'
            }, {
                type: 'integer',
                name: 'countActive'
            }, {
                type: 'integer',
                name: 'countInactive'
            }],
            data: '{userGroupData}'
        },

        userStats: {
            proxy: 'memory',
            sorters: [{
                property: 'index',
                direction: 'DESC'
            }],
            fields: [{
                type: 'string',
                name: 'label'
            }, {
                type: 'integer',
                name: 'count'
            }],
            data: '{userData}'
        }
    }

});