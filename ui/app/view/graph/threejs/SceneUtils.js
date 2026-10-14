Ext.define('CMDBuildUI.graph.threejs.SceneUtils', {
    singleton: true,

    /**
     * @param {Object} data
     * @type {String} data.containerView
     * @type {cy.nodes} data.cy
     */
    init: function (data) {
        this.reset();
        this.cy = data.cy;
        //saves the container view
        this.containterView = data.containterView;

        this.createScene();
        this.fillScene(data.cy);
        this.tooltip = new this.tooltipContext(this.tooltipConf, '', '');
        this.renderFunction();
    },

    /**
     *
     */
    destroyScene: function () {
        this.renderSceneVariable = false;
        this.tooltip.destroy();
    },

    /**
     * This function adds in the scene the sphere according to the cy nodes
     * @param {cytoscape} cy graph rapresented by cytoscape. Have information about edges and nodes
     */
    fillScene: function (cy) {
        const me = this,
            objects = [];

        Ext.Array.forEach(cy.nodes().toArray(), function (node, index, allnodes) {
            if (!me.nodeRegister[node.id()]) {
                me.nodeRegister[node.id()] = true; //save the nodeRegister new data
                me.fillNode(node, objects);
            }
        });

        Ext.Array.forEach(cy.edges().toArray(), function (edge, index, alledges) {
            if (!me.edgeRegister[edge.id()]) {
                me.edgeRegister[edge.id()] = true; //save the edgeRegister new data
                me.fillEdge(edge);
            }
        });

        // Add drag control
        if (!Ext.isEmpty(objects)) {
            this.addDragControl(objects);
        }

        CMDBuildUI.graph.threejs.GuiSphere.run(this.scene, cy, this.camera);
    },

    /**
     * Change visibility of labels
     * @param {Boolean} value
     */
    enableLabels: function (value) {
        Ext.Array.forEach(this.cy.nodes(), function (node, index, allnodes) {
            if (node.glLabel) {
                node.glLabel.visible = value;
            }
        });

        Ext.Array.forEach(this.cy.edges(), function (edge, index, alledges) {
            if (edge.glLabel) {
                edge.glLabel.visible = value;
            }
        });
        this.allLabelsEnabled = value;
    },

    /**
     * This function modifies the scene line associated to the object
     * @param {Mesh.Sprite} scene_object_1 sprite being moved
     */
    modifyLine: function (scene_object_1) {
        const me = this,
            id1 = scene_object_1.name,
            cy_node = this.cy.nodes('#' + id1),
            edges = cy_node.connectedEdges().toArray();

        Ext.Array.forEach(edges, function (edge, index, alledges) {
            //get id2 id
            var id2 = edge.source().id();
            if (id1 == id2) {
                id2 = edge.target().id();
            }

            const scene_object_2 = me.scene.getObjectByName(id2),
                scene_line = edge.glLine;

            scene_line.geometry.setFromPoints([scene_object_1.position, scene_object_2.position]);

            var label = edge.glLabel;
            label.position.set(
                (scene_object_1.position.x + scene_object_2.position.x) / 2,
                (scene_object_1.position.y + scene_object_2.position.y) / 2,
                (scene_object_1.position.z + scene_object_2.position.z) / 2
            );
        });
    },

    /**
     * @param {String[]} spriteIds
     * @param {[lineIds]} lineIds
     */
    removeObjects: function (spriteIds, lineIds) {
        const me = this;
        Ext.Array.forEach(spriteIds, function (spriteId, index, allSprites) {
            const spriteObj = me.findObjectInScene('name', spriteId);
            if (spriteObj) {
                me.scene.remove(spriteObj);
                delete me.nodeRegister[spriteId];
            } else {
                throw new Error('Is Not Possible, must be here this element with this id');
            }
        });

        Ext.Array.forEach(lineIds, function (lineId, index, allLineId) {
            const lineObj = me.findObjectInScene('name', lineId);
            if (lineObj) {
                me.scene.remove(lineObj);
                delete me.edgeRegister[lineId];
            } else {
                throw new Error('Is Not possible');
            }
        });
    },

    /**
     *
     * @param {[Number]} ids the Number containing the id of the nodes interessed
     * @param {Boolean} add if true none element will be deleted, if false the old element will be deleted. DEFAULT false;
     */
    setSelectedNode: function (ids, add) {
        const me = this;
        add = add || false;
        Ext.Array.forEach(ids, function (id, index, allids) {
            if (me.selectedNode[id]) {
                me.selectedNode[id].deletable = 'persistent';
            } else {
                const object = me.findObjectInScene('name', id);
                me.selectedNode[id] = {
                    object: object,
                    oldColor: me.colorManage('copy', object.material.color),
                    deletable: false
                };
                me.spriteManage('add', id);
            }
        });

        const selectionIds = [];

        Ext.Object.each(me.selectedNode, function (key, value, allel) {
            if (me.selectedNode.hasOwnProperty(key)) {
                if (add) {
                    if (value.deletable === 'persistent') {
                        //delete the sprite
                        me.spriteManage('remove', key);
                        delete me.selectedNode[key];
                    } else {
                        value.deletable = true;
                        selectionIds.push(key);
                    }
                } else if (value.deletable === true && !add) {
                    me.spriteManage('remove', key);
                    delete me.selectedNode[key];
                } else {
                    value.deletable = true;
                    selectionIds.push(key);
                }
            }
        });

        Ext.GlobalEvents.fireEventArgs('sceneselectednode', [selectionIds]);
    },

    /**
     * This functions sets the camera target in the baricenter of the graph
     */
    setCameraTarget: function () {
        const nodes = this.cy.nodes().toArray(),
            baricenter = this.calculateBaricenter(nodes);
        this.controls.target = baricenter;
    },

    privates: {
        /**
         * If true the request animationFrame can continue
         */
        renderSceneVariable: true,

        /**
         * Saves tooltip informations
         */
        tooltipConf: {
            /**
             * Coordinates of the mouse
             */
            mouseX: null,
            mouseY: null,
            enable: true
        },

        allLabelsEnabled: false,

        /**
         * Saves the information about the selected Node in sceneJS
         */
        selectedNode: {},

        /**
         *
         */
        clickCTRL: false,

        /**
         * This object saves the cytoscape id of the node that have a related three.js sphere
         * {1234: true,
         * 1233: true}
         *
         * Is't easy to tell if a node has been created or not
         * TODO: replace the findInSceneFunction
         */
        nodeRegister: {},

        /**
         * This object saves the cytoscape id of the edge that have a related three.js line
         * {1234: true,
         * 1233: true}
         *
         * Is't easy to tell if a node has been created or not
         */
        edgeRegister: {},

        /**
         * This is a class which handless the tooltip
         * @param {*} tooltipConf
         * @param {*} text
         */
        tooltipContext: function (tooltipConf, text) {
            this.text = text || '';
            this.panel = null;
            this.enable = tooltipConf.enable;
            this.pause = tooltipConf.pause || false;
            this.tooltipConf = tooltipConf;

            this.init = function () {
                this.panel = Ext.create('Ext.panel.Panel', {
                    width: 200,
                    renderTo: Ext.getBody(),
                    hidden: true
                });
                this.panel.ariaEl.dom.style.zIndex = 190000;
            };

            /**
             *
             * @param {String} mode node or relation
             * @param {Object} config
             */
            this.setText = function (mode, config) {
                this.panel.setHtml();
                switch (mode) {
                    case 'node':
                        if (config) {
                            this.panel.setHtml(
                                Ext.String.format(
                                    '<div class="relgraph-tip relgraph-tip-node">{0}: {1}',
                                    config.id.includes('compound')
                                        ? CMDBuildUI.locales.Locales.relationGraph.compoundnode
                                        : CMDBuildUI.util.helper.ModelHelper.getObjectDescription(config.type),
                                    config.description
                                )
                            );
                        }
                        break;
                    case 'relation':
                        const createLabel = function (relation) {
                            var attributes;
                            if (relation.relationAttributes) {
                                attributes = '<div class="relgraph-tip relgraph-tip-attributes">';
                                Ext.Array.forEach(
                                    Ext.Object.getKeys(relation.relationAttributes),
                                    function (item, index, allitems) {
                                        const value = relation.relationAttributes[item];
                                        if (value) {
                                            attributes = attributes.concat(
                                                Ext.String.format('<b>{0}</b>:<br>{1}<br>', item, value)
                                            );
                                        }
                                    }
                                );
                                attributes = attributes.concat('</div>');
                            }

                            return Ext.String.format(
                                '<div class="relgraph-tip relgraph-tip-relation">{0}: {1}</br><b>{2}</b></br>{3}: {4}{5}</div>',
                                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(relation.sourceType),
                                relation.sourceDescription,
                                relation.isDirect ? relation.descriptionDirect : relation.descriptionInverse,
                                CMDBuildUI.util.helper.ModelHelper.getObjectDescription(relation.destinationType),
                                relation.destinationDescription,
                                attributes
                            );
                        };

                        var label = '';
                        Ext.Array.forEach(config, function (item, index, allitems) {
                            if (index > 0) {
                                label = label.concat('<hr>');
                            }
                            label = label.concat(createLabel(item));
                        });

                        this.panel.setHtml(label);
                        break;
                }
            };

            this.setEnable = function (enable) {
                this.enable = enable;

                if (!enable) {
                    this.destroy();
                }
            };
            this.getEnable = function () {
                return this.enable;
            };

            this.pauseStart = function () {
                this.pause = true;
            };
            this.pauseEnd = function () {
                this.pause = false;
            };

            this.open = function () {
                if (this.panel && this.enable && !this.pause) {
                    this.panel.setVisible(true);
                    this.position();
                }
            };
            this.isOpen = function () {
                if (this.panel) {
                    return this.panel.isVisible();
                }
            };

            this.close = function () {
                if (this.panel && this.enable && !this.pause) {
                    this.panel.setVisible(false);
                }
            };

            this.destroy = function () {
                if (this.panel && this.enable) {
                    this.panel.destroy();
                }
            };

            this.position = function () {
                const left = this.tooltipConf.mouseX,
                    top = this.tooltipConf.mouseY;

                if (this.panel && this.panel.ariaEl.dom && this.enable && !this.pause) {
                    this.panel.ariaEl.dom.style.top = top - this.panel.getHeight() - 20 + 'px';
                    this.panel.ariaEl.dom.style.left = left - this.panel.getWidth() / 2 + 'px';
                }
            };

            this.init();
            this.setText('');
            this.position();
        },

        /**
         * Reset the private variables
         */
        reset: function () {
            this.tooltipConf = {
                //Gets the pressed state of the button
                enable: CMDBuildUI.graph.util.canvasMenu.getComponent('#enableTooltip').pressed //HACK: Disable tooltip
            };
            this.selectedNode = {};
            this.renderSceneVariable = true;
            this.clickCTRL = false;
            this.nodeRegister = {};
            this.edgeRegister = {};
            this.containterView = null;
        },

        /**
         * The render function required by threejs
         */
        renderFunction: function () {
            const me = this;
            const render = function () {
                me.raycaster.setFromCamera(me.mouse, me.camera);
                me.controls.update();
                me.mouseMoveInteraction();

                me.renderer.render(me.scene, me.camera);
                if (me.renderSceneVariable) {
                    requestAnimationFrame(render);
                } else {
                    /**
                     * Threejs saved variables reset
                     */
                    this.render = null;
                    this.camera = null;
                    this.raycaster = null;
                    this.controls = null;
                    this.scene = null;
                    this.mouse = null;
                }
            };
            render();
            this.camera.position.z = 20;
            this.renderer.setClearColor(0xffffff);
        },

        /**
         *Creates the basic scene adding necessary controls
         */
        createScene: function () {
            this.div = Ext.getElementById('cy');
            this.scene = new THREE.Scene();
            this.camera = new THREE.PerspectiveCamera(60, this.div.clientWidth / this.div.clientHeight, 1, 3000);

            this.renderer = new THREE.WebGLRenderer();

            this.renderer.setSize(this.div.clientWidth, this.div.clientHeight);
            this.div.appendChild(this.renderer.domElement);

            // Adds the raycast function
            this.addInteractionControls();
            this.manageEventListeners('add');

            // Add trackballControls. NOTE: this method must stay after 'this.div.append(this.renderer.domElement);'
            this.addTrackballControl();
        },

        /**
         * Adds the trackballControls
         */
        addTrackballControl: function () {
            this.controls = new TrackballControls.TrackballControls(this.camera, this.renderer.domElement);
            this.controls.rotateSpeed = 1.5;
            this.controls.zoomSpeed = 1.2;
            this.controls.panSpeed = 0.8;
            this.controls.noZoom = false;
            this.controls.noPan = false;
            this.controls.staticMoving = false;
            this.controls.dynamicDampingFactor = 0.3;
        },

        /**
         * This function handles the mouse coordinates change and the creation of the raycast vector
         */
        addInteractionControls: function () {
            const me = this;
            this.raycaster = new THREE.Raycaster();
            this.mouse = new THREE.Vector2();

            // Adds the click control
            this.addClickControl();

            this.div.onmousemove = function (event) {
                me.mouse.x = (event.offsetX / me.renderer.domElement.width) * 2 - 1;
                me.mouse.y = -(event.offsetY / me.renderer.domElement.height) * 2 + 1;

                // Save tooltip informations
                me.tooltipConf.mouseX = event.clientX;
                me.tooltipConf.mouseY = event.clientY;
            };
        },

        /**
         * Here is handled the intersection between the mouse and the objects in the scene
         */
        mouseMoveInteraction: function () {
            const intersects = this.raycaster.intersectObjects(this.scene.children),
                vmGraph = this.containterView.getViewModel();

            // Handles the tooltip
            if (Ext.isEmpty(intersects)) {
                if (this.tooltip.isOpen()) {
                    this.tooltip.close();
                    vmGraph.set('pointerExternalCanvas', true);
                }
            } else {
                if (this.tooltip.getEnable()) {
                    //Need to be always opened to set the position
                    this.tooltip.open();

                    const object = Ext.Array.findBy(intersects, function (item, index) {
                        return ['Sprite', 'Line'].indexOf(item.object.type) > -1;
                    });

                    if (vmGraph && vmGraph.get('pointerExternalCanvas')) {
                        const type = object.object.type,
                            name = object.object.name;
                        if (type == 'Sprite') {
                            this.getNodeInformation(name, function (obj) {
                                this.tooltip.setText('node', obj);
                            });
                        } else if (type == 'Line') {
                            this.getEdgeInformation(name, function (obj) {
                                this.tooltip.setText('relation', obj);
                            });
                        }
                    }
                } else {
                    if (this.tooltip.isOpen()) {
                        this.tooltip.close();
                    }
                }
            }
        },

        /**
         * This function handles the add an the remove of keyDown and KeyUp event listeners
         * @param {String} method the config string, can be add || remove;
         */
        manageEventListeners: function (method) {
            const me = this,
                win = Ext.getWin();

            /**
             * Handler event for the keydown event
             * @param {Object} eOpts
             */
            const keyDownFunction = function (eOpts) {
                eOpts = eOpts.browserEvent;
                if (eOpts.key == 'Control') {
                    me.clickCTRL = true;
                }
            };

            /**
             * Handler function for the keyup event
             * @param {Object} eOpts
             */
            const keyUpFunction = function (eOpts) {
                eOpts = eOpts.browserEvent;
                if (eOpts.key == 'Control') {
                    me.clickCTRL = false;
                }
            };

            switch (method) {
                case 'add':
                    me.containterView.mon(win, 'keydown', keyDownFunction, me);
                    me.containterView.mon(win, 'keyup', keyUpFunction, me);
                    break;
            }
        },

        /**
         *
         * @param {*} node
         * @param {*} objects
         */
        fillNode: function (node, objects) {
            const sprite = this.createSprite(node.data().type, node.id());
            objects.push(sprite); //insert the three.js object in the array
            this.scene.add(sprite); //add three.js object to the scene

            //create the related label
            const message = Ext.String.format(
                '{0}: {1}',
                Ext.String.startsWith(node.data().type, 'compound_')
                    ? CMDBuildUI.locales.Locales.relationGraph.compoundnode
                    : CMDBuildUI.util.helper.ModelHelper.getObjectDescription(node.data().type),
                node.data().description
            );
            const label = this.createLabel(message, 'node');
            sprite.add(label);

            node.glSprite = sprite; //save the three.js object in cytoscape as a reference
            node.glLabel = label;
        },

        /**
         *
         * @param {*} edge
         */
        fillEdge: function (edge) {
            const line = this.createLine(edge);
            this.scene.add(line);

            //creates the related label
            const message = Ext.String.format('{0}', edge.data().descriptionDirect),
                label = this.createLabel(message, 'edge');
            line.add(label);

            edge.glLine = line;
            edge.glLabel = label;
        },

        /**
         * This function creates the mesh sphere.
         * @param {String} type
         * @param {String} id
         * @param {String} imgType Type Parameters for a specific image
         * @returns the THREE.Mesh object
         */
        createSprite: function (type, id, imgType) {
            var spriteMap;
            const image = this.getImage(
                type,
                function (imgType, event) {
                    if (imgType == 'selected') {
                        sprite.scale.set(2, 2, 1);
                    } else {
                        const step_radius = CMDBuildUI.util.helper.Configurations.get(
                            CMDBuildUI.model.Configuration.relgraph.node.stepRadius
                        );
                        var scale_x = 1,
                            scale_y = 1;

                        scale_x = scale_x * (step_radius / 60);
                        scale_y = scale_y * (step_radius / 60);

                        sprite.scale.set(scale_x, scale_y, 1);
                    }
                    spriteMap.needsUpdate = true;
                },
                imgType
            );

            spriteMap = new THREE.Texture(image);
            const spriteMaterial = new THREE.SpriteMaterial({ map: spriteMap }),
                sprite = new THREE.Sprite(spriteMaterial);

            sprite.name = id;
            sprite._isNode = true;
            return sprite;
        },

        /**
         *
         * @param {String} text
         * @param {Object} type
         * @returns
         */
        createLabel: function (text, type) {
            const fontsize = 64,
                fontface = 'Arial';
            text = text || '';

            const canvas = document.createElement('canvas');
            canvas.width = 1;
            canvas.height = 2.75 * fontsize; //2 * fontsize;
            const context = canvas.getContext('2d');

            // Sets the context text parameters
            const setContextText = function () {
                context.font = Ext.String.format('{0}px {1}', fontsize, fontface);
                context.textBaseline = 'alphabetic';
                context.textAlign = 'left';
                context.fillStyle = '#FFFFFF';
            };

            // Calculates the text metrics
            setContextText();
            const metrics = context.measureText(text);

            // Set the canvas width 5% longer than the text. Use ceil to have integer numbers
            canvas.width = metrics.width + metrics.width * (20 / 100);

            //calculates text position
            const textWidth = metrics.width,
                cx = canvas.width / 2,
                cy = canvas.height / 2,
                tx = textWidth / 2,
                ty = fontsize / 2;

            // Calculates backgroud color of the label
            if (type == 'edge') {
                context.fillStyle = 'rgba(116, 116, 116, 1)';
            } else if (type == 'node') {
                context.fillStyle = 'rgba(0, 92, 168, 1)'; //$basecolor color
            }

            // Draws the label backgroud
            context.fillRect(0, 0, canvas.width, canvas.height);

            // Writes the text in the context
            setContextText();
            context.fillText(text, cx - tx, cy + ty);

            // Creates the sprite label
            const spriteMap = new THREE.CanvasTexture(canvas);
            spriteMap.needsUpdate = true;

            const spriteMaterial = new THREE.SpriteMaterial({ map: spriteMap, color: 0xffffff, fog: false });
            spriteMaterial.sizeAttenuation = false;

            const sprite = new THREE.Sprite(spriteMaterial);

            // Sets the scale. This maintains the correct proportion
            const factor = 16;
            sprite.scale.set(canvas.width / canvas.height / factor, 1 / factor, 1);

            // Sets the position of the label higher than his parent
            sprite.position.set(0, 1.5, 0);

            // Sets the visibility of the label
            sprite.visible = this.allLabelsEnabled;

            sprite.name = Ext.String.format('{0}_{1}', 'label', type);
            return sprite;
        },

        /**
         * This function creates a line rapresenting the edge between two nodes in the graph
         * @param {} edge the cytoscape edge description
         * @returns the mesh.Line
         */
        createLine: function (edge) {
            const source = edge.source(),
                target = edge.target(),
                scene_source = this.scene.getObjectByName(source.id()),
                scene_target = this.scene.getObjectByName(target.id()),
                p1 = scene_source.position,
                p2 = scene_target.position,
                line_material = new THREE.LineBasicMaterial({
                    color: CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.relgraph.edge.color
                    ),
                    linewidth: 1
                }),
                line_geometry = new THREE.BufferGeometry().setFromPoints([p1, p2]),
                line = new THREE.Line(line_geometry, line_material);

            line.name = edge.id();
            return line;
        },

        /**
         * This function adds the drag controls to objects
         * @param {[objects]} objects an array of objects that are draggable
         */
        addDragControl: function (objects) {
            const me = this,
                dragControls = new DragControls.DragControls(objects, this.camera, this.renderer.domElement);

            dragControls.addEventListener('dragstart', function (objectF) {
                me.controls.enabled = false;
                me.modifyLineBool = true;

                me.tooltip.close();
                me.tooltip.pauseStart();

                // Modifies the edges
                const callTymeOut = function (object) {
                    if (me.modifyLineBool) {
                        setTimeout(function () {
                            me.modifyLine(object);
                            callTymeOut(object);
                        }, 20);
                    }
                };

                callTymeOut(objectF.object);
            });

            dragControls.addEventListener('dragend', function () {
                me.controls.enabled = true;
                me.tooltip.pauseEnd();
                delete me.modifyLineBool;
                me.setCameraTarget();
            });
        },

        /**
         * this function handles the click on a node
         */
        addClickControl: function () {
            const me = this;
            me.div.onclick = function () {
                me.raycaster.setFromCamera(me.mouse, me.camera);
                const intersects = me.raycaster.intersectObjects(me.scene.children),
                    node = Ext.Array.findBy(intersects, function (item, index) {
                        return (
                            ['Sprite', 'Mesh'].indexOf(item.object.type) > -1 &&
                            item.object._isNode &&
                            item.object.name !== 'selected'
                        );
                    });

                if (node) {
                    me.setSelectedNode([node.object.name], me.clickCTRL);
                }
            };

            me.div.ondblclick = function () {
                const intersects = me.raycaster.intersectObjects(me.scene.children),
                    node = Ext.Array.findBy(intersects, function (item, index) {
                        return (
                            ['Sprite', 'Mesh'].indexOf(item.object.type) > -1 &&
                            item.object._isNode &&
                            item.object.name !== 'selected'
                        );
                    });

                if (node) {
                    Ext.GlobalEvents.fireEventArgs('doubleclicknode', [[node.object.name]]);
                }
            };

            me.div.oncontextmenu = function (eOpts) {
                eOpts.preventDefault();
            };
        },

        /**
         *
         * @param {String} action The action to take: add or remove
         * @param {String} id The id of the sprite selected
         */
        spriteManage: function (action, id) {
            var selectedSprite;
            const parentSprite = this.findObjectInScene('name', id);

            if (parentSprite && parentSprite.children) {
                selectedSprite = Ext.Array.findBy(parentSprite.children, function (item, index) {
                    return item.name == 'selected';
                });
            }

            switch (action) {
                case 'add':
                    //if not adds
                    if (!selectedSprite) {
                        const newSprite = this.createSprite(null, null, 'selected');
                        newSprite.name = 'selected';

                        newSprite.position.x = 0;
                        newSprite.position.y = 0;
                        newSprite.position.z = 0;

                        parentSprite.add(newSprite);
                    }
                    break;
                case 'remove':
                    try {
                        //if found remove it
                        if (selectedSprite) {
                            parentSprite.remove(selectedSprite);
                        }
                    } catch (err) {
                        console.error('should happen only after removing a compound');
                    }
                    break;
            }
        },

        /**
         * a self made function for finding objects in the scene
         * @param {String} name the name of the param to look for
         * @param {} value the value of the name
         */
        findObjectInScene: function (name, value) {
            var el;

            Ext.Array.each(this.scene.children, function (item, index, allitem) {
                if (item[name] == value) {
                    el = item;
                    return false;
                }
            });

            return el;
        },

        /**
         * This function finds the data saved in the cytoscape libraries relate to a specific node
         * @param {String} nodeId the id of the node saved in the cytoscape
         * @param {function} callback the callbackFunction to call
         * @param {Object} scope
         */
        getNodeInformation: function (nodeId, callback, scope) {
            scope = scope || this;
            const data = this.cy.nodes('#' + nodeId).data();
            callback.call(scope, data);
        },

        /**
         * This function finds the data saved in the cytoscape libraries relate to specifics nodes
         * @param {String} edgesId the ids of the nodes saved in the cytoscape
         * @param {function} callback the callbackFunction to call
         * @param {Object} scope
         */
        getEdgeInformation: function (edgesId, callback, scope) {
            const me = this;
            var data = [];

            scope = scope || this;
            Ext.Array.each(edgesId, function (item, index, allitems) {
                const element = me.cy.edges('#' + item).data();
                if (index > 0 && (data[0].source != element.source || data[0].target != element.target)) {
                    data = Ext.Array.from(data[0]);
                    return false;
                } else {
                    data.push(element);
                }
            });
            callback.call(scope, data);
        },

        /**
         * This function manages different functions for the specific color vector
         * @param {*} op
         * @param {*} value1
         * @param {*} value2
         * @returns
         */
        colorManage: function (op, value1, value2) {
            switch (op) {
                case 'copy':
                    return {
                        r: value1.r,
                        b: value1.b,
                        g: value1.g
                    };
                case 'set':
                    value1.material.color.r = value2.r;
                    value1.material.color.b = value2.b;
                    value1.material.color.g = value2.g;
                    break;
            }
        },

        /**
         * This function gets some cyNodes and calculates the baricenter of the structure by its scene position
         * @param {cy.nodes} nodes nodes.glSprite contains the related threejs element
         * @returns the point rapresenting the baricenter of that
         */
        calculateBaricenter: function (nodes) {
            var sum_x = 0,
                sum_y = 0,
                sum_z = 0;
            const length = nodes.length;
            Ext.Array.forEach(nodes, function (node, index, allnodes) {
                if (node.glSprite) {
                    const pos = node.glSprite.position;
                    sum_x += pos.x;
                    sum_y += pos.y;
                    sum_z += pos.z;
                }
            });

            return new THREE.Vector3(sum_x / length, sum_y / length, sum_z / length);
        },

        /**
         * This function returns an img object
         * @param {String} type
         * @param {Function} onLoad
         * @param {*} imgType
         * @returns
         */
        getImage: function (type, onLoad, imgType) {
            const image = Ext.dom.Element.create(
                {
                    tag: 'img'
                },
                'img'
            );

            image.crossOrigin = 'use-credentials';
            image.onload = function (imgType, evt) {
                onLoad.call(this, imgType, evt);
            }.bind(image, imgType);

            imgType = imgType || null;
            switch (imgType) {
                //return the class image if set. Return Default image;
                case null:
                    //Assertion if null imgType --> type != null should allways bew set
                    const tmpType = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(type);
                    var icon;

                    if (tmpType) {
                        // tipo trovato
                        icon = tmpType.get('_icon');
                    } else {
                        //tipo non trovato --> compound Node
                        image.src = Ext.getResourcePath('images/compound.png', 'shared');
                        return image;
                    }

                    if (icon) {
                        image.src = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, icon);
                        image.onerror = function (e) {
                            //Handles the error on loading image from server
                            const objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(type);
                            switch (objectType) {
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                    image.src = Ext.getResourcePath('images/default.png', 'shared');
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                    image.src = Ext.getResourcePath('images/process.png', 'shared');
                                    break;
                                default:
                                    console.error('Should be a type');
                            }
                        };
                    } else {
                        const objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(type);
                        switch (objectType) {
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                image.src = Ext.getResourcePath('images/default.png', 'shared');
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                image.src = Ext.getResourcePath('images/process.png', 'shared');
                                break;
                            default:
                                console.error('Should be a type');
                        }
                    }
                    break;
                //Return always the default image
                case 'default':
                    image.src = Ext.getResourcePath('images/default.png', 'shared');
                    break;
                // Return the selected image
                case 'selected':
                    image.src = Ext.getResourcePath('images/selected.png', 'shared');
                    break;
                default:
                    console.error('Should never happen');
            }
            return image;
        }
    }
});
