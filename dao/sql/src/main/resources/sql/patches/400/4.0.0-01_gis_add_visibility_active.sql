-- add visibility active

ALTER TABLE "_GisAttribute" ALTER COLUMN "Visibility" DROP DEFAULT;
ALTER TABLE "_GisAttribute" ALTER COLUMN "Visibility" TYPE jsonb USING to_jsonb("Visibility");
ALTER TABLE "_GisAttribute" ALTER COLUMN "Visibility" SET DEFAULT '{}'::jsonb;

SELECT _cm3_class_triggers_disable('"_GisAttribute"'::regclass);

DO $$ DECLARE
    _gisattribute record;
    _visibility text;
    _mapvisibility jsonb;
BEGIN
    FOR _gisattribute IN SELECT "Id", "Visibility" FROM "_GisAttribute" WHERE jsonb_typeof("Visibility") = 'array' LOOP
        _mapvisibility = '{}'::jsonb;
        FOR _visibility IN SELECT * FROM jsonb_array_elements_text(_gisattribute."Visibility") LOOP
            _mapvisibility = _mapvisibility || jsonb_build_object(_visibility, TRUE);
        END LOOP;
        UPDATE "_GisAttribute" SET "Visibility" = _mapvisibility WHERE "Id" = _gisattribute."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_GisAttribute"'::regclass);