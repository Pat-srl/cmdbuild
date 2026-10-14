
Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.Tree',{
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.TreeController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.TreeModel'
    ],
    alias: 'widget.administration-content-importexport-gatetemplates-card-tabitems-importon-tree',
    controller: 'administration-content-importexport-gatetemplates-card-tabitems-importon-tree',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-card-tabitems-importon-tree'
    },

    layout: 'fit',
    viewConfig: {
        markDirty: false
    },
    ui: 'administration-navigation-tree',
    bind: {
        store: '{classesTreeStoreStore}'
    },
    rootVisible: false,
    columns: [{
        xtype: 'checkcolumn',
        width: 75,        
        dataIndex: 'enabled',
        bind: {
            disabled: '{actions.view}'
        },                    
        listeners: {
            checkchange: function (column, recordIndex, checked) {
                var theGate = this.getView()
                    .lookupViewModel()
                    .get('theGate');
                var importOnClasses = theGate.get('importOn');                
                var storeRecordName = this.getView().getStore().getAt(recordIndex).get('name');
                var importOnClassesIndex = importOnClasses.indexOf(storeRecordName);
                if (checked && importOnClassesIndex === -1) {
                    importOnClasses.push(storeRecordName);
                } else if (!checked && importOnClassesIndex > -1) {
                    importOnClasses.splice(importOnClassesIndex, 1);
                }
                theGate.set('importOn', importOnClasses);
            }
        }
    },{
        xtype: 'treecolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
        },
        dataIndex: 'text',
        align: 'left',
        flex: 1
    }]
});
