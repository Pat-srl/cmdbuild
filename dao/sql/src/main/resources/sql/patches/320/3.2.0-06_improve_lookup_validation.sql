-- lookup validation

CREATE TABLE _patch_aux_lookup (id bigint primary key, parentid bigint not null, status char(1) not null);

INSERT INTO _patch_aux_lookup(id, parentid, status) SELECT "Id", "ParentId", "Status" FROM "LookUp" WHERE "ParentId" IS NOT NULL;

ALTER TABLE "LookUp" DROP COLUMN "ParentId";

SELECT _cm3_attribute_create('"LookUp"', 'ParentId', 'bigint', 'FKTARGETCLASS: LookUp');

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM _patch_aux_lookup WHERE status = 'A' LOOP
        IF NOT EXISTS (SELECT * FROM "LookUp" WHERE "Id" = _record.parentid and "Status" = 'A') THEN
            RAISE WARNING 'CM: invalid lookup parent id for record = %, delete record', _record.id;
            UPDATE "LookUp" SET "Status" = 'N' WHERE "Id" = _record.id;
        ELSE
            UPDATE "LookUp" SET "ParentId" = _record.parentid WHERE "Id" = _record.id;
        END IF;
    END LOOP;

    DELETE FROM _patch_aux_lookup WHERE NOT EXISTS (SELECT * FROM "LookUp" WHERE "Id" = parentid);

    PERFORM _cm3_class_triggers_disable('"LookUp"');
    FOR _record IN SELECT * FROM _patch_aux_lookup LOOP
        RAISE NOTICE 'CM: upgrade lookup parent id for record = %', _record.id;
        UPDATE "LookUp" SET "ParentId" = _record.parentid WHERE "Id" = _record.id;
    END LOOP;
    PERFORM _cm3_class_triggers_enable('"LookUp"');

END $$ LANGUAGE PLPGSQL;

DROP TABLE _patch_aux_lookup;
