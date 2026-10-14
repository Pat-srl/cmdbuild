Ext.define('CMDBuildUI.view.administration.content.tasks.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-tasks-card',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.CardController',
        'CMDBuildUI.view.administration.content.tasks.card.CardModel',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-tasks-card',
    viewModel: {
        type: 'view-administration-content-tasks-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        theTask: null
    },
    formBind: true,
    bind: {
        theTask: '{theTask}'
    },
    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,

    ui: 'administration-formpagination',
    items: [

    ],

    initComponent: function () {
        Ext.asap(function () {
            try {
                this.up().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {

            }
        }, this);
        this.callParent(arguments);
    },

    isValid: function () {

        return true;
    },
    addStep: function (name, index, content) {
        var vm = this.getViewModel();

        this.add({
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{hidden}'
            },
            layout: {
                type: 'card'
            },
            hidden: true,

            name: name,
            active: false,
            step: index,

            viewModel: {
                data: {
                    hidden: true
                },
                formulas: {
                    hideManager: {
                        bind: {
                            currentStep: '{currentStep}'
                        },
                        get: function (data) {
                            var myStep = this.getView().step;
                            if (myStep === data.currentStep) {
                                this.set('hidden', false);
                            } else {
                                this.set('hidden', true);
                            }
                        }
                    }
                }
            },

            items: content
        });
        vm.set('totalStep', vm.get('totalStep') + 1);
    }
});