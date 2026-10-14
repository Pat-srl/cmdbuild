Ext.define('CMDBuildUI.view.map.tab.cards.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-layers',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },

        '#saveLayersPreferencesBtn': {
            click: 'onSaveLayersPreferencesBtnClick',
            destroy: 'onDestroyTool'
        }
    },

    /**
     * @param {CMDBuildUI.view.map.tab.cards.Layers} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel(),
            prefs = CMDBuildUI.util.helper.UserPreferences.getGridPreferences("gis", "layers"),
            unchecked = prefs && prefs.uncheckedLayers ? prefs.uncheckedLayers : [],
            toolSave = view.down("#saveLayersPreferencesBtn");

        if (Ext.Object.isEmpty(prefs)) {
            toolSave.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'));
        } else {
            toolSave.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'solid'));
        }

        CMDBuildUI.util.helper.MapHelper.getLayersMenu().then(function (tree) {
            vm.bind({
                bindTo: '{geoAttributesStore}',
                single: true
            }, function (layers) {
                const treeRoot = tree.getRoot().getChildAt(0),
                    root = {
                        id: 'root',
                        skipnode: true,
                        text: CMDBuildUI.locales.Locales.gis.root,
                        expanded: true,
                        checked: true,
                        leaf: false,
                        zoomMax: Ext.Number.MAX_SAFE_INTEGER,
                        zoomMin: Ext.Number.MIN_SAFE_INTEGER,
                        children: []
                    },
                    addedLayers = [];

                const addLeaf = function (rootNode, layer) {
                    layer.set('checked', !Ext.Array.contains(unchecked, layer.get("composed_name")) && layer.get('visibility')[vm.get('objectTypeName')]);
                    const icon = layer.get('_icon');
                    const type = layer.get('subtype');
                    const child = {
                        text: layer.get('text'),
                        zoomMax: layer.get('zoomMax'),
                        zoomMin: layer.get('zoomMin'),
                        checked: layer.get('checked'),
                        ollayername: layer.get('ollayername'),
                        geoattribute: layer,
                        leaf: true,
                    };

                    if (icon) {
                        child.icon = Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, icon);
                    } else if (type === CMDBuildUI.model.gis.GeoAttribute.subtype.polygon) {
                        child.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('draw-polygon', 'solid');
                    } else if (type === CMDBuildUI.model.gis.GeoAttribute.subtype.linestring) {
                        child.iconCls = 'cmdbuildicon-flow-line';
                    } else if (type === CMDBuildUI.model.gis.GeoAttribute.subtype.point) {
                        child.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid');
                    } else if (type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape) {
                        child.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('shapes', 'solid'); // non va
                    }

                    // --- html style element with class for color of the layer ---
                    const className = `layer-color-${layer.get('_id')}`;
                    if (!document.getElementById(className) && !icon) { // create only if doesn't exist and it doesn't have an icon
                        const style = document.createElement('style');
                        style.id = className
                        style.innerHTML = `.${className} { color: ${layer.getStyle().get('fillColor')}; }`;
                        document.getElementsByTagName('head')[0].appendChild(style);
                    }

                    child.iconCls += ` ${className}`;
                    rootNode.children.push(child);
                }

                const hasCheckedChild = function (childItems) {
                    const hasChecked = Ext.Array.findBy(childItems, function (item) {
                        if (item.get("menuType") === CMDBuildUI.model.menu.MenuItem.types.folder) {
                            return hasCheckedChild(item.childNodes);
                        } else {
                            const layer = layers.findRecord("composed_name", item.get("objectTypeName"));
                            if (layer) {
                                return !Ext.Array.contains(unchecked, layer.get("composed_name"));
                            }
                            return false;
                        }
                    });
                    return !Ext.isEmpty(hasChecked);
                }

                const addNodes = function (rootNode, items) {
                    items.forEach(function (node) {
                        if (node.get("menuType") === CMDBuildUI.model.menu.MenuItem.types.folder) {
                            const folder = {
                                text: node.get("_objectDescription_translation"),
                                expanded: true,
                                checked: hasCheckedChild(node.childNodes), // only if there is at least one child checked
                                leaf: false,
                                skipnode: true,
                                children: []
                            };
                            addNodes(folder, node.childNodes);

                            if (folder.children.length) {
                                // add folder to root node
                                rootNode.children.push(folder);
                                // set min and max zoom
                                folder.zoomMin = Ext.Array.min(Ext.Array.pluck(folder.children, "zoomMin"));
                                folder.zoomMax = Ext.Array.max(Ext.Array.pluck(folder.children, "zoomMax"));
                            }
                        } else {
                            const layer = layers.findRecord("composed_name", node.get("objectTypeName"));
                            if (layer) {
                                addLeaf(rootNode, layer);
                                addedLayers.push(layer.get("_id"));
                            }
                        }
                    });
                }

                if (treeRoot) {
                    // add nodes in the tree
                    addNodes(root, treeRoot.childNodes);
                }

                // add missing layers
                if (addedLayers.length !== layers.getTotalCount()) {
                    layers.getRange().forEach(function (layer) {
                        if (!Ext.Array.contains(addedLayers, layer.get("_id"))) {
                            addLeaf(root, layer);
                        }
                    });
                }

                vm.set("treeRootItem", root);
            });
        });
    },

    /**
     * @param {Ext.store.Tree} store
     * @param {Ext.data.Model} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {Object} details
     * @param {Object} eOpts
     */
    onLayersTreeStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        const view = this.getView();
        const checked = record.get("checked");
        const geoattribute = record.get("geoattribute");
        const parentNode = record.parentNode;
        if (record.isLeaf()) {
            geoattribute.set("checked", checked);
            if (checked) {
                Ext.GlobalEvents.fireEventArgs("addDataLayer", [null, geoattribute.getId()]);
                if (!parentNode.get('checked')) {
                    store.suspendEvent('update');
                    parentNode.set('checked', checked);
                    store.resumeEvent('update');
                }
            } else {
                // clear data for specific layer
                const olLayer = view.getOlLayer(view.getMapContainerView().getViewMap().getOlMap(), geoattribute.get("ollayername"));
                if (olLayer) view.getOlLayerSource(olLayer).clear();
                if (parentNode.get('checked')) {
                    // check if parentNode has checked childs
                    const hasChecked = Ext.Array.findBy(parentNode.childNodes, function (node) {
                        return node.get('checked');
                    });
                    store.suspendEvent('update');
                    parentNode.set('checked', !Ext.isEmpty(hasChecked));
                    store.resumeEvent('update');
                }
            }
        } else {
            Ext.Array.forEach(record.childNodes, function (child, index) {
                child.set("checked", checked);
            });
        }
    },

    /**
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onSaveLayersPreferencesBtnClick: function (tool, event) {
        const me = this;
        const view = me.getView();
        const savePreferences = function () {
            const vm = tool.lookupViewModel();
            const layerStore = vm.get('layerStore');
            const allLayers = view.getOlLayerSource(layerStore);
            const unchecked = [];


            allLayers.getRange().forEach(function (layer) {
                if (!layer.get("checked")) {
                    unchecked.push(layer.get("composed_name"));
                }
            });
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences("gis", "layers", {
                uncheckedLayers: unchecked
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.gis.layersTab.preferencesSaved);
                tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'solid'));
            });
        }

        const clearPreferences = function () {
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences("gis", "layers", {
                uncheckedLayers: undefined
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.gis.layersTab.preferencesCleared);
                tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'));
            });
        }

        if (Ext.Object.isEmpty(CMDBuildUI.util.helper.UserPreferences.getGridPreferences("gis", "layers"))) {
            // save preferences if not saved yet
            savePreferences();
        } else {
            // show menu to chose action update or clear
            if (tool.menu) {
                tool.menu.show();
            } else {
                tool.menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: [{
                        text: CMDBuildUI.locales.Locales.gis.layersTab.updatePreferences,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('save', 'regular'),
                        handler: function () {
                            savePreferences();
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.gis.layersTab.clearPreferences,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                        handler: function () {
                            clearPreferences();
                        }
                    }]
                });
                tool.menu.alignTo(tool.el.id, 't-b?');
            }
        }
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Object} eOpts
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    }
});