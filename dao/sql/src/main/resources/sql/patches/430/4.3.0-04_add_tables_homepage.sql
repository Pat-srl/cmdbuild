-- Create table _Homepage

-- * create tables anche indexes

SELECT _cm3_class_create('NAME: _Homepage|MODE: reserved|TYPE: class|DESCR: Homepage');
SELECT _cm3_attribute_create('OWNER: _Homepage|NAME: Active|TYPE: boolean|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _Homepage|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _Homepage|NAME: GroupName|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _Homepage|NAME: IconFont|TYPE: varchar');

SELECT _cm3_attribute_notnull_set('"_Homepage"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_Homepage"', 'Code');

-- * create functions

CREATE OR REPLACE FUNCTION _cm3_homepage_config_check(_config JSONB) RETURNS BOOLEAN
    IMMUTABLE
    SET search_path = public
    LANGUAGE PLPGSQL
AS
$$  DECLARE
    _val varchar;
    _res boolean;
BEGIN
    _res = TRUE;
    IF _config IS NULL OR _config->'components' IS NULL OR _config->'layout' IS NULL THEN
        RAISE WARNING 'invalid homepage config structure';
        RETURN false;
    END IF;
    FOR _val IN WITH
                    _component_ids AS (SELECT _component->>'_id' _id FROM jsonb_array_elements(_config->'components') _component),
                    _rows AS (SELECT r _row FROM jsonb_array_elements(_config->'layout'->'rows') r),
                    _cols AS (SELECT jsonb_array_elements(_row->'columns') _col FROM _rows),
                    _layout_components_ids AS (SELECT jsonb_array_elements_text(_col->'components') _id FROM _cols)
                SELECT _id FROM _component_ids WHERE _id NOT IN (SELECT _id FROM _layout_components_ids) UNION SELECT _id FROM _layout_components_ids WHERE _id NOT IN (SELECT _id FROM _component_ids) LOOP
            RAISE WARNING 'found mismatching homepage component id = %', _val;
            _res = FALSE;
        END LOOP;
    RETURN _res;
END;
$$;

-- * create constraints

DO $$ BEGIN

    IF NOT EXISTS (SELECT 1
                   FROM pg_constraint c
                       JOIN pg_class t ON t.oid = c.conrelid
                       JOIN pg_namespace n ON n.oid = t.relnamespace
                   WHERE n.nspname = current_schema()
                     AND t.relname = '_Homepage'
                     AND c.conname = '_cm3_IdClass_check') THEN
        ALTER TABLE "_Homepage" ADD CONSTRAINT "_cm3_IdClass_check" CHECK(("IdClass")::OID = ('"_Homepage"'::regclass)::OID);
    END IF;

    ALTER TABLE "_Homepage" ALTER COLUMN "IdClass" SET DEFAULT '"_Homepage"'::regclass;

    IF NOT EXISTS (SELECT 1
                   FROM pg_constraint c
                       JOIN pg_class t ON t.oid = c.conrelid
                       JOIN pg_namespace n ON n.oid = t.relnamespace
                   WHERE n.nspname = current_schema()
                     AND t.relname = '_Homepage'
                     AND c.conname = '_cm3_Config_check') THEN
        ALTER TABLE "_Homepage" ADD CONSTRAINT "_cm3_Config_check" CHECK (("Status" <> 'A'::BPCHAR) OR _cm3_homepage_config_check("Config"));
    END IF;

END $$;

-- * create triggers

CREATE OR REPLACE FUNCTION _cm3_trigger_homepage_cleanup() returns trigger
    language plpgsql
as
$$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'homepage' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
            RAISE NOTICE 'cascade drop grant record = %', _record;
            EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
        END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((
                                                            SELECT jsonb_agg(x)
                                                            FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
                                                            WHERE NOT (x->>'type' = 'homepage' AND x->>'target' = OLD."Code"))
                                                        )
    WHERE "Status" = 'A'
      AND EXISTS (
                SELECT x
                FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
                WHERE x->>'type' = 'homepage'
                  AND x->>'target' = OLD."Code");
    RETURN NEW;
END $$;

-- * Add triggers to _Homepage table

DO $$ BEGIN

    CREATE TRIGGER "_cm3_trigger_homepage_cleanup" AFTER UPDATE
        ON "_Homepage"
        FOR EACH ROW
        WHEN (old."Status" <> 'N'::bpchar AND new."Status" = 'N'::bpchar)
    EXECUTE PROCEDURE _cm3_trigger_homepage_cleanup();

END $$;

-- * create function for user mapping

CREATE OR REPLACE FUNCTION _cm3_session_user_mapping_get(_user_id bigint) RETURNS
    SETOF bigint
    LANGUAGE PLPGSQL
AS
$$ BEGIN
    IF _user_id = -1 THEN
--       RETURN QUERY SELECT m."Id", m."IdClass" FROM "Employee" as m WHERE "Employee"."Status" = 'A';
        RETURN;
    ELSE
--        RETURN QUERY SELECT m."Id", m."IdClass" FROM "Employee" as m JOIN "Map_UserEmployee" ON m."Id" = "Map_UserEmployee"."IdObj2" WHERE "Map_UserEmployee"."IdObj1" = _user_id  AND "m."Status" = 'A' AND "Map_UserEmployee"."Status" = 'A';
        RETURN;
    END IF;
END $$;