Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.classes.ClassesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-domains-classes',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#originTreeCheckActive': {
            checkchange: 'onCheckChangeOriginTree'
        },
        '#destinationTreeCheckActive': {
            checkchange: 'onCheckChangeDestinationTree'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.domains.tabitems.domains.Classes} view
     */
    onBeforeRender: function (view) {
        const vm = view.getViewModel();
        let theObject;

        vm.bind(
            {
                bindTo: {
                    source: '{theDomain.source}',
                    destination: '{theDomain.destination}'
                }
            },
            function (data) {
                if (data.source && data.destination) {
                    theObject = vm.get('theDomain') || vm.getData().theDomain;

                    const classStore = Ext.create('Ext.data.ChainedStore', {
                        source: 'classes.Classes'
                    });

                    const processStore = Ext.create('Ext.data.ChainedStore', {
                        source: 'processes.Processes'
                    });

                    // source can be class or process
                    const source = !theObject.get('sourceProcess')
                        ? classStore.getById(theObject.get('source'))
                        : processStore.getById(theObject.get('source'));

                    // destination can be domain or process
                    const destination = !theObject.get('destinationProcess')
                        ? classStore.getById(theObject.get('destination'))
                        : processStore.getById(theObject.get('destination'));

                    // set source checkbox checked or not
                    let sourceTree = [];
                    if (source) {
                        sourceTree = source.getChildrenAsTree(true, function (item) {
                            item.set(
                                'enabled',
                                theObject.get('disabledSourceDescendants').indexOf(item.get('name')) === -1
                            );
                            return item;
                        });
                    }

                    // set destination checkbox checked or not
                    let destinationTree = [];
                    if (destination) {
                        destinationTree = destination.getChildrenAsTree(true, function (item) {
                            item.set(
                                'enabled',
                                theObject.get('disabledDestinationDescendants').indexOf(item.get('name')) === -1
                            );
                            return item;
                        });
                    }

                    // generate the source tree
                    const originRoot = {
                        expanded: true,
                        text: theObject.getSourceDescription(),
                        name: source ? source.get('name') : theObject.get('source'),
                        leaf: sourceTree.length >= 1 ? false : true,
                        children: sourceTree.length >= 1 ? sourceTree : false,
                        enabled: !sourceTree.length
                            ? true
                            : sourceTree.length == 1
                            ? sourceTree[0].enabled
                                ? true
                                : false
                            : undefined
                    };
                    vm.get('originStore').setRoot(originRoot);

                    // generate the destination tree
                    const destinationRoot = {
                        expanded: true,
                        text: theObject.getDestinationDescription(),
                        name: destination ? destination.get('name') : theObject.get('destination'),
                        leaf: destinationTree.length >= 1 ? false : true,
                        children: destinationTree.length >= 1 ? destinationTree : false,
                        enabled: !destinationTree.length
                            ? true
                            : destinationTree.length == 1
                            ? destinationTree[0].enabled
                                ? true
                                : false
                            : undefined
                    };
                    vm.get('destinationStore').setRoot(destinationRoot);
                    vm.set('action', vm.get('action'));
                }
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        const vm = button.up('administration-content-domains-tabitems-domains-classes').getViewModel();
        const theObject = vm.get('theDomain');
        theObject.save({
            success: function (batch, options) {}
        });
        const nextUrl = Ext.String.format('administration/domains/{0}', theObject.get('name'));
        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
            const treeComponent = Ext.getCmp('administrationNavigationTree');
            const treeComponentStore = treeComponent.getStore();
            const selected = treeComponentStore.findNode('href', nextUrl);

            treeComponent.setSelection(selected);
        });
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        me.redirectTo(nextUrl, true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const tabView = button.up('administration-content-domains-tabitems-domains-classes');
        const vm = tabView.getViewModel();
        const nextUrl = Ext.String.format('administration/domains/{0}', vm.get('theDomain.name'));
        this.redirectTo(nextUrl, true);
    },

    /**
     *
     * @param {Ext.grid.column.Check} column
     * @param {Number} rowIndex
     * @param {Boolean} checked
     * @param {Ext.data.Model} record
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onCheckChangeOriginTree: function (column, rowIndex, checked, record, e, eOpts) {
        const theDomain = column.getView().lookupViewModel().get('theDomain');
        const sourceDescendant = theDomain.get('disabledSourceDescendants');
        const storeRecordName = column.getView().getStore().getAt(rowIndex).get('name');
        const sourceDescendantIndex = sourceDescendant.indexOf(storeRecordName);
        if (!checked && sourceDescendantIndex === -1) {
            sourceDescendant.push(storeRecordName);
        } else if (checked && sourceDescendantIndex > -1) {
            sourceDescendant.splice(sourceDescendantIndex, 1);
        }
        theDomain.set('disabledSourceDescendants', sourceDescendant);
    },

    /**
     *
     * @param {Ext.grid.column.Check} column
     * @param {Number} rowIndex
     * @param {Boolean} checked
     * @param {Ext.data.Model} record
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onCheckChangeDestinationTree: function (column, rowIndex, checked, record, e, eOpts) {
        const theDomain = column.getView().lookupViewModel().get('theDomain');
        const destinationDescendant = theDomain.get('disabledDestinationDescendants');
        const storeRecordName = column.getView().getStore().getAt(rowIndex).get('name');
        const destinationDescendantIndex = destinationDescendant.indexOf(storeRecordName);
        if (!checked && destinationDescendantIndex === -1) {
            destinationDescendant.push(storeRecordName);
        } else if (checked && destinationDescendantIndex > -1) {
            destinationDescendant.splice(destinationDescendantIndex, 1);
        }
        theDomain.set('disabledDestinationDescendants', destinationDescendant);
    }
});
