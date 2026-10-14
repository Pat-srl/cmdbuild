Ext.define('CMDBuildUI.view.administration.content.reports.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-reports-tabpanel',
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
        }
    },
    formulas: {
        tabManager: {
            bind: '{action}',
            get: function (action) {
                if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                    this.toggleEnableTabs(0);
                }
            }
        },
        componentTypeName: {
            bind: '{theReport}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.reports.singular;
            }
        },
        panelTitle: {
            bind: {
                theReport: '{theReport}',
                description: '{theReport.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    CMDBuildUI.locales.Locales.administration.reports.plural,
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