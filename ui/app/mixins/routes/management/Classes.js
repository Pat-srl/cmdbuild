Ext.define('CMDBuildUI.mixins.routes.management.Classes', {
    mixinId: 'managementroutes-classes-mixin',

    /******************* CARDS GRID ********************/
    /**
     * Before show cards grid
     *
     * @param {String} className
     * @param {Object} action
     */
    onBeforeShowCardsGrid: function (className, action) {
        const me = this;
        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        if (CMDBuildUI.util.helper.ModelHelper.getClassFromName(className)) {
            // redirect to custom routing
            if (me.getCustomClassActionRouting(className, null, 'showGrid', action)) {
                return;
            }
            if (CMDBuildUI.util.Navigation.checkCurrentContext(type, className, true)) {
                if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(null)) {
                    // fire global event objectidchanged
                    Ext.GlobalEvents.fireEventArgs('objectidchanged', [null]);
                    CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(null);
                }
                action.stop();
            } else {
                action.resume();
            }
        } else {
            CMDBuildUI.util.Utilities.redirectTo('management');
            action.stop();
        }
    },

    /**
     * Show cards grid
     *
     * @param {String} className
     * @param {Numeric} cardId This attribute is used when the function
     * is called dicretly from code, not from router.
     */
    showCardsGrid: function (className, cardId) {
        // update current context
        if (!CMDBuildUI.util.Navigation.shouldUseCustomRouting.call(this, className)) {
            CMDBuildUI.util.Navigation.updateCurrentManagementContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                className,
                cardId
            );

            CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true); //suspend the detailsWindow events
            CMDBuildUI.util.Navigation.addIntoManagementContainer('classes-cards-grid-container', {
                objectTypeName: className,
                maingrid: true,
                viewModel: {
                    data: {
                        selectedId: cardId ? cardId : null
                    }
                }
            });

            // fire global event objecttypechanged
            Ext.GlobalEvents.fireEventArgs('objecttypechanged', [className]);
        }
    },

    /******************* CARD ********************/
    /**
     * Before show card view
     *
     * @param {String} className
     * @param {Numeric} cardId
     * @param {Object} action
     */
    onBeforeShowCard: function (className, cardId, action) {
        //removes the detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);

        //removes action from the context
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(null);

        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        CMDBuildUI.util.helper.ModelHelper.getModel(type, className).then(
            function (model) {
                if (CMDBuildUI.util.Navigation.checkCurrentContext(type, className, true)) {
                    if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextObjectId(cardId)) {
                        // fire global event objectidchanged
                        Ext.GlobalEvents.fireEventArgs('objectidchanged', [cardId]);
                        CMDBuildUI.util.Navigation.updateCurrentManagementContextObjectId(cardId);
                    }
                    action.stop();
                } else {
                    action.resume();
                }
            },
            function () {
                action.stop();
            }
        );
    },

    /**
     * Show card view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCard: function (className, cardId) {
        this.showCardsGrid(className, cardId);
    },

    /******************* CARD VIEW ********************/
    /**
     * Before show card view
     *
     * @param {String} className
     * @param {Numeric} cardId
     * @param {Object} action
     */
    onBeforeShowCardWindow: function (className, cardId, formMode, action) {
        const me = this;
        // fix variables for create form
        if (!action) {
            action = cardId;
            cardId = null;
        }

        // load model
        const type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        CMDBuildUI.util.helper.ModelHelper.getModel(type, className).then(
            function (model) {
                // redirect to custom routing
                if (me.getCustomClassActionRouting(className, cardId, formMode, action)) {
                    return;
                }
                // check consisntence of main content
                if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, className, true)) {
                    // show cards grid for className
                    me.showCardsGrid(className, cardId);
                }
                // resume action
                action.resume();
            },
            function () {
                action.stop();
            }
        );
    },

    /**
     * Show card view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardView: function (className, cardId) {
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.view);
    },

    /**
     * Show details view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardDetails: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.detail_read)) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show notes view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardNotes: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.note_read)) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.notes);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show relation view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardRelations: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.relation_read)) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.relations);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show history view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardHistory: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.history_read)) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.history);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show emails view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardEmails: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (obj.get(CMDBuildUI.model.users.Grant.permissions.email_read)) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.emails);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show relation view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardAttachments: function (className, cardId) {
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) &&
            obj.get(CMDBuildUI.model.users.Grant.permissions.attachment_read)
        ) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.attachments);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show relation view
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardSchedules: function (className, cardId) {
        const privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get('rolePrivileges'),
            obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className, 'class');
        if (
            (privileges.calendar_access || privileges.calendar_event_create) &&
            obj.get(CMDBuildUI.model.users.Grant.permissions.schedule_read)
        ) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.schedules);
        } else {
            this.redirectTo(Ext.String.format('classes/{0}/cards/{1}', className, cardId));
        }
    },

    /**
     * Show card edit
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardEdit: function (className, cardId) {
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
    },

    /**
     * Show card clone
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardClone: function (className, cardId) {
        let config;

        if (
            CMDBuildUI.util.Navigation.getCurrentContext().objectType ===
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
        ) {
            // if context is navigation
            // get selectect node
            const sel = CMDBuildUI.util.Navigation.getManagementNavigation().getSelection();
            const navtreedef = sel.get('_navtreedef');
            const domain = navtreedef.get('domain');
            const value = sel.get('_objectid');
            const description = sel.get('_objectdescription');

            // configs for card create panel
            config = {
                // the event will be fired by the onAfterSave function
                fireGlobalEventsAfterSave: false,

                // add default value
                defaultValues: [
                    {
                        domain: domain,
                        value: value,
                        valuedescription: description,
                        editable: true
                    }
                ],

                onAfterSave: function (record) {
                    // refresh navigation
                    CMDBuildUI.util.Navigation.refreshNavigationTree(record);
                    const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
                    if (detailWindow && !detailWindow.navTreeNode) {
                        // fire global event
                        Ext.GlobalEvents.fireEventArgs('cardcreated', [record]);
                    }
                }
            };
        }
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.clone, config);
    },

    /**
     * Show card clone and relations
     *
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardCloneandRelations: function (className, cardId) {
        let config;

        if (
            CMDBuildUI.util.Navigation.getCurrentContext().objectType ===
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
        ) {
            // if context is navigation
            // get selectect node
            const sel = CMDBuildUI.util.Navigation.getManagementNavigation().getSelection();
            const navtreedef = sel.get('_navtreedef');
            const domain = navtreedef.get('domain');
            const value = sel.get('_objectid');
            const description = sel.get('_objectdescription');

            // configs for card create panel
            config = {
                // the event will be fired by the onAfterSave function
                fireGlobalEventsAfterSave: false,

                // add default value
                defaultValues: [
                    {
                        domain: domain,
                        value: value,
                        valuedescription: description,
                        editable: true
                    }
                ],

                onAfterSave: function (record) {
                    // refresh navigation
                    CMDBuildUI.util.Navigation.refreshNavigationTree(record);
                    const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
                    if (detailWindow && !detailWindow.navTreeNode) {
                        // fire global event
                        Ext.GlobalEvents.fireEventArgs('cardcreated', [record]);
                    }
                }
            };
        }
        this.showCardTabPanel(
            className,
            cardId,
            CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
            config
        );
    },

    /**
     * Show card create
     *
     * @param {String} className
     */
    showCardCreate: function (className) {
        let config;

        if (
            CMDBuildUI.util.Navigation.getCurrentContext().objectType ===
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
        ) {
            // if context is navigation
            // get selectect node
            const sel = CMDBuildUI.util.Navigation.getManagementNavigation().getSelection();
            const navtreedef = sel.get('_navtreedef');
            const domain = navtreedef.get('domain');
            const direction = navtreedef.get('_direction');
            const objecttype = sel.get('_objecttype');
            const value = sel.get('_objectid');
            const description = sel.get('_objectdescription');

            // configs for card create panel
            config = {
                // the event will be fired by the onAfterSave function
                fireGlobalEventsAfterSave: false,

                // add default value
                defaultValues: [
                    {
                        domain: domain,
                        value: value,
                        valuedescription: description,
                        editable: true
                    }
                ],

                onAfterSave: function (record) {
                    // add relation
                    const relStore = Ext.create('Ext.data.Store', {
                        model: 'CMDBuildUI.model.domains.Relation',
                        proxy: {
                            type: 'baseproxy',
                            url: CMDBuildUI.util.api.Classes.getCardRelations(record.get('_type'), record.get('_id'))
                        },
                        autoLoad: false
                    });

                    relStore.load({
                        callback: function (records, operation, success) {
                            // creates the relation if it is not created by a reference field
                            if (domain && !relStore.query('_type', domain).length) {
                                relStore.add({
                                    _type: domain,
                                    _sourceType: record.get('_type'),
                                    _sourceId: record.get('_id'),
                                    _destinationType: objecttype,
                                    _destinationId: value,
                                    _is_direct: direction == '_1'
                                });
                                relStore.sync({
                                    callback: function () {
                                        onRelationCreated();
                                    }
                                });
                            } else {
                                onRelationCreated();
                            }
                        }
                    });

                    function onRelationCreated() {
                        // refresh navigation
                        CMDBuildUI.util.Navigation.refreshNavigationTree(record);
                        const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
                        if (detailWindow && !detailWindow.navTreeNode) {
                            // fire global event
                            Ext.GlobalEvents.fireEventArgs('cardcreated', [record]);
                        }

                        // destroy store
                        Ext.asap(function () {
                            relStore.destroy();
                        });
                    }
                }
            };
        }
        this.showCardTabPanel(className, null, CMDBuildUI.mixins.DetailsTabPanel.actions.create, config);
    },

    privates: {
        /**
         * Show card tab panel
         * @param {String} className
         * @param {Number} cardId
         * @param {String} action
         * @param {Object} config
         */
        showCardTabPanel: function (className, cardId, action, config) {
            if (this.getCustomClassActionRouting(className, cardId, action)) {
                return;
            }

            config = config || {};
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action)) {
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('classes-cards-tabpanel', {
                    tabtools: [],
                    viewModel: {
                        data: {
                            objectTypeName: className,
                            objectId: cardId,
                            action: action
                        }
                    },
                    formConfig: config
                });
            }
        },

        /**
         *
         * @param {String} className
         * @param {String} idCard
         * @param {String} formMode
         * @param {String} action
         *
         * @return {Boolean}
         */
        getCustomClassActionRouting: function (className, idCard, formMode, action) {
            const actions = CMDBuildUI.util.Navigation.getClassActionsMap();
            const klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className);
            const uiRouting_custom = klass.get('uiRouting_custom') || {};
            let customUiRouting = uiRouting_custom[actions[formMode]];

            if (!Ext.isEmpty(customUiRouting)) {
                customUiRouting = customUiRouting.replace(':className', className);
                customUiRouting = customUiRouting.replace(':idCard', idCard);

                CMDBuildUI.util.Utilities.redirectTo(customUiRouting, true);
                if (action) {
                    action.stop();
                }
                return true;
            }
        }
    }
});
