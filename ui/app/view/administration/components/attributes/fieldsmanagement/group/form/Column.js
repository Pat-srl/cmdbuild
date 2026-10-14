Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.Column', {
    extend: 'Ext.view.View',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.ColumnController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.ColumnModel'
    ],
    alias: 'widget.administration-components-attributes-fieldsmanagement-group-form-column',
    controller: 'administration-components-attributes-fieldsmanagement-group-form-column',
    viewModel: {
        type: 'administration-components-attributes-fieldsmanagement-group-form-column'
    },
    config: {
        colIndex: 0,
        rowIndex: 0,
        isAllAttributes: false
    },
    // style: 'border: dashed',
    cls: 'cmdbuild-fieldsmanagements-column',
    tpl: new Ext.XTemplate('<tpl for=".">',
        '<div class="attribute-source x-unselectable" data-testid="administration-fieldsmanagement-attribute-{name}{attribute}"><table><tbody>',
        '<tr><td class="attribute-name" >{descriptionWithName}</td></tr>',
        '</tbody></table></div>',
        '</tpl>'),
    itemSelector: 'div.attribute-source',
    overItemCls: 'attribute-over',
    selectedItemClass: 'attribute-selected',
    singleSelect: true,
    layout: 'fit',
    listeners: {
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

            view.dragZone = Ext.create('Ext.dd.DragZone', view.getEl(), {
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

                        return (view.dragData = {
                            sourceEl: sourceEl,
                            sourceView: view,
                            repairXY: Ext.fly(sourceEl).getXY(),
                            ddel: d,
                            attributeData: view.getRecord(sourceEl).data
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
            function getGroupFromTarget(target) {
                var fieldset = view.up('administration-components-attributes-fieldsmanagement-fieldset');
                var group = fieldset.down('administration-components-attributes-fieldsmanagement-group-group');
                if (group) {
                    return group;
                }
                return false;
            }

            function getAllAttributeStore(group) {
                var fieldset = group.up('fieldset');
                var vm = fieldset.lookupViewModel();
                var allGroupAttributesStore = vm.get('attributesStore');
                return allGroupAttributesStore;
            }

            function allowAttribute(group, name) {
                if (!group) {
                    return false;
                }
                var attributesStore = getAllAttributeStore(group);
                var record = attributesStore.findRecord('name', name, 0, false, true);
                var groupId = group.getGroup().get('_id');
                var attributeGroup = record.get('group');
                if (attributeGroup === groupId ||
                    (attributeGroup === '' && groupId === CMDBuildUI.model.AttributeGrouping.nogroup)
                ) {
                    return record;
                }
                return false;
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

                    var group = getGroupFromTarget(target),
                        name = data.attributeData.name || data.attributeData.attribute,
                        proto = Ext.dd.DropZone.prototype,
                        targetView = Ext.getCmp(target.id);
                    if (data.sourceView.isAllAttributes && targetView.isAllAttributes) {
                        return proto.dropNotAllowed;
                    }
                    return allowAttribute(group, name) ? proto.dropAllowed : proto.dropNotAllowed;
                },

                // On node drop, we can interrogate the target node to find the underlying
                // application object that is the real target of the dragged data.
                // In this case, it is a Record in the GridPanel's Store.
                // We can use the data set up by the DragZone's getDragData method to read
                // any data we decided to attach.
                onNodeDrop: function (target, dd, e, data) {
                    var group = getGroupFromTarget(target),
                        name = data.attributeData.name || data.attributeData.attribute;
                    var attribute = allowAttribute(group, name);
                    if (attribute) {

                        var sourceStore = data.sourceView.getStore();
                        var targetView = Ext.getCmp(target.id);
                        var groupData = group.down('grid').getStore();
                        if (data.sourceView.getIsAllAttributes() && targetView.getIsAllAttributes()) {
                            return false;
                        }
                        var sourceIndex = sourceStore.findExact('attribute', attribute.get('name'));
                        if (sourceIndex < 0) {
                            sourceIndex = sourceStore.findExact('name', attribute.get('name'));
                        }
                        /**
                         * logic for sort
                         */
                        var orderIndex;
                        if (!targetView.isAllAttributes) {
                            var releaseY = e.clientY - view.getY();
                            orderIndex = view.getNodes().length;
                            try {
                                Ext.Array.forEach(view.getNodes(), function (node, index) {
                                    if (releaseY < (node.offsetTop + (node.clientHeight / 2))) {
                                        throw index;
                                    } else if (
                                        releaseY > (node.offsetTop + (node.clientHeight / 2)) &&
                                        releaseY < (node.offsetTop + (node.clientHeight))
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
                            orderIndex = attribute.get('index');
                        }

                        /*****/
                        // remove attribute from column store
                        sourceStore.removeAt(sourceIndex);

                        var sRowIndex = data.sourceView.getRowIndex();
                        var sColIndex = data.sourceView.getColIndex();

                        var sRows = groupData.getRange();
                        var sColumns = sRows[sRowIndex].columns || sRows[sRowIndex].get('columns');
                        attribute.set('descriptionWithName', attribute.getDescriptionWithName());

                        var sFields = sColumns[sColIndex].fields;
                        var sAttribute = Ext.Array.findBy(sFields, function (field) {
                            return field.attribute === attribute.get('name');
                        });
                        // remove attribute from source
                        Ext.Array.remove(sFields, sAttribute);
                        if (targetView.getIsAllAttributes()) {
                            // source store index

                            // is all attributes container
                            // add attribute to free attributes store
                            var alredyExistInStore = view.getStore().findExact('name', attribute.get('name'));

                            if (alredyExistInStore < 0) {
                                view.getStore().add(attribute);
                            }

                            var columns = groupData.getRange()[sRowIndex].columns || groupData.getRange()[sRowIndex].get('columns');
                            columns[sColIndex].fields = sFields;
                            group.getGroup().set('rows', sRows);

                            return true;
                        } else {
                            // is column of form                            
                            if (attribute) {
                                var targetComponent = Ext.getCmp(target.id);
                                var _attribute = {
                                    attribute: attribute.get('name'),
                                    descriptionWithName: attribute.getDescriptionWithName()
                                };
                                targetComponent.getStore().insert(orderIndex, _attribute);
                                // find the column where to push the attribute
                                var tRowIndex = targetView.getRowIndex();
                                var tColIndex = targetView.getColIndex();
                                var tColumns = sRows[tRowIndex].columns || sRows[tRowIndex].get('columns');
                                tColumns[tColIndex].fields = Ext.Array.insert(tColumns[tColIndex].fields, orderIndex, [_attribute]);
                                group.getGroup().set('rows', sRows);
                            }



                            CMDBuildUI.util.Logger.log('Dropped attribute ' + name +
                                ' on column ', CMDBuildUI.util.Logger.levels.debug, 'Drop gesture');

                            return true;
                        }
                    }
                }
            });
        }
    }
});