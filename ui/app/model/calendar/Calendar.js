Ext.define('CMDBuildUI.model.calendar.Calendar', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        onCardDeleteAction: {
            clear: 'clear',
            delete: 'delete'
        },
        source: {
            user: 'user',
            system: 'system'
        }
    },
    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,

    fields: [{
        name: 'name',
        type: 'string',
        defaultValue: CMDBuildUI.util.helper.ModelHelper.objecttypes.event
    }, {
        name: 'dmsCategory',
        type: 'string'
    }],
    mixins: [
        'CMDBuildUI.mixins.model.Filter'
    ],

    hasMany: [{
        model: 'CMDBuildUI.model.base.Filter',
        name: 'filters'
    }, {
        model: 'CMDBuildUI.model.AttributeGrouping',
        name: 'attributeGroups'
    }, {
        model: 'CMDBuildUI.model.ContextMenuItem',
        name: 'contextMenuItems',
        associationKey: 'contextMenuItems'
    }],

    /**
     * This is a model used as Event's parent.
     */
    proxy: {
        type: 'memory'
    },

    init: function () {
        this._contextMenuItems = Ext.create("Ext.data.Store", {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                label: "Mark complete",
                _label_translation: CMDBuildUI.locales.Locales.calendar.cm_markcomplete,
                type: "custom",
                active: true,
                visibility: "many",
                script: Ext.String.format(
                    "CMDBuildUI.util.Msg.confirm('{0}', '{1}', {2})",
                    CMDBuildUI.locales.Locales.notifier.attention,
                    CMDBuildUI.locales.Locales.calendar.cm_confirmcomplete,
                    function (btnText) {
                        if (btnText.toLowerCase() === 'yes') {

                            function refresh() {
                                api._grid.getSelectionModel().deselectAll();
                                api.refreshGrid();
                            }
                            var l = records.length;
                            records.forEach(function (record) {
                                if (record.get("status") == "active" || record.get("status") == "expired") {
                                    api.updateRecord(record, { status: "completed" }, function (record, operation, success) {
                                        l--;
                                        if (l == 0) {
                                            refresh();
                                        }
                                    });
                                } else {
                                    l--;
                                    if (l == 0) {
                                        refresh();
                                    }
                                }
                            }, this);
                        }
                    })
            }, {
                label: "Mark cancelled",
                _label_translation: CMDBuildUI.locales.Locales.calendar.cm_markcancelled,
                type: "custom",
                active: true,
                visibility: "many",
                script: Ext.String.format(
                    "CMDBuildUI.util.Msg.confirm('{0}', '{1}', {2})",
                    CMDBuildUI.locales.Locales.notifier.attention,
                    CMDBuildUI.locales.Locales.calendar.cm_confirmcancel,
                    function (btnText) {
                        if (btnText.toLowerCase() === 'yes') {
                            function refresh() {
                                api._grid.getSelectionModel().deselectAll();
                                api.refreshGrid();
                            }
                            var l = records.length;
                            records.forEach(function (record) {
                                api.updateRecord(record, { status: "canceled" }, function (record, operation, success) {
                                    l--;
                                    if (l == 0) {
                                        refresh();
                                    }
                                });
                            }, this);
                        }
                    }
                )
            }]
        });
    }
});