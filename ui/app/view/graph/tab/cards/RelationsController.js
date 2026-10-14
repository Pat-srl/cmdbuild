Ext.define('CMDBuildUI.view.graph.tab.cards.RelationsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-relations',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            groupexpand: 'onGroupExpand',
            beforegroupcollapse: 'onBeforeGroupCollapse'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.graph.tab.cards.Relations} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();

        Ext.grid.feature.Grouping.override({
            collapse: function (groupBd, options) {
                options = options || {};
                Ext.applyIf(options, {
                    skip: {
                        beforegroupcollapse: false
                    }
                });
                if (options.skip.beforegroupcollapse) {
                    //skips the firing event of beforegroupcollapse
                    this.callOverridden(arguments);
                } else if (
                    view.fireEvent('beforegroupcollapse', this.view, this.getHeaderNode(groupBd), groupBd) !== false
                ) {
                    this.callOverridden(arguments);
                }
            }
        });

        Ext.grid.feature.Grouping.override({
            expand: function (groupBd, options) {
                options = options || {};
                Ext.applyIf(options, {
                    skip: {
                        beforegroupexpand: false
                    }
                });
                if (options.skip.beforegroupexpand) {
                    //skips the firing event of beforegroupexpand
                    this.callOverridden(arguments);
                } else if (
                    view.fireEvent('beforegroupexpand', this.view, this.getHeaderNode(groupBd), groupBd) !== false
                ) {
                    this.callOverridden(arguments);
                }
            }
        });

        vm.bind('{selectedNode}', function (selectedNode) {
            if (!selectedNode || selectedNode.length === 0) return;
            selectedNode = selectedNode[0].id;

            const cy = vm.get('cy'),
                gridStore = view.getStore(),
                relationStore = vm.get('relationStore'),
                edges = cy
                    .$('#' + selectedNode)
                    .connectedEdges()
                    .toArray();

            gridStore.removeAll();

            //Finds the relation of the selected node
            Ext.Array.forEach(edges, function (edge, index, alledges) {
                const record = relationStore.findRecord('_id', edge.id());
                if (record) {
                    let newRecord;

                    if (record.get('_sourceId') == selectedNode) {
                        newRecord = record.clone();
                    } else if (record.get('_destinationId') == selectedNode) {
                        //Need to switch destination and source
                        newRecord = Ext.create('CMDBuildUI.model.graph.EdgesRelation', {
                            _destinationCode: record.get('_sourceCode'),
                            _destinationDescription: record.get('_sourceDescription'),
                            _destinationType: record.get('_sourceType'),
                            _type: record.get('_type')
                        });
                    }

                    //copy hasMany data (_node)
                    newRecord.nodes().insert(0, record.nodes().getRange());

                    //handles compound name in destination. Used to get class description from class.Class store
                    let destinationType = newRecord.get('_destinationType');
                    destinationType = destinationType.includes('compound_')
                        ? destinationType.replace('compound_', '')
                        : destinationType;

                    //get class description and set it
                    newRecord.set(
                        'destTypeDescription',
                        CMDBuildUI.util.helper.ModelHelper.getObjectDescription(destinationType)
                    );

                    gridStore.insert(0, newRecord);
                }
            });

            //avoid dirty records (red corner in top left)
            gridStore.commitChanges();

            vm.set(
                'relationTabTitle',
                Ext.String.format(
                    '{0} <span class="badge">{1}</span>',
                    CMDBuildUI.locales.Locales.relationGraph.cardRelations,
                    edges.length
                )
            );

            /**
             * Sets the groups collapsed or expanded
             */
            const feature = view.getView().findFeature('grouping'),
                edgesRelationStore = vm.get('edgesRelationStore'),
                groups = edgesRelationStore.getGroups().getRange(),
                map = vm.get('_map_Node_CompoundName');

            Ext.Array.forEach(groups, function (group, index, allgroup) {
                let present = false;
                const groupKey = group.getGroupKey();

                if (selectedNode.includes('compound')) {
                    if (view.up().getActiveTab().isRelationTab) {
                        feature.expand(groupKey, { skip: { beforegroupexpand: true } });
                    }
                } else if (!groupKey.includes('compound')) {
                    feature.expand(groupKey, { skip: { beforegroupexpand: true } });
                } else {
                    const items = group.items[0].nodes().getRange();
                    Ext.Array.each(items, function (item, index, allitems) {
                        const gpNodeId = item.get('_destinationId');
                        if (map[gpNodeId]) {
                            feature.collapse(groupKey, { skip: { beforegroupcollapse: true } });
                            present = true;
                            return false;
                        }
                    });
                    if (!present) {
                        feature.expand(groupKey, { skip: { beforegroupexpand: true } });
                    }
                }
            });
        });
    },

    /**
     * @param {Ext.view.Table}
     * @param {HTMLElement} node
     * @param {String} group
     */
    onBeforeGroupCollapse: function (view, node, group) {
        return false;
    },

    /**
     * @param {Ext.view.Table}
     * @param {HTMLElement} node
     * @param {String} group
     */
    onGroupExpand: function (view, node, group) {
        const store = this.getStore('edgesRelationStore'),
            storeGroups = store.getGroups().items,
            nodeGroup = storeGroups.find(function (storeGroup) {
                if (storeGroup.getGroupKey() === group) {
                    return storeGroup;
                }
            });

        let compoundId = false;
        const items = nodeGroup.items[0].nodes().getRange(),
            map = this.getViewModel().get('_map_Node_CompoundName');

        for (let i = 0; i < items.length && !compoundId; i++) {
            const nodeId = items[i].get('_destinationId'); //could chose each one of the items but we select the one in position 0;
            compoundId = map[nodeId];
        }

        if (compoundId) {
            Ext.GlobalEvents.fireEventArgs('doubleclicknode', [[compoundId]]);
        }
    }
});
