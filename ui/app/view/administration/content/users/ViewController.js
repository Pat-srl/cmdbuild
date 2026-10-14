Ext.define('CMDBuildUI.view.administration.content.users.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        Ext.getStore('localizations.Languages').setAutoLoad(true);
        this.manageIgnoreTenantsMsg(view);
    },
    onAllUsersStoreDatachanged: function (store, records) {
        var counter = this.getView().down('#userGridCounter');
        counter.setHtml(Ext.String.format(CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords, null, null, store.totalCount));
    },

    privates: {
        manageIgnoreTenantsMsg: function (view) {
            var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();

            // add change tenant action
            if (session && session.get("canIgnoreTenants") && !session.get("ignoreTenants")) {
                view.insert(0, {
                    region: 'north',
                    margin: 10,
                    ui: 'messagewarning',
                    xtype: 'container',
                    layout: 'hbox',
                    items: [{
                        flex: 1,
                        ui: 'custom',
                        xtype: 'panel',
                        html: CMDBuildUI.locales.Locales.administration.users.messages.ignoretenantswarningmsg
                    }, {
                        xtype: 'button',
                        ui: 'administration-warning-action-small',
                        text: CMDBuildUI.locales.Locales.administration.users.messages.ignoretentnantsbtntext,
                        listeners: {
                            click: function () {
                                CMDBuildUI.util.Msg.confirm(
                                    CMDBuildUI.locales.Locales.notifier.attention,
                                    CMDBuildUI.locales.Locales.administration.users.messages.confirmenabletenant,
                                    function (btnText) {
                                        if (btnText === "yes") {
                                            session.set("ignoreTenants", true);
                                            session.save({
                                                success: function () {
                                                    window.location.reload();
                                                }
                                            });
                                        }
                                    }, this);
                            }
                        }
                    }]
                });
            }
        }
    }

});