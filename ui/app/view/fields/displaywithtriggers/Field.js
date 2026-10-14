Ext.define('CMDBuildUI.view.fields.displaywithtriggers.Field', {
    extend: 'Ext.form.field.Display',

    alias: 'widget.displayfieldwithtriggers',

    config: {
        /**
         * @cfg {Boolean} hideTrigger
         * `true` to hide all triggers
         */
        hideTrigger: false,

        // @cmd-auto-dependency {aliasPrefix: "trigger.", isKeyedObject: true}
        /**
         * @cfg {Object} triggers
         * {@link CMDBuildUI.view.fields.displaywithtriggers.Trigger Triggers} to use in this field.  The keys in
         * this object are unique identifiers for the triggers. The values in this object
         * are {@link CMDBuildUI.view.fields.displaywithtriggers.Trigger Trigger} configuration objects.
         *
         *     Ext.create('Ext.form.field.Text', {
         *         renderTo: document.body,
         *         fieldLabel: 'My Custom Field',
         *         triggers: {
         *             foo: {
         *                 cls: 'my-foo-trigger',
         *                 handler: function() {
         *                     console.log('foo trigger clicked');
         *                 }
         *             },
         *             bar: {
         *                 cls: 'my-bar-trigger',
         *                 handler: function() {
         *                     console.log('bar trigger clicked');
         *                 }
         *             }
         *         }
         *     });
         *
         * The weight value may be a negative value in order to position custom triggers
         * ahead of default triggers like that of ComboBox.
         *
         *     Ext.create('Ext.form.field.ComboBox', {
         *         renderTo: Ext.getBody(),
         *         fieldLabel: 'My Custom Field',
         *         triggers: {
         *             foo: {
         *                 cls: 'my-foo-trigger',
         *                 weight: -2, // negative to place before default triggers
         *                 handler: function() {
         *                     console.log('foo trigger clicked');
         *                 }
         *             },
         *             bar: {
         *                 cls: 'my-bar-trigger',
         *                 weight: -1,
         *                 handler: function() {
         *                     console.log('bar trigger clicked');
         *                 }
         *             }
         *         }
         *     });
         */
        triggers: undefined
    },

    childEls: [
        /**
         * @property {Ext.dom.Element} triggerWrap
         * A reference to the element which encapsulates the input field and all
         * trigger button(s). Only set after the field has been rendered.
         */
        'triggerWrap',

        /**
         * @property {Ext.dom.Element} inputWrap
         * A reference to the element that wraps the input element. Only set after the
         * field has been rendered.
         */
        'inputWrap'
    ],

    triggerWrapCls: Ext.baseCSSPrefix + 'form-trigger-wrap',

    triggerWrapFocusCls: Ext.baseCSSPrefix + 'form-trigger-wrap-focus',
    triggerWrapInvalidCls: Ext.baseCSSPrefix + 'form-trigger-wrap-invalid',

    cls: Ext.baseCSSPrefix + 'displayfieldwithtriggers',

    preSubTpl: [
        '<div id="{cmpId}-triggerWrap" data-ref="triggerWrap"',
        '<tpl if="ariaEl == \'triggerWrap\'">',
        '<tpl foreach="ariaElAttributes"> {$}="{.}"</tpl>',
        '<tpl else>',
        ' role="presentation"',
        '</tpl>',
        ' class="{triggerWrapCls} {triggerWrapCls}-display {triggerWrapCls}-{ui}">',
        '<div id={cmpId}-inputWrap data-ref="inputWrap"',
        ' role="presentation" class="' + Ext.baseCSSPrefix + 'input-wrap ' + Ext.baseCSSPrefix + 'input-wrap-{ui}">'
    ],

    postSubTpl: [
        '</div>', // end inputWrap
        '<tpl for="triggers">{[values.renderTrigger(parent)]}</tpl>',
        '</div>' // end triggerWrap
    ],

    getSubTplData: function (fieldData) {
        var me = this;
        var ret = Ext.apply(me.callParent([fieldData]), {
            value: this.getDisplayValue(),
            triggerWrapCls: me.triggerWrapCls,
            triggers: me.orderedTriggers
        });

        return ret;
    },

    onRender: function () {
        var me = this,
            triggers = me.getTriggers(),
            elements = [],
            id;

        me.callParent();

        if (triggers) {
            this.invokeTriggers('onFieldRender');

            /**
             * @property {Ext.CompositeElement} triggerEl
             * @deprecated 5.0
             * A composite of all the trigger button elements. Only set after the field has
             * been rendered.
             */
            for (id in triggers) {
                elements.push(triggers[id].el);
            }
            // for 4.x compat, also set triggerCell
            me.triggerEl = me.triggerCell = new Ext.CompositeElement(elements, true);
        }
    },

    afterRender: function () {
        var me = this;
        me.callParent();
        me.invokeTriggers('afterFieldRender');
    },

    applyTriggers: function (triggers) {
        var me = this,
            hideAllTriggers = me.getHideTrigger(),
            orderedTriggers = me.orderedTriggers = [],
            repeatTriggerClick = me.repeatTriggerClick,
            id, triggerCfg, trigger, triggerCls, i;

        //<debug>
        if (me.rendered) {
            Ext.raise("Cannot set triggers after field has already been rendered.");
        }

        // don't warn if we have both triggerCls and triggers, because picker field
        // uses triggerCls to style the "picker" trigger.
        if ((me.triggerCls && !triggers) || me.trigger1Cls) {
            Ext.log.warn("Ext.form.field.Text: 'triggerCls' and 'trigger<n>Cls'" +
                " are deprecated.  Use 'triggers' instead.");
        }
        //</debug>

        if (!triggers) {
            // For compatibility with 4.x, transform the trigger<n>Cls configs into the
            // new "triggers" config.
            triggers = {};

            if (me.triggerCls && !me.trigger1Cls) {
                me.trigger1Cls = me.triggerCls;
            }

            // Assignment in conditional test is deliberate here
            for (i = 1; (triggerCls = me['trigger' + i + 'Cls']); i++) { // jshint ignore:line
                triggers['trigger' + i] = {
                    cls: triggerCls,
                    extraCls: Ext.baseCSSPrefix + 'trigger-index-' + i,
                    handler: 'onTrigger' + i + 'Click',
                    compat4Mode: true,
                    scope: me
                };
            }
        }

        for (id in triggers) {
            if (triggers.hasOwnProperty(id)) {
                triggerCfg = triggers[id];
                triggerCfg.field = me;
                triggerCfg.id = id;

                /*
                 * An explicitly-configured 'triggerConfig.hideOnReadOnly : false' allows {@link #hideTrigger} analysis
                 */
                if (hideAllTriggers && triggerCfg.hidden !== false) {
                    triggerCfg.hidden = true;
                }
                if (repeatTriggerClick && (triggerCfg.repeatClick !== false)) {
                    triggerCfg.repeatClick = true;
                }

                trigger = triggers[id] = CMDBuildUI.view.fields.displaywithtriggers.Trigger.create(triggerCfg);
                orderedTriggers.push(trigger);
            }
        }

        Ext.Array.sort(orderedTriggers, CMDBuildUI.view.fields.displaywithtriggers.Trigger.weightComparator);

        return triggers;
    },

    /**
     * Invokes a method on all triggers.
     * @param {String} methodName
     * @private
     */
    invokeTriggers: function (methodName, args) {
        var me = this,
            triggers = me.getTriggers(),
            id, trigger;

        if (triggers) {
            for (id in triggers) {
                if (triggers.hasOwnProperty(id)) {
                    trigger = triggers[id];
                    // IE8 needs "|| []" if args is undefined
                    trigger[methodName].apply(trigger, args || []);
                }
            }
        }
    },

    /**
     * Returns the trigger with the given id
     * @param {String} id
     * @return {CMDBuildUI.view.fields.displaywithtriggers.Trigger}
     */
    getTrigger: function (id) {
        return this.getTriggers()[id];
    },

    updateHideTrigger: function (hideTrigger) {
        this.invokeTriggers(hideTrigger ? 'hide' : 'show');
    },

    doDestroy: function () {
        var me = this;

        me.invokeTriggers('destroy');
        Ext.destroy(me.triggerRepeater);

        me.callParent();
    }
});