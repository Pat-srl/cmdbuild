Ext.define('CMDBuildUI.view.dependencytree.tab.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dependencytree-tab-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.graph.tab.cards.Card} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        vm.bind('{selectedNode}', function (selectedNode) {
            if (Ext.Object.isEmpty(selectedNode) || selectedNode.data.isGroupNode) {
                return;
            }

            // Set the value for the label
            const objectTypeName = selectedNode.data.objectTypeName;
            const id = selectedNode.data.objectId;
            const url = CMDBuildUI.util.Navigation.getClassBaseUrl(objectTypeName, id);

            const fieldContainer = {
                xtype: 'container',
                layout: 'hbox',
                padding: '0 5 5 10',
                items: [
                    {
                        xtype: 'displayfield',
                        labelAlign: 'left',
                        labelWidth: 'auto',
                        cls: Ext.baseCSSPrefix + 'process-action-field',
                        fieldLabel: CMDBuildUI.locales.Locales.relationGraph.class,
                        value: CMDBuildUI.util.helper.ModelHelper.getObjectDescription(objectTypeName)
                    },
                    {
                        xtype: 'tbfill'
                    },
                    {
                        xtype: 'tool',
                        cls: 'management-tool',
                        margin: '7 5',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                        tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                        handler: function () {
                            const currentToken = Ext.util.History.getToken();
                            if (currentToken !== url) {
                                CMDBuildUI.util.Utilities.redirectTo(url);
                                CMDBuildUI.util.Utilities.closePopup('dependencyTreePopup');
                            }
                        }
                    }
                ]
            };

            const dataField = {
                xtype: 'classes-cards-card-view',
                padding: '0 5',
                shownInPopup: true,
                hideTools: true,
                hideWidgets: true,
                scrollable: true,
                flex: 1,
                viewModel: {
                    data: {
                        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                        objectTypeName: objectTypeName,
                        objectId: id
                    }
                }
            };

            view.removeAll();
            view.add(fieldContainer, dataField);
        });
    }
});
