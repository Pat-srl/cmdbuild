
Ext.define('CMDBuildUI.view.events.event.View', {
    extend: 'Ext.form.Panel',
    alias: 'widget.events-event-view',
    requires: [
        'CMDBuildUI.view.events.event.ViewController',
        'CMDBuildUI.view.events.event.ViewModel'
    ],

    mixins: [
        'CMDBuildUI.view.events.event.Mixin'
    ],

    controller: 'events-event-view',
    viewModel: {
        type: 'events-event-view'
    },

    config: {
        hideTools: false,

        /**
        * @cfg {Boolean} shownInPopup
        * Set to true get inline form.
        */
        shownInPopup: false
    },

    bind: {
        title: '{title}'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,

    tabpaneltools: CMDBuildUI.view.events.Util.getTools(),

    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    items: [],

    /**
     * same function in CMDBuildUI.view.fields.schedulerdate.ViewController
     */
    generateForm: function () {
        const model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
        this.readonly = true;

        const panel = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            readonly: true,
            linkName: 'theEvent',
            showAsFieldsets: true,
            layout: this.getFormLayout(),
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.read
        });

        const missingDays = panel[0]._cmdbuildFields[this._missingdays_row_index].items[1].items[0];
        Ext.apply(missingDays, this.getMissingDaysExtraConf());

        // adds the partecipants combobox
        panel[0]._cmdbuildFields.splice(this._partecipantGroup_row_index, 0, this.getUserGroupParticipantsFields());

        /**
         * if want to enable/disable notification template field in view, uncomment/comment the code below lined
         */
        // //adds the notification_template combo
        // var notificationtemplate = this.getNotificationTemplateComboField();
        // panel[0].items[this._notification_delay_row_index].items[0].items.push(notificationtemplate);

        // adds the daysAdvanceNotification text
        panel[0]._cmdbuildFields[this._notification_delay_row_index].items[0].items.push(this.getDaysAdvanceNofificationFieldRead());

        if (!this.getHideTools()) {
            // add toolbar
            const toolbar = {
                xtype: 'toolbar',
                cls: 'fieldset-toolbar',
                items: Ext.Array.merge([{
                    xtype: 'tbfill'
                }], this.tabpaneltools)
            };
            Ext.Array.insert(panel, 0, [toolbar]);
        }

        this.removeAll();
        this.add(this.getMainPanelForm(panel));
    }
});