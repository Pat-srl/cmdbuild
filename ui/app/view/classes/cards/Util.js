Ext.define('CMDBuildUI.view.classes.cards.Util', {
    singleton: true,

    getTools: function () {
        return [
            {
                // open tool
                xtype: 'tool',
                itemId: 'opentool',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                cls: 'management-tool',
                action: 'view',
                hidden: true,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                autoEl: {
                    'data-testid': 'classes-cards-tool-open'
                },
                bind: {
                    hidden: '{hiddentools.open}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
                }
            },
            {
                // edit tool
                xtype: 'tool',
                itemId: 'editBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                cls: 'management-tool',
                hidden: true,
                disabled: true,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                autoEl: {
                    'data-testid': 'classes-cards-tool-edit'
                },
                bind: {
                    hidden: '{hiddentools.edit}',
                    disabled: '{!permissions.edit}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.modifycard'
                }
            },
            {
                // delete tool
                xtype: 'tool',
                itemId: 'deleteBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                cls: 'management-tool',
                hidden: true,
                disabled: true,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.deletecard,
                autoEl: {
                    'data-testid': 'classes-cards-tool-delete'
                },
                bind: {
                    hidden: '{hiddentools.delete}',
                    disabled: '{!permissions.delete}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.deletecard'
                }
            },
            {
                // clone tool
                xtype: 'tool',
                itemId: 'cloneMenuBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                cls: 'management-tool',
                hidden: true,
                disabled: true,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.clone,
                autoEl: {
                    'data-testid': 'classes-cards-tool-clone'
                },
                bind: {
                    hidden: '{hiddentools.clone}',
                    disabled: '{!permissions.clone}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.clone'
                }
            },
            {
                // bim tool
                xtype: 'tool',
                itemId: 'bimBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cube', 'solid'),
                cls: 'management-tool',
                hidden: true,
                tooltip: CMDBuildUI.locales.Locales.bim.showBimCard,
                autoEl: {
                    'data-testid': 'classes-cards-tool-bim'
                },
                bind: {
                    hidden: '{hiddentools.bim}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.showBimCard'
                }
            },
            {
                // dependency tree tool
                xtype: 'tool',
                itemId: 'dependencytreetool',
                iconCls: 'cmdbuildicon-relgraph',
                cls: 'management-tool',
                hidden: true,
                autoEl: {
                    'data-testid': 'classes-cards-tool-dependencytree'
                },
                bind: {
                    hidden: '{hiddentools.dependencytree && hiddentools.relgraph}'
                },
                tooltip: CMDBuildUI.locales.Locales.dependencytree.opendependencytree,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.dependencytree.opendependencytree'
                }
            },
            {
                // print tool
                xtype: 'tool',
                itemId: 'printBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('print', 'solid'),
                cls: 'management-tool',
                hidden: true,
                disabled: true,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.print,
                autoEl: {
                    'data-testid': 'classes-cards-tool-print'
                },
                bind: {
                    hidden: '{hiddentools.print}',
                    disabled: '{!permissions.print}'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.print'
                }
            },
            this.getHelpTool()
        ];
    },

    getHelpTool: function () {
        return {
            // help tool
            xtype: 'tool',
            itemId: 'helpBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid'),
            cls: 'management-tool no-action',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.common.actions.help,
            autoEl: {
                'data-testid': 'classes-cards-tool-help'
            },
            bind: {
                hidden: '{hiddentools.help}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.help'
            }
        };
    },

    /**
     * Remove card
     *
     * @param {CMDBuildUI.model.classes.Card} card
     *
     * @return {Ext.promise.Promise}
     */
    deleteCard: function (card) {
        var deferred = new Ext.Deferred();

        this.getDeleteMsg(
            card.get('_type'),
            {
                attribute: {
                    simple: {
                        attribute: 'Id',
                        operator: 'equal',
                        value: [card.get('_id')]
                    }
                }
            },
            [card.get('_id')],
            CMDBuildUI.locales.Locales.classes.cards.deleteconfirmation
        ).then(function () {
            CMDBuildUI.util.Ajax.setActionId('class.card.delete');
            // erase the object
            card.erase({
                success: function (record, operation) {
                    deferred.resolve(record);
                },
                failure: function (record, operation) {
                    deferred.reject();
                }
            });
        });
        return deferred.promise;
    },

    /**
     *
     * @param {String} type Class type
     * @param {Object} filter filter of class
     * @param {cardsId} cardsId ids for search foreign keys
     * @param {String} basemsg base message
     *
     * @return {Ext.promise.Promise}
     */
    getDeleteMsg: function (type, filter, cardsId, basemsg) {
        const me = this;
        const deferred = new Ext.Deferred();
        const allPromises = [];
        const blockremove = {};

        const promiseTypes = [];

        // Get the class
        const className = CMDBuildUI.util.helper.ModelHelper.getClassFromName(type);
        className.getFkDomains().then(function (result) {
            const items = result.getRange();
            Ext.Array.forEach(items, function (item) {
                const cascadeAction = item.get('cascadeAction');
                const sourceType = item.get('source');
                const fk_attribute_name = item.get('fk_attribute_name');
                const sourceName = item.get('sourceProcess')
                    ? CMDBuildUI.util.helper.ModelHelper.getProcessDescription(sourceType)
                    : CMDBuildUI.util.helper.ModelHelper.getClassDescription(sourceType);
                if (cascadeAction === 'restrict') {
                    allPromises.push(me.getCardsByForeignKey(sourceType, fk_attribute_name, cardsId));
                    promiseTypes.push({
                        type: 'fk',
                        sourceType: sourceType,
                        sourceName: sourceName
                    });
                }
            });

            if (Ext.isArray(filter)) {
                Ext.Array.forEach(filter, function (item) {
                    allPromises.push(me.getRelations(type, item));
                    promiseTypes.push({ type: 'relation' });
                });
            } else {
                const filterCopy = Ext.isObject(filter) ? Ext.JSON.encode(filter) : filter;
                allPromises.push(me.getRelations(type, filterCopy));
                promiseTypes.push({ type: 'relation' });
            }

            Ext.Promise.all(allPromises).then(function (responses, eOpts) {
                var message,
                    messages = [],
                    removecards = {},
                    removerelations = {};

                Ext.Array.forEach(responses, function (response, index, allitems) {
                    const promiseType = promiseTypes[index];
                    const responseData = Ext.JSON.decode(response.responseText);

                    if (promiseType.type === 'fk' && responseData.success) {
                        var cards = responseData.data;
                        const cardLength = cards.length;
                        if (cardLength > 0) {
                            if (!blockremove[promiseType.sourceType]) {
                                blockremove[promiseType.sourceType] = {
                                    description: promiseType.sourceName,
                                    count: 0
                                };
                            }
                            blockremove[promiseType.sourceType].count += cardLength;
                        }
                    } else if (promiseType.type === 'relation' && responseData.success) {
                        // Handle relations response
                        var res = Ext.JSON.decode(response.responseText);
                        if (res.success) {
                            res.data.forEach(function (d) {
                                var domain = CMDBuildUI.util.helper.ModelHelper.getDomainFromName(d.domain);
                                if (domain) {
                                    var destType, destName, askconfirm;
                                    if (d.direction === 'direct') {
                                        destType = domain.get('destination');
                                        destName = domain.get('destinationProcess')
                                            ? CMDBuildUI.util.helper.ModelHelper.getProcessDescription(destType)
                                            : CMDBuildUI.util.helper.ModelHelper.getClassDescription(destType);
                                        askconfirm = domain.get('cascadeActionDirect_askConfirm');
                                    } else {
                                        destType = domain.get('source');
                                        destName = domain.get('sourceProcess')
                                            ? CMDBuildUI.util.helper.ModelHelper.getProcessDescription(destType)
                                            : CMDBuildUI.util.helper.ModelHelper.getClassDescription(destType);
                                        askconfirm = domain.get('cascadeActionInverse_askConfirm');
                                    }
                                    switch (d.cascadeAction) {
                                        case CMDBuildUI.model.domains.Domain.cascadeAction.restrict:
                                            if (!blockremove[destType]) {
                                                blockremove[destType] = {
                                                    description: destName,
                                                    count: 0
                                                };
                                            }
                                            blockremove[destType].count += d.count;
                                            break;
                                        case CMDBuildUI.model.domains.Domain.cascadeAction.deletecard:
                                            if (askconfirm) {
                                                if (!removecards[destType]) {
                                                    removecards[destType] = {
                                                        description: destName,
                                                        count: 0
                                                    };
                                                }
                                                removecards[destType].count += d.count;
                                            }
                                            break;
                                        case CMDBuildUI.model.domains.Domain.cascadeAction.setnull:
                                            if (askconfirm) {
                                                if (!removerelations[destType]) {
                                                    removerelations[destType] = {
                                                        description: destName,
                                                        count: 0
                                                    };
                                                }
                                                removerelations[destType].count += d.count;
                                            }
                                            break;
                                    }
                                }
                            });
                        }
                    }
                });

                /**
                 * Used to reject the deferred and end saving form status
                 */
                function rejectDeferred() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.reject();
                }

                // if there are relations with cascade restrict
                // show alert message and reject promise
                if (!Ext.Object.isEmpty(blockremove)) {
                    CMDBuildUI.locales.Locales.classes.cards.deleteblocked;
                    CMDBuildUI.util.Msg.alert(
                        CMDBuildUI.locales.Locales.notifier.attention,
                        Ext.String.format(
                            CMDBuildUI.locales.Locales.classes.cards.deleteblocked,
                            descsFromObject(blockremove)
                        ),
                        function () {
                            rejectDeferred();
                        }
                    );
                } else {
                    // prepare message
                    if (!Ext.Object.isEmpty(removecards)) {
                        messages.push(
                            '<li>' +
                                Ext.String.format(
                                    CMDBuildUI.locales.Locales.classes.cards.deleterelatedcards,
                                    descsFromObject(removecards)
                                ) +
                                '</li>'
                        );
                    }
                    if (!Ext.Object.isEmpty(removerelations)) {
                        messages.push(
                            '<li>' +
                                Ext.String.format(
                                    CMDBuildUI.locales.Locales.classes.cards.deleterelations,
                                    descsFromObject(removerelations)
                                ) +
                                '</li>'
                        );
                    }
                    if (Ext.isEmpty(messages)) {
                        message = basemsg;
                    } else {
                        message = Ext.String.format(
                            '{0} <br /> {1} <ul style="margin: 0;"> {2} </ul>',
                            basemsg,
                            CMDBuildUI.locales.Locales.classes.cards.deletebeaware,
                            messages.join(' ')
                        );
                    }

                    // show confirm
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.notifier.attention,
                        message,
                        function (btnText) {
                            if (btnText === 'yes') {
                                deferred.resolve();
                            } else {
                                rejectDeferred();
                            }
                        }
                    );
                }
            });
        });

        /**
         *
         * @param {Object} obj
         */
        function descsFromObject(obj) {
            var values = [];
            Ext.Object.eachValue(obj, function (v) {
                values.push(Ext.String.format('<strong>{0} ({1})</strong>', v.description, v.count));
            });
            return values.join(', ');
        }

        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId
     * @param {String} action
     */
    doOpenCard: function (typeName, objectId, action) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Utilities.redirectTo(
            CMDBuildUI.view.classes.cards.Util.getBasePath(typeName, objectId, action),
            true
        );
        deferred.resolve();
        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId
     */
    doEditCard: function (typeName, objectId) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('class.card.edit');
        CMDBuildUI.util.Utilities.redirectTo(
            CMDBuildUI.view.classes.cards.Util.getBasePath(
                typeName,
                objectId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.edit
            ),
            true
        );
        deferred.resolve();
        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String} objectTypeName
     * @param {CMDBuildUI.model.classes.Card} record
     */
    doDeleteCard: function (objectType, objectTypeName, record) {
        var deferred = new Ext.Deferred();

        CMDBuildUI.view.classes.cards.Util.deleteCard(record).then(function (record) {
            // fire global card deleted event
            Ext.GlobalEvents.fireEventArgs('carddeleted', [record]);

            // execute after delete form triggers
            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
            if (item) {
                // get form triggers
                var triggers = item.getFormTriggersForAction(
                    CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterDelete
                );
                if (triggers && triggers.length) {
                    var api = Ext.apply(
                        {
                            record: record
                        },
                        CMDBuildUI.util.api.Client.getApiForFormAfterDelete()
                    );

                    CMDBuildUI.util.helper.FormHelper.executeFormTriggers(triggers, api);
                }
            }
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId
     */
    doCloneCard: function (typeName, objectId) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('class.card.clone');
        CMDBuildUI.util.Utilities.redirectTo(
            CMDBuildUI.view.classes.cards.Util.getBasePath(
                typeName,
                objectId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.clone
            ),
            true
        );
        deferred.resolve();
        return deferred.promise;
    },

    /**
     *
     * @param {String} typeName
     * @param {String|Number} objectId
     */
    doCloneCardWithRelation: function (typeName, objectId) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('class.card.clonewithrelations');
        CMDBuildUI.util.Utilities.redirectTo(
            CMDBuildUI.view.classes.cards.Util.getBasePath(
                typeName,
                objectId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations
            ),
            true
        );
        deferred.resolve();
        return deferred.promise;
    },

    /**
     * Get resource base path for routing.
     * @param {String} typeName
     * @param {String} objectId
     * @param {String} action
     * @return {String}
     */
    getBasePath: function (typeName, objectId, action) {
        var url = CMDBuildUI.util.Navigation.getClassBaseUrl(typeName, objectId, action);
        return url;
    },

    privates: {
        /**
         * Get cards filtered by foreign key
         * @param {String} className - The name of the class to query
         * @param {String} foreignKeyAttr - The name of the foreign key attribute
         * @param {String[]|Number[]} foreignKeyIds - The value to filter by
         * @returns {Ext.promise.Promise}
         */
        getCardsByForeignKey: function (className, foreignKeyAttr, foreignKeyIds) {
            var deferred = new Ext.Deferred();

            // Filter object
            const fkFilter = {
                attribute: {
                    simple: {
                        attribute: foreignKeyAttr,
                        operator: 'in',
                        value: foreignKeyIds
                    }
                }
            };

            // URL request
            const urlReq = Ext.String.format('{0}/classes/{1}/cards', CMDBuildUI.util.Config.baseUrl, className);
            Ext.Ajax.request({
                url: urlReq,
                method: 'GET',
                params: Ext.apply({
                    filter: Ext.JSON.encode(fkFilter)
                }),
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve(response);
                    } else {
                        deferred.reject(response);
                    }
                }
            });

            return deferred.promise;
        },

        /**
         * Get relations for class
         * @param {String} type
         * @param {String} filter
         * @returns {Ext.promise.Promise}
         */
        getRelations: function (type, filter) {
            var deferred = new Ext.Deferred();

            Ext.Ajax.request({
                url: Ext.String.format('{0}/classes/{1}/relations', CMDBuildUI.util.Config.baseUrl, type),
                method: 'GET',
                params: {
                    filter: filter
                },
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve(response);
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        }
    }
});
