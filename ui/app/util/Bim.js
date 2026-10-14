/**
 * @file CMDBuildUI.util.Bim
 * @module CMDBuildUI.util.Bim
 * @author PAT srl
 * @access private
 */
Ext.define('CMDBuildUI.util.Bim', {
    singleton: true,

    /**
     * @param {String} GlobalId
     * @param {Function} callback The function to execute in success case
     * @param {Object} scope The scope for the function
     */
    getRelatedCard: function (projectId, globalId, callback, scope) {
        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/bim/projects/{1}/values/{2}?{3}',
                CMDBuildUI.util.Config.baseUrl,
                projectId,
                globalId,
                'if_exists=true'
            ),

            method: 'GET',
            success: function (response) {
                var data = JSON.parse(response.responseText).data;

                callback.call(scope || this, data);
            }
        });
    },

    /**
     *
     * @param {*} projectId
     * @param {*} selectedId
     */
    openBimPopup: function (typeViewer, projectId, selectedId) {
        function openPopup(xtype) {
            CMDBuildUI.util.Utilities.openPopup('bimPopup', CMDBuildUI.locales.Locales.bim.bimViewer, {
                xtype: xtype,
                projectId: projectId,
                selectedId: selectedId
            });
        }

        openPopup('bim-xeokit-container');
    }
});
