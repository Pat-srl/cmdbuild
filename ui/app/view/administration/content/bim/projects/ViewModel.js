Ext.define('CMDBuildUI.view.administration.content.bim.projects.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-view',

    data: {
        theProject: null,
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canConvert: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_bim_modify}',
            get: function (canModify) {
                this.set('toolAction._canAdd', canModify);
                this.set('toolAction._canClone', canModify);
                this.set('toolAction._canUpdate', canModify);
                this.set('toolAction._canDelete', canModify);
                this.set('toolAction._canActiveToggle', canModify);
            }
        },

        canConvert: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_bim_modify}',
                _can_convert: '{theProject._can_convert}'
            },
            get: function (data) {
                this.set('toolAction._canConvert', data.canModify && data._can_convert);
            }
        },

        parentManager: {
            bind: '{theProject.parentId}',
            get: function (parentId) {
                const me = this;
                if (!Ext.isEmpty(parentId)) {
                    const store = this.get('projectsWithoutParent');
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
    },

    stores: {
        projects: {
            type: 'bim-projects',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },

        projectsWithoutParent: {
            source: '{projects}',
            filters: [function (project) {
                return Ext.isEmpty(project.get('parentId'));
            }]
        }
    }
});