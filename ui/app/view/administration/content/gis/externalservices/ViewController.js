Ext.define('CMDBuildUI.view.administration.content.gis.externalservices.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-externalservices-view',
    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onEditBtnClick: function () {
        this.getViewModel().set('actions.view', false);
        this.getViewModel().set('actions.edit', true);
        this.getViewModel().set('actions.add', false);
    },

    onCancelBtnClick: function () {
        this.getViewModel().set('actions.view', true);
        this.getViewModel().set('actions.edit', false);
        this.getViewModel().set('actions.add', false);
        var vm = this.getViewModel();
        var theConfig = vm.get('theConfig');
        theConfig.reject();
    },

    onSaveBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var theConfig = vm.get('theConfig');

        var config = {
            org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__enabled: theConfig.get('geoserverenabled'),
            org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__admin__DOT__user: theConfig.get('geoserveradminuser'),
            org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__admin__DOT__password:
                theConfig.get('geoserveradminpassword'),
            org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__url: theConfig.get('geoserverurl'),
            org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__workspace: theConfig.get('geoserverworkspace'),
            org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel: theConfig.get('defaultzoom'),
            org__DOT__cmdbuild__DOT__gis__DOT__keepZoomAndPosition__DOT__enabled: theConfig.get('keepzoomandposition'),
            org__DOT__cmdbuild__DOT__gis__DOT__minZoomLevel: theConfig.get('minimumzoom'),
            org__DOT__cmdbuild__DOT__gis__DOT__maxZoomLevel: theConfig.get('maximumzoom')
        };
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(config, null, null, this).then(function () {
            if (!vm.destroyed) {
                button.setDisabled(false);
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }
        });
        this.getViewModel().set('actions.view', true);
        this.getViewModel().set('actions.edit', false);
        this.getViewModel().set('actions.add', false);
    }
});
