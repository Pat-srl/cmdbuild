Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-modelsstats-modelsstats',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addModelTool': {
            click: 'onAddModelToolClick',
            destroy: 'onDestroyTool'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStats} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const vm = this.getViewModel();
        CMDBuildUI.util.Utilities.showLoader(true, view);
        vm.set('countLabel', CMDBuildUI.locales.Locales.administration.home.count);

        view.add({
            xtype: 'cartesian',
            reference: 'chart',
            width: '100%',
            height: 325,
            insetPadding: '50 20 0 20',
            flipXY: true,
            theme: 'admindashboard',
            animation: {
                easing: 'easeOut',
                duration: 500
            },
            bind: {
                store: '{modelsStats}'
            },
            axes: [
                {
                    type: 'numeric',
                    position: 'bottom',
                    fields: 'count',
                    grid: true,
                    majorTickSteps: 10,
                    increment: 10,
                    title: CMDBuildUI.locales.Locales.administration.home.count,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.home.count'
                    }
                },
                {
                    type: 'category',
                    position: 'left',
                    fields: 'description',
                    grid: true
                }
            ],
            series: [
                {
                    type: 'bar',
                    xField: 'description',
                    yField: 'count',
                    style: {
                        opacity: 0.8,
                        minGapWidth: 5
                    },
                    highlightCfg: {
                        opacity: 0.95
                    },
                    label: {
                        field: 'count',
                        display: 'insideEnd',
                        font: '12px'
                    }
                }
            ]
        });

        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/functions/_cm3_dashboard_model_stats/outputs',
            method: 'GET',
            timeout: 0
        }).then(
            function (response, opts) {
                if (!vm.destroyed) {
                    const responseJson = Ext.JSON.decode(response.responseText, true);
                    const _data = [];
                    Ext.Array.forEach(responseJson.data || [], function (model) {
                        switch (model.type) {
                            case 'class':
                                model.index = 7;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.classes;
                                break;
                            case 'processclass':
                                model.index = 6;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.processes;
                                break;
                            case 'domain':
                                model.index = 5;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.domains;
                                break;
                            case 'view':
                                model.index = 4;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.views;
                                break;
                            case 'report':
                                model.index = 3;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.reports;
                                break;
                            case 'dashboard':
                                model.index = 2;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.dashboards;
                                break;
                            case 'custompage':
                                model.index = 1;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.custompages;
                                break;
                            case 'busdescriptor':
                                model.index = 0;
                                model.description = CMDBuildUI.locales.Locales.administration.navigation.busdescriptors;
                                break;
                            default:
                                break;
                        }
                        _data.push(model);
                    });

                    vm.set('data', _data);
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                }
            },
            function () {
                if (!vm.destroyed) {
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                }
            }
        );
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onAddModelToolClick: function (tool, e, owner, eOpts) {
        const me = this;
        tool.menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            items: [
                {
                    text: CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/classes');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/processes');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/domains');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.views.addview,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    menu: [
                        {
                            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                            height: 32,
                            listeners: {
                                click: function (menuitem, eOpts) {
                                    me.redirectTo('administration/classes');
                                }
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                            height: 32,
                            listeners: {
                                click: function (menuitem, eOpts) {
                                    me.redirectTo('administration/processes');
                                }
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                            height: 32,
                            listeners: {
                                click: function (menuitem, eOpts) {
                                    me.redirectTo('administration/domains');
                                }
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                            height: 32,
                            menu: [
                                {
                                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                                    height: 32,
                                    listeners: {
                                        click: function (menuitem, eOpts) {
                                            me.redirectTo('administration/views/_new/FILTER/true');
                                        }
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                                    height: 32,
                                    listeners: {
                                        click: function (menuitem, eOpts) {
                                            me.redirectTo('administration/joinviews_empty/true');
                                        }
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                                    height: 32,
                                    listeners: {
                                        click: function (menuitem, eOpts) {
                                            me.redirectTo('administration/views/_new/SQL/true');
                                        }
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                                    height: 32,
                                    listeners: {
                                        click: function (menuitem, eOpts) {
                                            me.redirectTo('administration/views/_new/CALENDAR/true');
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
                            height: 32,
                            listeners: {
                                click: function (menuitem, eOpts) {
                                    me.redirectTo('administration/reports/_new');
                                }
                            }
                        }
                    ]
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/reports/_new');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.dashboards.adddashboard,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/dashboards/_new');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.custompages.texts.addcustompage,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/custompages/_new');
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.bus.addbusdescriptor,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cubes', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/bus/descriptors_empty/true');
                        }
                    }
                }
            ]
        });
        tool.menu.alignTo(tool.el.id, 'tr-br?');
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Object} eOpts
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    }
});
