Ext.define('CMDBuildUI.view.dependencytree.CanvasController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dependencytree-canvas',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#initialStateBtn': {
            click: 'onClickInitialStateBtn'
        },
        '#downloadBtn': {
            click: 'onClickDownloadBtn'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.dependencytree.Canvas} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();
        const principalPanel = this.getPrincipalPanel();
        const rootId = principalPanel.getRootId();
        let childrenPath = principalPanel.getChildrenPath() ? principalPanel.getChildrenPath() : [];
        const childrenPathLength = childrenPath.length;

        if (!rootId) {
            CMDBuildUI.util.Msg.alert(
                CMDBuildUI.locales.Locales.notifier.info,
                CMDBuildUI.locales.Locales.dependencytree.filteredid,
                function (action) {
                    if (action === 'ok') {
                        CMDBuildUI.util.Utilities.closePopup('dependencyTreePopup');
                    }
                }
            );
            return;
        }

        this._loadMask = CMDBuildUI.util.Utilities.addLoadMask(principalPanel);

        Ext.Loader.loadScript({
            url: 'resources/js/d3/d3.min.js',
            onLoad: function () {
                const navTreeData = {
                    id: rootId,
                    name: principalPanel.getRootDescription(),
                    objectId: rootId,
                    objectTypeName: principalPanel.getRootClassName(),
                    isLeaf: false,
                    nodeBorderColor: view.getBaseColor(),
                    isSelected: childrenPathLength == 1 ? true : false,
                    isInThePath: childrenPathLength ? true : false,
                    children: []
                };

                const domainTree = vm.get('domainTree');
                const rootNode = domainTree.nodes().getRange()[0];

                let initialDepth = domainTree.get('initialExpansionDepth') || vm.get('initialDepth');
                if (!Ext.isEmpty(childrenPath)) {
                    initialDepth = childrenPathLength - 1;
                }
                vm.set('initialDepth', initialDepth);

                let addDataToTree = 0;
                const nodesDepth = {};
                nodesDepth[rootNode.getId()] = 0;
                navTreeData['navTreeNodes'] = rootNode.get('nodes');

                const loadData = function (treeData, childrens) {
                    if (Ext.isEmpty(childrens) || nodesDepth[childrens[0].parent] == initialDepth) {
                        if (!addDataToTree) {
                            me.createDependencyTree(navTreeData);
                        }
                    } else {
                        addDataToTree += childrens.length;
                        let childNotFound = true;
                        Ext.Array.forEach(childrens, function (item, index, allitems) {
                            if (item.parent && !nodesDepth[item._id]) {
                                nodesDepth[item._id] = nodesDepth[item.parent] + 1;
                            }

                            let objectTypeName = treeData.objectTypeName;
                            let objectId = treeData.objectId;

                            if (!Ext.isEmpty(childrenPath)) {
                                const childType = childrenPath[0]._type;
                                const childId = childrenPath[0]._id;

                                if (childrens.length > 1) {
                                    const object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                        item.targetClass
                                    );
                                    const objectChildrens = [item.targetClass];
                                    Ext.Array.forEach(object.getChildren(), function (childClass) {
                                        objectChildrens.push(childClass.get('name'));
                                    });

                                    if (
                                        childNotFound &&
                                        childrenPath.length > 1 &&
                                        objectChildrens.indexOf(childrenPath[1]._type) > -1
                                    ) {
                                        childNotFound = false;
                                        objectTypeName = childType;
                                        objectId = childId;
                                        childrenPath = Ext.Array.removeAt(childrenPath, 0);
                                        item.isTheCorrectChild = true;
                                    }
                                } else {
                                    objectTypeName = childType;
                                    objectId = childId;
                                    childrenPath = Ext.Array.removeAt(childrenPath, 0);
                                    item.isTheCorrectChild = true;
                                }
                            }

                            me.loadCards(item, objectTypeName, objectId).then(function (cardsData) {
                                addDataToTree -= 1;

                                const targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                    item.targetClass
                                );
                                const targetDescription =
                                    targetClass && targetClass.getTranslatedDescription()
                                        ? targetClass.getTranslatedDescription()
                                        : item.targetClass;

                                if (Ext.isEmpty(cardsData)) {
                                    if (childrens.length > 1 && cardsData) {
                                        const groupNode = me.createGroupNode(targetDescription, cardsData.length);
                                        treeData.children.push(groupNode);
                                    } else {
                                        treeData.isLeaf = true;
                                    }

                                    if (!addDataToTree) {
                                        me.createDependencyTree(navTreeData);
                                    }
                                } else {
                                    let actualNodes = treeData.children;
                                    if (childrens.length > 1) {
                                        const groupNode = me.createGroupNode(targetDescription, cardsData.length);
                                        if (
                                            (!Ext.isEmpty(childrenPath) && item.isTheCorrectChild) ||
                                            (Ext.isEmpty(childrenPath) && nodesDepth[item._id] <= initialDepth)
                                        ) {
                                            groupNode.isGroupNodeClosed = false;
                                        }
                                        actualNodes.push(groupNode);
                                        actualNodes = actualNodes[actualNodes.length - 1].children;
                                    }

                                    if (!Ext.isEmpty(childrenPath)) {
                                        if (item.isTheCorrectChild) {
                                            let pathChild, treeNode;
                                            Ext.Array.forEach(cardsData, function (elem, ind, allelems) {
                                                const newChildren = me.createChildren(elem, item);
                                                actualNodes.push(newChildren);
                                                if (
                                                    elem._id == childrenPath[0]._id &&
                                                    elem._type == childrenPath[0]._type
                                                ) {
                                                    pathChild = newChildren;
                                                    treeNode = Ext.Array.findBy(item.nodes, function (node, ind) {
                                                        let type = null;
                                                        if (childrenPath.length > 1) {
                                                            type = childrenPath[1]._type;
                                                        }

                                                        const object =
                                                            CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                                node.targetClass
                                                            );
                                                        const objectChildrens = [node.targetClass];
                                                        Ext.Array.forEach(object.getChildren(), function (childClass) {
                                                            objectChildrens.push(childClass.get('name'));
                                                        });

                                                        return objectChildrens.indexOf(type) > -1;
                                                    });
                                                    if (!treeNode) {
                                                        newChildren.isSelected = true;
                                                    } else {
                                                        newChildren.isInThePath = true;
                                                    }
                                                }
                                            });

                                            loadData(pathChild, treeNode ? item.nodes : []);
                                        } else {
                                            Ext.Array.forEach(cardsData, function (elem, ind, allelems) {
                                                const newChildren = me.createChildren(elem, item);
                                                actualNodes.push(newChildren);
                                            });
                                            loadData({}, []);
                                        }
                                    } else {
                                        addDataToTree += cardsData.length;
                                        Ext.Array.forEach(cardsData, function (elem, ind, allelems) {
                                            const newChildren = me.createChildren(elem, item);
                                            addDataToTree -= 1;
                                            actualNodes.push(newChildren);
                                            loadData(newChildren, item.nodes);
                                        });
                                    }
                                }
                            });
                        });
                    }
                };

                loadData(navTreeData, rootNode.get('nodes'));
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onClickInitialStateBtn: function (button, event, eOpts) {
        const me = this;
        const view = this.getView();
        const principalPanel = this.getPrincipalPanel();
        const domainTree = this.getViewModel().get('domainTree');

        CMDBuildUI.util.DependencyTree.openPopupToSelectRoot(domainTree, function (button) {
            const selectedCard = button.up('#dependencyTree').down('referencecombofield').getSelection();

            principalPanel.setRootId(selectedCard.getId());
            principalPanel.setRootDescription(selectedCard.get('Description'));

            const svg = d3.select('#deepTree');
            svg.selectAll('*').remove();

            me.onAfterRender(view);

            CMDBuildUI.util.Utilities.closePopup('dependencyTree');
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onClickDownloadBtn: function (button, event, eOpts) {
        const view = this.getView();
        const popup = view.up('#dependencyTreePopup');
        const svg = d3.select('#deepTree');
        allNodeIcon = svg.selectAll('.iconNodes');
        const downloadBtn = view.down('#downloadBtn');
        const iconDownloadBtn = downloadBtn.iconCls;

        /**
         * Used to download graph as svg
         */
        function downloadSVG() {
            downloadBtn.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('spinner', 'solid') + ' fa-spin');
            downloadBtn.disable();
            allNodeIcon.attr('visibility', 'hidden');
            const svg = d3.select('#deepTree').node();

            const temp = document.createElement('div');
            temp.className = 'nodeSelected';
            document.body.appendChild(temp);
            const color = getComputedStyle(temp).fill;
            document.body.removeChild(temp);

            const css = `
            .nodeSelected{
                fill: ${color};
            }

            .nodesPath{
                fill: #f6f6f6;
            }

            .normalNode {
                fill: white;
            }
            `;

            const style = document.createElementNS('http://www.w3.org/2000/svg', 'style');
            style.textContent = css;
            svg.insertBefore(style, svg.firstChild);

            const serializer = new XMLSerializer();
            const source = serializer.serializeToString(svg);

            const blob = new Blob([source], { type: 'image/svg+xml;charset=utf-8' });
            const url = URL.createObjectURL(blob);

            const link = document.createElement('a');
            link.href = url;
            link.download = 'graph.svg';
            link.click();
            URL.revokeObjectURL(url);
            allNodeIcon.attr('visibility', 'visible');
            downloadBtn.setIconCls(iconDownloadBtn);
            downloadBtn.enable();
        }

        /**
         * Used to download graph as png, jpeg or pdf
         * @param {String} extension
         */
        function downloadImage(extension) {
            downloadBtn.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('spinner', 'solid') + ' fa-spin');
            downloadBtn.disable();
            allNodeIcon.attr('visibility', 'hidden');
            const svg = d3.select('#deepTree').node();
            const serializer = new XMLSerializer();
            const svgString = serializer.serializeToString(svg);

            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            const img = new Image();

            img.onload = function () {
                canvas.width = img.width;
                canvas.height = img.height;

                ctx.drawImage(img, 0, 0);

                if (extension == 'pdf') {
                    Ext.Loader.loadScript({
                        url: ['resources/js/jspdf/jspdf.umd.min.js'],
                        onLoad: function () {
                            const imgData = canvas.toDataURL('image/png');
                            const pdf = new jspdf.jsPDF({
                                unit: 'px',
                                format: [canvas.width, canvas.height]
                            });
                            pdf.addImage(imgData, 'PNG', 0, 0, canvas.width, canvas.height);
                            pdf.save('graph.pdf');
                            allNodeIcon.attr('visibility', 'visible');
                            downloadBtn.setIconCls(iconDownloadBtn);
                            downloadBtn.enable();
                        }
                    });
                } else {
                    const a = document.createElement('a');
                    a.download = 'graph.' + extension;
                    a.href = canvas.toDataURL('image/' + extension);
                    a.click();
                    allNodeIcon.attr('visibility', 'visible');
                    downloadBtn.setIconCls(iconDownloadBtn);
                    downloadBtn.enable();
                }
            };
            img.src = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svgString)));
        }

        const menu = Ext.create('Ext.menu.Menu', {
            minWidth: 35,
            items: [
                {
                    text: CMDBuildUI.locales.Locales.dependencytree.printsvg,
                    tooltip: CMDBuildUI.locales.Locales.dependencytree.printsvg,
                    typeDownload: 'svg',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-image', 'regular'),
                    height: 32,
                    handler: function (menuItem, event) {
                        downloadSVG();
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.dependencytree.printpng,
                    tooltip: CMDBuildUI.locales.Locales.dependencytree.printpng,
                    typeDownload: 'png',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-image', 'regular'),
                    height: 32,
                    handler: function (menuItem, event) {
                        downloadImage('png');
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.dependencytree.printjpeg,
                    tooltip: CMDBuildUI.locales.Locales.dependencytree.printjpeg,
                    typeDownload: 'jpeg',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-image', 'regular'),
                    height: 32,
                    handler: function (menuItem, event) {
                        downloadImage('jpeg');
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.dependencytree.printpdf,
                    tooltip: CMDBuildUI.locales.Locales.dependencytree.printpdf,
                    typeDownload: 'pdf',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-pdf', 'regular'),
                    height: 32,
                    handler: function (menuItem, event) {
                        downloadImage('pdf');
                    }
                }
            ]
        });

        if (popup) {
            let alwaysOnTop = popup.getAlwaysOnTop();
            popup.alwaysOnTop = false;
            menu.on(
                'hide',
                function () {
                    popup.alwaysOnTop = alwaysOnTop;
                },
                this,
                { single: true }
            );
        }

        menu.show();
        menu.alignTo(button.el, 't-b?');
    },

    privates: {
        _treeRoot: null,
        _tree: null,

        svgDimensions: {
            nodeHeight: 40,
            nodeWidth: 200,
            marginTop: 60,
            marginRight: 10,
            marginBottom: 60,
            marginLeft: 40,
            internalNodePadding: 10,
            iconWidth: 20,
            dx: 70 // The pixels that separe rows
        },

        _timerOnClick: null,
        _loadMask: null,
        _uid: 0,

        /**
         * Used to create initial dependency tree
         * @param {Object} treeData
         */
        createDependencyTree: function (treeData) {
            const me = this;
            const vm = this.getViewModel();
            const svgDimensions = this.svgDimensions;

            // The pixels that separe columns
            const dy = svgDimensions.nodeWidth + svgDimensions.dx;

            // Calculate the graph
            const root = d3.hierarchy(treeData);
            this._treeRoot = root;

            // Define the tree layout and the shape for links.
            const tree = d3.tree().nodeSize([svgDimensions.dx, dy]);
            this._tree = tree;

            const width =
                svgDimensions.marginLeft +
                svgDimensions.marginRight +
                (root.height + 1) * (svgDimensions.nodeWidth + svgDimensions.dx);

            // Create the SVG container, a layer for the links and a layer for the nodes.
            const svg = d3
                .select('#deepTree')
                .attr('width', width)
                .attr('viewBox', [-svgDimensions.marginLeft, -svgDimensions.marginTop, width, svgDimensions.dx]);

            const gTree = svg.append('g');

            gTree
                .append('g')
                .attr('class', 'gLink')
                .attr('fill', 'none')
                .attr('stroke', this.getView().getBaseColor())
                .attr('stroke-width', 1.5);

            const gNode = gTree
                .append('g')
                .attr('class', 'gNode')
                .attr('cursor', 'pointer')
                .attr('pointer-events', 'all');

            // Used to deselected data when click outside the graph
            svg.on('click', function (event) {
                if (event.target === this) {
                    const selectedNode = vm.get('selectedNode');
                    if (!Ext.Object.isEmpty(selectedNode)) {
                        selectedNode.data.isSelected = false;
                        Ext.Array.forEach(selectedNode.ancestors(), function (ancestor, index, allancerstors) {
                            ancestor.data.isInThePath = false;
                        });
                    }

                    gNode
                        .selectAll('rect, path')
                        .attr('class', d =>
                            d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                        );

                    vm.set('selectedNode', {});
                    vm.set('isGroupNode', false);
                    vm.set('disableTabCard', true);
                    me.getTabPanel().setActiveTab(0);
                }
            });

            // Do the first update to the initial configuration of the tree
            root.x0 = dy / 2;
            root.y0 = 0;
            root.descendants().forEach((d, i) => {
                d._children = d.children;
                if (d.data.isGroupNode && d.data.isGroupNodeClosed) {
                    d.children = null;
                }
            });

            CMDBuildUI.util.Utilities.removeLoadMask(this._loadMask);
            this._loadMask = null;

            this.update(null, root);
        },

        /**
         *
         * @param {Object} d
         * @returns the path for the link
         */
        diagonal: function (d) {
            const startX = d.source.y + this.svgDimensions.nodeWidth / 2;
            const startY = d.source.x;

            const endX = d.target.y - this.svgDimensions.nodeWidth / 2;
            const endY = d.target.x;

            const midX = (startX + endX) / 2;

            // M stand for move to
            // C represents the Cubic Bézier curve
            return `
                    M${startX},${startY}
                    C${midX},${startY}
                    ${midX},${endY}
                    ${endX},${endY}
                `;
        },

        /**
         *
         * @param {Event} event
         * @param {Object} source
         * @param {Boolean} notScrollView
         */
        update: function (event, source, notScrollView) {
            const me = this;
            const view = this.getView();
            const vm = this.getViewModel();
            const containerWidth = view.getWidth();
            const containerHeight = view.getHeight();
            const svgDimensions = this.svgDimensions;
            const baseColor = view.getBaseColor();
            const principalPanel = this.getPrincipalPanel();

            let root = this._treeRoot;
            const descendants = root.descendants();
            const depthOfExpandedNodes = d3.max(descendants, d => d.depth);

            let width =
                svgDimensions.marginLeft +
                svgDimensions.marginRight +
                (depthOfExpandedNodes + 1) * (svgDimensions.nodeWidth + svgDimensions.dx);

            if (width < containerWidth) {
                // 20 pixels are removed to prevent the horizontal scrollbar from appearing
                width = containerWidth - 20;
            }

            const nodes = descendants.reverse();
            const links = root.links();
            const svg = d3.select('#deepTree');

            // Compute the new tree layout.
            const tree = this._tree;
            tree(root);

            let left = root;
            let right = root;
            root.eachBefore(node => {
                if (node.x < left.x) left = node;
                if (node.x > right.x) right = node;

                // Used to move all tree to right for add left space to root
                node.y += 100;
            });

            const height = right.x - left.x + svgDimensions.marginTop + svgDimensions.marginBottom;

            const transition = svg
                .transition()
                .duration(500)
                .attr('width', width)
                .attr('height', height)
                .attr('viewBox', [-svgDimensions.marginLeft, left.x - svgDimensions.marginTop, width, height])
                .tween('resize', window.ResizeObserver ? null : () => () => svg.dispatch('toggle'))
                .end()
                .then(function () {
                    const scroller = view.getScrollable();

                    if (!Ext.isEmpty(principalPanel.getChildrenPath())) {
                        const selectedNode = svg.selectAll('.nodeSelected').node();
                        const matrix = selectedNode.getCTM();
                        scroller.scrollTo(
                            Math.max(0, matrix.e),
                            Math.max(0, matrix.f - containerHeight / 2 + svgDimensions.nodeHeight)
                        );
                        principalPanel.setChildrenPath(null);
                    } else if (!notScrollView) {
                        const position = scroller.getPosition();
                        scroller.scrollTo(position.x + svgDimensions.nodeWidth + svgDimensions.dx, position.y, true);
                    }
                });

            const gNode = d3.select('.gNode');
            const gLink = d3.select('.gLink');

            // Update the nodes
            const node = gNode.selectAll('g').data(nodes, d => d.data.id);

            // Enter any new nodes at the parent's previous position.
            const nodeEnter = node
                .enter()
                .append('g')
                .attr('transform', d => `translate(${source.y0},${source.x0})`)
                .attr('fill-opacity', 0)
                .attr('stroke-opacity', 0)
                .on('click', (event, d) => {
                    // Used because the dblclick event is preceded by two click events
                    clearTimeout(me._timerOnClick);
                    me._timerOnClick = setTimeout(function () {
                        vm.set('disableTabCard', false);
                        vm.set('isGroupNode', false);
                        if (d.data.isGroupNode) {
                            vm.set('disableTabCard', true);
                            vm.set('isGroupNode', true);
                            me.getTabPanel().setActiveTab(0);
                        }

                        const selectedNode = vm.get('selectedNode');
                        if (!Ext.Object.isEmpty(selectedNode)) {
                            selectedNode.data.isSelected = false;
                            Ext.Array.forEach(selectedNode.ancestors(), function (ancestor, index, allancerstors) {
                                ancestor.data.isInThePath = false;
                            });
                        }

                        Ext.Array.forEach(d.ancestors(), function (ancestor, index, allancerstors) {
                            ancestor.data.isInThePath = true;
                        });
                        d.data.isSelected = true;

                        gNode
                            .selectAll('rect, path')
                            .attr('class', d =>
                                d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                            );

                        vm.set('selectedNode', d);
                    }, 300);
                })
                .on('dblclick', (event, d) => {
                    const nodeData = d.data;
                    clearTimeout(me._timerOnClick);

                    if (!nodeData.isLeaf) {
                        if (d._children) {
                            // Children are shown or hidden directly if already calculated
                            d.children = d.children ? null : d._children;
                            let closeNode = d.children ? false : true;
                            if (nodeData.isGroupNodeClosed) {
                                nodeData.isGroupNodeClosed = false;
                                closeNode = false;
                            }
                            me.update(event, d, closeNode);
                        } else {
                            let addDataToTree = nodeData.navTreeNodes.length;
                            let haveChildrenData = false;

                            me._loadMask = CMDBuildUI.util.Utilities.addLoadMask(me.getView());
                            Ext.Array.forEach(nodeData.navTreeNodes, function (item, index, allitems) {
                                me.loadCards(item, nodeData.objectTypeName, nodeData.objectId).then(
                                    function (cardsData) {
                                        addDataToTree -= 1;

                                        const targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                            item.targetClass
                                        );
                                        const targetDescription =
                                            targetClass && targetClass.getTranslatedDescription()
                                                ? targetClass.getTranslatedDescription()
                                                : item.targetClass;

                                        if (Ext.isEmpty(cardsData)) {
                                            if (nodeData.navTreeNodes.length > 1 && cardsData) {
                                                const groupNode = me.createGroupNode(
                                                    targetDescription,
                                                    cardsData.length
                                                );
                                                nodeData.children.push(groupNode);
                                            }
                                        } else {
                                            haveChildrenData = true;
                                            let actualNodes = nodeData.children;
                                            if (nodeData.navTreeNodes.length > 1) {
                                                const groupNode = me.createGroupNode(
                                                    targetDescription,
                                                    cardsData.length
                                                );

                                                actualNodes.push(groupNode);
                                                actualNodes = actualNodes[actualNodes.length - 1].children;
                                            }

                                            Ext.Array.forEach(cardsData, function (elem, ind, allelems) {
                                                const newChildren = me.createChildren(elem, item);
                                                actualNodes.push(newChildren);
                                            });
                                        }

                                        if (!addDataToTree) {
                                            nodeData.isLeaf = !haveChildrenData;
                                            const rootData = root.data;

                                            const oldNodes = new Map(root.descendants().map(n => [n.data.id, n]));
                                            const newRoot = d3.hierarchy(rootData);

                                            newRoot.eachBefore(n => {
                                                const old = oldNodes.get(n.data.id);
                                                n._children = n.children;
                                                if (old) {
                                                    n.x0 = old.x0;
                                                    n.y0 = old.y0;

                                                    if (!old.children && old._children) {
                                                        n.children = null;
                                                    }
                                                }

                                                if (n.data.isGroupNode && n.data.isGroupNodeClosed) {
                                                    n.children = null;
                                                }
                                            });

                                            root = newRoot;
                                            me._treeRoot = root;
                                            me.update(null, d, !haveChildrenData);
                                        }
                                        CMDBuildUI.util.Utilities.removeLoadMask(me._loadMask);
                                        me._loadMask = null;
                                    }
                                );
                            });
                        }
                    }
                });

            // Enter any new nodes
            nodeEnter.each(function (d) {
                const actualNode = d3.select(this);

                if (d.data.nodeIcon) {
                    actualNode
                        .append('text')
                        .attr(
                            'x',
                            -svgDimensions.nodeWidth / 2 +
                                svgDimensions.iconWidth / 2 +
                                svgDimensions.internalNodePadding
                        )
                        .attr('y', 0)
                        .attr('text-anchor', 'middle')
                        .attr('dominant-baseline', 'middle')
                        .attr('font-family', '"Font Awesome 5 Free"')
                        .attr('font-size', svgDimensions.iconWidth)
                        .attr('font-weight', '900')
                        .attr('fill', d.data.nodeIconColor)
                        .attr('class', 'iconNodes')
                        .text(String.fromCharCode(parseInt(d.data.nodeIcon, 16)));
                }

                actualNode
                    .append('text')
                    .attr('class', 'text-rect')
                    .attr('x', d.data.nodeIcon ? 0 + svgDimensions.internalNodePadding : 0)
                    .attr('y', 0)
                    .attr('text-anchor', 'middle')
                    .attr('dominant-baseline', 'middle')
                    .attr('fill', 'black')
                    .text(d => d.data.name)
                    .call(
                        view.wrapCentered,
                        d.data.nodeIcon
                            ? svgDimensions.nodeWidth - svgDimensions.iconWidth - svgDimensions.internalNodePadding * 3
                            : svgDimensions.nodeWidth - svgDimensions.internalNodePadding * 2
                    );

                const textNode = actualNode.select('.text-rect').node();
                const bbox = textNode.getBBox();
                const height = bbox.height + svgDimensions.internalNodePadding * 2;

                if (d.data.isLeaf && !d.data.isGroupNode) {
                    actualNode
                        .insert('path', 'text')
                        .attr(
                            'd',
                            me.leftRoundedRect(
                                -svgDimensions.nodeWidth / 2,
                                -height / 2,
                                svgDimensions.nodeWidth,
                                height,
                                10
                            )
                        )
                        .attr('stroke-width', 2)
                        .attr('fill', 'white')
                        .attr('stroke', d => (d.data.nodeBorderColor ? d.data.nodeBorderColor : baseColor))
                        .attr('class', d =>
                            d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                        );
                } else {
                    actualNode
                        .insert('rect', 'text')
                        .attr('width', svgDimensions.nodeWidth)
                        .attr('height', height)
                        .attr('x', -svgDimensions.nodeWidth / 2)
                        .attr('y', -height / 2)
                        .attr('rx', d => (d.data.isGroupNode ? '30' : '10'))
                        .attr('stroke-width', 2)
                        .attr('stroke', d => (d.data.nodeBorderColor ? d.data.nodeBorderColor : baseColor))
                        .attr('fill', 'white')
                        .attr('class', d =>
                            d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                        );
                }

                if (d.data.isSelected) {
                    vm.set('disableTabCard', false);
                    vm.set('selectedNode', d);
                }
            });

            // Update existing nodes
            const nodeMerged = nodeEnter.merge(node);
            nodeMerged.each(function (d) {
                const actualNode = d3.select(this);

                if (d.data.isLeaf && !d.data.isGroupNode && actualNode.select('path').empty()) {
                    actualNode.select('rect').remove();
                    const textNode = actualNode.select('.text-rect').node();
                    const bbox = textNode.getBBox();
                    const height = bbox.height + svgDimensions.internalNodePadding * 2;

                    actualNode
                        .insert('path', 'text')
                        .attr(
                            'd',
                            me.leftRoundedRect(
                                -svgDimensions.nodeWidth / 2,
                                -height / 2,
                                svgDimensions.nodeWidth,
                                height,
                                10
                            )
                        )
                        .attr('stroke-width', 2)
                        .attr('fill', 'white')
                        .attr('stroke', d => (d.data.nodeBorderColor ? d.data.nodeBorderColor : baseColor))
                        .attr('class', d =>
                            d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                        );
                }

                if (d.data.isSelected) {
                    vm.set('disableTabCard', false);
                    vm.set('selectedNode', d);
                }
            });

            nodeEnter
                .merge(node)
                .select('rect')
                .attr('stroke', d => (d.data.nodeBorderColor ? d.data.nodeBorderColor : baseColor))
                .attr('class', d =>
                    d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                );

            nodeEnter
                .merge(node)
                .select('path')
                .attr('stroke', d => (d.data.nodeBorderColor ? d.data.nodeBorderColor : baseColor))
                .attr('class', d =>
                    d.data.isSelected ? 'nodeSelected' : d.data.isInThePath ? 'nodesPath' : 'normalNode'
                );

            // Transition nodes to their new position.
            const nodeUpdate = node
                .merge(nodeEnter)
                .transition(transition)
                .attr('transform', d => `translate(${d.y},${d.x})`)
                .attr('fill-opacity', 1)
                .attr('stroke-opacity', 1);

            // Transition existing nodes to the parent's new position.
            const nodeExit = node
                .exit()
                .transition(transition)
                .remove()
                .attr('transform', d => `translate(${source.y},${source.x})`)
                .attr('fill-opacity', 0)
                .attr('stroke-opacity', 0);

            // Update the links
            const link = gLink.selectAll('path').data(links, d => d.target.id);

            // Enter any new links at the parent's previous position.
            const linkEnter = link
                .enter()
                .append('path')
                .attr('d', d => {
                    const o = { x: source.x0, y: source.y0 };
                    return me.diagonal({ source: o, target: o });
                });

            // Transition links to their new position.
            link.merge(linkEnter)
                .transition(transition)
                .attr('d', d => {
                    return me.diagonal({ source: d.source, target: d.target });
                });

            // Transition exiting nodes to the parent's new position.
            link.exit()
                .transition(transition)
                .remove()
                .attr('d', d => {
                    const o = { x: source.x, y: source.y };
                    return me.diagonal({ source: o, target: o });
                });

            // Stash the old positions for transition.
            root.eachBefore(d => {
                d.x0 = d.x;
                d.y0 = d.y;
            });
        },

        /**
         *
         * @param {Object} item
         * @param {String} objectTypeName
         * @param {String} objectId
         * @returns
         */
        loadCards: function (item, objectTypeName, objectId) {
            const me = this;
            const deferred = new Ext.Deferred();
            const advancedFilter = new CMDBuildUI.util.AdvancedFilter();
            let attributeLookupToLoad = '';

            const domain = Ext.getStore('domains.Domains').findRecord('name', item.domain);
            const disabledClasses =
                item.direction == '_1'
                    ? domain.get('disabledDestinationDescendants')
                    : domain.get('disabledSourceDescendants');

            if (disabledClasses.includes(objectTypeName)) {
                deferred.resolve();
            } else {
                advancedFilter.addRelationFilter(
                    item.domain,
                    objectTypeName,
                    item.targetClass,
                    item.direction,
                    CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof,
                    [{ className: objectTypeName, id: objectId }]
                );

                if (item.ecqlFilter) {
                    advancedFilter.addEcqlFilter(item.ecqlFilter);
                }

                let attrs = 'Id,Description,IdClass';
                if (item.sourceLookupAttribute) {
                    attrs += ',' + item.sourceLookupAttribute;
                    attributeLookupToLoad = item.sourceLookup;
                }

                Ext.Ajax.request({
                    url: Ext.String.format('{0}/classes/{1}/cards', CMDBuildUI.util.Config.baseUrl, item.targetClass),
                    method: 'GET',
                    params: {
                        attrs: attrs,
                        filter: advancedFilter.encode()
                    },
                    success: function (response) {
                        const jsonDecode = Ext.JSON.decode(response.responseText);
                        if (attributeLookupToLoad) {
                            me.loadLookupValues(attributeLookupToLoad).then(function () {
                                deferred.resolve(jsonDecode.data);
                            });
                        } else {
                            deferred.resolve(jsonDecode.data);
                        }
                    },
                    failure: function () {
                        deferred.reject();
                    }
                });
            }

            return deferred.promise;
        },

        /**
         *
         * @param {String} targetDescription
         * @param {Number} cardsDataLength
         * @returns
         */
        createGroupNode: function (targetDescription, cardsDataLength) {
            return {
                id: `group_${++this._uid}`,
                name: Ext.String.format('{0} ({1})', targetDescription, cardsDataLength),
                isGroupNode: true,
                isGroupNodeClosed: true, // It is used to prevent children of a grouper node from being shown if closed when recomputing the tree
                isLeaf: cardsDataLength == 0 ? true : false,
                children: []
            };
        },

        /**
         *
         * @param {Object} cardData
         * @param {Object} treeNode
         * @returns
         */
        createChildren: function (cardData, treeNode) {
            const view = this.getView();
            const sourceLookup = treeNode.sourceLookup;
            const borderSource = treeNode.borderSource;
            const iconSource = treeNode.iconSource;
            const iconColorMode = treeNode.iconColorMode;
            let nodeBorderColor = view.getBaseColor();
            let nodeIconColor = view.getBaseColor();
            let nodeIcon, lookupData;

            if (sourceLookup) {
                lookupData = CMDBuildUI.model.lookups.Lookup.getLookupValueById(
                    sourceLookup,
                    cardData[treeNode.sourceLookupAttribute]
                );
            }

            if (borderSource !== CMDBuildUI.view.dependencytree.Mixin.default) {
                nodeBorderColor =
                    borderSource == CMDBuildUI.view.dependencytree.Mixin.static
                        ? treeNode.borderColor
                        : lookupData.get(borderSource);
            }

            if (iconSource !== CMDBuildUI.view.dependencytree.Mixin.default) {
                nodeIcon =
                    iconSource == CMDBuildUI.view.dependencytree.Mixin.static
                        ? treeNode.iconFont
                        : lookupData.get(iconSource);
                if (iconColorMode !== CMDBuildUI.view.dependencytree.Mixin.default) {
                    nodeIconColor =
                        iconColorMode == CMDBuildUI.view.dependencytree.Mixin.static
                            ? treeNode.iconColor
                            : lookupData.get(iconColorMode);
                }
            }

            return {
                id: cardData._id,
                name: cardData.Description,
                objectId: cardData._id,
                objectTypeName: cardData._type,
                navTreeNodes: treeNode.nodes,
                nodeBorderColor: nodeBorderColor,
                nodeIcon: nodeIcon ? CMDBuildUI.util.helper.IconHelper.getUnicodeValueFromDBValue(nodeIcon) : null,
                nodeIconColor: nodeIconColor,
                isSelected: false,
                isLeaf: Ext.isEmpty(treeNode.nodes),
                relationDomain: treeNode.domain,
                relationDirection: treeNode.direction,
                children: []
            };
        },

        /**
         *
         * @param {Number} x
         * @param {Number} y
         * @param {Number} width
         * @param {Number} height
         * @param {Number} radius
         * @returns
         */
        leftRoundedRect: function (x, y, width, height, radius) {
            const right = x + width;
            const bottom = y + height;

            // Function to draw rect where M is move to, H is horizontal line, V is vertical line,
            // A is Arc, Z close the figure returning to initial point
            return `
                M ${x + radius} ${y}
                H ${right}
                V ${bottom}
                H ${x + radius}
                A ${radius} ${radius} 0 0 1 ${x} ${bottom - radius}
                V ${y + radius}
                A ${radius} ${radius} 0 0 1 ${x + radius} ${y}
                Z
            `;
        },

        /**
         *
         * @returns {}
         */
        getPrincipalPanel: function () {
            return this.getView().up('dependencytree-panel');
        },

        /**
         *
         * @returns {}
         */
        getTabPanel: function () {
            return this.getPrincipalPanel().down('tabpanel');
        },

        /**
         *
         * @param {String} type
         * @returns {Ext.Deferred}
         */
        loadLookupValues: function (type) {
            const deferred = new Ext.Deferred();
            const lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);

            if (lt.values().isLoading()) {
                lt.values().on('load', function (records, operation, success) {
                    deferred.resolve(records);
                });
            } else if (!lt.values().isLoaded()) {
                const url = CMDBuildUI.util.api.Lookups.getLookupValues(type);

                lt.values().getProxy().setUrl(url);
                lt.values().load({
                    callback: function (records, operation, success) {
                        deferred.resolve(records);
                    }
                });
                // load values for parent
                if (!Ext.isEmpty(lt.get('parent'))) {
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(lt.get('parent'));
                }
            } else {
                deferred.resolve(lt.values().getRange());
            }

            return deferred.promise;
        }
    }
});
