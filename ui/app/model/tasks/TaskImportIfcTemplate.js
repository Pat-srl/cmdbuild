Ext.define('CMDBuildUI.model.tasks.TaskImportIfcTemplate', {
    extend: 'CMDBuildUI.model.tasks.TaskImportExport',

    /**
     * @override
     */
    fields: [{
        name: 'config',
        critical: true,
        persist: true,
        reference: {
            type: 'CMDBuildUI.model.tasks.TaskImportIfcTemplateConfig',
            unique: true
        }
    }]
});


