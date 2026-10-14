-- updated menu constraints and menu elements translations

DO $$ DECLARE
    _menu RECORD;
BEGIN
    PERFORM _cm3_attribute_index_delete('"_Menu"', 'GroupName', 'TargetDevice');
    FOR _menu IN SELECT * FROM "_Menu" WHERE "Status" = 'A' LOOP
	UPDATE "_Menu" SET "Code" = _menu."GroupName" || '_' || _menu."TargetDevice" WHERE "Id" = _menu."Id" AND "Status" = 'A';
    END LOOP;
    PERFORM _cm3_attribute_index_create('"_Menu"', TRUE, 'Code');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_menu_code() RETURNS trigger AS $$ BEGIN
    NEW."Code" = NEW."GroupName" || '_' || NEW."TargetDevice";
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE TRIGGER _cm3_create_menu_code BEFORE INSERT OR UPDATE ON "_Menu" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_menu_code();

DO $$ DECLARE
    _menu record;
    _code varchar;
BEGIN
FOR _menu IN SELECT * FROM "_Menu" WHERE "Status" = 'A' LOOP
    FOR _code IN SELECT jsonb_array_elements(_cm3_utils_menu_to_list(_menu."Data"))->>'code' LOOP
        UPDATE "_Translation" SET "Code" = 'menuitem.' || _menu."Code" || '.' || _code || '.description' WHERE "Code" = 'menuitem.' || _code || '.description' AND "Status"='A';
    END LOOP;
END LOOP;
END $$ LANGUAGE PLPGSQL;