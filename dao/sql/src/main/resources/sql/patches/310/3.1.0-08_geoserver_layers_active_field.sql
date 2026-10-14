--  added active field to GeoServer Layers and gis attribute

SELECT _cm3_attribute_create('"_GisLayer"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true');
SELECT _cm3_attribute_create('"_GisAttribute"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true');
