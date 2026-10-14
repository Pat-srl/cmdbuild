-- upgrade view table to std table, add active flag

CREATE TABLE _patch_aux_view AS SELECT "Id","User","BeginDate","Name","Description","Filter","IdSourceClass","SourceFunction","Type" FROM "_View";

DROP TABLE "_View";

SELECT _cm3_class_create('_View','MODE: reserved');
SELECT _cm3_attribute_create('"_View"', 'Type', 'TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('"_View"', 'Filter', 'TYPE: varchar');
SELECT _cm3_attribute_create('"_View"', 'SourceFunction', 'TYPE: varchar');
SELECT _cm3_attribute_create('"_View"', 'IdSourceClass', 'TYPE: regclass');
SELECT _cm3_attribute_create('"_View"', 'Active', 'TYPE: boolean|NOTNULL: true|DEFAULT: true');

SELECT _cm3_class_triggers_disable('"_View"');

INSERT INTO "_View" ("Id","IdClass","CurrentId","Status","User","BeginDate","Code","Description","Filter","IdSourceClass","SourceFunction","Type") 
    SELECT "Id",'"_View"'::regclass,"Id",'A',"User","BeginDate","Name","Description","Filter","IdSourceClass","SourceFunction","Type" FROM _patch_aux_view;

SELECT _cm3_class_triggers_enable('"_View"');

DROP TABLE _patch_aux_view;
