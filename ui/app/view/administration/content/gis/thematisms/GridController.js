Ext.define('CMDBuildUI.view.administration.content.gis.thematisms.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-thematisms-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
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

    /**
     *
     * @param {CMDBuildUI.view.administration.content.gis.thematisms.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().set('title', CMDBuildUI.locales.Locales.administration.gis.thematisms);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const store = vm.getStore('thematismsStore');

        const modifiedItems = {};
        Ext.Array.forEach(store.getRange(), function (item, index, allitems) {
            if (item.crudState === 'U') {
                modifiedItems[item.getId()] = item.get('_public');
            }
        });

        Ext.Ajax.request({
            url: vm.get('storeProxyUrl') + '/visibility',
            method: 'POST',
            jsonData: modifiedItems,
            callback: function () {
                store.load();
            }
        });

        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        vm.getStore('thematismsStore').rejectChanges();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        const vm = this.getViewModel();
        const searchValue = field.getValue();
        const store = vm.get('thematismsStore');

        if (searchValue) {
            store.getAdvancedFilter().addQueryFilter(searchValue);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        const vm = this.getViewModel();
        // clear store filter
        const store = vm.get('thematismsStore');
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
    }
});
