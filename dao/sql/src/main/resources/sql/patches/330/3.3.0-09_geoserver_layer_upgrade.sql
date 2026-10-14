-- geoserver layer upgrade

SELECT _cm3_class_triggers_disable('"_GisAttribute"');

UPDATE "_GisAttribute" SET "Type" = lower("Type");

SELECT _cm3_class_triggers_enable('"_GisAttribute"');

ALTER TABLE "_GisAttribute" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('point','linestring','polygon','shape','geotiff') );

SELECT _cm3_class_create('_GisGeoserverLayer', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Gis Geoserver Layer|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_GisGeoserverLayer"', 'OwnerClass', 'regclass', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_GisGeoserverLayer"', 'Attribute', 'varchar', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_GisGeoserverLayer"', 'OwnerCard', 'bigint', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_GisGeoserverLayer"', 'GeoserverStore', 'varchar', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_GisGeoserverLayer"', 'GeoserverLayer', 'varchar', 'NOTNULL: true');

SELECT _cm3_attribute_index_unique_create('"_GisGeoserverLayer"', 'OwnerClass', 'Attribute', 'OwnerCard'); 
-- SELECT _cm3_attribute_index_unique_create('"_GisGeoserverLayer"', 'GeoserverStore'); 

INSERT INTO "_GisAttribute" ("Code","Description","Type","Owner","Index","MinimumZoom","DefaultZoom","MaximumZoom","Visibility","Active")
    SELECT "Code","Description",lower("Type"),"OwnerClass","Index","MinimumZoom","DefaultZoom","MaximumZoom","Visibility","Active" FROM "_GisLayer" WHERE "Status" = 'A';

-- TODO migrate id, begindate, user ??
INSERT INTO "_GisGeoserverLayer" ("Attribute","OwnerClass","OwnerCard","GeoserverStore","GeoserverLayer") SELECT "Code","OwnerClass","OwnerCard","Code","GeoserverName" FROM "_GisLayer" WHERE "Status" = 'A';

DROP TABLE "_GisLayer" CASCADE;