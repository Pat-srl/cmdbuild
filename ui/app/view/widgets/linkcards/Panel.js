/**
 * @file CMDBuildUI.view.widgets.linkcards
 * @module CMDBuildUI.view.widgets.linkcards
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.linkcards.Panel', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.widgets.linkcards.PanelController', 'CMDBuildUI.view.widgets.linkcards.PanelModel'],

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    statics: {
        /**
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @return {Object}
         */
        getTypeInfo: function (widget) {
            var objectTypeName;
            // get object type from type name
            if (widget.get('_Filter_ecql')) {
                objectTypeName = widget.get('_Filter_ecql').from;
            } else {
                objectTypeName = widget.get('ClassName');
            }
            // get object type from type name
            var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
            return {
                objectType: objectType,
                objectTypeName: objectTypeName
            };
        },

        /**
         * @param {String} objecttype
         * @return {String}
         */
        getStoreType: function (objecttype) {
            switch (objecttype) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    return 'CMDBuildUI.store.classes.Cards';
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    return 'CMDBuildUI.store.processes.Instances';
                default:
                    return null;
            }
        },

        /**
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @return {Ext.promise.Promise}
         */
        loadDefaults: function (widget, target) {
            var deferred = new Ext.Deferred();

            if (widget.get('_DefaultSelection_ecql')) {
                var typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget);
                // var storetype = CMDBuildUI.view.widgets.linkcards.Panel.getStoreType(typeinfo.objectType);

                CMDBuildUI.util.helper.ModelHelper.getModel(typeinfo.objectType, typeinfo.objectTypeName).then(
                    function (model) {
                        var ecql = CMDBuildUI.util.ecql.Resolver.resolve(widget.get('_DefaultSelection_ecql'), target);

                        var store = Ext.create('Ext.data.Store', {
                            type: 'store',
                            model: model.getName(),
                            autoLoad: false,
                            autoDestroy: true,
                            advancedFilter: {
                                ecql: ecql
                            },
                            pageSize: 0
                        });

                        store.load(function (records, operation, success) {
                            if (success) {
                                CMDBuildUI.util.Logger.log(
                                    Ext.String.format('loadDefaults: {0} records loaded', records.length),
                                    CMDBuildUI.util.Logger.levels.debug
                                );
                                deferred.resolve(records);
                            } else {
                                deferred.resolve([]);
                            }
                        });
                    }
                );
            } else {
                deferred.resolve([]);
            }

            return deferred.promise;
        },

        /**
         *
         * @param {CMDBuildUI.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         */
        onTargetFormOpen: function (target, widget, config) {
            var outputAttr = widget.get('_output'),
                typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget),
                klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    typeinfo.objectTypeName,
                    typeinfo.objectType
                );

            if (klass) {
                // load default data if the output attribute in target is not defined
                if (
                    target &&
                    outputAttr &&
                    Ext.isEmpty(target.get(outputAttr)) &&
                    !Ext.isArray(target.get(outputAttr))
                ) {
                    CMDBuildUI.view.widgets.linkcards.Panel.loadDefaults(widget, target).then(function (records, w) {
                        var defaults = [];
                        // add ids on output attribute in target
                        records.forEach(function (r) {
                            defaults.push({
                                _id: r.get('_id')
                            });
                        });
                        target.set(outputAttr, defaults);

                        // validate button
                        if (defaults.length && config.form) {
                            var widgetbutton = config.form.down('#widgetbutton_' + widget.get('_id'));
                            if (widgetbutton) {
                                widgetbutton.fireEvent('validitychange', widgetbutton, true);
                            }
                        }
                    });
                } else {
                    widget.set('_defaultsLoaded', false);
                }
            } else {
                config.form.down('#widgetbutton_' + widget.get('_id')).disable();
            }
        }
    },

    alias: 'widget.widgets-linkcards-panel',
    controller: 'widgets-linkcards-panel',
    viewModel: {
        type: 'widgets-linkcards-panel'
    },

    /**
     * @constant {String} ClassName
     * Class name or process name to get data.
     */
    ClassName: null,

    /**
     * @constant {Object} _Filter_ecql
     * eCQL filter definition.
     */
    _Filter_ecql: {},

    /**
     * @constant {String} _DefaultSelection_ecql
     * Default selection defined as eCQL filter.
     */
    _DefaultSelection_ecql: null,

    /**
     * @constant {Number} NoSelect
     * If equals to 1 disable the selection.
     */
    NoSelect: undefined,

    /**
     * @constant {Number} SingleSelect
     * If equals to 1 enable the selection of only one item.
     */
    SingleSelect: undefined,

    /**
     * @constant {*} AllowCardEditing
     * If present and different to False, allows the user to modify the row item.
     */
    AllowCardEditing: null,

    /**
     * @constant {Boolean} DisableGridFilterToggler
     * If True disable filter toggle button.
     */
    DisableGridFilterToggler: false,

    /**
     * @constant {Boolean} DefaultOnlySelected
     * If True enable the Selected Only button.
     */
    DefaultOnlySelected: false,

    /**
     * @constant {String} Output
     * The variable where the output of the widget is saved (if inserted in a process).
     */
    Output: null,

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,

    layout: 'fit',
    tbar: [
        {
            xtype: 'button',
            enableToggle: true,
            ui: 'management-neutral-action-small',
            disabled: true,
            reference: 'togglefilter',
            itemId: 'togglefilter',
            text: CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterenabled,
            bind: {
                text: '{textTogglefilter}',
                disabled: '{disableTogglefilter}',
                pressed: '{disablegridfilter}'
            }
        },
        {
            xtype: 'textfield',
            name: 'search',
            width: 250,

            emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
            },
            reference: 'searchtext',
            itemId: 'searchtext',
            cls: 'management-input',
            bind: {
                value: '{search.value}',
                hidden: '{!canFilter}'
            },
            listeners: {
                specialkey: 'onSearchSpecialKey'
            },
            triggers: {
                search: {
                    cls: Ext.baseCSSPrefix + 'form-search-trigger',
                    handler: 'onSearchSubmit'
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: 'onSearchClear'
                }
            }
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action-small',
            disabled: true,
            reference: 'refreshselection',
            itemId: 'refreshselection',
            text: CMDBuildUI.locales.Locales.widgets.linkcards.refreshselection,
            bind: {
                text: '{textRefreshselection}',
                disabled: '{disableRefreshselection}'
            }
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action-small',
            itemId: 'checkedonly',
            disabled: true,
            enableToggle: true,
            bind: {
                text: '{checkedOnlyText}',
                disabled: '{theWidget.NoSelect}',
                pressed: '{selectedOnlyPressed}'
            }
        },
        {
            xtype: 'tbfill'
        },
        CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig('gridrows')
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{theWidget._inline}'
            },
            items: [
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'button',
                    ui: 'secondary-action',
                    reference: 'closebtn',
                    itemId: 'closebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    }
                }
            ]
        }
    ]
});
