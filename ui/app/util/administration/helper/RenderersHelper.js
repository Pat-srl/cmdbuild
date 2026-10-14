Ext.define('CMDBuildUI.util.administration.helper.RendererHelper', {

    requires: [
        'Ext.util.Format'
    ],

    singleton: true,

    getDisplayPassword: function (value) {
        if (value) {
            var pswlength = 6;
            var pswstring = '';
            var i;
            for (i = 0; i < pswlength; i++) {
                pswstring += '•';
            }
            return pswstring;
        }
    },

    getIpType: function (value) {
        if (value) {
            switch (value) {
                case 'ipv4':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.ipv4;
                case 'ipv6':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.ipv6;
                case 'any':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.any;
            }
        }
    },

    getEditorType: function (value, metaData, record, rowIndex, colIndex, store, view) {
        switch (value) {
            case 'HTML':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.editorhtml;
            case 'PLAIN':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.plaintext;
            case 'MARKDOWN':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.editormarkdown;
            default:
                return value;
        }
    },
    getAttributeMode: function (value, metaData, record, rowIndex, colIndex, store, view) {
        switch (value) {
            case 'write':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.editable;
            case 'read':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.readonly;
            case 'hidden':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.hidden;
            case 'immutable':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.immutable;
        }
    },

    getEmailContentType: function (value, metaData, record, rowIndex, colIndex, store, view) {
        // get label from store
        var contetTypesStore = Ext.getStore('administration.emails.ContentTypes');
        var contetTypesRecord = contetTypesStore.findRecord('value', value) || null;
        return contetTypesRecord ? contetTypesRecord.get('label') : '<em>' + value + '</em>';
    },
    disabledCheckboxColumn: function (value, meta) {
        // remember to disable the check  
        /** listeners: {
         *     beforecheckchange: function () {
         *         return false;
         *     }
         *  }
         */
        var defaultRenderer = this.defaultRenderer(value, meta);
        defaultRenderer = defaultRenderer.replace('class="x-grid-checkcolumn', 'class="x-grid-checkcolumn x-item-disabled');
        return defaultRenderer;
    },

    getGeoatributeTypeAndSubype: function (record) {
        var typeValues = CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributeTypes();
        var subtypeValues = CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributeSubtypes();
        var typeDescription = CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(typeValues, record.get('type'));
        var subtypeDescription = CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(subtypeValues, record.get('subtype'));
        var fulldescription = Ext.String.format('{0} {1}', typeDescription,
            subtypeDescription && typeDescription.toLowerCase() !== subtypeDescription.toLowerCase() ?
                Ext.String.format(' - {0}', subtypeDescription) :
                '');
        record.set('_type_description',
            fulldescription
        );
        return fulldescription;
    },
    getGeoatributeType: function (record) {
        var typeValues = CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributeTypes();
        var typeDescription = CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(typeValues, record.get('type'));

        record.set('_type_description',
            typeDescription
        );
        return typeDescription;
    },

    getMenuTargetDevice: function (value) {
        var devices = CMDBuildUI.util.administration.helper.ModelHelper.getMenuTargetDevices();
        return CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(devices, value);
    },
    getCustomUITargetDevice: function (value) {
        var devices = CMDBuildUI.util.administration.helper.ModelHelper.getCustomUITargetDevices();
        return CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(devices, value);
    },
    getBulkSettingsComboLabel: function (value) {
        var data = CMDBuildUI.util.helper.ModelHelper.bulkComboSettingsData();
        return CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(data, value);
    },
    getBulkUiConfigurationsComboLabel: function (value) {
        var data = CMDBuildUI.util.helper.ModelHelper.bulkComboPermissionsData();
        if (Ext.isEmpty(value)) {
            value = 'null';
        }
        return CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(data, value + '');
    },
    getCustomeCodeHelp: function () {
        return CMDBuildUI.locales.Locales.administration.systemconfig.customercodehelp;
    },
    getDeviceNamePrefixHelp: function () {
        return CMDBuildUI.locales.Locales.administration.systemconfig.devicenameprefixhelp;
    },
    getMobileAuthenticationInfo: function () {
        return CMDBuildUI.locales.Locales.administration.systemconfig.mobilenotificationauthinfo;
    },

    getSearchfieldInGridsOptionsLabel: function (value, withDefault) {
        var data = CMDBuildUI.util.administration.helper.ModelHelper.getSearchfieldInGridsOptions(withDefault);
        return CMDBuildUI.util.administration.helper.RendererHelper.getLabelFromArray(data, value + '');
    },

    privates: {

        getLabelFromArray: function (values, value) {
            var arrayItem = Ext.Array.findBy(values, function (item) {
                try {
                    return item.value === value || item.value === JSON.parse(value);
                } catch (e) {
                    return false;
                }
            });
            if (arrayItem) {
                return arrayItem.label;
            }
            return value;
        }
    }

});