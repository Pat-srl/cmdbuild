Ext.define('CMDBuildUI.view.thematisms.LauncherController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-launcher',
    listen: {
        component: {
            '#mainbtnThematism': {
                beforerender: 'onMainBtnBeforeRender'
            },
            '#clearthematismtool': {
                click: 'onClearThematismToolClick'
            },
            '#thematismdesc': {
                click: 'onOpenMenuClick'
            }
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onMainBtnBeforeRender: function (button, eOpts) {
        var vm = this.getViewModel();
        var me = this;

        vm.bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            }
        }, function (data) {
            if (data.objectTypeName && data.objectType) {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                vm.set("item", item);
                item.getThematisms().then(function (thematisms) {
                    me.initMenu(button, thematisms.getRange());
                });
            }
        });
    },

    /**
     * Open a popup to create a new filter
     */
    onAddNewThematismClick: function () {
        var vm = this.getViewModel();
        var thematism = Ext.create('CMDBuildUI.model.thematisms.Thematism', {
            name: CMDBuildUI.locales.Locales.thematism.newThematism,
            description: CMDBuildUI.locales.Locales.thematism.newThematism,
            owner: vm.get('objectTypeName'),
            analysistype: CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual,
            type: CMDBuildUI.model.thematisms.Thematism.sources.table,
            segments: null
        });
        this.editThematism(thematism);
    },

    onOpenMenuClick: function () {
        var view = this.getView();
        if (!view.isMenuExpanded) {
            view.expandMenu();
        }
    },

    /**
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     */
    editThematism: function (thematism) {
        var me = this,
            vm = this.getViewModel(),
            viewmodel = {
                data: {
                    objectType: vm.get("objectType"),
                    objectTypeName: vm.get("objectTypeName"),
                    theThematism: thematism
                }
            },
            // popup definition
            popup = CMDBuildUI.util.Utilities.openPopup(null, thematism.get("name"), {
                xtype: 'thematisms-panel',
                viewModel: viewmodel,
                listeners: {
                    /**
                     *
                     * @param {CMDBuildUI.view.thematisms.Panel} panel
                     * @param {CMDBuildUI.model.themtism.Thematism} thematism
                     * @param {Object} eOpts
                     */
                    applythematism: function (panel, thematism, eOpts) {
                        var applyThematismHandler = function () {
                            thematism.set('tryRules', true);
                            me.onApplyThematism(thematism, true);
                            popup.close();
                        };

                        if (thematism.hasRules()) {
                            applyThematismHandler();
                        } else {
                            me.calculateRulesHandler(panel, thematism, eOpts, applyThematismHandler, me);
                        }
                    },
                    /**
                     *
                     * @param {CMDBuildUI.view.thematisms.Panel} panel
                     * @param {CMDBuildUI.model.themtism.Thematism} thematism
                     * @param {Object} eOpts
                     */
                    saveandapplythematism: function (panel, thematism, eOpts) {
                        var saveAndApplyHandler = function () {
                            thematism.set('tryRules', false);
                            me.onSaveAndApplyThematism(thematism, panel).then(function (thematism) {
                                if (thematism) {
                                    popup.close();
                                }
                            });
                        }

                        if (thematism.hasRules()) {
                            saveAndApplyHandler();
                        } else {
                            me.calculateRulesHandler(panel, thematism, eOpts, saveAndApplyHandler, me);
                        }
                    },

                    /**
                     *
                     * @param {CMDBuildUI.view.thematisms.Panel} panel
                     * @param {CMDBuildUI.model.themtism.Thematism} thematism
                     * @param {Object} eOpts
                     */
                    calculaterules: function (panel, thematism, eOpts) {
                        me.calculateRulesHandler(panel, thematism, eOpts);
                    },

                    /**
                     * Custom event to close popup directly from popup
                     * @param {Object} eOpts
                     */
                    popupclose: function (panel, thematism, eOpts) {
                        thematism.reject(true);
                        thematism.rules().rejectChanges();
                        popup.close();
                    }
                }
            });
    },

    /**
     *
     * @param {CMDBuildUI.view.thematisms.Panel} panel
     * @param {CMDBuildUI.model.themtism.Thematism} thematism
     * @param {Object} eOpts
     * @param {Function} callback
     * @param {Object} scope
     */
    calculateRulesHandler: function (panel, thematism, eOpts, callback, scope) {
        var me = this;

        me.onCalculateRules(thematism, function () {
            if (callback) {
                callback.call(scope);
            }
        }, this);
    },

    /**
     *
     * @param {CMDBuildUI.model.thematism.Thematism} thematism
     */
    deleteThematism: function (grid, thematism) {
        var me = this,
            vm = me.getViewModel();
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.thematism.deletethematism,
            function (btnText) {
                if (btnText === "yes") {
                    // erase item
                    if (vm.get("appliedthematism.id") === thematism.getId()) {
                        me.onClearThematismToolClick();
                    }
                    thematism.erase();
                    grid.getStore().remove(thematism);
                    var button = me.lookup("mainbtn");
                    me.initMenu(button, grid.getStore().getRange());
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                } else {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            }
        );

    },
    /**
     *
     * @param {CMDBuildUI.model.thematism.Thematism} thematism
     * @param {Boolean} calltype tells if the apply comes from an apply or saveandApply event
     */
    onApplyThematism: function (thematism, calltype) {
        var me = this;
        me.applyThematism(thematism, calltype);

        var button = me.lookup("mainbtn");
        button.un("click", me.onAddNewThematismClick, me);
        button.on("click", me.onOpenMenuClick, me);
    },

    /**
     *
     * @param {CMDBuildUI.model.themtism.Thematism} thematism
     * @param {Ext.form.Panel} panel
     */
    onSaveAndApplyThematism: function (thematism, panel) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var deferred = new Ext.Deferred();
        var me = this;

        thematism.save({
            success: function (record, operation) {
                thematism.rules().commitChanges();
                me.onApplyThematism(thematism);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                deferred.resolve(thematism);
            },
            failure: function () {
                var applyBtn = panel.down("#applybutton"),
                    applyAndSaveBtn = panel.down("#savebutton"),
                    cancelBtn = panel.down("#cancelbutton");
                CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, applyAndSaveBtn, cancelBtn]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                deferred.reject();
            },
            scope: me
        });

        return deferred;
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {CMDBuildUI.view.thematisms.Launcher} owner
     * @param {Object} eOpts
     */
    onClearThematismToolClick: function (tool, e, owner, eOpts) {
        this.getView().clearThematism();
        Ext.GlobalEvents.fireEvent('applythematism', null);
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {HTMLElement} td
     * @param {Number} cellIndex
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     * @param {HTMLElement} tr
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onThematismGridCellClick: function (grid, td, cellIndex, thematism, tr, rowIndex, e, eOpts) {
        var me = this;
        if (cellIndex === 0) {
            me.onClearThematismToolClick();
            me.applyThematism(thematism, thematism.get('tryRules'));
            me.getView().collapseMenu();
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     * @param {Function} callback
     * @param {Object} scope
     */
    onCalculateRules: function (thematism, callback, scope) {
        CMDBuildUI.thematisms.util.Util.calculateRules(thematism,
            /**
             *
             * @param {CMDBuildUI.model.thematisms.Thematism} thematism
             * @param {[CMDBuildUI.model.thematisms.Rules]} rules
             */
            function (thematism, rules) {
                // thematism.rules().clearData();
                // thematism.rules().insert(0, rules);
                // thematism.stringifyThematism();
                if (callback) {
                    callback.call(scope || this);
                }
            }, this);
    },

    privates: {

        /**
         * @param {CMDBuildUI.model.thematisms.Thematism} thematism
         * @param {Boolean} calltype tells if the apply comes from an apply or saveandApply event
         */
        applyThematism: function (thematism, calltype) {
            var vm = this.getViewModel();

            function applyThematism() {
                Ext.GlobalEvents.fireEvent('applythematism', thematism.getId());

                //have to add a new record in the thematisms store of the class
                var thematisms = vm.get("item").thematisms();
                var thematismposition = thematisms.findRecord('_id', thematism.getId());
                if (!thematismposition || thematismposition === -1) {
                    // thematismposition.add(thematism)
                    thematisms.add(thematism);
                }

                vm.set('appliedthematism.id', thematism.getId());
                vm.set('appliedthematism.name', thematism.get('name'));
            }

            applyThematism();
        },

        initMenu: function (button, thematisms) {
            var me = this;
            if (thematisms && thematisms.length) {
                button.un("click", me.onAddNewThematismClick, me);
                button.on("click", me.onOpenMenuClick, me);
            } else {
                button.un("click", me.onOpenMenuClick, me);
                button.on("click", me.onAddNewThematismClick, me);
            }
        }
    }

});