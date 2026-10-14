Ext.define('CMDBuildUI.view.dashboards.common.components.list.ListController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.ComponentController',
    alias: 'controller.dashboards-components-list-list',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#showAllButton': {
            click: 'onShowAllButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.dashboards.components.list.List} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const component = view.getItemDefinition();
        const me = this;
        const vm = view.lookupViewModel();

        me.renderers = {};

        const list = view.down('#list');
        if (list && list.tpl) {
            list.tpl.controller = me;
        }

        if (vm) {
            vm.set({
                seeAllElements: component.get('seeAllElements'),
                emptyTextFormatted: component.get('_emptyListMessage_translation') || component.get('emptyListMessage')
            });
        }

        component.getSourceAttributes().then(function (attributes) {
            attributes.getRange().forEach(function (attribute) {
                const name = attribute.get('name');
                component.set(Ext.String.format('_{0}_translation', name), attribute.get('_description_translation'));

                const field = CMDBuildUI.util.helper.ModelHelper.getModelField(attribute.getData());
                const column = field ? CMDBuildUI.util.helper.GridHelper.getColumn(field) : null;
                if (column && column.renderer) {
                    me.renderers[name] = column.renderer;
                }
            });

            if (list) {
                list.refresh();
            }
        });

        this.configureStore(view, component);
    },

    /**
     * Handles the click event on a list item.
     *
     * @param {Ext.view.View} dataview
     * @param {Ext.data.Model} record
     */
    onListItemClick: function (dataview, record) {
        if (!this.hasLinkToClass()) {
            return;
        }

        const basePath = this.getSourceBasePath();
        if (basePath) {
            this.redirectTo(Ext.String.format('{0}/{1}/view', basePath, record.get('_id')));
        }
    },

    /**
     * Handles the click event on "Show All" button.
     */
    onShowAllButtonClick: function () {
        const me = this;
        const basePath = me.getSourceBasePath();

        if (!basePath) {
            return;
        }

        me.applySearchFilter().then(function () {
            CMDBuildUI.util.Navigation.clearCurrentContext();
            CMDBuildUI.util.Utilities.redirectTo(basePath, true);
        });
    },

    /**
     * Handles the click event on "Reload" button.
     *
     * @param {Ext.menu.Item|Ext.button.Button} item
     * @param {Function} [callback]
     */
    menuItemReloadHandler: function (item, callback) {
        const view = this.getView();
        const listView = view.down('#list');

        const store = listView ? listView.getStore() : null;

        if (store) {
            store.reload({
                callback: function (records, operation, success) {
                    if (Ext.isFunction(callback)) {
                        callback(records, operation, success);
                    }
                }
            });
        } else {
            const vm = view.lookupViewModel();
            const vmStore = vm ? vm.getStore('records') : null;
            if (vmStore) {
                vmStore.reload();
            }
        }
    },

    /**
     * Checks whether a specific field type has a non-empty value in the given record data.
     * Used in the tpl
     *
     * @param {Object|Ext.data.Model} values
     * @param {String} fieldType
     * @returns {Boolean}
     */
    hasFieldValue: function (values, fieldType) {
        const view = this.getView();
        const component = view.getItemDefinition();

        if (!component) return false;
        const fieldName = component.get(fieldType);
        if (!fieldName) return false;

        const raw = values && values.isModel ? values.get(fieldName) : values ? values[fieldName] : null;
        return !Ext.isEmpty(raw);
    },

    /**
     * Formats and returns the value of a specific field type using the corresponding renderer if available.
     * Used in the tpl
     *
     * @param {Object|Ext.data.Model} value
     * @param {String} fieldType
     * @returns {String|*}
     */
    getFormattedFieldValue: function (values, fieldType) {
        const view = this.getView();
        const component = view.getItemDefinition();

        if (!component) return '';
        const fieldName = component.get(fieldType);
        if (!fieldName) return '';

        const raw = values.isModel ? values.get(fieldName) : values[fieldName];
        const renderer = this.renderers ? this.renderers[fieldName] : null;

        if (renderer) {
            const record = values.isModel ? values : Ext.create('Ext.data.Model', values);
            return renderer(raw, {}, record);
        }

        return raw != null ? raw : '';
    },

    /**
     * Checks if a 'linkToClass' configuration exists on the component.
     * Used in the tpl
     *
     * @returns {Boolean}
     */
    hasLinkToClass: function () {
        const view = this.getView();
        const component = view.getItemDefinition();

        return Boolean(component && component.get('linkToClass'));
    },

    privates: {
        renderers: null,
        unlimitedPageSize: 999999,

        /**
         * Builds the base path (without record id) to the class/process/view instance list
         *
         * @returns {String|null}
         */
        getSourceBasePath: function () {
            const view = this.getView();
            const component = view.getItemDefinition();
            const objectType = this.getRealObjectType();
            let path;

            switch (objectType) {
                case CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass:
                    path = 'classes/{0}/cards';
                    break;
                case CMDBuildUI.model.dashboards.Component.dataSourceTypes.process:
                    path = 'processes/{0}/instances';
                    break;
                case CMDBuildUI.model.dashboards.Component.dataSourceTypes.view:
                    path = 'views/{0}/items';
                    break;
            }

            return path ? Ext.String.format(path, component.get('dataSourceName')) : null;
        },

        /**
         * Sets the item's (class/process) current filter to the saved filter record
         * configured as `listSearchFilter` (matched by name), if any; otherwise clears it.
         *
         * @returns {Ext.promise.Promise}
         */
        applySearchFilter: function () {
            const view = this.getView();
            const component = view.getItemDefinition();
            const objectType = this.getRealObjectType();
            const searchFilterName = component ? component.get('listSearchFilter') : null;
            const isClassOrProcess =
                objectType === CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass ||
                objectType === CMDBuildUI.model.dashboards.Component.dataSourceTypes.process;

            if (!component) {
                return Promise.resolve();
            }

            const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                component.get('dataSourceName'),
                objectType
            );

            if (!item || !item.setCurrentFilter) {
                return Promise.resolve();
            }

            if (!searchFilterName || !isClassOrProcess) {
                item.setCurrentFilter(null);
                return Promise.resolve();
            }

            return item.getFilters().then(function (filters) {
                item.setCurrentFilter(filters.findRecord('name', searchFilterName) || null);
            });
        },

        /**
         * @param {CMDBuildUI.view.dashboards.components.list.List} view
         * @param {CMDBuild.model.dashboards.Component} component
         */
        configureStore: function (view, component) {
            const vm = view.lookupViewModel();
            const storeinfo = component.getSourceStoreParameters();
            const limit = component.get('dataSourceLimit');
            const filter = this.parseFilter(storeinfo.filter);
            const objectName = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(component.get('dataSourceName'));
            const sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(objectName);

            const config = {
                type: 'store',
                proxyurl: storeinfo.proxyurl,
                autoload: true,
                filter: filter,
                pagesize: limit || this.unlimitedPageSize,
                sorters: sorters || []
            };

            vm.set('storedata', config);
            vm.set('storedata.extraparams', {
                filter: filter,
                sorters: Ext.encode(sorters || [])
            });
        }
    },

    /**
     * @param {String|Object|null} filter
     * @returns {Object|null}
     */
    parseFilter: function (filter) {
        if (!filter) {
            return null;
        }

        if (typeof filter !== 'string') {
            return filter;
        }

        try {
            return JSON.parse(filter);
        } catch (e) {
            return null;
        }
    },

    /**
     * @returns {String}
     */
    getRealObjectType: function () {
        const view = this.getView();
        const component = view.getItemDefinition();
        if (!component) return 'class';

        const dsName = component.get('dataSourceName');
        if (!dsName) return 'class';

        if (component.get('dataSourceType') === CMDBuildUI.model.dashboards.Component.dataSourceTypes.view) {
            return 'view';
        }

        return CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(dsName) || 'class';
    }
});
