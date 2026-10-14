Ext.define('CMDBuildUI.view.main.notifications.MenuListModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-notifications-menulist',

    formulas: {
        emptyTextMessage: {
            get: function() {
                return Ext.String.format(
                    '<div class="{0}notification-empty-message"><p><span class="{1} fa-3x"></span></p>{2}</div>',
                    Ext.baseCSSPrefix,
                    CMDBuildUI.util.helper.IconHelper.getIconId('info-circle', 'solid'),
                    CMDBuildUI.locales.Locales.notifications.emptymessage
                );
            }
        }
    }
});
