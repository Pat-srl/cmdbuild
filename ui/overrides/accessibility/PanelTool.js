Ext.define('Overrides.accessibility.PanelTool', {
    override: 'Ext.panel.Tool',

    renderTpl: `
        <div
            aria-label="{[values.$comp.getAriaLabel(values)]}"
            role="button"
            id="{id}-toolEl"
            data-ref="toolEl"
            class="{className} {childElCls}"
            <tpl if="glyph">
                <tpl if="glyphFontFamily">
                    style="font-family:{glyphFontFamily};">
                </tpl>
                {glyph}
                <tpl else>
                >
            </tpl>
        </div>
    `,

    getAriaLabel: function (values) {
        const me = values.$comp;
        const ariaLabel = me.tooltip || '' + me.type + ' tool' || '';
        return Ext.String.htmlEncode(String(ariaLabel));
    }
});
