Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.builder.Column', {
    extend: 'Ext.view.View',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.card.builder.ColumnController',
        'CMDBuildUI.view.administration.content.dashboards.common.card.builder.ColumnModel'
    ],
    alias: 'widget.administration-content-dashboards-card-builder-column',
    controller: 'administration-content-dashboards-card-builder-column',
    viewModel: {
        type: 'administration-content-dashboards-card-builder-column'
    },
    config: {
        colIndex: 0,
        rowIndex: 0,
        isAllFreeItems: false
    },
    scrollable: false,
    cls: 'cmdbuild-fieldsmanagements-column x-dashboard-builder-column',
    viewConfig: {
        navigationModel: {}
    },

    prepareData: function (data, index, record) {
        if (this.getIsAllFreeItems()) {
            return data;
        }
        try {
            var components = this.lookupViewModel().get('theDashboard.components').findRecord('_id', data);
            components.set('descriptionWithName', components.get('description'));
            return components.getData();
        } catch (error) {
            CMDBuildUI.util.Logger.log('something wrong in components list', CMDBuildUI.util.Logger.levels.debug);
        }
    },
    itemSelector: 'div.attribute-source',
    overItemCls: 'attribute-over',
    selectedItemClass: 'attribute-selected',
    singleSelect: true,
    layout: 'fit',
    initComponent: function () {
        this.tpl = new Ext.XTemplate(
            `
                <tpl for=".">
                    <tpl if="!this.isAllFreeItems(values)">
                        <div id="{[this.getToolId(values._id, "container")]}"
                            class="attribute-source x-unselectable {[this.isComponentDisabled(values) ? "is-disabled" : ""]}"
                            data-testid="administration-content-dashboards-card-builder-components-column">
                            <table id="{[this.getToolId(values._id, "table")]}" style="width:100%; table-layout: fixed;">
                                <tbody>
                                    <tr>
                                        <td class="attribute-name" style="max-width: 0;">
                                            <div class="draggable" style="position:relative; display:block; padding-right: 25px;">
                                                <div style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                                    <span style="margin-left: 5px;">{[this.getIcon(values)]} {[this.getComponentType(values)]} - {[this.getDescription(values)]}</span>
                                                </div>

                                                <span class="draggable-tool" style="position:absolute; right: 5px; top: 5px">
                                                    <tpl if="this.isEditMode()">
                                                        <div id="{[this.getToolId(values._id, "menu")]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip("menu")]}">
                                                            <div data-event="showMenu" data-ref="toolEl" class="x-tool-tool-el {[CMDBuildUI.util.helper.IconHelper.getIconId("ellipsis-v", "solid")]}"></div>
                                                        </div>
                                                    <tpl else>
                                                        <div id="{[this.getToolId(values._id, "open")]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip("open")]}">
                                                            <div data-event="open" data-ref="toolEl" class="x-tool-tool-el {[CMDBuildUI.util.helper.IconHelper.getIconId("external-link-alt", "solid")]}"></div>
                                                        </div>
                                                    </tpl>
                                                </span>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    <tpl else>
                        <div class="attribute-source x-unselectable" data-testid="administration-content-dashboards-card-builder-components-column">
                            <table style="display:flex; flex:1; overflow: hidden; text-overflow:ellipsis">
                                <tbody>
                                    <tr>
                                        <td class="attribute-name" style="flex-direction: row; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                                            <span style="float: left; flex-direction: row; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                                                {[this.getIcon(values)]} {[this.getDescription(values)]}
                                            </span>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </tpl>
                </tpl>
            `,
            {
                scope: this,
                isComponentDisabled: function (value) {
                    return !value.active;
                },
                isEditMode: function () {
                    var vm = this.scope.lookupViewModel();
                    return !vm.get('actions.view');
                },
                getQtip: function (item) {
                    switch (item) {
                        case 'open':
                            return CMDBuildUI.locales.Locales.administration.common.actions.open;
                        case 'menu':
                            return CMDBuildUI.locales.Locales.administration.common.actions.actions || '';
                        default:
                            break;
                    }
                },
                getToolId: function (recordId, toolType) {
                    return Ext.String.format('{0}_{1}_tool', this.scope.id, toolType);
                },
                isAllFreeItems: function (value) {
                    return this.scope.getIsAllFreeItems();
                },
                getComponentType: function (value) {
                    var componentDefinition = CMDBuildUI.model.dashboards.Component.getComponentTypes(value.type);
                    return componentDefinition.label;
                },
                getDescription: function (value) {
                    if (typeof value.descriptionWithName !== 'undefined') {
                        return value.descriptionWithName || '&nbsp;';
                    }
                    return value;
                },
                getIcon: function (value) {
                    var componentDefinition = CMDBuildUI.model.dashboards.Component.getComponentTypes(
                        value.type || value.value
                    );
                    return Ext.String.format('<span class="{0}"></span>', componentDefinition.iconCls);
                },
                getComponent: function (value) {
                    return value._id;
                }
            }
        );
        this.callParent(arguments);
    },
    listeners: {
        itemclick: function (view, record, item, index, e, eOpts) {
            try {
                var clickedEl = Ext.get(e.target);
                this.fireEventArgs(Ext.String.format('item{0}click', clickedEl.dom.dataset.event), [record, e, eOpts]);
                e.stopPropagation();
                return false;
            } catch (event) {
                e.stopPropagation();
                return false;
            }
        },
        itemdblclick: function (view, record, item, index, e, eOpts) {
            if (view.getIsAllFreeItems()) {
                return false;
            }
            if (view.lookupViewModel().get('actions.view')) {
                this.fireEventArgs('itemopenclick', [record, e, eOpts, true]);
            } else {
                this.fireEventArgs('itemeditclick', [record, e, eOpts]);
            }
            return false;
        },

        /*
         * Here is where we "activate" the DataView.
         * We have decided that each node with the class "attribute-source" encapsulates a single draggable
         * object.
         *
         * So we inject code into the DragZone which, when passed a mousedown event, interrogates
         * the event to see if it was within an element with the class "attribute-source". If so, we
         * return non-null drag data.
         *
         * Returning non-null drag data indicates that the mousedown event has begun a dragging process.
         * The data must contain a property called "ddel" which is a DOM element which provides an image
         * of the data being dragged. The actual node clicked on is not dragged, a proxy element is dragged.
         * We can insert any other data into the data object, and this will be used by a cooperating DropZone
         * to perform the drop operation.
         */

        render: function (view) {
            var me = this;
            view.dragZone = Ext.create('Ext.dd.DragZone', view.getEl(), {
                onBeforeDrag: function (data, e) {
                    if (e.target.dataset.event) {
                        return false;
                    }
                },

                //      On receipt of a mousedown event, see if it is within a draggable element.
                //      Return a drag data object if so. The data object can contain arbitrary application
                //      data, but it should also contain a DOM element in the ddel property to provide
                //      a proxy to drag.
                getDragData: function (e) {
                    var sourceEl = e.getTarget(view.itemSelector, 10),
                        d;
                    if (sourceEl && !view.lookupViewModel().get('actions.view')) {
                        d = sourceEl.cloneNode(true);
                        d.id = Ext.id();
                        d.style.width = sourceEl.offsetWidth + 'px';

                        return (view.dragData = {
                            sourceEl: sourceEl,
                            sourceView: view,
                            repairXY: Ext.fly(sourceEl).getXY(),
                            ddel: d,
                            componentData: view.getRecord(sourceEl).data
                        });
                    }
                },

                //      Provide coordinates for the proxy to slide back to on failed drag.
                //      This is the original XY coordinates of the draggable element.
                getRepairXY: function () {
                    return this.dragData.repairXY;
                }
            });

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            function getDataFromTarget(target) {
                var fieldset = view.up('#entityCard');
                var vm = fieldset.lookupViewModel();
                var gridStore = vm.get('rows').getRange();
                if (gridStore) {
                    return gridStore;
                }
                return false;
            }

            function getAllComponentsStore() {
                var fieldset = view.up('#entityCard');
                var vm = fieldset.lookupViewModel();
                var allComponentsStore = vm.get('componentsStore');
                return allComponentsStore;
            }

            function allowComponent(name) {
                var componentsStore = getAllComponentsStore();
                var record = componentsStore.findRecord('value', name);
                if (!record) {
                    record = view.lookupViewModel().get('theDashboard.components').findRecord('_id', name);
                }
                return record;
            }
            view.dropZone = Ext.create('Ext.dd.DropZone', view.el, {
                // If the mouse is over a target node, return that node. This is
                // provided as the "target" parameter in all "onNodeXXXX" node event handling functions
                getTargetFromEvent: function (e) {
                    return e.getTarget('.cmdbuild-fieldsmanagements-column');
                },

                // // On entry into a target node, highlight that node.
                onNodeEnter: function (target, dd, e, data) {
                    Ext.fly(target).addCls('column-target-hover');
                },

                // On exit from a target node, unhighlight that node.
                onNodeOut: function (target, dd, e, data) {
                    Ext.fly(target).removeCls('column-target-hover');
                },

                // While over a target node, return the default drop allowed class which
                // places a "tick" icon into the drag proxy.
                onNodeOver: function (target, dd, e, data) {
                    var name = data.componentData.value,
                        proto = Ext.dd.DropZone.prototype,
                        targetView = Ext.getCmp(target.id);
                    if (data.sourceView.isAllFreeItems && targetView.isAllFreeItems) {
                        return proto.dropNotAllowed;
                    }
                    return typeof data.componentData === 'string' || allowComponent(name)
                        ? proto.dropAllowed
                        : proto.dropNotAllowed;
                },

                // On node drop, we can interrogate the target node to find the underlying
                // application object that is the real target of the dragged data.
                // In this case, it is a Record in the GridPanel's Store.
                // We can use the data set up by the DragZone's getDragData method to read
                // any data we decided to attach.
                onNodeDrop: function (target, dd, e, data) {
                    var name = data.componentData.value || data.componentData;

                    var component = allowComponent(name);
                    if (component) {
                        var sourceStore = data.sourceView.getStore();
                        var targetView = Ext.getCmp(target.id);
                        var groupData = getDataFromTarget();
                        if (data.sourceView.getIsAllFreeItems() && targetView.getIsAllFreeItems()) {
                            return false;
                        }

                        var sourceIndex;
                        if (!data.sourceView.getIsAllFreeItems()) {
                            sourceIndex = sourceStore.findExact('component', data.componentData);
                        } else {
                            sourceIndex = sourceStore.findExact('value', component.get('value'));
                        }
                        /**
                         * logic for sort
                         */
                        var orderIndex;
                        if (!targetView.isAllFreeItems) {
                            var releaseY = e.clientY - view.getY();
                            orderIndex = view.getNodes().length;
                            try {
                                Ext.Array.forEach(view.getNodes(), function (node, index) {
                                    if (releaseY < node.offsetTop + node.clientHeight / 2) {
                                        throw index;
                                    } else if (
                                        releaseY > node.offsetTop + node.clientHeight / 2 &&
                                        releaseY < node.offsetTop + node.clientHeight
                                    ) {
                                        throw index + 1;
                                    }
                                });
                            } catch (index) {
                                if (data.sourceView === targetView && sourceIndex < index) {
                                    orderIndex = index - 1;
                                } else {
                                    orderIndex = index;
                                }
                            }
                        } else {
                            orderIndex = component.get('index');
                        }

                        /*****/
                        if (!data.sourceView.getIsAllFreeItems()) {
                            // remove component from column store
                            sourceStore.removeAt(sourceIndex);
                        }

                        var sRowIndex = data.sourceView.getRowIndex();
                        var sColIndex = data.sourceView.getColIndex();
                        var sRows = groupData;
                        var sColumns = sRows[sRowIndex].columns || sRows[sRowIndex].get('columns');
                        // component.set('descriptionWithName', component.getDescriptionWithName());

                        var sComponents = sColumns[sColIndex].components;

                        if (component.get('_id')) {
                            var sComponent = Ext.Array.findBy(sComponents, function (_component) {
                                return (
                                    _component === component.get('_id') ||
                                    _component.component === component.get('name')
                                );
                            });
                            // remove component from source
                            Ext.Array.remove(sComponents, sComponent);
                        }
                        if (data.sourceView.getIsAllFreeItems()) {
                            // is new component

                            // generate new id

                            var newComponent = CMDBuildUI.model.dashboards.Component.create({
                                _id: CMDBuildUI.util.Utilities.generateUUID(),
                                type: component.get('value'),
                                dataSourceType:
                                    component.get('value') === 'text'
                                        ? CMDBuildUI.model.dashboards.Component.dataSourceTypes.text
                                        : component.get('value') === 'list'
                                          ? ''
                                          : CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion
                            });
                            // source store index

                            var columns =
                                groupData[view.getRowIndex()].columns || groupData[view.getRowIndex()].get('columns');
                            columns[view.getColIndex()].components.push(newComponent.get('_id'));
                            view.up('#entityCard').getViewModel().get('theDashboard').components().add(newComponent);

                            me.fireEventArgs('itemcreated', [newComponent]);
                            view.lookupViewModel().get('rows').setData(sRows);
                            return true;
                        } else {
                            // is column of form
                            if (component) {
                                var targetComponent = Ext.getCmp(target.id);
                                targetComponent.getStore().insert(orderIndex, component.get('id'));
                                // find the column where to push the component
                                var tRowIndex = targetView.getRowIndex();
                                var tColIndex = targetView.getColIndex();
                                var tColumns = sRows[tRowIndex].columns || sRows[tRowIndex].get('columns');
                                tColumns[tColIndex].components = Ext.Array.insert(
                                    tColumns[tColIndex].components,
                                    orderIndex,
                                    [component.get('_id')]
                                );
                                Ext.suspendLayouts();
                                view.lookupViewModel().get('rows').setData(sRows);
                                Ext.resumeLayouts();
                            }

                            CMDBuildUI.util.Logger.log(
                                'Dropped component ' + name + ' on column ',
                                CMDBuildUI.util.Logger.levels.debug,
                                'Drop gesture'
                            );

                            return true;
                        }
                    }
                }
            });
        }
    }
});
