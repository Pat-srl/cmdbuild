/**
 * Private record store class which takes the place of the view's data store to provide a grouped
 * view of the data when the Grouping feature is used.
 *
 * Relays granular mutation events from the underlying store as refresh events to the view.
 *
 * On mutation events from the underlying store, updates the summary rows by firing update events on the corresponding
 * summary records.
 * @private
 */
Ext.define('CMDBuildUI.components.grid.features.GroupStore', {
    extend: 'Ext.grid.feature.GroupStore',

    /**
     * @override
     *
     * @param {Ext.data.Store} store
     */
    processStore: function (store) {
        var me = this,
            groupingFeature = me.groupingFeature,
            collapseAll = Ext.isBoolean(groupingFeature.startCollapsed) ? groupingFeature.startCollapsed : false,
            collapseLimit = Ext.isNumber(groupingFeature.startCollapsed) ? groupingFeature.startCollapsed : null,
            data = me.data,
            groups = store.getGroups(),
            groupCount = groups ? groups.length : 0,
            groupField = store.getGroupField(),
            metaGroup, i, featureGrouper, group, key;

        if (data) {
            data.clear();
        }
        else {
            data = me.data = new Ext.util.Collection({
                rootProperty: 'data',
                extraKeys: {
                    byInternalId: {
                        property: 'internalId',
                        rootProperty: ''
                    }
                }
            });
        }

        if (store.getCount()) {
            // Upon first process of a loaded store, clear the "always" collapse" flag
            groupingFeature.startCollapsed = false;

            if (groupCount > 0) {
                for (i = 0; i < groupCount; i++) {
                    group = groups.getAt(i);

                    // Cache group information by group name.
                    key = group.getGroupKey();

                    // If there is no store grouper and the groupField looks up a complex data type,
                    // the store will stringify it and the group name will be '[object Object]'.
                    // To fix this, groupers can be defined in the feature config, so we'll
                    // simply do a lookup here and re-group the store.
                    //
                    // Note that if a grouper wasn't defined on the feature that we'll just default
                    // to the old behavior and still try to group.
                    // eslint-disable-next-line max-len
                    if (me.badGrouperKey === key && (featureGrouper = groupingFeature.getGrouper(groupField))) {
                        // We must reset the value because store.group() will call
                        // into this method again!
                        store.getGroups().remove(group);
                        groupingFeature.startCollapsed = collapseAll;
                        store.group(featureGrouper);

                        return;
                    }

                    metaGroup = groupingFeature.getMetaGroup(group);

                    // This is only set at initialization time to handle startCollapsed
                    // if (collapseAll) {
                    //     metaGroup.isCollapsed = collapseAll;
                    // }

                    metaGroup.isCollapsed = (collapseLimit && group.items.length > collapseLimit) || collapseAll || metaGroup.isCollapsed;

                    // Collapsed group - add the group's placeholder.
                    if (metaGroup.isCollapsed) {
                        data.add(metaGroup.placeholder);
                    }
                    // Expanded group - add the group's child records.
                    else {
                        data.insert(me.data.length, group.items);
                    }
                }
            }
            else {
                data.add(store.getRange());
            }
        }
    }
});