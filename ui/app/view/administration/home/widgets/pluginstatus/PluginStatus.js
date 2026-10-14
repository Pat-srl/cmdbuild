Ext.define('CMDBuildUI.view.administration.home.widgets.pluginstatus.PluginStatus', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.administration-home-widgets-pluginstatus-pluginstatus',
    title: CMDBuildUI.locales.Locales.administration.home.pluginstatus.title,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.pluginstatus.title'
    },
    disableSelection: true,
    ui: 'admindashboard',
    forceFit: true,
    bind: {
        store: '{plugins}'
    },
    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.home.pluginstatus.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.pluginstatus.description'
            },
            dataIndex: 'description',
            flex: 1
        },
        {
            text: CMDBuildUI.locales.Locales.administration.home.pluginstatus.version,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.pluginstatus.version'
            },
            dataIndex: 'version',
            flex: 0.5
        },
        {
            text: CMDBuildUI.locales.Locales.administration.home.pluginstatus.status,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.pluginstatus.status'
            },
            dataIndex: 'status',
            flex: 0.5,
            renderer: function (value) {
                const statuses = CMDBuildUI.model.pluginmanager.Plugins.statuses;
                const translated = statuses[value] || value;
                const mapping = {
                    ready: 'x-pluginTag ready',
                    error: 'x-pluginTag error'
                };

                const cls = mapping[value] || 'x-pluginTag';

                return `<span class="${cls}">${translated}</span>`;
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.home.pluginstatus.expiration,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.pluginstatus.expiration'
            },
            dataIndex: 'expirationDate',
            flex: 0.5,
            renderer: function (value) {
                if (!value) {
                    return CMDBuildUI.locales.Locales.administration.home.pluginstatus.noexpiration;
                }
                return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
            }
        }
    ]
});
