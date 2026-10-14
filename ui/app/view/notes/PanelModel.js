Ext.define('CMDBuildUI.view.notes.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.notes-panel',

    data: {
        editmode: false,
        hiddenbtns: {
            cancel: true,
            edit: true,
            save: true
        }
    },

    formulas: {
        updatePermissions: {
            bind: {
                editmode: '{editmode}'
            },
            get: function(data) {
                var view = this.getView();
                if (data.editmode) {
                    this.set("hiddenbtns.cancel", false);
                    this.set("hiddenbtns.save", false);
                    this.set("hiddenbtns.edit", true);
                } else {
                    this.set("hiddenbtns.cancel", true);
                    this.set("hiddenbtns.save", true);
                    if (!view.getReadOnly()) {
                        this.set("hiddenbtns.edit", false);
                    }
                }
            }
        }
    }

});
