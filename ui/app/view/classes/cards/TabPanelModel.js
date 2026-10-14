Ext.define('CMDBuildUI.view.classes.cards.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-tabpanel',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
        objectTypeName: null,
        objectId: null,
        objectDescription: null,
        storeinfo: {
            autoload: false
        },
        basepermissions: {
            clone: false,
            delete: false,
            edit: false
        },
        configenabled: {
            bim: false,
            relgraph: false,
            help: false
        },
        disabled: {
            attachments: true,
            card: false,
            email: true,
            history: true,
            masterdetail: true,
            notes: true,
            relations: true,
            schedules: true
        },
        permissions: {
            clone: false,
            delete: false,
            edit: false,
            relgraph: false,
            print: false,
            bim: false
        },
        hiddentools: {
            edit: true,
            delete: true,
            clone: true,
            bim: true,
            relgraph: true,
            print: true,
            open: true,
            help: true,
            dependencytree: true
        },
        emailtemplatestoevaluate: [],
        tabcounters: {
            attachments: false,
            emails: false,
            notes: false
        },
        dependencyTrees: []
    },

    formulas: {
        updateWindowTitle: {
            bind: {
                objecttypename: '{objectTypeName}',
                objectid: '{objectId}',
                objectdescription: '{objectDescription}'
            },
            get: function (data) {
                var view = this.getView();
                if (view && view.isInDetailWindow() && data.objecttypename) {
                    // set description in detail window
                    this.set(
                        'titledata.type',
                        CMDBuildUI.util.helper.ModelHelper.getClassDescription(data.objecttypename)
                    );
                    // set item description in detail window
                    if (data.objectdescription) {
                        this.set(
                            'titledata.item',
                            CMDBuildUI.util.helper.FieldsHelper.renderTextField(data.objectdescription, {
                                skipnewline: true
                            })
                        );
                    }
                }
            }
        },

        updateToolsVisibility: {
            bind: {
                action: '{action}',
                activetab: '{activetab}',
                enabledbim: '{configenabled.bim}',
                enabledrelgraph: '{configenabled.relgraph}'
            },
            get: function (data) {
                const klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(this.get('objectTypeName'));
                const enabledhelp = !Ext.isEmpty(klass.get('metadata').cm_help);
                const dependencyTrees = CMDBuildUI.util.DependencyTree.getDependencyTreeClasses(
                    this.get('objectTypeName')
                );
                const enabledDependencyTree = dependencyTrees && !Ext.isEmpty(dependencyTrees) ? true : false;
                if (enabledDependencyTree) {
                    this.set('dependencyTrees', dependencyTrees);
                }

                if (data.action === CMDBuildUI.mixins.DetailsTabPanel.actions.readonly) {
                    var isview = data.activetab === CMDBuildUI.mixins.DetailsTabPanel.actions.view;
                    this.set('hiddentools.edit', !isview);
                    this.set('hiddentools.delete', !isview);
                    this.set('hiddentools.clone', !isview);
                    this.set('hiddentools.bim', data.enabledbim ? !isview : true);
                    this.set('hiddentools.relgraph', data.enabledrelgraph ? !isview : true);
                    this.set('hiddentools.dependencytree', enabledDependencyTree ? !isview : true);
                    this.set('hiddentools.print', !isview);
                    this.set('hiddentools.help', enabledhelp ? !isview : true);
                    this.set('hiddentools.open', false);
                } else {
                    this.set('hiddentools.edit', false);
                    this.set('hiddentools.delete', false);
                    this.set('hiddentools.clone', false);
                    this.set('hiddentools.bim', !data.enabledbim);
                    this.set('hiddentools.relgraph', !data.enabledrelgraph);
                    this.set('hiddentools.dependencytree', !enabledDependencyTree);
                    this.set('hiddentools.print', false);
                    this.set('hiddentools.help', !enabledhelp);
                    this.set('hiddentools.open', true);
                }
            }
        },

        updateCounters: {
            bind: {
                attachments: '{theObject._attachment_count}',
                emails: '{theObject._email_count}',
                notes: '{theObject.Notes}'
            },
            get: function (data) {
                this.set('tabcounters', {
                    attachments: data.attachments,
                    emails: data.emails,
                    notes: !!CMDBuildUI.util.Utilities.extractTextFromHTML(data.notes || null)
                });
            }
        },

        updatePermissions: {
            bind: {
                clone: '{theObject._model.' + CMDBuildUI.model.base.Base.permissions.clone + '}',
                delete: '{theObject._model.' + CMDBuildUI.model.base.Base.permissions.delete + '}',
                edit: '{theObject._model.' + CMDBuildUI.model.base.Base.permissions.edit + '}'
            },
            get: function (data) {
                // set base permissions
                this.set('basepermissions', {
                    clone: data.clone,
                    delete: data.delete,
                    edit: data.edit
                });
            }
        }
    }
});
