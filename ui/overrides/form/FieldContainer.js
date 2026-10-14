Ext.define('Override.form.FieldContainer', {
    override: 'Ext.form.FieldContainer',

    /**
     * @override
     *
     */
    initComponent: function () {
        const me = this;
        const tpl = this.afterLabelTextTpl || '';

        /**
         * Add one single tool btn or icon next to the label.
         *
         * The input field should have 3 properties
         *
         * @argument labelToolIconCls: String (mandatory) (Ex. "fa-list")
         *
         * @function labelToolIconClick : Function (optional) executed on click
         * action, if is not provided return only icon with qtip.
         *
         * @argument toolIconQtip: String (optional) (Ex. "Grid button")
         *
         * @argument hideToolOnViewMode: boolean
         */
        this.afterLabelTextTpl = new Ext.XTemplate(
            '<span class="required-field-placeholder"><tpl if="this.showMandatoryIcon()"> *</tpl></span>' +
                tpl +
                '<tpl if="this.showToolBtn()">',
            '<span style="float: right; cursor: {[this.getCursorStyle()]}" class="x-tool x-tool-item x-form-item-label-button" role="button">',
            '<span style="font-weight: 400;" class="{[this.showToolBtn()]}"',
            '<tpl if="this.showToolIconQtip()"> data-qtip="{[this.showToolIconQtip()]}" data-qalign="t-b" data-qdismissDelay="0"</tpl>>',
            '</span>',
            '</span>',
            '</tpl>',
            {
                disableFormats: true,
                showMandatoryIcon: function () {
                    if (me.xtype !== 'displayfield') {
                        const label = me.fieldLabel || '';
                        return !Ext.String.endsWith(label, '*') && me.allowBlank === false;
                    }
                    return false;
                },
                showToolBtn: function () {
                    const vm = me.lookupViewModel();
                    if ((me.hideToolOnViewMode && vm.get('actions.view')) || vm.get('pluginMode')) {
                        return false;
                    }
                    if (me.labelToolIconCls) {
                        return me.labelToolIconCls;
                    }
                    return false;
                },
                showToolIconQtip: function () {
                    if (me.labelToolIconQtip) {
                        return Ext.String.htmlEncode(me.labelToolIconQtip);
                    }
                    return false;
                },
                getCursorStyle: function () {
                    if (me.labelToolIconClick) {
                        return 'pointer';
                    }
                    return 'help';
                }
            }
        );

        if (me.labelToolIconCls && me.labelToolIconClick) {
            me.on({
                el: {
                    delegate: '.x-form-item-label-button',
                    click: me.labelToolIconClick,
                    mouseenter: function (ev, button) {
                        if (button.classList && !button.classList.value.match('x-tool-over')) {
                            button.className += ' x-tool-over';
                        }
                    },
                    mouseleave: function (ev, button) {
                        if (button.classList.value.match('x-tool-over')) {
                            button.className = button.className.replace(' x-tool-over', '');
                        }
                    }
                }
            });
        }
        this.callParent(arguments);
    }
});
