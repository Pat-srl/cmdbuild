Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.Topbar', {
    extend: 'Ext.toolbar.Toolbar',

    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-permissions'
    },
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
    dock: 'top',
    forceFit: true,
    loadMask: true,
    border: false,
    style: {
        borderColor: '#fff'
    },
    items: [{
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchingrid,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchingrid'
        },
        hidden: true,
        bind: {
            hidden: '{(objectType === "classes" && grantHierarchicalView.classes) || (objectType === "processes" && grantHierarchicalView.processes)}'
        },
        cls: 'administration-input',
        reference: 'searchtext',
        itemId: 'searchtext',
        listeners: {
            change: 'onSearchChange'
        },
        triggers: {
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-groupandpermission-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-groupandpermission-toolbar-search-form'
        }
    }, '->', {
        xtype: 'button',
        align: 'right',
        itemId: 'copyFrom',
        reference: 'copyfrom',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
        cls: 'administration-tool',
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom,
        tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom',
            tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom'
        },
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },

        menu: {
            items: []
        },
        visible: false,
        autoEl: {
            'data-testid': 'administration-process-properties-tool-clone'
        }
    },{
        xtype: 'checkbox',
        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.hierarchicalview,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.hierarchicalview'
        },
        labelAlign: 'left',
        labelStyle: 'width:auto',
        labelWidth: false,    
        hidden: true,
        bind: {
            hidden: '{hierachicalViewHidden}'                 
        },
        listeners: {
            afterrender: function(){                
                var vm = this.up('administration-content').lookupViewModel();                
                if(this.up('administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes')){
                    this.setValue(vm.get('grantHierarchicalView.classes'));
                }else if(this.up('administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes')){
                    this.setValue(vm.get('grantHierarchicalView.processes'));
                }
            },
            change: 'onHierarchicalViewCheckChange'
        }
    }, {
        xtype: 'tool',
        align: 'right',
        itemId: 'editBtn',
        cls: 'administration-tool',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        callback: 'onEditBtnClick',
        hidden: true,
        autoEl: {
            'data-testid': 'administration-groupandpermission-permission-tool-editbtn'
        },
        bind: {
            hidden: '{actions.edit}',
            disabled: '{!toolAction._canUpdate}'
        }
    }]
});