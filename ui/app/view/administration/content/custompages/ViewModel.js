Ext.define('CMDBuildUI.view.administration.content.custompages.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-custompages-view',

    data: {
        theTranslation: false,
        target: null,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        hasMobile: false,
        hasDefault: false,
        mobileRemoved: false,
        defaultRemoved: false
    },
    formulas: {
        hideMobileFields: {
            get: function () {
                return !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled);
            }
        },
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_uicomponents_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify === true);
                this.set('toolAction._canUpdate', canModify === true);
                this.set('toolAction._canDelete', canModify === true);
                this.set('toolAction._canActiveToggle', canModify === true);
            }
        },
        targetManager: {
            bind: '{theCustompage.device}',
            get: function (target) {
                this.set('target', target);
            }
        },
        custompageLabel: {
            bind: '{theCustompage.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.custompages.singular;
            }
        },

        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.getParent().toggleEnableTabs(0);
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        customPageManager: {
            bind: '{theCustompage}',
            get: function (theCustompage) {
                if (!theCustompage.phantom) {
                    var devices = theCustompage.get('devices');
                    this.set('hasDefault', devices.indexOf(CMDBuildUI.model.menu.Menu.device['default']) > -1);
                    this.set('hasMobile', devices.indexOf(CMDBuildUI.model.menu.Menu.device.mobile) > -1);
                }
            }
        },
        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                if (data.isView && !data.isHiddenForm) {
                    return false;
                }
                return true;
            }
        },
        targetDevices: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getCustomUITargetDevices();
            }
        },
        theCustompageManager: {
            bind: '{theCustompage}',
            get: function (theCustompage) {
                if (theCustompage) {
                    const rawIconFont = theCustompage.get('icon_font');
                    if (rawIconFont) {
                        theCustompage.set(
                            'icon_font',
                            CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                        );

                        theCustompage.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }
                }
            }
        }
    },
    stores: {
        targetDevicesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            sorters: ['label'],
            data: '{targetDevices}',
            proxy: {
                type: 'memory'
            }
        }
    },
    /**
     * Change form mode
     *
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        me.set('action', mode);
        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:
                me.set('actions.view', true);
                me.set('actions.edit', false);
                me.set('actions.add', false);
                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.add:
                me.set('actions.view', false);
                me.set('actions.edit', false);
                me.set('actions.add', true);
                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                me.set('actions.add', false);
                break;
        }
    }
});
