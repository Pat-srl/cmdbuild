Ext.define('Overrides.quicktips.QuickTips', {
    override: 'Ext.tip.QuickTip',

    // set default to 100
    alwaysOnTop: 100,

    tagConfig: {
        namespace: 'data-',
        attribute: 'qtip',
        width: 'qwidth',
        target: 'target',
        title: 'qtitle',
        hide: 'hide',
        cls: 'qclass',
        align: 'qalign',
        anchor: 'anchor',
        showDelay: 'qshowDelay',
        dismissDelay: 'qdismissDelay',
        hideDelay: 'qhideDelay'
    },

    /**
     * @override
     * 
     * @param {*} target 
     * @param {*} event 
     */
    handleTargetOver: function (target, event) {
        var me = this,
            currentTarget = me.currentTarget,
            cfg = me.tagConfig,
            ns = cfg.namespace,
            tipText = me.getTipText(target, event),
            autoHide;

        if (tipText) {

            autoHide = currentTarget.getAttribute(ns + cfg.hide);

            me.activeTarget = {
                el: currentTarget,
                text: tipText,
                width: +currentTarget.getAttribute(ns + cfg.width) || null,
                autoHide: autoHide !== "user" && autoHide !== 'false',
                title: currentTarget.getAttribute(ns + cfg.title),
                cls: currentTarget.getAttribute(ns + cfg.cls),
                align: currentTarget.getAttribute(ns + cfg.align),
                showDelay: currentTarget.getAttribute(ns + cfg.showDelay),
                hideAction: currentTarget.getAttribute(ns + cfg.hideAction),
                alignTarget: currentTarget.getAttribute(ns + cfg.anchorTarget),
                dismissDelay: currentTarget.getAttribute(ns + cfg.dismissDelay)
            };

            // If we were not configured with an anchor,
            // allow it to be set by the target's properties
            if (!me.initialConfig.hasOwnProperty('anchor')) {
                me.anchor = currentTarget.getAttribute(ns + cfg.anchor);
            }

            // If we are anchored, and not configured with an anchorTarget,
            // anchor to the target element, or whatever its 'data-anchortarget' points to
            if (me.anchor && !me.initialConfig.hasOwnProperty('anchorTarget')) {
                me.alignTarget = me.activeTarget.alignTarget || target;
            }

            me.activateTarget();
        }
    },

    /**
     * @override
     * 
     */
    activateTarget: function () {
        var me = this,
            activeTarget = me.activeTarget,
            delay = activeTarget.showDelay,
            hideAction = activeTarget.hideAction;

        if (activeTarget.dismissDelay) {
            me.dismissDelay = parseInt(activeTarget.dismissDelay, 10);
        } else {
            me.dismissDelay = 5000;
        }

        // If moved from target to target rapidly, the hide delay will not
        // have fired, so just update content and alignment.
        if (me.isVisible()) {
            me.updateContent();
            me.realignToTarget();
        }
        else {
            if (activeTarget.showDelay) {
                delay = me.showDelay;
                me.showDelay = parseInt(activeTarget.showDelay, 10);
            }

            me.delayShow();

            if (activeTarget.showDelay) {
                me.showDelay = delay;
            }

            if (!(hideAction = activeTarget.hideAction)) {
                delete me.hideAction;
            }
            else {
                me.hideAction = hideAction;
            }
        }
    }

});