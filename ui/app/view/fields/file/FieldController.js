Ext.define('CMDBuildUI.view.fields.file.FieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-file-field',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'filefield': {
            'change': 'onFileFieldChange'
        },
        '#cleartool': {
            click: 'onClearToolClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.file.Field} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var linkName = view.getRecordLinkName(),
            fieldset = view.down("formpaginationfieldset");

        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
            view.metadata.dmsModel
        ).then(function (model) {

            if (linkName) {
                var vm = view.lookupViewModel();

                vm.set("filedata.status", view.statuses.loaded());

                // bind on value
                vm.bind('{' + linkName + '.' + view.name + '}', function (value) {
                    vm.set("filedata.hidden", !value);
                    if (value && !fieldset._alreadyexpanded) {
                        fieldset.expand();
                        fieldset._alreadyexpanded = true;
                    }
                });

                function updateExtension(extension) {
                    if (Ext.isArray(extension)) {
                        var exts = [];
                        extension.forEach(function (item) {
                            exts.push('.' + item);
                        });
                        extension = exts.join(',');
                    }
                    vm.set("filedata.extension", extension);
                    view.updateAllowedExtensions(extension);
                }

                // bind on filename
                vm.bind({
                    filename: '{' + linkName + '._' + view.name + '_name}',
                    size: '{' + linkName + '._' + view.name + '_Size}'
                }, function (data) {
                    var extension;
                    if (data.filename && !vm.get("filedata.istemp")) {
                        var sfilename = data.filename.split(".");
                        extension = "." + sfilename[sfilename.length - 1];
                        vm.set("filedata.name", data.filename);
                        vm.set("filedata.loaded", true);
                        updateExtension(extension);
                    } else {
                        var dmsCategory = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryType(vm.get('classObject.name'), vm.get('objectType')),
                            dmsCategoryValue = view.config.metadata.dmsCategory;
                        dmsCategory.getCategoryValues().then(function (categoryValuesStore) {
                            var dmsCategoryValueId = categoryValuesStore.findRecord('code', dmsCategoryValue).getId();
                            extension = CMDBuildUI.util.helper.AttachmentsHelper.getAllowedExtensions(dmsCategory.data.name, dmsCategoryValueId, model);
                            updateExtension(extension);
                        });
                    }
                });
            }

            var basebind = "{" + linkName + "._" + view.name + "_{0}}",
                fields = CMDBuildUI.util.helper.FormHelper.getFormFields(model, {
                    mode: view.formmode
                });
            fields.forEach(function (field) {
                field.bind.value = Ext.String.format(basebind, field.name);
                if (!field.hidden) {
                    vm.set("filedata.fieldsethidden", false);
                }
            });
            fieldset.add(fields);
        });
    },

    /**
     * @param {Ext.form.field.File} field
     * @param {String} value
     * @param {Object} eOpts
     */
    onFileFieldChange: function (field, value, eOpts) {
        if (value) {
            var view = this.getView(),
                vm = view.lookupViewModel(),
                file = field.fileInputEl.dom.files[0],
                fieldset = view.down("formpaginationfieldset"),
                alreadyPresent = false;

            function fileChange() {
                field.disable();
                vm.set("filedata.name", file.name);
                vm.set("filedata.hidden", false);
                vm.set("filedata.status", view.statuses.loading());

                CMDBuildUI.util.File.uploadFileWithMetadata(
                    "POST",
                    CMDBuildUI.util.Config.baseUrl + "/uploads/_TEMP",
                    file
                ).then(function (response) {
                    view.setFieldValue(response._id, response.name, response.size);
                    vm.set("filedata.istemp", true);
                    vm.set("filedata.loaded", true);
                    if (alreadyPresent) {
                        vm.set("filedata.status", view.statuses.alreadypresent(vm.get("filedata.name")));
                        vm.set("filedata.fieldsethidden", true);
                    } else {
                        vm.set("filedata.status", view.statuses.ready());
                        vm.set("filedata.fieldsethidden", false);
                        view.updateAllowedExtensions(vm.get("filedata.extension"));
                        fieldset.expand();
                    }
                    field.reset();
                    field.enable();
                });
            }

            if (vm.get("objectId")) {
                Ext.Ajax.request({
                    url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Classes.getAttachments(vm.get("objectTypeName"), vm.get("objectId")),
                    method: "GET",
                    success: function (response) {
                        var data = Ext.JSON.decode(response.responseText).data;
                        alreadyPresent = Ext.Array.findBy(data, function (item, index) {
                            return item.name === file.name;
                        });
                        fileChange();
                    }
                })
            } else {
                fileChange();
            }
        }
    },

    /**
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.container.Container} owner
     * @param {Object} eOpts
     */
    onClearToolClick: function (tool, e, owner, eOpts) {
        var view = this.getView(),
            vm = tool.lookupViewModel(),
            fieldset = view.down("formpaginationfieldset");
        vm.set("filedata.loaded", false);
        vm.set("filedata.status", null);
        vm.set("filedata.name", null);
        if (vm.get("filedata.istemp")) {
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + "/uploads/_TEMP/" + view.getValue(),
                method: "DELETE"
            });
        }
        view.setFieldValue();
        Ext.Array.forEach(fieldset.items.items, function (item, index, allitems) {
            if (item.isDirty()) {
                item.reset();
            }
        });
        fieldset.collapse();
        view.validate();
    }
});
