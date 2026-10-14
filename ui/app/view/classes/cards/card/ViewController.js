Ext.define('CMDBuildUI.view.classes.cards.card.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();
        if (!view.getObjectTypeName() && !view.getObjectId()) {
            const config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                const record = config._rowContext.record; // get widget record
                if (record && record.getData()) {
                    // view.setObjectTypeName(record.getRecordType());
                    // view.setObjectId(record.getRecordId());
                    vm.set('objectTypeName', record.getRecordType());
                    vm.set('objectId', record.getRecordId());
                    vm.set('theObject', record);
                }
            }
        }

        // if the object is not defined on parent
        if (!(vm.get('theObject') && vm.get('objectModel')) && !vm.get('diffMode')) {
            // bind object type name and object id
            // to get model and load card data
            vm.bind(
                {
                    bindTo: {
                        objectTypeName: '{objectTypeName}',
                        objectId: '{objectId}'
                    }
                },
                this.onObjectTypeNameAndIdChanged,
                this
            );
        }

        // bind card data load to show the form
        vm.bind(
            {
                bindTo: {
                    theobjecttype: '{theObject._type}',
                    theobjectid: '{theObject._id}',
                    objectmodel: '{objectModel}'
                }
            },
            function (data) {
                Ext.asap(function () {
                    me.onObjectLoaded(data);
                });
            },
            this
        );
    },

    privates: {
        /**
         *
         * @param {Object} data
         * @param {String} data.objectTypeName
         * @param {Number|String} data.objectId
         */
        onObjectTypeNameAndIdChanged: function (data) {
            const vm = this.getViewModel();
            if (data.objectTypeName && data.objectId) {
                CMDBuildUI.util.helper.ModelHelper.getModel(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                    data.objectTypeName
                ).then(function (model) {
                    if (vm.getData()) {
                        vm.set('objectModel', model);

                        vm.linkTo('theObject', {
                            type: model.getName(),
                            id: data.objectId
                        });
                    }
                });
            }
        },

        /**
         *
         * @param {Object} data
         * @param {String} data.theobjecttype
         * @param {CMDBuildUI.model.classes.Card} data.objectmodel
         */
        onObjectLoaded: function (data) {
            const view = this.getView();
            const vm = this.getViewModel();
            if (data.theobjecttype && data.objectmodel) {
                let items = [];
                if (view.getShownInPopup()) {
                    // get form fields as fieldsets
                    items = view.getDynFormFields();
                    items = view.getMainPanelForm(items, view.getHideTools());
                } else {
                    // get form fields as tab panel
                    const klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                        vm.get('objectTypeName'),
                        vm.get('objectType')
                    );
                    const grouping = klass.attributeGroups().getRange();
                    let layout;
                    if (klass.get('formStructure') && klass.get('formStructure').active) {
                        layout = klass.get('formStructure').form;
                    }
                    const panel = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get('objectModel'), {
                        mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                        showAsFieldsets: false,
                        grouping: grouping,
                        layout: layout,
                        formValidation: klass.get('validationRule')
                    });

                    if (!view.getHideTools()) {
                        // add toolbar
                        Ext.apply(panel, {
                            tools: view.tabpaneltools
                        });
                    }

                    items.push(panel);
                }
                view.removeAll(true);
                view.setHtml();
                view.add(items);
            }
        }
    }
});
