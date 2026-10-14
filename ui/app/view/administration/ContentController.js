Ext.define('CMDBuildUI.view.administration.ContentController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content',
    listen: {
        global: {
            showadministrationcontentmask: 'onShowAdministrationContentMask'
        }
    },

    config: {
        maskCount: 0
    },
    onShowAdministrationContentMask: function (active, force, message) {
        var me = this;
        if (!active && force) {
            me.setMaskCount(0);
            setTimeout(function () {
                if (me.view) {
                    me.view.unmask();
                }
            }, 250);
        } else {
            switch (active) {
                case true:
                    try{
                        me.view.isVisible() && me.view.mask(message || CMDBuildUI.locales.Locales.administration.common.messages.loading);
                    }catch(e){
                        CMDBuildUI.util.Logger.log(e,CMDBuildUI.util.Logger.levels.debug);
                    }
                    me.setMaskCount(me.getMaskCount() + 1);
                    break;
                default:
                    me.setMaskCount(me.getMaskCount() - 1);
                    if (me.getMaskCount() <= 0) {
                        me.setMaskCount(0);
                        setTimeout(function () {
                            if (me.view) {
                                me.view.unmask();
                            }
                        }, 250);
                    }
                    break;
            }
        }
    }

});