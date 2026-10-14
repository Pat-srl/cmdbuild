-- add single table for custom components

SELECT _cm3_class_create('_UiComponent', 'MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: Type|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: Data|TYPE: bytea|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: Active|TYPE: boolean|DEFAULT: true|NOTNULL: true');
SELECT _cm3_attribute_notnull_set('"_UiComponent"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_UiComponent"', 'Code', 'Type');
ALTER TABLE "_UiComponent" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('contextmenu','custompage','widget') );

SELECT _cm3_class_triggers_disable('"_UiComponent"');

INSERT INTO "_UiComponent" ("Id","CurrentId","BeginDate","IdClass","Type","Code","Description","Data","Active","Status","User") 
    SELECT "Id","CurrentId","BeginDate",'"_UiComponent"'::regclass,'contextmenu',"Code","Description","Data","Active",'A',"User" FROM "_ContextMenuComp" WHERE "Status" = 'A';
INSERT INTO "_UiComponent" ("Id","CurrentId","BeginDate","IdClass","Type","Code","Description","Data","Active","Status","User") 
    SELECT "Id","CurrentId","BeginDate",'"_UiComponent"'::regclass,'custompage',"Code","Description","Data","Active",'A',"User" FROM "_CustomPage" WHERE "Status" = 'A';

SELECT _cm3_class_triggers_enable('"_UiComponent"');

DROP TABLE "_ContextMenuComp" CASCADE;
DROP TABLE "_CustomPage" CASCADE;
