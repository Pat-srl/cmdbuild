-- fix code handling for calendar triggers

DO $$ DECLARE
    _caltrigger record;
    _code varchar;
BEGIN
FOR _caltrigger IN SELECT * FROM "_CalendarTrigger" WHERE "Status" = 'A' AND "Code" IS NULL LOOP
    IF(_caltrigger."Description" ILIKE '%{%' OR _caltrigger."Description" IS NULL) THEN
        _code = SUBSTRING (_cm3_utils_random_id(), 1, 10);
    ELSE
        _code = regexp_replace(replace(_caltrigger."Description", ' ',''), '[^A-Za-z0-9áéíóúüÁÉÍÓÚÜ¡¿!?\-:]', '', 'g');
    END IF;
    UPDATE "_CalendarTrigger" SET "Code" = _code WHERE "Id" = _caltrigger."Id" AND "Status" = 'A';
    UPDATE "_Translation" SET "Code" = 'schedule.' || _code || '.content' WHERE "Code" = 'schedule.' || _caltrigger."Id" || '.content' AND "Status"='A';
    UPDATE "_Translation" SET "Code" = 'schedule.' || _code || '.description' WHERE "Code" = 'schedule.' || _caltrigger."Id" || '.description' AND "Status"='A';
END LOOP;
END $$ LANGUAGE PLPGSQL;