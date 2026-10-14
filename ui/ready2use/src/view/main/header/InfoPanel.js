Ext.define('CMDBuildUI.view.main.header.InfoPanel', {
    extend: 'Ext.panel.Panel',

    statics: { popupHeight: 430 },

    alias: 'widget.main-header-infopanel',

    layout: { type: 'vbox', align: 'stretch' },

    config: { applicationVersion: null },

    bind: { applicationVersion: '{applicationVersion}' },

    items: [
        {
            xtype: 'main-header-logo',
            padding: '15 15 5',
            clickCounter: 0,
            listeners: {
                afterrender: {
                    fn: function (view, width, height, oldWidth, oldHeight, eOpts) {
                        // Used to see the logo correctly on the Firefox browser
                        Ext.defer(function () {
                            view.updateLayout();
                        }, 100);
                    }
                },
                click: {
                    fn: function () {
                        const view = Ext.get(this);
                        view.component.clickCounter += 1;
                        if (view.component.clickCounter === 10) {
                            view.component.clickCounter = 0;
                            CMDBuildUI.util.Msg.openDialog('Project contributors', { html: '@@CMDBUILD_CONTRIBUTORS' });
                        }
                    },
                    element: 'el'
                }
            }
        },
        {
            bind: {
                html:
                    '<div class="x-selectable">' +
                    '    <p><strong>Version:</strong> {applicationVersion}</p>' +
                    '    <p><strong>License:</strong> the software is released under <a href="http://www.gnu.org/licenses/agpl-3.0.html" target="_blank">AGPL</a> license.</p>' +
                    '    <p><strong>Credits:</strong> CMDBuild READY2USE is a verticalization of CMDBuild for IT Governance and is developed and maintained by <a href="https://pat.eu" target="_blank">PAT srl</a>.' +
                    '    <br />CMDBuild READY2USE ® is a registered trademark of <a href="https://pat.eu" target="_blank">PAT srl</a> and can\'t be removed.</p>' +
                    '    <p style="margin-top: 35px; color: #83878b;">For further information please visit <a href="http://www.cmdbuildready2use.org" target="_blank">www.cmdbuildready2use.org</a></p>' +
                    '</div>'
            },
            padding: '10 15'
        }
    ]
});
