Ext.define('CMDBuildUI.view.administration.content.emails.signatures.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-signatures-topbar',

    control: {
        '#addsignature': {
            click: 'onNewSignatureBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewSignatureBtnClick: function (item, event, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-signatures-card-card',
            viewModel: {
                links: {
                    theSignature: {
                        type: 'CMDBuildUI.model.emails.Signature',
                        create: true
                    }
                },
                data: {
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