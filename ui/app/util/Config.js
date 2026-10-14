/**
 * @file CMDBuildUI.util.Config
 * @module CMDBuildUI.util.Config
 * @author PAT srl
 * @access public
 */
(function () {
    window.cmdbuildConfig = window.cmdbuildConfig || {
        baseUrl: ''
    };
    Ext.define('CMDBuildUI.util.Config', {
        singleton: true,

        /**
         * @constant {String} baseUrl
         * Webservices base url.
         */
        baseUrl: window.cmdbuildConfig.baseUrl,

        /**
         * @constant {String} gatesBaseUrl
         * Gates base url.
         */
        gatesBaseUrl: window.cmdbuildConfig.baseUrl.replace('/rest/v4', '') + '/etl/gate',

        /**
         * @constant {String} socketUrl
         * Socket url.
         */
        socketUrl: window.cmdbuildConfig.socketUrl,

        /**
         * @constant {String} geoserverBaseUrl
         * GEO server url.
         */
        geoserverBaseUrl: window.cmdbuildConfig.geoserverBaseUrl,

        /**
         * @constant {Numeric} ajaxTimeout
         * AJAX timeout in milliseconds.
         */
        ajaxTimeout: 15000, // milliseconds

        /**
         * @constant {String} uiBaseUrl
         * UI base url.
         */
        uiBaseUrl: window.location.origin + window.location.pathname,

        widgets: {
            aiassistant: 'widgets-aiassistant-panel',
            customForm: 'widgets-customform-panel',
            linkCards: 'widgets-linkcards-panel',
            createModifyCard: 'widgets-createmodifycard-panel',
            goToCard: 'widgets-gotocard-panel',
            createReport: 'widgets-createreport-panel',
            openAttachment: 'widgets-attachmentwidget-panel',
            openNote: 'widgets-notewidget-panel',
            manageEmail: 'widgets-manageemail-panel',
            calendar: 'widgets-calendar-panel',
            workflow: 'widgets-startworkflow-panel',
            startWorkflow: 'widgets-startworkflow-panel',
            presetFromCard: 'widgets-presetfromcard-panel',
            sequenceView: 'widgets-sequenceview-panel'
        }
    });
})();
