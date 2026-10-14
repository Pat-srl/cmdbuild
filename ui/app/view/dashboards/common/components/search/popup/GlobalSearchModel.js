Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearchModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.homepage-globalsearch',

    data: {
        /**
         * `default` (classes search) or `ai`. Set by the controller from the view config.
         */
        mode: 'default',

        /**
         * Text of the last search launched: it drives the popup title.
         */
        query: null,

        /**
         * Text shown in the popup search box (two-way bound).
         */
        searchText: null,

        /**
         * `true` while a search (default or AI) is running.
         */
        searching: false,

        /**
         * Default search: percentage of classes already searched and description of the current one.
         */
        progress: 0,
        progressClass: null,

        /**
         * Default search: number of rows found (`null` = no search done).
         */
        totalCount: null,

        /**
         * Title of the open card detail (not encoded), `null` when the results are shown.
         */
        detailTitle: null,

        /**
         * AI search: `idle`, `loading`, `success` or `error`, the answer text
         * and the number of resources loaded in the results grid.
         */
        aiStatus: 'idle',
        aiAnswer: '',
        aiResourcesCount: 0
    },

    formulas: {
        aiLoading: function (get) {
            return get('aiStatus') === 'loading';
        },

        aiError: function (get) {
            return get('aiStatus') === 'error';
        },

        aiHasResources: function (get) {
            return get('aiStatus') === 'success' && get('aiResourcesCount') > 0;
        },

        noResults: function (get) {
            if (get('mode') === 'ai') {
                return get('aiStatus') === 'success' && get('aiResourcesCount') === 0;
            }
            return !!get('query') && !get('searching') && get('totalCount') === 0;
        },

        hasDetail: function (get) {
            return !!get('detailTitle');
        },

        searchEmptyText: function (get) {
            const actions = CMDBuildUI.locales.Locales.common.actions;
            return get('mode') === 'ai' ? actions.searchaitext : actions.searchtext;
        },

        /**
         * Popup title: card description while a detail is open, otherwise "Results for: <query>".
         */
        title: function (get) {
            const locales = CMDBuildUI.locales.Locales;
            const detail = get('detailTitle');
            const query = get('query');

            if (detail) {
                return Ext.String.htmlEncode(detail);
            }
            if (query) {
                return Ext.String.format(locales.homepage.search.resultsfor, Ext.String.htmlEncode(query));
            }
            return locales.common.actions.searchtext;
        },

        totalFoundText: function (get) {
            const count = get('totalCount');
            const grid = CMDBuildUI.locales.Locales.common.grid;

            if (!Ext.isNumber(count)) {
                return '';
            }
            return Ext.String.format('{0} {1}', count, count !== 1 ? grid.rows : grid.row);
        }
    }
});
