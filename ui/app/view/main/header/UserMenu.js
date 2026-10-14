Ext.define('CMDBuildUI.view.main.header.UserMenu', {
    extend: 'Ext.button.Button',

    requires: ['CMDBuildUI.view.main.header.UserMenuController', 'CMDBuildUI.view.main.header.UserMenuModel'],

    alias: 'widget.main-header-usermenu',
    controller: 'main-header-usermenu',
    viewModel: {
        type: 'main-header-usermenu'
    },

    ui: 'header',

    cls: 'user-menu',
    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sign-out-alt', 'solid'),

    bind: {
        text: '{text}'
    },

    ariaAttributes: {
        'aria-expanded': false,
        'aria-haspopup': true,
        'aria-label': CMDBuildUI.locales.Locales.arialabels.usermenu
    },

    listeners: {
        menushow: function () {
            this.ariaEl.dom.setAttribute('aria-expanded', 'true');
        },
        menuhide: function () {
            this.ariaEl.dom.setAttribute('aria-expanded', 'false');
        }
    }
});
