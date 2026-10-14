Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.GlobalSearchController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.homepage-globalsearch',

    /**
     * @property {Number} AI_REQUEST_TIMEOUT
     * Timeout (ms) of the AI request. Ext.Ajax default (30s) is too short for an AI answer.
     */
    AI_REQUEST_TIMEOUT: 60000,

    init: function (view) {
        this.getViewModel().set('mode', view.getSearchMode());
    },

    destroy: function () {
        this.cancelSearch();
        this.callParent();
    },

    /*
     * Search box
     */

    onSearchTriggerClick: function (field) {
        this.search(field.getValue());
    },

    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.search(field.getValue());
        }
    },

    onSearchClearClick: function () {
        const me = this;
        const view = me.getView();
        const vm = me.getViewModel();
        const ctx = me.ctx;

        me.cancelSearch();
        me.resetResults();
        me.showResults();
        vm.set({ query: null, searchText: '' });

        if (ctx) {
            view.fireEvent('searchend', view, ctx.query, vm.get('mode'), { success: false, stopped: true, total: 0 });
        }
    },

    /*
     * Toolbars
     */

    onStopSearchClick: function () {
        const me = this;

        if (me.ctx) {
            me.ctx.stopped = true;
            me.abortRequest();
        }
    },

    onBackToSearchClick: function () {
        this.showResults(true);
    },

    onAiRetryClick: function () {
        this.search(this.getViewModel().get('query'));
    },

    /*
     * Results grid
     */

    onRowDblClick: function (grid, record) {
        this.showDetail(record);
    },

    onViewCardClick: function (grid, rowIndex, colIndex, item, e, record) {
        this.showDetail(record);
    },

    onRedirectToCardClick: function (grid, rowIndex, colIndex, item, e, record) {
        this.openInApplication(record);
    },

    /*
     * Public API (see GlobalSearch#search)
     */

    /**
     * Validates the query and starts a new search, aborting the one in progress.
     *
     * @param {String} query
     * @returns {Boolean} `false` if the query was not valid.
     */
    search: function (query) {
        const me = this;
        const view = me.getView();
        const vm = me.getViewModel();
        const text = Ext.String.trim(query || '');
        const mode = vm.get('mode');

        if (text.length < view.self.MIN_QUERY_LENGTH) {
            CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.errors.value3chars);
            return false;
        }

        me.cancelSearch();
        me.resetResults();
        me.showResults();

        vm.set({
            query: text,
            searchText: text,
            searching: true
        });

        // context of this search
        me.ctx = {
            query: text,
            stopped: false
        };

        view.fireEvent('searchstart', view, text, mode);

        if (mode === 'ai') {
            me.aiSearch(me.ctx);
        } else {
            me.classesSearch(me.ctx);
        }

        return true;
    },

    privates: {
        /*
         * Search lifecycle
         */

        /**
         * @param {Object} ctx Search context.
         * @returns {Boolean} `true` if `ctx` is the search in progress.
         */
        isCurrent: function (ctx) {
            return !this.destroyed && ctx === this.ctx;
        },

        /**
         * Aborts the request in progress.
         */
        abortRequest: function () {
            const req = this.request;

            this.request = null;
            if (req) {
                req.abort();
            }
        },

        /**
         * Discards the search in progress: the callbacks of its requests are ignored.
         */
        cancelSearch: function () {
            this.ctx = null;
            this.abortRequest();
        },

        /**
         * Closes the search in progress and notifies the listeners.
         *
         * @param {Object} ctx Search context.
         * @param {Object} result `searchend` result.
         */
        endSearch: function (ctx, result) {
            const me = this;
            const view = me.getView();
            const vm = me.getViewModel();

            me.ctx = null;
            me.request = null;
            vm.set('searching', false);

            view.fireEvent('searchend', view, ctx.query, vm.get('mode'), result);
        },

        /**
         * Empties the results of the previous search.
         */
        resetResults: function () {
            this.getResultsStore().removeAll();

            this.getViewModel().set({
                searching: false,
                progress: 0,
                progressClass: null,
                totalCount: null,
                aiStatus: 'idle',
                aiAnswer: '',
                aiResourcesCount: 0
            });
        },

        /**
         * @returns {Ext.data.Store} Store of the results grid (default or AI mode).
         */
        getResultsStore: function () {
            return this.lookup('resultsGrid').getStore();
        },

        /*
         * Default search (classes)
         */

        /**
         * Searches the configured classes one at a time.
         *
         * @param {Object} ctx Search context.
         */
        classesSearch: function (ctx) {
            ctx.classes = Ext.Array.clone(this.getView().getSearchClasses() || []);
            ctx.searched = 0;

            this.getViewModel().set('totalCount', 0);
            this.searchNextClass(ctx);
        },

        /**
         * @param {Object} ctx Search context.
         */
        searchNextClass: function (ctx) {
            const me = this;
            const vm = me.getViewModel();
            const total = ctx.searched + ctx.classes.length;
            const targetClass = ctx.classes.shift();

            if (!targetClass || ctx.stopped) {
                return me.finishClassesSearch(ctx);
            }

            vm.set({
                progress: Math.round((ctx.searched / total) * 100),
                progressClass: targetClass._description_translation
            });

            me.request = Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Classes.getCardsUrl(targetClass.name),
                method: 'GET',
                params: {
                    filter: Ext.JSON.encode({ query: ctx.query }),
                    sort: Ext.JSON.encode([{ property: 'Description', direction: 'ASC' }])
                },
                callback: function (options, success, response) {
                    if (!me.isCurrent(ctx)) {
                        return;
                    }

                    if (!success) {
                        // no error message when the user stopped the search
                        if (!ctx.stopped) {
                            CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.errors.genericerror);
                        }
                        return me.finishClassesSearch(ctx);
                    }

                    me.addClassResults(targetClass, (Ext.JSON.decode(response.responseText, true) || {}).data);

                    ctx.searched++;
                    me.searchNextClass(ctx);
                }
            });
        },

        /**
         * @param {Object} targetClass
         * @param {Object[]} cards
         */
        addClassResults: function (targetClass, cards) {
            const store = this.getResultsStore();

            if (Ext.isEmpty(cards)) {
                return;
            }

            store.add(
                cards.map(card => ({
                    _type: targetClass.name,
                    _cmdb_id: card._id,
                    Code: card.Code,
                    Description: card.Description
                }))
            );
            this.getViewModel().set('totalCount', store.getCount());
        },

        /**
         * Closes the default search (completed, failed or stopped).
         *
         * @param {Object} ctx Search context.
         */
        finishClassesSearch: function (ctx) {
            const total = this.getResultsStore().getCount();

            this.getViewModel().set({
                progress: 0,
                progressClass: null,
                totalCount: total
            });

            this.endSearch(ctx, { success: true, stopped: ctx.stopped, total: total });
        },

        /*
         * AI search
         */

        /**
         * Sends the query to the AI endpoint.
         *
         * @param {Object} ctx Search context.
         */
        aiSearch: function (ctx) {
            const me = this;

            me.getViewModel().set('aiStatus', 'loading');

            me.request = Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Ai.getAiUrl(),
                method: 'POST',
                timeout: me.AI_REQUEST_TIMEOUT,
                jsonData: { query: ctx.query, context: 'search' },
                callback: function (options, success, response) {
                    if (me.isCurrent(ctx)) {
                        me.finishAiSearch(ctx, success ? Ext.JSON.decode(response.responseText, true) : null);
                    }
                }
            });
        },

        /**
         * Shows the AI answer and its resources, or the error state.
         *
         * @param {Object} ctx Search context.
         * @param {Object} res Decoded response, `null` on failure.
         */
        finishAiSearch: function (ctx, res) {
            const me = this;
            const vm = me.getViewModel();
            const store = me.getResultsStore();

            if (!res) {
                vm.set('aiStatus', 'error');
                return me.endSearch(ctx, { success: false });
            }

            // the answer can be wrapped in `data`
            const payload = Ext.isObject(res.data) ? res.data : res;

            store.loadData(me.toAiResultRows(payload.resources));

            vm.set({
                aiStatus: 'success',
                aiAnswer: payload.response || '',
                aiResourcesCount: store.getCount()
            });

            me.loadMissingDescriptions(store);

            me.endSearch(ctx, { success: true, total: store.getCount() });
        },

        /**
         * Converts the AI resources (`{_id, _type, description}`) into grid rows,
         * skipping duplicates and the resources that are not cards or process instances.
         *
         * @param {Object[]} resources
         * @returns {Object[]}
         */
        toAiResultRows: function (resources) {
            const me = this;
            const seen = {};

            return Ext.Array.from(resources)
                .filter(function (resource) {
                    if (!Ext.isObject(resource) || !resource._id || !me.isOpenableType(resource._type)) {
                        return false;
                    }

                    const key = resource._type + '/' + resource._id;
                    const duplicated = seen[key];

                    seen[key] = true;
                    return !duplicated;
                })
                .map(resource => ({
                    _type: resource._type,
                    _cmdb_id: String(resource._id),
                    Description: resource.description || null
                }));
        },

        /**
         * Loads the cards of the rows without a description and sets
         * their Description (or Code).
         *
         * @param {Ext.data.Store} store
         */
        loadMissingDescriptions: function (store) {
            const me = this;
            const ModelHelper = CMDBuildUI.util.helper.ModelHelper;

            store.each(function (record) {
                const type = record.get('_type');

                if (record.get('Description')) {
                    return;
                }

                ModelHelper.getModel(ModelHelper.getObjectTypeByName(type), type).then(function (model) {
                    model.load(record.get('_cmdb_id'), {
                        callback: function (card, operation, success) {
                            // the row can be removed in the meantime by a new search
                            if (success && !me.destroyed && store.indexOf(record) !== -1) {
                                record.set('Description', card.get('Description') || card.get('Code'), {
                                    commit: true
                                });
                            }
                        }
                    });
                });
            });
        },

        /*
         * Card detail and navigation
         */

        /**
         * @param {String} type
         * @returns {Boolean} `true` if `type` is a class or a process.
         */
        isOpenableType: function (type) {
            const ModelHelper = CMDBuildUI.util.helper.ModelHelper;
            const objectType = type && ModelHelper.getObjectTypeByName(type);

            return objectType === ModelHelper.objecttypes.klass || objectType === ModelHelper.objecttypes.process;
        },

        /**
         * @param {String} type Class or process name.
         * @returns {Boolean}
         */
        isProcess: function (type) {
            const ModelHelper = CMDBuildUI.util.helper.ModelHelper;
            return ModelHelper.getObjectTypeByName(type) === ModelHelper.objecttypes.process;
        },

        /**
         * Shows the card (or process instance) of the record in the popup.
         *
         * @param {Ext.data.Model} record
         */
        showDetail: function (record) {
            const me = this;
            const ModelHelper = CMDBuildUI.util.helper.ModelHelper;
            const type = record.get('_type');
            const id = record.get('_cmdb_id');
            const isProcess = me.isProcess(type);

            ModelHelper.getModel(ModelHelper.getObjectTypeByName(type), type).then(function () {
                if (me.destroyed) {
                    return;
                }

                const view = me.getView();

                Ext.destroy(me.lookup('detail'));

                const detail = view.add({
                    xtype: isProcess ? 'processes-instances-instance-view' : 'classes-cards-card-view',
                    reference: 'detail',
                    shownInPopup: true,
                    objectTypeName: type,
                    objectId: id,
                    viewModel: {
                        data: {
                            objectTypeName: type,
                            objectId: id
                        }
                    }
                });

                me.detailRecord = record;
                me.getViewModel().set(
                    'detailTitle',
                    [ModelHelper.getObjectDescription(type), record.get('Description')].filter(Boolean).join(' - ')
                );
                view.getLayout().setActiveItem(detail);
            });
        },

        /**
         *
         * @param {Boolean} [selectLast] `true` to select the row of the detail just closed.
         */
        showResults: function (selectLast) {
            const me = this;
            const record = me.detailRecord;

            me.getView().getLayout().setActiveItem(0);
            me.getViewModel().set('detailTitle', null);
            Ext.destroy(me.lookup('detail'));
            me.detailRecord = null;

            if (selectLast && record) {
                me.lookup('resultsGrid').ensureVisible(record, { select: true, focus: true });
            }
        },

        /**
         * Closes the popup and opens the card (or process instance) in the application.
         *
         * @param {Ext.data.Model} record
         */
        openInApplication: function (record) {
            const type = record.get('_type');
            const id = record.get('_cmdb_id');
            const url = this.isProcess(type)
                ? Ext.String.format('processes/{0}/instances/{1}', type, id)
                : Ext.String.format('classes/{0}/cards/{1}/view', type, id);

            this.getView().close();
            CMDBuildUI.util.Utilities.redirectTo(url, true);
        }
    }
});
