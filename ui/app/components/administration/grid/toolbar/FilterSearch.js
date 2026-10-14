Ext.define('CMDBuildUI.components.administration.grid.toolbar.FilterSearch', {
    extend: 'Ext.toolbar.Paging',
    alias: 'widget.pagingtoolbarfiltersearch',

    panelTitle: '',
    searchField: {},
    // searchFieldPlaceholder: '',
    filterItemWidth: '50%',
    filterOnType: true,
    ui: 'administration',

    searchColumnIndexes: [],

    /**
     * @override
     * 
     * Gets the standard paging items in the toolbar
     * @private
     */
    getPagingItems: function () {
        var me = this,
            inputListeners = {
                scope: me,
                blur: me.onPagingBlur
            };

        inputListeners[Ext.supports.SpecialKeyDownRepeat ? 'keydown' : 'keypress'] = me.onPagingKeyDown;

        return [{
            xtype: 'title',
            text: this.panelTitle,
            localized: {
                text: this.localized.panelTitle
            }
        }, '->',
        this.getFilterTextField()
        ];
    },

    /**
     * 
     * @returns 
     */
    getFilterTextField: function () {
        var me = this;
        if (!this.filterTextField) {
            this.filterTextField = Ext.create('Ext.form.field.Text', {
                submitValue: false,
                isFormField: false,
                width: this.filterItemWidth,
                emptyText: this.searchField.emptyText,
                localized: this.searchField.localized,
                margin: '-1 2 3 2',
                enableKeyEvents: true,
                triggers: {
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: function (field, trigger, eOpts) {
                            field.reset();
                            me.doFilter();
                        },
                        autoEl: {
                            'data-testid': 'administration-groupandpermission-toolbar-form-clear-trigger'
                        }
                    }
                }
            });
            if (this.filterOnType) {
                this.filterTextField.on('change', this.doFilter, this);
            } else {
                this.filterTextField.on('specialkey', function (e) {
                    if (e.getKey() == e.ENTER) {
                        this.doFilter();
                    }
                }, this);
            }
        }
        return this.filterTextField;
    },

    /**
     * 
     */
    doFilter: function () {
        var searchValue = this.getFilterTextField().getValue();
        this.localFilter(this.searchColumnIndexes, searchValue);
    },

    /**
     * 
     * @param {*} searchColumnIndexes 
     * @param {*} searchValue 
     */
    localFilter: function (searchColumnIndexes, searchValue) {
        this.store.removeFilter(this.filter);
        this.filter = new Ext.util.Filter({
            filterFn: function (record) {
                if (searchColumnIndexes.length === 0 || Ext.isEmpty(searchValue)) {
                    return true;
                }

                var found = false;
                Ext.Array.each(searchColumnIndexes, function (dataIndex) {
                    if (record.get(dataIndex) && record.get(dataIndex).toUpperCase().indexOf(searchValue.toUpperCase()) != -1) {
                        found = true;
                        return false;
                    }
                }, this);
                return found;
            }
        });
        this.store.addFilter(this.filter);
    }

});