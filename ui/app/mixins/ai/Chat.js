Ext.define('CMDBuildUI.mixins.ai.Chat', {
    mixinId: 'ai-chat-controller-mixin',

    onCopyAnswerClick: function (record) {
        CMDBuildUI.util.Utilities.copyToClipboard(
            record.get('rawMessage') || record.get('message'),
            CMDBuildUI.locales.Locales.ai.copiedtoclipboard
        );
    },

    /**
     * On view card click
     *
     * @param {String} objectTypeName
     * @param {String} objectId
     */
    onViewCardClick: function (objectTypeName, objectId) {
        let xtype;
        const me = this;
        const objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);

        if (objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
            xtype = 'classes-cards-card-view';
        } else if (objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            xtype = 'processes-instances-instance-view';
        }

        if (xtype) {
            const config = {
                xtype: xtype,
                viewModel: {
                    data: {
                        objectTypeName: objectTypeName,
                        objectId: objectId
                    }
                },
                shownInPopup: true,
                hideTools: false,
                tabpaneltools: [
                    {
                        xtype: 'tool',
                        itemId: 'opentool',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                        cls: 'management-tool',
                        tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                        autoEl: {
                            'data-testid': 'ai-card-view-opentool'
                        },
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
                        },
                        listeners: {
                            click: function (tool, e, owner, eOpts) {
                                if (objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                                    popup.close();
                                    me.redirectTo(
                                        CMDBuildUI.util.Navigation.getClassBaseUrl(objectTypeName, objectId, 'view')
                                    );
                                } else if (objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                                    popup.close();
                                    me.redirectTo(
                                        CMDBuildUI.util.Navigation.getProcessBaseUrl(objectTypeName, objectId)
                                    );
                                }
                            }
                        }
                    }
                ],
                padding: 10
            };

            const popup = CMDBuildUI.util.Utilities.openPopup(
                null,
                CMDBuildUI.locales.Locales.ai.viewrelatedcard,
                config
            );
        }
    },

    /**
     * On view attachment click
     *
     * @param {String} objectTypeName
     * @param {String} objectId
     * @param {String} documentId
     * @param {String} fileName
     * @param {String} mimeType
     */
    onViewAttachmentClick: function (objectTypeName, objectId, documentId, fileName, mimeType) {
        const attachmenturl =
            CMDBuildUI.util.Config.baseUrl +
            CMDBuildUI.util.api.Classes.getAttachments(objectTypeName, objectId) +
            '/' +
            documentId;

        CMDBuildUI.util.helper.AttachmentsHelper.viewOrDownloadAttachment(attachmenturl, mimeType, fileName);
    },

    /**
     * On open url on another tab
     *
     * @param {String} url
     */
    onOpenUrl: function (url) {
        window.open(url, '_blank');
    },

    /**
     * On item added to the conversation view
     *
     * @param {Ext.data.Model[]} records The model instances
     * @param {Number} index The index at which the set of records was inserted
     * @param {HTMLElement[]} node The node that has just been updated
     * @param {CMDBuildUI.view.management.ai.Chat} view The view adding the item
     * @param {Object} eOpts event options
     */
    onItemAdd: function (records, index, node, view, eOpts) {
        this.getView().down('management-ai-conversation').getScrollable().scrollTo(null, Infinity, { duration: 1000 });
    }
});
