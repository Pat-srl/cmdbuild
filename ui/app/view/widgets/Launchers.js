Ext.define('CMDBuildUI.view.widgets.Launchers', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.widgets.LaunchersController', 'CMDBuildUI.view.widgets.LaunchersModel'],

    statics: {
        /**
         * Returns the widget definition
         *
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @param {Ext.form.Panel} view
         * @returns {Object}
         */
        getWidgetConfig: function (widget, target, view) {
            const widgettype = widget.get('_type');
            let xtype = CMDBuildUI.util.Config.widgets[widgettype];

            // search widget in custom widgets store
            if (!xtype) {
                const store = Ext.StoreManager.get('customcomponents.Widgets');
                const w = store.findRecord('name', widgettype);
                xtype = w.get('alias').replace('widget.', '');
            }

            // break execution and show warning if widget does not exists
            if (!xtype) {
                CMDBuildUI.util.Msg.alert('Warning', 'Widget not found');
                return;
            }

            // check dms feature if widget is attachments widget
            if (widgettype === 'openAttachment' && !CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms) {
                CMDBuildUI.util.Msg.alert('Warning', 'Attachments disabled!');
                return;
            }

            // return widget config
            return {
                xtype: xtype,
                itemId: Ext.String.format('{0}-{1}', xtype, widget.getId()),
                widgetId: widget.getId(),
                output: widget.get('_output'),
                formMode: view.formmode,
                _widgetOwner: view,
                viewModel: {
                    data: {
                        theWidget: widget,
                        theTarget: target
                    }
                },
                bind: {
                    target: '{theTarget}'
                }
            };
        },

        /**
         * Check if the widget is addable.
         *
         * @param {String} formmode
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         */
        isWidgetAddable: function (formmode, widget) {
            switch (formmode) {
                case CMDBuildUI.util.helper.FormHelper.formmodes.read:
                    return widget.get('_alwaysenabled');
                case CMDBuildUI.util.helper.FormHelper.formmodes.create:
                    return !widget.get('_hideincreation');
                case CMDBuildUI.util.helper.FormHelper.formmodes.update:
                    return !widget.get('_hideinedit');
            }
        },

        /**
         * Return the attributes binds of the view rule
         *
         * @param {String} expr
         * @param {String} viewRule
         * @returns {Object}
         */
        calculateViewRuleBinds: function (expr, viewRule) {
            const api = {};
            // extract bind property
            const bind = expr.exec(viewRule);
            try {
                if (bind && Ext.isArray(bind) && bind.length) {
                    eval(bind[0]);
                }
            } catch (err) {
                CMDBuildUI.util.Logger.log(
                    'Error evaluating executeViewRule binds',
                    CMDBuildUI.util.Logger.levels.error,
                    '',
                    err
                );
            }

            // set auto value binds
            var bindConfig;
            if (api.bind) {
                const b = {};
                api.bind.forEach(function (k) {
                    b[k] = Ext.String.format('{theObject.{0}}', k);
                });
                bindConfig = {
                    bindTo: b
                };
            } else {
                // bind every object change
                bindConfig = {
                    bindTo: '{theObject}',
                    deep: true
                };
            }

            return bindConfig;
        },

        /**
         * Add inline widgets in form items
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition[]} widgets
         * @param {String} formmode
         * @param {Ext.Component[]} items
         */
        addInlineWidgets: function (target, widgets, form, items) {
            if (widgets.count()) {
                function getGroupIndex(groupId) {
                    const group = Ext.Array.findBy(items, function (i) {
                        return i && i.groupId == groupId;
                    });
                    return group ? Ext.Array.indexOf(items, group) : -1;
                }

                widgets.getRange().forEach(function (widget) {
                    if (
                        CMDBuildUI.view.widgets.Launchers.isWidgetAddable(form.formmode, widget) &&
                        widget.get('_active')
                    ) {
                        const widgettype = widget.get('_type');
                        let wxtype = CMDBuildUI.util.Config.widgets[widgettype];

                        if (!wxtype) {
                            const store = Ext.StoreManager.get('customcomponents.Widgets');
                            const w = store.findRecord('name', widgettype);
                            if (w) {
                                wxtype = w.get('alias').replace('widget.', '');
                            } else {
                                CMDBuildUI.util.Logger.log(
                                    'Widget ' + widgettype + ' not found!',
                                    CMDBuildUI.util.Logger.levels.error
                                );
                                return;
                            }
                        }

                        const required = widget.get('_required');
                        const viewRule = widget.get('ViewRule');
                        const useViewRule = viewRule && !required;

                        const fieldset = {
                            xtype: 'widgets-fieldset',
                            title: widget.get('_label_translation'),
                            required: required,
                            collapsed: widget.get('_inlineclosed'),
                            hidden: useViewRule,
                            items: [CMDBuildUI.view.widgets.Launchers.getWidgetConfig(widget, target, form)],
                            listeners: {
                                added: function (fieldset, container, pos, eOpts) {
                                    widget._ownerPanel = fieldset;
                                    const vm = fieldset.lookupViewModel();

                                    if (widget.get('_output')) {
                                        vm.bind(
                                            {
                                                bindTo: '{theObject.' + widget.get('_output') + '}'
                                            },
                                            function (_output) {
                                                fieldset.setValue(_output);
                                                fieldset.isValid();
                                            }
                                        );
                                    }

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

                                        vm.bind(bindConfig, function () {
                                            const api = Ext.apply(
                                                {
                                                    record: this.get('theObject'),
                                                    mode: form.formmode
                                                },
                                                CMDBuildUI.util.api.Client.getApiForFieldVisibility()
                                            );

                                            const visibility = executeViewRule(api);
                                            if (
                                                visibility === true ||
                                                visibility === 'true' ||
                                                visibility === 'enabled'
                                            ) {
                                                fieldset.setHidden(false);
                                                fieldset.setDisabled(false);
                                            } else if (visibility === 'disabled') {
                                                fieldset.setHidden(false);
                                                fieldset.setDisabled(true);
                                            } else {
                                                fieldset.setHidden(true);
                                                fieldset.setDisabled(true);
                                            }
                                        });
                                    }
                                }
                            }
                        };

                        let index, position;
                        if (widget.get('_inlineafter')) {
                            index = getGroupIndex(widget.get('_inlineafter'));
                            position = 'after';
                        } else if (widget.get('_inlinebefore')) {
                            index = getGroupIndex(widget.get('_inlinebefore'));
                            position = 'before';
                        }

                        if (index !== -1 && position === 'after') {
                            Ext.Array.insert(items, index + 1, [fieldset]);
                        } else if (index !== -1 && position === 'before') {
                            Ext.Array.insert(items, index, [fieldset]);
                        } else {
                            items.push(fieldset);
                        }
                    }
                });
            }
        }
    },

    alias: 'widget.widgets-launchers',
    controller: 'widgets-launchers',
    viewModel: {
        type: 'widgets-launchers'
    },

    cls: Ext.baseCSSPrefix + 'widgets-launchers-container',

    config: {
        /**
         * @cfg {Ext.data.Store} [widgets]
         * A store of {CMDBuildUI.model.WidgetDefinition} instances.
         */
        widgets: null,

        /**
         * @cfg {String} [formmode]
         * Possible values are `read`, `update` and `create`.
         * Default value is `read`.
         */
        formMode: 'read',

        /**
         * @cfg {String} [targetLinkName="theObject"]
         * The link name to use to pass the target to the widget.
         */
        targetLinkName: 'theObject'
    },
    publishes: ['widgets', 'formMode'],

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    hidden: true,

    bind: {
        hidden: '{hideLaunchersPanel}'
    },

    /**
     * @event widgetschanged
     * Fires when widgets property is changed.
     *
     * @param {Ext.Component} this
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     * @param {Object} eOpts
     */

    /**
     * @event widgetbuttonclick
     * Fires when widget button is clicked.
     *
     * @param {Ext.Component} this
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */

    /**
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    updateWidgets: function (newvalue, oldvalue) {
        this.fireEvent('widgetschanged', this, newvalue, oldvalue);
    }
});
