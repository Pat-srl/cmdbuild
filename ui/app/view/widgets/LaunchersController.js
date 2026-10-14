Ext.define('CMDBuildUI.view.widgets.LaunchersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-launchers',

    control: {
        '#': {
            widgetschanged: 'onWidgetsChanged',
            widgetbuttonclick: 'onWidgetButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.Launchers} view
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    onWidgetsChanged: function (view, newvalue, oldvalue) {
        const vm = view.lookupViewModel();
        if (newvalue && newvalue.getData().length) {
            Ext.Array.each(newvalue.getRange(), function (widget, index) {
                if (widget.get('_active') && !widget.get('_inline')) {
                    const widgettype = widget.get('_type');
                    const required = widget.get('_required');
                    const disabled = !CMDBuildUI.view.widgets.Launchers.isWidgetAddable(view.getFormMode(), widget);
                    const viewRule = widget.get('ViewRule');
                    const useViewRule = viewRule && !disabled && !required;

                    const wconf = {
                        xtype: 'widgets-button',
                        itemId: 'widgetbutton_' + widget.get('_id'),
                        text: widget.get('_label_translation') + (required ? ' *' : ''),
                        disabled: disabled,
                        hidden: useViewRule,
                        required: required,
                        handler: function (button, e) {
                            view.fireEvent('widgetbuttonclick', view, button, widget, e);
                        },
                        listeners: {
                            render: function (button) {
                                const vmButton = button.lookupViewModel();
                                vmButton.bind(
                                    {
                                        bindTo: '{theObject.' + widget.get('_output') + '}'
                                    },
                                    function (_output) {
                                        button.setValue(_output);
                                        button.isValid();
                                    }
                                );

                                if (useViewRule) {
                                    const expr = /^api\.bind(\s?)=(\s?)\[.*\](\s?);/;
                                    const bindConfig = CMDBuildUI.view.widgets.Launchers.calculateViewRuleBinds(
                                        expr,
                                        viewRule
                                    );
                                    const script = viewRule.replace(expr, '');
                                    /* jshint ignore:start */
                                    try {
                                        const jsfn = Ext.String.format(
                                            'function executeViewRule(api) {\n{0}\n}',
                                            script
                                        );
                                        eval(jsfn);
                                    } catch (err) {
                                        CMDBuildUI.util.Logger.log(
                                            'Error evaluating executeViewRule script',
                                            CMDBuildUI.util.Logger.levels.error,
                                            '',
                                            err
                                        );
                                        var executeViewRule = function () {
                                            return true;
                                        };
                                    }
                                    /* jshint ignore:end */

                                    vmButton.bind(bindConfig, function () {
                                        const api = Ext.apply(
                                            {
                                                record: this.get('theObject'),
                                                mode: view.getFormMode()
                                            },
                                            CMDBuildUI.util.api.Client.getApiForFieldVisibility()
                                        );

                                        const visibility = executeViewRule(api);
                                        if (visibility === true || visibility === 'true' || visibility === 'enabled') {
                                            button.setHidden(false);
                                            button.setDisabled(false);
                                        } else if (visibility === 'disabled') {
                                            button.setHidden(false);
                                            button.setDisabled(true);
                                        } else {
                                            button.setHidden(true);
                                            button.setDisabled(true);
                                        }
                                    });
                                }
                            }
                        }
                    };

                    function addWidget() {
                        try {
                            // add widget button
                            widget._ownerButton = view.add(wconf);
                            // show panel
                            vm.set('hideLaunchersPanel', false);
                        } catch (e) {
                            CMDBuildUI.util.Logger.log(
                                'Malformed widget configuration.',
                                CMDBuildUI.util.Logger.levels.warn,
                                null,
                                wconf
                            );
                        }
                    }

                    const isCustomWidget = !CMDBuildUI.util.Config.widgets[widgettype];
                    if (isCustomWidget) {
                        const widgetsStore = Ext.StoreManager.get('customcomponents.Widgets');

                        function loadCustomWidget() {
                            const w = widgetsStore.findRecord('name', widgettype);
                            if (w) {
                                addWidget();
                                Ext.require(Ext.String.format('CMDBuildUI.{0}', w.get('componentId')));
                            } else {
                                CMDBuildUI.util.Logger.log(
                                    Ext.String.format(CMDBuildUI.locales.Locales.widgets.notimplemented, widgettype),
                                    CMDBuildUI.util.Logger.levels.warn
                                );
                            }
                        }

                        if (!widgetsStore.isLoaded()) {
                            widgetsStore.load({
                                callback: loadCustomWidget
                            });
                        } else {
                            loadCustomWidget();
                        }
                    } else {
                        addWidget();
                    }
                }
            });

            vm.bind(
                {
                    theObject: '{theObject}'
                },
                function (data) {
                    CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetFormOpen(data.theObject, newvalue.getRange(), {
                        formmode: view.getFormMode(),
                        form: view
                    });
                },
                {
                    single: true
                }
            );
        }
    },

    /**
     * Return the name of the model used by the widget.
     * @return {String}
     */
    getModelName: function (theWidget) {
        return 'CMDBuildUI.model.customform.' + theWidget.getId();
    },

    /**
     * @param {Ext.Component} view
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetButtonClick: function (view, button, widget, e, eOpts) {
        // update ajax action id
        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('widget.open.{0}.{1}', widget.get('_type'), widget.getId()));

        let popup;
        // create widget configuration
        const config = CMDBuildUI.view.widgets.Launchers.getWidgetConfig(
            widget,
            view.lookupViewModel().get(view.getTargetLinkName()),
            view.up('form')
        );

        // custom event listener
        config.listeners = {
            /**
             * Custom event to close popup directly from widget
             */
            popupclose: function (eOpts) {
                popup.close();
            }
        };

        // custom panel listeners
        const listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            beforeclose: function (panel, eOpts) {
                panel.removeAll(true);
            },
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                button.fireEvent('validitychange', button, button.isValid());
            }
        };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(null, widget.get('_label_translation'), config, listeners);
    }
});
