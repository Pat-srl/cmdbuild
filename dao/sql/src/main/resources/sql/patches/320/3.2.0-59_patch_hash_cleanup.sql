-- patch hash cleanup

DO $$ BEGIN
    UPDATE "_Patch" SET "Hash" = NULL WHERE "BeginDate" < NOW() - interval '1 day';
    UPDATE "_Function" SET "Hash" = NULL WHERE "BeginDate" < NOW() - interval '1 day';
END $$ LANGUAGE PLPGSQL;
