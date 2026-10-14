Ext.define('CMDBuildUI.view.joinviews.configuration.items.AttributesCustomizationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-attributescustomization',

    control: {
        '#': {
            show: 'onShow',
            attributedescriptionlocalizebtnclick: 'onAttributeDescriptionLocalizeBtnClick',
            attributegruopchanged: 'onAttributeGroupsChanged',
            attributegruopremoved: 'onAttributeGroupsRemoved',
            verifycheck: 'verifyCheck'
        },
        tableview: {
            beforedrop: 'onBeforeDrop'
        }
    },

    onShow: function (view, eOpts) {
        this.verifyCheck(view.down("#attributegridcustom").getStore());
    },

    onAttributeDescriptionLocalizeBtnClick: function (field, trigger, eOpts) {
        var mainView = this.getView().up('joinviews-configuration-main'),
            record = field.getRefOwner().context.record,
            mainVm = mainView.getViewModel(),
            translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewAttributeDescription(mainVm.get('theView.name'), record.get('name'));

        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, mainVm.get('action'), Ext.String.format('theAttributeDescriptionTranslation_{0}', record.get('name')), mainVm, true);
    },

    onAttributeGroupsChanged: function (attributeGroup) {
        var grid = this.getView().down('#attributegridcustom');
        grid.getView().refresh();
    },

    onAttributeGroupsRemoved: function (attributeGroup) {
        var grid = this.getView().down('#attributegridcustom'),
            gridStore = grid.getStore();

        gridStore.each(function (item) {
            if (item.get('group') === attributeGroup.get('name')) {
                item.set('group', null);
            }
        });
    },

    onBeforeDrop: function () {
        if (this.getViewModel().get("actions.view")) {
            return false;
        }
    },

    verifyCheck: function (store) {
        var showInGrid = false,
            showInReducedGrid = false;

        Ext.Array.each(store.getRange(), function (item) {
            showInGrid = showInGrid ? true : item.get("showInGrid");
            showInReducedGrid = showInReducedGrid ? true : item.get("showInReducedGrid");
            if (showInGrid && showInReducedGrid) {
                return false;
            }
        });

        var visible = showInGrid && showInReducedGrid ? false : true;
        this.getViewModel().set("hideWarningCheckBox", !visible);
    }

});