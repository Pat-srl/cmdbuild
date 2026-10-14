Ext.define('CMDBuildUI.components.administration.fapicker.FaPicker', {
    extend: 'Ext.form.field.Picker',

    // Font Awesome version currently used: 5.6.3,

    alias: 'widget.cmdbuild-fapicker',

    editable: true,

    icons: CMDBuildUI.util.helper.IconHelper.icons,

    viewModelKey: 'theValue',

    vtype: 'FaPickerValidation',

    /**
     * Set icon button class
     *
     * @param {String} iconCls
     */
    setActiveIconCls: function (iconCls) {
        const me = this;
        const pickerPanel = me.getPicker();
        if (!pickerPanel) {
            return;
        }
        const panelItems = pickerPanel.items && pickerPanel.items.items ? pickerPanel.items.items : [];
        Ext.Array.forEach(panelItems, function (fieldset) {
            if (!fieldset || !fieldset.items) {
                return;
            }
            fieldset.items.each(function (btn) {
                if (iconCls && btn.iconCls === iconCls) {
                    btn.addCls('active');
                } else {
                    btn.removeCls('active');
                }
            });
        });
    },

    /**
     * @override
     *
     * @returns
     */
    createPicker: function () {
        let me = this,
            fieldSet = [],
            categories = Ext.Array.sort(CMDBuildUI.util.helper.IconHelper.getCategories(), function (a, b) {
                return a[1] < b[1] ? -1 : 1;
            }),
            keyCategories = Ext.Array.pluck(categories, 0);

        let handler = function (button, event) {
            me.clickedIconCls = button.iconCls;
            me.lookupViewModel().get(me.viewModelKey).set('icon_font', button.iconCls);

            me.setActiveIconCls(button.iconCls);

            if (me.getValue() === button.iconDescription) {
                me.setRawValue(button.iconDescription);
                me.collapse();
            } else {
                me.setConfig({
                    value: button.iconDescription
                });
            }
        };

        Ext.Object.eachValue(me.icons, function (value) {
            const index = Ext.Array.indexOf(keyCategories, value.category);
            !categories[index][2] ? (categories[index][2] = [value]) : categories[index][2].push(value);
        });

        Ext.Array.forEach(categories, function (iconsCategory, indexIcon, allIcons) {
            let countItems = 0,
                buttons = [];
            Ext.Array.forEach(iconsCategory[2], function (item, index, allitems) {
                const filter = me._filter;
                if (filter) {
                    const itemNameLower = (item.name || '').toLowerCase();
                    if (itemNameLower.indexOf(filter) === -1) {
                        return;
                    }
                }

                const idIcons = [item['cls_base'], item['cls_regular'], item['cls_solid']];
                Ext.Array.forEach(Ext.Array.clean(idIcons), function (elem, index, allitems) {
                    let btn = Ext.create('Ext.Button', {
                        ui: 'administration-fapicker-btn',
                        iconCls: elem,
                        iconDescription: item.name,
                        margin: '4 4 4 4',
                        match: false,
                        tooltip: Ext.String.format(
                            '<b>{0}</b>: {1}<br><b>{2}</b>: {3}',
                            CMDBuildUI.locales.Locales.administration.common.labels.description,
                            item.name,
                            CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
                            elem
                        ),
                        handler: handler
                    });
                    if (me.getValue() === elem) {
                        btn.addCls('active');
                    }
                    buttons.push(btn);
                    countItems++;
                });
            });

            if (countItems > 0) {
                fieldSet.push({
                    xtype: 'fieldset',
                    title: iconsCategory[1] + ' (' + countItems + ')',
                    ui: 'administration-formpagination',
                    layout: 'column',
                    items: buttons
                });
            }
        });

        let picker = new Ext.panel.Panel({
            pickerField: me,
            floating: true,
            hidden: true,
            ownerCt: this.ownerCt,
            renderTo: Ext.bodyEl,
            bodyPadding: 5,
            height: 450,
            autoScroll: true,
            items: fieldSet
        });

        return picker;
    },

    listeners: {
        change: function (picker, newValue, oldValue, eOpts) {
            picker.collapse();
            const me = this;
            const wasIconClick = !!me.clickedIconCls;
            const rawValue = (me.getRawValue() || '').toLowerCase();

            me._filter = rawValue;

            if (me.picker) {
                try {
                    me.picker.destroy();
                } catch (e) {}
                me.picker = null;
            }

            const pickerPanel = me.getPicker();
            if (!pickerPanel) {
                return;
            }
            const iconArray = Object.entries(me.icons);
            const icon = iconArray.find(function (icon) {
                return icon[1].name.toLowerCase() === rawValue;
            });
            if (!newValue) {
                me.lookupViewModel().get(me.viewModelKey).set('icon_font', null);
                me.setActiveIconCls(null);
                return;
            }
            const iconCls =
                me.clickedIconCls ||
                (icon && icon[1] && (icon[1].cls_base || icon[1].cls_regular || icon[1].cls_solid));
            me.lookupViewModel().get(me.viewModelKey).set('icon_font', iconCls);
            if (icon) {
                me.clickedIconCls = null;
                me.setConfig({
                    value: icon[1].name
                });
                me.setActiveIconCls(iconCls);
            }
            if ((icon && !oldValue) || wasIconClick) {
                return;
            }
            picker.expand();
        }
    }
});
