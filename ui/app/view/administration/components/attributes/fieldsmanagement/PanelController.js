Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-attributes-fieldsmanagement-panel',
    control: {
        '#': {
            afterrender: 'onAfterRender',
            show: function (view) {
                if (this.getView().down('fieldset')) {
                    this.doFieldsets();
                }
            }
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleEnableBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleEnableBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#layoutAutogenBtn': {
            click: 'onLayoutAutogenBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.attributes.fieldsmanagement.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var me = this;
        // bind for classes
        me.classesBind();

        // bind for DMSmodel
        me.dmsModelBind();

        // bind for process
        me.processBind(view);

        // bind for view with join
        me.joinViewsBind();
    },
    onEditBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.getView().items.each(function (view) {
            if (view.xtype === 'administration-components-attributes-fieldsmanagement-fieldset') {
                view.down('administration-components-attributes-fieldsmanagement-group-group').updateGroupAndRefresh(
                    true
                );
            }
        });
    },

    onCancelBtnClick: function (button) {
        button.setDisabled();
        var vm = button.lookupViewModel();
        if (!vm.get('activity')) {
            var url;
            switch (vm.get('objectType').toLowerCase()) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(vm.get('theObject._id'));
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(vm.get('theModel._id'));
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(vm.get('theView._id'));
                    break;
                default:
                    break;
            }
            this.redirectTo(url, true);
        } else {
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
        }
    },
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true, button.up('administration-attributes-fieldsmanagement-panel'));
        var view = this.getView();
        var vm = view.lookupViewModel();

        var formStructure = vm.get('theFormStructure');
        view.items.each(function (fieldset) {
            var group = fieldset.getGroup();
            var groupStructure = {
                rows: []
            };
            Ext.Array.forEach(
                fieldset
                    .down('administration-components-attributes-fieldsmanagement-group-group')
                    .getViewModel()
                    .get('rows')
                    .getRange() || [],
                function (row) {
                    // TODO: check why added row is not a model
                    var _row = row.columns ? row : row.getData();
                    Ext.Array.forEach(_row.columns, function (column) {
                        Ext.Array.forEach(column.fields, function (field) {
                            delete field.id;
                            delete field.descriptionWithName;
                        });
                        delete column.id;
                    });
                    delete _row.id;
                    groupStructure.rows.push(_row);
                }
            );
            formStructure.form[group.get('name')] = groupStructure;
        });

        if (!vm.get('activity')) {
            switch (vm.get('objectType').toLowerCase()) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    this.saveClass(button, formStructure);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                    this.saveDMSModel(button, formStructure);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                    this.saveView(button, formStructure);
                    break;

                default:
                    break;
            }
        } else {
            this.saveActivity(button, formStructure);
        }
    },

    onToggleEnableBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var record = vm.get('activity') || vm.get('theObject') || vm.get('theModel') || vm.get('theView');
        var formStructure = record.get('formStructure');
        if (formStructure) {
            var newValue = formStructure.active === false ? true : false;
            vm.set('theFormStructure.active', newValue);
            formStructure.active = newValue;
            record.set('formStructure', formStructure);
            if (vm.get('activity')) {
                record.save();
            } else {
                CMDBuildUI.util.Utilities.showLoader(
                    true,
                    button.up('administration-attributes-fieldsmanagement-panel')
                );
                switch (vm.get('objectType').toLowerCase()) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        me.saveClass(button, formStructure);
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                        me.saveDMSModel(button, formStructure);
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                        this.saveView(button, formStructure);
                        break;
                    default:
                        break;
                }
            }
        }
    },
    onDeleteBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    var record = vm.get('activity') || vm.get('theObject') || vm.get('theModel') || vm.get('theView');
                    record.set('formStructure', null);
                    if (vm.get('activity')) {
                        record.save({
                            callback: function () {
                                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            }
                        });
                    } else {
                        CMDBuildUI.util.Utilities.showLoader(
                            true,
                            button.up('administration-attributes-fieldsmanagement-panel')
                        );
                        switch (vm.get('objectType').toLowerCase()) {
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                me.saveClass(button, null);
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                                me.saveDMSModel(button, null);
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                                me.saveView(button, null);
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        );
    },

    onLayoutAutogenBtnClick: function (button, event, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = me.getViewModel();
        var form = vm.get('theFormStructure.form');
        var groups = Ext.Object.getAllKeys(form);
        var fieldsets = [];
        CMDBuildUI.util.Utilities.showLoader(true, view);
        groups.forEach(function (fieldset, index) {
            var fieldsetItemId = Ext.String.format('#{0}_fieldset', CMDBuildUI.util.Utilities.stringToHex(fieldset));
            var fieldsetView = view.down(fieldsetItemId);
            if (fieldsetView) {
                fieldsets.push(fieldsetView);
            }
        });
        fieldsets.forEach(function (fieldsetView, index) {
            Ext.asap(function () {
                fieldsetView
                    .down('administration-components-attributes-fieldsmanagement-group-group')
                    .fireEventArgs('autogenerate', [fieldsets.length - 1 === index]);
            });
        });
    },

    privates: {
        classesBind: function () {
            var me = this,
                vm = this.getViewModel();

            vm.bind(
                {
                    bindTo: {
                        theObject: '{theObject}',
                        allAttributesComplete: '{attributesStore.complete}'
                    }
                },
                function (data) {
                    if ((data.theObject || data.theModel) && data.allAttributesComplete) {
                        switch (vm.get('objectType')) {
                            case CMDBuildUI.util.helper.ModelHelper.systemClasses.class:
                                vm.set('_can_modify', data.theObject.get('_can_modify'));
                                me.doFieldsets();
                                break;
                            default:
                                break;
                        }
                    }
                }
            );
        },

        dmsModelBind: function () {
            var me = this,
                vm = this.getViewModel();

            vm.bind(
                {
                    bindTo: {
                        theModel: '{theModel}',
                        allAttributesComplete: '{attributesStore.complete}'
                    }
                },
                function (data) {
                    if ((data.theObject || data.theModel) && data.allAttributesComplete) {
                        switch (vm.get('objectType')) {
                            case 'dmsmodel':
                                vm.set('_can_modify', data.theModel.get('_can_modify'));
                                me.doFieldsets();
                                break;
                            default:
                                break;
                        }
                    }
                }
            );
        },
        processBind: function (view) {
            var me = this,
                vm = this.getViewModel(),
                formStructure;

            if (view._rowContext && view._rowContext.record) {
                vm.set('activity', view._rowContext.record);
                vm.set('grid', view._rowContext.ownerGrid);
            }

            vm.bind(
                {
                    bindTo: {
                        activity: '{activity}'
                    }
                },
                function (data) {
                    if (data.activity) {
                        var vmGrid = vm.get('grid').getViewModel();
                        var theProcess = vmGrid.get('theProcess');
                        var attributes = data.activity.attributes();

                        if (data.activity.get('_definition') === 'DUMMY_TASK_FOR_CLOSED_PROCESS') {
                            vm.set('showClosedTaskMessage', true);
                            attributes = Ext.create('Ext.data.Store', {});
                            theProcess._attributes.each(function (attribute) {
                                if (theProcess.get('flowStatusAttr') !== attribute.get('_id')) {
                                    attributes.add({
                                        _id: attribute.get('_id'),
                                        action: false,
                                        detail: attribute.getData(),
                                        index: attribute.get('index'),
                                        mandatory: attribute.get('mandatory'),
                                        writable: attribute.get('mode')
                                    });
                                }
                            });
                        }
                        vm.set('_can_modify', vmGrid.get('theProcess._can_modify'));
                        var processGroupsStore = vmGrid.get('attributeGroupsStore');
                        var aGroupsStore = Ext.create('Ext.data.Store', {
                            model: 'CMDBuildUI.model.AttributeGrouping',
                            proxy: {
                                type: 'memory'
                            }
                        });
                        var aStore = Ext.create('Ext.data.Store', {
                            model: 'CMDBuildUI.model.Attribute',
                            proxy: {
                                type: 'memory'
                            }
                        });

                        attributes.each(function (attribute) {
                            var aDetail = attribute.get('detail');
                            if (aDetail) {
                                var aGroup = processGroupsStore.findRecord('name', aDetail.group);
                                if (aGroup && !aGroupsStore.findRecord('name', aGroup.get('name'))) {
                                    aGroupsStore.add(aGroup);
                                }
                                if (
                                    !attribute.get('action') &&
                                    attribute.get('_id') !== theProcess.get('messageAttr')
                                ) {
                                    aStore.add(aDetail);
                                }
                            } else {
                                Ext.asap(function () {
                                    CMDBuildUI.util.Notifier.showWarningMessage(
                                        Ext.String.format(
                                            CMDBuildUI.locales.Locales.administration.processes.texts
                                                .activityattributenotfountinprocess,
                                            attribute.get('_id')
                                        )
                                    );
                                });
                            }
                        });

                        vm.set('attributeGroupsStore', aGroupsStore);
                        vm.set('attributesStore', aStore);

                        if (data.activity.get('formStructure')) {
                            vm.set('theFormStructure', data.activity.get('formStructure'));
                            me.createFieldsets(view, vm.get('theFormStructure'));
                        } else {
                            formStructure = {
                                active: true,
                                form: {}
                            };
                            vm.set('theFormStructure', formStructure);
                            me.createFieldsets(view, vm.get('theFormStructure'));
                        }
                    }
                }
            );
        },

        joinViewsBind: function () {
            var me = this,
                vm = this.getViewModel();

            vm.bind(
                {
                    bindTo: {
                        theView: '{theView}'
                    }
                },
                function (data) {
                    if (data.theView) {
                        vm.set('_can_modify', true); // TODO define permission
                        vm.set('attributeGroupsStore', data.theView.attributeGroups());
                        data.theView.getAttributes().then(function (attributesStore) {
                            vm.set('attributesStore', attributesStore);
                            me.doFieldsets();
                        });
                    }
                }
            );
        },
        doFieldsets: function () {
            var me = this;
            var view = me.getView();
            view.removeAll();
            var vm = me.getViewModel();
            var formStructure =
                vm.get('theObject.formStructure') ||
                vm.get('theModel.formStructure') ||
                vm.get('theView.formStructure');
            if (formStructure) {
                vm.set('theFormStructure', formStructure);
                me.createFieldsets(view, vm.get('theFormStructure'));
            } else {
                formStructure = {
                    active: true,
                    form: {}
                };
                vm.set('theFormStructure', formStructure);
                me.createFieldsets(view, vm.get('theFormStructure'));
            }
        },
        saveActivity: function (button, formStructure) {
            CMDBuildUI.util.Logger.log('Save activity', CMDBuildUI.util.Logger.levels.debug);
            var vm = button.lookupViewModel();
            var activity = vm.get('activity');
            activity.set('formStructure', formStructure);
            activity.save({
                success: function (record, operation) {
                    CMDBuildUI.util.Logger.log('Save activity success', CMDBuildUI.util.Logger.levels.info);
                    var gridVm = vm.get('grid').getViewModel();
                    gridVm.get('activitiesStore').load();
                    gridVm.set('activitiesWithForm.isReady', false);
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                },
                callback: function () {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                },
                failure: function () {
                    CMDBuildUI.util.Logger.log('Save activity failure', CMDBuildUI.util.Logger.levels.info);
                }
            });
        },
        saveClass: function (button, formStructure) {
            var me = this;
            var vm = button.lookupViewModel();
            var theObject = vm.get('theObject');

            theObject.set('formStructure', formStructure);
            Ext.apply(theObject.data, theObject.getAssociatedData());

            // delete all id / _id in associatedData
            theObject.data.formTriggers = [];

            vm.get('formTriggersStore')
                .getRange()
                .forEach(function (record, index) {
                    theObject.data.formTriggers.push(record.getData());
                });

            theObject.data.attributeGroups.forEach(function (record, index) {
                if (theObject.data.attributeGroups[index]._id === CMDBuildUI.model.AttributeGrouping.nogroup) {
                    Ext.Array.remove(theObject.data.attributeGroups, record);
                } else {
                    delete theObject.data.attributeGroups[index].rows;
                    delete theObject.data.attributeGroups[index].attributes;
                    delete theObject.data.attributeGroups[index].id;
                }
            });

            theObject.data.contextMenuItems.forEach(function (record, index) {
                delete theObject.data.contextMenuItems[index].id;
                delete theObject.data.contextMenuItems[index]._id;
            });
            theObject.data.widgets.forEach(function (record, index) {
                delete theObject.data.widgets[index].id;
                delete theObject.data.widgets[index]._id;
            });

            theObject.save({
                success: function (record, operation) {
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    me.redirectTo(Ext.String.format('administration/classes/{0}', vm.get('theObject._id')), true);
                },
                callback: function () {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                }
            });
        },

        saveDMSModel: function (button, formStructure) {
            var me = this;
            var vm = button.lookupViewModel();
            var theModel = vm.get('theModel');

            theModel.set('formStructure', formStructure);
            Ext.apply(theModel.data, theModel.getAssociatedData());

            // delete all id / _id in associatedData
            theModel.data.formTriggers = [];

            vm.get('formTriggersStore')
                .getRange()
                .forEach(function (record, index) {
                    theModel.data.formTriggers.push(record.getData());
                });

            theModel.data.attributeGroups.forEach(function (record, index) {
                if (theModel.data.attributeGroups[index]._id === CMDBuildUI.model.AttributeGrouping.nogroup) {
                    Ext.Array.remove(theModel.data.attributeGroups, record);
                } else {
                    delete theModel.data.attributeGroups[index].rows;
                    delete theModel.data.attributeGroups[index].attributes;
                    delete theModel.data.attributeGroups[index].id;
                }
            });

            theModel.data.widgets.forEach(function (record, index) {
                delete theModel.data.widgets[index].id;
                delete theModel.data.widgets[index]._id;
            });

            theModel.save({
                success: function (record, operation) {
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    var url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(theModel.getId());
                    me.redirectTo(url, true);
                },
                callback: function () {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                }
            });
        },

        saveView: function (button, formStructure) {
            var me = this;
            var vm = button.lookupViewModel();
            var theView = vm.get('theView');

            theView.set('formStructure', formStructure);
            Ext.apply(theView.data, theView.getAssociatedData());

            theView.data.attributeGroups.forEach(function (record, index) {
                if (theView.data.attributeGroups[index]._id === CMDBuildUI.model.AttributeGrouping.nogroup) {
                    Ext.Array.remove(theView.data.attributeGroups, record);
                } else {
                    delete theView.data.attributeGroups[index].rows;
                    delete theView.data.attributeGroups[index].attributes;
                    delete theView.data.attributeGroups[index].id;
                }
            });
            theView.save({
                success: function (record, operation) {
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    var url = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(theView.getId());
                    me.redirectTo(url, true);
                },
                callback: function () {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false);
                }
            });
        },

        createFieldsets: function (view, theFormStructure) {
            var vm = view.getViewModel();
            var tabDockedItemsHeight = 34 + 40;

            var groupsStore = Ext.create('Ext.data.Store', {
                model: CMDBuildUI.model.AttributeGrouping,
                data: vm.get('attributeGroupsStore').getRange()
            });
            var noGroup = groupsStore.findRecord('name', CMDBuildUI.model.AttributeGrouping.nogroup);
            if (!noGroup) {
                noGroup = CMDBuildUI.model.AttributeGrouping.create({
                    description: CMDBuildUI.locales.Locales.common.attributes.nogroup,
                    index: groupsStore.length,
                    name: CMDBuildUI.model.AttributeGrouping.nogroup,
                    _description_translation: CMDBuildUI.locales.Locales.common.attributes.nogroup,
                    _id: CMDBuildUI.model.AttributeGrouping.nogroup
                });
                groupsStore.add(noGroup);
            }

            // get all attributes af class
            var attributesStore = vm.get('attributesStore');
            // for each group create a fieldset that contain
            // the form builder and the list of all attributes

            groupsStore.each(function (group) {
                if (!theFormStructure.form[group.get('name')]) {
                    theFormStructure.form[group.get('name')] = {
                        rows: []
                    };
                }
                // set all attributes
                var gname;
                if (group.get('name') === noGroup.get('_id')) {
                    gname = '';
                } else {
                    gname = group.get('name');
                }
                group.set(
                    'attributes',
                    attributesStore.queryBy(function (item) {
                        // only for join views
                        if (vm.get('theView')) {
                            return item.get('group') === gname && item.get('hidden') === false;
                        } else {
                            return (
                                item.get('active') &&
                                item.get('group') === gname &&
                                item.canAdminShow() &&
                                item.get('hidden') === false
                            );
                        }
                    })
                );
                if (group.get('attributes').length) {
                    // if the group is alredy defined in the builder
                    // get all rows and assign to the group
                    var columnParser = function (column, colIndex) {
                        Ext.Array.forEach(column.fields, function (field, fieldIndex) {
                            var attributes = group.get('attributes');

                            var attribute = attributes.findBy(function (item) {
                                return item.get('name') === field.attribute;
                            });
                            if (attribute) {
                                field.descriptionWithName = attribute.getDescriptionWithName();
                                attributes.remove(attribute);
                            } else {
                                Ext.Array.remove(column.fields, field);
                            }
                        });
                    };
                    var rowParser = function (row, rowIndex) {
                        Ext.Array.forEach(row.columns, columnParser);
                    };
                    for (var fieldset in theFormStructure.form) {
                        if (fieldset === group.get('name')) {
                            Ext.Array.forEach(theFormStructure.form[fieldset].rows, rowParser);
                            group.set('rows', theFormStructure.form[fieldset].rows);
                        }
                    }
                    // draw the fieldset
                    view.add({
                        xtype: 'administration-components-attributes-fieldsmanagement-fieldset',
                        group: group,
                        itemId: Ext.String.format(
                            '{0}_fieldset',
                            CMDBuildUI.util.Utilities.stringToHex(group.get('_id'))
                        ),
                        maxHeight: view.getHeight() - tabDockedItemsHeight,
                        viewModel: {
                            stores: {
                                rows: {
                                    fields: [
                                        {
                                            name: 'index'
                                        }
                                    ],
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: group.get('rows'),
                                    autoLoad: true
                                }
                            }
                        },
                        listeners: {
                            afterrender: function (view) {
                                view.items.each(function (_view) {
                                    if (
                                        _view.xtype ===
                                        'administration-components-attributes-fieldsmanagement-group-group'
                                    ) {
                                        _view.updateGroupAndRefresh(true);
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
});
