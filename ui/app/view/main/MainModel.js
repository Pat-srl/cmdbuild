/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('CMDBuildUI.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: 'CMDBuildUI',
        isAdministrationModule: null,
        theSession: null,
        isAuthenticated: false,
        isNecessaryReload: false,
        disableAlertBtn: false,
        hideChatBot: true,
        chatBotAiEnabled: false,
        chatBotAiName: '',

        language: {
            default: null,
            showselector: null
        },

        scheduler: {
            enabled: false
        },

        notifications: {
            count: 0,
            hasUnread: false
        },

        chatconversations: {
            count: 0
        }
    },

    formulas: {
        updateFavicon: {
            bind: {
                notifications: '{notifications.hasUnread}',
                chatconversations: '{chatconversations.count}'
            },
            get: function (data) {
                CMDBuildUI.util.Utilities.updateFavicon(data.notifications || data.chatconversations);
            }
        },

        /**
         * Used to determinate if user is authenticated or not.
         */
        isAuthenticated: {
            bind: {
                session: '{theSession}',
                rolePrivileges: '{theSession.rolePrivileges}'
            },
            get: function (data) {
                return !!data.session && !Ext.Object.isEmpty(data.rolePrivileges);
            }
        }
    },

    stores: {
        notificationStore: {
            model: 'CMDBuildUI.model.messages.Notification',
            sorters: [
                {
                    property: 'timestamp',
                    direction: 'DESC'
                }
            ],
            advancedFilter: {
                attributes: {
                    sourceType: {
                        operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        value: ['system']
                    }
                }
            },
            autoLoad: '{isAuthenticated}',
            autoDestroy: false,
            trackRemoved: false,
            remoteSort: true,
            listeners: {
                load: 'onNotificationStoreLoad',
                update: 'onNotificationStoreUpdate',
                remove: 'onNotificationStoreRemove'
            }
        }
    }
});
