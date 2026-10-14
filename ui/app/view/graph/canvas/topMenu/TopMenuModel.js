Ext.define('CMDBuildUI.view.graph.canvas.topMenu.TopMenuModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-canvas-topmenu-topmenu',

    data: {
        navTreeData: [],
        disableChooseNavTree: true,
        disableReopenGraph: true
    },

    formulas: {
        updateNavTreeMenu: {
            bind: {
                selectedNode: '{selectedNode}',
                navTreesLoaded: '{navTreesLoaded}'
            },
            get: function (data) {
                if (data.navTreesLoaded && data.selectedNode) {
                    const numberOfSelectedNodes = data.selectedNode.length;
                    this.set('disableReopenGraph', numberOfSelectedNodes !== 1);

                    if (numberOfSelectedNodes == 1) {
                        const items = [];
                        Ext.Array.forEach(
                            Ext.getStore('navigationtrees.NavigationTrees').getRange(),
                            function (record, i, allrecords) {
                                let index = -1;
                                const navid = record.get('_id');
                                const tmpStore = record.nodes();

                                if (tmpStore && !Ext.Array.contains(['gisnavigation', 'bimnavigation'], navid)) {
                                    const objectTypeName = data.selectedNode[0].type;
                                    const item =
                                        CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName) ||
                                        CMDBuildUI.util.helper.ModelHelper.getProcessFromName(objectTypeName);
                                    const hierarchy = item ? item.getHierarchy() : [];

                                    for (var j = hierarchy.length; j > 0 && index == -1; j--) {
                                        index = tmpStore.find('targetClass', hierarchy[j - 1]);
                                    }

                                    if (index !== -1) {
                                        items.push({
                                            label: record.get('_description_translation') || record.get('description'),
                                            value: navid
                                        });
                                    }
                                }
                            }
                        );

                        if (items.length == 0) {
                            this.set('disableChooseNavTree', true);
                            this.set('navTreeData', []);
                        } else {
                            this.set('disableChooseNavTree', false);
                            this.set('navTreeData', items);
                        }
                    } else {
                        this.set('disableChooseNavTree', true);
                    }
                }
            }
        }
    },

    stores: {
        navTreeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{navTreeData}'
        }
    }
});
