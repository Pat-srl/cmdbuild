Ext.define('CMDBuildUI.view.fields.allelementscombo.AllelementsCombo', {

    extend: 'Ext.form.field.ComboBox',

    alias: 'widget.allelementscombo',

    viewModel: {

    },

    config: {
        /**
         * @cfg {Boolean} withClasses true for add classes to store data
         */
        withStandardClasses: null,
        /**
         * @cfg {Boolean} withClasses true for add classes to store data
         */
        withClasses: null,
        /**
        * @cfg {Boolean} onlyChildClasses true for filter only non prototype class
         */
        onlyChildClasses: false,
        /**
         * @cfg {Boolean} withProcesses true for add processes to store data
         */
        withProcesses: null,
        /**
        * @cfg {Boolean} onlyChildProcesses true for filter only non prototype process
         */
        onlyChildProcesses: false,
        /**
         * @cfg {Boolean} withDashboards true for add dashboards to store data
         */
        withDashboards: null,
        /**
         * @cfg {Boolean} withCustompages true for add custompages to store data
         */
        withCustompages: null,
        /**
         * @cfg {Boolean} withViews true for add views to store data
         */
        withViews: null,
        /**
         * @cfg {Boolean} withDMSModels true for add DMS models to store data
         */
        withDMSModels: null,
        /**
         * @cfg {Boolean} withMenuNavTrees true for add menu navigation trees to store data
         */
        withMenuNavTrees: null,
        /**
         * @cfg {Boolean} typePrefix true for add objectType before 
         * objectTypeName (Ex. class:MyClassName) to store data
         */
        typePrefix: null,

        /**
         * @cfg {Boolean} showTranslatedLabels
         * `true` to show translated descriptions
         */
        showTranslatedDescriptions: false
    },

    valueField: '_id',

    displayField: 'label',

    queryMode: 'local',

    forceSelection: false,

    typeAhead: true,

    reference: 'allelementscombo',

    autoEl: {
        'data-testid': 'allements-input'
    },

    store: {
        proxy: {
            type: 'memory'
        },
        data: [],
        autoDestroy: true
    },
    triggers: {
        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger(function (combo) {
            if (combo.isExpanded) {
                combo.doLocalQuery("");
            }
            combo.lastSelectedRecords = [];
            if (combo.hasBindingValue) {
                combo.getBind().value.setValue(null);
            }
        })
    },

    tpl: new Ext.XTemplate(
        '<tpl for=".">',
        '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values)]}</div></tpl>',
        '<div class="x-boundlist-item {[this.getDisabledClass(values)]}">{label}</div>',
        '</tpl>', {
        shouldShowHeader: function (group) {
            return this.currentGroup !== group;
        },
        showHeader: function (group) {
            this.currentGroup = group.group;
            return group.groupLabel;
        },
        getDisabledClass: function (values) {
            if (!values.active) {
                return Ext.baseCSSPrefix + 'hidden-display ' + Ext.baseCSSPrefix + 'form-data-hidden';
            }
            return '';
        }
    }),

    initComponent: function () {

        var me = this;
        var types = {};
        var promises = [];
        if (me.getWithStandardClasses()) {
            promises.push(CMDBuildUI.util.Stores.load('classes.Classes', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.klass] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    childrens: data.filter(function (item) {
                        var res = item.get('type') === CMDBuildUI.model.classes.Class.classtypes.standard;
                        if (me.getOnlyChildClasses()) {
                            return res && !item.get('prototype');
                        }
                        return res;

                    })
                };
            }));
        }
        if (me.getWithClasses()) {
            promises.push(CMDBuildUI.util.Stores.load('classes.Classes', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.klass] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    childrens: data.filter(function (item) {
                        if (me.getOnlyChildClasses()) {
                            return !item.get('prototype');
                        }
                        return true;

                    })
                };
            }));
        }

        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (me.getWithProcesses() && wfEnabled) {
            promises.push(CMDBuildUI.util.Stores.load('processes.Processes', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.process] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                    childrens: data.filter(function (item) {
                        if (me.getOnlyChildProcesses()) {
                            return !item.get('prototype');
                        }
                        return true;

                    })
                };
            }));
        }
        if (me.getWithDashboards()) {
            promises.push(CMDBuildUI.util.Stores.load('dashboards.Dashboards', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                    childrens: data
                };
            }));
        }
        if (me.getWithCustompages()) {
            promises.push(CMDBuildUI.util.Stores.load('custompages.CustomPages', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                    childrens: data
                };
            }));
        }
        if (me.getWithViews()) {
            promises.push(CMDBuildUI.util.Stores.load('views.Views', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.view] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.views,
                    childrens: data
                };
            }));
        }
        if (me.getWithDMSModels()) {
            promises.push(CMDBuildUI.util.Stores.load('dms.DMSModels', true).then(function (data) {
                types[CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.dmsmodels,
                    childrens: data
                };
            }));
        }
        if (me.getWithMenuNavTrees()) {
            promises.push(CMDBuildUI.util.Stores.load('menu.NavigationTrees', true).then(function (data) {
                types[CMDBuildUI.model.menu.MenuItem.types.navtree] = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                    childrens: data
                };
            }));
        }
        Ext.Promise.all(
            promises
        ).then(function () {
            var data = [];
            Object.keys(types).forEach(function (type, typeIndex) {

                types[type].childrens.forEach(function (value, index) {
                    var id = me.getTypePrefix() ? Ext.String.format('{0}:{1}', type, value.get('name') ? value.get('name') : value.get('_id')) : value.get('name');
                    var item = {
                        group: type,
                        groupLabel: types[type].label,
                        _id: id,
                        label: value.getTranslatedDescription(),
                        active: value.get('active')
                    };
                    data.push(item);
                });
            });
            data.sort(function (a, b) {
                var aGroup = a.group.toUpperCase();
                var bGroup = b.group.toUpperCase();
                var aLabel = a.label.toUpperCase();
                var bLabel = b.label.toUpperCase();

                if (aGroup === bGroup) {
                    return (aLabel < bLabel) ? -1 : (aLabel > bLabel) ? 1 : 0;
                } else {
                    return (aGroup < bGroup) ? -1 : 1;
                }
            });
            if (!me.destroyed) {
                me.getStore().setData(data);
                me.setValue(me.getValue());
                me.forceSelection = true;
                me.isValid();
            }
        });
        me.callParent(arguments);
    },
    listeners: {
        afterrender: function () {
            this.el.dom.dataset.testid = Ext.String.format('allements-{0}-input', this.getName() || 'noname');
        }
    }
});