Ext.define('CMDBuildUI.view.bim.xeokit.tab.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.bim-xeokit-tab-card',
    data: {
        nameClass: null,
        entity: null
    },

    formulas: {
        activeCard: {
            bind: {
                entity: '{entity}'
            },
            get: function (data) {
                if (data.entity) {
                    var mappingInfo = data.entity.mappingInfo;
                    this.set("enabledTabs.card", mappingInfo.exists);
                    this.set("nameClass", CMDBuildUI.util.helper.ModelHelper.getClassDescription(mappingInfo.ownerType));
                }
            }
        }
    }

});