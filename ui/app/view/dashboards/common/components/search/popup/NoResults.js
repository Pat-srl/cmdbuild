/**
 * Empty state of the popup: shown when a search ends without results.
 */
Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.NoResults', {
    extend: 'Ext.Component',
    alias: 'widget.homepage-globalsearch-noresults',

    cls: 'homepage-globalsearch-noresults',

    bind: {
        data: {
            query: '{query}'
        }
    },

    initComponent: function () {
        const search = CMDBuildUI.locales.Locales.homepage.search;

        this.tpl = [
            '<div class="homepage-globalsearch-noresults-body">',
            '<div class="homepage-globalsearch-noresults-icon">',
            '<span class="' + CMDBuildUI.util.helper.IconHelper.getIconId('search-minus', 'solid') + '"></span>',
            '</div>',
            '<div class="homepage-globalsearch-noresults-title">' + search.noresults + '</div>',
            '<tpl if="query">',
            '<div class="homepage-globalsearch-noresults-query">&ldquo;{query:htmlEncode}&rdquo;</div>',
            '</tpl>',
            '<div class="homepage-globalsearch-noresults-hint">' + search.noresultshint + '</div>',
            '</div>'
        ];

        this.callParent(arguments);
    }
});
