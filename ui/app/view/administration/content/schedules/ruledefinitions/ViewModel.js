Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-schedules-ruledefinitions-view',
    data: {
        totalScheduleCount: 0,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_calendar_modify}'
            },
            get: function (data) {                           
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        actionManager: {
            bind: '{action}',
            get: function (action) {
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        getToolbarButtons: {
            bind: '{theSchedule.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theSchedule.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        }
    },

    stores: {
        allSchedules: {
            type: 'calendar-triggers',
            autoload: true,
            autoDestroy: true,            
            listeners: {
                datachanged: 'onAllSchedulesStoreDatachanged'
            },
            sorters: ['description']
        },
        allGroups: {
            type:'chained',
            source:'groups.Groups',
            autoDestroy: true
        },
        timeZonesStore: {
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format(
                    '{0}/timezones',
                    CMDBuildUI.util.Config.baseUrl
                )
            },
            sorters: ['description'],
            pageSize: 0,
            autoLoad: true           
        }
    }
});
