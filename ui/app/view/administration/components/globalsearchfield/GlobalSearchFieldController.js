Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-globalsearchfield-globalsearchfield',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onDestroy',
            specialkey: 'onSearchSpecialKey'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.setTriggers({
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                itemId: 'globalsearchsubmit',
                handler: this.onGlobalSearchSubmit,
                autoEl: {
                    'data-testid': 'administration-grid-localsearchfield-input-search-trigger'
                }
            },
            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
        });
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onGlobalSearchSubmit: function (field, newValue, oldValue) {
        const value = field.getValue();
        if (field.resultPanel) {
            field.resultPanel.destroy();
            field.resultPanel = null;
        }
        const objectType = field.getObjectType();
        const subType = field.getSubType();
        let searchType, filter;

        switch (objectType) {
            case 'widget':
            case 'contextmenu':
                filter = Ext.JSON.encode({
                    query: value
                });
                searchType = 'components/' + objectType;
                break;
            case 'dmsmodels':
                searchType = 'dms/models';
                break;
            case 'dmscategories':
                searchType = 'dms/categories';
                break;
            case 'etlgates':
                searchType = 'etl/gates';
                break;
            case 'templates':
                searchType = 'etl/templates';
                filter = Ext.JSON.encode({
                    query: value,
                    attribute: {
                        simple: {
                            value: ['csv', 'xls', 'xlsx'],
                            operator: 'IN',
                            attribute: 'fileFormat',
                            parameterType: 'fixed'
                        }
                    }
                });
                break;
            default:
                searchType = objectType;
                filter = Ext.JSON.encode({
                    query: value
                });
                break;
        }

        if (value) {
            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}/search/{1}?{2}={3}',
                    CMDBuildUI.util.Config.baseUrl,
                    searchType,
                    filter ? 'filter' : 'query',
                    filter ? encodeURIComponent(filter) : encodeURIComponent(value)
                ),
                method: 'GET',
                callback: function (request, success, response) {
                    const parsedResponse = Ext.JSON.decode(response.responseText);
                    field.resultPanel = Ext.create(
                        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanel',
                        {
                            y: field.getY() + field.getHeight(),
                            x: field.getX(),
                            objectType: objectType,
                            subType: subType,
                            viewModel: {
                                data: {
                                    responseData: parsedResponse.data
                                }
                            }
                        }
                    );
                    field.resultPanel.show();
                    field.focus();
                }
            });
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onGlobalSearchSubmit(field);
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField} view
     * @param {Object} eOpts
     */
    onDestroy: function (view, eOpts) {
        const resultPanel = view.resultPanel;
        if (resultPanel && !resultPanel.destroyed) {
            resultPanel.destroy();
        }
    },

    privates: {
        resultPanel: null
    }
});
