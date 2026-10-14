Ext.define('CMDBuildUI.view.dashboards.common.components.search.Search', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.search.SearchController',
        'CMDBuildUI.view.dashboards.common.components.search.SearchModel'
    ],

    alias: 'widget.dashboards-components-search-search',
    controller: 'dashboards-components-search-search',
    viewModel: {
        type: 'dashboards-components-search-search'
    },

    tools: [],

    layout: {
        type: 'hbox',
        align: 'middle',
        pack: 'start'
    },

    initComponent: function () {
        const me = this;
        const isAiSearch =
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.homepage.type) === 'ai';

        me.header = false;
        me.addCls(Ext.baseCSSPrefix + 'dashboard-component-no-background');

        me.items = [
            me.getWelcomeConfig(),
            {
                xtype: 'component',
                flex: 1
            },
            me.getSearchFieldConfig(isAiSearch)
        ];

        me.callParent(arguments);
    },

    privates: {
        /**
         * @override
         * @returns {null}
         */
        getComponentHeight: function () {
            return null;
        },

        /**
         * Returns the salutation matching the current time of day.
         *
         * @returns {String}
         */
        getSalutation: function () {
            const locales = CMDBuildUI.locales.Locales.homepage.search;
            const hour = new Date().getHours();

            if (hour < 6) {
                return locales.goodnight;
            }
            if (hour < 13) {
                return locales.goodmorning;
            }
            if (hour < 17) {
                return locales.goodafternoon;
            }
            return locales.goodevening;
        },

        /**
         * Config of the welcome message (logo/icon + salutation + user name).
         * All dynamic values are escaped by the template.
         *
         * @returns {Object}
         */
        getWelcomeConfig: function () {
            const session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();

            return {
                xtype: 'component',
                cls: 'dashboard-welcome-container',
                tpl: [
                    '<tpl if="image">',
                    '<img src="{image:htmlEncode}" class="welcome-icon" alt="Logo" />',
                    '<tpl else>',
                    '<i class="fa fa-user-circle welcome-icon"></i>',
                    '</tpl>',
                    '<span class="welcome-text">',
                    '{salutation:htmlEncode}, <strong class="welcome-username">{username:htmlEncode}</strong>!',
                    '</span>'
                ],
                data: {
                    salutation: this.getSalutation(),
                    username: session.get('userDescription'),
                    image: this.getItemDefinition().get('logo') || null
                }
            };
        },

        /**
         * Config of the search text field.
         *
         * @param {Boolean} isAiSearch
         * @returns {Object}
         */
        getSearchFieldConfig: function (isAiSearch) {
            const actions = CMDBuildUI.locales.Locales.common.actions;

            const config = {
                xtype: 'textfield',
                name: 'search',
                width: 400,
                emptyText: isAiSearch ? actions.searchaitext : actions.searchtext,
                reference: 'searchtext',
                itemId: 'searchtext',
                cls: 'management-input homepage-search-input',
                bind: {
                    value: '{search.value}',
                    disabled: '{search.disabled}',
                    hidden: '{search.hidden}'
                },
                triggers: {
                    search: {
                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                        handler: 'onSearchTriggerClick'
                    },
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClearClick'
                    }
                },
                listeners: {
                    specialkey: 'onSearchSpecialKey'
                }
            };

            if (!isAiSearch) {
                config.localized = {
                    emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
                };
            }

            return config;
        }
    }
});
