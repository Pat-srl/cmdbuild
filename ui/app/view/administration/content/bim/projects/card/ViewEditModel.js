Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-card-viewedit',

    data: {
        actions: {
            edit: false,
            view: false,
            add: false
        }
    },

    formulas: {
        panelTitle: {
            bind: {
                theProject: '{theProject}',
                description: '{theProject.description}'
            },
            get: function (data) {
                return title = Ext.String.format(
                    '{0} {1} {2}',
                    data.theProject.phantom ? CMDBuildUI.locales.Locales.administration.bim.newproject : CMDBuildUI.locales.Locales.administration.bim.projectlabel,
                    data.description ? ' - ' : '',
                    data.description
                );
            }
        },

        parentManager: {
            bind: '{theProject.parentId}',
            get: function (parentId) {
                const me = this;
                if (!Ext.isEmpty(parentId)) {
                    const store = this.get("projectsWithoutParent");
                    if (store) {
                        const record = store.findRecord('_id', parentId, false, false, true, true);
                        if (record) {
                            try {
                                const theParent = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(record.get('ownerClass'));
                                if (theParent) {
                                    CMDBuildUI.util.api.Client.getRemoteCard(record.get('ownerClass'), record.get('ownerCard')).then(function (card) {
                                        me.set('theProject._parentClassDescription', theParent.get('description'));
                                        me.set('theProject._parentCardDescription', card.get('Description'));
                                    });
                                }
                            } catch (error) {
                                CMDBuildUI.util.Logger.log(Ext.String.format("error on fetch remote associated card {0} on bim project", me.get('theProject._id')), CMDBuildUI.util.Logger.levels.error);
                                CMDBuildUI.util.Logger.log(error.message, CMDBuildUI.util.Logger.levels.error);
                            }
                        }
                    }
                } else {
                    me.set('theProject._parentClassDescription', '');
                    me.set('theProject._parentCardDescription', '');
                }
            }
        },

        updateCardDescription: {
            bind: '{theProject.ownerCard}',
            get: function (ownerCard) {
                if (ownerCard) {
                    const me = this;
                    const theProject = me.get('theProject');
                    const ownerClass = theProject.get('ownerClass');
                    try {
                        if (ownerClass && ownerCard) {
                            CMDBuildUI.util.api.Client.getRemoteCard(ownerClass, ownerCard).then(function (card) {
                                me.set('theProject._ownerCardDescription', card.get('Description'));
                            });
                        } else {
                            me.set('theProject._ownerCardDescription', "");
                        }
                    } catch (error) {
                        CMDBuildUI.util.Logger.log(Ext.String.format("error on fetch remote card {0} on bim project", me.get('theProject._id')), CMDBuildUI.util.Logger.levels.error);
                        CMDBuildUI.util.Logger.log(error.message, CMDBuildUI.util.Logger.levels.error);
                    }
                }
            }
        }
    }
});