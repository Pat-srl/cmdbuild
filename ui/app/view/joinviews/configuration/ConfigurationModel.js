Ext.define('CMDBuildUI.view.joinviews.configuration.ConfigurationModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-configuration',

    data: {
        activeTab: null,
        theView: null,
        toolAction: {
            _canAdd: false
        }
    },

    formulas: {
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_views_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify);
            }
        },

        theViewManager: {
            bind: '{theView}',
            get: function (theView) {
                const me = this;
                if (this.get('showForm') === 'false') {
                    const title = this.get('isAdministrationModule')
                        ? CMDBuildUI.locales.Locales.administration.navigation.views +
                          ' - ' +
                          CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin
                        : theView.phantom
                        ? CMDBuildUI.locales.Locales.joinviews.newjoinview
                        : CMDBuildUI.locales.Locales.joinviews.joinview;
                    this.set('panelTitle', title);
                }

                Ext.asap(function () {
                    me.set('activeTab', me.get('activeTabs.joinView') || 0);
                });
            }
        },

        panelTitleManager: {
            bind: '{panelTitle}',
            get: function (panelTitle) {
                this.getParent().set('title', panelTitle);
            }
        }
    }
});
