Ext.define('CMDBuildUI.model.tasks.TaskImportGisTemplate', {
    extend: 'CMDBuildUI.model.tasks.TaskImportExport',

    /**
     * @override
     */
    fields: [{
        name: 'config',
        critical: true,
        persist: true,
        reference: {
            type: 'CMDBuildUI.model.tasks.TaskImportGisTemplateConfig',
            unique: true
        }
    }]
});


