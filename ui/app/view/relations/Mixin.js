Ext.define('CMDBuildUI.view.relations.Mixin', {
    mixinId: 'relations-mixin',
    /**
     * Redirects to a detail view of a record.
     *
     * @param {CMDBuildUI.model.base.Base} record - Record containing the
     *        destination type and id.
     * @param {string} [action] - Optional action to append to the URL.
     */
    openRelatedItem: function (record, action) {
        var path;
        if (record.isProcessInstance) {
            path = CMDBuildUI.util.Navigation.getProcessBaseUrl(record.get('_type'), record.get('_id').split('_')[0]);
        } else if (record.isCard) {
            path = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get('_type'), record.get('_id').split('_')[0]);
        }
        if (action) {
            path += '/' + action;
        }
        if (path) {
            this.redirectTo(path);
        }
    },

    /**
     * Retrieves a relation item by its destination identifier.
     *
     * @param {string} destination - Destination id (may contain a type
     *        prefix separated by an underscore).
     * @returns {Ext.data.Model|undefined} The relation model or `undefined`
     *          if not found.
     */
    findRelationItem: function (destination) {
        const vm = this.getViewModel();
        const domainname = vm.get('domain._id');
        const rels = vm.get('allRelations');
        const destinationParts = destination.split('_');
        const relPos = rels.findBy(function (relItem) {
            if (destinationParts.length == 1) {
                return relItem.get('_type') === domainname && relItem.get('_destinationId') == destinationParts[0];
            } else {
                return relItem.getId() == destinationParts[1];
            }
        });
        if (relPos !== -1) {
            return rels.getAt(relPos);
        }
    },

    /**
     * Chooses the record to be used in a context menu.
     *
     * @param {Ext.data.Model} record - The original grid record.
     * @param {Ext.data.Model} [relationitem] - Record representing the
     *        relation, if available.
     * @returns {Ext.data.Model} The relation record or the original record.
     */
    getMenuRelationItem: function (record, relationitem) {
        return relationitem || record;
    },

    /**
     * Checks whether opening the related card should be disabled.
     *
     * @param {Ext.data.Model} record - Record to evaluate.
     * @returns {boolean} `true` if opening is disabled.
     */
    isOpenCardDisabled: function (record) {
        const destinationType = record.get('_destinationType') || record.get('_type');
        if (!destinationType) {
            return !(record.isCard || record.isProcessInstance);
        }

        return !CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType);
    },

    /**
     * Determines if editing a relation is permitted.
     *
     * @param {Ext.grid.Panel} grid - The grid containing the action.
     * @param {Ext.data.Model} record - The record involved.
     * @param {Ext.data.Model} [relationitem] - Optionally, the relation
     *        record.
     * @returns {boolean} `true` if editing is disabled.
     */
    isEditRelationDisabled: function (grid, record, relationitem) {
        const vm = grid.lookupViewModel();
        const item = relationitem || record;
        const recordType = item.get('_type');
        const referenceDomains = vm.get('referenceDomains') || [];
        const editableDomains = vm.get('editableDomains') || {};

        return (
            !item.get('_can_update') ||
            record.get('_destinationIsProcess') ||
            record.isProcessInstance ||
            Ext.Array.contains(referenceDomains, recordType) ||
            !editableDomains[recordType]
        );
    },

    /**
     * Determines whether the delete relation action should be disabled for a given
     * record. The method checks permissions and domain configuration to decide
     * if the delete operation is allowed.
     *
     * @param {Ext.grid.Panel} grid - The grid instance that contains the
     *        action column. Used to obtain the ViewModel.
     * @param {Ext.data.Model} record - The record for which the delete action
     *        is being evaluated.
     * @param {Ext.data.Model} [relationitem] - Optional relation item record
     *        that may be different from the grid record.
     * @returns {boolean} `true` if the delete action should be disabled;
     *          otherwise `false`.
     */
    isDeleteRelationDisabled: function (grid, record, relationitem) {
        const vm = grid.lookupViewModel();
        const item = relationitem || record;
        const recordType = item.get('_type');
        const referenceDomains = vm.get('referenceDomains') || [];
        const editableDomains = vm.get('editableDomains') || {};

        return (
            !item.get('_can_delete') ||
            record.get('_destinationIsProcess') ||
            record.isProcessInstance ||
            Ext.Array.contains(referenceDomains, recordType) ||
            !editableDomains[recordType]
        );
    },

    /**
     * Determines if editing a card is permitted.
     *
     * @param {Ext.data.Model} record - The record to edit.
     * @returns {boolean} `true` if editing is disabled.
     */
    isEditCardDisabled: function (record) {
        const destinationType = record.get('_destinationType') || record.get('_type');
        const titem = destinationType && CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType);

        if (!titem) {
            return true;
        }

        return (
            !titem.get(CMDBuildUI.model.base.Base.permissions.edit) ||
            record.get('_destinationIsProcess') ||
            record.isProcessInstance
        );
    },

    /**
     * Creates the action column configuration for the relations grid.
     *
     * @returns {Ext.grid.column.Column[]} Array containing the action column
     *          configuration.
     */
    getActionColumns: function () {
        const me = this;
        return [
            {
                xtype: 'actioncolumn',
                minWidth: 25,
                maxWidth: 25,
                hideable: false,
                menuDisabled: true,
                items: [
                    {
                        itemId: 'menuAction',
                        iconCls:
                            'relations-grid-action ' +
                            CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-v', 'solid'),
                        tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            const anchorTarget =
                                e && e.getTarget ? e.getTarget('.x-action-col-icon') || e.getTarget() : null;
                            me.showActionMenu(anchorTarget, grid, record);
                        },
                        autoEl: {
                            'data-testid': 'relations-fieldset-viewcardaction'
                        }
                    }
                ]
            }
        ];
    },

    /**
     * Builds the menu items for the action column context menu.
     *
     * @param {Ext.grid.Panel} grid - The grid containing the record.
     * @param {Ext.data.Model} record - The record for which the menu is
     *        created.
     * @returns {Ext.menu.Item[]} Array of menu item configs.
     */
    getActionMenuItems: function (grid, record) {
        const me = this;
        const vm = grid.lookupViewModel();
        const relationitem = me.findRelationItem(record.getId());
        const menuRelationItem = me.getMenuRelationItem(record, relationitem);
        const items = [];

        if (!vm.get('readonly')) {
            items.push({
                text: CMDBuildUI.locales.Locales.relations.opencard,
                iconCls:
                    'relations-grid-action ' +
                    CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                disabled: me.isOpenCardDisabled(record),
                handler: function () {
                    me.openRelationItem(grid, record, false);
                }
            });

            items.push({
                text: CMDBuildUI.locales.Locales.relations.editrelation,
                iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                disabled: me.isEditRelationDisabled(grid, record, menuRelationItem),
                handler: function () {
                    me.editRelationItem(grid, menuRelationItem);
                }
            });

            items.push({
                text: CMDBuildUI.locales.Locales.relations.deleterelation,
                iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                disabled: me.isDeleteRelationDisabled(grid, record, menuRelationItem),
                handler: function () {
                    me.deleteRelationItem(grid, menuRelationItem);
                }
            });
        } else {
            items.push({
                text: CMDBuildUI.locales.Locales.relations.opencard,
                iconCls:
                    'relations-grid-action ' +
                    CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                disabled: me.isOpenCardDisabled(record),
                handler: function () {
                    me.openRelationItem(grid, record, vm.get('readonly'));
                }
            });
        }

        if (vm.get('readonly') && vm.get('readOnlyAllowCardEdit')) {
            items.push({
                text: CMDBuildUI.locales.Locales.relations.editcard,
                iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
                disabled: me.isEditCardDisabled(record),
                handler: function () {
                    me.editRelatedCard(grid, record);
                }
            });
        } else if (!vm.get('readonly')) {
            items.push({
                text: CMDBuildUI.locales.Locales.relations.editcard,
                iconCls: 'relations-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
                disabled: me.isEditCardDisabled(record),
                handler: function () {
                    me.editRelatedCard(grid, record);
                }
            });
        }

        return items;
    },

    /**
     * Opens a relation item either as a list or detail view.
     *
     * @param {Ext.grid.Panel} grid - Parent grid.
     * @param {Ext.data.Model} record - Record to open.
     * @param {boolean} [openInGrid] - Whether to keep the view inside the
     *        grid.
     */
    openRelationItem: function (grid, record, openInGrid) {
        if (record.get('_destinationType') && record.get('_destinationId')) {
            this.openListRelationItem(record, openInGrid);
        } else {
            this.openRelatedItem(record, openInGrid ? null : CMDBuildUI.mixins.DetailsTabPanel.actions.view);
        }
    },

    /**
     * Constructs configuration object used when editing a relation.
     *
     * @param {Ext.grid.Panel} grid - The grid containing the relation.
     * @returns {Object} Configuration for the edit dialog.
     */
    getRelationEditConfig: function (grid) {
        const vm = grid.lookupViewModel();
        const objectType = vm.get('objectType');
        const objectTypeName = vm.get('objectTypeName');
        const objectId = vm.get('objectId');
        const proxyUrl =
            vm.get('storedata.proxyurl') ||
            (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                ? CMDBuildUI.util.api.Processes.getProcessInstanceRelations(objectTypeName, objectId)
                : CMDBuildUI.util.api.Classes.getCardRelations(objectTypeName, objectId));

        return {
            theObject: vm.get('theObject'),
            proxyurl: proxyUrl,
            objectType: objectType,
            objecttypename: objectTypeName,
            objectid: objectId
        };
    },

    /**
     * Redirects to the target of a relation.
     *
     * @param {string} objectName - Name of the current object.
     * @param {string} destinationType - Type of the destination object.
     * @param {string} destinationId - Identifier of the destination.
     * @param {string} [action] - Optional action to perform.
     */
    redirectToRelationTarget: function (objectName, destinationType, destinationId, action) {
        var url = '';

        switch (CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(destinationType)) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                url = CMDBuildUI.util.Navigation.getClassBaseUrl(objectName, destinationId, action, true);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                url = CMDBuildUI.util.Navigation.getProcessBaseUrl(objectName, destinationId, action, null, true);
                break;
            default:
                return;
        }

        CMDBuildUI.util.Utilities.closeAllPopups();
        CMDBuildUI.util.Utilities.redirectTo(url);
    },

    /**
     * Opens a list view of the related item in a separate window.
     *
     * @param {Ext.data.Model} record - Relation record.
     * @param {boolean} [openInGrid] - Whether to open inside the grid.
     */
    openListRelationItem: function (record, openInGrid) {
        const me = this;
        const destinationType = record.get('_destinationType');
        const destinationId = record.get('_destinationId');
        var action = null;
        const destinationObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType);

        if (
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.view) &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.subclass)
        ) {
            action = CMDBuildUI.mixins.DetailsTabPanel.actions.view;
        } else if (!openInGrid) {
            action = CMDBuildUI.mixins.DetailsTabPanel.actions.view;
        }

        const redirectTo = function (objectName) {
            me.redirectToRelationTarget(objectName, destinationType, destinationId, action);
        };

        if (
            destinationObj.get('prototype') &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.redirectTo.subclass)
        ) {
            CMDBuildUI.util.api.Client.getRemoteCard(
                CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                destinationId
            ).then(function (card) {
                redirectTo(card.get('_type'));
            });
        } else {
            redirectTo(destinationType);
        }
    },

    /**
     * Reloads the relation grid store.
     *
     * @param {Ext.grid.Panel} grid - The grid to reload.
     * @param {function} [callback] - Callback after reload.
     */
    reloadRelationGrid: function (grid, callback) {
        if (grid && grid.getStore) {
            grid.getStore().load({
                callback: callback
            });
        } else if (callback) {
            callback();
        }
    },

    /**
     * Handles post‑edit updates to the grid and related stores.
     *
     * @param {Ext.grid.Panel} grid - Grid that triggered the edit.
     * @param {Ext.data.Model} relationItem - The edited relation record.
     */
    afterRelationEdit: function (grid, relationItem) {
        const vm = grid.lookupViewModel();

        this.reloadRelationGrid(grid, function () {
            Ext.GlobalEvents.fireEventArgs('updateMasterDetailStore', [relationItem.get('_type')]);
            if (vm.get('domain')) {
                Ext.GlobalEvents.fireEventArgs('updateRelationStore', [vm.get('domain').getId()]);
            }
            Ext.GlobalEvents.fireEvent('reloadFieldsetGrid');
        });
    },

    /**
     * Handles post‑delete updates to the grid and related stores.
     *
     * @param {Ext.grid.Panel} grid - Grid that triggered the delete.
     * @param {Ext.data.Model} relationItem - The deleted relation record.
     */
    afterRelationDelete: function (grid, relationItem) {
        this.reloadRelationGrid(grid, function () {
            Ext.GlobalEvents.fireEventArgs('updateMasterDetailStore', [relationItem.get('_type')]);
            Ext.GlobalEvents.fireEvent('updateRelationStore');
            Ext.GlobalEvents.fireEvent('reloadFieldsetGrid');
        });
    },

    /**
     * Initiates the editing of a relation via the utility dialog.
     *
     * @param {Ext.grid.Panel} grid - The grid containing the relation.
     * @param {Ext.data.Model} relationItem - The relation to edit.
     */
    editRelationItem: function (grid, relationItem) {
        const me = this;
        if (!(relationItem && relationItem.get('_can_update'))) {
            return;
        }

        CMDBuildUI.view.relations.Utils.editRelation(relationItem, this.getRelationEditConfig(grid)).then(function () {
            me.afterRelationEdit(grid, relationItem);
        });
    },

    /**
     * Deletes a relation using the utility dialog.
     *
     * @param {Ext.grid.Panel} grid - Grid containing the relation.
     * @param {Ext.data.Model} relationItem - The relation to delete.
     */
    deleteRelationItem: function (grid, relationItem) {
        const me = this;
        if (!(relationItem && relationItem.get('_can_delete'))) {
            return;
        }

        CMDBuildUI.view.relations.Utils.deleteRelation(relationItem)
            .then(function () {
                me.afterRelationDelete(grid, relationItem);
            })
            .otherwise(function () {
                if (grid && grid.getStore) {
                    grid.getStore().reload();
                }
            });
    },

    /**
     * Gathers data required for editing a card in a popup.
     *
     * @param {Ext.data.Model} record - Record whose card should be edited.
     * @returns {Object} Data object for the edit popup.
     */
    getEditCardData: function (record) {
        const rawObjectId = record.get('_destinationId') || record.get('_id') || record.getId();
        const objectId = Ext.isString(rawObjectId) ? rawObjectId.split('_')[0] : rawObjectId;

        return {
            objectTypeName: record.get('_destinationType') || record.get('_type'),
            objectId: objectId,
            title:
                record.get('_destinationDescription') ||
                record.get('_description') ||
                CMDBuildUI.locales.Locales.relations.editcard,
            masterDetailType: record.get('_type') || record.get('_destinationType')
        };
    },

    /**
     * Renders the edit card popup window.
     *
     * @param {Ext.grid.Panel} grid - The grid from which the popup opens.
     * @param {Object} editCardData - Configuration data for the popup.
     */
    openEditCardPopup: function (grid, editCardData) {
        var popup;
        const me = this;
        const config = {
            xtype: 'classes-cards-card-edit',
            padding: 10,
            hideInlineElements: false,
            viewModel: {
                data: {
                    objectTypeName: editCardData.objectTypeName,
                    objectId: editCardData.objectId
                }
            },
            buttons: [
                {
                    ui: 'secondary-action',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'relations-editcard-cancel'
                    },
                    handler: function () {
                        popup.destroy();
                    }
                },
                {
                    ui: 'management-primary',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'relations-editcard-save'
                    },
                    formBind: true,
                    handler: function (btn) {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        const cancelBtn = btn.up().down('#detailclosebtn');
                        btn.showSpinner = true;
                        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

                        popup
                            .down('classes-cards-card-edit')
                            .getController()
                            .saveForm({
                                failure: function () {
                                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                    CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                                }
                            })
                            .then(function (updatedRecord) {
                                if (grid && grid.updateRowWithExpader) {
                                    grid.updateRowWithExpader(updatedRecord);
                                }

                                if (grid && grid.getStore) {
                                    grid.getStore().load({
                                        callback: function () {
                                            me.afterEditCardSave(editCardData.masterDetailType);
                                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                            popup.destroy();
                                        }
                                    });
                                } else {
                                    me.afterEditCardSave(editCardData.masterDetailType);
                                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                    popup.destroy();
                                }
                            })
                            .otherwise(function () {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                            });
                    }
                }
            ],
            listeners: {
                itemupdated: function () {
                    popup.close();
                    if (grid && grid.getStore) {
                        grid.getStore().load();
                    }
                },
                cancelupdating: function () {
                    popup.close();
                }
            }
        };

        popup = CMDBuildUI.util.Utilities.openPopup(null, editCardData.title, config);
    },

    /**
     * Opens an edit popup for a related card.
     *
     * @param {Ext.grid.Panel} grid - Grid containing the record.
     * @param {Ext.data.Model} record - Record to edit.
     */
    editRelatedCard: function (grid, record) {
        this.openEditCardPopup(grid, this.getEditCardData(record));
    },

    /**
     * Fires after the card popup is saved.
     *
     * @param {string} [masterDetailType] - Optional master detail type.
     */
    afterEditCardSave: function (masterDetailType) {
        if (masterDetailType) {
            Ext.GlobalEvents.fireEventArgs('updateMasterDetailStore', [masterDetailType]);
        }
        Ext.GlobalEvents.fireEvent('updateRelationStore');
        Ext.GlobalEvents.fireEvent('reloadFieldsetGrid');
    },

    /**
     * Displays the action menu for a grid record.
     *
     * @param {HTMLElement|Ext.Element} anchorTarget - Element triggering the
     *        menu.
     * @param {Ext.grid.Panel} grid - Grid containing the record.
     * @param {Ext.data.Model} record - Record for which the menu is shown.
     */
    showActionMenu: function (anchorTarget, grid, record) {
        const menu = Ext.create('Ext.menu.Menu', {
            items: this.getActionMenuItems(grid, record)
        });

        const anchorEl = anchorTarget && anchorTarget.dom ? anchorTarget : Ext.get(anchorTarget);
        if (anchorEl) {
            menu.showBy(anchorEl);
        } else {
            menu.showBy(grid);
        }
    }
});
