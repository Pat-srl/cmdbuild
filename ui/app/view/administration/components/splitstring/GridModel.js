Ext.define('CMDBuildUI.view.administration.components.splitstring.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-splitstring-grid',
    data: {
    },

    formulas: {

        updateStoreVariables: {
            get: function () {
                var theMessage = this.get('theMessage');
                var theDivisor = this.get('theDivisor');
                if (theMessage && theDivisor) {
                    var splitString = theMessage.split(theDivisor);
                    var resList = [];
                    splitString.forEach(function (substring) {
                        resList.push({
                            substring: substring
                        });
                    });
                    return resList;
                }else{
                    return [];
                }
            }
        }
    },

    stores: {
        splitStringStore: {
            model: 'CMDBuildUI.model.base.SplitString',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{updateStoreVariables}'
        }
    }
});