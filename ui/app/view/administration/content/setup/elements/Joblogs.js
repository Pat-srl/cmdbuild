Ext.define('CMDBuildUI.view.administration.content.setup.elements.Joblogs', {
    extend: 'CMDBuildUI.view.administration.content.bus.messages.Grid',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.JoblogsController'        ,
        'CMDBuildUI.view.administration.content.setup.elements.JoblogsModel'        
    ],
    alias: 'widget.administration-content-setup-elements-joblogs',
    controller: 'administration-content-setup-elements-joblogs',
    viewModel: {       
        type: 'administration-content-setup-elements-joblogs'
    }
});