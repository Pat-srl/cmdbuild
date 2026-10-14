-- improved attribute groups


SELECT _cm3_attribute_create('"_AttributeGroup"', 'Index', 'int', '');
SELECT _cm3_attribute_create('"_AttributeGroup"', 'Owner', 'regclass', '');

SELECT _cm3_attribute_index_delete('"_AttributeGroup"','Code');

DO $$ DECLARE
    _attr record;
    _group record;
BEGIN
    CREATE TABLE _patch_aux AS SELECT * FROM _cm3_attribute_list_detailed() a;
    FOR _attr IN WITH q AS (SELECT owner, name, comment->>'GROUP' groupname from _patch_aux a) SELECT * FROM q WHERE NULLIF(groupname, '') IS NOT NULL LOOP
        IF NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Owner" = _attr.owner AND "Code" = _attr.groupname AND "Status" = 'A') THEN
            INSERT INTO "_AttributeGroup" ("Code", "Description", "Owner") SELECT "Code", "Description", _attr.owner FROM "_AttributeGroup" WHERE "Code" = _attr.groupname AND "Status" = 'A' AND "Owner" IS NULL;
        END IF;
    END LOOP;
    UPDATE "_AttributeGroup" g SET "Index" = COALESCE((SELECT MIN(COALESCE(comment->>'INDEX','9999')::int) from _patch_aux a WHERE a.owner = g."Owner" AND comment->>'GROUP' = g."Code"), 9999) WHERE "Status" = 'A' AND "Owner" IS NOT NULL;
    DROP TABLE _patch_aux;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"_AttributeGroup"');

DELETE FROM "_AttributeGroup" WHERE "Owner" IS NULL OR "Status" <> 'A';

UPDATE "_AttributeGroup" g SET "Index" = (WITH q AS (SELECT ROW_NUMBER() OVER(ORDER BY "Index") _row_number,"Id" FROM "_AttributeGroup" WHERE "Owner" = g."Owner") SELECT _row_number FROM q WHERE "Id" = g."Id");

SELECT _cm3_class_triggers_enable('"_AttributeGroup"');

SELECT _cm3_attribute_notnull_set('"_AttributeGroup"', 'Index');
SELECT _cm3_attribute_notnull_set('"_AttributeGroup"', 'Owner');

SELECT _cm3_attribute_index_unique_create('"_AttributeGroup"', 'Owner', 'Code');
