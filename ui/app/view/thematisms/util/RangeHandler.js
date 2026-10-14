Ext.define('CMDBuildUI.thematisms.util.RangeHandler', {
    singleton: true,

    rangeHandler: function (min, max, segments, cmdbuildtype, config) {
        this.min = min;
        this.max = max;
        this.segments = segments;
        this.cmdbuildtype = cmdbuildtype;
        this.config = config;
        this.baseStep = (max - min) / this.segments;

        this.ranges = [];

        this._calculateSteps = function () {
            switch (this.cmdbuildtype) {
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint:
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer:
                    for (var i = 0; i <= segments; i++) {
                        this.ranges[i] = CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(
                            Math.round((this.baseStep * i) + this.min),
                            this.config);
                    }
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal:
                    for (var i = 0; i <= segments; i++) {
                        this.ranges[i] = CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(
                            (this.baseStep * i) + this.min,
                            this.config);
                    }
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double:
                    for (var i = 0; i <= segments; i++) {
                        this.ranges[i] = CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(
                            (this.baseStep * i) + this.min,
                            this.config);
                    }

                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date:
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime:
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time:
                    break;
                default:
                    console.error('Cmdbuild Type not recognized');
            }
        }

        this._calculateSteps();
    }
})