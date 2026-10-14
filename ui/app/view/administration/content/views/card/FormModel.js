Ext.define('CMDBuildUI.view.administration.content.views.card.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-views-card-form',
    data: {
        viewType: null,
        filterDisabled: true,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_views_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        sourceClassNameManager: {
            bind: {
                theViewFilter: '{theViewFilter}',
                sourceClass: '{theViewFilter.sourceClassName}'
            },
            get: function (data) {
                if (data.sourceClass) {
                    var description;
                    try {
                        description = Ext.getStore('classes.Classes')
                            .findRecord('name', data.sourceClass)
                            .get('description');
                    } catch (error) {
                        description = Ext.getStore('processes.Processes')
                            .findRecord('name', data.sourceClass)
                            .get('description');
                    }
                    this.set('theViewFilter._sourceClassName_description', description);
                }
                if (data.theViewFilter.get('type') !== CMDBuildUI.model.views.View.types.filter || data.sourceClass) {
                    return this.set('filterDisabled', false);
                }
                return this.set('filterDisabled', true);
            }
        },
        removeFilterDisabled: {
            bind: {
                filter: '{theViewFilter.filter}',
                action: '{action}'
            },
            get: function (data) {
                if (data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view || !data.filter) {
                    return true;
                }
                return false;
            }
        },
        title: {
            bind: {
                description: '{theViewFilter.description}'
            },
            get: function (data) {
                var me = this;
                var typeTitle;
                switch (me.get('theViewFilter.type')) {
                    case CMDBuildUI.model.views.View.types.filter:
                        typeTitle = Ext.String.format(
                            ' - {0}',
                            CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter
                        );
                        break;
                    case CMDBuildUI.model.views.View.types.join:
                        typeTitle = Ext.String.format(
                            ' - {0}',
                            CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin
                        );
                        break;
                    case CMDBuildUI.model.views.View.types.sql:
                        typeTitle = Ext.String.format(
                            ' - {0}',
                            CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql
                        );
                        break;
                    case CMDBuildUI.model.views.View.types.calendar:
                        typeTitle = Ext.String.format(
                            ' - {0}',
                            CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule
                        );
                        break;

                    default:
                        break;
                }
                me.getParent().set(
                    'title',
                    Ext.String.format(
                        '{0} {1} {2}',
                        CMDBuildUI.locales.Locales.administration.navigation.views,
                        typeTitle,
                        data.description ? ' - ' + data.description : ''
                    )
                );
            }
        },
        viewTypeManager: {
            bind: {
                theViewFilter: '{theViewFilter}',
                description: '{theViewFilter.description}'
            },
            get: function (data) {
                if (data.theViewFilter) {
                    var me = this;
                    const rawIconFont = data.theViewFilter.get('icon_font');
                    if (rawIconFont) {
                        data.theViewFilter.set(
                            'icon_font',
                            CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                        );

                        data.theViewFilter.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }

                    switch (data.theViewFilter.get('type')) {
                        case CMDBuildUI.model.views.View.types.sql:
                            me.set('isSqlType', true);
                            me.set('isScheduleType', false);
                            me.set('isFilterType', false);
                            me.set('viewType', CMDBuildUI.model.views.View.types.sql);
                            return CMDBuildUI.model.views.View.types.sql;
                        case CMDBuildUI.model.views.View.types.filter:
                            me.set('isSqlType', false);
                            me.set('isScheduleType', false);
                            me.set('isFilterType', true);
                            me.set('viewType', CMDBuildUI.model.views.View.types.filter);
                            return CMDBuildUI.model.views.View.types.filter;
                        case CMDBuildUI.model.views.View.types.calendar:
                            me.set('isSqlType', false);
                            me.set('isScheduleType', true);
                            me.set('isFilterType', false);
                            me.set('viewType', CMDBuildUI.model.views.View.types.calendar);
                            return CMDBuildUI.model.views.View.types.calendar;
                        default:
                            break;
                    }
                }
            }
        }
    },

    stores: {
        getFunctionsStore: {
            source: 'Functions',
            sorters: ['description'],
            pageSize: 0, // disable pagination
            autoLoad: '{isSqlType}'
        },

        calendarDefinitionsStore: {
            model: 'CMDBuildUI.model.calendar.Trigger',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: CMDBuildUI.util.administration.helper.ApiHelper.server.getSchedulesTriggerUrl(),
                type: 'baseproxy',
                reader: {
                    type: 'json'
                },
                extraParams: {
                    detailed: true,
                    active: true
                }
            }
        }
    }
});
