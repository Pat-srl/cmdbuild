Ext.define('CMDBuildUI.view.widgets.customform.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-customform-panel',

    data: {
        permissions: {
            add: true,
            clone: true,
            delete: true,
            export: true,
            import: true,
            modify: true,
            refresh: true
        }
    },

    formulas: {
        updatePermissions: {
            bind: {
                theWidget: '{theWidget}'
            },
            get: function (data) {
                var readonly = data.theWidget.get('ReadOnly') === true || data.theWidget.get('ReadOnly') === 'true';
                // add permission
                if (
                    readonly ||
                    (data.theWidget.get('AddDisabled') && data.theWidget.get('AddDisabled').toString() === 'true')
                ) {
                    this.set('permissions.add', false);
                } else {
                    this.set('permissions.add', true);
                }
                // clone permission
                if (
                    readonly ||
                    (data.theWidget.get('CloneDisabled') && data.theWidget.get('CloneDisabled').toString() === 'true')
                ) {
                    this.set('permissions.clone', false);
                } else {
                    this.set('permissions.clone', true);
                }
                // delete permission
                if (
                    readonly ||
                    (data.theWidget.get('DeleteDisabled') && data.theWidget.get('DeleteDisabled').toString() === 'true')
                ) {
                    this.set('permissions.delete', false);
                } else {
                    this.set('permissions.delete', true);
                }
                // export permission
                if (
                    readonly ||
                    (data.theWidget.get('ExportDisabled') && data.theWidget.get('ExportDisabled').toString() === 'true')
                ) {
                    this.set('permissions.export', false);
                } else {
                    this.set('permissions.export', true);
                }
                // import permission
                if (
                    readonly ||
                    (data.theWidget.get('ImportDisabled') && data.theWidget.get('ImportDisabled').toString() === 'true')
                ) {
                    this.set('permissions.import', false);
                } else {
                    this.set('permissions.import', true);
                }
                // modify permission
                if (
                    readonly ||
                    (data.theWidget.get('ModifyDisabled') && data.theWidget.get('ModifyDisabled').toString() === 'true')
                ) {
                    this.set('permissions.modify', false);
                } else {
                    this.set('permissions.modify', true);
                }
                // refresh permission
                if (
                    readonly ||
                    (data.theWidget.get('RefreshHidden') && data.theWidget.get('RefreshHidden').toString() === 'true')
                ) {
                    this.set('permissions.refresh', false);
                } else {
                    this.set('permissions.refresh', true);
                }
            }
        }
    }
});
