Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.FilterController', {
    extend: 'CMDBuildUI.view.filters.attachments.PanelController',
    alias: 'controller.administration-content-emails-templates-card-attachments-filter',

    /**
     * @override
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.attachments.Filter} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel();

        var promises = [];
        Ext.getStore('dms.DMSCategoryTypes').each(function (categoryType) {
            promises.push(categoryType.getCategoryValues());
        });


        Ext.Promise.all(
            promises
        ).then(function (categories) {
            var categoriesStoreData = [];
            var modelsNames = [];

            categories.forEach(function (category) {
                category.getData().items.forEach(function (value) {
                    Ext.Array.include(modelsNames, value.get("modelClass"));
                    categoriesStoreData.push({
                        label: value.get("_description_translation"),
                        value: value.get("_id"),
                        model: value.get("modelClass"),
                        _type: value.get('_type')
                    });

                });
            });

            // set cagetories store data
            vm.set("catetoriesvalues", categoriesStoreData);

            // // prepare store data
            if (modelsNames.length > 0) {
                var promises = [],
                    hierachies = {},
                    multipleModels = modelsNames.length > 1;

                // add promises for DMSModel model generator
                modelsNames.forEach(function (modelName) {
                    // add promise to get DMS model model
                    promises.push(
                        CMDBuildUI.util.helper.ModelHelper.getModel(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                            modelName
                        )
                    );

                    // if there are multiple models get model hierarchy
                    if (multipleModels) {
                        var modelDefinition = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);
                        hierachies[modelName] = modelDefinition.getHierarchy();
                    }
                });

                // get common model for multiple models
                if (multipleModels) {
                    var intersect = Ext.Array.intersect.apply(null, Ext.Object.getValues(hierachies)),
                        commonSuperModel = intersect[intersect.length - 1];

                    Ext.Array.insert(promises, 0, [CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                        commonSuperModel
                    )]);
                }

                // generate models
                Ext.Promise.all(promises).then(function (models) {
                    var commonModel = Ext.Array.splice(models, 0, 1),
                        commonFields = commonModel[0].getFields().filter(function (field) {
                            return field.name === 'Code' || field.name === 'Description';
                        }),
                        commonFieldsNames = [],
                        modelsFields = {},
                        storedata = [],
                        allfields = {};

                    // get common fields names
                    commonFields.forEach(function (cf) {
                        var name = cf.name;
                        commonFieldsNames.push(name);

                        if (!Ext.String.startsWith(name, "_") && !cf.hidden && !cf.attributeconf.hideInFilter) {
                            storedata.push({
                                value: name,
                                label: cf.attributeconf.description_localized || cf.description,
                                category_description: null
                            });

                            allfields[name] = cf;
                        }
                    });

                    storedata.push({
                        value: "Category",
                        label: CMDBuildUI.locales.Locales.attachments.category,
                        category_description: null
                    });

                    allfields.Category = {
                        cmdbuildtype: 'dmscategory',
                        attributeconf: {
                            _description_translation: CMDBuildUI.locales.Locales.attachments.category
                        }
                    };

                    if (multipleModels) {
                        // get specific fields for other models
                        models.forEach(function (model) {
                            var modelFields = model.getFields(),
                                fields = [];
                            modelFields.forEach(function (field) {
                                if (
                                    !Ext.String.startsWith(field.name, "_") &&
                                    !field.hidden &&
                                    !field.attributeconf.hideInFilter &&
                                    Ext.Array.indexOf(commonFieldsNames, field.name) === -1
                                ) {
                                    fields.push(field);
                                }
                            });
                            if (fields.length) {
                                modelsFields[model.objectTypeName] = fields;
                            }
                        });

                        // add missinig fields for categories
                        categoriesStoreData.forEach(function (category) {
                            var fields = modelsFields[category.model];
                            if (fields) {
                                fields.forEach(function (field) {
                                    var v = me.getCategoryValueId(category.value, field.name);
                                    storedata.push({
                                        value: v,
                                        label: field.attributeconf.description_localized || field.description,
                                        category_description: category.label,
                                        category: category.value,
                                        model: category.model
                                    });
                                    allfields[v] = field;
                                });
                            }
                        });
                    }

                    vm.set("allfields", allfields);
                    vm.set("metadatavalues", storedata);

                    me._populateWithFilterData(allfields, categoriesStoreData);
                });
            }
        });
    }

});