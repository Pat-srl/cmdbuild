Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-attachments-fromcard',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#attachments_input': {
            change: 'onAttahmentsInputChange'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCard} view 
     */
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: '{theTemplate.data}',
            deep: true
        },
            function (data) {
                if (data.uploadAttachments === null || typeof data.uploadAttachments == 'undefined') {
                    vm.set('attachmentsMode', 'noone');
                } else if (Ext.isEmpty(data.uploadAttachments) || data.uploadAttachments === '{}') {
                    vm.set('removeFilterDisabled', true);
                    vm.set('attachmentsMode', 'all');
                } else {
                    vm.set('attachmentsMode', 'fromFilter');
                    vm.set('removeFilterDisabled', false);
                }
            });

    },
    /**
     * 
     * @param {Ext.form.field.ComboBox} input 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    onAttahmentsInputChange: function (input, newValue, oldValue) {
        var vm = input.lookupViewModel();
        switch (newValue) {
            case 'noone':
                vm.get('theTemplate.data').uploadAttachments = null;
                break;
            case 'all':
                vm.get('theTemplate.data').uploadAttachments = '{}';
                break;
            case 'fromFilter':
                vm.get('theTemplate.data').uploadAttachments = vm.get('theTemplate.data').uploadAttachments || '{}';
                break;
            default:
                break;
        }
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditFilterBtnClick: function (button, e, eOpts) {
        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theTemplate');
        var actions = vm.get('actions');
        var recordFilter;

        if (typeof record.get('data').uploadAttachments == 'string') {
            recordFilter = JSON.parse(record.get('data').uploadAttachments);
        } else if (typeof record.get('data').uploadAttachments == 'object') {
            recordFilter = record.get('data').uploadAttachments;
        }
        if (!Ext.Object.isEmpty(recordFilter) && !recordFilter.attachment) {
            recordFilter = { attachment: recordFilter };
        }
        if (!recordFilter) {
            recordFilter = '{}';
        }

        var popuTitle = CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters;
        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            configuration: recordFilter,
            isOnlyDmsFilter: true,
            shared: false
        });

        var viewmodel = {
            data: {
                theFilter: filter,
                actions: Ext.copy(actions),
                visibletextfield: false,
                displayOnly: actions.view
            }
        };

        var attachmentsPanel = this.getDmsFilterTab(viewmodel);
        var listeners = {
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            applyfilter: function (panel, filter, eOpts) {
                me.onApplyFilter(filter);
                me.popup.close();
            },
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            saveandapplyfilter: function (panel, filter, eOpts) {
                me.onSaveAndApplyFilter(filter);
                me.popup.close();
            },
            /**
             * Custom event to close popup directly from popup
             * @param {Object} eOpts 
             */
            popupclose: function (eOpts) {
                me.popup.close();
            }
        };
        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (_button) {
                    me.setRecordFilterFromPanel(me.popup, vm.get('theTemplate'), 'uploadAttachments');
                }
            }, {
                handler: function () {
                    me.popup.close();
                }
            })
        }];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attachmentsPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };


        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
            ui: 'administration-actionpanel',
            listeners: {

            },

            viewModel: {
                data: {
                    index: '0',
                    grid: {},
                    record: record,
                    canedit: true
                }
            }
        });
    },

    /**
     * @override
     */
    privates: {
        /**
         * 
         * @param {Ext.app.ViewModel} viewmodel 
         * @returns 
         */
        getDmsFilterTab: function (viewmodel) {
            return {
                xtype: 'administration-content-emails-templates-card-attachments-filter',
                reference: 'attachmentspanel',
                viewModel: viewmodel
            };
        },

        /**
         * 
         * @param {Ext.panel.Panel} popup 
         * @param {Ext.data.Model} record 
         * @param {String} key 
         * @param {Boolean} disableAutoclose 
         * @param {Boolean} ignoreWarning 
         * @returns 
         */
        setRecordFilterFromPanel: function (popup, record, key, disableAutoclose, ignoreWarning) {
            var attachmentspanel = popup.down("administration-content-emails-templates-card-attachments-filter");

            var value = attachmentspanel && attachmentspanel.getAttachmentsData() || {};
            var tempFilter = new CMDBuildUI.util.AdvancedFilter();
            tempFilter.applyAdvancedFilter(value);

            if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(tempFilter)) {
                value = CMDBuildUI.util.administration.helper.FilterHelper.removeEmptyFilterKeys(value, true, 'configuration');
                if (record && key) {
                    record.get('data')[key] = value !== '{}' ? value : null;
                }
                if (!disableAutoclose) {
                    popup.close();
                }
                if (!record && !key) {
                    return value;
                }
            } else {
                if (!ignoreWarning) {
                    Ext.asap(function () {
                        CMDBuildUI.util.Notifier.showWarningMessage(
                            Ext.String.format(
                                '<span data-testid="message-window-text">{0}</span>',
                                CMDBuildUI.locales.Locales.errors.invalidfilter
                            )
                        );
                    });
                }
            }
        }

    }

});