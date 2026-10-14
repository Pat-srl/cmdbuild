Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.OtherPropertiesControllerMixin', {
    mixinId: 'administration-otherpropertiescontrollermixin',

    onAceEditorHelpExpand: function () {
        this.onAceExpand(
            'help',
            'ace/mode/markdown',
            CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif
        );
    },

    onAceEditorShowIfExpand: function () {
        this.onAceExpand(
            'showIf',
            'ace/mode/javascript',
            CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif
        );
    },

    onAceEditorValidationRulesExpand: function () {
        this.onAceExpand(
            'validationRules',
            'ace/mode/javascript',
            CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules
        );
    },

    onAceEditorAutoValueExpand: function () {
        this.onAceExpand(
            'autoValue',
            'ace/mode/javascript',
            CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue
        );
    },

    /**
     * On show sql button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAceExpand: function (property, mode, panelTitle) {
        var vm = this.getViewModel();
        var attribute = vm.get('theAttribute');
        var value = attribute.get(property);
        var elementId = Ext.String.format('{0}_{1}', attribute.getId(), property);
        var popupId = Ext.String.format('popup-attribute-{0}', elementId);

        var editor;

        var popUp,
            content = {
                xtype: 'panel',
                layout: {
                    type: 'hbox'
                },
                ui: 'administration-formpagination',
                fieldDefaults: {
                    labelAlign: 'top'
                },
                viewModel: {},
                dockedItems: [
                    {
                        hidden: vm.get('actions.view'),
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            {
                                xtype: 'component',
                                flex: 1
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                                ui: 'administration-secondary-action-small',
                                listeners: {
                                    click: function () {
                                        attribute.set(property, value);
                                        popUp.fireEvent('close');
                                    }
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                                ui: 'administration-action-small',
                                listeners: {
                                    click: function (_button) {
                                        vm.set(Ext.String.format('theAttribute.{0}', property), editor.getValue());
                                        var mainInput =
                                            CMDBuildUI.util.Navigation.getAdministrationDetailsWindow().down(
                                                Ext.String.format('#attribute-{0}', property)
                                            );
                                        if (mainInput) {
                                            mainInput.getAceEditor().setValue(editor.getValue(), -1);
                                        }
                                        popUp.fireEvent('close');
                                    }
                                }
                            }
                        ]
                    }
                ],

                items: [
                    {
                        flex: 1,
                        layout: 'card',
                        height: '100%',
                        viewModel: {},
                        items: [
                            {
                                xtype: 'component',
                                layout: 'card',
                                height: '100%',
                                viewModel: {},
                                html: Ext.String.format('<div id="{0}"></div>', elementId),
                                listeners: {
                                    afterrender: function (cmp) {
                                        editor = ace.edit(elementId, {
                                            //set autoscroll
                                            autoScrollEditorIntoView: true,
                                            maxLines: 90, // this will be changed on "heightUpdateFunction"
                                            // set theme
                                            theme: 'ace/theme/chrome',
                                            // show line numbers
                                            showLineNumbers: true,
                                            // hide print margin
                                            showPrintMargin: false,
                                            wrap: true
                                        });
                                        if (vm.get('actions.view')) {
                                            editor.setReadOnly(true);
                                        }

                                        editor.getSession().setMode(mode);
                                        editor.getSession().setUseWrapMode(true);
                                        editor.setValue(value, -1);

                                        var heightUpdateFunction = function (editor) {
                                            var editorDiv = document.getElementById(elementId); // its container
                                            var lineHeight = editor.renderer.lineHeight;
                                            editorDiv.style.height = '100%'; // set new container height
                                            editorDiv.style.width = '100%'; // force container width to 100%
                                            editor.setOption(
                                                'maxLines',
                                                Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight)
                                            );
                                            editor.setOption(
                                                'minLines',
                                                Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight)
                                            );
                                        };
                                        cmp.mon(cmp, 'resize', function () {
                                            editor.renderer.$updateSizeAsync();
                                        });

                                        // Set initial size to match initial content
                                        Ext.asap(
                                            function (editor) {
                                                heightUpdateFunction(editor);
                                            },
                                            this,
                                            [editor]
                                        );

                                        // Whenever a change happens inside the ACE editor, update
                                        // the size again
                                        onUpdateFunction = function (event, doc) {
                                            // update stored value
                                            attribute.set(property, doc.getValue());
                                        };

                                        editor.getSession().on('change', onUpdateFunction);
                                    }
                                }
                            }
                        ]
                    }
                ]
            };

        popUp = CMDBuildUI.util.Utilities.openPopup(
            popupId,
            panelTitle,
            content,
            {
                closed: function () {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                },
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                close: function (panel, eOpts) {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                }
            },
            {
                ui: 'administration',
                width: '80%',
                height: '80%',
                viewModel: {},
                draggable: true
            }
        );
    }
});
