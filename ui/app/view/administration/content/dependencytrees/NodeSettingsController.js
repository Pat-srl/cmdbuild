Ext.define('CMDBuildUI.view.administration.content.dependencytrees.NodeSettingsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dependencytrees-nodesettings',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#borderSource_input': {
            change: 'onChangeBorderSource'
        },
        '#iconSource_input': {
            change: 'onChangeIconSource'
        },
        '#iconColorMode_input': {
            change: 'onChangeIconColorMode'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.dependencytrees.NodeSettings} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const treeNode = vm.get('treeNode');
        const targetClass = treeNode.get('targetClass');
        const object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass);
        const lookupAttributes = [];

        const rawIconFont = treeNode.get('iconFont');
        if (rawIconFont) {
            treeNode.set('icon_font', CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont));
            treeNode.set(
                'icon_description',
                CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
            );
        }

        object.getAttributes().then(function (attributes) {
            Ext.Array.forEach(attributes.getRange(), function (item, index, allitems) {
                if (
                    item.get('type') == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup &&
                    item.get('active') &&
                    !item.get('hidden')
                ) {
                    lookupAttributes.push({
                        value: item.get('lookupType'),
                        label: item.get('description') || item.get('_description_translation') || item.get('name'),
                        lookupName: item.getId()
                    });
                }
            });
            vm.set('lookupAttributesListData', lookupAttributes);
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        this.getViewModel().get('treeNode').reject();
        this.getView().closePopup();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const treeNode = this.getViewModel().get('treeNode');
        if (treeNode.get('icon_font')) {
            treeNode.set('iconFont', CMDBuildUI.util.helper.IconHelper.setIconDBValue(treeNode.get('icon_font')));
        }
        if (treeNode.get('sourceLookup')) {
            treeNode.set('sourceLookupAttribute', view.down('#sourceLookup_input').getSelection().get('lookupName'));
        }
        view.closePopup();
    },

    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onChangeBorderSource: function (field, newValue, oldValue, eOpts) {
        const view = this.getView();
        const iconField = view.down('#bordercolorpicker');

        if (newValue == CMDBuildUI.view.dependencytree.Mixin.static) {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, false, view);
        } else {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, true, view);
        }

        this.setAllowBlankLookup();
    },

    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onChangeIconColorMode: function (field, newValue, oldValue, eOpts) {
        const view = this.getView();
        const iconField = view.down('#iconcolorpicker');

        if (newValue == CMDBuildUI.view.dependencytree.Mixin.static) {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, false, view);
        } else {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, true, view);
        }

        this.setAllowBlankLookup();
    },

    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onChangeIconSource: function (field, newValue, oldValue, eOpts) {
        const view = this.getView();
        const iconFieldContainer = view.down('#faIconInput');
        const iconField = iconFieldContainer.down('#classIconFont');

        if (newValue == CMDBuildUI.view.dependencytree.Mixin.static) {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, false, view);
        } else {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(iconField, true, view);
        }

        this.setAllowBlankLookup();
    },

    privates: {
        /**
         *
         */
        setAllowBlankLookup: function () {
            const view = this.getView();
            const fieldContainer = view.down('#lookupfieldset');
            const lookuplistField = fieldContainer.down('#sourceLookup_input');
            const borderSourceValue = view.down('#borderSource_input').getValue();
            const iconSourceValue = view.down('#iconSource_input').getValue();
            const iconColorModeValue = view.down('#iconColorMode_input').getValue();

            if (
                [
                    CMDBuildUI.view.dependencytree.Mixin.textColor,
                    CMDBuildUI.view.dependencytree.Mixin.iconColor
                ].indexOf(borderSourceValue) > -1 ||
                iconSourceValue == CMDBuildUI.view.dependencytree.Mixin.iconFont ||
                (iconSourceValue !== CMDBuildUI.view.dependencytree.Mixin.default &&
                    iconColorModeValue == CMDBuildUI.view.dependencytree.Mixin.iconColor)
            ) {
                fieldContainer.setHidden(false);
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(lookuplistField, false, view);
            } else {
                fieldContainer.setHidden(true);
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(lookuplistField, true, view);
            }
        }
    }
});
