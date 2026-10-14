Ext.define('CMDBuildUI.view.relations.list.Container', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.relations.list.ContainerController', 'CMDBuildUI.view.relations.list.ContainerModel'],

    alias: 'widget.relations-list-container',
    controller: 'relations-list-container',
    viewModel: {
        type: 'relations-list-container'
    },

    config: {
        /**
         * @cfg {readOnly}
         *
         * Set to `true` to shwow details tabs in read-only mode
         */
        readOnly: false,

        /**
         * @cfg {showRelGraphBtn}
         *
         * Set to `true` to shwow relations graph when view is in read-only mode
         */
        showRelGraphBtn: false,

        /**
         * @cfg {showEditCardBtn}
         *
         * Set to `true` to shwow edit card button when view is in read-only mode
         */
        showEditCardBtn: false
    },
    layout: 'card',
    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.relations.addrelations,
            reference: 'addrelationbtn',
            itemId: 'addrelationbtn',
            ui: 'management-primary-small',
            disabled: true,
            hidden: true,
            bind: {
                disabled: '{addbtn.disabled}',
                hidden: '{hiddenbtns.addbtn}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.addrelations'
            },
            autoEl: {
                'data-testid': 'relations-list-container-addbtn'
            }
        },
        {
            xtype: 'button',
            iconCls: 'cmdbuildicon-relgraph',
            ui: 'management-neutral-action-small',
            cls: 'cmdbuildicon-fix', // need for fix custom icon overflow
            width: 40,
            itemId: 'openrelgraphbtn',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.relationGraph.openRelationGraph,
            autoEl: {
                'data-testid': 'cards-card-view-bimBtn'
            },
            bind: {
                hidden: '{hiddenbtns.relgraph}',
                disabled: '{!permissions.relgraph}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relationGraph.openRelationGraph'
            }
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'checkboxfield',
            fieldLabel: CMDBuildUI.locales.Locales.relations.extendeddata,
            itemId: 'showextendedfield',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.relations.extendeddata'
            }
        }
    ]
});
