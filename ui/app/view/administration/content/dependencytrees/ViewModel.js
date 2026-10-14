Ext.define('CMDBuildUI.view.administration.content.dependencytrees.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dependencytrees-view',

    formulas: {
        dataManager: {
            bind: {
                theDependencyTree: '{theDependencyTree}'
            },
            get: function (data) {
                const theDependencyTree = data.theDependencyTree;

                if (!theDependencyTree) {
                    return;
                }

                const rawIconFont = theDependencyTree.get('icon_font');
                if (rawIconFont) {
                    theDependencyTree.set(
                        'icon_font',
                        CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                    );

                    theDependencyTree.set(
                        'icon_description',
                        CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                    );
                }

                const targetClass = theDependencyTree.get('targetClass');
                const nodes = theDependencyTree.get('nodes');

                if (!targetClass && nodes && nodes.length) {
                    theDependencyTree.set('targetClass', nodes[0].targetClass);
                }
            }
        },

        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                return data.isView && !data.isHiddenForm ? false : true;
            }
        }
    }
});
