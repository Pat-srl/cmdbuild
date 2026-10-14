Ext.define('CMDBuildUI.view.joinviews.configuration.items.FiltersController', {
    extend: 'CMDBuildUI.view.filters.attributes.PanelController',
    alias: 'controller.joinviews-configuration-items-filters',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get("attributeslist");

        vm.bind({
            bindTo: {
                currentStep: '{currentStep}',
                allAttributesStore: '{allAttributesStore}',
                action: '{action}'
            }
        }, function (data) {
            if (data.currentStep === 5) {
                var filter = Ext.create('CMDBuildUI.model.base.Filter', {
                    name: CMDBuildUI.locales.Locales.filters.newfilter,
                    description: CMDBuildUI.locales.Locales.filters.newfilter,
                    ownerType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                    target: vm.get('theView.name'),
                    configuration: vm.get('theView.filter'),
                    shared: true
                });
                var fields = {};
                store.removeAll();
                CMDBuildUI.util.helper.FiltersHelper.addOperators(store);
                data.allAttributesStore.each(function (attribute) {
                    var attrconf = attribute.get('attributeconf');
                    if (!fields[attribute.get('expr')] && !attrconf.password && !attrconf.hideInFilter) {
                        fields[attribute.get('expr')] = attribute.getData();

                        store.addSorted(Ext.create("CMDBuildUI.model.base.ComboItem", {
                            value: attribute.get('expr'),
                            label: attribute.get('_attributeDescription'),
                            group: attribute.get('targetAlias'),
                            attributeconf: attrconf
                        }));
                    }
                });

                vm.set('theFilter', filter);
                vm.set("allfields", fields);
                var config = vm.get("theFilter").get("configuration");
                if (config.attribute || !Ext.Object.isEmpty(config)) {
                    view.down("#blockitems").removeAll();
                    CMDBuildUI.util.helper.FiltersHelper.populateAttributeContainer(view, config.attribute || config);
                }

                if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                    var disable = vm.get('actions.view') ? true : false;
                    vm.set("displayOnly", disable);
                }
            }
        });
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        var vm = this.getView().lookupViewModel(),
            filter = Ext.JSON.decode(vm.get("theView.filter"), true);
        if (!Ext.isEmpty(newValue) && !Ext.Object.isEmpty(filter)) {
            this.modifyClassFilter(filter, newValue, oldValue);
        }
    },

    /**
     * @event
     * @param {Ext.data.Model} node 
     * @param {Object} context 
     */
    onDomainChange: function (record, context, eOpts) {
        var vm = this.getView().lookupViewModel(),
            filter = Ext.JSON.decode(vm.get("theView.filter"), true);

        if (!Ext.isEmpty(filter)) {
            switch (context.field) {
                case 'targetAlias':
                    this.modifyClassFilter(filter, record.get("targetAlias"), record.getPrevious("targetAlias"));
                    break;

                case 'targetType':
                    this.modifyClassFilter(filter, record.getPrevious("targetAlias"), record.getPrevious("targetAlias"));
                    break;

                default:
                    break;
            }
        }
    },

    privates: {
        modifyClassFilter: function (filter, newValue, oldValue) {
            var me = this,
                vm = this.getView().lookupViewModel(),
                newFilter = new CMDBuildUI.util.AdvancedFilter(),
                attributes = filter.attribute ? (filter.attribute.and || filter.attribute.or) : (filter.and || filter.or);
            if (!attributes) {
                attributes = [filter.attribute];
            }

            Ext.Array.forEach(attributes, function (item, index, allitems) {
                if (item && (item.and || item.or)) {
                    me.modifyClassFilter(item, newValue, oldValue);
                } else if (item && item.simple) {
                    var attr = item.simple.attribute,
                        klass = attr.split(".")[0];

                    if (klass === oldValue) {
                        if (oldValue === newValue) {
                            Ext.Array.remove(attributes, item);
                        } else {
                            attr = Ext.String.format("{0}.{1}", newValue, attr.split(".")[1]);
                            item.simple.attribute = attr;
                        }
                    }
                }
            });

            if (filter.attribute) {
                newFilter.applyAdvancedFilter(filter);
            } else {
                newFilter.applyAdvancedFilter({
                    attribute: filter
                });
            }

            vm.set("theView.filter", newFilter.encode());
        }
    }

});