/*
 * This file launches the application by asking Ext JS to create
 * and launch() the Application class.
 */
Ext.application({
    extend: 'CMDBuildUI.Application',

    name: 'CMDBuildUI',

    //-------------------------------------------------------------------------
    // Most customizations should be made to CMDBuildUI.Application. If you need to
    // customize this file, doing so below this section reduces the likelihood
    // of merge conflicts when upgrading to new versions of Sencha Cmd.
    //-------------------------------------------------------------------------

    listen: {
        controller: {
            '#': {
                unmatchedroute: 'onUnmatchedRoute'
            }
        }
    },

    onUnmatchedRoute: function () {
        var token = Ext.History.getToken();
        var stoken = token.split("/");
        var newstoken = Ext.Array.slice(stoken, 0, stoken.length - 1);
        var newtoken = newstoken.join("/");
        if (Ext.isEmpty(newtoken)) {
            newtoken = "management";
        }
        CMDBuildUI.util.Utilities.redirectTo(newtoken);
    }
});
