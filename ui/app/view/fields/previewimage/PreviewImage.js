Ext.define('CMDBuildUI.view.fields.previewimage.PreviewImage', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.previewimage.PreviewImageController',
        'CMDBuildUI.view.fields.previewimage.PreviewImageModel'
    ],

    mixins: [
        'Ext.form.field.Field'
    ],

    alias: 'widget.previewimage',
    controller: 'fields-previewimage',
    viewModel: {
        type: 'fields-previewimage'
    },

    layout: 'anchor',

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,
    hidden: true,
    config: {
        withRemoveButton: true,
        src: null,
        alt: '',
        localized: {},
        imageHeigth: 30,
        imageWidth: null,
        resetKey: null
    },
    bind: {
        hidden: '{updatingImage}'
    },
    initComponent: function () {
        this.callParent(arguments);
        this.addItems();
    },

    items: [],

    addItems: function () {
        this.removeAll();
        if (this.getSrc()) {

            this.add({
                xtype: 'image',
                height: this.getImageHeigth() || undefined,
                width: this.getImageWidth() || undefined,
                cls: 'logo',
                alt: this.getAlt(),
                localized: this.getLocalized(),
                hidden: true,
                reference: 'previewimage',
                bind: {
                    hidden: Ext.String.format('{!{0}}', this.getSrc()),
                    src: Ext.String.format('{{0}}', this.getSrc())
                }
            });
            if (this.getWithRemoveButton()) {
                this.add({
                    marginLeft: '5px',
                    xtype: 'button',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                    cls: 'input-action-button',
                    ui: 'administration-secondary-action-small',
                    reference: 'removeimagebtn',
                    height: this.getImageHeigth() || undefined,
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{actions.view || !{0}}', this.getSrc())
                    },
                    listeners: {
                        click: 'onRemoveImageBtnClick'
                    }
                });
            }
        }
    }
});