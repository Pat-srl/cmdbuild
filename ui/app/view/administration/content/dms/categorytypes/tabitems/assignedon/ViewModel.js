Ext.define('CMDBuildUI.view.administration.content.dms.categorytypes.tabitems.assignedon.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-categorytypes-tabitems-assignedon-view',
    data: {
        assignedClassesCounter: 0,
        assignedProcessesCounter: 0,
        defaultCategoryMessage: null
    },
    formulas: {
        classesFieldsetTitle: {
            bind: '{assignedClassesCounter}',
            get: function (assignesClassesCounter) {
                return Ext.String.format('{0} ({1})', CMDBuildUI.locales.Locales.administration.classes.title, assignesClassesCounter);
            }
        },
        processesFieldsetTitle: {
            bind: '{assignedProcessesCounter}',
            get: function (assignedProcessesCounter) {
                return Ext.String.format('{0} ({1})', CMDBuildUI.locales.Locales.administration.processes.title, assignedProcessesCounter);
            }
        },
        assignedonClassesAndProcessFilter: {
            bind: {
                theDMSCategoryType: '{theDMSCategoryType}'
            },
            get: function (data) {                
                if(data.theDMSCategoryType.get('name') === CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category)){
                    this.set('defaultCategoryMessage', CMDBuildUI.locales.Locales.administration.dmscategories.defaultcategory);
                    return [function (item) {                    
                        return item.get('dmsCategory') === '';
                    }];
                }else{
                    return [function (item) {                    
                        return item.get('dmsCategory') === data.theDMSCategoryType.get('name');
                    }];
                }
            }
        },
        countersManager: {
            bind: {
                assignedonClassesStore: '{assignedonClassesStore}',
                assignedonProcessesStore: '{assignedonProcessesStore}'
            },
            get: function (data) {
                if (data.assignedonClassesStore) {
                    this.set('assignedClassesCounter', data.assignedonClassesStore.getRange().length);
                }
                if (data.assignedonProcessesStore) {
                    this.set('assignedProcessesCounter', data.assignedonProcessesStore.getRange().length);
                }
            }
        }
    },
    stores: {
        assignedonClassesStore: {
            source: 'classes.Classes',
            filters: '{assignedonClassesAndProcessFilter}'
        },

        assignedonProcessesStore: {
            source: 'processes.Processes',
            filters: '{assignedonClassesAndProcessFilter}'
        }
    }
});