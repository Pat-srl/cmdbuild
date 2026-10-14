Ext.define('CMDBuildUI.view.administration.content.dashboards.common.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-common-form',

    // prettier-ignore
    mixins: {
        tileController: 'CMDBuildUI.view.administration.content.dashboards.common.mixin.TileControllerMixin',
        listController: 'CMDBuildUI.view.administration.content.dashboards.common.mixin.ListControllerMixin',
        dataSourceController: 'CMDBuildUI.view.administration.content.dashboards.common.mixin.DataSourceControllerMixin',
        localizationController: 'CMDBuildUI.view.administration.content.dashboards.common.mixin.LocalizationControllerMixin'
    },
    listen: {
        store: {
            '#functionAttributesStore': {
                load: 'onFunctionAttributesStoreLoad',
                datachanged: 'onFunctionAttributesStoreLoad'
            }
        }
    },
    // prettier-ignore
    control: {
        '#saveBtn': { click: 'onSaveBtnClick' },
        '#cancelBtn': { click: 'onCancelBtnClick' },
        '#editBtn': { click: 'onEditBtnClick' },
        '#deleteBtn': { click: 'onDeleteBtnClick' },
        '#cloneBtn': { click: 'onCloneBtnClick' },
        '#enableBtn': { click: 'onToggleActiveBtnClick' },
        '#disableBtn': { click: 'onToggleActiveBtnClick' },

        '#dataSourceFuncktionName_input': { change: 'onFunctionChange' },
        '#dataSourceType_input': { change: 'onSourceTypeChange' },
        '#dataSourceClassName_input, #dataSourceViewName_input, #dataSourceProcessName_input, #dataSourceFunktionName_input': {change: 'onDataSourceChange' },

        '#editFilterBtn': { click: 'onEditFilterBtn' },
        '#removeFilterBtn': { click: 'onRemoveFilterBtn' },

        '#tilesContainer': { afterrender: 'onTilesContainerRender' },
        '#addTileBtn': { click: 'onAddTileBtnClick' },
        '#removeTileTool': { click: 'onRemoveTileToolClick' },

        '#searchLogoFile': { change: 'onSearchLogoFileChange' },
        '#searchLogoRemoveBtn': { click: 'onSearchLogoRemoveBtnClick' }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            theComponent = vm.get('theComponent');

        var parameters = [];
        theComponent.dataSourceParameters().each(function (parameter) {
            if (!Ext.isEmpty(parameter.get('fieldType')) || !Ext.isEmpty(parameter.get('type'))) {
                parameters.push(parameter.getData());
            }
        });

        var tiles = [];
        theComponent.getTilesStore().each(function (tile) {
            tiles.push(tile.getData());
        });

        // clean hidden data
        Ext.Array.forEach(Ext.Object.getKeys(theComponent.getData()), function (key) {
            if (vm.get(Ext.String.format('hiddenTypeProperty.{0}', key))) {
                delete theComponent.data[key];
            }
        });
        theComponent.set('dataSourceParameters', parameters);
        theComponent.set('tiles', tiles);
        vm.get('owner').getController().onRowsStoreDatachanged();
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            owner = vm.get('owner'),
            ownerVm = owner.getViewModel(),
            theDashboard = ownerVm.get('theDashboard'),
            theComponent = vm.get('theComponent'),
            components = theDashboard.components(),
            rows = ownerVm.get('rows');

        this.undoLocales(theDashboard, theComponent, vm.get('owner').getViewModel());

        theComponent.reject();
        if (vm.get('actions.add')) {
            components.remove(theComponent);
            rows.each(function (row) {
                Ext.Array.forEach(row.get('columns'), function (column) {
                    if (column.components && Ext.Array.contains(column.components, theComponent.getId())) {
                        Ext.Array.remove(column.components, theComponent.getId());
                    }
                });
            });
            rows.fireEventArgs('datachanged', [rows]);
        }
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this._fireOwnerColumnEvent('itemeditclick');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        this._fireOwnerColumnEvent('itemdeleteclick');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        this._fireOwnerColumnEvent('itemcloneclick');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        this._fireOwnerColumnEvent('itemdisableclick');
    },

    onSearchLogoFileChange: function (field, value) {
        if (!field.fileInputEl || !field.fileInputEl.dom.files.length) {
            return;
        }

        var file = field.fileInputEl.dom.files[0];
        var fileExtension = file.name.split('.').pop().toLowerCase();

        if (fileExtension !== 'png') {
            field.extractFileInput();
            CMDBuildUI.util.Msg.alert(
                'Error',
                Ext.String.format(CMDBuildUI.locales.Locales.attachments.wrongfileextension, fileExtension)
            );
            return;
        }

        var vm = this.getViewModel();
        var reader = new FileReader();
        reader.onload = function (e) {
            vm.set('theComponent.logo', e.target.result); // data URI base64
        };
        reader.readAsDataURL(file);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSearchLogoRemoveBtnClick: function (button, e, eOpts) {
        var fileField = this.getView().down('#searchLogoFile');
        if (fileField) {
            fileField.reset();
        }
        this.getViewModel().set('theComponent.logo', null);
    },

    privates: {
        /**
         * Fires an event on the owner column
         * @param {String} eventName
         */
        _fireOwnerColumnEvent: function (eventName) {
            const vm = this.getViewModel();
            const ownerColumn = vm.get('ownerColumn');
            if (!ownerColumn) return;
            const componentId = vm.get('theComponent._id');
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            ownerColumn.fireEventArgs(eventName, [{ data: componentId }]);
        }
    }
});
