Ext.define('CMDBuildUI.view.graph.GraphContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-graphcontainer',

    control: {
        '#': {
            beforedestroy: 'onBeforeDestroy',
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#sliderLevel': {
            beforerender: 'onSliderBeforeRender',
            change: 'onSliderChange',
            changecomplete: 'onSliderChangeComplete'
        },
        '#sliderValue': {
            beforerender: 'onSliderValueBeforeRender'
        }
    },

    listen: {
        global: {
            sceneselectednode: 'onSceneSelectedNode',
            doubleclicknode: 'onDoubleClickNode'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.graph.GraphContainer} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this,
            vm = this.getViewModel();

        this.loadAllDomainTrees();
        vm.bind('{node}', function (node) {
            if (node._id && node._type) {
                // Creates the first element record
                const record = Ext.create('CMDBuildUI.model.domains.Relation', {
                    _destinationType: node._type,
                    _destinationId: node._id,
                    _destinationCode: node._code,
                    _destinationDescription: node._description
                });

                // fills the first element in relation store
                vm.get('relationStore').insert(0, record);

                // Creates the cytoscape
                const cy = cytoscape({});
                vm.set('cy', cy);

                // Fills the first element in cytoscape. Sets his parameter as root
                const cyNode = me.cytoNode(record);
                cyNode.data.isRoot = true;
                cy.add(cyNode);

                // Create the ListNodeDataStructure and fills the first element in ListNodeDataStructure
                const ListNodeDataStructure = new me.ListNodeDataStructure({
                    scope: me,
                    root: {
                        _id: node._id,
                        _type: node._type,
                        _description: node._description,
                        _three_id: null,
                        _code: node._code
                    }
                });

                // Save the ListNodeDataStructure in the view model
                vm.set('memoryNodeDataStructure', ListNodeDataStructure);

                CMDBuildUI.graph.threejs.SceneUtils.init({
                    cy: cy,
                    containterView: view
                });

                // First call
                me.graphRelation('childOfMultiDepth', {
                    depth: view.down('slider').getValue(),
                    callback: {
                        fn: CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode,
                        scope: CMDBuildUI.graph.threejs.SceneUtils,
                        arguments: {
                            ids: [node._id]
                        }
                    }
                });
            } else {
                CMDBuildUI.util.Logger.log('Something gone wrong', CMDBuildUI.util.Logger.levels.warn);
            }
        });
    },

    /**
     *
     * @param {CMDBuildUI.view.graph.GraphContainer} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        this.getViewModel().set('node', {
            _type: view._type,
            _id: view._id,
            _code: view._code,
            _description: view._description
        });
    },

    /**
     * @param {Ext.Component} container
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (container, eOpts) {
        if (this.loadMask) {
            CMDBuildUI.util.Utilities.removeLoadMask(this.loadMask);
        }
        CMDBuildUI.graph.threejs.SceneUtils.destroyScene();
    },

    /**
     * @param {[Number]} ids an array of card id. The corrisponding cards are selected;
     */
    onSceneSelectedNode: function (ids) {
        const store = this.getViewModel().get('relationStore'),
            selectedNodes = [];

        Ext.Array.forEach(ids, function (id, index, allids) {
            const record = store.findRecord('_destinationId', id),
                targetClass = record.get('_destinationType');
            selectedNodes.push({
                id: id,
                type: targetClass
            });
        });

        this.updateSelectionNode(selectedNodes);
    },

    /**
     * @param {[Number]} ids contains the ids of the double clicked nodes
     */
    onDoubleClickNode: function (ids) {
        const me = this,
            vm = this.getViewModel(),
            compounds = [],
            nonCompound = [],
            newSceneSelected = [],
            copy_map = JSON.parse(JSON.stringify(vm.get('_map_Node_CompoundName')));

        this.tmpSelectedNode = vm.get('selectedNode');
        this.updateSelectionNode([]);

        Ext.Array.forEach(ids, function (id, index, allids) {
            if (id.includes('compound')) {
                compounds.push(id);
            } else {
                nonCompound.push(id);
            }
        });

        this.expandCompound(compounds, {
            callback: {
                /**
                 * @param {[Object]} nodes
                 */
                fn: function (relationToAdd) {
                    me.graphRelation('nodesLocal', {
                        nodeRelationsObject: relationToAdd,
                        callback: {
                            /**
                             * @param {[{String} ,[{Ext.data.Model}]]} newRelation a pair representing the id of the compound node and the array of node added from that compound
                             */
                            fn: function (newRelations) {
                                Ext.Array.forEach(
                                    newRelations,
                                    function (newRelationsCompound, index, allNewRelationsCompound) {
                                        const nodeId = newRelationsCompound[0].get('_destinationId'),
                                            wasInCompound = copy_map[nodeId] || false;
                                        let wasSelected = false;

                                        if (wasInCompound) {
                                            Ext.Array.forEach(me.tmpSelectedNode, function (item, index, allItems) {
                                                if (item.id === wasInCompound) {
                                                    wasSelected = true;
                                                }
                                            });
                                        }

                                        if (wasSelected) {
                                            Ext.Array.forEach(
                                                newRelationsCompound,
                                                function (newRelationCompound, index, allNewRelationsCompound) {
                                                    newSceneSelected.push(
                                                        String(newRelationCompound.get('_destinationId'))
                                                    );
                                                }
                                            );
                                        }
                                    }
                                );

                                if (Ext.isEmpty(nonCompound)) {
                                    Ext.Array.forEach(
                                        me.tmpSelectedNode,
                                        function (tmpSelectedNode_, index, allTmpSelectedNodes) {
                                            if (!tmpSelectedNode_.id.includes('compound')) {
                                                newSceneSelected.push(tmpSelectedNode_.id);
                                            }
                                        }
                                    );
                                    CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(newSceneSelected);
                                }
                            }
                        }
                    });
                }
            }
        });

        this.graphRelation('relationOf', {
            nodes: nonCompound,
            callback: {
                fn: function () {
                    Ext.Array.forEach(me.tmpSelectedNode, function (tmpSelectedNode_, index, allTmpSelectedNodes) {
                        if (!tmpSelectedNode_.id.includes('compound')) {
                            newSceneSelected.push(tmpSelectedNode_.id);
                        }
                    });
                    CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(newSceneSelected);
                }
            }
        });
    },

    /**
     *@param {Object} newNode the new Starting node after reset
     */
    resetEnvironment: function (newNode) {
        const vm = this.getViewModel(),
            relationStore = vm.get('relationStore');

        if (newNode) {
            this.tmpSelectedNode = newNode;
        } else {
            const selNode = vm.get('selectedNode');
            if (!Ext.isEmpty(selNode)) {
                //FIXME: vedere come gestire gli stati del selNode
                this.tmpSelectedNode = selNode[0];
            }
        }

        vm.set('_map_Node_CompoundName', {});
        relationStore.removeAll();
    },

    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        const vm = this.getViewModel();

        vm.set('memoryNodeDataStructure', null);
        vm.set('cy', null);
        this.eraseThreeJsScene();
        this.clearTabs();

        vm.set('node', {
            _type: this.tmpSelectedNode.type,
            _id: this.tmpSelectedNode.id,
            _code: this.tmpSelectedNode.code,
            _description: this.tmpSelectedNode.description
        });
    },

    /**
     * @param {Ext.Component} slider
     * @param {Object} eOpts
     */
    onSliderBeforeRender: function (slider, eOtps) {
        const value = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.baselevel);
        slider.setValue(value);
    },

    /**
     * @param {Ext.slider.Multi} slider
     * @param {Number} newValue
     * @param {Ext.slider.Thumb} thumb
     * @param {object} eOpts
     */
    onSliderChange: function (slider, newValue, thumb, eOpts) {
        const tbtext = this.getView().down('#sliderValue');
        tbtext.setHtml(newValue);
    },

    /**
     * @param {Ext.slider.Multi} slider
     * @param {Number} newValue
     * @param {Ext.slider.Thumb} thumb
     * @param {object} eOpts
     */
    onSliderChangeComplete: function (slider, newValue, thumb, eOpts) {
        const vm = this.getViewModel(),
            selNode = vm.get('selectedNode')[0],
            dataNode = vm
                .get('cy')
                .nodes('#' + selNode.id)
                .data(),
            tmpSelectedNode = {
                type: dataNode.type,
                id: dataNode.id,
                code: dataNode.code,
                description: dataNode.description
            };

        if (this.waitingCallbackResponse) {
            this.ignoreCallbackResponse = true;
        }

        this.resetEnvironment(tmpSelectedNode);
    },

    /**
     * @param {Ext.Component} tbtext
     * @param {Object} eOpts
     */
    onSliderValueBeforeRender: function (tbtext, eOpts) {
        const value = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.baselevel);
        tbtext.setHtml(value);
    },

    privates: {
        /**
         * @type {Boolean}
         * This field invalidates the incoming callback response
         */
        ignoreCallbackResponse: false,

        /**
         * @type {Boolean}
         * This field indicates if we are waiting for a callback response
         */
        waitingCallbackResponse: false,

        /**
         * @param {[NodeDataStructure]} nodes
         * @param {Object} config
         * @type {Object} config.callback Function to launch when the loading of all the nodes is complete
         * callback.fn {Function}
         * callback.scope {Object}
         */
        nodeRelations: function (nodes, config) {
            const me = this;
            config.l = nodes.length;
            if (!this.loadMask && nodes.length != 0) {
                this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(this.getView());
            }

            Ext.Array.forEach(nodes, function (node, index, allnodes) {
                me.waitingCallbackResponse = true;
                me.proxyNodeRelation(
                    node.getId(),
                    node.getType(),
                    node.getDescription(),
                    node.getCode(),
                    node.getTreeId(),
                    config,
                    me.ProxyNodeRelationCallback
                );
            });
        },

        /**
         * @param id
         * @param type
         * @param {String} description
         * @param {String} code
         * @param treeId
         * @param {Object} config Config to pass in the callback
         * @param callback the callback function
         * @returns the records related to that card
         */
        proxyNodeRelation: function (id, type, description, code, treeId, config, callback) {
            const me = this,
                newStore = Ext.create('Ext.data.Store', {
                    model: 'CMDBuildUI.model.domains.Relation',
                    proxy: {
                        url: Ext.String.format('/classes/{0}/cards/{1}/relations', type, id),
                        type: 'baseproxy'
                    },
                    autoLoad: true,
                    autoDestroy: true
                });

            newStore.load({
                params: {
                    limit: -1,
                    detailed: true
                },
                callback: function (records, operation, success) {
                    if (me.getView()) {
                        Ext.Array.forEach(records, function (item, index, allitems) {
                            item.set('_destinationCode', Ext.String.htmlEncode(item.get('_destinationCode')));
                            item.set(
                                '_destinationDescription',
                                Ext.String.htmlEncode(item.get('_destinationDescription'))
                            );
                        });
                        callback.call(me, id, type, description, code, treeId, records, config, operation, success);
                    }
                }
            });
        },

        /**
         *
         * @param {*} id
         * @param {*} type
         * @param {String} description
         * @param {String} code
         * @param {[Ext.data.Model]} records
         * @param {*} operation
         * @param {*} success
         */
        ProxyNodeRelationCallback: function (id, type, description, code, treeId, records, config, operation, success) {
            records = records || [];
            config = config || {};
            Ext.applyIf(config, {
                compound: true,
                ignoreL: false,
                l: 1
            });

            config.l = config.l - 1;
            const tmpRecords = [];

            // Removes record on which i have not permission due to server error
            Ext.Array.forEach(records, function (record) {
                const recType = record.get('_destinationType'),
                    classPermission = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(recType),
                    domainStore = Ext.getStore('domains.Domains'),
                    domainPermission = domainStore.find('_id', record.get('_type'));

                if (classPermission && domainPermission != -1) {
                    record.set('_sourceDescription', description);
                    record.set('_sourceCode', code);
                    tmpRecords.push(record);
                }
            });

            if (!this.ignoreCallbackResponse) {
                // Removes nodes on which i have not visibility due to navigation tree selected
                records = this.applyNavigationTree(tmpRecords, treeId, type);

                let recordCytoscape;
                if (config.compound) {
                    recordCytoscape = this.compoundCytoscape(id, type, description, code, records); //HACK: Here the records are hugely modified
                } else {
                    recordCytoscape = records;
                }

                this.fillMemoryNodeDataStructure(id, records);
                this.fillCytoscape(id, recordCytoscape);
            }

            if (config.l === 0 || config.ignoreL) {
                this.waitingCallbackResponse = false;
                if (this.ignoreCallbackResponse) {
                    this.ignoreCallbackResponse = false;
                    return;
                }
                CMDBuildUI.util.Utilities.removeLoadMask(this.loadMask);
                delete this.loadMask;
                if (config.callback) {
                    config.callback.fn.call(config.callback.scope);
                }
                Ext.GlobalEvents.fireEvent('acquisitionend');
            }
        },

        /**
         * @param {String} mode The defined mode
         * @param {Object} config
         * @type {String[]} config.nodes
         * @type {Number} config.depth
         * @type {object} config.callback
         *  config.callback: {
         *      fn: @type {Function},
         *      scope: @type {Object}
         *  }
         */
        graphRelation: function (mode, config) {
            const me = this,
                vm = this.getViewModel(),
                cy = vm.get('cy');

            config = config || {};
            Ext.applyIf(config, {
                scope: me
            });

            const memoryNodeDataStructure = vm.get('memoryNodeDataStructure');
            switch (mode) {
                case 'childOfMultiDepth':
                    const recCallback = function () {
                        const lastDepth = memoryNodeDataStructure.getLastDepth();
                        if (lastDepth < config.depth && !memoryNodeDataStructure.isComplete()) {
                            me.childOfDepth(lastDepth, {
                                fn: recCallback,
                                scope: me
                            });
                        } else {
                            CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                            // Callback call of graphRelation
                            if (config.callback) {
                                config.callback.fn.call(
                                    config.callback.scope || me,
                                    config.callback.arguments.ids || null
                                );
                            }
                        }
                    };

                    const lastDepth = memoryNodeDataStructure.getLastDepth();
                    if (lastDepth < config.depth) {
                        me.childOfDepth(lastDepth, {
                            fn: recCallback,
                            scope: me
                        });
                    }
                    break;
                case 'relationOf':
                    // Ideal for multiple nodes at unknown depth
                    const tmpNodes = [];
                    Ext.Array.forEach(config.nodes, function (id, index, allIds) {
                        tmpNodes.push(memoryNodeDataStructure.getNode(id));
                    });

                    me.nodeRelations(tmpNodes, {
                        callback: {
                            fn: function () {
                                CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                                // Callback call of graphRelation
                                if (config.callback) {
                                    config.callback.fn.call(
                                        config.callback.scope || me,
                                        config.callback.arguments || null
                                    );
                                }
                            }
                        }
                    });
                    break;
                case 'nodesLocal':
                    //Used for when there is no need to make proxy calls
                    const nodes = config.nodeRelationsObject,
                        returned = [];
                    for (let id in nodes) {
                        if (!me.loadMask) {
                            me.loadMask = CMDBuildUI.util.Utilities.addLoadMask(me.getView());
                        }
                        const node = memoryNodeDataStructure.getNode(id);
                        me.ProxyNodeRelationCallback(
                            id,
                            node.getType(),
                            node.getDescription(),
                            node.getCode(),
                            node.getTreeId(),
                            nodes[id],
                            {
                                compound: false,
                                ignoreL: true
                            }
                        );
                        returned.push(nodes[id]);
                    }
                    CMDBuildUI.graph.threejs.SceneUtils.fillScene(cy);

                    config.callback.fn.call(config.callback.fn.scope || config.scope, returned);
                    break;
                default:
                    console.error('No mode found');
            }
        },

        /**
         * @param {Number} depth
         * @param {Object} callback
         * callback.fn {Function}
         * callback.scope {Object}
         */
        childOfDepth: function (depth, callback) {
            const memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure'),
                nodes = memoryNodeDataStructure.getNodesAtdepth(depth, { all: false });
            this.nodeRelations(nodes, { callback: callback });
        },

        /**
         * @param {Number} id node id
         * @param {Ext.data.Mode} records the node childs
         */
        fillCytoscape: function (id, records) {
            const me = this,
                cy = this.getViewModel().get('cy'),
                relationStore = this.getStore('relationStore');

            Ext.Array.forEach(records, function (record, index, allrecords) {
                const cId = record.get('_destinationId'),
                    cType = record.get('_destinationType');

                if (!me.isLoaded(cId, cType)) {
                    // Fill cytoscape nodes and edges
                    cy.add(me.cytoNode(record, id));
                    cy.add(me.cytoEdge(record, id));

                    // Fill the memory store;
                    relationStore.insert(0, record);
                } else {
                    if (cy.$('#' + record.get('_id')).length === 0) {
                        // Fill cytoscape edge
                        cy.add(me.cytoEdge(record, id));

                        // Fill the memory store;
                        relationStore.insert(0, record);
                    }
                }
            });
        },

        /**
         * @param {String} id The id of a specific node
         * @param {String} type the type of the specific node
         * @param {String} description the description of the node
         * @param {String} code
         * @param {[Ext.data.Model]} records  The relations of the node whit id = id //CMDBuildUI.model.domains.Relation
         * @returns {[Ext.data.Model]} the records to insert into the cytoscape
         */
        compoundCytoscape: function (id, type, description, code, records) {
            const me = this,
                vm = this.getViewModel(),
                clusteringThreshold = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.relgraph.clusteringThreshold
                ),
                memoryNodeDataStructure = vm.get('memoryNodeDataStructure'),
                _map_Node_CompoundName = vm.get('_map_Node_CompoundName'),
                newRecords = [],
                oldRecords = [],
                tmpOldRecords = [];

            Ext.Array.forEach(records, function (record, index, allRecords) {
                const node = memoryNodeDataStructure.getNode(record.get('_destinationId'));
                if (node) {
                    oldRecords.push(record);
                } else {
                    newRecords.push(record);
                }
            });

            // Handles the relations with already existing nodes
            Ext.Array.forEach(oldRecords, function (oldRecord, index, allOldRecords) {
                const compoundId = _map_Node_CompoundName[oldRecord.get('_destinationId')];

                //Handle the case in which the existing nodes are compound nodes
                if (compoundId) {
                    let newRecord = Ext.Array.findBy(tmpOldRecords, function (item, index) {
                        return item.get('_destinationId') == compoundId;
                    });
                    if (!newRecord) {
                        // Here is created the relation between new nodes and existing ones under compound status
                        newRecord = Ext.create('CMDBuildUI.model.domains.Relation', {
                            _id: 'compound_' + oldRecord.get('_id') + '_ID', //the id of the relation remains the same
                            _destinationType: 'compound_' + oldRecord.get('_destinationType'),
                            _destinationId: compoundId,
                            _type: 'compound_' + oldRecord.get('_type'),
                            _destinationDescription: 'compound node',
                            _is_direct: oldRecord.get('_is_direct'),
                            _sourceId: id,
                            _sourceType: type,
                            _sourceDescription: description,
                            _sourceCode: code
                        });
                        newRecord.nodes().insert(0, oldRecord);

                        tmpOldRecords.push(newRecord);
                    } else {
                        newRecord.nodes().insert(0, oldRecord);
                    }
                } else {
                    tmpOldRecords.push(oldRecord);
                }
            });

            // Search for new nodes to make as compound
            let isPresent,
                returnRecords = [];
            const countArray = me._countArray(newRecords);

            Ext.Object.each(countArray, function (key, value, myself) {
                if (value.length >= clusteringThreshold) {
                    const tmpRecord = me.cytoAdHocRecord(value);

                    //Saves information of which nodesId are in compound node
                    Ext.Array.forEach(value, function (relationRecord, index, allRelationRecords) {
                        isPresent = true;
                        const id = relationRecord.get('_destinationId');
                        _map_Node_CompoundName[id] = tmpRecord.get('_destinationId');
                    });
                    returnRecords.push(tmpRecord);
                } else {
                    returnRecords = returnRecords.concat(value);
                }
            });

            returnRecords = returnRecords.concat(tmpOldRecords);

            if (isPresent) {
                vm.set('_map_Node_CompoundName', _map_Node_CompoundName);
            }

            return returnRecords;
        },

        /**
         * @param {[Ext.data.Model]} records CMDBuildUI.model.domains.Relation
         * @returns {Ext.data.Model} CMDBuildUI.model.domains.Relation
         */
        cytoAdHocRecord: function (records) {
            //Creates the new record
            const newRecord = Ext.create('CMDBuildUI.model.domains.Relation', {
                _id: 'compound_' + records[0].get('_id') + '_ID',
                _destinationType: 'compound_' + records[0].get('_destinationType'),
                _destinationId: 'compound_' + records[0].get('_destinationId'),
                _type: 'compound_' + records[0].get('_type'),
                _destinatinCode: 'compound node',
                _destinationDescription: 'compound node',
                _is_direct: records[0].get('_is_direct'),
                _sourceId: records[0].get('_sourceId'),
                _sourceType: records[0].get('_sourceType'),
                _sourceCode: records[0].get('_sourceCode'),
                _sourceDescription: records[0].get('_sourceDescription')
            }); //NOTE:Don't change the compound name, it's used

            newRecord.nodes().insert(0, records);
            return newRecord;
        },

        /**
         *
         * @param {[Ext.data.Model]} records
         * @param {Boolean} skip
         * @returns {Object} The structure of the returned object:
         * {
         *  'type1': [record1, record2, record3],
         *  'type2': [id4, id5, id6]
         * }
         * @type {Ext.data.Model} record1, record2, ...
         *
         */
        _countArray: function (records) {
            const returned = {};
            Ext.Array.forEach(records, function (record, index, allRecords) {
                const type = record.get('_type');
                if (!returned[type]) {
                    returned[type] = [record];
                } else {
                    returned[type].push(record);
                }
            });
            return returned;
        },

        /**
         * This function fills the memoryNodedataStructure
         * @param {String} id
         * @param {Ext.data.Mode} records
         */
        fillMemoryNodeDataStructure: function (id, records) {
            const memoryNodeDataStructure = this.getViewModel().get('memoryNodeDataStructure'),
                parentNode = memoryNodeDataStructure.getNode(id),
                childs = [];

            // Finds the relations that are new
            const oldRecords = [];
            Ext.Array.forEach(records, function (record, index, allRecords) {
                const node = memoryNodeDataStructure.getNode(record.get('_destinationId'));
                if (!node) {
                    oldRecords.push(record);
                }
            });

            const countArray = this._countArray(oldRecords),
                clusteringThreshold = CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.relgraph.clusteringThreshold
                );

            if (parentNode) {
                Ext.Array.forEach(records, function (record, index, allRecords) {
                    const config = countArray[record.get('_type')] ? countArray[record.get('_type')] : [];
                    childs.push({
                        id: record.get('_destinationId'),
                        type: record.get('_destinationType'),
                        description: record.get('_destinationDescription'),
                        code: record.get('_destinationCode'),
                        treeId: record.get('treeId'), //this field is not in the model. Is added when enters in the applyNavTree if selected
                        config: config.length >= clusteringThreshold ? { childLoaded: true } : null
                    });
                });

                memoryNodeDataStructure.addChildren(parentNode, childs);
            }
        },

        /**
         *
         * @param {*} id
         * @param {*} type
         * @returns
         */
        isLoaded: function (id, type) {
            const cy = this.getViewModel().get('cy'),
                nd = cy.nodes('#' + id);

            switch (nd.length) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    console.error("Can't be a different number of elements");
            }
        },

        /**
         * creates the node structure for the cytoscape node
         * @param {Ext.record} record the source of info to extract
         * @param {String} parentId
         * @returns an addable node in the cytoscape graph
         */
        cytoNode: function (record, parentId) {
            return {
                group: 'nodes',
                data: this.mapNode_2_nodes(record, parentId)
            };
        },

        /**
         * @param {Ext.data.Model} record the source of info
         * @param {Number} id the parent data in the listNodeDataStructure
         * @returns an addable edge to the cytoscape graph
         */
        cytoEdge: function (record, id) {
            return {
                group: 'edges',
                data: this.mapNode_2_edges(record, id)
            };
        },

        /**
         * This function takes a record and transform its'data. Used for cytoscape node transformation
         * @param {Object} record
         * @param {String} parentId
         * @returns the data field on a cytoscape node
         */
        mapNode_2_nodes: function (record, parentId) {
            const relationId = record.get('_id');
            let description;

            if (relationId.includes('compound')) {
                description = Ext.String.format(
                    '{0} ({1})',
                    CMDBuildUI.util.helper.ModelHelper.getObjectDescription(
                        record.get('_destinationType').replace('compound_', '')
                    ),
                    record.nodes().getRange().length
                );
            } else {
                description = record.get('_destinationDescription');
            }

            return {
                id: record.get('_destinationId'),
                type: record.get('_destinationType'),
                description: description,
                code: record.get('_destinationCode'),
                parent: parentId
            };
        },

        /**
         * This function takes a record and it's parent and transform its'data. Used for cytoscape edges creation
         * @param {Ext.data.Model} record
         * @param {Number} parent
         * @returns the data field of a cytoscape edge
         */
        mapNode_2_edges: function (record, id) {
            const cy = this.getViewModel().get('cy'),
                node = cy.nodes('#' + id),
                domainStore = Ext.getStore('domains.Domains'),
                relationId = record.get('_id'),
                relationAttributes = record.get('_relationAttributes');

            let type = record.get('_type'),
                destinationType,
                destinationDescription;

            if (relationId.includes('compound')) {
                type = record.get('_type').replace('compound_', '');
                destinationType = 'compound node';
                destinationDescription = Ext.String.format(
                    '{0} ({1})',
                    CMDBuildUI.util.helper.ModelHelper.getObjectDescription(
                        record.get('_destinationType').replace('compound_', '')
                    ),
                    record.nodes().getRange().length
                );
            } else {
                destinationType = record.get('_destinationType');
                destinationDescription = record.get('_destinationDescription');
            }

            const domain = domainStore.findRecord('_id', type);

            return {
                id: relationId,
                source: id,
                target: record.get('_destinationId'),
                type: record.get('_type'),
                isDirect: record.get('_is_direct'),
                sourceDescription: node.data('description'),
                sourceType: node.data('type'),
                descriptionDirect: domain.get('_descriptionDirect_translation'),
                descriptionInverse: domain.get('_descriptionInverse_translation'),
                destinationDescription: destinationDescription,
                destinationType: destinationType,
                relationAttributes: !Ext.isEmpty(Ext.Object.getValues(relationAttributes)) ? relationAttributes : null
            };
        },

        /**
         * Updates the selected nodes
         * @param {[Object]}
         */
        updateSelectionNode: function (nodes) {
            const vm = this.getViewModel();
            nodes = nodes || vm.get('selectedNode');
            vm.set('selectedNode', nodes);
        },

        /**
         * This function expands the compound nodes rapresented by the id
         * @param {String[]} ids
         * @param {Object} config
         * @type {Object} config.callback
         * @type {Function} config.callback.fn
         * @type {Object} config.callback.scope
         * @returns {Object} containing the parent id and the array of childs.
         */
        expandCompound: function (ids, config) {
            config = config || {};
            Ext.applyIf(config, {
                callback: null
            });

            const vm = this.getViewModel(),
                relationStore = vm.get('relationStore'),
                cy = vm.get('cy'),
                relationToAdd = {},
                _map_Node_CompoundName = vm.get('_map_Node_CompoundName');

            Ext.Array.forEach(ids, function (id, index, allIds) {
                const cyNode = cy.$('#' + id),
                    edges = cyNode.connectedEdges().toArray(),
                    relationToRemove = [],
                    edgeToRemove = [];

                Ext.Array.forEach(edges, function (edge, index, allEdges) {
                    const idEdge = edge.id(),
                        relation = relationStore.findRecord('_id', idEdge),
                        compoundRelations = relation.nodes().getRange();

                    Ext.Array.forEach(compoundRelations, function (compoundRelation, index, allCompoundRelations) {
                        const sourceId = compoundRelation.get('_sourceId');
                        delete _map_Node_CompoundName[compoundRelation.get('_destinationId')];
                        if (!relationToAdd[sourceId]) {
                            relationToAdd[sourceId] = [compoundRelation];
                        } else {
                            relationToAdd[sourceId].push(compoundRelation);
                        }
                    });

                    relationToRemove.push(relation);
                    edgeToRemove.push(idEdge);
                });
                relationStore.remove(relationToRemove);
                cy.remove(cyNode);
                CMDBuildUI.graph.threejs.SceneUtils.removeObjects([id], edgeToRemove);
            });

            vm.set('_map_Node_CompoundName', _map_Node_CompoundName);
            config.callback.fn.call(config.callback.scope || this, relationToAdd);
        },

        /**
         * Erase threejs scene
         */
        eraseThreeJsScene: function () {
            CMDBuildUI.graph.threejs.SceneUtils.destroyScene();
            const canvasView = this.getView().down('graph-canvas-canvaspanel');
            canvasView.setHtml('<div id="cy"></div>');
            const div = document.getElementById('cy');
            div.style.height = 'inherit';
        },

        /**
         * Remove all data on the stores on various tabs and clear number of elements in relation tab title
         */
        clearTabs: function () {
            const view = this.getView(),
                vm = this.getViewModel(),
                listCardView = view.down('graph-tab-cards-listcard'),
                listRelationView = view.down('graph-tab-cards-relations'),
                listCardClassView = view.down('graph-tab-cards-listclass'),
                listCardStore = listCardView.getViewModel().get('listCardStore'),
                listCardClassStore = listCardClassView.getViewModel().get('listClassStore'),
                listRelationStore = listRelationView.getViewModel().get('edgesRelationStore');

            listCardStore.removeAll();
            listCardClassStore.removeAll();
            listRelationStore.removeAll();

            vm.set(
                'relationTabTitle',
                Ext.String.format('{0} ({1})', CMDBuildUI.locales.Locales.relationGraph.cardRelations, 0)
            );
            view.down('graph-tab-tabpanel').setActiveTab(0);
        },

        /**
         * This function loads all the information for each navigationTree
         */
        loadAllDomainTrees: function () {
            const vm = this.getViewModel(),
                navStore = Ext.getStore('navigationtrees.NavigationTrees'),
                domainTrees = navStore.getRange();

            let l = domainTrees.length;

            const _auxFunction = function (length) {
                --length;
                if (length === 0) {
                    vm.set('navTreesLoaded', true);
                }
                return length;
            };

            Ext.Array.forEach(domainTrees, function (domainTree, index, allDomainTree) {
                const id = domainTree.getId(),
                    childs = domainTree.nodes();

                if (!childs.isLoaded()) {
                    CMDBuildUI.model.navigationTrees.DomainTree.load(id, {
                        callback: function (record, operation, success) {
                            if (success) {
                                navStore.add([record]);
                                l = _auxFunction(l);
                            }
                        }
                    });
                } else {
                    l = _auxFunction(l);
                }
            });
        },

        /**
         * @param {[Ext.data.Model]} records all the relation of A specific card
         * @param {String} parentTreeId The type of the parent of all those records
         * @param {String} parentTreeClass
         * @returns {[Ext.data.Model]} An array of records eliminating the ones who doesn't respect the navigation tree dependencies
         */
        applyNavigationTree: function (records, parentTreeId, parentTreeClass) {
            const navTree = this.getView().down('#chooseNavTree').getSelection();
            const navTreeName = navTree ? navTree.get('value') : null;

            switch (navTreeName) {
                case null:
                case 'init': {
                    //No filter on domains
                    return records;
                }
                default: {
                    const domainTree = Ext.getStore('navigationtrees.NavigationTrees').getById(navTreeName),
                        newRecords = [];

                    if (!parentTreeId) {
                        parentTreeId = this.traversalVisit(domainTree, parentTreeClass);
                    }
                    const domains = this.getValidDomains(domainTree, parentTreeId);

                    Ext.Array.forEach(records, function (record, index, allRecords) {
                        const domain = Ext.Array.findBy(domains, function (item, index) {
                            return item.domainName === record.get('_type');
                        });
                        if (domain) {
                            record.set('treeId', domain.treeId);
                            newRecords.push(record);
                        }
                    });
                    return newRecords;
                }
            }
        },

        /**
         * @param {Ext.data.Store} domainTree model: CMDBuildUI.model.navigationTrees.TreeNode
         * @param {String} targetClass
         * @returns {Number} Id of the class in the inOrder visit of the tree
         */
        traversalVisit: function (domainTree, targetClass) {
            const queue = [domainTree.getRoot()];
            let tmpNode;

            while (!Ext.isEmpty(queue)) {
                tmpNode = queue.shift();

                const tmpClass = tmpNode.get('targetClass');
                let tmpTargetClass = targetClass;

                while (!Ext.isEmpty(tmpTargetClass)) {
                    if (tmpClass == tmpTargetClass) {
                        return tmpNode.get('_id');
                    }
                    const item =
                        CMDBuildUI.util.helper.ModelHelper.getClassFromName(tmpTargetClass) ||
                        CMDBuildUI.util.helper.ModelHelper.getProcessFromName(tmpTargetClass);
                    tmpTargetClass = item ? item.get('parent') : '';
                }

                Ext.Array.forEach(domainTree.getChild(tmpNode.getId()), function (child, index, allChilds) {
                    queue.push(child);
                });
            }

            console.error('Should never happen');
        },

        /**
         * @param {Ext.data.Store} domainTree
         * @param {String} treeId
         * @returns {String[]} an array containing all the domain valid for that class
         */
        getValidDomains: function (domainTree, treeId) {
            const currentRecord = domainTree.getNode(treeId),
                childs = domainTree.getChild(treeId),
                domains = [];

            // Case in which the currentClass is the root
            if (currentRecord.get('domain') != '') {
                domains.push({
                    domainName: currentRecord.get('domain'),
                    treeId: currentRecord.get('parent')
                });
            }

            // Adds child domains
            Ext.Array.forEach(childs, function (child, index, allChilds) {
                domains.push({
                    domainName: child.get('domain'),
                    treeId: child.get('_id')
                });
            });

            return domains;
        },

        /**
         * NodeDataStructure type
         * @param {String} id
         * @param {String} type
         * @param {String} description
         * @param {String} code
         * @param {String} treeId
         * @param {Object} _config
         */
        NodeDataStructure: function (id, type, description, code, treeId, _config) {
            const me = this;
            this.id = id;
            this.type = type;
            this.treeId = treeId;
            this.description = description;
            this.code = code;
            this.children = [];

            // This field tells if this node has passed through the operation of child (relation) acquisition
            this.childLoaded = false;

            // This field indicates at which depth this element is found
            this.depth = null;

            this.init = function (conf) {
                if (typeof conf.depth === 'number') {
                    this.depth = conf.depth;
                }

                if (conf.childLoaded) {
                    this.childLoaded = true;
                }
            };

            this.getId = function () {
                return this.id;
            };

            this.getType = function () {
                return this.type;
            };

            this.getDescription = function () {
                return this.description;
            };
            this.setDescription = function (description) {
                this.description = description;
            };

            this.getCode = function () {
                return this.code;
            };
            this.setCode = function (code) {
                this.code = code;
            };

            this.getTreeId = function () {
                return this.treeId;
            };
            this.setTreeId = function (treeId) {
                this.treeId = treeId;
            };

            this.getDepth = function () {
                return this.depth;
            };
            /**
             * Sets the node depth. If yet inizializied doesn't change the value
             * @param {Number} depth
             */
            this.setDepth = function (depth) {
                if (typeof this.depth != 'number') {
                    this.depth = depth;
                }
            };

            /**
             * @returns {Boolean} the value of this.childLoaded
             */
            this._getChildLoaded = function () {
                return this.childLoaded;
            };
            /**
             * This function changes the internal state of the value childLoaded
             * settting that to true
             */
            this._setChildLoaded = function () {
                this.childLoaded = true;
            };

            /**
             * Adds children to the current NodeDataStructure
             * Sets depth and
             * @param {[NodeDataStructure]} nodes
             * @param {Object} config Configuration object
             */
            this.addChildren = function (nodes, config) {
                Ext.Array.forEach(nodes, function (node, index, allNodes) {
                    const yetIn = Ext.Array.findBy(me.children, function (item, index, allItems) {
                        return item.getId() == node.getId();
                    });

                    if (!yetIn) {
                        me.children.push(node); //If the node is not saved
                        node.setDepth(me.depth + 1);
                        /**
                         * Fires the related afterNodeAdd function
                         * NOTE: This event could be fire only once saving the nodes in an array: tmpNodes.push(node) and handle an array of objects instead of the single node
                         */
                        if (config.afterNodeAdd) {
                            config.afterNodeAdd.fn.call(config.afterNodeAdd.scope || me, node);
                        }
                    }
                });
                me._setChildLoaded();
            };

            if (_config) {
                this.init(_config);
            }
        },

        /**
         * ListNodeDataStructure
         * @param {Object} _config the global scope
         */
        ListNodeDataStructure: function (_config) {
            const me = this;
            this.nodes = {};

            /**
             * This field have this style:
             * {
             *  0: {
             *      _meta: {
             *          complete: true
             *      },
             *      nodes: {
             *          id1: NodeDataStructure,
             *          ...,
             *          idn: NodeDataStructure
             *      }
             *  },
             *  1: {...},
             *  ...,
             *  n: {...}
             * }
             *
             * _meta: Contains information about the set of nodes
             *  _meta.complete: Indicates if all the possible element that can be found at level n are in the set
             *  ES:
             *      at level 0 all the elements that can be found in that level are present by default so depth[0]._meta.complete = true
             *      at level 1 can be found all the childs (suppose to be 2 elements) of the root so depth[1]._meta.complete = true
             *      at level 2 if i load only one element of the level 1 the depth[2]._meta.complete = false. After loading the other element yet not loaded and adding his child i can set depth[2]._meta.complete = true
             */
            this.depth = {};
            this.me = null;

            this._init = function (conf) {
                if (!conf.scope) {
                    console.error('Define scope');
                } else {
                    this.me = conf.scope;
                }

                if (!conf.root) {
                    console.error('Define Root');
                } else {
                    const tmpNodeDataStructure = new this.me.NodeDataStructure(
                        conf.root._id,
                        conf.root._type,
                        conf.root._description,
                        conf.root._code,
                        conf._tree_id,
                        { depth: 0 }
                    );
                    //add the node in this.nodes
                    this._saveNode(tmpNodeDataStructure);
                    //sync the this.depth
                    this.depth[0] = {
                        _meta: {
                            complete: true
                        },
                        nodes: {}
                    };
                    this.depth[0].nodes[conf.root._id] = tmpNodeDataStructure;
                }
            };

            /**
             *
             * @param {String[]} ids array of ids
             * @returns {[NodeDataStructure]}
             */
            this.getNodes = function (ids) {
                const tmpNodes = [];

                Ext.Array.forEach(ids, function (id, index, allIds) {
                    const tmpNode = me.nodes[id];
                    if (tmpNode) {
                        tmpNodes.push(tmpNode);
                    } else {
                        console.error('cant find that node');
                    }
                });

                return tmpNodes;
            };

            /**
             *
             * @param {Number} id
             * @returns {NodeDataStructure} the node with that id
             */
            this.getNode = function (id) {
                const node = this.nodes[id];
                return node ? node : null;
            };

            /**
             * Adds nodes in the this.nodes object
             * @param {NodeDataStructure} node
             */
            this._saveNode = function (node) {
                const id = node.getId();
                if (!this.nodes[id]) {
                    this.nodes[id] = node;
                } else {
                    console.error('Duplicated Id');
                }
            };

            /**
             *
             * @param {NodeDataStructure} parentNode
             * @param {Object} childNodes
             * childNodes.id
             * childNodes.type
             */
            this.addChildren = function (parentNode, childNodes) {
                const tmpAdds = [];

                Ext.Array.forEach(childNodes, function (childNode, index, allChildNodes) {
                    let tmpNode = me.getNode(childNode.id);
                    if (!tmpNode) {
                        tmpNode = new me.me.NodeDataStructure(
                            childNode.id,
                            childNode.type,
                            childNode.description,
                            childNode.code,
                            childNode.treeId,
                            childNode.config
                        );
                        me._saveNode(tmpNode);
                    }
                    tmpAdds.push(tmpNode);
                });

                parentNode.addChildren(tmpAdds, {
                    /**
                     * This function modifies the this.depth list
                     * @param {DataNodeStructure} node
                     */
                    afterNodeAdd: {
                        fn: function (node) {
                            const depth = node.getDepth();

                            if (!me.depth[depth]) {
                                me.depth[depth] = {
                                    _meta: {
                                        complete: false
                                    },
                                    nodes: {}
                                };
                            }

                            me.depth[depth].nodes[node.getId()] = node;
                        }
                    }
                });
            };

            /**
             * @param {Number} depth
             * @param {Object} config
             * config.all if true load all the childs else only the ones with not loaded. Default is true
             * @returns {[NodeDataStructure]}
             */
            this.getNodesAtdepth = function (depth, config) {
                config = config || {};
                Ext.applyIf(config, {
                    all: true
                });
                if (!this.depth[depth]) {
                    console.error('No elements at this depth');
                }

                const tmpIds = [];
                Ext.Object.each(this.depth[depth].nodes, function (key, value, myself) {
                    if (config.all) {
                        tmpIds.push(key);
                    } else {
                        if (!value.childLoaded) {
                            tmpIds.push(key);
                        }
                    }
                });

                return this.getNodes(tmpIds);
            };

            /**
             * This function returns the last depth at wich the _meta.complete = true
             * Each time this function is called the values are recalculated in a efficient way
             * @returns {Number} the last complete depth load
             */
            this.getLastDepth = function () {
                const maxDepth = this._getMaxDepth();
                let lastComplete = maxDepth;

                while (!this.depth[lastComplete]._meta.complete) {
                    lastComplete--;
                }

                // lastComplete have always the .complete field = true
                while (lastComplete != maxDepth) {
                    if (this._areAllNodesLoaded(lastComplete)) {
                        lastComplete++;
                        this.depth[lastComplete]._meta.complete = true;
                    } else {
                        return lastComplete;
                    }
                }

                return lastComplete;
            };

            /**
             * @param {Number} depth
             * @returns {Boolean} true if all the nodes in that depth have NodeDateStructure.childLoaded = true; False otherwise
             */
            this._areAllNodesLoaded = function (depth) {
                let allNodesLoaded = true;
                Ext.Object.each(this.depth[depth].nodes, function (key, value, myself) {
                    if (!value._getChildLoaded()) {
                        allNodesLoaded = false;
                        return false;
                    }
                });

                return allNodesLoaded;
            };

            /**
             * @returns {Number} the maximum depth reached
             */
            this._getMaxDepth = function () {
                let maxDepth = 0;

                Ext.Object.each(this.depth, function (key, value, myself) {
                    if (parseInt(key) > parseInt(maxDepth)) {
                        maxDepth = key;
                    }
                });

                return maxDepth;
            };

            this.isComplete = function () {
                return this._areAllNodesLoaded(this._getMaxDepth()) ? true : false;
            };

            // Start configuration
            this._init(_config);
        }
    }
});
