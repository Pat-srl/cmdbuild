Ext.define('CMDBuildUI.view.administration.content.views.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-views-tabpanel',
    data: {
        activeTab: 0,
        disabledTabs: {
            properties: false,
            permissions: false
        },
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: false,
            edit: true,
            add: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_views_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        tabManager: {
            bind: '{action}',
            get: function (action) {
                if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                    this.toggleEnableTabs(0);
                }
            }
        },
        custompageLabel: {
            bind: '{theViewFilter}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.custompages.singular;
            }
        },
        panelTitle: {
            bind: {
                theViewFilter: '{theViewFilter}',
                description: '{theViewFilter.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    CMDBuildUI.locales.Locales.administration.navigation.views,
                    data.description ? ' - ' : '',
                    data.description
                );
                this.getParent().set('title', title);
            }
        }

    },
    /**
    * 
    * @param {Number} currrentTabIndex 
    */
    toggleEnableTabs: function (currrentTabIndex) {
        var me = this;
        var view = me.getView();
        var tabs = view.items.items;

        tabs.forEach(function (tab) {
            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                me.set('disabledTabs.' + tab.reference, true);
            }
        });

    }
});