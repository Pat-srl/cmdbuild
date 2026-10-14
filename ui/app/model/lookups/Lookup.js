Ext.define('CMDBuildUI.model.lookups.Lookup', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        icontypes: {
            none: 'none',
            image: 'image',
            font: 'font'
        },

        getIconTypes: function () {
            return [
                {
                    value: CMDBuildUI.model.lookups.Lookup.icontypes.none,
                    label: CMDBuildUI.locales.Locales.administration.common.strings.none
                },
                {
                    value: CMDBuildUI.model.lookups.Lookup.icontypes.font,
                    label: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.font
                },
                {
                    value: CMDBuildUI.model.lookups.Lookup.icontypes.image,
                    label: CMDBuildUI.locales.Locales.administration.common.strings.image
                }
            ];
        },

        /**
         * Load lookup values for given Lookup Type
         * @param {String} type
         * @param {Number|String} id
         * @return {CMDBuildUI.model.lookups.Lookup}
         */
        getLookupValueById: function (type, id) {
            const lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            return lt.values().getById(id);
        },

        /**
         * Load lookup values for given Lookup Type
         * @param {String} type
         * @param {String} code
         * @return {CMDBuildUI.model.lookups.Lookup}
         */
        getLookupValueByCode: function (type, code) {
            const lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            return lt.values().findRecord('code', code);
        }
    },

    fields: [
        {
            name: '_type',
            type: 'string',
            critical: true
        },
        {
            name: 'code',
            type: 'string',
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true
        },
        {
            name: '_description_translation',
            type: 'string',
            persist: false
        },
        {
            name: 'text',
            type: 'string',
            calculate: function (data) {
                return data._description_translation || data.description;
            }
        },
        {
            name: 'number',
            type: 'integer',
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            critical: true
        },
        {
            name: 'default',
            type: 'boolean',
            critical: true
        },
        {
            name: 'parent_id',
            type: 'string',
            critical: true
        },
        {
            name: 'parent_description',
            type: 'string',
            critical: true
        },
        {
            name: 'parent_type',
            type: 'string',
            critical: true
        },
        {
            name: 'note',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_type',
            type: 'string',
            defaultValue: 'none',
            critical: true
        },
        {
            name: 'icon_image',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_font',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_color',
            type: 'string',
            critical: true
        },
        {
            name: 'text_color',
            type: 'string',
            critical: true
        },
        {
            name: 'index',
            type: 'number',
            critical: true
        }
    ],

    proxy: {
        type: 'baseproxy'
    },

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('description');
        }
        return this.get('_description_translation') || this.get('description');
    },

    /**
     * @return {HTML}
     */
    getFormattedDescription: function (showOnlyIconIfPresent) {
        let output = '';

        // get parent description
        if (this.get('parent_type') && this.get('parent_id')) {
            const lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(
                this.get('parent_type'),
                this.get('parent_id')
            );
            output = lookupvalue ? lookupvalue.getFormattedDescription() + ' / ' : '';
        }

        const txt = this.get('_description_translation') || this.get('description');
        const qtipAttr = showOnlyIconIfPresent ? Ext.String.format('data-qtip="{0}" ', Ext.String.htmlEncode(txt)) : '';

        // get font icon
        if (this.get('icon_type') == 'font' && this.get('icon_font')) {
            const icon_font_splitted = this.get('icon_font').split(':');
            const icon_font = CMDBuildUI.util.helper.IconHelper.getIconId(
                icon_font_splitted[0],
                icon_font_splitted.length > 1 ? icon_font_splitted[1] : ''
            );
            output += '<span class="' + icon_font + '" ';
            if (this.get('icon_color')) {
                output += 'style="color: ' + this.get('icon_color') + ';" ';
            }
            output += qtipAttr;
            output += '></span> ';
        }

        // get font image
        if (this.get('icon_type') == 'image' && this.get('icon_image')) {
            output += Ext.String.format(
                '<img src="{0}" style="max-width: 16px; max-height: 16px; vertical-align: text-bottom;" {1}/> ',
                this.get('icon_image'),
                qtipAttr
            );
        }

        if (!showOnlyIconIfPresent) {
            if (this.get('text_color')) {
                output += Ext.String.format('<span style="color: {0};">{1}</span>', this.get('text_color'), txt);
            } else {
                output += txt;
            }
        }
        return output;
    }
});
