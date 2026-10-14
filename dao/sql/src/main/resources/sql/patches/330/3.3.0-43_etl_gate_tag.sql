-- handle etl gate tags
 
DO $$ DECLARE
    _gate record;
BEGIN
    FOR _gate IN SELECT * FROM "_EtlGate" WHERE "Status"='A' LOOP
        IF _gate."Items" IS NOT NULL AND jsonb_array_length(_gate."Items"::jsonb) = 1 THEN
		IF _gate."Items"::jsonb->0->'type' IS NOT NULL THEN
			UPDATE "_EtlGate" SET "Config" = _gate."Config" || jsonb_build_object('tag',_gate."Items"::jsonb->0->'type') WHERE "Id"=_gate."Id"; 
		END IF;
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

