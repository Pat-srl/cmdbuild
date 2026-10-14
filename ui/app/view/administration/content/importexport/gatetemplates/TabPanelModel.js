Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-gatetemplates-tabpanel',
    data: {
        activeTab: 0,
        disabledTabs: {
            properties: false,
            templates: false,
            importon: false,
            permissions: false
        },
        enabledTab: null
    },

    formulas: {
        setEnabledTabs: {
            bind: '{enabledTab}',
            get: function (enabledTab) {
                if (enabledTab) {
                    this.set('disabledTabs.properties', enabledTab !== 'properties');
                    this.set('disabledTabs.templates', enabledTab !== 'templates');
                    this.set('disabledTabs.importon', enabledTab !== 'importon');
                    this.set('disabledTabs.permissions', enabledTab !== 'permissions');
                } else {
                    this.set('disabledTabs.properties', false);
                    this.set('disabledTabs.templates', false);
                    this.set('disabledTabs.importon', false);
                    this.set('disabledTabs.permissions', true);
                }
            }
        },

        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                this.set(
                    'activeTab',
                    this.getView().up('administration-content').getViewModel().get('activeTabs.gatetemplate') || 0
                );
                if (data.isView) {
                    this.set('formModeCls', 'formmode-view');
                    this.set('enabledTab', null);
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    this.set('enabledTab', 'properties');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.getParent().set(
                    'actions.view',
                    value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                );
                this.getParent().set(
                    'actions.edit',
                    value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                );
                this.getParent().set(
                    'actions.add',
                    value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                );
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
