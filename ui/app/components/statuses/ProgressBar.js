Ext.define('CMDBuildUI.components.statuses.ProgressBar', {
    extend: 'Ext.view.View',
    alias: 'widget.statuses-progress-bar',

    config: {
        /**
         * @cfg {String} lookuptype
         * The Lookup Type to use for populate the progress bar.
         */
        lookuptype: null,

        /**
         * @cfg {Numeric} lookupvalue
         * The id of the selected lookup value.
         */
        lookupvalue: null,

        /**
         * @cfg {Object} filter
         * The filter to apply at the lookup server call
         */
        ecqlFilter: null
    },

    // make lookupvalue bindable
    publishes: ['lookupvalue'],

    baseCls: Ext.baseCSSPrefix + 'statuses-progress-bar',
    ui: 'default',

    itemTpl: new Ext.XTemplate(
        '<div class="',
        '<tpl if="current">{itemCls}-current</tpl>',
        '">',
        '<div class="{itemCls}-selector">',
        '<span>&nbsp;</span>',
        '</div>',
        '<div class="{itemCls}-description">{description}</div>',
        '</div>'
    ),

    itemSelector: '.' + Ext.baseCSSPrefix + 'statuses-progress-bar-item',
    itemCls: Ext.baseCSSPrefix + 'statuses-progress-bar-item',
    scrollable: false,

    // disable load mask
    loadMask: false,

    // disable selection
    selectionModel: false,

    ariaAttributes: {
        'aria-hidden': 'true'
    },

    /**
     * @override
     *
     * Custom init component
     */
    initComponent: function () {
        if (this.getLookuptype()) {
            var filters = [
                {
                    property: 'active',
                    value: true
                }
            ];

            // if lookup type is FlowStatus show only Running and Completed statuses
            if (this.getLookuptype() === 'FlowStatus') {
                filters.push({
                    property: 'code',
                    operator: 'in',
                    value: ['open.running', 'closed.completed']
                });
            }

            //creates the store
            var store = Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.lookups.Lookup',
                proxy: {
                    url: CMDBuildUI.util.api.Lookups.getLookupValues(this.getLookuptype()),
                    type: 'baseproxy'
                },
                autoLoad: true
            });

            //apply the filter to the store
            var ecql = this.getEcqlFilter();
            if (ecql) {
                var advancedFilter = store.getAdvancedFilter();
                advancedFilter.addEcqlFilter(ecql);
            }

            // add store to progress bar
            Ext.apply(this, {
                store: store
            });
        }
        this.callParent();
    },

    /**
     * @override
     *
     * Provide custom formatting for each Record that is used by
     * this ProgressBar's template to render each node.
     * @param {Object/Object[]} data The raw data object that was used to create the Record.
     * @param {Number} recordIndex the index number of the Record being prepared for rendering.
     * @param {Ext.data.Model} record The Record being prepared for rendering.
     * @return {Array/Object} The formatted data in a format expected by the internal {@link #cfg-tpl template}'s overwrite() method.
     * (either an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'}))
     */
    prepareData: function (data, recordIndex, record) {
        return {
            id: record.getId(),
            code: record.get('code'),
            description: record.get('text'),
            number: record.get('number'),
            current: record.get('isCurrent'),
            done: record.get('isDone'),
            itemCls: this.itemCls
        };
    },

    /**
     * Update navigation model
     * @param {Ext.view.NavigationModel} navigationModel
     * @param {Ext.view.NavigationModel} oldNavigationModel
     */
    updateNavigationModel: function (navigationModel, oldNavigationModel) {
        navigationModel.focusCls = '';
    },

    /**
     * @param {Numeric} newValue
     * @param {Numeric} oldValue
     */
    updateLookupvalue: function (newValue, oldValue) {
        if (newValue) {
            var store = this.getStore();
            if (store.isLoaded()) {
                this.setCurrentStateCls(store, newValue);
            } else {
                var me = this;
                store.on('load', function (records) {
                    me.setCurrentStateCls(store, newValue);
                });
            }
        }
    },

    privates: {
        /**
         * @param {Ext.data.Store} store
         * @param {Numeric} currentId
         */
        setCurrentStateCls: function (store, currentId) {
            var currentfound = false;
            store.getRange().forEach(function (record, index, array) {
                record.set('number', index + 1);
                if (record.getId() == currentId) {
                    record.set('isCurrent', true);
                    currentfound = true;
                } else if (!currentfound) {
                    record.set('isDone', true);
                }
            });
        }
    }
});
