Ext.define('CMDBuildUI.mixins.model.Lock', {
    mixinId: 'model-lock-mixin',

    /**
     * Check lock on item.
     * 
     * @return {Ext.Deferred}
     */
    isLocked: function () {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            Ext.Ajax.request({
                url: Ext.String.format("{0}/{1}/lock", this.getProxy().getUrl(), this.getId()),
                method: 'GET',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    deferred.resolve(res.found);
                },
                error: function (response) {
                    deferred.resolve(false);
                }
            });
        } else {
            deferred.resolve(false);
        }
        return deferred.promise;
    },

    /**
     * Check lock on item.
     * 
     * @return {Ext.Deferred}
     */
    addLock: function () {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            var me = this;
            Ext.Ajax.request({
                url: Ext.String.format("{0}/{1}/lock", this.getProxy().getUrl(), this.getId()),
                method: 'POST',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    if (res.success) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format("Card {0}-{1} locked.", me.get("_type"), me.getId()),
                            CMDBuildUI.util.Logger.levels.debug,
                            null,
                            res.data
                        );
                        deferred.resolve(res.success);
                    } else {
                        var user = CMDBuildUI.locales.Locales.main.cardlock.someone;
                        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.showuser) && res.user) {
                            user = res.user;
                        }
                        CMDBuildUI.util.Notifier.showWarningMessage(
                            Ext.String.format(CMDBuildUI.locales.Locales.main.cardlock.lockedmessage, user)
                        );
                        deferred.resolve(res.success);
                    }
                },
                error: function(response) {
                    deferred.resolve(true);
                }
            });
        } else {
            deferred.resolve(true);
        }
        return deferred.promise;
    },

    /**
     * Remove lock from item.
     * 
     * @return {Ext.Deferred}
     */
    removeLock: function () {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            var me = this;
            Ext.Ajax.request({
                url: Ext.String.format("{0}/{1}/lock", this.getProxy().getUrl(), this.getId()),
                method: 'DELETE',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format("Card {0}-{1} unlocked.", me.get("_type"), me.getId()),
                        CMDBuildUI.util.Logger.levels.debug,
                        null,
                        res.data
                    );
                    deferred.resolve(res.success);
                }
            });
        } else {
            deferred.resolve(true);
        }
        return deferred.promise;
    }

});