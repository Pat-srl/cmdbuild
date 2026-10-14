Ext.define('Overrides.conponents.DisplayField', {
    override: 'Ext.form.field.Display',
    fieldSubTpl: [
        '<div id="{id}" data-ref="inputEl" role="textbox" aria-readonly="true"',
        ' aria-labelledby="{cmpId}-labelEl" {inputAttrTpl}',
        ' tabindex="<tpl if="tabIdx != null">{tabIdx}<tpl else>-1</tpl>"',
        '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
        ' class="{fieldCls} {fieldCls}-{ui} x-selectable">{value}</div>',
        {
            compiled: true,
            disableFormats: true
        }
    ]
});