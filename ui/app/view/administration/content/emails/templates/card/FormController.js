Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-form',

    control: {
        '#': {
            beforeRender: 'onBeforeRender',
            tabchange: 'onTabChange'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.Form} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        Ext.getStore('emails.Accounts').load();
        Ext.getStore('emails.Signatures').load();

        vm.bind('{theTemplate.description}', function (theTemplateDescription) {
            let title = CMDBuildUI.locales.Locales.administration.emails.newtemplate;

            if (theTemplateDescription) {
                if (vm.get('theTemplate').phantom) {
                    title = Ext.String.format(
                        'New template {0} {1}',
                        theTemplateDescription && theTemplateDescription.length ? ' - ' : '',
                        theTemplateDescription
                    );
                } else {
                    title = theTemplateDescription;
                }
            }

            const detailWindow = view.up('administration-detailswindow');
            if (detailWindow) {
                detailWindow.getViewModel().set('title', title);
            }
        });

        if (!vm.get('theTemplate') && this.getView().getInitialConfig()._rowContext) {
            vm.linkTo('theTemplate', {
                type: 'CMDBuildUI.model.emails.Template',
                id: this.getView().getInitialConfig()._rowContext.record.get('_id')
            });
        }

        vm.bind('{theTemplate.provider}', function (provider) {
            if (view._rowContext) {
                const tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
                const generalProperties =
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getGeneralPropertyFieldset();
                tabPanelHelper.addTab(view, 'properties', generalProperties.title, [generalProperties], 0, {
                    disabled: '{disabledTabs.properties}'
                });

                const template =
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getTemplateFieldset();
                tabPanelHelper.addTab(view, 'template', template.title, [template], 1, {
                    disabled: '{disabledTabs.template}'
                });

                const metadata =
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getMetadataFieldset();
                tabPanelHelper.addTab(view, 'metadata', metadata.title, [metadata], 2, {
                    disabled: '{disabledTabs.metadata}'
                });

                if (provider === CMDBuildUI.model.emails.Template.providers.email) {
                    tabPanelHelper.addTab(
                        view,
                        'attachments',
                        CMDBuildUI.locales.Locales.administration.busmessages.attachments,
                        [{ xtype: 'administration-content-emails-templates-card-attachments' }],
                        3,
                        {
                            disabled: '{disabledTabs.showOnClasses}'
                        }
                    );

                    const showOnClasses =
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getShowOnClassesFieldset();
                    tabPanelHelper.addTab(view, 'showOnClasses', showOnClasses.title, [showOnClasses], 4, {
                        disabled: '{disabledTabs.showOnClasses}'
                    });
                }
                vm.set('activeTab', vm.get('activeTabs.emailstemplate'));
            } else {
                view.add(
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getGeneralPropertyFieldset()
                );
                view.add(
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getTemplateFieldset()
                );
                view.add(
                    CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getMetadataFieldset()
                );
                if (provider === CMDBuildUI.model.emails.Template.providers.email) {
                    view.add({ xtype: 'administration-content-emails-templates-card-attachments' });
                    view.add(
                        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getShowOnClassesFieldset()
                    );
                }
            }
        });

        vm.bind(
            {
                bindTo: '{theTemplate.showOnClasses}',
                single: true
            },
            function (showOnClasses) {
                let isEmpty = true;
                const assigned = [];
                if (showOnClasses.length && showOnClasses !== 'noone') {
                    isEmpty = false;
                    const classes = showOnClasses.split(',');
                    classes.forEach(function (klass) {
                        const classData = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(klass);
                        if (classData) {
                            assigned.push({
                                description: classData.get('description')
                            });
                        }
                    });
                    vm.set('showOnClassesViewData', assigned);
                    vm.set(
                        'showOnClassesDescription',
                        CMDBuildUI.locales.Locales.administration.emails.selectedclasses
                    );
                    vm.set('showOnAllClasses', isEmpty + '');
                } else if (showOnClasses === 'noone') {
                    vm.set('showOnClassesViewData', []);
                    vm.set(
                        'showOnClassesDescription',
                        CMDBuildUI.locales.Locales.administration.attributes.strings.noone
                    );
                    vm.set('showOnAllClasses', 'noone');
                } else if (!assigned.length) {
                    vm.set('showOnClassesViewData', assigned);
                    vm.set(
                        'showOnClassesDescription',
                        CMDBuildUI.locales.Locales.administration.emails.showonallclasses
                    );
                    vm.set('showOnAllClasses', isEmpty + '');
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bus.descriptors.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.emailstemplate',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        const vm = this.getViewModel();
        const form = this.getView();

        if (form.isValid()) {
            const theTemplate = vm.get('theTemplate');
            const attachmentsElements = form.down('administration-content-emails-templates-card-attachments');
            if (attachmentsElements) {
                const attachments = attachmentsElements.getData();
                theTemplate.set('reports', attachments.reports);
            }
            theTemplate.set(
                'data',
                Ext.apply(vm.get('keyvaluedataStore').getDataObject(), {
                    cm_lang_expr: theTemplate.get('cm_lang_expr'),
                    action: theTemplate.get('action'),
                    actionLabel: theTemplate.get('actionLabel'),
                    uploadAttachments: theTemplate.get('data').uploadAttachments
                })
            );
            Ext.asap(function () {
                theTemplate.save({
                    success: function (record, operation) {
                        me.saveLocales(vm, record);
                        Ext.GlobalEvents.fireEventArgs('templateupdated', [record]);
                        form.up().fireEvent('closed');
                    },
                    callback: function () {
                        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
                    }
                });
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        const theTemplate = vm.get('theTemplate');
        const { cm_lang_expr, action, actionLabel } = theTemplate.getData();
        theTemplate.reject(); // discard changes
        vm.set('theTemplate.cm_lang_expr', cm_lang_expr);
        vm.set('theTemplate.action', action);
        vm.set('theTemplate.actionLabel', actionLabel);
        this.getView().up().fireEvent('closed');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        const me = this;
        const vm = this.getViewModel();
        const grid = Ext.ComponentQuery.query('administration-content-emails-templates-grid')[0];
        const theTemplate = vm.get('theTemplate') || grid.getInitialConfig()._rowContext.record;

        if (theTemplate.get('_can_write')) {
            theTemplate.set('active', !theTemplate.get('active'));
            const attachments = me.getView().down('administration-content-emails-templates-card-attachments').getData();
            theTemplate.set('reports', attachments.reports);
            theTemplate.set(
                'data',
                Ext.apply(vm.get('keyvaluedataStore').getDataObject(), {
                    cm_lang_expr: theTemplate.get('cm_lang_expr'),
                    action: theTemplate.get('action'),
                    actionLabel: theTemplate.get('actionLabel'),
                    uploadAttachments: theTemplate.get('data').uploadAttachments
                })
            );
            Ext.asap(function () {
                theTemplate.save({
                    success: function (record, operation) {
                        grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [
                            grid,
                            record,
                            me
                        ]);
                    }
                });
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        const template = this.getViewModel().get('theTemplate') || this.getView().getInitialConfig()._rowContext.record;
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-form',
            viewModel: {
                data: {
                    theTemplate: template,
                    actions: {
                        view: true,
                        add: false,
                        edit: false
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const template = this.getViewModel().get('theTemplate') || this.getView().getInitialConfig()._rowContext.record;
        if (template.get('_can_write')) {
            const container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.add({
                xtype: 'administration-content-emails-templates-card-form',
                viewModel: {
                    data: {
                        theTemplate: template,
                        actions: {
                            view: false,
                            add: false,
                            edit: true
                        }
                    }
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        const newTemplate = vm.get('theTemplate').clone();

        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-form',

            viewModel: {
                links: {
                    theTemplate: {
                        type: 'CMDBuildUI.model.emails.Template',
                        create: newTemplate.getData()
                    }
                },
                data: {
                    actions: {
                        view: false,
                        add: true,
                        edit: false
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        const me = this;
        const callback = function (btnText) {
            if (btnText === 'yes') {
                CMDBuildUI.util.Ajax.setActionId('delete-template');
                const grid = Ext.ComponentQuery.query('administration-content-emails-templates-grid')[0];
                grid.getStore().remove(me.getViewModel().get('theTemplate'));
                grid.getStore().sync();
                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            }
        };

        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateDescriptionClick: function (button, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNotificationDescription(
                vm.get('theTemplate').phantom ? '.' : vm.get('theTemplate').get('name')
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm,
            true
        );
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateSubjectClick: function (button, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNotificationSubject(
                vm.get('theTemplate').phantom ? '.' : vm.get('theTemplate').get('name')
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theSubjectTranslation',
            vm,
            true
        );
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateBodyClick: function (button, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNotificationBody(
            vm.get('theTemplate').phantom ? '.' : vm.get('theTemplate').get('name')
        );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theBodyTranslation',
            vm,
            true,
            vm.get('theTemplate.provider') === 'email' ? 'htmleditor' : 'textarea'
        );
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateActionLabelClick: function (button, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNotificationActionLabel(
                vm.get('theTemplate').phantom ? '.' : vm.get('theTemplate').get('name')
            );

        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theActionLabelTranslation',
            vm,
            true
        );
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.view.administration.content.emails.templates.card.FormModel} vm
         * @param {Ext.data.Model} record
         */
        saveLocales: function (vm, record, cb) {
            const translations = [
                'theDescriptionTranslation',
                'theSubjectTranslation',
                'theBodyTranslation',
                'theActionLabelTranslation'
            ];

            const keyFunction = [
                'getLocaleKeyOfNotificationDescription',
                'getLocaleKeyOfNotificationSubject',
                'getLocaleKeyOfNotificationBody',
                'getLocaleKeyOfNotificationActionLabel'
            ];

            Ext.Array.forEach(translations, function (item, index) {
                if (vm.get(item)) {
                    const translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[
                        keyFunction[index]
                    ](record.get('name'));
                    vm.get(item).crudState = 'U';
                    vm.get(item).crudStateWas = 'U';
                    vm.get(item).phantom = false;
                    vm.get(item).set('_id', translationCode);
                    vm.get(item).save({
                        success: function (translations, operation) {
                            CMDBuildUI.util.Logger.log(
                                item + ' localization was saved',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                    });
                }
            });
        }
    }
});
