Ext.define('CMDBuildUI.view.dependencytree.tab.GerarchicController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dependencytree-tab-gerarchic',

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
        view.down('#textNoCard').setHtml(
            Ext.String.format(
                '<i style="height:100%; display:flex; justify-content:center; align-items:center;"> {0} </i>',
                CMDBuildUI.locales.Locales.dependencytree.nocardselected
            )
        );

        vm.bind('{selectedNode}', function (selectedNode) {
            if (!window.d3) {
                return;
            }

            const svg = d3.select('#gerarchicTree');
            svg.selectAll('*').remove();

            if (Ext.Object.isEmpty(selectedNode)) {
                return;
            }

            const width = view.getWidth();
            const nodeWidth = 180;
            const externalPadding = 40;
            const internalNodePadding = 10;
            const spacing = 70;
            const nodes = [];
            const graphColor = view.getBaseColor();
            let height = externalPadding;

            Ext.Array.forEach(selectedNode.ancestors(), function (item, index, allitems) {
                const nodeData = item.data;
                if (!nodeData.isGroupNode) {
                    if (nodeData.relationDomain) {
                        const domain = CMDBuildUI.util.helper.ModelHelper.getDomainFromName(nodeData.relationDomain);
                        nodeData.relationLabel =
                            nodeData.relationDirection == '_1'
                                ? domain.getTranslatedDescriptionInverse()
                                : domain.getTranslatedDescriptionDirect();
                    }

                    item.x = width / 2 - nodeWidth / 2;
                    Ext.Array.insert(nodes, 0, [item]);
                }
            });

            const nodeGroups = svg.selectAll('node-group').data(nodes).enter().append('g').attr('class', 'node-group');
            nodeGroups
                .append('text')
                .attr('x', d => d.x + nodeWidth / 2)
                .attr('dominant-baseline', 'middle')
                .attr('text-anchor', 'middle')
                .text(d => d.data.name)
                .call(view.wrapCentered, nodeWidth - internalNodePadding * 2);

            nodeGroups.each(function (d) {
                const actualNode = d3.select(this);
                const textNode = actualNode.select('text').node();
                const bbox = textNode.getBBox();
                const nodeHeight = bbox.height + internalNodePadding * 2;
                d.nodeHeight = nodeHeight;
                d.y = height;

                actualNode
                    .insert('rect', 'text')
                    .attr('width', nodeWidth)
                    .attr('height', nodeHeight)
                    .attr('x', d => d.x)
                    .attr('y', d => d.y)
                    .attr('rx', 10)
                    .attr('stroke', graphColor)
                    .attr('stroke-width', 2)
                    .attr('fill', 'white');

                actualNode.select('text').attr('y', height + nodeHeight / 2);

                height += nodeHeight + spacing;
            });

            svg.attr('height', height - spacing + externalPadding);

            const links = nodes.slice(0, -1).map((node, i) => ({
                source: nodes[i],
                domainLabel: nodes[i + 1].data.relationLabel,
                target: nodes[i + 1]
            }));

            svg.selectAll('.link')
                .data(links)
                .enter()
                .append('line')
                .attr('class', 'link')
                .attr('x1', d => d.source.x + nodeWidth / 2)
                .attr('y1', d => d.source.y + d.source.nodeHeight)
                .attr('x2', d => d.target.x + nodeWidth / 2)
                .attr('y2', d => d.target.y)
                .attr('stroke', graphColor)
                .attr('stroke-width', 2)
                .attr('marker-end', 'url(#arrow)');

            svg.selectAll('.labelLink')
                .data(links)
                .enter()
                .append('text')
                .attr('x', d => d.source.x + nodeWidth / 2 + 10)
                .attr('y', d => d.source.y + d.source.nodeHeight + spacing / 2)
                .text(d => d.domainLabel)
                .call(view.wrapCentered, width / 2 - internalNodePadding * 2);

            svg.append('defs')
                .append('marker')
                .attr('id', 'arrow')
                .attr('viewBox', '0 -5 10 10')
                .attr('refX', 10)
                .attr('refY', 0)
                .attr('markerWidth', 6)
                .attr('markerHeight', 6)
                .attr('orient', 'auto')
                .append('path')
                .attr('d', 'M0,-5L10,0L0,5')
                .attr('fill', graphColor);
        });
    }
});
