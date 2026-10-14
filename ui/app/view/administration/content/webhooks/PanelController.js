Ext.define('CMDBuildUI.view.administration.content.webhooks.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-webhooks-panel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        },

        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * @param {MDBuildUI.view.administration.content.webhooks.Panell} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "grid", null /** label */, [{
            xtype: 'administration-content-webhooks-grid',
            itemId: 'webhookgrid'
        }], 0, { disabled: '{disabledTabs.grid}' });
    },

    /**
     * @param {MDBuildUI.view.administration.content.webhooks.Panel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.webhooks', this, view, newtab, oldtab, eOpts);
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     */
    onAddBtnClick: function (btn) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                links: {
                    theWebhook: {
                        type: 'CMDBuildUI.model.webhooks.Webhook',
                        create: true
                    }
                },
                data: {
                    grid: btn.up('tabpanel').down('grid'),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    }
});
