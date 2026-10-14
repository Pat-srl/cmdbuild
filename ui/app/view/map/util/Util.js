Ext.define('CMDBuildUI.map.util.Util', {
    singleton: true,
    DRAWMODIFYLAYERNAME: 'DRAWMODIFYLAYER',

    DRAWINTERACTIONNAME: 'DRAWINTERACTION',
    MODIFYINTERACTIONNAME: 'MODIFYINTERACTION',

    DRAWADD: 'add',
    DRAWREMOVE: 'remove',

    /**
     * 
     * @param {*} olCoordinates 
     */
    olCoordinatesToObject: function (type, olCoordinates) {
        type = type.toLowerCase();
        const points = [];

        switch (type) {
            case 'point':
                return {
                    x: olCoordinates[0],
                    y: olCoordinates[1]
                };
            case 'linestring':
                Ext.Array.forEach(olCoordinates, function (item, index, array) {
                    points.push({
                        x: item[0] || item.x,
                        y: item[1] || item.y
                    });
                }, this);
                return points;
            case 'polygon':
                Ext.Array.forEach(olCoordinates[0], function (item, index, array) {
                    points.push({
                        x: item[0] || item.x,
                        y: item[1] || item.y
                    });
                }, this);
                return points;
        }
    }
});