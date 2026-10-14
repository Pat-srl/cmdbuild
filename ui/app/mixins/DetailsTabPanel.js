Ext.define('CMDBuildUI.mixins.DetailsTabPanel', {
    mixinId: 'custompage-mixin',

    statics: {
        actions: {
            attachments: 'attachments',
            clone: 'clone',
            create: 'new',
            edit: 'edit',
            emails: 'emails',
            history: 'history',
            masterdetail: 'details',
            notes: 'notes',
            relations: 'relations',
            view: 'view',
            clonecardandrelations: 'clonecardandrelations',
            readonly: 'readonly',
            schedules: 'schedules'
        }
    },

    config: {
        /**
         * @cfg {String} ui
         * A UI style for a component.
         */
        ui: 'management',

        /**
         * @cfg {"top"/"bottom"/"left"/"right"} tabPosition
         * The position where the tab strip should be rendered. Possible values are: 
         * 
         *  - top
         *  - bottom
         *  - left
         *  - right
         */
        tabPosition : 'left',

        /**
         * @cfg {Boolean} readOnlyTabs
         * 
         * Set to `true` to shwow tabs in read-only mode, if supported
         */
        readOnlyTabs: false,

        /**
         * @cfg {Ext.panel.Tool[]} tabpaneltools
         * 
         * Set to `true` to show tabs in read-only mode, if supported
         */
        tabpaneltools: [],

        /**
         * @cfg {Object} formConfig
         * 
         * An object containing form configuration
         */
        formConfig: {}
    },

    /**
     * @return {Boolean}
     */
    isInDetailWindow: function() {
        return this.up() && this.up().getId() === CMDBuildUI.view.management.DetailsWindow.elementId;
    },

    /**
     * @return {Object} Object form base config
     */
    getObjectFormBaseConfig: function () {
        var formConfig = this.getFormConfig() || {};
        return Ext.merge({
            bodyPadding: 10,
            reference: this._objectFormReference,
            itemId: this._objectFormReference
        }, formConfig);
    },

    /**
     * @return {Ext.model.Model}
     */
    getFormObject: function () {
        var panel = this.lookup(this._objectFormReference);
        if (panel) {
            return panel.lookupViewModel().get(this._objectLinkName);
        }
    },

    /**
     * @return {Ext.model.Model}
     */
    getFormMode: function () {
        var panel = this.lookup(this._objectFormReference);
        if (panel) {
            return panel.formmode;
        }
    },

    /**
     * 
     * @param {Ext.panel.Tool[]} newdata 
     * @param {Ext.panel.Tool[]} olddata 
     */
    updateTabpaneltools: function(newdata, olddata) {
        this.addTool(newdata);
    },

    privates: {
        /**
         * @property {_objectFormReference}
         */
        _objectFormReference: 'objectForm',

        /**
         * @property {_emailReference}
         */
        _emailReference: 'emailGrid',

        _objectLinkName: 'theObject'
    }
});