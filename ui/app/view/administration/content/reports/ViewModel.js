Ext.define('CMDBuildUI.view.administration.content.reports.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-reports-view',

    data: {
        theTranslation: false,
        // actions: {
        //     view: false,
        //     edit: false,
        //     add: false
        // },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_reports_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify === true);
                this.set('toolAction._canUpdate', canModify === true);
                this.set('toolAction._canDelete', canModify === true);
                this.set('toolAction._canActiveToggle', canModify === true);
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
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
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
        processingModes: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getExecutionModes();
        },
        theReportManager: {
            bind: '{theReport}',
            get: function (theReport) {
                if (theReport) {
                    const rawIconFont = theReport.get('icon_font');
                    if (rawIconFont && rawIconFont.indexOf(':') !== -1) {
                        theReport.set('icon_font', CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont));
                        theReport.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }
                }
            }
        }
    },

    stores: {
        processingModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{processingModes}'
        }
    }
});
