-- scrub auth data

DO $$ DECLARE
    _record record;
    _payload jsonb;
    _key varchar;
BEGIN
    FOR _record IN SELECT * FROM "_Request" WHERE "Path" ~* '^/?services/rest/v./(sessions|users(/.+)?|)/?$' AND "Method" ~* 'POST|PUT' AND _cm3_utils_is_not_blank("Payload") AND "Payload" ~ '[{].+[}]' LOOP
        _payload = _record."Payload"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_payload) k WHERE k ~* '^(password|oldpassword|confirmpassword)$' LOOP
            _payload = _payload || jsonb_build_object(_key, '___password_removed___');
        END LOOP;
        UPDATE "_Request" SET "Payload" = _payload WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

UPDATE "_Request" SET "Payload" = regexp_replace("Payload", '(<[^<>]*[^a-z]Password[^a-z][^<>]*>)([^<>]*)', '\1___password_removed___', 'i') 
    WHERE "Path" ~* '/?services/soap/Private/?' AND "Method" ~* 'POST' AND _cm3_utils_is_not_blank("Payload") AND "Payload" ~* '<[^<>]*[^a-z]Password[^a-z][^<>]*>[^<>]*<';

-- patch to strip auth
-- CREATE FUNCTION _cm3_aux_strip_auth() RETURNS TRIGGER AS $$ BEGIN
--     IF NEW."Path" ~* '/?services/rest/v./sessions/?' AND NEW."Method" ~* 'POST' AND _cm3_utils_is_not_blank(NEW."Payload") AND NEW."Payload" ~ '[{].+[}]' AND _cm3_utils_is_not_blank((NEW."Payload"::jsonb)->>'password') THEN
--         NEW."Payload" = (NEW."Payload"::jsonb || jsonb_build_object('password', '___password_removed___'))::varchar;
--     END IF;
--     IF NEW."Path" ~* '/?services/soap/Private/?' AND NEW."Method" ~* 'POST' AND _cm3_utils_is_not_blank(NEW."Payload") AND NEW."Payload" ~* '<[^<>]*[^a-z]Password[^a-z][^<>]*>[^<>]*<' THEN
--         NEW."Payload" = regexp_replace(NEW."Payload", '(<[^<>]*[^a-z]Password[^a-z][^<>]*>)([^<>]*)', '\1___password_removed___', 'i');
--     END IF;
--     RETURN NEW;
-- END $$ LANGUAGE PLPGSQL;
-- 
-- CREATE TRIGGER _cm3_aux_strip_auth BEFORE INSERT OR UPDATE ON "_Request"
--     FOR EACH ROW EXECUTE PROCEDURE _cm3_aux_strip_auth();

DROP TRIGGER IF EXISTS  _cm3_aux_strip_auth ON "_Request";
DROP FUNCTION IF EXISTS _cm3_aux_strip_auth();

