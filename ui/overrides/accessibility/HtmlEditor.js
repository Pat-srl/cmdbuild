Ext.define('Overrides.accessibility.HtmlEditor', {
    override: 'Ext.form.field.HtmlEditor',

    ariaIframeTitle: CMDBuildUI.locales.Locales.arialabels.iframeplaceholder,

    afterRender: function () {
        this.callParent(arguments);

        if (this.iframeEl && this.iframeEl.dom) {
            this.iframeEl.dom.setAttribute('title', this.ariaIframeTitle);
        }
    }
});
