Ext.define('CMDBuildUI.view.administration.content.dependencytrees.PermissionsTabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dependencytrees-permissionstab',

    data: {
        hiddenColumns: {
            modeTypeNone: false,
            modeTypeAllow: true,
            modeTypeRead: false,
            modeTypeWFBasic: true,
            modeTypeWFPlus: true,
            modeTypeWFDefault: true,
            modeTypeWrite: true,
            actionFilter: true,
            actionResetFilter: true,
            actionActionDisabled: true,
            modeTypeNoneOther: true,
            modeTypeReadOther: true,
            modeTypeWriteOther: true
        }
    },

    formulas: {
        proxyurl: {
            bind: {
                id: '{theDependencyTree._id}',
                name: '{theDependencyTree.name}'
            },
            get: function (data) {
                if (data.id && data.name) {
                    return Ext.String.format('/roles/_ALL/grants/by-target/dependencytree/{0}', data.id);
                }
            }
        }
    },
    stores: {
        grantsChainedStore: {
            proxy: {
                type: 'baseproxy',
                url: '{proxyurl}'
            },
            model: 'CMDBuildUI.model.users.Grant',
            sorters: ['_object_description'],
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        }
    }
});
