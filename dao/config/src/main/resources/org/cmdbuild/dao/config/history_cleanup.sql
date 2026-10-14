
DO $$ BEGIN

    DELETE FROM "Class" WHERE "Status" = 'N';
    DELETE FROM "Class" WHERE "Status" = 'U';
    DELETE FROM "Map" WHERE "Status" = 'N';
    DELETE FROM "Map" WHERE "Status" = 'U';

END $$ LANGUAGE PLPGSQL;

