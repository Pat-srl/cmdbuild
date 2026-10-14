Ext.define('CMDBuildUI.view.navcontent.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.navcontent.ContainerController',
        'CMDBuildUI.view.navcontent.ContainerModel'
    ],

    config: {
        navTreeName: undefined
    },

    alias: 'widget.navcontent-container',
    controller: 'navcontent-container',
    viewModel: {
        type: 'navcontent-container'
    },

    iconAlign: 'right',
    layout: 'fit',

    listeners: {
        titlechange: function (panel, newTitle, oldTitle, eOpts) {
            var title = panel.down("title"),
                icon = title.iconEl;
            if (icon && Ext.Object.isEmpty(icon.hasListeners)) {
                title.flex = 0;
                icon.dom.style.margin = "0px 10px";
                icon.addListener("click", function (event, htmlElement, eOpts) {
                    panel.fireEvent("clickIconTitle");
                });
                icon.addListener("mouseover", function (event, htmlElement, eOpts) {
                    htmlElement.style.cursor = "pointer";
                });
            }
        }
    }

});