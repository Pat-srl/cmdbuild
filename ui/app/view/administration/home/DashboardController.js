Ext.define('CMDBuildUI.view.administration.home.DashboardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-dashboard',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function () {
        const vm = this.getViewModel();
        const store = Ext.getStore('pluginmanager.Plugins');

        if (!store) return;

        const now = Ext.Date.clearTime(new Date());
        const locales = CMDBuildUI.locales.Locales.administration.home;
        let html = '';

        const pluginsExpiring = store
            .getRange()
            .map(function (rec) {
                const exp = rec.get('expirationDate');
                if (!exp) return null;

                const diff = Ext.Date.diff(now, Ext.Date.clearTime(exp, true), Ext.Date.DAY);

                if (diff <= 30) {
                    return { name: rec.get('name'), days: diff };
                }
                return null;
            })
            .filter(Boolean);

        if (!pluginsExpiring.length) return;

        if (pluginsExpiring.length === 1) {
            html = `
                <span>${this.getPluginExpirationMessage(pluginsExpiring[0])}</span>
            `;
        } else {
            html = `
                <span>${Ext.String.format(locales.pluginexpirationcount, pluginsExpiring.length)}</span>
                <ul style="padding-bottom: 0; margin-bottom: 0;">
                    ${Ext.Array.map(
                        pluginsExpiring,
                        function (p) {
                            return `<li>${this.getPluginExpirationMessage(p)}</li>`;
                        },
                        this
                    ).join('')}
                </ul>
            `;
        }

        vm.set({
            isWarningHidden: Ext.isEmpty(html),
            warningHtml: html
        });
    },

    privates: {
        /**
         * @param {Object} plugin
         * @param {String} plugin.name
         * @param {Number} plugin.days days to expiration, negative if already expired
         * @return {String}
         */
        getPluginExpirationMessage: function (plugin) {
            const locales = CMDBuildUI.locales.Locales.administration.home;

            if (plugin.days < -1) {
                return Ext.String.format(locales.pluginexpireditem, plugin.name, Math.abs(plugin.days));
            } else if (plugin.days === -1) {
                return Ext.String.format(locales.pluginexpireditem_yesterday, plugin.name);
            } else if (plugin.days === 0) {
                return Ext.String.format(locales.pluginexpirationitem_today, plugin.name);
            } else if (plugin.days === 1) {
                return Ext.String.format(locales.pluginexpirationitem_tomorrow, plugin.name);
            }
            return Ext.String.format(locales.pluginexpirationitem, plugin.name, plugin.days);
        }
    }
});
