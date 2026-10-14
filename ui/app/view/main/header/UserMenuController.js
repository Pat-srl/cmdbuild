Ext.define('CMDBuildUI.view.main.header.UserMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-usermenu',

    control: {
        '#': {
            show: 'onAfterRender'
        },
        '#preferences': {
            click: 'onPreferencesClick'
        },
        '#configuremobile': {
            click: 'onConfigureMobileClick'
        },
        '#changepassword': {
            click: 'onChangePasswordClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.main.header.UserMenu} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
            availableTenantsLength = session.get("availableTenantsExtendedData").length;

        var menu = [];
        // Add user info
        menu.push({
            text: session.get("userDescription") || session.get("username") || Ext.String.format('<i>User</i>'),
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('user', 'solid'),
            disabled: true,
            autoEl: {
                'data-testid': 'usermenu-user'
            }
        }, {
            xtype: 'menuseparator'
        });

        var addseparator = false;
        // add change roles action
        if (session && session.get("availableRolesExtendedData").length > 1) {
            menu.push({
                text: CMDBuildUI.locales.Locales.main.changegroup,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
                autoEl: {
                    'data-testid': 'usermenu-changegroup'
                },
                menu: this.getGroupMenu(session)
            });
            addseparator = true;
        }
        // add change tenant action
        if (session && (availableTenantsLength > 1 || session.get("canIgnoreTenants"))) {
            var itemTenants = {
                text: Ext.String.format(
                    CMDBuildUI.locales.Locales.main.changetenant,
                    CMDBuildUI.util.Utilities.getTenantLabel()
                ),
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                reference: 'changetenant',
                itemId: 'changetenant',
                autoEl: {
                    'data-testid': 'usermenu-changetenant'
                }
            }

            if (availableTenantsLength > 20) {
                itemTenants.handler = function (item, event) {
                    CMDBuildUI.util.Utilities.openPopup(
                        null,
                        Ext.String.format(CMDBuildUI.locales.Locales.main.changetenant, CMDBuildUI.util.Utilities.getTenantLabel()),
                        {
                            xtype: 'tenants-menu'
                        }
                    );
                }
            } else {
                itemTenants.menu = this.getTenantsMenu(session);
            }

            menu.push(itemTenants);
            addseparator = true;
        }
        // add separator
        if (addseparator) {
            menu.push({
                xtype: 'menuseparator'
            });
        }
        // add preferences action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.userpreferences,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
            reference: 'preferences',
            itemId: 'preferences',
            autoEl: {
                'data-testid': 'usermenu-preferences'
            }
        });
        // add change password
        if (session) {
            menu.push({
                text: CMDBuildUI.locales.Locales.main.password.change,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('key', 'solid'),
                reference: 'changepassword',
                itemId: 'changepassword',
                hidden: true,
                bind: {
                    hidden: '{changepasswordHidden}'
                },
                autoEl: {
                    'data-testid': 'usermenu-changepassword'
                }
            });
        }
        // mobile configuartion
        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled) &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.customercode)
        ) {
            menu.push({
                text: CMDBuildUI.locales.Locales.mobile.config.configure,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.mobile.config.configure'
                },
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('mobile-alt', 'solid'),
                itemId: 'configuremobile',
                autoEl: {
                    'data-testid': 'usermenu-configuremobile'
                }
            });
        }
        // add administration module action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.administrationmodule,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
            reference: 'administration',
            itemId: 'administration',
            href: '#administration',
            hidden: true,
            bind: {
                hidden: '{!isAdministrator}'
            },
            autoEl: {
                'data-testid': 'usermenu-administration'
            }
        });
        // add data management module action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.managementmodule,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
            reference: 'management',
            itemId: 'management',
            href: '#',
            hidden: true,
            bind: {
                hidden: '{!isAdministrationModule}'
            },
            autoEl: {
                'data-testid': 'usermenu-management'
            }
        });
        // add preferences action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.logout,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sign-out-alt', 'solid'),
            reference: 'logout',
            itemId: 'logout',
            autoEl: {
                'data-testid': 'usermenu-logout'
            }
        });
        view.setMenu(menu);
    },

    /**
     * creates a menu with the available roles
     * @param {CMDBuildUI.model.users.Session} session
     */
    getGroupMenu: function (session) {
        var currentRole = session.get('role'),
            roles = session.get('availableRolesExtendedData'),
            multigroup = session.get("multigroup"),
            group = [];

        roles.forEach(function (element) {
            group.push({
                text: element._description_translation || element.description,
                itemId: element.code,
                disabled: multigroup || element.code === currentRole,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
                handler: 'changeGroupClick'
            });
        });

        Ext.Array.sort(group, function (a, b) {
            return a.text.toUpperCase() < b.text.toUpperCase() ? -1 : 1;
        });

        return group;
    },

    /**
     * 
     * @param {CMDBuildUI.model.users.Session} session 
     */
    getTenantsMenu: function (session) {
        this.activetenants = Ext.Array.clone(session.get('activeTenants')).sort();

        var me = this,
            menuitems = [],
            availabletenants = session.get('availableTenantsExtendedData'),
            ignoretenants = session.get("ignoreTenants"),
            isMultitenant = session.get("multiTenantActivationPrivileges") !== "one";

        // add ignore tenants option
        if (session.get("canIgnoreTenants")) {
            menuitems.push({
                xtype: 'menucheckitem',
                text: Ext.String.format(
                    CMDBuildUI.locales.Locales.main.ignoretenants,
                    CMDBuildUI.util.Utilities.getTenantLabel()
                ),
                checked: ignoretenants,
                isIgnoreTenantsCheck: true,
                listeners: {
                    checkchange: function (checkitem, checked, eOpts) {
                        var text;
                        if (checked) {
                            text = CMDBuildUI.locales.Locales.main.confirmenabletenant;
                        } else {
                            text = CMDBuildUI.locales.Locales.main.confirmdisabletenant;
                        }
                        CMDBuildUI.util.Msg.confirm(
                            CMDBuildUI.locales.Locales.notifier.attention,
                            text,
                            function (btnText) {
                                if (btnText === "yes") {
                                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                                    session.set("ignoreTenants", checked);
                                    session.save({
                                        success: function () {
                                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                            window.location.reload();
                                        },
                                        failure: function () {
                                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                        }
                                    });
                                }
                            }, this);
                    }
                }
            });
            if (availabletenants.length) {
                // add separator
                menuitems.push({
                    xtype: 'menuseparator'
                });
            }
        }

        availabletenants.forEach(function (tenant) {
            var isCurrent = Ext.Array.contains(me.activetenants, tenant.code),
                menuitem = {
                    text: tenant.description,
                    tenantCode: tenant.code,
                    disabled: ignoretenants || (!isMultitenant && isCurrent)
                };

            if (isMultitenant) {
                menuitem = Ext.merge(menuitem, {
                    xtype: 'menucheckitem',
                    checked: isCurrent,
                    startvalue: isCurrent,
                    listeners: {
                        checkchange: function (checkitem, checked, eOpts) {
                            if (checked) {
                                Ext.Array.include(me.activetenants, tenant.code);
                            } else {
                                Ext.Array.remove(me.activetenants, tenant.code);
                            }
                        }
                    }
                });
            } else {
                menuitem = Ext.merge(menuitem, {
                    xtype: 'menuitem',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                    handler: function (item, e, eOpts) {
                        me.activetenants = [item.tenantCode];
                        me.onTenantMenuHide();
                    }
                });
            }
            menuitems.push(menuitem);
        });

        if (!ignoretenants && isMultitenant && availabletenants.length > 1) {
            menuitems.push({
                xtype: 'menuseparator'
            }, {
                xtype: 'menucheckitem',
                text: CMDBuildUI.locales.Locales.main.selectdeselettenants,
                checked: me.activetenants.length === availabletenants.length,
                listeners: {
                    checkchange: function (checkitem, checked, eOpts) {
                        checkitem.parentMenu.items.items.forEach(function (i) {
                            if (!i.isIgnoreTenantsCheck) {
                                i.setChecked && i.setChecked(checked);
                            }
                        });
                    }
                }
            });
        }

        var menu = {
            items: menuitems
        }

        if (isMultitenant) {
            menu.listeners = {
                hide: 'onTenantMenuHide'
            };
        }
        return menu;
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Event} e
     * @param {Object} eOpts
     */
    changeGroupClick: function (item, e, eOpts) {
        var theSession = this.getViewModel().get('theSession');
        var role = item.getItemId();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.main.confirmchangegroup,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    theSession.set('role', role);
                    theSession.save({
                        success: function () {
                            theSession.commit();
                            CMDBuildUI.util.Utilities.redirectTo("");
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            window.location.reload();
                        },
                        failure: function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }, this);
    },

    /**
     * 
     * @param {Ext.panel.Panel} panel 
     * @param {Object} eOpts 
     */
    onTenantMenuHide: function (panel, eOpts) {
        var me = this;
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        if (!Ext.Array.equals(session.get("activeTenants").sort(), this.activetenants.sort())) {
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                CMDBuildUI.locales.Locales.main.confirmchangetenants,
                function (btnText) {
                    if (btnText === "yes") {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        CMDBuildUI.util.helper.SessionHelper.updateActiveTenants(this.activetenants);
                        session.save({
                            success: function () {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                window.location.reload();
                            },
                            failure: function () {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            }
                        });
                    } else {
                        this.activetenants = Ext.Array.clone(session.get("activeTenants"));
                        var items = me.getView().getMenu().items.getByKey("changetenant").getMenu().getRefItems();
                        items.forEach(function (m) {
                            if (!m.isIgnoreTenantsCheck) {
                                m.setChecked ? m.setChecked(m.startvalue) : null;
                            }
                        });
                    }
                }, this);
        }
    },

    onChangePasswordClick: function (item, e, eOpts) {

        var title = CMDBuildUI.locales.Locales.main.password.change;
        var config = {
            xtype: 'login-changepassword-form',
            listeners: {
                passwordchange: function (view) {
                    view.up("panel").close();
                }
            }
        };
        CMDBuildUI.util.Utilities.openPopup('popup-change-password', title, config, null, {
            width: '450px',
            height: '350px'
        });
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPreferencesClick: function (item, e, eOpts) {
        CMDBuildUI.util.Utilities.openPopup('UserPreferences', CMDBuildUI.locales.Locales.main.userpreferences, {
            xtype: 'main-header-preferences'
        });
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Event} e
     * @param {Object} eOpts
     */
    onConfigureMobileClick: function (item, e, eOpts) {
        CMDBuildUI.util.Utilities.openPopup(null, item.text, {
            xtype: 'mobile-config-panel'
        }, null, {
            width: (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.popupwindow.width) / 2) + "%",
            height: (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.popupwindow.height) / 1.5) + "%"
        });
    }
});