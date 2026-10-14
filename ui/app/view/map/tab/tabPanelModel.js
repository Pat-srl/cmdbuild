Ext.define('CMDBuildUI.view.map.tab.tabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-tabpanel',

    data: {
        activeItemCalculation: undefined
    },

    formulas: {
        activeItemCalculation: {
            bind: '{gisNavigation}',
            get: function (gisNavigation) {
                this.set('hiddenTabNavigationTree', !gisNavigation);
                if (gisNavigation) {
                    return CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-navigationtree'];
                }
                return CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-list'];
            }
        },

        updateTabs: {
            bind: '{drawmode}',
            get: function (drawmode) {
                if (Ext.isBoolean(drawmode)) {
                    this.set('disableTabNavigationTree', drawmode);
                    this.set('disableTabList', drawmode);
                    this.set('drawmode', drawmode);
                    this.set('disableTabLegend', drawmode);
                }
            }
        },

        disableTabLayerCalc: {
            bind: {
                store: '{geoAttributesStore}',
                drawmode: '{drawmode}',
                totalCount: '{geoAttributesStore.totalCount}'
            },
            get: function (data) {
                if (data.drawmode === true || !data.store) {
                    this.set('disableTabLayer', true);
                    return;
                }
                this.set('disableTabLayer', !data.totalCount);
            }
        },

        disableTabCards: {
            bind: '{objectId}',
            get: function (objectId) {
                return this.get('contextmenu.multiselection.enabled') ? true : !Ext.isNumeric(objectId);
            }
        },

        hideTabLegend: {
            bind: '{thematismId}',
            get: function (thematismId) {
                return Ext.isEmpty(thematismId);
            }
        }
    }
});
