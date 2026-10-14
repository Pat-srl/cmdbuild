Ext.define('CMDBuildUI.model.administration.MenuItem', {
    extend: 'Ext.data.TreeModel',

    requires: ['CMDBuildUI.proxy.MenuProxy'],

    statics: {
        types: {
            folder: 'folder',
            klass: 'class',
            domain: 'domain',
            lookuptype: 'lookuptype',
            dmsmodel: 'dmsmodel',
            dmscategory: 'dmscategory',
            menu: 'menu',
            process: 'processclass',
            user: 'user',
            groupsandpermissions: 'groupsandpermissions',
            view: 'view',
            report: 'report',
            setup: 'setup',
            email: 'email',
            emailqueue: 'emailqueue',
            localization: 'localization',
            reportpdf: 'reportpdf',
            reportodt: 'reportodt',
            reportrtf: 'reportrtf',
            reportcsv: 'reportcsv',
            dashboard: 'dashboard',
            custompage: 'custompage',
            customcomponent: 'customcomponent',
            widget: 'widget',
            contextmenu: 'contextmenu',
            navigationtree: 'navigationtree',
            menunavigationtree: 'menunavigationtree',
            dependencytree: 'dependencytree',
            task: 'task',
            searchfilter: 'filter',
            schedule: 'schedule',
            script: 'script',
            gis: 'gis',
            bim: 'bim',
            importexport: 'etltemplate',
            gisgatetemplate: 'gisgatetemplate',
            databasegatetemplate: 'databasegatetemplate',
            ifcgatetemplate: 'ifcgatetemplate',
            home: 'home',
            bus: 'bus',
            servermanagement: 'servermanagement',
            notification: 'notification',
            pluginmanager: 'pluginmanager',
            homepage: 'homepage',
            ai: 'ai'
        }
    },

    fields: [
        {
            name: 'menutype',
            type: 'string',
            mapping: 'menuType'
        },
        {
            name: 'selectable',
            type: 'boolean',
            persist: false,
            defaultValue: true
        },
        {
            name: 'index',
            type: 'integer'
        },
        {
            name: 'objecttype',
            type: 'string',
            mapping: 'objectType'
        },
        {
            name: 'objectid',
            type: 'auto',
            mapping: 'objectId'
        },
        {
            name: 'text',
            type: 'string',
            mapping: 'objectDescription'
        },
        {
            name: 'leaf',
            type: 'boolean',
            persist: false
        },
        {
            name: 'rowCls',
            type: 'string',
            persist: false
        },
        {
            name: 'iconCls',
            type: 'string',
            persist: false,
            calculate: function (data) {
                switch (data.menutype) {
                    case CMDBuildUI.model.menu.MenuItem.types.folder:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('folder', 'solid');
                    case CMDBuildUI.model.menu.MenuItem.types.klass:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular');
                    case CMDBuildUI.model.menu.MenuItem.types.process:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid');
                    case CMDBuildUI.model.menu.MenuItem.types.view:
                    case CMDBuildUI.model.menu.MenuItem.types.lookuptype:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid');
                    case CMDBuildUI.model.menu.MenuItem.types.report:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular') + ' fa-flip-horizontal';
                    case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('file-pdf', 'regular');
                    case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular');
                    case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular');
                    case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular');
                    case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid');
                    case CMDBuildUI.model.menu.MenuItem.types.homepage:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid');
                    case CMDBuildUI.model.menu.MenuItem.types.custompage:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.user:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('user', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.email:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.notification:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('bell', 'regular');
                    case CMDBuildUI.model.administration.MenuItem.types.emailqueue:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('tasks', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.localization:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('globe-americas', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.task:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('clock', 'regular');
                    case CMDBuildUI.model.administration.MenuItem.types.schedule:
                        return 'cmdbuild-customnavicon cmdbuildicon-stopwatch';
                    case CMDBuildUI.model.administration.MenuItem.types.gis:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid');
                    case CMDBuildUI.model.administration.MenuItem.types.bim:
                        return CMDBuildUI.util.helper.IconHelper.getIconId('building', 'solid');
                }
            }
        }
    ],
    proxy: {
        type: 'menuproxy',
        url: '/sessions/current/'
    }
});
