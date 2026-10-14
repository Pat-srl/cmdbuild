Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.Panel', {
    extend: 'Ext.form.Panel',
    requires: [
        'CMDBuildUI.view.filters.attributes.Panel',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.PanelController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.PanelModel'
    ],

    alias: 'widget.administration-attributes-fieldsmanagement-panel',
    controller: 'administration-attributes-fieldsmanagement-panel',
    viewModel: {
        type: 'administration-attributes-fieldsmanagement-panel'
    },
    scrollable: 'y',
    config: {
        groups: [],

        attributes: [],

        theComposer: null,

        objectTypeName: null, // TODO verify if is needed enymore

        objectId: null, // TODO verify if is needed enymore

        activity: null,

        maskCount: 0,

        mymask: null
    },
    items: [],

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: {
                        bind: {
                            disabled: '{!toolAction._canUpdate}'
                        }
                    },
                    activeToggle: {
                        bind: {
                            disabled: '{!toolAction._canUpdate}'
                        }
                    },
                    delete: {
                        bind: {
                            disabled: '{!toolAction._canUpdate}'
                        }
                    }
                }, 'fieldsmanagement', 'theFormStructure',
                [],
                [{
                    xtype: 'tool',
                    itemId: 'layoutAutogenBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('magic', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.forms.autogenerate,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.forms.autogenerate'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-layoutAutogenBtn', 'fieldsmanagement')
                    },
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                }])
        }, {
            hidden: true,
            bind: {
                hidden: '{!showClosedTaskMessage}'
            },
            margin: 10,
            xtype: 'container',
            ui: 'messagewarning',
            items: [{
                flex: 1,
                ui: 'custom',
                xtype: 'panel',
                html: CMDBuildUI.locales.Locales.administration.processes.texts.formclosedactivitymessage,
                localized: {
                    html: 'CMDBuildUI.locales.Locales.administration.processes.texts.formclosedactivitymessage'
                }
            }]
        }]

    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false, {
            testid: 'fieldsmanagement'
        }, {
            testid: 'fieldsmanagement'
        })
    }],

    addMask: function () {
        if (!this.getMaskCount()) {
            CMDBuildUI.util.Utilities.showLoader(true, this);
        }
        this.setMaskCount(this.getMaskCount() + 1);
    },
    removeMask: function (force) {
        this.setMaskCount(this.getMaskCount() - 1);
        if (force) {
            this.setMaskCount(0);
        }
        if (!this.getMaskCount() || this.getMaskCount() < 0) {
            CMDBuildUI.util.Utilities.showLoader(false, this);
        }
    }

});