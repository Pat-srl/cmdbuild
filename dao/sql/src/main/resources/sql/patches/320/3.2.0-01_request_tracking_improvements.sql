-- request tracking binary content handling improvements

SELECT _cm3_attribute_create('"_Request"', 'PayloadBytes', 'bytea', 'MODE: read');
SELECT _cm3_attribute_create('"_Request"', 'ResponseBytes', 'bytea', 'MODE: read');

DO $$ DECLARE
    _record RECORD;
    _value bytea[];
BEGIN
    FOR _record IN SELECT * FROM "_Request" LOOP
        IF _record."Payload" IS NOT NULL THEN
            BEGIN
                UPDATE "_Request" SET "PayloadBytes" = decode("Payload", 'base64'), "Payload" = NULL WHERE "Id" = _record."Id";
            EXCEPTION WHEN OTHERS THEN
                -- no b64, ignore
            END;
        END IF;
        IF _record."Response" IS NOT NULL THEN
            BEGIN
                UPDATE "_Request" SET "ResponseBytes" = decode("Response", 'base64'), "Response" = NULL WHERE "Id" = _record."Id";
            EXCEPTION WHEN OTHERS THEN
                -- no b64, ignore
            END;
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;
