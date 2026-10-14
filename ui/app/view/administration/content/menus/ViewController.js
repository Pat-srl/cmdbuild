Ext.define('CMDBuildUI.view.administration.content.menus.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-view',

    control: {
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        }
    },

    setCopyButton: function (view) {
        var me = this;
        me.getViewModel().bind(
            {
                bindTo: {
                    menus: '{menus}',
                    device: '{theMenu.device}'
                }
            },
            function (data) {
                var menus = data.menus;
                var device = data.device;
                var copyFromButton = view.up('components-administration-toolbars-formtoolbar').down('#copyFrom');
                copyFromButton.menu.removeAll();
                menus.sort(function (a, b) {
                    return a.get('description').localeCompare(b.get('description'));
                });
                Ext.Array.forEach(menus, function (element) {
                    if (
                        element.get('name') !== me.getViewModel().get('theMenu.name') &&
                        element.get('device') === device
                    ) {
                        var description = element.get('description');
                        if (element.get('description') === '_default') {
                            description = CMDBuildUI.locales.Locales.administration.common.strings['default'];
                        }
                        copyFromButton.menu.add({
                            text: description,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
                            listeners: {
                                click: function () {
                                    me.cloneFrom(element, view);
                                }
                            }
                        });
                    }
                });
            }
        );
    },

    cloneFrom: function (menu, view, currentGrantsStore) {
        var me = this;
        var mainView = view.up('administration-content-menu-view');
        var vm = mainView.getViewModel();
        CMDBuildUI.util.administration.helper.AjaxHelper.getMenuForGroup(menu.get('_id')).then(
            function (response) {
                var _menu = me.generateNewIds(response, true);
                vm.set('theMenu.children', _menu.children);
                vm.set('theMenu.sourceGroup', menu.get('group'));
                var originPanel = mainView.down('administration-content-menus-treepanels-originpanel');
                originPanel.fireEventArgs('generateoriginpanel', [originPanel]);
            },
            function (e) {
                CMDBuildUI.util.Logger.log('Error on get menu for group', CMDBuildUI.util.Logger.levels.error);
            }
        );
    },
    /**
     *
     * @param {*} button
     * @param {*} event
     * @param {*} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().setCurrentAction(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var theMenu = this.getViewModel().get('theMenu');
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.menus.strings['delete'],
            CMDBuildUI.locales.Locales.administration.menus.strings.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);

                    CMDBuildUI.util.Ajax.setActionId('delete-menu');
                    theMenu.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(
                                null,
                                record.get('device')
                            );
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                'href',
                                Ext.util.History.getToken(),
                                nextUrl,
                                me
                            );
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                        }
                    });
                }
            },
            this
        );
    },

    privates: {
        generateNewIds: function (item, isClone) {
            var me = this;
            if (item.menuType !== 'root') {
                if (isClone) {
                    item.originId = item._id;
                }
                item._id = CMDBuildUI.util.Utilities.generateUUID();
            }
            if (item.children && item.children.length) {
                Ext.Array.forEach(item.children, function (_item, index) {
                    item.children[index] = me.generateNewIds(_item, true);
                });
            }
            return item;
        }
    }
});
