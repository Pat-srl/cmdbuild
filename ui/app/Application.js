/**
 * The main application class. An instance of this class is created by app.js when it
 * calls Ext.application(). This is the ideal place to handle application launch and
 * initialization details.
 */
Ext.define('CMDBuildUI.Application', {
    extend: 'Ext.app.Application',
    requires: [
        // grid features
        'Ext.grid.feature.*',

        // validators
        'Ext.data.validator.*',

        // mixins
        'CMDBuildUI.mixins.*',

        // models
        'CMDBuildUI.model.*',

        // stores
        'CMDBuildUI.store.*',

        // helpers
        'CMDBuildUI.util.*',

        // components
        'CMDBuildUI.components.*',

        // views
        'CMDBuildUI.view.*',

        // locales
        'CMDBuildUI.locales.Locales',

        // charts
        'Ext.chart.*',

        // extra form fields
        'Ext.ux.form.*'
    ],

    name: 'CMDBuildUI',

    stores: [
        'icons.Icons',
        'users.Tenants',
        'groups.Grants',
        'groups.Groups',
        'classes.Classes',
        'calendar.Triggers',
        'menu.Menu',
        'menu.NavigationTrees',
        'administration.MenuAdministration',
        'administration.common.WidgetTypes',
        'administration.common.Applicability',
        'administration.emails.Queue',
        'administration.emails.ContentTypes',
        'emails.Accounts',
        'emails.Templates',
        'emails.Signatures',
        'searchfilters.Searchfilters',
        'importexports.Templates',
        'importexports.Gates',
        'importexports.GateTemplates',
        'Functions',
        'tasks.Tasks',
        'processes.Processes',
        'reports.Reports',
        'dashboards.Dashboards',
        'homepages.Homepages',
        'views.Views',
        'custompages.CustomPages',
        'customcomponents.ContextMenus',
        'customcomponents.Widgets',
        'customcomponents.Scripts',
        'domains.Domains',
        'lookups.LookupTypes',
        'bim.Projects',
        'navigationtrees.NavigationTrees',
        'localizations.LocalizationsByCode',
        'dms.DMSModels',
        'dms.DMSCategoryTypes',
        'localizations.Languages',
        'pluginmanager.Plugins',
        'dependencyTrees.DependencyTrees',
        'ai.Messages'
    ],

    launch: function () {
        /**
         * Global initializations to improve the application's accessibility.
         *
         * - Sets the language of the HTML document (`lang` attribute)
         *   Required for screen readers and WCAG 2.1 – Success Criterion 3.1.1 (Language of Page)
         *
         * - Configures the viewport meta tag to ensure proper zoom and scaling
         *   Supports WCAG 2.1 – Success Criteria 1.4.4 (Resize text) and 1.4.10 (Reflow)
         *
         * - Hides Ext JS internal tab-guard elements from assistive technologies
         *   Maintains keyboard navigation while preventing ARIA validation issues
         *   Related to WCAG 2.1 – Success Criterion 2.1.1 (Keyboard)
         */
        (function initAccessibility() {
            const metaViewport = document.querySelector('meta[name="viewport"]');
            if (metaViewport) {
                metaViewport.setAttribute('content', 'width=device-width, initial-scale=1');
            }

            const hideTabGuardsFromAT = function () {
                Ext.select('.x-tab-guard').each(function (el) {
                    if (!el.dom.hasAttribute('aria-hidden')) {
                        el.dom.setAttribute('aria-hidden', 'true');
                    }
                });
            };

            hideTabGuardsFromAT();
            Ext.GlobalEvents.on('afterlayout', hideTabGuardsFromAT);
        })();

        const me = this;

        CMDBuildUI.util.helper.SessionHelper.updateStartingUrlWithCurrentUrl();

        // Initialize Ajax
        CMDBuildUI.util.Ajax.init();
        CMDBuildUI.util.Ajax.installAdministrationPrefixInterceptor();

        // check for session
        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity({ operator: true })
            .then(function (session) {
                // if session is valid load configurations and preferences to get language informations
                Ext.Promise.all([
                    CMDBuildUI.util.helper.Configurations.loadSystemConfs(),
                    CMDBuildUI.util.helper.UserPreferences.load()
                ]).then(function (responses) {
                    Ext.Promise.all([
                        CMDBuildUI.util.helper.SessionHelper.loadLocale(
                            CMDBuildUI.util.helper.SessionHelper.getLanguage(true)
                        ),
                        CMDBuildUI.util.helper.SessionHelper.loadCustomLocale()
                    ]).then(function () {
                        me.setMainView('CMDBuildUI.view.main.Main');
                        CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport(session);
                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                        CMDBuildUI.util.helper.Configurations.updateEnabledFeatures();
                        Ext.GlobalEvents.fireEvent('setTooltips');
                    });
                });
            })
            .otherwise(function (session) {
                // if session is not valid load public configurations to get language informations
                CMDBuildUI.util.helper.Configurations.loadPublicConfs().then(function () {
                    CMDBuildUI.util.helper.SessionHelper.loadLocale(
                        CMDBuildUI.util.helper.SessionHelper.getLanguage(false)
                    ).then(function () {
                        me.setMainView('CMDBuildUI.view.main.Main');
                        if (session && session.isModel) {
                            CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport(session);
                        }
                        CMDBuildUI.util.helper.Configurations.updateConfigsInViewport();
                        Ext.GlobalEvents.fireEvent('setTooltips');
                    });
                });
            });

        if (!String.prototype.includes) {
            String.prototype.includes = function () {
                'use strict';
                return String.prototype.indexOf.apply(this, arguments) !== -1;
            };
        }

        window.onbeforeunload = function confirmExit(e) {
            if (CMDBuildUI.util.helper.FormHelper.isFormSaving()) {
                e.preventDefault();
                const message = 'The application is still saving data. Are you sure you want to leave it?';
                e.returnValue = message;
                return message;
            }
        };
    },

    onAppUpdate: function () {
        const path = window.location.pathname.replace(/\/ui(_dev)?/, '');
        Ext.util.Cookies.clear(CMDBuildUI.util.helper.SessionHelper.localization, path);
        Ext.MessageBox.show({
            title: CMDBuildUI.locales.Locales.administration.common.messages.applicationupdate,
            message: CMDBuildUI.locales.Locales.administration.common.messages.applicationreloadquest,
            closeToolText: CMDBuildUI.locales.Locales.common.actions.close,
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            buttonText: {
                yes: CMDBuildUI.locales.Locales.administration.common.actions.yes,
                no: CMDBuildUI.locales.Locales.administration.common.actions.no
            },
            fn: function (buttonText) {
                if (buttonText === 'yes') {
                    window.location.reload();
                }
            }
        });
    }
});
