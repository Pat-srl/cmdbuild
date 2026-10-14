Ext.define('CMDBuildUI.view.widgets.createmodifycard.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-createmodifycard-panel',

    data: {
        addbtn: {
            disabled: true
        }
    },

    formulas: {
        updateData: {
            bind: {
                klassdescription: '{klassdescription}'
            },
            get: function(data) {
                this.set("addbtn.text", Ext.String.format("{0} {1}", CMDBuildUI.locales.Locales.classes.cards.addcard, data.klassdescription));
            }
        }
    }

});
