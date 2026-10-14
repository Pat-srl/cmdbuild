Ext.define('CMDBuildUI.components.administration.colorpicker.Colorpicker', {
    extend: 'Ext.form.field.Picker',

    alias: 'widget.cmdbuild-colorpicker',
    requires: ['Ext.picker.Color'],

    editable: true,
    vtype: 'hexColorValidation',
    config: {
        picker: null,
        displaySlot: 'color',
        colors: [
            // Rossi & Corallo (8)
            'c1121f',
            'd62828',
            'd90429',
            'ef233c',
            'e63946',
            'ff4d6d',
            'f25f5c',
            'e07a5f',

            // Arancioni & Terracotta (8)
            'dc2f02',
            'bb3e03',
            'e85d04',
            'f77f00',
            'ca6702',
            'ee9b00',
            'f4a261',
            'e76f51',

            // Gialli, Ocra & Ambra (8)
            'bc6c25',
            '855718',
            'ca9e50',
            'dda15e',
            'e3a857',
            'd4a373',
            'ffb703',
            'ffc300',

            // Lime & Verdi Chiari (8)
            'aacc00',
            '70e000',
            '38b000',
            '55a630',
            '74c69d',
            '52b788',
            '2b9348',
            '2a9d8f',

            // Verdi Scuri & Bosco (8)
            '081c15',
            '1b4332',
            '155d27',
            '208b3a',
            '2d6a4f',
            '40916c',
            '005f73',
            '005f73',

            // Ottanio, Ciano & Turchese (8)
            '028090',
            '00a896',
            '0a9396',
            '00c49f',
            '00b4d8',
            '48cae4',
            '0077b6',
            '3a86ff',

            // Blu, Indaco & Marina (8)
            '03045e',
            '0d3b66',
            '142c3b',
            '1d3557',
            '184e77',
            '023e8a',
            '0096c7',
            '1e6091',

            // Viola & Prugna (8)
            '5c0475',
            '480ca8',
            '560bad',
            '6a040f',
            '7209b7',
            '8338ec',
            '3f37c9',
            '9d4edd',

            // Magenta & Rosa (8)
            'd80032',
            'ff007f',
            'f72585',
            'b5179e',
            'e056fd',
            'c77dff',
            'ff70a6',
            'e0aaff',

            // Neutri & Grigi (8)
            '212529',
            '495057',
            '6c757d',
            'adb5bd',
            'ced4da',
            'dee2e6',
            'e9ecef',
            'f8f9fa'
        ]
    },

    /**
     * @override
     *
     * Create the color picker
     */
    createPicker: function () {
        var me = this,
            picker;
        if (!me.picker) {
            picker = Ext.create('Ext.picker.Color', {
                value: me.getValue(),
                renderTo: Ext.bodyEl,
                colors: this.getColors(),
                floating: true,
                style: {
                    height: 'auto'
                },
                listeners: {
                    select: {
                        fn: me.onColorPickerChange,
                        scope: me
                    }
                }
            });
            this.setPicker(picker);
        }

        return me.getPicker();
    },

    /**
     * @param {Ext.picker.Color} colorPicker
     * @param {Hex} color
     */
    onColorPickerChange: function (colorPicker, color) {
        this.setValue('#' + color);
        this.collapse();
    },

    /**
     * @override
     *
     * Collapses this field's picker dropdown.
     */
    collapse: function () {
        var me = this,
            openCls = me.openCls,
            aboveSfx = '-above',
            picker;

        if (me.isExpanded && !me.destroyed && !me.destroying) {
            picker = me.picker;

            // hide the picker and set isExpanded flag
            picker.hide();
            me.isExpanded = false;

            // remove the openCls
            me.bodyEl.removeCls([openCls, openCls + aboveSfx]);

            if (picker.el) {
                picker.el.removeCls(picker.baseCls + aboveSfx);
            }

            if (!me.ariaStaticRoles[me.ariaRole]) {
                me.ariaEl.dom.setAttribute('aria-expanded', false);
            }

            // remove event listeners
            me.touchListeners.destroy();
            me.scrollListeners.destroy();

            Ext.un('resize', me.alignPicker, me);
            me.fireEvent('collapse', me);

            me.onCollapse();
        }
    }
});
