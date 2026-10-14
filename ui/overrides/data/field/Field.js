Ext.define('Override.data.field.Field', {
    override: 'Ext.data.field.Field',

    getDescription: function () {
        if (this._localized_description != null) {
            return this._localized_description;
        }

        var description = null;

        if (Ext.isObject(this.attributeconf) && !Ext.Object.isEmpty(this.attributeconf)) {
            description = this.attributeconf._description_translation;
        }

        var localized = this.localized;
        if (Ext.isEmpty(description) && Ext.isObject(localized) && !Ext.Object.isEmpty(localized)) {

            var prop = 'description';
            var value = localized[prop];

            if (Ext.isString(value) && Ext.String.startsWith(value, "CMDBuildUI.locales.Locales")) {
                try {
                    description = this._localized_description = eval(value);
                } catch (e) {
                    this[prop] = value;
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format("Label {0} not found", value),
                        CMDBuildUI.util.Logger.levels.error
                    );
                }

            }
        }

        if (Ext.isEmpty(description)) {
            description = this.description
            this._localized_description = description;
        }

        return description;
    }
});