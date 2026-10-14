Ext.define('CMDBuildUI.view.thematisms.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-panel',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender'
            },
            '#cancelbutton': {
                click: 'onCancelButtonClick'
            },
            '#calculaterules': {
                click: 'onCalculateButtonClick'
            },
            '#modifyrules': {
                click: 'onModifyButtonClick'
            },
            '#modifyrules': {
                click: 'onModifyButtonClick'
            },
            '#applybutton': {
                click: 'onApplyButtonClick'
            },
            '#savebutton': {
                click: 'onSaveButtonClick'
            }
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.add([{
            xtype: 'thematisms-thematism-row',
            reference: 'thematisms-thematism-row'
        }, {
            xtype: 'thematisms-thematism-rules',
            reference: 'rules',
            needListener: true
        }])
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, eOpts) {
        this.getView().fireEvent('popupclose', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onCalculateButtonClick: function (button, eOpts) {
        this.getView().fireEvent('calculaterules', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onApplyButtonClick: function () {
        this.getView().fireEvent('applythematism', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, eOpts) {
        //TODO: implement the new popup asking for the name
        // this.getView().fireEvent('saveandapplythematism', this.getView(), this.getThematism());
        var view = this.getView(),
            thematism = this.getThematism(),
            applyBtn = view.down("#applybutton"),
            cancelBtn = view.down("#cancelbutton");

        var w = Ext.create('Ext.window.Window', {
            title: thematism.get('description'),
            width: 400,
            layout: 'fit',
            alwaysOnTop: 10,
            modal: true,
            ui: "management",

            viewModel: {
                data: {
                    theThematism: thematism
                }
            },

            items: {
                xtype: 'form',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [{
                    xtype: 'textfield',
                    name: 'name',
                    fieldLabel: CMDBuildUI.locales.Locales.thematism.name,
                    bind: '{theThematism.name}',
                    allowBlank: false  // requires a non-empty value
                }]
            },

            buttons: [{
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                ui: 'secondary-action-small',
                handler: function () {
                    CMDBuildUI.util.Utilities.enableFormButtons([applyBtn, button, cancelBtn]);
                    w.destroy();
                }
            }, {
                text: CMDBuildUI.locales.Locales.common.actions.save,
                ui: 'management-primary-small',
                handler: function () {
                    thematism.set("description", thematism.get("name"));
                    view.fireEvent('saveandapplythematism', view, thematism);
                    w.destroy();
                }
            }]
        });

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([applyBtn, button, cancelBtn]);
        w.show();
    },

    onModifyButtonClick: function (button, eOpts) {
        var thematism = this.getThematism(),
            allRules = thematism.rules().getRange(),
            rules = allRules.slice(0, allRules.length - 1),
            segments = [],
            config = [];

        Ext.Array.forEach(rules, function (item, index, allitems) {
            segments = Ext.Array.merge(segments, item.get("value").map(Number));
        });

        Ext.Array.forEach(segments, function (item, index, allitems) {
            config.push({
                xtype: 'displayfield',
                itemId: 'segment-' + index,
                value: segments[index]
            });
            if (index !== segments.length - 1) {
                config.push({
                    xtype: 'tbfill',
                    cls: 'x-thematism-line'
                });
            }
        });

        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.thematism.editThematism,
            {
                xtype: 'panel',
                viewModel: {
                    data: {
                        sliderValidation: true
                    }
                },
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype: 'formvalidatorfield',
                    margin: '0 10 0 10',
                    bind: {
                        hidden: '{sliderValidation}',
                        value: CMDBuildUI.locales.Locales.thematism.bordersIntervals,
                        errorMessage: CMDBuildUI.locales.Locales.thematism.bordersIntervals
                    }
                }, {
                    xtype: 'multislider',
                    itemId: 'slider',
                    layout: 'fit',
                    padding: '30 20 0 20',
                    values: segments.slice(1, segments.length - 1),
                    increment: 1,
                    minValue: segments[0],
                    maxValue: segments[segments.length - 1],
                    useTips: {
                        alwaysOnTop: 1000000
                    },
                    listeners: {
                        change: function (slider, newValue, thumb, eOpts) {
                            var values = slider.getValues(),
                                view = slider.ownerCt.down("#valuesThematism"),
                                unique = Ext.Array.unique(values).length === values.length && values[0] !== slider.minValue && values[values.length - 1] !== slider.maxValue;
                            slider.ownerCt.getViewModel().set("sliderValidation", unique ? true : false);
                            slider.ownerCt.ownerCt.setHeight(unique ? 200 : 250)
                            Ext.Array.each(values, function (item, index, allitems) {
                                var segment = view.down(Ext.String.format("#segment-{0}", index + 1));
                                if (segment.getValue() !== values[index]) {
                                    segment.setValue(values[index]);
                                    return false;
                                }
                            });
                        }
                    }
                }, {
                    xtype: 'container',
                    itemId: 'valuesThematism',
                    layout: 'hbox',
                    padding: '0 15 0 15',
                    items: config
                }],
                buttons: [{
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    itemId: 'cancelbtn',
                    ui: 'secondary-action',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    },
                    handler: function (button, event) {
                        button.ownerCt.ownerCt.ownerCt.close();
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    itemId: 'savebtn',
                    ui: 'management-primary',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    disabled: true,
                    bind: {
                        disabled: '{!sliderValidation}'
                    },
                    handler: function (button, event) {
                        var values = button.ownerCt.ownerCt.down("#slider").getValue();
                        Ext.Array.forEach(allRules.slice(0, allRules.length - 1), function (item, index, allitems) {
                            var valuesRule = item.get("value");
                            switch (index) {
                                case 0:
                                    item.set("value", [valuesRule[0], values[0]]);
                                    break;
                                case allRules.length - 2:
                                    item.set("value", [values[values.length - 1], valuesRule[1]]);
                                    break;
                                default:
                                    item.set("value", [values[index - 1], values[index]]);
                                    break;
                            }
                        });
                        thematism.rules().clearData();
                        thematism.rules().insert(0, allRules);
                        button.ownerCt.ownerCt.ownerCt.close();
                    }
                }]
            },
            null,
            {
                width: 400,
                height: 200
            }
        )
    },


    privates: {

        /**
         * @returns {CMDBuildUI.model.thematisms.Thematism}
         */
        getThematism: function () {
            var thematism = this.getViewModel().get("theThematism");
            //here are set some configurations
            return thematism;
        }
    }
});
