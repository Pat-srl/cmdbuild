Ext.define('CMDBuildUI.view.widgets.startworkflow.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-startworkflow-panel',
    control: {
        "#": {
            beforerender: "onBeforeRender"
        },
        '#startworkflowcancelBtn': {
            click: 'onstartworkflowcancelBtnClick'
        },
        '#startworkflowexecuteBtn': {
            click: 'onstartworkflowexecuteBtnClick'
        },
        '#startworkflowsaveBtn': {
            click: 'onstartworkflowsaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.startworkflow.PanelController} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = view.getViewModel();
        var theWidget = vm.get('theWidget');
        objectTypeName = theWidget.get('workflowName') || theWidget.get('WorkflowCode');

        // this type of widget does not support inline mode
        if (theWidget.get('_inline')) {
            view.showNotSupportedInlineMessage();
            return;
        }
        var theTarget = vm.get('theTarget');

        vm.set('objectTypeName', objectTypeName);
        var panel = view.add({
            xtype: 'processes-instances-instance-create',
            buttons: [{
                reference: 'startworkflowcancelBtn',
                itemId: 'startworkflowcancelBtn',
                ui: 'secondary-action-small',
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                autoEl: {
                    'data-testid': 'processinstance-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                }
            }, {
                reference: 'startworkflowsaveBtn',
                itemId: 'startworkflowsaveBtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                ui: 'management-primary-outline-small',
                bind: {
                    hidden: '{hideSaveButton}'
                },
                autoEl: {
                    'data-testid': 'processinstance-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            }, {
                reference: 'startworkflowexecuteBtn',
                itemId: 'startworkflowexecuteBtn',
                text: CMDBuildUI.locales.Locales.common.actions.execute,
                ui: 'management-primary-small',
                formBind: true, //only enabled once the form is valid
                disabled: true,
                autoEl: {
                    'data-testid': 'processinstance-execute'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.execute'
                }
            }]
        });
        panel.getViewModel().bind({
            bindTo: '{theObject}'
        }, function (object) {
            var presets = theWidget.get("preset"),
                jsonparsed,
                objpresets;
            if (presets) {
                try {
                    jsonparsed = JSON.parse(presets);
                    objpresets = jsonparsed;
                } catch (e) {
                    objpresets = me.toValidJSON(presets);
                }
                if (objpresets) {
                    for (var key in objpresets) {
                        var presetvar = me.extractVariableFromString(objpresets[key], theTarget);
                        object.set(key, presetvar);
                    }
                }
            } else if (theWidget.getData()) {
                var data = object.getData();
                for (var key in theWidget.getData()) {
                    if (!key.startsWith("_") && key in data) {
                        object.set(key, theWidget.get(key));
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowcancelBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowexecuteBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        // disable button
        button.disable();
        // execute process
        var view = this.getView(),
            panel = view.down('processes-instances-instance-create'),
            theWidget = panel.getViewModel().get('theWidget'),
            theTarget = panel.getViewModel().get('theTarget');
        panel.executeProcess({
            success: function (record, operation) {
                if (theWidget.get('_output')) {
                    theTarget.set(theWidget.get('_output'), record.get('_id'));
                }
                if (theWidget.get("_required")) {
                    theWidget.getOwner().setValue(true);
                }
                view.fireEvent("popupclose");
            },
            failure: function () {
                button.enable();
            },
            callback: function (record, operation, success) {
                if (panel && panel.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(panel.loadMask);
                }
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowsaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        // disable button
        button.disable();
        // execute process
        var view = this.getView(),
            panel = view.down('processes-instances-instance-create'),
            theObject = panel.getViewModel().get('theObject'),
            theWidget = panel.getViewModel().get('theWidget');
        panel.saveProcess({
            success: function (record, operation) {
                if (theWidget.get('_output')) {
                    theObject.set(theWidget.get('_output'), record.get('_id'));
                }
                if (theWidget.get("_required")) {
                    theWidget.getOwner().setValue(true);
                }
                view.fireEvent("popupclose");
            },
            failure: function () {
                button.enable();
            },
            callback: function (record, operation, success) {
                if (panel && panel.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(panel.loadMask);
                }
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * Resolve variable.
     * @param {String} variable
     * @param {CMDBuildUI.model.base.Base} theTarget
     * @return {*} The variable resolved.
     */
    extractVariableFromString: function (variable, theTarget) {
        if (CMDBuildUI.util.api.Client.testRegExp(/^{(client|server)+:*.+}$/, variable)) {
            variable = variable.replace("{", "").replace("}", "");
            var s_variable = variable.split(":"),
                resolvedVariable = null;
            if (s_variable[0] === "server") {
                resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
                return Object.values(resolvedVariable)[0];
            } else if (s_variable[0] === "client") {
                resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
                return Object.values(resolvedVariable)[0];
            } else if (s_variable.length === 1 && theTarget.getField(s_variable[0])) {
                return theTarget.get(s_variable[0]);
            }
        }
        return variable;
    },

    toValidJSON: function (presets) {
        presets = presets.replace(/\s+/g, '');
        var items = presets.substr(1, presets.length - 2).split(','),
            jsonString = '{';
        for (var i = 0; i < items.length; i++) {
            var current = items[i].split('=');
            jsonString += '"' + current[0] + '":"' + current[1] + '",';
        }
        jsonString = jsonString.substr(0, jsonString.length - 1) + '}';
        return JSON.parse(jsonString);
    }
});