Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-card-viewinrow',

    mixins: ['CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const selected = view._rowContext.record;
        vm.set('theValue', selected);
        vm.set('_is_system', selected.get('_is_system'));
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        const vm = this.getViewModel();
        const theValue = vm.get('theValue');
        const translationCode = Ext.String.format(
            'lookup.{0}.{1}.description',
            theValue.get('_type'),
            theValue.get('code')
        );
        const popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theTranslation',
            vm
        );
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    }
});
