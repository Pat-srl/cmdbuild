Ext.define('CMDBuildUI.view.dashboards.common.components.search.SearchController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.ComponentController',
    alias: 'controller.dashboards-components-search-search',

    requires: ['CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /*
     * Event handlers
     */

    onBeforeRender: function (view) {
        const me = this;
        const vm = me.getViewModel();
        const homepageConfig = CMDBuildUI.model.Configuration.homepage;

        const currentHomepage = CMDBuildUI.util.Stores.currentHomepage;
        if (currentHomepage) {
            view.lookupViewModel().set('object', currentHomepage);
        }

        const searchType = CMDBuildUI.util.helper.Configurations.get(homepageConfig.type);

        if (searchType === 'ai') {
            me.setSearchState(false, false);
        } else if (searchType === 'default') {
            const searchClassesRaw = CMDBuildUI.util.helper.Configurations.get(homepageConfig.searchclasses);

            vm.set('search.hidden', Ext.isEmpty(searchClassesRaw));

            if (searchClassesRaw) {
                me.loadTargetClasses();
            } else {
                vm.set('search.disabled', false);
            }
        } else {
            me.setSearchState(true, false);
        }
    },

    onSearchTriggerClick: function (field) {
        this.onSearchSubmit(field.getValue());
    },

    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field.getValue());
        }
    },

    onSearchClearClick: function (field) {
        field.reset();
    },

    /**
     * Validates the query and opens the popup, which runs the search.
     *
     * @param {String} searchValue
     */
    onSearchSubmit: function (searchValue) {
        const me = this;
        const query = Ext.String.trim(searchValue || '');
        const GlobalSearch = CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch;

        if (query.length < GlobalSearch.MIN_QUERY_LENGTH) {
            return CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.errors.value3chars);
        }

        me.getSearchPopup().search(query);
    },

    /**
     * The popup started a search (also when launched from the popup itself).
     *
     * @param {CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch} popup
     * @param {String} query
     */
    onPopupSearchStart: function (popup, query) {
        const vm = this.getViewModel();

        vm.set('search.value', query);
        vm.set('search.disabled', true);
    },

    /**
     * The popup finished (or aborted) a search.
     */
    onPopupSearchEnd: function () {
        this.getViewModel().set('search.disabled', false);
    },

    /**
     * The popup was closed: it is created again on the next search.
     */
    onPopupDestroy: function () {
        const me = this;

        me.searchPopup = null;

        if (!me.isDestroyed) {
            // a running search is aborted by the popup without notification
            me.getViewModel().set('search.disabled', false);
        }
    },

    /*
     * Lifecycle
     */

    destroy: function () {
        const me = this;

        Ext.destroy(me.searchPopup);
        me.searchPopup = null;

        me.callParent();
    },

    privates: {
        /**
         * @returns {Boolean} `true` if the homepage search is in AI mode.
         */
        isAiSearchEnabled: function () {
            return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.homepage.type) === 'ai';
        },

        /**
         * Sets visibility and enabled state of the search field.
         *
         * @param {Boolean} hidden
         * @param {Boolean} disabled
         */
        setSearchState: function (hidden, disabled) {
            const vm = this.getViewModel();
            vm.set('search.hidden', hidden);
            vm.set('search.disabled', disabled);
        },

        /**
         * Returns the (shown) global search popup, creating it when needed.
         *
         * @returns {CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch}
         */
        getSearchPopup: function () {
            const me = this;
            let popup = me.searchPopup;

            if (!popup || popup.isDestroyed) {
                popup = me.searchPopup = Ext.create(
                    'CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearch',
                    {
                        searchMode: me.isAiSearchEnabled() ? 'ai' : 'default',
                        searchModel: me.getViewModel().get('model'),
                        searchClasses: me.searchOnClasses,
                        listeners: {
                            scope: me,
                            searchstart: me.onPopupSearchStart,
                            searchend: me.onPopupSearchEnd,
                            destroy: me.onPopupDestroy
                        }
                    }
                );
            }

            popup.show();
            return popup;
        },

        /**
         * Parses the `homepage.searchclasses` configuration (e.g. "[Class1, Class2]").
         *
         * @param {*} configRaw
         * @returns {String[]}
         */
        parseSearchClasses: function (configRaw) {
            if (!Ext.isString(configRaw)) {
                return [];
            }

            return configRaw
                .replace(/^\[|\]$/g, '')
                .split(',')
                .map(s => s.trim())
                .filter(Boolean);
        },

        /**
         * Loads the model and the classes the default search works on.
         */
        loadTargetClasses: function () {
            const me = this;
            const view = me.getView();
            const vm = me.getViewModel();
            const isViewAlive = () => view && !view.isDestroyed;

            const onError = function () {
                vm.set('search.disabled', false);
                vm.set('search.hidden', true);
                CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.errors.genericerror);
            };

            CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, 'Class')
                .then(function (model) {
                    if (!isViewAlive()) {
                        return;
                    }

                    vm.set('model', model);

                    Ext.Ajax.request({
                        url: CMDBuildUI.util.Config.baseUrl + '/classes',
                        method: 'GET',
                        callback: function (options, success, response) {
                            if (!isViewAlive()) {
                                return;
                            }

                            if (!success || response.status >= 400) {
                                return onError();
                            }

                            const allClasses = (Ext.JSON.decode(response.responseText, true) || {}).data || [];
                            const names = me.parseSearchClasses(
                                CMDBuildUI.util.helper.Configurations.get(
                                    CMDBuildUI.model.Configuration.homepage.searchclasses
                                )
                            );

                            me.searchOnClasses = allClasses.filter(c => names.includes(c.name));
                            vm.set('search.disabled', false);
                        }
                    });
                })
                .catch(function (err) {
                    Ext.log({ level: 'error', msg: 'Unable to load the search classes', dump: err });
                    if (isViewAlive()) {
                        onError();
                    }
                });
        }
    }
});
