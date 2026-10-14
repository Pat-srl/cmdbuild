Ext.define('CMDBuildUI.view.fields.formvalidator.FieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-formvalidator-field',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            code = view.getValidationCode(),
            linkname = view.getLinkName(),
            activitylinkname = view.getActivityLinkName(),
            formmode = view.getFormMode(),
            bindings = CMDBuildUI.util.helper.FormHelper.extractBindFromExpression(code, linkname);

        if (view.getCalendarMessage()) {
            vm.set('errorMessage', view.getCalendarMessage());
            return;
        }

        /* jshint ignore:start */
        eval(Ext.String.format(
            'function executeFormValidationCode(api) {\n{0}\n}',
            code
        ));
        /* jshint ignore:end */

        var api = Ext.apply({
            record: vm.get(linkname),
            activity: vm.get(activitylinkname),
            mode: formmode
        }, CMDBuildUI.util.api.Client.getApiForFormCustomValidator());

        // add formula on view model
        vm.bind(bindings, function () {
            try {
                var isvalid = executeFormValidationCode(api);
                if (isvalid === false) {
                    isvalid = CMDBuildUI.locales.Locales.notifier.error;
                } else if (Ext.isArray(isvalid)) {
                    isvalid = isvalid.join('<br />');
                }
                if (isvalid !== true) {
                    vm.set('errorMessage', isvalid);
                } else {
                    vm.set('errorMessage', null);
                }
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    'Error on form validation code',
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    e
                );
                vm.set('errorMessage', null);
            }
        });

        if (view.getShowActionButton()) {
            const div = document.createElement('div');
            const span = document.createElement('span');
            const button = document.createElement('a');
            span.id = "formValidatorFieldText";
            button.id = "formValidatorFieldAction";
            button.className = Ext.baseCSSPrefix + 'formvalidator-field-action-button';
            button.innerHTML = CMDBuildUI.locales.Locales.bulkactions.addfields;
            div.append(span);
            div.append(button);
            view.messageEl.dom.appendChild(div);
        }
    }
});
