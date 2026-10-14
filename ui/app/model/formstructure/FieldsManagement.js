(function () {
    var structure = {
        "fieldset1": {
            "rows": [
                {
                    "columns": [
                        {
                            "attribute": "Code",
                            "empty": false
                        },
                        {
                            "attribute": null,
                            "empty": true
                        }
                    ]
                },
                {
                    "columns": [
                        {
                            "attribute": "Address",
                            "empty": false
                        },
                        {
                            "attribute": "Postcode",
                            "empty": false
                        },
                        {
                            "attribute": "City",
                            "empty": false
                        }
                    ]
                },
                {
                    "columns": [
                        {
                            "attribute": "Region",
                            "empty": false
                        },
                        {
                            "attribute": "Country",
                            "empty": false
                        }
                    ]
                }
            ]
        }
    };
    Ext.define('CMDBuildUI.model.formstructure.FieldsManagement', {
        extend: 'CMDBuildUI.model.base.Base',

        fields: [{
            name: 'layout',
            type: 'string',
            defaultValue:'form'
        }, {
            name: 'structure',
            type: 'string',
            defaultValue: Ext.JSON.encode(structure)
        }, {
            name: 'template',
            type: 'auto',
            calculate: function (data) {
                return Ext.JSON.decode(data.structure);
            }
        }, {
            name: 'active',
            type: 'boolean',
            defaultValue: true
        }],


        proxy: {
            type: 'memory'
        }
    });
})();
