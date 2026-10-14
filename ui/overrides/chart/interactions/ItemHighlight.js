Ext.define('Override.chart.interactions.ItemHighlight', {
    override: 'Ext.chart.interactions.ItemHighlight',

    onMouseMoveGesture: function (e) {
        var me = this,
            tooltipItems = me.tooltipItems,
            isMousePointer = e.pointerType === 'mouse',
            tooltips = [],
            item,
            oldItem,
            items,
            tooltip,
            oldTooltip,
            i,
            len,
            j,
            jLen;

        if (me.getSticky()) {
            return true;
        }

        if (isMousePointer && me.stickyHighlightItem) {
            me.stickyHighlightItem = null;
            me.highlight(null);
        }

        if (me.isDragging) {
            if (tooltipItems.length && isMousePointer) {
                me.hideTooltips(tooltipItems);
                tooltipItems.length = 0;
            }
        } else if (!me.stickyHighlightItem) {
            if (me.getMultiTooltips()) {
                items = me.getItemsForEvent(e);
            } else {
                item = me.getItemForEvent(e);
                items = item ? [item] : [];
            }

            for (i = 0, len = items.length; i < len; i++) {
                item = items[i];

                // Items are returned top to down, so first item is the top one.
                // Chart can only have one highlighted item.
                if (i === 0 && item !== me.getChart().getHighlightItem()) {
                    me.highlight(item);
                    me.sync();
                }

                tooltip = item.series.getTooltip();

                if (tooltip) {
                    tooltips.push(tooltip);
                }
            }

            // sync the last item on mouseleave
            me.highlight(item);

            if (isMousePointer) {
                // If we detected a mouse hit, show/refresh the tooltip
                if (items.length) {
                    for (i = 0, len = items.length; i < len; i++) {
                        item = items[i];
                        tooltip = item.series.getTooltip();

                        if (tooltip) {
                            // If there were different previously active items
                            // that are not going to be included in current active items,
                            // ask them to hide their tooltips. Unless those are
                            // the same tooltip instances that we are about to show,
                            // in which case we are just going to reposition them.
                            for (j = 0, jLen = tooltipItems.length; j < jLen; j++) {
                                oldItem = tooltipItems[j];

                                if (!Ext.Array.contains(items, oldItem)) {
                                    oldTooltip = oldItem.series.getTooltip();

                                    if (!Ext.Array.contains(tooltips, oldTooltip)) {
                                        oldItem.series.hideTooltip(oldItem, true);
                                    }
                                }
                            }

                            if (tooltip.getTrackMouse()) {
                                item.series.showTooltip(item, e);
                            } else {
                                me.showUntracked(item);
                            }
                        }
                    }

                    me.tooltipItems = items;
                }
                // No mouse hit - schedule a hide for hideDelay ms.
                // If pointer enters another item within that time,
                // there will be no flickery reshow.
                else {
                    me.hideTooltips(tooltipItems);
                    tooltipItems.length = 0;
                }
            }

            return false;
        }
    }
});
