Ext.define('Override.form.field.VTypes', {
    override: 'Ext.form.field.VTypes',

    /**
     * @cfg {RegExp} usernameValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    usernameValidationRe:
        /^([\w0-9._-]+$)|((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-||_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+([a-z]+|\d|-|\.{0,1}|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])?([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/,

    /**
     * @cfg {String} usernameValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    usernameValidationText:
        'Must cantain an email address or alphanumeric char with ._- \n(valid ex. "jhon.doe" or "jhon_doe@acme-industries.com',

    /**
     * The function used to validate username
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    usernameValidation: function (value, field) {
        return this.usernameValidationRe.test(value);
    },

    /**
     * The function used to check if password value match to passwordConfirmation
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    passwordMatch: function (value, field) {
        var up = field.up().getXType();
        var password = '';
        if (field.up(up)) {
            password = field.up(up).down('[reference=password]');
        }

        return value == password.getValue();
    },
    /**
     * @cfg {String} passwordMatchText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    passwordMatchText: "Passwords doesn't match",
    /**
     * The function used to validate password values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */

    /**
     * @cfg {RegExp} IPv4AddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPv4AddressRe:
        /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\/(3[0-2]|[1-2][0-9]|[1-9]))?$/,
    /**
     * @cfg {String} IPv4AddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPv4AddressText: 'Must be a numeric IP address',
    /**
     * @cfg {RegExp} IPv4AddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPv4AddressMask: /[\d\.\/]/i,
    /**
     * The function used to validate IP v4 values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPv4Address: function (value) {
        return this.IPv4AddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPv6AddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPv6AddressRe:
        /^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*(\/([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))?$/,
    /**
     * @cfg {String} IPv6AddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPv6AddressText: 'Must be a valid IP v6 address',
    /**
     * @cfg {RegExp} IPv6AddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPv6AddressMask: /[0-9a-fA-F:\/]/i,
    /**
     * The function used to validate IP v6 values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPv6Address: function (value) {
        return this.IPv6AddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPAddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    IPAddressRe:
        /(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\/(3[0-2]|[1-2][0-9]|[1-9]))?$)|(^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*(\/([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))?$)/,
    /**
     * @cfg {String} IPAddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    IPAddressText: 'Must be a valid IP address',
    /**
     * @cfg {RegExp} IPAddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    IPAddressMask: /([\d\.])|([0-9a-fA-F:\/])/i,
    /**
     * The function used to validate IP values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    IPAddress: function (value) {
        return this.IPAddressRe.test(value);
    },

    /**
     * @cfg {RegExp} IPAddressRe
     * RegExp for the value to be tested against within the validation function.
     */
    timeRe: /^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])(:([0-5]?[0-9]))?$/,
    /**
     * @cfg {String} IPAddressText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    timeText: 'Must be a valid time 24-hour clock',
    /**
     * @cfg {RegExp} IPAddressMask
     * vtype Mask property: The keystroke filter mask.
     */
    timeMask: /([\d{2}\:])/,
    /**
     * The function used to validate time values
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    time: function (value) {
        return this.timeRe.test(value);
    },

    /**
     * @cfg {RegExp} nameInputValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    nameInputValidationRe: /^[a-zA-Z]+[a-zA-Z0-9_]+$/,

    nameInputValidationMask: /^[a-zA-Z0-9_]$/,

    /**
     * @cfg {String} nameInputValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */

    nameInputValidationText:
        'This field allows only alphanumeric characters and underscore (_).</br>Underscore and numbers can not be used as first character.',
    /**
     * The function used to validate name input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    nameInputValidation: function (value, field) {
        return this.nameInputValidationRe.test(value);
    },

    /**
     * @cfg {RegExp} nameInputValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    nameInputValidationWithDashRe: /^[a-zA-Z]+[a-zA-Z0-9_-]+$/,

    nameInputValidationWithDashMask: /^[a-zA-Z0-9_-]$/,

    /**
     * @cfg {String} nameInputValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    nameInputValidationWithDashText:
        'This field can\'t start with "_" or "-" or number. Allowed char are Alphanumeric, dash and underscore.',

    /**
     * The function used to validate name input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    nameInputValidationWithDash: function (value, field) {
        return this.nameInputValidationWithDashRe.test(value);
    },

    /**
     * @cfg {RegExp} nameInputValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    lookupnameInputValidationRe: /^[0-9a-zA-Z]+($|[a-zA-Z0-9_-\s])+$/,

    /**
     * @cfg {String} nameInputValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    lookupnameInputValidationText:
        'This field allows only alphanumeric characters, dash (-) and underscore (_).</br>Underscore, dash, and numbers can not be used as first character.',

    /**
     * The function used to validate name input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    lookupnameInputValidation: function (value, field) {
        return this.lookupnameInputValidationRe.test(value);
    },

    /**
     * @cfg {RegExp} nameInputValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    lookupTypeNameInputValidationRe: /^[a-zA-Z]+($|[a-zA-Z0-9_-\s])+$/,

    /**
     * @cfg {RegExp} lookupTypeNameInputValidationMask
     * RegExp for the value mask.
     */
    lookupTypeNameInputValidationMask: /^[a-zA-Z0-9_-\s]+$/,
    /**
     * @cfg {String} nameInputValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    lookupTypeNameInputValidationText:
        'This field allows only alphanumeric characters, dash (-) and underscore (_).</br>Number, underscore, dash, and numbers can not be used as first character.',

    /**
     * The function used to validate name input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    lookupTypeNameInputValidation: function (value, field) {
        return this.lookupTypeNameInputValidationRe.test(value);
    },

    /**
     * @cfg {RegExp} hexColorValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    hexColorValidationRe: /^#([0-9a-f]{3}){1,2}$/i,
    /**
     * @cfg {String} hexColorValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    hexColorValidationText: 'This field must contain valid hex color',

    /**
     * The function used to validate color picker
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    hexColorValidation: function (value, field) {
        return this.hexColorValidationRe.test(value);
    },

    /**
     * @cfg {RegExp} attributeNameValidationRe
     * RegExp for the value to be tested against within the validation function.
     */
    attributeNameValidationRe: /^[a-zA-Z]+[a-zA-Z0-9_]+$/,

    attributeNameValidationMask: /^[a-zA-Z0-9_]$/,
    /**
     * @cfg {String} attributeNameValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    attributeNameValidationText: '',

    /**
     * The function used to validate attributes name
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    attributeNameValidation: function (value, field) {
        var isReserved =
            /^(Notes|BeginDate|EndDate|CurrentId|IdTenant)+$/i.test(value) ||
            (field.lookupViewModel().get('objectType').toLowerCase() ===
                CMDBuildUI.util.helper.ModelHelper.objecttypes.domain &&
                value === 'type');
        if (isReserved) {
            this.attributeNameValidationText = 'Attribute name is reserved';
            return false;
        }
        var isInvalidName = !this.nameInputValidationRe.test(value);
        if (isInvalidName) {
            this.attributeNameValidationText = this.nameInputValidationText;
            return false;
        }
        return true;
    },

    /**
     *
     * @param {*} value
     * @returns
     */
    multiemail: function (value) {
        var valid = true;

        if (!Ext.isEmpty(value)) {
            value = value.replace(/((["'])(?:(?=(\\?))\2.)*?\1[^<>]*)/g, '');
            var values = value.split(',');

            for (var i = 0; i < values.length && valid; i++) {
                var v = values[i];

                if (v.includes('<') && v.includes('>')) {
                    v = v.substring(v.lastIndexOf('<') + 1, v.lastIndexOf('>'));
                }

                valid *= this.email(Ext.String.trim(v));

                if (!valid) {
                    this.multiemailText = this.multiemailText_aux + v;
                }
            }
        }

        return valid;
    },

    multiemailText: 'The provided email list contains an error on email: ',
    multiemailText_aux: 'The provided email list contains an error on email: ',
    emailRegExp:
        /^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\.[a-zA-Z]{2,}$/,

    /**
     * @override
     *
     * The function used to validate URLs
     * @param {String} value The URL
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    url: function (value) {
        var urlRegex =
            '^(?:(?:http|https|ftp|ftps|ws|wss)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$';
        var url = new RegExp(urlRegex, 'i');
        return url.test(value);
    },

    /**
     * @override
     *
     * The function used to validate email addresses. Note that complete validation
     * per the email RFC specifications is very complex and beyond the scope of this class,
     * although this function can be overridden if a more comprehensive validation scheme
     * is desired. See the validation section of the [Wikipedia article on email addresses][1]
     * for additional information. This implementation is intended to validate the following
     * types of emails:
     *
     * - `barney@example.de`
     * - `barney.rubble@example.com`
     * - `barney-rubble@example.coop`
     * - `barney+rubble@example.com`
     * - `barney'rubble@example.com`
     * - `b.arne.y_r.ubbl.e@example.com`
     * - `barney4rubble@example.com`
     * - `barney4rubble!@example.com`
     * - `_barney+rubble@example.com`
     * - `"barney+rubble"@example.com`
     *
     * [1]: http://en.wikipedia.org/wiki/E-mail_address
     *
     * @param {String} value The email address
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    email: function (value) {
        return this.emailRegExp.test(value);
    },

    /**
     * @cfg {RegExp} TTCustomerCodeRe
     * RegExp for the value to be tested against within the validation function.
     */
    TTCustomerCodeRe: /^[A-Za-z0-9]{4}\-[A-Za-z0-9]{4}\-[A-Za-z0-9]{4}\-[A-Za-z0-9]{4}$/,

    /**
     * @cfg {RegExp} TTCustomerCodeMask
     * RegExp for the value to be tested against within the validation function.
     */
    TTCustomerCodeMask: /^[A-Za-z0-9-]$/,

    /**
     * @cfg {String} TTCustomerCodeText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    TTCustomerCodeText: 'Invalid code',

    /**
     * The function used to validate cobile customer code input
     * @param {String} value The value
     * @return {Boolean} true if the RegExp test passed, and false if not.
     */
    TTCustomerCode: function (value, field) {
        return this.TTCustomerCodeRe.test(value);
    },

    /**
     * @cfg {String} FaPickerValidationText
     * vtype Text property: The error text to display when the validation function returns false.
     */
    FaPickerValidationText: 'This field must contain a valid fa icon',

    /**
     * The function used to validate fa icon picker
     * @param {String} value The value
     * @return {Boolean} true if the icon exists, and false if not.
     */
    FaPickerValidation: function (value, field) {
        const icons = CMDBuildUI.util.helper.IconHelper.icons;
        const iconArray = Object.entries(icons);
        const icon = iconArray.find(function (icon) {
            return icon[1].name === value;
        });
        return !!icon;
    }
});
