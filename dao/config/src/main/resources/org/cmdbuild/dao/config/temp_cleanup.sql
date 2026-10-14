
DO $$ BEGIN

    TRUNCATE TABLE "_Request";
    TRUNCATE TABLE "_Temp";
    TRUNCATE TABLE "_JobRun";
    TRUNCATE TABLE "_EtlMessage";
    
    DELETE FROM "_Session" WHERE "ExpirationStrategy" = 'default';

END $$ LANGUAGE PLPGSQL;

