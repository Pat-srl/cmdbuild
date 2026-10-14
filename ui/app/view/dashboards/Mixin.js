Ext.define('CMDBuildUI.view.dashboards.Mixin', {
    mixinId: 'dashboards-mixin',

    /**
     * Returns the appropriate form field editor for a given dashboard parameter.
     *
     * @param {CMDBuildUI.model.dashboards.Parameter} parameter - The dashboard parameter model instance.
     * @param {Boolean} [isGlobalParameter=false] - Whether the parameter is a global parameter (hidden in the form).
     * @param {Object} [options] - Additional editor options.
     * @return {Ext.form.Field} The form field component configured for the given parameter.
     */
    getEditorForParameter: function (parameter, isGlobalParameter = false, options) {
        const type = parameter.get('type').toLowerCase();
        let editor;
        options = options || {};
        // base editor config
        const field = {
            name: CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(parameter.get('name')),
            hidden: isGlobalParameter,
            cmdbuildtype: parameter.get('type'),
            attributeconf: { attributename: parameter.get('name') },
            mandatory: parameter.get('required'),
            emptyText: parameter.get('_emptyText_translation') || parameter.get('emptyText'),
            writable: true,
            description: parameter.get('_description_translation') || parameter.get('description'),
            getDescription: function () {
                return this.description;
            }
        };
        const editorconfig = {
            linkName: options.linkName || 'parameters',
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.update
        };

        if (parameter.get('defaultValue')) {
            editorconfig.defaultValue = { value: parameter.get('defaultValue') };
        }

        // customize for string and integer types
        if (
            type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer ||
            type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint
        ) {
            switch (parameter.get('fieldType').toLowerCase()) {
                case 'card':
                    const target = parameter.get('classToUseForReferenceWidget');
                    field.cmdbuildtype = CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey;
                    field.attributeconf.preselectIfUnique = true;
                    field.attributeconf.targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(target);
                    field.attributeconf.targetClass = target;
                    break;
                case 'lookup':
                    field.attributeconf.preselectIfUnique = parameter.get('preselectIfUnique');
                    field.cmdbuildtype = CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup;
                    field.attributeconf.lookupType = parameter.get('lookupType');
                    break;
            }
            if (!Ext.isEmpty(parameter.get('ecqlFilter'))) {
                field.attributeconf.ecqlFilter = parameter.get('ecqlFilter');
            }
        } else if (type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string) {
            switch (parameter.get('fieldType').toLowerCase()) {
                case 'classes':
                    editor = CMDBuildUI.util.helper.FormHelper.getFormField(field, editorconfig);
                    Ext.merge(editor, {
                        xtype: 'combobox',
                        valueField: 'name',
                        displayField: '_description_translation',
                        autoLoadOnValue: false,
                        queryMode: 'local',
                        bind: { store: '{classes}' }
                    });
                    break;
            }
        }
        if (!editor) {
            editor = CMDBuildUI.util.helper.FormHelper.getFormField(field, editorconfig);
        }
        return editor;
    }
});
