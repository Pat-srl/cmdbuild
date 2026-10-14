Ext.define('CMDBuildUI.view.login.Container', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.login.ContainerController', 'CMDBuildUI.view.login.ContainerModel'],

    alias: 'widget.login-container',
    controller: 'login-container',
    viewModel: {
        type: 'login-container'
    },

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },

    scrollable: true,

    cls: Ext.baseCSSPrefix + 'login-main-container',

    items: [
        {
            xtype: 'container',
            width: 450,
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'center',
                        pack: 'center'
                    },
                    items: [
                        {
                            xtype: 'main-header-logodark',
                            height: 60,
                            width: '45%',
                            margin: '0 0 50 0'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    cls: Ext.baseCSSPrefix + 'login-form-container',
                    padding: 30,
                    items: [
                        {
                            hidden: true,
                            autoEl: {
                                'data-testid': 'login-text'
                            },
                            bind: {
                                hidden: '{!loginText}',
                                html: '{loginText}'
                            }
                        },
                        {
                            xtype: 'login-formpanel',
                            hidden: true,
                            bind: {
                                hidden: '{sso.hiddendefaultlogin}'
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'pwdforgottenbtn',
                            ui: 'link',
                            cls: Ext.baseCSSPrefix + 'mt-2',
                            autoEl: {
                                'data-testid': 'login-pwdforgottenbtn'
                            },
                            text: CMDBuildUI.locales.Locales.main.password.forgotten,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.main.password.forgotten'
                            },
                            bind: {
                                hidden: '{hideChangePasswordBtn || disabledfields.password || sso.hiddendefaultlogin}'
                            }
                        },
                        {
                            xtype: 'login-ssopanel'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            width: 450,
            cls: Ext.baseCSSPrefix + 'login-bottom-container',
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            items: [
                {
                    xtype: 'container',
                    flex: 1,
                    items: {
                        xtype: 'button',
                        hidden: true,
                        itemId: 'languageselector',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('globe', 'solid'),
                        cls: Ext.baseCSSPrefix + 'language-selector',
                        ui: 'language-selector',
                        autoEl: {
                            'data-testid': 'header-languageselector'
                        },
                        ariaAttributes: {
                            'aria-expanded': false,
                            'aria-haspopup': true,
                            'aria-label': CMDBuildUI.locales.Locales.arialabels.languageselector
                        },

                        listeners: {
                            menushow: function () {
                                this.ariaEl.dom.setAttribute('aria-expanded', 'true');
                            },
                            menuhide: function () {
                                this.ariaEl.dom.setAttribute('aria-expanded', 'false');
                            }
                        }
                    }
                },
                {
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'hbox',
                        pack: 'end'
                    },
                    items: [
                        {
                            xtype: 'component',
                            itemId: 'infoComponent',
                            bind: {
                                html: '{htmlInfoBtn}'
                            },
                            style: {
                                cursor: 'pointer'
                            },
                            listeners: {
                                click: {
                                    element: 'el',
                                    fn: 'onInfoBtnClick'
                                }
                            }
                        },
                        {
                            xtype: 'component',
                            bind: {
                                html: '<small><a href="https://pat.eu" target="_blank">PAT srl</a> © {currentYear}</small>'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
