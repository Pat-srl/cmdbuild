Ext.define('CMDBuildUI.view.bulkactions.Util', {
    singleton: true,

    /**
     *
     * @param {Ext.grid.Panel} grid
     * @return {Object} Object with `url`, `advancedFitler` and `count`.
     */
    getRequestInfo: function (grid) {
        const store = grid.getStore();
        const selection = grid.getSelection();
        const resp = {
            url: store.getProxy().getUrl(),
            advancedFitler: undefined,
            count: undefined,
            cardsId: undefined
        };

        // evaluate advanced filter and count
        if (grid.isSelectAllPressed) {
            // if select all advanced fitler becomes store filter
            // and count becomes store total count
            resp.advancedFitler = store.getAdvancedFilter().encode();
            resp.count = store.getTotalCount();
        } else {
            // if not select all create a filter with ID attribute
            // and value the list of selected items. Count is the
            // lenght of the array
            const selectedids = [];
            const lengthRequest = 50;

            selection.forEach(function (sel) {
                selectedids.push(sel.getId());
            });
            resp.cardsId = selectedids;
            const selectedLength = selectedids.length;
            if (selectedLength > lengthRequest) {
                const filters = [],
                    numberRequests = Math.ceil(selectedLength / lengthRequest);
                for (let i = 0; i < numberRequests; i++) {
                    const advancedFitler = new CMDBuildUI.util.AdvancedFilter();
                    advancedFitler.addAttributeFilter(
                        'Id',
                        'in',
                        Ext.Array.slice(selectedids, i * lengthRequest, (i + 1) * lengthRequest)
                    );
                    filters.push(advancedFitler.encode());
                }
                resp.advancedFitler = filters;
            } else {
                const advancedFitler = new CMDBuildUI.util.AdvancedFilter();
                advancedFitler.addAttributeFilter('Id', 'in', selectedids);
                resp.advancedFitler = advancedFitler.encode();
            }

            resp.count = selectedLength;
        }
        return resp;
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     */
    delete: function (grid) {
        // get request info
        const me = this;
        const requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid);
        const type = grid.lookupViewModel().get('objectTypeName');

        // create confirm message
        const message = Ext.String.format(CMDBuildUI.locales.Locales.bulkactions.confirmdelete, requestinfo.count);

        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        CMDBuildUI.view.classes.cards.Util.getDeleteMsg(
            type,
            requestinfo.advancedFitler,
            requestinfo.cardsId,
            message
        ).then(function () {
            const loadMask = CMDBuildUI.util.Utilities.addLoadMask(grid);
            const allPromises = [];

            if (Ext.isArray(requestinfo.advancedFitler)) {
                Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                    allPromises.push(me.makeDeleteRequest(requestinfo.url, item));
                });
            } else {
                allPromises.push(me.makeDeleteRequest(requestinfo.url, requestinfo.advancedFitler));
            }

            Ext.Promise.all(allPromises).then(
                function (responses, eOpts) {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    const selection = grid.getSelection();
                    grid.setSelection();
                    // reload store
                    grid.getStore().load();
                    const viewMap = grid.getGridContainer().getViewMap();
                    if (viewMap) {
                        Ext.Array.forEach(selection, function (card, index, allitems) {
                            viewMap.getMapContainerView().getNavigationTreeTab().getController().onCardDeleted(card);
                        });
                        viewMap.fireEvent('labelvisibilitychange');
                    }
                },
                function () {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                }
            );
        });
    },

    /**
     *
     * @param {*} grid
     */
    abort: function (grid) {
        // get request info
        const me = this;
        const requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid);

        // create confirm message
        const message = Ext.String.format(CMDBuildUI.locales.Locales.bulkactions.confirmabort, requestinfo.count);

        CMDBuildUI.util.Msg.confirm(CMDBuildUI.locales.Locales.notifier.attention, message, function (btn) {
            if (btn === 'yes') {
                CMDBuildUI.util.helper.FormHelper.startSavingForm();
                const loadMask = CMDBuildUI.util.Utilities.addLoadMask(grid),
                    allPromises = [];

                if (Ext.isArray(requestinfo.advancedFitler)) {
                    Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                        allPromises.push(me.makeDeleteRequest(requestinfo.url, item));
                    });
                } else {
                    allPromises.push(me.makeDeleteRequest(requestinfo.url, requestinfo.advancedFitler));
                }

                Ext.Promise.all(allPromises).then(
                    function (responses, eOpts) {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                        grid.setSelection();
                        // reload store
                        grid.getStore().load();
                    },
                    function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                    }
                );
            }
        });
    },

    privates: {
        /**
         * Make delete request
         * @param {String} url
         * @param {String} filter
         * @returns {Ext.promise.Promise}
         */
        makeDeleteRequest: function (url, filter) {
            const deferred = new Ext.Deferred();

            // make ajax request
            Ext.Ajax.request({
                url: url,
                method: 'DELETE',
                jsonData: {},
                params: {
                    filter: filter
                },
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        }
    }
});
