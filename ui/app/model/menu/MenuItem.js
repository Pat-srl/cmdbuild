Ext.define('CMDBuildUI.model.menu.MenuItem', {
    extend: 'Ext.data.Model',

    requires: ['CMDBuildUI.proxy.MenuProxy'],

    statics: {
        types: {
            favourites: 'favourites',
            folder: 'folder',
            klass: 'class',
            klassparent: 'classparent',
            process: 'processclass',
            processparent: 'processclassparent',
            view: 'view',
            report: 'report',
            reportpdf: 'reportpdf',
            reportodt: 'reportodt',
            reportrtf: 'reportrtf',
            reportcsv: 'reportcsv',
            dashboard: 'dashboard',
            homepage: 'homepage',
            custompage: 'custompage',
            searchfilter: 'filter',
            calendar: 'calendar',
            navtree: 'navtree',
            navtreeitem: 'navtreeitem',
            dependencytree: 'dependencytree',
            loader: 'loader'
        },
        icons: {
            favourites: CMDBuildUI.util.helper.IconHelper.getIconId('star', 'solid'),
            folder: CMDBuildUI.util.helper.IconHelper.getIconId('folder', 'solid'),
            klass: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'),
            klassparent: 'cmdbuildicon-classparent',
            process: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
            processparent: 'cmdbuildicon-processparent',
            view: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
            report: CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular'),
            reportpdf: CMDBuildUI.util.helper.IconHelper.getIconId('file-pdf', 'regular'),
            reportodt: CMDBuildUI.util.helper.IconHelper.getIconId('file-word', 'regular'),
            reportrtf: CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular'),
            reportcsv: CMDBuildUI.util.helper.IconHelper.getIconId('file-excel', 'regular'),
            dashboard: CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid'),
            homepage: CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid'),
            custompage: CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid'),
            calendar: CMDBuildUI.util.helper.IconHelper.getIconId('calendar-alt', 'solid'),
            navtree: CMDBuildUI.util.helper.IconHelper.getIconId('sitemap', 'solid'),
            dependencytree: CMDBuildUI.util.helper.IconHelper.getIconId('sitemap', 'solid'),
            loader: CMDBuildUI.util.helper.IconHelper.getIconId('spinner', 'solid') + ' fa-spin'
        },
        objecttypes: {
            folder: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            class: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            classparent: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            processclass: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            processclassparent: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            view: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
            report: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportpdf: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportodt: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportrtf: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportcsv: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            dashboard: CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard,
            homepage: CMDBuildUI.util.helper.ModelHelper.objecttypes.homepage,
            custompage: CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            calendar: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            navtree: CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
            navtreeitem: CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
            dependencytree: CMDBuildUI.util.helper.ModelHelper.objecttypes.dependencytree
        }
    },

    fields: [
        {
            name: 'menutype',
            type: 'string',
            mapping: 'menuType',
            persist: true,
            critical: true
        },
        {
            name: 'index',
            type: 'integer',
            persist: true,
            critical: true
        },
        {
            name: 'objecttypename',
            type: 'string',
            mapping: 'objectTypeName',
            persist: true,
            critical: true
        },
        {
            name: 'objectdescription',
            type: 'string',
            mapping: 'objectDescription',
            persist: true,
            critical: true
        },
        {
            name: '_actualDescription',
            type: 'string'
        },
        {
            name: 'objectdescription_translation',
            type: 'string',
            mapping: '_objectDescription_translation'
        },
        {
            name: 'text',
            type: 'string',
            calculate: function (data) {
                return data.objectdescription_translation || data.objectdescription;
            }
        },
        {
            name: 'leaf',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: '_targettypename',
            type: 'string',
            persist: false,
            critical: false
        },
        {
            name: '_forAdmin',
            type: 'boolean',
            persist: false,
            critical: false
        },
        {
            name: 'icon_font',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'iconCls',
            type: 'string',
            persist: false,
            calculate: function (data) {
                switch (data.menutype) {
                    case CMDBuildUI.model.menu.MenuItem.types.favourites: {
                        return CMDBuildUI.model.menu.MenuItem.icons.favourites;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.folder: {
                        return CMDBuildUI.model.menu.MenuItem.icons.folder;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.klass: {
                        const cls = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objecttypename);

                        if (cls && cls.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(cls.get('icon_font'));
                        }

                        if (cls && cls.get('prototype')) {
                            return CMDBuildUI.model.menu.MenuItem.icons.klassparent;
                        }

                        return CMDBuildUI.model.menu.MenuItem.icons.klass;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.process: {
                        const proc = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.objecttypename);

                        if (proc && proc.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(proc.get('icon_font'));
                        }

                        if (proc && proc.get('prototype')) {
                            return CMDBuildUI.model.menu.MenuItem.icons.processparent;
                        }

                        return CMDBuildUI.model.menu.MenuItem.icons.process;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.view: {
                        const view = CMDBuildUI.util.helper.ModelHelper.getViewFromName(data.objecttypename);

                        if (view && view.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(view.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.view;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.report: {
                        const rpt = CMDBuildUI.util.helper.ModelHelper.getReportFromName(data.objecttypename);

                        if (rpt && rpt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rpt.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.report;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.reportpdf: {
                        const rpt = CMDBuildUI.util.helper.ModelHelper.getReportFromName(data.objecttypename);

                        if (rpt && rpt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rpt.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.reportpdf;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.reportodt: {
                        const rpt = CMDBuildUI.util.helper.ModelHelper.getReportFromName(data.objecttypename);

                        if (rpt && rpt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rpt.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.reportodt;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.reportrtf: {
                        const rpt = CMDBuildUI.util.helper.ModelHelper.getReportFromName(data.objecttypename);

                        if (rpt && rpt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rpt.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.reportrtf;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.reportcsv: {
                        const rpt = CMDBuildUI.util.helper.ModelHelper.getReportFromName(data.objecttypename);

                        if (rpt && rpt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rpt.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.reportcsv;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.dashboard: {
                        const db = CMDBuildUI.util.helper.ModelHelper.getDashboardFromName(data.objecttypename);

                        if (db && db.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(db.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.dashboard;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.homepage: {
                        const db = CMDBuildUI.util.helper.ModelHelper.getHomepageFromName(data.objecttypename);

                        if (db && db.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(db.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.homepage;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.custompage: {
                        const cp = CMDBuildUI.util.helper.ModelHelper.getCustomPageFromName(data.objecttypename);

                        if (cp && cp.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(cp.get('icon_font'));
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.custompage;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.calendar: {
                        return CMDBuildUI.model.menu.MenuItem.icons.calendar;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.loader: {
                        return CMDBuildUI.model.menu.MenuItem.icons.loader;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.navtree:
                    case CMDBuildUI.model.menu.MenuItem.types.navtreeitem: {
                        const nt = CMDBuildUI.util.helper.ModelHelper.getNavtreeFromName(data.objecttypename);
                        if (nt && nt.get('icon_font')) {
                            return CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(nt.get('icon_font'));
                        } else if (data._forAdmin) {
                            return CMDBuildUI.model.menu.MenuItem.icons.navtree;
                        } else if (data._targettypename) {
                            return CMDBuildUI.model.menu.MenuItem.icons.klass;
                        }
                        return CMDBuildUI.model.menu.MenuItem.icons.folder;
                    }

                    case CMDBuildUI.model.menu.MenuItem.types.dependencytree: {
                        return CMDBuildUI.model.menu.MenuItem.icons.dependencytree;
                    }
                }
            }
        },
        {
            name: 'children',
            type: 'auto',
            persist: true,
            critical: true
        },
        {
            name: 'findcriteria',
            type: 'string',
            persist: false,
            calculate: function (r) {
                return r.menutype + ':' + r.objecttypename;
            }
        }
    ],

    proxy: {
        url: '/sessions/current/',
        type: 'menuproxy'
    }
});
