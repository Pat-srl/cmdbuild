Ext.define('CMDBuildUI.view.bim.xeokit.CanvasController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-canvas',

    control: {
        '#': {
            resetView: 'onResetView',
            modalityNavigation: 'onModalityNavigation',
            switchDimension: 'onSwitchDimension',
            switchPerspective: 'onSwitchPerspective',
            cutElement: 'onCutElement',
            resetSlice: 'onResetSlice',
            saveView: 'onSaveView',
            tooltipHelp: 'onTooltipHelp'
        }
    },

    /**
     * Return to initial view of canvas element
     * @param {Ext.panel.Tool} tool 
     */
    onResetView: function (tool) {
        var view = this.getView(),
            container = view.getContainer(),
            vm = container.getViewModel(),
            viewer = container.getViewer(),
            navCube = container.getViewerPlugin("NavCube"),
            tool2D = view.down("#twoD"),
            toolperspective = view.down("#perspective");

        tool2D.setTooltip(CMDBuildUI.locales.Locales.bim.menu.twoD);
        tool2D.setIconCls("cmdbuildicon-planimetry");
        toolperspective.setTooltip(CMDBuildUI.locales.Locales.bim.menu.ortho);
        toolperspective.removeCls(Ext.baseCSSPrefix + "bim-tool-pressed");

        viewer.cameraFlight.flyTo({
            eye: vm.get("eye"),
            look: vm.get("look"),
            up: vm.get("up"),
            duration: vm.get("duration"),
            projection: "perspective"
        }, function () {
            navCube.setVisible(true);
            viewer.cameraControl.navMode = "orbit";
            viewer.cameraMemento = null;
            vm.set("vision2D", false);
        });
    },

    /**
    * Used to modify the navigation mode
    * @param {Ext.panel.Tool} tool 
    */
    onModalityNavigation: function (tool) {
        var cameraControl = this.getView().getContainer().getViewer().cameraControl;
        if (cameraControl.navMode === 'orbit') {
            cameraControl.navMode = 'planView';
            tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('redo-alt', 'solid'));
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.rotateXeokit);
        } else {
            cameraControl.navMode = 'orbit';
            tool.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('arrows-alt', 'solid'));
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.panXeokit);
        }
    },

    /**
     * Used to switch from view 2D to view 3D and from view 3D to view 2D
     * @param {Ext.panel.Tool} tool 
     */
    onSwitchDimension: function (tool) {
        var view = this.getView(),
            container = view.getContainer(),
            vm = container.getViewModel(),
            viewer = container.getViewer(),
            storeyViewsPlugin = container.getViewerPlugin("StoreyViews"),
            navCube = container.getViewerPlugin("NavCube"),
            toolCut = view.down("#slice"),
            cameraMemento = viewer.cameraMemento;

        if (cameraMemento) {
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.twoD);
            tool.setIconCls("cmdbuildicon-planimetry");

            viewer.cameraFlight.flyTo({
                eye: cameraMemento._eye,
                look: cameraMemento._look,
                up: cameraMemento._up,
                duration: vm.get("duration"),
                projection: "perspective"
            }, function () {
                navCube.setVisible(true);
                viewer.cameraControl.navMode = "orbit";
                viewer.cameraMemento = null;
                vm.set("vision2D", false);
            });
        } else {
            viewer.cameraMemento = new xeokitSdkEs.CameraMemento();
            viewer.cameraMemento.saveCamera(viewer.scene);
            navCube.setVisible(false);
            var storey = Ext.Object.getValues(storeyViewsPlugin.storeys),
                surface = Ext.Array.map(storey, function (item, index, allitems) {
                    var aabb = item.aabb,
                        x = aabb[3] - aabb[0],
                        y = aabb[4] - aabb[1],
                        z = aabb[5] - aabb[2];
                    return 2 * x * y + 2 * x * z + 2 * y * z;
                });
            viewer.cameraFlight.jumpTo({
                fit: true
            });

            if (toolCut.pressed) {
                toolCut.pressed = false;
                toolCut.removeCls(Ext.baseCSSPrefix + "bim-tool-pressed");
                toolCut.setTooltip(CMDBuildUI.locales.Locales.bim.menu.enableslice);
                view.setStyle("cursor", "default");
                container.getViewerPlugin("SectionPlanes").hideControl();
            }

            vm.set("vision2D", true);
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.threeD);
            tool.setIconCls("cmdbuildicon-default-bim");

            if (!Ext.isEmpty(storey)) {
                storeyViewsPlugin.gotoStoreyCamera(storey[surface.indexOf(Ext.Array.max(surface))].storeyId, {
                    projection: "ortho",
                    duration: vm.get("duration"),
                    done: function () {
                        viewer.cameraControl.navMode = "planView";
                    }
                });
            } else {
                viewer.cameraFlight.flyTo({
                    eye: vm.get("eyeTop"),
                    look: vm.get("lookTop"),
                    up: [0, 0, -1],
                    projection: "ortho",
                    duration: vm.get("duration")
                }, function () {
                    viewer.cameraControl.navMode = "planView";
                });
            }
        }
    },

    /**
     * Used to switch from perspective view to ortogonal view and from ortogonal view to perspective view
     * @param {Ext.panel.Tool} tool 
     */
    onSwitchPerspective: function (tool) {
        var container = this.getView().getContainer(),
            vm = container.getViewModel(),
            viewer = container.getViewer(),
            cls = Ext.baseCSSPrefix + "bim-tool-pressed";
        viewer.cameraFlight.jumpTo({
            fit: true
        });
        if (viewer.scene.camera.projection === "perspective") {
            tool.addCls(cls);
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.perspe);
            viewer.cameraFlight.flyTo({
                projection: "ortho",
                duration: vm.get("duration")
            });
        } else {
            tool.removeCls(cls);
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.ortho);
            viewer.cameraFlight.flyTo({
                projection: "perspective",
                duration: vm.get("duration")
            });
        }
    },

    /**
     * Used when cut the canvas element
     * @param {Ext.panel.Tool} tool 
     */
    onCutElement: function (tool) {
        var view = this.getView(),
            container = view.getContainer(),
            vm = container.getViewModel(),
            viewer = container.getViewer(),
            scene = viewer.scene,
            plugin = container.getViewerPlugin("SectionPlanes"),
            cls = Ext.baseCSSPrefix + "bim-tool-pressed";

        if (tool.pressed) {
            tool.pressed = false;
            tool.removeCls(cls);
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.enableslice);
            view.setStyle("cursor", "default");
        } else {
            tool.pressed = true;
            tool.addCls(cls);
            tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.disableslice);
        }

        if (plugin.getShownControl()) {
            plugin.hideControl();
        } else if (!Ext.Object.isEmpty(plugin.sectionPlanes)) {
            plugin.showControl("cutPlane")
        } else if (tool.pressed) {
            view.setStyle("cursor", "crosshair");
        }

        scene.input.on("mouseclicked", function (coords) {
            var pickResult = scene.pick({
                canvasPos: coords,
                pickSurface: true
            });

            if (pickResult && Ext.Object.isEmpty(plugin.sectionPlanes) && tool.pressed) {
                plugin.createSectionPlane({
                    id: "cutPlane",
                    pos: pickResult.worldPos
                });
                plugin.showControl("cutPlane");
                view.setStyle("cursor", "default");
                vm.set("cutEmpty", false);
            }
        });
    },

    /**
     * Used to reset the slice of the canvas element
     * @param {Ext.panel.Tool} tool 
     */
    onResetSlice: function (tool) {
        var view = this.getView(),
            container = view.getContainer(),
            vm = container.getViewModel(),
            toolSlice = view.down("#slice");
        container.getViewerPlugin("SectionPlanes").clear();
        toolSlice.pressed = false;
        toolSlice.removeCls(Ext.baseCSSPrefix + "bim-tool-pressed");
        toolSlice.setTooltip(CMDBuildUI.locales.Locales.bim.menu.enableslice);
        view.setStyle("cursor", "default");
        vm.set("cutEmpty", true);
    },

    /**
     * Used to save the view of the canvas
     * @param {Ext.panel.Tool} tool 
     */
    onSaveView: function (tool) {
        var container = this.getView().getContainer(),
            vm = container.getViewModel(),
            camera = container.getViewer().camera;
        vm.set("eye", [camera._eye[0], camera._eye[1], camera._eye[2]]);
        vm.set("look", [camera._look[0], camera._look[1], camera._look[2]]);
        vm.set("up", [camera._up[0], camera._up[1], camera._up[2]]);
    },

    /**
     * Used to show keyboard shortcuts as tooltip on tool
     * @param {Ext.panel.Tool} tool  
     */
    onTooltipHelp: function (tool) {
        var html = "",
            rowTpl = '<div><i class="{0}"></i> {1}</div>';

        tool.setStyle("cursor", "help");

        // Title
        html += Ext.String.format('<div><b>{0}</b></div><br>', CMDBuildUI.locales.Locales.bim.menu.shortcuts.title);
        // Zoom in
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('plus-square', 'regular'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.zoomin);
        // Zoom out
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('minus-square', 'regular'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.zoomout);
        // Key left
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('arrow-left', 'solid'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.left);
        // Key right
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('arrow-right', 'solid'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.right);
        // Key up
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.up);
        // Key down
        html += Ext.String.format(rowTpl, CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'), CMDBuildUI.locales.Locales.bim.menu.shortcuts.down);

        new Ext.tip.ToolTip({
            alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++,
            target: tool,
            align: 'bl-tl',
            html: html,
            dismissDelay: 0,
            autoCreate: true
        });

    }

});