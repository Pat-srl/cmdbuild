Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-card-form',

    control: {
        '#': {
            afterlayout: 'onAfterLayout',
            afterrender: 'onAfterRender',
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     *
     * @param {Ext.container.Container} container
     * @param {Ext.layout.container.Container} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (container, layout, eOpts) {
        const vm = this.getViewModel();
        vm.getParent().set(
            'title',
            vm.get('objectTypeName') + ' - ' + 'geo attribute' + ' - ' + vm.get('theGeoAttribute.name')
        );
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.geoattributes.card.Form} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const theGeoAttribute = vm.get('theGeoAttribute');

        vm.set('initialVisibility', { ...theGeoAttribute.get('visibility') });

        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get('objectTypeName'));
        obj.getAttributes().then(function (attributesStore) {
            const linkAttributes = [];
            attributesStore.each(function (item) {
                if (item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file) {
                    linkAttributes.push({
                        value: item.get('name'),
                        label: item.get('description')
                    });
                }
            });
            vm.set('linkAttributes', linkAttributes);
        });

        if (theGeoAttribute) {
            if (!theGeoAttribute.getAssociatedData().style) {
                theGeoAttribute.set('style', CMDBuildUI.model.map.GeoAttributeStyle.create().getData());
            }

            const childrens = [];
            const promises = [
                CMDBuildUI.util.administration.helper.TreeClassesHelper.appendClasses(
                    true,
                    theGeoAttribute.get('visibility'),
                    true,
                    true,
                    [theGeoAttribute.get('owner_type')]
                ).then(
                    function (classes) {
                        childrens[0] = classes;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Classes store NOT LOADED!');
                    }
                )
            ];

            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled)) {
                promises.push(
                    CMDBuildUI.util.administration.helper.TreeClassesHelper.appendProcesses(
                        true,
                        theGeoAttribute.get('visibility'),
                        true,
                        true,
                        [theGeoAttribute.get('owner_type')]
                    ).then(
                        function (processes) {
                            childrens[1] = processes;
                        },
                        function () {
                            CMDBuildUI.util.Msg.alert('Error', 'Processes store NOT LOADED!');
                        }
                    )
                );
            }

            Ext.Promise.all(promises).then(function () {
                const tree = {
                    text: 'Root',
                    expanded: true,
                    children: childrens
                };
                vm.set('treeStoreData', tree);
            });
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.geoattributes.card.Form} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        if (this.getViewModel().get('actions.edit')) {
            const nameInput = view.down('[name="name"]');
            if (nameInput) {
                nameInput.vtype = undefined;
            }
        }
    },

    /**
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        const vm = input.lookupViewModel();
        const file = input.fileInputEl.dom.files[0];
        const reader = new FileReader();

        reader.addEventListener(
            'load',
            function () {
                vm.get('theGeoAttribute').set('style._iconPath', reader.result);
                input.up().down('#geoAttributeIconPreview').setSrc(reader.result);
            },
            false
        );

        if (file) {
            reader.readAsDataURL(file);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(
                vm.get('objectTypeName'),
                vm.get('theGeoAttribute.name')
            );

        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm,
            true
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        const me = this;
        const vm = me.getViewModel();
        const form = me.getView();
        if (form.isValid()) {
            const theGeoAttribute = vm.get('theGeoAttribute');

            theGeoAttribute.set('style', theGeoAttribute.getAssociatedData().style);
            if (Ext.isEmpty(theGeoAttribute.get('owner_type'))) {
                theGeoAttribute.set('owner_type', vm.get('objectTypeName'));
            }
            if (theGeoAttribute.get('type') !== CMDBuildUI.model.map.GeoAttribute.type.geometry) {
                theGeoAttribute.set('subtype', '');
            }
            delete theGeoAttribute.data.style.id;
            const objectType = vm.get('objectType').toLowerCase();

            theGeoAttribute.save({
                success: function (record, operation) {
                    me.saveDescriptionTranslation().then(function () {
                        const tabPanel = vm.get('tabpanel');
                        const layersVm = tabPanel
                            .down(
                                Ext.String.format(
                                    'administration-content-{0}-tabitems-layers-layers',
                                    Ext.util.Inflector.pluralize(objectType)
                                )
                            )
                            .getViewModel();
                        if (layersVm && layersVm.getStore('layersStore')) {
                            layersVm.getStore('layersStore').load();
                        }
                        Ext.GlobalEvents.fireEventArgs('geoattributeupdated', [record]);
                        button.setDisabled(false);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    });
                },
                failure: function () {
                    me.clearGeoAttribute();
                    button.setDisabled(false);
                }
            });
        } else {
            // TODO: show some message
            button.setDisabled(false);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.clearGeoAttribute();
        this.getView().up().fireEvent('closed');
    },

    privates: {
        /**
         * Execute promise for description translation
         */
        saveDescriptionTranslation: function () {
            const deferred = new Ext.Deferred();
            const vm = this.getViewModel();
            const key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(
                vm.get('objectTypeName'),
                vm.get('theGeoAttribute.name')
            );
            // save the translation
            if (vm.get('theDescriptionTranslation')) {
                vm.get('theDescriptionTranslation').crudState = 'U';
                vm.get('theDescriptionTranslation').crudStateWas = 'U';
                vm.get('theDescriptionTranslation').phantom = false;
                vm.get('theDescriptionTranslation').set('_id', key);
                vm.get('theDescriptionTranslation').save({
                    success: function (translations, operation) {
                        deferred.resolve();
                    }
                });
            } else {
                deferred.resolve();
            }
            return deferred.promise;
        },

        /**
         * Clear values of geoattribute
         */
        clearGeoAttribute: function () {
            const vm = this.getViewModel();
            const theGeoAttribute = vm.get('theGeoAttribute');
            theGeoAttribute.reject();
            theGeoAttribute._style.reject();
            if (vm.get('geoattributesStore')) {
                const record = vm.get('geoattributesStore').findRecord('_id', theGeoAttribute.getId());
                if (record) {
                    record.set('visibility', vm.get('initialVisibility'));
                }
            }
        }
    }
});
