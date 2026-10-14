Ext.define('CMDBuildUI.view.administration.content.emails.accounts.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-accounts-topbar',

    control: {
        '#addaccount': {
            click: 'onNewAccountBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewAccountBtnClick: function (item, event, eOpts) {        
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);        
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-accounts-card-create',
            viewModel: {
                links: {
                    theAccount: {
                        type: 'CMDBuildUI.model.emails.Account',
                        create: true
                    }
                }               
            }
        });

    }   
});