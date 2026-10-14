Ext.define('CMDBuildUI.view.administration.home.widgets.userstats.StatsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-userstats-stats',
    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addUsersTool': {
            click: 'onAddUsersToolClick',
            destroy: 'onDestroyTool'
        }
    },

    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    },

    onAddUsersToolClick: function (tool, e, owner, eOpts) {
        if (tool.menu) {
            tool.menu.show();
        } else {
            var me = this;
            tool.menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                items: [{
                    text: CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text'
                    },
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('user', 'solid'),
                    // height: 32,
                    listeners: {
                        click: function (menuitem, _eOpts) {
                            me.redirectTo('administration/users');
                            Ext.asap(function () {
                                var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                                container.removeAll();
                                container.add({
                                    xtype: 'administration-content-users-card-create',
                                    viewModel: {
                                        links: {
                                            theUser: {
                                                type: 'CMDBuildUI.model.users.User',
                                                create: {
                                                    changePasswordRequired: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.defaultchangepasswordfirstlogin)
                                                }
                                            }
                                        }
                                    }
                                });
                            });
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup'
                    },
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
                    listeners: {
                        click: function (menuitem, _eOpts) {
                            me.redirectTo('administration/groupsandpermissions_empty/true');
                        }
                    }
                }]
            });

            tool.menu.alignTo(tool.el.id, 'tr-br?');
        }
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Object} eOpts 
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    }

});