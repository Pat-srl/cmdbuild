Ext.define('CMDBuildUI.model.dms.DMSCategory', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        icontypes: {
            none: 'none',
            image: 'image'
        },

        /**
         * Load category values for given Lookup Type
         * @param {String} type
         * @param {Number|String} id
         * @return {CMDBuildUI.model.dms.DMSCategory}
         */
        getCategoryValueById: function (type, id) {
            const lt = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(type);
            return lt.values().getById(id);
        },

        /**
         * Load category values for given Category Type
         * @param {String} type
         * @param {String} code
         * @return {CMDBuildUI.model.dms.DMSCategory}
         */
        getLookupValueByCode: function (type, code) {
            const lt = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(type);
            return lt.values().findRecord('code', code);
        }
    },

    fields: [{
        name: '_type',
        type: 'string',
        critical: true
    }, {
        name: 'code',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: '_description_translation',
        type: 'string',
        persist: false
    }, {
        name: 'text',
        type: 'string',
        calculate: function (data) {
            return data._description_translation || data.description;
        }
    }, {
        name: 'number',
        type: 'integer',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        critical: true
    }, {
        name: 'default',
        type: 'boolean',
        critical: true
    }, {
        name: 'parent_id',
        type: 'string',
        critical: true
    }, {
        name: 'parent_description',
        type: 'string',
        critical: true
    }, {
        name: 'parent_type',
        type: 'string',
        critical: true
    }, {
        name: 'note',
        type: 'string',
        critical: true
    }, {
        name: 'icon_type',
        type: 'string',
        defaultValue: 'none',
        critical: true
    }, {
        name: 'icon_image',
        type: 'string',
        critical: true
    }, {
        name: 'index',
        type: 'number',
        critical: true
    }, {
        name: 'modelClass',
        type: 'string',
        defaultValue: 'BaseDocument',
        critical: true,
        persist: true
    }, {
        name: 'allowedExtensions',
        type: 'string',
        defaultValue: '',
        critical: true
    }, {
        name: 'checkCount',
        type: 'string',
        defaultValue: '', // no_check, at_least_number, exactly_number, max_number
        critical: true
    }, {
        name: 'checkCountNumber',
        type: 'string', // ignored if checkCount = no_check
        critical: true
    }, {
        name: 'maxFileSize',
        type: 'string',
        critical: true,
        defaultValue: null
    }],

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
            return this.get("description");
        }
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * @return {HTML}
     */
    getFormattedDescription: function () {
        var output = "";

        // get parent description
        if (this.get("parent_type") && this.get("parent_id")) {
            const categoryvalue = CMDBuildUI.model.dms.DMSCategory.getCategoryValueById(this.get("parent_type"), this.get("parent_id"));
            output = categoryvalue ? categoryvalue.getFormattedDescription() + " / " : "";
        }

        // get font icon
        if (this.get("icon_type") == "font" && this.get("icon_font")) {
            const icon_font_splitted = this.get('icon_font').split(':');
            const icon_font = CMDBuildUI.util.helper.IconHelper.getIconId(icon_font_splitted[0], icon_font_splitted.length > 1 ? icon_font_splitted[1] : '');
            output += '<span class="' + icon_font + '" ';
            if (this.get("icon_color")) {
                output += 'style="color: ' + this.get("icon_color") + ';" ';
            }
            output += '></span> ';
        }

        // get font image
        if (this.get("icon_type") == "image" && this.get("icon_image")) {
            output += Ext.String.format(
                '<img src="{0}" style="max-width: 16px; max-height: 16px; vertical-align: text-bottom;" /> ',
                this.get("icon_image")
            );
        }

        // value description
        const txt = this.get("_description_translation") || this.get("description");
        if (this.get("text_color")) {
            output += Ext.String.format(
                '<span style="color: {0};">{1}</span>',
                this.get("text_color"),
                txt
            );
        } else {
            output += txt;
        }
        return output;
    }
});