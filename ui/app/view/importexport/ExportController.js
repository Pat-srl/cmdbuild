Ext.define('CMDBuildUI.view.importexport.ExportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.importexport-export',

    control: {
        '#exportbtn': {
            click: 'onExportBtnClick'
        },
        '#tplcombo': {
            change: 'onTplComboChange'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onExportBtnClick: function (btn, eOpts) {
        const view = this.getView();
        if (view.closePopup) {
            const vm = view.lookupViewModel();
            const url = vm.get('exporturl');
            const selected = vm.get("selectedTemplate");
            let params;
            // get file name
            const filename = Ext.String.format(
                "{0}.{1}",
                Ext.util.Format.uri(selected.get("description")),
                selected.get("fileFormat")
            )
            // get filter
            if (!vm.get("isDomainExport") && vm.get("values.export") === 'filtered' && !Ext.isEmpty(view.getFilter())) {
                params = {
                    filter: view.getFilter()
                };
            }
            // download
            CMDBuildUI.util.File.download(url, filename, false, params).then(function () {
                Ext.asap(view.closePopup, 250);
            });
        }
    },

    /**
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {Number} newvalue
     * @param {Number} oldvalue
     * @param {Object} eOpts
     */
    onTplComboChange: function (combo, newvalue, oldvalue, eOpts) {
        const vm = this.getViewModel();
        if (newvalue) {
            combo.lookupViewModel().set("exporturl", Ext.String.format(
                "{0}/etl/templates/{1}/export?_download=true",
                CMDBuildUI.util.Config.baseUrl,
                newvalue
            ));

            vm.set("isDomainExport", vm.get("selectedTemplate").get("targetType") == CMDBuildUI.util.helper.ModelHelper.objecttypes.domain);
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (btn, eOpts) {
        const view = this.getView();
        if (view.closePopup) {
            view.closePopup();
        }
    }
});
