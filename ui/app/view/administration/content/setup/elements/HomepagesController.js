Ext.define('CMDBuildUI.view.administration.content.setup.elements.HomepagesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-homepages',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#classesTree': {
            beforeitemdblclick: 'onClassesTreeBeforeItemDblClick',
            beforecheckchange: 'onClassesTreeBeforeCheckChange',
            checkchange: 'onClassesTreeCheckChange'
        }
    },

    onBeforeRender: function (view) {
        this.updateTitle();
    },

    /**
     * @return {Boolean} false to cancel the dblclick default behavior
     */
    onClassesTreeBeforeItemDblClick: function () {
        return false;
    },

    /**
     * @return {Boolean} false to prevent check change while in view mode
     */
    onClassesTreeBeforeCheckChange: function () {
        return !this.getViewModel().get('actions.view');
    },

    /**
     * @param {Ext.data.TreeModel} node
     * @param {Boolean} checked
     */
    onClassesTreeCheckChange: function (node, checked) {
        if (checked) {
            this.checkChild(node);
        } else {
            this.uncheckChild(node);
        }
    },

    /**
     * Reads the selected nodes from the "search on class" tree
     * and writes them, separated by commas, into theSetup
     */
    collectSearchClasses: function () {
        const vm = this.getViewModel();
        const store = vm.getStore('classesStore');
        const root = store.getRoot();

        if (!root) {
            return;
        }

        const names = [];
        root.cascade(function (node) {
            if (node.get('checked')) {
                names.push(node.get('name'));
            }
        });

        vm.set('theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__searchclasses', names.length ? names.join(',') : '');
    },

    privates: {
        /**
         * Update page title
         */
        updateTitle: function () {
            const view = this.getView();

            view.up('administration-content')
                .getViewModel()
                .set(
                    'title',
                    Ext.String.format(
                        '{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.navigation.homepages,
                        CMDBuildUI.locales.Locales.administration.navigation.settings
                    )
                );

            view.up('administration-content-setup-view')
                .getViewModel()
                .setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        },

        /**
         * @param {Ext.data.TreeModel} node
         */
        uncheckChild: function (node) {
            const me = this,
                childrens = node.childNodes;

            Ext.Array.forEach(childrens, function (childNode) {
                childNode.set('checked', false);
                if (childNode.childNodes) {
                    me.uncheckChild(childNode);
                }
            });
        },

        /**
         * @param {Ext.data.TreeModel} node
         */
        checkChild: function (node) {
            const me = this,
                childrens = node.childNodes;

            Ext.Array.forEach(childrens, function (childNode) {
                childNode.set('checked', true);
                if (childNode.childNodes) {
                    me.checkChild(childNode);
                }
            });
        }
    }
});
