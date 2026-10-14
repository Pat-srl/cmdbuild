Ext.define('CMDBuildUI.view.administration.content.custompages.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-custompages-tabpanel',
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
        custompageLabel: {
            bind: '{theCustompage}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.custompages.singular;
            }
        },
        panelTitle: {
            bind: {
                theReport: '{theCustompage}',
                description: '{theCustompage.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    CMDBuildUI.locales.Locales.administration.custompages.plural,
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