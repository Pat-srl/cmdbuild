Ext.define('CMDBuildUI.view.processes.instances.instance.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-view',

    data: {
        hiddenbtns: {
            open: true,
            relgraph: true,
            opentabs: true
        }
    },

    formulas: {
        title: function (get) {
            return null; // return null to hide header
        },
        popupTitle: {
            bind: {
                instance: '{theObject.Description}',
                activity: '{theActivity._description_translation}'
            },
            get: function (data) {
                var vm = this.getParent().getParent();
                if (vm) {
                    vm.set("titledata.item",
                        Ext.String.format("{0} &mdash; {1}",
                            CMDBuildUI.util.helper.FieldsHelper.renderTextField(data.instance, {
                                skipnewline: true
                            }),
                            data.activity
                        )
                    );
                }
            }
        },
        updatePermissions: {
            bind: {
                activity: '{theActivity}'
            },
            get: function (data) {
                var item = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(this.get("objectTypeName"));
                if (data.activity && data.activity.get("writable")) {
                    var can_delete = false;
                    if (
                        CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges").admin_access ||
                        item.get("stoppableByUser")
                    ) {
                        can_delete = true;
                    }
                    this.getParent().set("basepermissions", {
                        edit: true,
                        delete: can_delete,
                        relgraph: item.get(CMDBuildUI.model.base.Base.permissions.relgraph)
                    });

                } else {
                    this.getParent().set("basepermissions", {
                        edit: false,
                        delete: false,
                        relgraph: item.get(CMDBuildUI.model.base.Base.permissions.relgraph)
                    });
                }
                this.getParent().set("permissions.relgraph", item.get(CMDBuildUI.model.base.Base.permissions.relgraph))
                this.getParent().set("configenabled.relgraph", (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.enabled)));
            }
        }
    }

});