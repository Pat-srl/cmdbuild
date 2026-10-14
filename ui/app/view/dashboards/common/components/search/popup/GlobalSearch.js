Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch', {
    extend: 'Ext.window.Window',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearchController',
        'CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearchModel',
        'CMDBuildUI.view.dashboards.common.components.search.popup.ClassesResultsGrid',
        'CMDBuildUI.view.dashboards.common.components.search.popup.AiResults',
        'CMDBuildUI.view.dashboards.common.components.search.popup.NoResults'
    ],

    alias: 'widget.homepage-globalsearch',
    controller: 'homepage-globalsearch',
    viewModel: {
        type: 'homepage-globalsearch'
    },

    statics: {
        MIN_QUERY_LENGTH: 3
    },

    config: {
        /**
         * @cfg {String} searchMode `default` (search on classes) or `ai`.
         */
        searchMode: 'default',

        /**
         * @cfg {Function} searchModel Model of the `Class` superclass (default mode).
         */
        searchModel: null,

        /**
         * @cfg {Object[]} searchClasses Classes to search on (default mode).
         */
        searchClasses: null
    },

    ui: 'management',
    alwaysOnTop: true,
    resizable: true,
    draggable: true,
    modal: true,
    constrain: true,
    frame: false,
    shadow: false,
    border: false,
    closeAction: 'destroy',
    height: '80%',
    width: '80%',
    layout: 'card',

    bind: {
        title: '{title}'
    },

    tbar: [
        {
            xtype: 'button',
            ui: 'management-neutral-action-small',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-left', 'solid'),
            handler: 'onBackToSearchClick',
            hidden: true,
            bind: {
                hidden: '{!hasDetail}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.back'
            }
        },
        {
            xtype: 'textfield',
            flex: 1,
            cls: 'management-input homepage-search-input',
            bind: {
                value: '{searchText}',
                emptyText: '{searchEmptyText}'
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
        }
    ],

    initComponent: function () {
        const me = this;

        if (me.getSearchMode() === 'ai') {
            me.items = [{ xtype: 'homepage-globalsearch-airesults' }];
        } else {
            me.items = [
                {
                    xtype: 'container',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'homepage-globalsearch-classesresultsgrid',
                            searchModel: me.getSearchModel(),
                            flex: 1,
                            bind: {
                                hidden: '{noResults}'
                            }
                        },
                        {
                            xtype: 'homepage-globalsearch-noresults',
                            flex: 1,
                            hidden: true,
                            bind: {
                                hidden: '{!noResults}'
                            }
                        }
                    ]
                }
            ];
            me.bbar = me.getProgressToolbarConfig();
        }

        me.callParent(arguments);
    },

    /**
     * Launches a new search in this popup.
     *
     * @param {String} query
     * @returns {Boolean} `false` if the query was not valid.
     */
    search: function (query) {
        return this.getController().search(query);
    },

    privates: {
        /**
         * Default mode: progress of the search on the classes + stop button + rows found.
         *
         * @returns {Object}
         */
        getProgressToolbarConfig: function () {
            return {
                xtype: 'toolbar',
                items: [
                    {
                        xtype: 'container',
                        cls: 'homepage-globalsearch-progress',
                        layout: {
                            type: 'hbox',
                            align: 'middle'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{!searching}'
                        },
                        items: [
                            {
                                xtype: 'component',
                                cls: 'homepage-globalsearch-progress-ring',
                                width: 24,
                                height: 24,
                                bind: {
                                    html:
                                        '<svg viewBox="0 0 36 36">' +
                                        '<circle class="track" cx="18" cy="18" r="15.9155"></circle>' +
                                        '<circle class="bar" cx="18" cy="18" r="15.9155" stroke-dasharray="{progress} 100"></circle>' +
                                        '</svg>' +
                                        '<span>{progress}</span>'
                                }
                            },
                            {
                                xtype: 'button',
                                cls: 'homepage-globalsearch-stop',
                                ui: 'plain',
                                width: 15,
                                height: 15,
                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('stop', 'solid'),
                                handler: 'onStopSearchClick'
                            },
                            {
                                xtype: 'component',
                                cls: 'homepage-globalsearch-progress-class',
                                bind: {
                                    html: '{progressClass:htmlEncode}'
                                }
                            }
                        ]
                    },
                    '->',
                    {
                        xtype: 'tbtext',
                        bind: {
                            text: '{totalFoundText}'
                        }
                    }
                ]
            };
        }
    }
});
