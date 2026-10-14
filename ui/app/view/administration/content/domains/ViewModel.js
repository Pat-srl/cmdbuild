Ext.define('CMDBuildUI.view.administration.content.domains.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-view',

    data: {
        isSimpleClass: false,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true,
            delete: true
        },
        theDomainDescription: null,
        theDomainDescriptionTranslation: null,
        theDirectDescriptionTranslation: null,
        theInverseDescriptionTranslation: null,
        theMasterDetailTranslation: null,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_domains_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify);
                this.set('toolAction._canUpdate', data.canModify);
                this.set('toolAction._canDelete', data.canModify);
                this.set('toolAction._canActiveToggle', data.canModify);
            }
        },

        domainLabel: {
            bind: '{theDomain.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.domains.singularTitle;
            }
        },

        action: {
            bind: {
                theDomain: '{theDomain}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
            get: function (data) {
                if (data.isEdit) {
                    data.theDomain.getAttributes();
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    data.theDomain.getAttributes();
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        updateToolbarButtons: {
            bind: '{theDomain.active}',
            get: function (active) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                if (active) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        },

        theDomainDestination: {
            bind: '{theDomain}',
            get: function (domain) {
                const storeId = domain.destinationProcess ? 'processes.Processes' : 'classes.Classes';
                const record = Ext.getStore(storeId).getById(domain.get('destination'));
                return record && record.get('description');
            }
        },

        theDomainSource: {
            bind: '{theDomain}',
            get: function (domain) {
                const storeId = domain.destinationProcess ? 'processes.Processes' : 'classes.Classes';
                const record = Ext.getStore(storeId).getById(domain.get('source'));
                return record && record.get('description');
            }
        }
    }
});
