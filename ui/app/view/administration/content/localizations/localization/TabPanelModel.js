Ext.define('CMDBuildUI.view.administration.content.localizations.localization.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-localization-tabpanel',
    data: {
        actions: {
            view: true,
            edit: false
        },
        toolAction: {
            _canUpdate: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_localization_modify}'
            },
            get: function (data) {
                this.set('toolAction._canUpdate', data.canModify === true);
            }
        },
        updateActiveTab: {
            bind: '{activeTab}',
            get: function (data) {
                this.getView().setActiveTab(data);
            }
        },
        menuInfomessage: function () {
            return CMDBuildUI.locales.Locales.administration.localizations.menuinfomessage;
        }
    },

    stores: {
        languages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format(
                    '{0}/languages?active=true',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            pageSize: 0,
            listeners: {
                load: 'onstoreLoaded'
            }
        }

    },
    /**
     * Enable/disable tabs
     * @param {Number} currentTabIndex 
     */
    toggleEnableTabs: function (currentTabIndex) {
        var me = this;
        var view = me.getView();
        var tabs = view.items.items;

        if (me.get('actions.view')) {
            tabs.forEach(function (tab) {
                tab.enable();
            });
        } else
        if (me.get('actions.edit')) {
            tabs.forEach(function (tab) {
                if (tab.tabConfig.tabIndex !== currentTabIndex) {
                    tab.disable();
                } else {
                    tab.enable();
                }
            });

        }
    }

});