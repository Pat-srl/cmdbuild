Ext.define('Overrides.accessibility.Button', {
    override: 'Ext.button.Button',

    renderTpl: `
        <span
            id="{id}-btnWrap"
            data-ref="btnWrap"
            role="presentation"
            unselectable="on"
            style="{btnWrapStyle};"
            class="{btnWrapCls} {btnWrapCls}-{ui} {splitCls}{childElCls}"
            aria-label="{[values.$comp.getAriaLabel(values)]}"
        >
            <span
                id="{id}-btnEl"
                data-ref="btnEl"
                role="presentation"
                unselectable="on"
                style="{btnElStyle}"
                class="{btnCls} {btnCls}-{ui} {textCls} {noTextCls} {hasIconCls} {iconAlignCls} {textAlignCls} {btnElAutoHeightCls}{childElCls}"
            >
                <tpl if="iconBeforeText">
                    {[values.$comp.renderIcon(values)]}
                </tpl>
                <span
                    id="{id}-btnInnerEl"
                    data-ref="btnInnerEl"
                    unselectable="on"
                    class="{innerCls} {innerCls}-{ui}{childElCls}"
                >{text}</span>
                <tpl if="!iconBeforeText">
                    {[values.$comp.renderIcon(values)]}
                </tpl>
            </span>
        </span>

        {[values.$comp.getAfterMarkup ? values.$comp.getAfterMarkup(values) : ""]}

        <tpl if="closable">
            <span
                id="{id}-closeEl"
                data-ref="closeEl"
                class="{baseCls}-close-btn"
            >
                <tpl if="closeText">
                     {closeText}
                </tpl>
            </span>
        </tpl>

        <tpl if="split">
            <span
                id="{id}-arrowEl"
                class="{arrowElCls}"
                data-ref="arrowEl"
                role="button"
                hidefocus="on"
                unselectable="on"
                <tpl if="tabIndex != null"> tabindex="{tabIndex}"</tpl>
                <tpl foreach="arrowElAttributes"> {$}="{.}"</tpl>
                style="{arrowElStyle}"
            >
                {arrowElText}
            </span>
        </tpl>
    `,

    getAriaLabel: function (values) {
        const me = values.$comp;

        let ariaLabel =
            (me.ariaAttributes && me.ariaAttributes['aria-label']) ||
            me.text ||
            me.tooltip ||
            (Ext.isString(me.tooltipLabel)
                ? me.tooltipLabel.split('.').reduce(function (obj, key) {
                      return obj && obj[key];
                  }, window)
                : me.tooltipLabel) ||
            CMDBuildUI.locales.Locales.arialabels.buttonplaceholder;

        return Ext.String.htmlEncode(String(ariaLabel));
    },

    afterRender: function () {
        this.callParent(arguments);
        const me = this;

        // Manage aria attributes for buttons with menus
        if (me.menu) {
            me.getEl().dom.removeAttribute('aria-owns');
            me.on({
                menushow: function () {
                    const menuEl = this.menu.getEl();
                    if (menuEl && menuEl.dom && menuEl.dom.id) {
                        this.getEl().dom.setAttribute('aria-owns', menuEl.dom.id);
                    }
                    this.getEl().dom.setAttribute('aria-expanded', 'true');
                },
                menuhide: function () {
                    this.getEl().dom.setAttribute('aria-expanded', 'false');
                },
                scope: me
            });
        }
    }
});
