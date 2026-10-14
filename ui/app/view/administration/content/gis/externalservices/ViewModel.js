Ext.define('CMDBuildUI.view.administration.content.gis.externalservices.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-externalservices-view',
    data: {
        theConfig: {
            geoserverenabled: null,
            geoserveradminuser: '',
            geoserveradminpassword: '',
            geoserverurl: '',
            geoserverworkspace: '',
            defaultzoom: 0,
            keepzoomandposition: false,
            minimumzoom: 0,
            maximumzoom: 25
        },

        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolAction: {
            _canUpdate: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            get: function (data) {
                this.set('toolAction._canUpdate', data.canModify === true);
            }
        },
        configManager: function () {
            const me = this;

            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (configs) {
                if (!me.destroyed) {
                    const geoserverenabled = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__enabled';
                    })[0];

                    const geoserveradminuser = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__admin__DOT__user';
                    })[0];

                    const geoserveradminpassword = configs.filter(function (config) {
                        return (
                            config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__admin__DOT__password'
                        );
                    })[0];

                    const geoserverurl = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__url';
                    })[0];

                    const geoserverworkspace = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__workspace';
                    })[0];

                    const defaultzoom = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel';
                    })[0];

                    const keepzoomandposition = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__keepZoomAndPosition__DOT__enabled';
                    })[0];

                    const minimumzoom = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__minZoomLevel';
                    })[0];

                    const maximumzoom = configs.filter(function (config) {
                        return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__maxZoomLevel';
                    })[0];

                    const servicetype = 'OpenStreetMap';

                    const theConfig = CMDBuildUI.model.gis.Externalservices.create({
                        geoserverenabled: geoserverenabled.hasValue
                            ? geoserverenabled.value
                            : geoserverenabled['default'],
                        geoserveradminuser: geoserveradminuser.hasValue
                            ? geoserveradminuser.value
                            : geoserveradminuser['default'],
                        geoserveradminpassword: geoserveradminpassword.hasValue
                            ? geoserveradminpassword.value
                            : geoserveradminpassword['default'],
                        geoserverurl: geoserverurl.hasValue ? geoserverurl.value : geoserverurl['default'],
                        geoserverworkspace: geoserverworkspace.hasValue
                            ? geoserverworkspace.value
                            : geoserverworkspace['default'],
                        servicetype: servicetype,
                        defaultzoom: defaultzoom.hasValue ? defaultzoom.value : defaultzoom['default'],
                        keepzoomandposition: keepzoomandposition.hasValue
                            ? keepzoomandposition.value
                            : keepzoomandposition['default'],
                        minimumzoom: minimumzoom.hasValue ? minimumzoom.value : minimumzoom['default'],
                        maximumzoom: maximumzoom.hasValue ? maximumzoom.value : maximumzoom['default']
                    });

                    me.set('theConfig', theConfig);
                }
            });
        }
    }
});
