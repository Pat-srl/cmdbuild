Ext.define('Overrides.accessibility.FileButton', {
    override: 'Ext.form.field.FileButton',

    afterRender: function () {
        this.callParent(arguments);

        const fileInputEl = this.fileInputEl;
        if (!fileInputEl) {
            return;
        }

        const buttonText = this.getText() || this.text || '';

        fileInputEl.dom.setAttribute('aria-label', buttonText);
        fileInputEl.dom.setAttribute('title', buttonText);
    }
});
