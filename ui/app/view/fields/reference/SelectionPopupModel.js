Ext.define('CMDBuildUI.view.fields.reference.SelectionPopupModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-reference-selectionpopup',

    data: {
        searchvalue: null,
        addbtn: {
            text: null,
            disabled: false,
            hidden: true
        },
        addButtonUi: 'management-primary',
        saveButtonUi: 'management-primary-small',
        searchInputCls: 'management-input'
    },

    formulas: {
        uiManager: function () {
            if (CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
                this.set('addButtonUi', 'administration-action-small');
                this.set('saveButtonUi', 'administration-action-small');
                this.set('searchInputCls', 'administration-input');
            }
        },
        /**
         * Return add card button text.
         */
        updateAddBtnInfo: {
            bind: {
                objectTypeDescription: '{objectTypeDescription}'
            },
            get: function (data) {
                this.set("addbtn.text", Ext.String.format(
                    "{0} {1}",
                    CMDBuildUI.locales.Locales.classes.cards.addcard,
                    data.objectTypeDescription
                ));

                this.set("addbtn.hidden", this.get("objectType") !== CMDBuildUI.util.helper.ModelHelper.objecttypes.klass);
            }
        },

        /**
         * Update store info
         */
        updateStoreInfo: {
            get: function () {
                // store type
                var type, object;
                switch (this.get("objectType")) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        type = 'classes-cards';
                        object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(this.get("objectTypeName"));
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                        type = 'processes-instances';
                        object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(this.get("objectTypeName"));
                        break;
                }
                this.set("storeinfo.type", type);

                // add filter
                var advancedfilter = {};
                if (this.get("storeinfo.typesfilter") && !Ext.Object.isEmpty(this.get("storeinfo.typesfilter"))) {
                    advancedfilter.attributes = this.get("storeinfo.typesfilter");
                }
                if (this.get("storeinfo.ecqlfilter") && !Ext.Object.isEmpty(this.get("storeinfo.ecqlfilter"))) {
                    advancedfilter.ecql = this.get("storeinfo.ecqlfilter");
                }
                if (this.get("storeinfo.relsfilter") && !Ext.Object.isEmpty(this.get("storeinfo.relsfilter"))) {
                    advancedfilter.relation = this.get("storeinfo.relsfilter");
                }
                this.set("storeinfo.advancedfilter", advancedfilter);

                var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(object, true);
                this.set("storeinfo.sorters", sorters);
            }
        },

        /**
         * Save button disabled property.
         * Bindend on selection.
         */
        saveBtnDisabled: {
            bind: '{selection}',
            get: function (selection) {
                return !selection;
            }
        }
    },

    stores: {
        records: {
            type: '{storeinfo.type}',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}'
        }
    }

});