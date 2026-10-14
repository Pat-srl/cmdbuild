Ext.define('CMDBuildUI.view.administration.content.dashboards.common.mixin.TileControllerMixin', {
    extend: 'Ext.Mixin',

    mixinConfig: {
        id: 'tileController'
    },

    // ======================================================
    // RENDER & BUILDERS
    // ======================================================

    /**
     * @param {Ext.container.Container} container
     */
    onTilesContainerRender: function (container) {
        const me = this;
        const vm = me.getViewModel();
        const theComponent = vm.get('theComponent');

        if (!theComponent) return;

        const functionStore = vm.getStore('functionsStore');

        functionStore.on(
            'load',
            function () {
                theComponent.getTilesStore().each(function (record, index) {
                    const fieldset = container.add(me.buildTileFieldset(record, index));
                    if (record.get('dataSourceName')) {
                        const functionRecord = functionStore.findRecord('name', record.get('dataSourceName'));
                        me.loadTileFunctionAttributes(fieldset, functionRecord);
                    }
                    if (record.get('scope') === 'selected' && record.get('goto')) {
                        me.buildCardReferenceField(fieldset, record.get('goto'));
                    }
                });
            },
            me,
            { single: true }
        );

        if (!functionStore.isLoading()) {
            functionStore.load();
        }
    },

    /**
     * Builds the ExtJS config (viewModel, stores, fields) for a single tile fieldset.
     *
     * @param {Ext.data.Model} record - tile record from theComponent.getTilesStore()
     * @param {Number} index - zero-based tile index, used for the fieldset title
     * @returns {Object} ExtJS fieldset config
     */
    buildTileFieldset: function (record, index) {
        index = index + 1;
        return {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.dashboards.tile + ' #' + index,
            localized: {
                title: CMDBuildUI.locales.Locales.administration.dashboards.tile + ' #' + index
            },
            layout: 'column',
            columnWidth: 1,
            itemId: 'tileFieldset_' + record.get('_id'),
            viewModel: {
                data: {
                    theComponent: record
                },
                formulas: {
                    tileSearchFiltersFilteredData: {
                        bind: '{theComponent.goto}',
                        get: function (goto) {
                            return CMDBuildUI.util.helper.ModelHelper.getSearchFiltersByTarget(goto);
                        }
                    },

                    cardorfilterDescription: {
                        bind: {
                            cardorfilter: '{theComponent.cardorfilter}',
                            goto: '{theComponent.goto}',
                            scope: '{theComponent.scope}'
                        },
                        get: function (data) {
                            var me = this;

                            if (data.scope !== 'selected' || !data.cardorfilter || !data.goto) {
                                me.set('theComponent._cardorfilterDescription', '');
                                return;
                            }

                            var targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(data.goto);
                            var promise;

                            if (targetType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                                promise = CMDBuildUI.util.api.Client.getRemoteProcessInstance(
                                    data.goto,
                                    data.cardorfilter
                                );
                            } else {
                                promise = CMDBuildUI.util.api.Client.getRemoteCard(data.goto, data.cardorfilter);
                            }

                            promise.then(function (record) {
                                me.set('theComponent._cardorfilterDescription', record.get('Description'));
                            });
                        }
                    }
                },
                stores: {
                    // used for the value field combo
                    tileFunctionAttributesStore: {
                        model: 'CMDBuildUI.model.Attribute',
                        proxy: { type: 'memory' }
                    },

                    // used for the foregroung color combo
                    tileFunctionAttributesStoreString: {
                        source: '{tileFunctionAttributesStore}',
                        filters: [{ property: 'type', operator: '=', value: 'string' }]
                    },

                    tileSearchFiltersFilteredStore: {
                        model: 'CMDBuildUI.model.base.Filter',
                        data: '{tileSearchFiltersFilteredData}',
                        pageSize: 0,
                        proxy: { type: 'memory' }
                    }
                }
            },
            items: [
                {
                    xtype: 'tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
                    cls: 'administration-tool',
                    style: 'position: absolute; right: 0;',
                    itemId: 'removeTileTool',
                    recordId: record.get('_id'),
                    hidden: true,
                    bind: { hidden: '{actions.view}' }
                }
            ].concat(
                CMDBuildUI.view.administration.content.dashboards.common.FieldsetHelper.getSingleTileConfigurationFieldset()
            )
        };
    },

    /**
     * Dynamically creates/replaces the referencecombofield used to pick a card/instance
     * for a tile with scope 'selected', scoped to the resolved class or process.
     *
     * @param {Ext.form.FieldSet} fieldset - the tile's fieldset
     * @param {String} gotoValue - target class or process name
     */
    buildCardReferenceField: function (fieldset, gotoValue) {
        var container = fieldset.down('#cardorfilter_selected_container');
        if (!container) return;

        container.removeAll();

        var targetType = gotoValue ? CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(gotoValue) : undefined;

        container.add({
            xtype: 'referencecombofield',
            itemId: 'cardorfilter_selected_input',
            name: 'cardorfilter',
            displayField: 'Description',
            valueField: '_id',
            width: '100%',
            disabled: !gotoValue,
            metadata: {
                targetType: gotoValue ? targetType : undefined,
                targetClass: gotoValue ? gotoValue : undefined
            },
            bind: {
                value: '{theComponent.cardorfilter}'
            },
            validator: function (val) {
                return true;
            }
        });
    },

    // ======================================================
    // MAIN ACTIONS
    // ======================================================

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddTileBtnClick: function (button, e, eOpts) {
        var me = this,
            vm = me.getViewModel(),
            theComponent = vm.get('theComponent'),
            view = me.getView(),
            container = view.down('#tilesContainer'),
            tilesStore = theComponent.getTilesStore();

        var record = tilesStore.add({
            _id: CMDBuildUI.util.Utilities.generateUUID()
        })[0];

        var index = tilesStore.indexOf(record);

        container.add(me.buildTileFieldset(record, index));
    },

    /**
     * @param {Ext.Component} tool - the trash tool clicked, carrying tool.recordId
     */
    onRemoveTileToolClick: function (tool) {
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === 'yes') {
                    var vm = this.getViewModel(),
                        theComponent = vm.get('theComponent'),
                        tilesStore = theComponent.getTilesStore();

                    var record = tilesStore.findRecord('_id', tool.recordId);
                    if (record) {
                        tilesStore.remove(record);
                    }

                    Ext.asap(function () {
                        tool.up('fieldset').destroy();
                    });
                }
            },
            this
        );
    },

    /**
     * Handles the click event for the tile label translation trigger element.
     *
     * @param {Ext.event.Event} event
     * @param {HTMLElement} target
     * @param {Object} [eOpts]
     *
     * @returns {Void}
     */
    onTranslateTileLabelClick: function (event, target, eOpts) {
        const field = Ext.Component.fromElement(target);
        if (!field) return;

        const tileRecord = this.getTileRecordFromField(field);
        if (!tileRecord) return;

        const vm = this.getViewModel();
        const ownerVm = vm.get('owner').getViewModel();
        const tileId = tileRecord.get('_id');

        const getLocaleKeyFn = this.isHomepageContext()
            ? CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentTileLabel
            : CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentTileLabel;

        const translationCode = getLocaleKeyFn(ownerVm.get('theDashboard.name'), tileId);
        const vmLocaleObject = Ext.String.format('theComponentTileLabel_{0}', tileId.replaceAll('-', '_'));

        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            vmLocaleObject,
            ownerVm,
            true
        );
    },

    // ======================================================
    // ON FIELD CHANGE LISTENERS
    // ======================================================

    /**
     * On user change of a tile's data function
     *
     * @param {Ext.form.field.ComboBox} field
     * @param {String} newValue - selected function name
     * @param {String} oldValue
     */
    onTileFunctionChange: function (field, newValue, oldValue) {
        if (!this.shouldIgnoreChange(field, newValue, oldValue)) {
            const me = this;
            const view = me.getView();
            const vm = view.lookupViewModel();
            const fieldset = field.up('fieldset');
            const functionStore = vm.getStore('functionsStore');
            const functionRecord = functionStore.findRecord('name', newValue);

            this.clearCurrentTileFields(field, ['valueField', 'colortype', 'fgcolor', 'fgcolorField']);
            this.loadTileFunctionAttributes(fieldset, functionRecord);
        }
    },

    /**
     * On user change of a tile's color type
     *
     * @param {Ext.form.field.ComboBox} field
     * @param {String} newValue - 'static' | 'dynamic'
     * @param {String} oldValue
     */
    onTileColorTypeChange: function (field, newValue, oldValue) {
        if (!this.shouldIgnoreChange(field, newValue, oldValue)) {
            this.clearCurrentTileFields(field, ['fgcolor', 'fgcolorField']);
        }
    },

    /**
     * On user change of a tile's navigation target.
     *
     * @param {Ext.form.field.ComboBox} field
     * @param {String} newValue - target class or process name
     * @param {String} oldValue
     */
    onTileGoToChange: function (field, newValue, oldValue) {
        if (!this.shouldIgnoreChange(field, newValue, oldValue)) {
            this.clearCurrentTileFields(field, ['scope', 'cardorfilter']);
        }
    },

    /**
     * On user change of a tile's navigation scope.
     *
     * @param {Ext.form.field.ComboBox} field
     * @param {String} newValue - 'all' | 'filtered' | 'selected'
     * @param {String} oldValue
     */
    onTileScopeChange: function (field, newValue, oldValue) {
        if (!this.shouldIgnoreChange(field, newValue, oldValue)) {
            this.clearCurrentTileFields(field, ['cardorfilter']);

            const fieldset = field.up('fieldset');
            const container = fieldset.down('#cardorfilter_selected_container');
            if (newValue === 'selected') {
                const currentTileRecord = this.getTileRecordFromField(field);
                this.buildCardReferenceField(fieldset, currentTileRecord.get('goto'));
            } else if (container) {
                container.removeAll();
            }
        }
    },

    privates: {
        /**
         * Determines whether a field change event should be ignored.
         *
         * @param {Ext.form.field.Base} field
         * @param {*} newValue
         * @param {*} oldValue
         * @returns {Boolean}
         */
        shouldIgnoreChange: function (field, newValue, oldValue) {
            return !field.hasFocus || newValue === oldValue;
        },

        /**
         * Resolves the tile record backing a given field.
         *
         * @param {Ext.form.field.Base} field
         * @returns {Ext.data.Model|null}
         */
        getTileRecordFromField: function (field) {
            const fieldset = field.up('fieldset');
            if (!fieldset) return null;

            const tileViewModel = fieldset.getViewModel();
            if (!tileViewModel) return null;

            return tileViewModel.get('theComponent') || null;
        },

        /**
         * Nulls out the given properties on the tile record backing the given field.
         *
         * @param {Ext.form.field.Base} field
         * @param {String[]} fieldsToClear
         */
        clearCurrentTileFields: function (field, fieldsToClear) {
            const currentTileRecord = this.getTileRecordFromField(field);
            if (!currentTileRecord) return;

            let updates = {};
            for (let i = 0; i < fieldsToClear.length; i++) {
                updates[fieldsToClear[i]] = null;
            }

            currentTileRecord.set(updates);
        },

        /**
         * Loads and sets a tile's function attributes into its per-tile memory store.
         *
         * @param {Ext.form.FieldSet} fieldset - the tile's fieldset
         * @param {CMDBuildUI.model.Function|null} functionRecord
         */
        loadTileFunctionAttributes: function (fieldset, functionRecord) {
            const tileVm = fieldset.getViewModel();
            const store = tileVm ? tileVm.getStore('tileFunctionAttributesStore') : null;
            if (!store) return;

            if (!functionRecord) {
                store.getProxy().setData([]);
                store.load();
                return;
            }

            functionRecord.getAttributes().then(function (attributes) {
                store.getProxy().setData(attributes.getRange());
                store.load();
            });
        }
    }
});
