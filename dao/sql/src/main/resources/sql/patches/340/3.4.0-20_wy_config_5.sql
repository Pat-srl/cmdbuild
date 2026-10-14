-- waterway config format fix

DO $$ DECLARE
    _config jsonb;
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_EtlConfig" WHERE "Status" = 'A' LOOP
        _config = _record."Config";
        IF _cm3_utils_is_not_blank(_config->>'module') THEN
            _config = _config - 'module' || jsonb_build_object('descriptor', _config->>'module');
            UPDATE "_EtlConfig" SET "Config" = _config, "Data" = _config::varchar WHERE "Id" = _record."Id";
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Function" DROP CONSTRAINT "_cm3_Category_check";

UPDATE "_Function" SET "Category" = 'bus' WHERE "Category" = 'module';

ALTER TABLE "_Function" ADD CONSTRAINT "_cm3_Category_check" CHECK ("Category" IN ('system', 'bus'));
