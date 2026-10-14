-- add `Empty` email template

DO $$ BEGIN
    IF NOT EXISTS (SELECT * FROM "_EmailTemplate" WHERE "Code" = 'Empty') THEN
        INSERT INTO "_EmailTemplate" ("Code", "Description") VALUES ('Empty', 'Empty template');
    END IF;
END $$ LANGUAGE PLPGSQL;

