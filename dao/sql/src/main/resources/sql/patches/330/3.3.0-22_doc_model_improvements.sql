-- doc model classes, lookup refactoring


SELECT _cm3_attribute_create('"DmsModel"', 'MimeType', 'varchar', 'NOTNULL: true|MODE: syshidden|DESCR: Mime Type'); 
SELECT _cm3_attribute_create('"DmsModel"', 'Category', 'varchar', 'MODE: syshidden|DESCR: Category');
SELECT _cm3_attribute_create('"DmsModel"', 'Hash', 'varchar', 'NOTNULL: true|MODE: syshidden|DESCR: File Hash');
SELECT _cm3_attribute_create('"DmsModel"', 'Size', 'bigint', 'NOTNULL: true|MODE: syshidden|DESCR: File Size');
SELECT _cm3_attribute_create('"DmsModel"', 'CreationDate', 'timestamp', 'NOTNULL: true|MODE: syshidden|DESCR: Creation Date');

SELECT _cm3_attribute_notnull_set('"DmsModel"', 'FileName');
SELECT _cm3_attribute_notnull_set('"BaseDocument"', 'FileName');
SELECT _cm3_attribute_notnull_set('"DmsModel"', 'Version');
SELECT _cm3_attribute_notnull_set('"BaseDocument"', 'Version');
SELECT _cm3_attribute_notnull_set('"DmsModel"', 'Card');
SELECT _cm3_attribute_notnull_set('"BaseDocument"', 'Card');
SELECT _cm3_attribute_notnull_set('"DmsModel"', 'DocumentId');
SELECT _cm3_attribute_notnull_set('"BaseDocument"', 'DocumentId');
SELECT _cm3_attribute_unique_set('"DmsModel"', 'DocumentId');
SELECT _cm3_attribute_unique_set('"BaseDocument"', 'DocumentId');

SELECT _cm3_attribute_index_unique_create('"DmsModel"', 'Card', 'FileName');
SELECT _cm3_attribute_index_unique_create('"BaseDocument"', 'Card', 'FileName');

DO $$ BEGIN
    IF _cm3_system_config_get('org.cmdbuild.dms.service.type') = 'postgres' THEN

        PERFORM _cm3_class_triggers_disable('"_Document"');
        UPDATE "_Document" SET "Code" = _cm3_utils_random_id() WHERE "Status" = 'A';
        UPDATE "_Document" d SET "Code" = (SELECT "Code" FROM "_Document" cur WHERE "Id" = d."CurrentId" AND "Status" = 'A') WHERE "Status" <> 'A';
        PERFORM _cm3_class_triggers_enable('"_Document"');

        CREATE TABLE _patch_aux AS SELECT "_Document"."Code" document_id, "_Document"."Version" _version, "_DocumentData"."Data" _data FROM "_DocumentData" JOIN "_Document" ON "_DocumentData"."DocumentId" = "_Document"."Id";
        
        INSERT INTO "BaseDocument" ("Card","DocumentId","FileName","MimeType","Version","Category","Hash","Size","CreationDate","Description","Notes")
            SELECT "CardId","Code","FileName","MimeType","Version","Category","Hash","Size","CreationDate","Description","Notes" FROM "_Document" WHERE "Status" = 'A';
    
    END IF;

    DROP TABLE "_Document_history";
    DROP TABLE "_Document"; 

    DROP TABLE "_DocumentData";

    PERFORM _cm3_class_create('NAME: _Document|MODE: reserved|TYPE: simpleclass|DESCR: Document data');
    PERFORM _cm3_attribute_create('"_Document"', 'DocumentId', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Document id');
    PERFORM _cm3_attribute_create('"_Document"', 'Version', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Document version');
    PERFORM _cm3_attribute_create('"_Document"', 'Data', 'bytea', 'NOTNULL: true|MODE: read|DESCR: Data');

    PERFORM _cm3_attribute_index_unique_create('"_Document"', 'DocumentId', 'Version');  
         
    IF _cm3_system_config_get('org.cmdbuild.dms.service.type') = 'postgres' THEN
        INSERT INTO "_Document" ("DocumentId", "Version", "Data") SELECT document_id, _version, _data FROM _patch_aux WHERE EXISTS (SELECT * FROM "DmsModel" WHERE "DocumentId" = document_id AND "Version" = _version);
        DROP TABLE _patch_aux;
    END IF;
END $$ LANGUAGE PLPGSQL;
