Ext.define('CMDBuildUI.view.administration.content.pluginmanager.Template', {
    extend: 'Ext.view.View',
    alias: 'widget.administration-content-pluginmanager-template',

    requires: ['CMDBuildUI.view.administration.content.pluginmanager.TemplateController'],

    controller: 'administration-content-pluginmanager-template',
    store: 'plugins',

    cls: Ext.baseCSSPrefix + 'plugins-cards-view',
    itemCls: Ext.baseCSSPrefix + 'plugins-cards-item',
    itemTpl: new Ext.XTemplate(
        `<div class="${Ext.baseCSSPrefix}card__inner">
            <div class="${Ext.baseCSSPrefix}card__header">
                <div class="${Ext.baseCSSPrefix}icon ${CMDBuildUI.util.helper.IconHelper.getIconId(
                    'puzzle-piece',
                    'solid'
                )}"> </div>
                <div class="${Ext.baseCSSPrefix}card__title">
                    <div class="${Ext.baseCSSPrefix}card__name">{description}</div>
                </div>
            </div>
            <div class="${Ext.baseCSSPrefix}card__body">
                <tpl if="expirationDate !== null">
                    <small class="${Ext.baseCSSPrefix}card__date">
                        Expiration date: {[Ext.util.Format.date(values.expirationDate, CMDBuildUI.util.helper.UserPreferences.getDateFormat())]}
                    </small>
                <tpl else>
                    <small class="${Ext.baseCSSPrefix}card__date"></small>
                </tpl>

                <div class="${Ext.baseCSSPrefix}card__warnings">
                    <tpl if="_hasPatches == true">
                            <span class="${CMDBuildUI.util.helper.IconHelper.getIconId(
                                'exclamation-triangle',
                                'solid'
                            )}"></span>
                            <span>{[CMDBuildUI.locales.Locales.administration.plugin.patchavailable]}</span>
                    </tpl>
                </div>
            </div>
            <div class="${Ext.baseCSSPrefix}card__footer">
                <div class="${Ext.baseCSSPrefix}card__tags">
                    <span class="${Ext.baseCSSPrefix}pluginTag">{tag}</span>
                    <span class="${Ext.baseCSSPrefix}pluginTag {status}">{status}</span>
                </div>
                <div class="${Ext.baseCSSPrefix}card__version">{version}</div>
            </div>
        </div>`
    ),
    emptyText: CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults,
    localized: {
        emptyText: 'CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults'
    }
});
