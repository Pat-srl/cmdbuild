Ext.define('CMDBuildUI.view.dashboards.parameters.ParametersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dashboards-parameters-parameters',
    mixins: ['CMDBuildUI.view.dashboards.Mixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.dashboards.parameters.Parameters} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.add(this.getParametersForm(Ext.create('Ext.data.Store', { data: view.getParameters() })));
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} e
     * @param {CMDBuildUI.view.dashboards.parameters.Parameters} view
     */
    onApplyBtnClick: function (button, e) {
        const view = button.up('dashboards-parameters-parameters');
        Ext.asap(function () {
            Ext.GlobalEvents.fireEvent('paramschanged', view);
        });
    },

    privates: {
        /**
         *
         * @param {CMDBuild.model.dashboards.Component} component
         * @return {Ext.form.Panel}
         */
        getParametersForm: function (parameters) {
            const me = this;
            const vm = this.getViewModel();
            const values = {};
            const paramsnames = vm.get('paramsnames');
            const form = {
                xtype: 'form',
                layout: 'column',
                bodyPadding: '0 10 0 0',
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [],
                bind: { hidden: '{form.hidden}' },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        defaults: { minWidth: 100 },
                        items: [
                            { xtype: 'tbfill' },
                            {
                                xtype: 'button',
                                formBind: true,
                                ui: 'management-primary-outline-small',
                                text: CMDBuildUI.locales.Locales.common.actions.apply,
                                handler: me.onApplyBtnClick
                            }
                        ]
                    }
                ]
            };
            parameters.getRange().forEach(function (parameter, index) {
                const field = me.getEditorForParameter(parameter, false);
                form.items.push({
                    xtype: 'container',
                    columnWidth: 0.5,
                    layout: 'fit',
                    padding: '0 0 0 10',
                    items: [
                        Ext.apply(field, {
                            columnWidth: 1
                        })
                    ]
                });
                values[field.name] = parameter.get('defaultValue');
                paramsnames[field.name] = field.metadata.attributename;
            });
            vm.set('parameters', Ext.create('Ext.data.Model', values));
            vm.set('paramsnames', paramsnames);
            return form;
        }
    }
});
