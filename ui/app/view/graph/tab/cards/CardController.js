Ext.define('CMDBuildUI.view.graph.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-card',

    control: { '#': { beforerender: 'onBeforeRender' } },

    /**
     *
     * @param {CMDBuildUI.view.graph.tab.cards.Card} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        vm.bind('{selectedNode}', function (selectedNode) {
            if (!selectedNode || selectedNode.length === 0) return;

            let dataField = {};
            // Set the value for the label
            const type = selectedNode[0].type;
            const id = selectedNode[0].id;
            let objectname = type;
            let url;

            if (type.includes('compound_')) {
                objectname = type.replace('compound_', '');
            }

            const defaultField = {
                xtype: 'displayfield',
                labelAlign: 'left',
                labelWidth: 'auto',
                cls: Ext.baseCSSPrefix + 'process-action-field',
                fieldLabel: '',
                value: CMDBuildUI.util.helper.ModelHelper.getObjectDescription(objectname)
            };

            if (CMDBuildUI.util.helper.ModelHelper.getClassFromName(type)) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.class;
                dataField = {
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
                            objectTypeName: type,
                            objectId: id
                        }
                    }
                };
                url = CMDBuildUI.util.Navigation.getClassBaseUrl(type, id);
            } else if (CMDBuildUI.util.helper.ModelHelper.getProcessFromName(type)) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.activity;
                url = CMDBuildUI.util.Navigation.getProcessBaseUrl(type, id);
            } else if (type.includes('compound')) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.compoundnode;
            } else {
                console.error('Should never be here');
            }

            const fieldContainer = {
                xtype: 'container',
                layout: 'hbox',
                padding: '0 5 5 10',
                items: [
                    defaultField,
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
                                CMDBuildUI.util.Utilities.closePopup('graphPopup');
                            }
                        }
                    }
                ]
            };

            view.removeAll();
            view.add(fieldContainer, dataField);
        });
    }
});
