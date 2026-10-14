Ext.define('CMDBuildUI.view.administration.content.searchfilters.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-searchfilters-tabpanel',
    data: {
        activeTab: 0,
        disabledTabs: {
            properties: false,
            permissions: false
        },

        actions: {
            view: false,
            edit: true,
            add: false
        }
    },
    formulas: {
        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
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
                    CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
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