Ext.define('CMDBuildUI.view.classes.cards.TabPanel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.classes-cards-tabpanel',

    requires: [
        'CMDBuildUI.view.classes.cards.TabPanelController',
        'CMDBuildUI.view.classes.cards.TabPanelModel',

        'CMDBuildUI.view.classes.cards.card.View',
        'CMDBuildUI.view.classes.cards.card.Edit'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    controller: 'classes-cards-tabpanel',
    viewModel: {
        type: 'classes-cards-tabpanel'
    },

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,
    header: false,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },

    layout: 'fit',

    tabtools: CMDBuildUI.view.classes.cards.Util.getTools(),

    /**
     * Get view tab configuration
     *
     * @returns {Object} Configuration for view tab
     */
    getViewTabConfig: function () {
        const theObject = this.getViewModel().get('theObject');
        return {
            xtype: 'classes-cards-card-view',
            objectTypeName: theObject.get('_type'),
            objectId: theObject.get('_id'),
            shownInPopup: true,
            autoScroll: true,
            hideTools: this.getReadOnlyTabs(),
            hideInlineElements: false
        };
    },

    /**
     * Get edit tab configuration
     *
     * @returns {Object} Configuration for edit tab
     */
    getEditTabConfig: function () {
        return {
            xtype: 'classes-cards-card-edit',
            hideInlineElements: false
        };
    },

    /**
     * Get create tab configuration
     *
     * @returns {Object} Configuration for create tab
     */
    getCreateTabConfig: function () {
        return {
            xtype: 'classes-cards-card-create',
            hideInlineElements: {
                inlineNotes: false
            }
        };
    },

    /**
     * Get clone tab configuration
     *
     * @returns {Object} Configuration for clone realtions tab
     */
    getCloneTabConfig: function () {
        return {
            xtype: 'classes-cards-card-create',
            cloneObject: true,
            hideInlineElements: {
                inlineNotes: false
            }
        };
    },

    /**
     * Get clone realtions tab configuration
     *
     * @returns {Object} Configuration for clone realtions tab
     */
    getCloneRelationsTabConfig: function () {
        return {
            xtype: 'classes-cards-clonerelations-container',
            cloneObject: true,
            hideInlineElements: {
                inlineNotes: false
            }
        };
    },

    /**
     * Get master detail tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for master detail tab
     */
    getRelationsMasterDetailTabConfig: function (classDef) {
        return {
            xtype: 'relations-masterdetail-tabpanel',
            readOnly: this.getReadOnlyTabs() || !this.getViewModel().get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.detail_write]
        };
    },

    /**
     * Get notes tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for notes tab
     */
    getNotesTabConfig: function (classDef) {
        return {
            xtype: 'notes-panel',
            readOnly: this.getReadOnlyTabs() || !this.getViewModel().get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.note_write],
            bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding
        };
    },

    /**
     * Get relations list tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for relations list tab
     */
    getRelationsTabConfig: function (classDef) {
        return {
            xtype: 'relations-list-container',
            readOnly: this.getReadOnlyTabs() || !this.getViewModel().get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.relation_write]
        };
    },

    /**
     * Get history tab configuration
     *
     * @returns {Object} Configuration for history tab
     */
    getHistoryTabConfig: function () {
        return {
            xtype: 'history-grid',
            autoScroll: true
        };
    },

    /**
     * Get emails tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for emails tab
     */
    getEmailsTabConfig: function (classDef) {
        return {
            xtype: 'emails-container',
            readOnly: this.getReadOnlyTabs() || !this.getViewModel().get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.email_write]
        };
    },

    /**
     * Get dms tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for dms tab
     */
    getDmsTabConfig: function (classDef) {
        return {
            xtype: 'dms-container',
            readOnly: this.getReadOnlyTabs() || !this.getViewModel().get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.attachment_write]
        };
    },

    /**
     * Get events tab configuration
     *
     * @param {Object} classDef 
     * @returns {Object} Configuration for events tab
     */
    getEventsTabConfig: function (classDef) {
        const vm = this.getViewModel(),
            theObject = vm.get('theObject');
        return {
            xtype: 'events-grid',
            bind: {
                store: '{eventsStore}'
            },
            viewModel: {
                data: {
                    permissionTools: {
                        edit: !vm.get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.schedule_write],
                        delete: !vm.get('basepermissions.edit') || !classDef[CMDBuildUI.model.users.Grant.permissions.schedule_write]
                    }
                },
                stores: {
                    eventsStore: {
                        model: 'CMDBuildUI.model.calendar.Event',
                        pageSize: 50,
                        leadingBufferZone: 100,
                        autoLoad: true,
                        proxy: {
                            type: 'baseproxy',
                            url: '/calendar/events'
                        },
                        advancedFilter: {
                            attributes: {
                                card: [{
                                    operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                                    value: [theObject.get('_id')]
                                }]
                            }
                        },
                        sorters: [{
                            property: 'date',
                            direction: 'ASC'
                        }],
                        autoDestroy: true
                    }
                }
            },
            hideTools: true
        };
    }
});