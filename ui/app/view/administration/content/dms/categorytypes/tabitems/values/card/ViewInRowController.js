Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabitems-values-card-viewinrow',

    mixins: ['CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ToolsMixin'],
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
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
     *
     * @param {CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        Ext.asap(function () {
            try {
                view.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                CMDBuildUI.util.Logger.log(
                    'unable to mask lookup value forminrow',
                    CMDBuildUI.util.Logger.levels.debug
                );
            }
        });

        const me = this;
        const vm = me.getViewModel();
        const selected = view._rowContext.record;
        const panelVm = Ext.getCmp('CMDBuildAdministrationContentDMSCategoryTypesView').getViewModel();

        vm.set('_is_system', panelVm.get('theDMSCategoryType._is_system'));

        CMDBuildUI.model.dms.DMSCategory.getProxy().setExtraParam('active', false);
        CMDBuildUI.model.dms.DMSCategory.getProxy().setUrl(
            CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(
                panelVm.get('objectTypeName')
            )
        );

        vm.linkTo('theValue', {
            type: 'CMDBuildUI.model.dms.DMSCategory',
            id: selected.get('_id')
        });
        me.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        this.getViewModel().bind(
            {
                bindTo: {
                    theValue: '{theValue}'
                }
            },
            function (data) {
                if (data.theValue) {
                    Ext.asap(function () {
                        try {
                            view.unmask();
                        } catch (error) {
                            CMDBuildUI.util.Logger.log(
                                'unable to unmask view in row',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                    });
                } else {
                    CMDBuildUI.util.Logger.log(
                        'unable to unmask view in row, theValue is undefined',
                        CMDBuildUI.util.Logger.levels.debug
                    );
                }
            }
        );
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
