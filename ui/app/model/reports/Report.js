Ext.define('CMDBuildUI.model.reports.Report', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        extensions: {
            csv: 'csv',
            odt: 'odt',
            pdf: 'pdf',
            rtf: 'rtf'
        },

        processingmodes: {
            batch: 'batch',
            realtime: 'realtime'
        }
    },

    mixins: ['CMDBuildUI.mixins.model.Attribute'],

    fields: [
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
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: 'processing',
            type: 'string'
        },
        {
            name: 'config',
            type: 'auto',
            critical: true,
            defaultValue: {}
        },
        {
            name: 'icon_font',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_description',
            type: 'string',
            persist: false
        }
    ],

    proxy: {
        url: '/reports/',
        type: 'baseproxy'
    },

    hasMany: [
        {
            name: 'attributes',
            model: 'CMDBuildUI.model.Attribute'
        }
    ],

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
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('code');
    },

    /**
     * @return {String} domains url
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Reports.getReportAttributesUrlByReportId(this.getId());
    },

    /**
     * @override
     *
     * @param {Object} options
     * @return {Ext.data.operation.Create/Ext.data.operation.Update/Ext.data.operation.Destroy}
     */
    save: function (options) {
        this.set('config.processing', this.get('config___processing'));
        return this.callParent(arguments);
    }
});
