Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-panel',

    onPartFocus: function (input) {
        var vm = input.lookupViewModel();
        vm.set('activeTab', input.relatedTab);
    }
});