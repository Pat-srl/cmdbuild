Ext.define('Override.form.field.Date', {
    requires: ['CMDBuildUI.util.helper.UserPreferences', 'CMDBuildUI.util.helper.Configurations'],
    override: 'Ext.picker.Date',

    /**
     * @override
     * 
     */
    initComponent: function () {
        var me = this;
        if (CMDBuildUI.util.helper.UserPreferences._preferences) {
            me.startDay = CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startDay) || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.startDay) || Ext.Date.firstDayOfWeek;
        }
        me.callParent(arguments);
    }
});