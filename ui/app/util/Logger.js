/**
 * @file CMDBuildUI.util.Logger
 * @module CMDBuildUI.util.Logger
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.Logger', {
    singleton: true,

    /**
     * @constant {Object} levels
     * @property {String} error
     * @property {String} warn
     * @property {String} info
     * @property {String} log
     * @property {String} debug
     */
    levels: {
        error: 'error',
        debug: 'debug',
        warn: 'warn',
        info: 'info',
        log: 'log'
    },

    /**
     * Log into browser console.
     *
     * @param {String} message The message to log (required).
     * @param {String} level One {@link CMDBuildUI.util.Logger#levels CMDBuildUI.util.Logger.levels} property. Default: CMDBuildUI.util.Logger.levels.log
     * @param {Numeric} [code] The known code of the message.
     * @param {Object} [dump] An object to dump to the log as part of the message.
     */
    log: function (message, level, code, dump) {
        const log = {};

        // set level
        log.level = level;
        if (!level) {
            log.level = this.levels.info;
        }

        log.msg = '';
        if (code) {
            log.msg += 'Err.code: ' + code + ' - ';
        }
        log.msg += message;

        // dump object
        if (dump) {
            log.dump = dump;
        }

        Ext.log(log);
    }
});
