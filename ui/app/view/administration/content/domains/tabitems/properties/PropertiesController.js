Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-properties',

    control: {
        '#': {
            afterlayout: 'onAfterLayout',
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
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
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#linkToContextBtn': {
            click: 'onLinkToContextBtn'
        }
    },

    /**
     *
     * @param {Ext.container.Container} container
     * @param {Ext.layout.container.Container} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (container, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.domains.tabitems.properties.Properties} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        view.addDocked(
            {
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                    {
                        edit: true,
                        delete: true,
                        view: view.config._rowContext && view.config._rowContext.record ? true : false,
                        activeToggle: true,
                        linkToContextTool:
                            (view.config._rowContext && view.config._rowContext.record) ||
                            view.up('administration-detailswindow')
                    },
                    'domains',
                    'theDomain'
                ),
                bind: {
                    hidden: '{!actions.view}'
                }
            },
            0
        );

        if (view.config._rowContext && view.config._rowContext.record) {
            const viewInRow = view.down('#domain-generaldatafieldset');
            viewInRow.setTitle(null);
            viewInRow.setStyle('pading-top: 0;border-width: 0 !important;margin-bottom: 10px!important;');
            vm.set('theDomain', view.config._rowContext.record);
        } else {
            if (vm.get('actions.add')) {
                vm.linkTo('theDomain', {
                    type: 'CMDBuildUI.model.domains.Domain',
                    create: true
                });
            }
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        try {
            if (
                view &&
                view.container &&
                view.container.component &&
                view.container.component.getXType() !== CMDBuildUI.view.administration.DetailsWindow.xtype
            ) {
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            } else {
                const container =
                    Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                    Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                const theDomain = vm.get('theDomain');
                const grid = vm.get('grid') || view.config._rowContext.ownerGrid;

                container.removeAll();
                container.add({
                    xtype: 'administration-content-domains-tabitems-properties-properties',
                    viewModel: {
                        data: {
                            theDomain: theDomain,
                            title: Ext.String.format(
                                '{0} - {1}',
                                CMDBuildUI.locales.Locales.administration.localizations.domain,
                                theDomain.get('name')
                            ),
                            grid: grid,
                            actions: {
                                view: false,
                                edit: true,
                                add: false
                            },
                            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                            objectTypeName: theDomain.get('name')
                        }
                    }
                });
            }
        } catch (error) {
            CMDBuildUI.util.Logger.log('unable to enter in edit mode', CMDBuildUI.util.Logger.levels.debug);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        const view = this.getView();
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        const vm = view.lookupViewModel();
        const theDomain = vm.get('theDomain');
        container.add({
            xtype: 'administration-content-domains-tabitems-properties-properties',
            viewModel: {
                data: {
                    theDomain: theDomain,
                    title: Ext.String.format(
                        '{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.localizations.domain,
                        theDomain.get('name')
                    ),
                    grid: view.config._rowContext.ownerGrid,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    objectTypeName: theDomain.get('name'),
                    toolAction: vm.get('toolAction')
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        const me = this;
        const view = this.getView();
        const vm = view.lookupViewModel();
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId);

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    button.setDisabled(true);
                    CMDBuildUI.util.Utilities.showLoader(true);
                    const theDomain = vm.get('theDomain');
                    CMDBuildUI.util.Ajax.setActionId('delete-domain');

                    theDomain.erase({
                        success: function (record, operation) {
                            const itemToRemove = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(
                                record.getId()
                            );

                            if (!(view._rowContext && view._rowContext.record)) {
                                const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                    'href',
                                    itemToRemove,
                                    nextUrl,
                                    me
                                );
                            } else {
                                view._rowContext.ownerGrid.getStore().load();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', itemToRemove);
                            }
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);

                            if (container) {
                                container.fireEvent('closed');
                            }
                        }
                    });
                }
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const me = this;
        const view = me.getView();
        const vm = view.lookupViewModel();
        const theDomain = vm.get('theDomain');
        theDomain.set('active', !theDomain.get('active'));
        Ext.apply(theDomain.data, theDomain.getAssociatedData());
        theDomain.save({
            success: function (record, operation) {
                const grid = view.up('grid');
                if (grid) {
                    const plugin = grid.getPlugin('administration-forminrowwidget');
                    if (plugin) {
                        plugin.view.fireEventArgs('itemupdated', [grid, record, me]);
                    }
                }
                CMDBuildUI.util.administration.Utilities.showToggleActiveMessage(record);
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            },
            failure: function (record, reason) {
                record.reject();
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onLinkToContextBtn: function (button, e, eOpts) {
        const vm = this.getView().lookupViewModel();
        const theDomain = vm.get('theDomain');
        const url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(theDomain.get('name'));
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', url, this);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        const vm = this.getView().lookupViewModel();
        let klass, objectType;
        CMDBuildUI.util.Utilities.showLoader(true);
        button.setDisabled(true);

        const theDomain = vm.get('theDomain');
        delete theDomain.data.system;

        if (vm.get('grid')) {
            const sourceClassView = vm.get('grid').getViewModel().get('objectTypeName');
            if (sourceClassView) {
                klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(sourceClassView);
                objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourceClassView);
                const hierarchy = klass.getHierarchy();
                if (
                    hierarchy.indexOf(theDomain.get('source')) < 0 &&
                    hierarchy.indexOf(theDomain.get('destination')) < 0
                ) {
                    CMDBuildUI.util.Notifier.showErrorMessage(
                        Ext.String.format(
                            CMDBuildUI.locales.Locales.administration.domains.strings.classshoulbeoriginordestination,
                            klass.get('description')
                        )
                    );
                    CMDBuildUI.util.Utilities.showLoader(false);
                    button.setDisabled(false);
                    return false;
                }
            }
        }

        // save the domain
        theDomain.save({
            success: function (record, operation) {
                let nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(record.get('_id'));
                const cardView = me.getView().up('administration-detailswindow');
                me.saveLocales(Ext.copy(vm), record);
                if (cardView) {
                    vm.get('grid')
                        .getStore()
                        .load(function () {
                            const plugin = vm.get('grid').getPlugin('administration-forminrowwidget');
                            plugin.view.fireEventArgs('itemupdated', [vm.get('grid'), record, me]);

                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                switch (objectType) {
                                    case CMDBuildUI.model.administration.MenuItem.types.klass:
                                        nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(
                                            klass.get('_id')
                                        );
                                        break;
                                    case CMDBuildUI.model.administration.MenuItem.types.process:
                                    case 'process':
                                        nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(
                                            klass.get('_id')
                                        );
                                        break;
                                    default:
                                        break;
                                }
                                const treestore = Ext.getCmp('administrationNavigationTree');
                                const selected = treestore.getStore().findNode('href', nextUrl);
                                treestore.setSelection(selected);
                            });
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        });
                } else {
                    if (vm.get('actions.edit')) {
                        const treestore = Ext.getCmp('administrationNavigationTree').getStore();
                        const selected = treestore.findNode('href', nextUrl);
                        selected.set('text', record.get('description'));

                        if (vm.get('grid')) {
                            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            vm.set('actions.view', true);
                            vm.set('actions.add', false);
                            vm.set('actions.edit', false);
                            const store = vm.get('grid').getStore();
                            store.load({
                                callback: function () {
                                    const gridView = vm.get('grid').getView();
                                    const gridStore = vm.get('grid').getStore();
                                    gridView.refresh();

                                    const index = gridStore.findExact('_id', record.getId());
                                    const storeItem = gridStore.getAt(index);

                                    vm.get('grid')
                                        .getPlugin('administration-forminrowwidget')
                                        .view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                    vm.get('grid')
                                        .getPlugin('administration-forminrowwidget')
                                        .view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                    if (button.el.dom) {
                                        button.setDisabled(false);
                                    }
                                    CMDBuildUI.util.Utilities.showLoader(false);
                                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                                }
                            });
                        } else {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            me.redirectTo(nextUrl, true);
                        }
                    } else {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                            const treeComponent = Ext.getCmp('administrationNavigationTree');
                            const treeComponentStore = treeComponent.getStore();
                            const selected = treeComponentStore.findNode('href', nextUrl);

                            treeComponent.setSelection(selected);
                        });
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        CMDBuildUI.util.Utilities.showLoader(false);
                        vm.getParent().set(
                            'actionManager',
                            CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                        );
                        me.redirectTo(nextUrl, true);
                    }
                }
            },
            failure: function (record, reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const detailsWindow = button.up('#CMDBuildAdministrationDetailsWindow');
        let vm = this.getView().lookupViewModel();
        if (detailsWindow) {
            vm.get('theDomain').reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            vm.set('actions.view', true);
            vm.set('actions.edit', false);
            vm.set('actions.add', false);
            detailsWindow.fireEvent('closed');
        } else {
            if (vm.get('actions.edit')) {
                this.redirectTo(Ext.String.format('administration/domains/{0}', vm.get('theDomain._id')), true);
            } else if (vm.get('actions.add')) {
                const store = Ext.getStore('administration.MenuAdministration');
                vm = Ext.getCmp('administrationNavigationTree').getViewModel();
                const currentNode = store.findNode('objecttype', CMDBuildUI.model.administration.MenuItem.types.domain);
                vm.set('selected', currentNode);
                this.redirectTo('administration/domains_empty', true);
            }
        }
    },

    privates: {
        saveLocales: function (vm, record) {
            const translations = [
                'theDomainDescriptionTranslation',
                'theDirectDescriptionTranslation',
                'theInverseDescriptionTranslation',
                'theMasterDetailTranslation'
            ];
            const keyFunction = [
                'getLocaleKeyOfDomainDescription',
                'getLocaleKeyOfDomainDirectDescription',
                'getLocaleKeyOfDomainInverseDescription',
                'getLocaleKeyOfDomainMasterDetail'
            ];
            Ext.Array.forEach(translations, function (item, index) {
                if (vm.get(item)) {
                    const translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[
                        keyFunction[index]
                    ](record.get('name'));

                    vm.get(item).crudState = 'U';
                    vm.get(item).crudStateWas = 'U';
                    vm.get(item).phantom = false;
                    vm.get(item).set('_id', translationCode);
                    vm.get(item).save({
                        success: function (translations, operation) {
                            CMDBuildUI.util.Logger.log(
                                item + ' localization was saved',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                    });
                }
            });
        }
    }
});
